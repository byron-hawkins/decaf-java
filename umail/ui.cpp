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

// ui.cpp

#include <ui.h>

#include <account.h>
#include <umail.h>
#include <jMail.h>
#include <plugin.h>
#include <util.h>

// MenuItem
const int MenuItem::OPTION = 0;
const int MenuItem::MESSAGE = 1;

// MenuOption
bool MenuOption::requiresExit()
{
	return false;
}

bool MenuOption::done()
{
	return false;
}

int MenuOption::getType()
{
	return MenuItem::OPTION;
}

// MenuMessage
int MenuMessage::display()
{
	cout << this->message;
	return this->message.size();
}

int MenuMessage::getType()
{
	return MenuItem::MESSAGE;
}

// TextMenuOption
TextMenuOption::TextMenuOption(string text) : 
	MenuOption() 
{
	//clog << "TextMenuOption::TextMenuOption" << endl;
	this->init(text);
}

TextMenuOption::TextMenuOption(string text, string identifier) : 
    MenuOption() 
{
	//clog << "TextMenuOption::TextMenuOption" << endl;
	this->init(text);
}

void TextMenuOption::init(string text)
{
    this->displayText = text;
}

int TextMenuOption::display()
{
    cout << this->displayText;
    return this->displayText.size();
}

// PluginMenuOption
PluginMenuOption::PluginMenuOption(string text, string pluginMethodName) :
	TextMenuOption(text)
{
	Activity::activate("PluginMenuOption_" + text, new Plugin(pluginMethodName, 1, new jType("jAccount")));
}

void PluginMenuOption::choose()
{
    Plugin* plugin = this->getPlugin();
	plugin->instantiate(1, new jAccount(uMail::getActiveAccount()));
	plugin->run();
}

string PluginMenuOption::getDisplayText()
{
	return TextMenuOption::displayText;
}

MessageMenuOption::MessageMenuOption(string text, Message* m) :
	TextMenuOption(text), message(m) 
{
	//clog << "MessageMenuOption::MessageMenuOption" << endl;
	Activity::activate("MessageMenuOption", new Plugin("displayMessage", 2, new jType("jAccount"), new jType("jMessageDisplay")));
}

void MessageMenuOption::choose()
{
	MessageDisplay* display = new MessageDisplay(this->message);
	Plugin* plugin = this->getPlugin();
	plugin->instantiate(2, new jAccount(uMail::getActiveAccount()), new jMessageDisplay(display));
	plugin->run();
}

// FolderMenuOption
FolderMenuOption::FolderMenuOption(Folder* f) : 
	TextMenuOption(f->getName()), folder(f)
{
	FolderMenu::activate(this);
}

FolderMenuOption::FolderMenuOption(string display, Folder* f) : 
	TextMenuOption(display), folder(f)
{
	FolderMenu::activate(this);
}

void FolderMenuOption::choose()
{
	string title = "Contents of " + this->folder->getName();

	Menu* menu; 
	bool done;
	while (true) 
	{
		menu = new FolderMenu(title, this->folder, (Activity*)this); 
		done = menu->go();
		if (done)
		{
			break;
		}
	}
}

// PluginFolderMenuOption
PluginFolderMenuOption::PluginFolderMenuOption(string text, Folder* f, string runMethodName) :
	TextMenuOption(text), folder(f)
{
	//clog << "PluginFolderMenuOption::PluginFolderMenuOption" << endl;
	Activity::activate("PluginFolderMenuOption_" + text, new Plugin(runMethodName, 2, new jType("jAccount"), new jType("jFolder")));
}

void PluginFolderMenuOption::choose()
{
    Plugin* plugin = this->getPlugin();

	//clog << "PluginFolderMenuOption::choose(): plugin: " << plugin << endl;

	plugin->instantiate(2, new jAccount(uMail::getActiveAccount()), new jFolder(this->folder));
	plugin->run();
}

// Menu
Menu::Menu(const Menu& m)
{
    //this->items = new List(&m.items);
    this->question = m.question;
}

string Menu::getTitle()
{
    return this->title;
}

