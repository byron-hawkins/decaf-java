/*
 * Copyright (C) 2003 HawkinsSoftware
 *
 * This prototype of the Decaf Java development environment is free 
 * software.  You can redistribute it and/or modify it under the terms 
 * of the GNU General Public License as published by the Free Software 
 * Foundation.  However, no compilation of this code or a derivative
 * of it may be used with or integrated into any commercial application,
 * except by the written permisson of HawkinsSoftware.  Future versions 
 * of this product will be sold commercially under a different license.  
 * HawkinsSoftware retains all rights to this product, including its
 * concepts, design and implementation.
 *
 * This prototype is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
// <[a name="start"/]>

// umail.cpp

#include <umail.h>
#include <jMail.h>
#include <account.h>
#include <util.h>
#include <plugin.h>
#include <ui.h>

// Activity
Activity::Activity(Activity* a)
{
	this->classname = a->classname;
	this->pluginModel = a->pluginModel;
	this->display = a->display;
	this->active = a->active;
}

void Activity::activate(string cn, Plugin* model) 
{
    this->classname = cn;
    this->pluginModel = model;
    
    //clog << "Activity::activate: plugin: " << this->pluginModel->describe() << endl;
    
    string pluginDescription = this->pluginModel->describe();

    this->display = this->classname + " " + pluginDescription;
    
    this->active = true;
}

string Activity::getName()
{
	return this->classname;
}

string Activity::getDisplayText()
{
    return this->display;
}

void Activity::assignPlugin(string instanceName)
{
    uMail::getActiveAccount()->assignPlugin(this, instanceName);
}

bool Activity::hasPlugin()
{
	//cout << "Activity::hasPlugin(): this: " << this << endl;
	return uMail::getActiveAccount()->hasPlugin(this);
}

bool Activity::isActive()
{
	return this->active;
}

Plugin* Activity::getPlugin()
{
	return this->getPlugin(uMail::getActiveAccount());
}

Plugin* Activity::getPlugin(Account* forAccount)
{
	PluginAssignment* pluginAssignment = forAccount->getPlugin(this);
	if (pluginAssignment == NULL)
	{
		clog << "No plugin of class " << this->classname << " found for the current user " << uMail::getActiveAccount()->getUser()->getAddress() << endl;
		return NULL;
	}
	
	clog << "pluginAssignment->getAssignment(): assignment for plugin class " << this->classname << " is " << pluginAssignment->getAssignment() << endl;
	
	return this->pluginModel->createFor(pluginAssignment->getAssignment());
}

void Activity::edit()
{
	clog << "Preparing to edit activity " << this->classname << " with plugin " << uMail::getActiveAccount()->getPlugin(this)->getAssignment() << endl;
	this->pluginModel->edit(uMail::getActiveAccount()->getPlugin(this)->getAssignment());
}

// DisplayMenu
DisplayMenu::DisplayMenu(Menu* m) : Activity(), menu(m)
{ 
}

MenuOption* DisplayMenu::run()
{
    //cout << "DisplayMenu::run()" << endl;
    
    if (this->hasPlugin())
    {
    	Plugin* plugin = this->getPlugin();
        plugin->instantiate(2, new jAccount(uMail::getActiveAccount()), new jMenu(this->menu));
        plugin->run();
    }        
    return this->menu->askUser();
}

// SendMail
SendMail::SendMail() 
{
	//cout << "SendMail::SendMail() " << endl;

	Activity::activate("SendMail", new Plugin("sendMail", 2, new jType("jAccount"), new jType("jMessage")));
}

void SendMail::send(Message* message)
{
	clog << "Attempting to run sendMail plugin " << endl;
	Plugin* plugin = this->getPlugin();
	plugin->instantiate(2, new jAccount(uMail::getActiveAccount()), new jMessage(message));
	plugin->run();
}
     
// ReceiveMail
ReceiveMail::ReceiveMail() 
{
	//cout << "ReceiveMail::ReceiveMail() " << endl;
	Activity::activate("ReceiveMail", new Plugin("receiveMail", 2, new jType("jAccount"), new jType("jMessage")));
}

void ReceiveMail::receive(Message* message)
{
	message->getFrom();
	message->getTo();

	if ((message == NULL) || (message->getTo() == NULL))
	{
		return;
	}
	
	Account* recipient = uMail::instance()->accounts->get(message->getTo()->getAddress());
	if (recipient == NULL)
	{
		recipient = new Account(uMail::instance()->jvm->env, message->getTo());
		uMail::instance()->accounts->add(recipient);
	}
	
	//this->plugin->setName("Receive");
	
	Plugin* plugin = this->getPlugin(recipient);
	plugin->instantiate(2, new jAccount(recipient), new jMessage(message));
	plugin->run();
}

// uMail
uMail* uMail::_instance = NULL;

uMail* uMail::instance()
{
	if (uMail::_instance == NULL)
	{
		clog << "instance is null, fetching from Plugin::appHandle: " << Plugin::appHandle << endl;
		uMail::_instance = (uMail*)Plugin::appHandle;
	}
	return uMail::_instance;
}

uMail::uMail(jVM* vm, string c) : jvm(vm)
{
	uMail::_instance = this;

/*	Function pointer example:
	Methods* methods = new Methods();           
	Folder::exportMethods(methods);

    Plugin::init(vm, c, this, "./uMail.log", methods);
    Plugin::callEmptyStaticMethod("jMail", "init");
*/	

    Plugin::init(vm, c, this, "./uMail.log");

    time_t now = time(NULL);
    clog << endl << "Starting uMail at " << ctime(&now) << endl << endl;
    clog << "creating accountList" << endl;
    
    this->mailFile = new MailFile("./uMail.dat");
    this->accounts = new Accounts(mailFile);
    
    clog << "creating main menu list" << endl;
    
    List* main = new List();
    main->append(new CreateMessage());
    main->append(new MailViewer());
    main->append(new PluginViewer());
    main->append(new StepUp("Sign Out", "MainMenu_SignOut"));
    main->append(new Exit());

    clog << "creating main menu" << endl;

    this->mainMenu = new Menu("Main Menu", main, "Select an activity: ");
    this->activity_mainMenu = new DisplayMenu(this->mainMenu);
    
    this->sendMail = new SendMail();
    this->receiveMail = new ReceiveMail();
}