bool Menu::go()
{
	MenuOption* option = this->askUser();
		
	if (option == NULL)
  		{
		return false;
  	}
    return option->done();
}
 
MenuOption* Menu::askUser()
{
	cout << endl << endl;

    MenuItem* item;
    ListIterator* iterator = this->items->iterator();
    int i = 1;
    int pluginColumn = 40;
    int padding;
    int displayLength;
    Plugin* plugin;
    MenuOption* option;
    while (iterator->hasNext())
    {
    	item = (MenuItem*)iterator->next();
    	cout << i++ << ") ";
        displayLength = item->display(); 
    	if (item->getType() == MenuItem::OPTION)
    	{
    		option = (MenuOption*)item;
    		if (option->isActive())
    		{
		        plugin = option->getPlugin();
		        padding = pluginColumn - displayLength;
		        for (int i = 0; i < padding; i++)
		        {
		            cout << " ";
		        }
		        cout << "<" << plugin->getName() << ">";
		    }
        }
        cout << endl;
    }
    cout << endl << "Follow option by 'e' to edit the option's <plugin> (instead of running it)." << endl;
    cout << "Follow option by ':<plugin>' to change the plugin" << endl;
    cout << "    (e.g. '3:FolderSummary' sets option 3 to use the plugin FolderSummary.cup)" << endl << endl;
    
    cout << "\n" << question << " ";
    
    string response;
    cin >> response;

    /*
    if (response.find("!") != string::npos)
    {
    	// exit here
   	}
   	*/

   	string pluginName;
   	int colon = response.find(":");
   	bool setPlugin = (colon != string::npos);
    bool edit = (response.find("e") != string::npos);
   	if (setPlugin)
   	{
   		pluginName = *new string(response);
   		pluginName = pluginName.substr(colon+1, pluginName.size());
   	}
    if (edit || setPlugin)
    {
    	response.substr(0, response.size()-1);
   	}
   	
    int selected = atoi(response.c_str());

    option = NULL;
    iterator = this->items->iterator();
    while (iterator->hasNext())
    {
        item = (MenuItem*)iterator->next();
    	if (selected == 1)
    	{
	        if (item->getType() == MenuItem::OPTION)
	        {
	        	option = (MenuOption*)item;
	            break;
	        }
        }
        if (item->getType() == MenuItem::OPTION)
        {
        	selected--;
        }
    }

    if (option == NULL)
    {
    	cout << "I'm sorry, I can't find the item " << response << endl;
    	return NULL;
    }
    
    cout << endl;
    
    if (edit)
    {
    	option->edit();
    	return NULL;
   	}
   	else if (setPlugin)
   	{
   		option->assignPlugin(pluginName);
   		return NULL;
   	}
   	else
   	{
    	option->choose();
    	return option;
   	}
}

void Menu::add(MenuOption* option)
{
    this->items->append(option);
}

int Menu::getSize()
{
    return this->items->getSize();
}

// FolderMenu
FolderMenu::FolderMenu(string t, Folder* f, Activity* a) : Activity(a), Menu(t), folder(f)
{
	//clog << "FolderMenu::FolderMenu" << endl;
	//clog << messages->getSize() << " message and " << folders->getSize() << " folders." << endl;
	
	items = new List();
	
	Plugin* plugin = getPlugin();
	plugin->instantiate(3, new jAccount(uMail::getActiveAccount()), new jFolder(this->folder), new jMenu(this));
	plugin->run();

	items->append(new StepUp("Done", "viewFolder_Done"));
	question = "Selection: ";
}
 
void FolderMenu::activate(Activity* a)
{
	a->activate("FolderMenu", new Plugin("getFolderMenu", 3, new jType("jAccount"), new jType("jFolder"), new jType("jMenu")));
}

// PluginMenu
PluginMenu::PluginMenu(string t, Activity* a) : Menu(t), Activity(a)
{
	items = new List();
	
	Plugin* plugin = getPlugin();
	clog << "Plugin is " << plugin << endl;
	plugin->instantiate(2, new jAccount(uMail::getActiveAccount()), new jMenu(this));
	plugin->run();

	items->append(new StepUp("Done", "viewPlugins_Done"));
	question = "Run plugin: ";
}

//Prompt
Prompt::Prompt(string d, bool a) : display(d), atomic(a) 
{
}

string Prompt::askUser(string defaultResponse)
{
    cout << this->display;
    
    string response;
    
    string atom;  
    while (true)
    {
    	cin >> atom;
    	if (atom == ".")
    	{
    		break;
		}
		response += atom;
		if (this->atomic)
		{
			break;
		}
		response += " ";
	}
	
	clog << "Prompt::askUser(): user said " << response << endl;
    
    return response;
}

// MessageInput
Prompt* MessageInput::to = new Prompt("To: ", true);
Prompt* MessageInput::subject = new Prompt("\n(terminate with floating ' . ')\nSubject: ", false);
Prompt* MessageInput::body = new Prompt("\n(terminate with floating ' . ')\nBody:\n\n", false);

Message* MessageInput::getMessage()
{
	return this->message;
}

DisplayContext::DisplayContext()
{
	this->shortest = -1;
	this->longest = -1;
}

void DisplayContext::apply(int length)
{
	if (this->shortest == -1)
	{
		this->shortest = this->longest = length;
	}
	else
	{
		if (length < this->shortest)
		{
			this->shortest = length;
		}
		else if (length > this->longest)
		{
			this->longest = length;	
		}
	}
}

DataDisplay::DataDisplay(DisplayContext* c, string l) : context(c), valueOffset(1) 
{
	this->setLabel(l);
}

DataDisplay::DataDisplay(DisplayContext* c) : context(c), valueOffset(1) 
{
	this->setLabel("");
}

void DataDisplay::setLabel(string l)
{
	this->label = l;
	
	int size = this->label.size();
	if (size > 0)
	{
		this->context->apply(size);
	}
}

void DataDisplay::setValue(string v)
{
	this->value = v;
}

int DataDisplay::display()
{
	int len = this->value.size();
	if (this->label.size() > 0)
	{
		len += this->label.size() + this->valueOffset + 1;
		cout << this->label << ":";
		for (int i = 0; i < this->valueOffset; i++)
		{
			cout << " ";
		}
	}
	cout << this->value << endl;
	
	return len;
}

	
MessageDisplay::MessageDisplay(Message* m) : message(m) 
{
	this->from = new DataDisplay(this, "From");
	this->subject = new DataDisplay(this, "Subject");
	this->body = new DataDisplay(this);
	this->sentTime = new DataDisplay(this, "Sent at");
}

void MessageDisplay::initValues() 
{
	this->from->setValue(this->message->getFrom()->getAddress());
	this->subject->setValue(this->message->getSubject());
	this->body->setValue(this->message->getBody());
}
  
Message* MessageDisplay::getMessage()
{
	return this->message;
}

// fix!
void MessageDisplay::align()
{
	int offset = this->longest - this->shortest;
 	this->from->valueOffset = offset + (this->longest - this->from->label.size());
	this->subject->valueOffset = offset + (this->longest - this->subject->label.size());
	this->body->valueOffset = offset + (this->longest - this->body->label.size());
	this->sentTime->valueOffset = offset + (this->longest - this->sentTime->label.size());
}

// CreateMessage
CreateMessage::CreateMessage() : TextMenuOption("Send Message") 
{
	Activity::activate("CreateMessage", 
 				   new Plugin("createMessage", 2, new jType("jAccount"), new jType("jMessageInput")));
}

void CreateMessage::choose()
{
	//clog << "CreateMessage::choose()" << endl;

	Account* active = uMail::getActiveAccount();
	Message* message = new Message(active->getUser());

	clog << "Attempting to run createMessage() plugin for " << active->getUser()->getAddress() << endl;
	
	Plugin* plugin = this->getPlugin();
	plugin->instantiate(2, new jAccount(active), new jMessageInput(new MessageInput(message)));
	plugin->run();

 	clog << "Sending message to " << message->getTo()->getAddress() << endl;
	
	uMail::instance()->send(message);
}