typedef Messages* (Folder::*Folder_getMessages)();

void uMail::run()
{
    Prompt* prompt = new Prompt("\nEnter your email address ", true);
	string username;
    MenuOption* option;
 	bool first = true;
 	
  	while (true)
    {
    	username = prompt->askUser();
    	
    	cout << endl << "Greetings, " << username << ", and welcome to uMail," << endl << "the mail client that does what you want." << endl << endl;

	    this->activeAccount = this->accounts->get(username);
	    if (this->activeAccount == NULL)
	    {
		    this->activeAccount = new Account(this->jvm->env, new Addressee(username)); 
		    this->accounts->add(this->activeAccount);
		}

	    while (true)
	    {
	        option = this->activity_mainMenu->run();
	        if (option == NULL)
	        {
	        	continue;
        	}
	        if (option->done())
	        { 
	            break;
	        }
	    }
	    
	    if (option->requiresExit())
	    {
	    	break;
 		}
	}
     
    this->accounts->write(this->mailFile);
}

Account* uMail::getActiveAccount()
{
	return uMail::instance()->activeAccount;
}

void uMail::send(Message* message)
{
	clog << "uMail::send" << endl;
	
	time_t t = time(NULL);
	tm* time = (tm*)malloc(sizeof(tm));
	*time = *localtime(&t);
	message->setSentTime(time);
	this->sendMail->send(message);
	clog << "about to this->receiveMail->receive(message);" << endl;
	clog << "message: " << message->getFrom()->getAddress() <<endl;
	this->receiveMail->receive(message);
}