// MailViewer
MailViewer::MailViewer() : TextMenuOption("View Mail") 
{
	FolderMenu::activate(this);
}

void MailViewer::choose()
{
 	Account* current = uMail::getActiveAccount();
	string title = "Mail for account " + current->getUser()->getAddress();
	
	Menu* folderMenu;
	bool done;
	while (true) 
 	{
 	 	folderMenu = new FolderMenu(title, current->getRoot(), this); 
 	 	done = folderMenu->go();
 	 	if (done)
 	 	{
 	 		break;
 	 	}
  	}
}

// PluginViewer
PluginViewer::PluginViewer() : TextMenuOption("View Plugins")
{
	List* options = new List();
	options->append(new SystemOperations());
	options->append(new Plugins());
	options->append(new StepUp("Done", "ViewPlugins_Done"));
	this->menu = new Menu("Plugins", options, "Select a category");
}

void PluginViewer::choose()
{
	MenuOption* option;
    while (true)
    {
        option = this->menu->askUser();
        if (option == NULL)
        {
        	continue;
    	}
        if (option->done())
        { 
            break;
        }
    }
}

// SystemOperations
SystemOperations::SystemOperations() : MenuOption() 
{
	List* options = new List();

	Activity* sendMail = new Activity();
	sendMail->activate("SendMail", new Plugin("sendMail", 2, new jType("jAccount"), new jType("jMessage")));
	options->append(new SystemOperation("Send Mail", "This function is run when you send a message to someone.\nYou might, for example, put a copy of the message in a\n'Sent Items' folder for your reference.", sendMail));

 	Activity* receiveMail = new Activity();
	receiveMail->activate("ReceiveMail", new Plugin("receiveMail", 2, new jType("jAccount"), new jType("jMessage")));
	options->append(new SystemOperation("Receive Mail", "This function is run when you receive a message from\nsomeone else.  A simple plugin might put the message\nin your inbox, while a more complex one might examine\nit and choose an appropriate folder.", receiveMail));

	options->append(new StepUp("Done", "SystemOperations_Done"));
	this->menu = new Menu("System Operations", options, "Edit or change plugin: ");
}

void SystemOperations::choose()
{
	MenuOption* option;
    while (true)
    {
        option = this->menu->askUser();
        if (option == NULL)
        {
        	continue;
    	}
        if (option->done())
        { 
            break;
        }
    }
}

int SystemOperations::display()
{
	string display = "System operations";
	cout << display;
	return display.size();
}

// SystemOperation
SystemOperation::SystemOperation(string disp, string desc, Activity* activity) : 
	MenuOption(activity), displayText(disp), description(desc)
{
}

void SystemOperation::choose()
{
	cout << this->description << endl;
}

int SystemOperation::display()
{
	cout << this->displayText;
	return this->displayText.size();
}

// Plugins
Plugins::Plugins() : TextMenuOption("Plugins")
{
	Activity::activate("DisplayPlugins", new Plugin("getPluginMenu", 2, new jType("jAccount"), new jType("jMenu")));
}

void Plugins::choose()
{
	Menu* menu;
	bool done;
	while (true) 
 	{
 		menu = new PluginMenu("Plugins", this);
 		done = menu->go();
 		if (done)
 		{
 			break;
		}
  	}
}

// StepUp
StepUp::StepUp(string t, string n) : TextMenuOption(t) 
{
}

void StepUp::choose()
{
	if (this->hasPlugin())
	{
        Plugin* plugin = this->getPlugin();
		plugin->instantiate(1, new jAccount(uMail::getActiveAccount()));
		plugin->run();
	}
}

bool StepUp::done()
{
	return true;
}

// Exit
Exit::Exit() : TextMenuOption("Quit") 
{ 
	Activity::activate("Exit", new Plugin("exit", 1, new jType("jAccount")));
}

void Exit::choose()
{
	if (this->hasPlugin())
	{
	    Plugin* plugin = this->getPlugin();
		plugin->instantiate(1, new jAccount(uMail::getActiveAccount()));
		plugin->run();
	}
}

bool Exit::requiresExit()
{
	return true;
}

bool Exit::done()
{
	return true;
}
