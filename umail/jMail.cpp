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

// jMail.cpp

#include <jMail.h>

#include <account.h>
#include <ui.h>
#include <util.h> 


jTime::jTime(tm* t) : data(t)
{
}

int jTime::getSecond()
{
	return this->data->tm_sec;
}

int jTime::getMinute()
{
	return this->data->tm_min;
}

int jTime::getHour()
{
	return this->data->tm_hour;
}

int jTime::getDay()
{
	return this->data->tm_mday;
}

int jTime::getMonth()
{
	return this->data->tm_mon;
}

int jTime::getYear()
{
	return this->data->tm_year;
}

bool jTime::isDST()
{
	return this->data->tm_isdst;
}

// jAddressee
jAddressee::jAddressee(void* a) : jType("jAddressee", a), addressee((Addressee*)a) 
{
}

const char* jAddressee::getAddress()
{
	string address = this->addressee->getAddress();
    return address.c_str();
}

// jMessage
jMessage::jMessage(void* m) : jType("jMessage", m), message((Message*)m) 
{
}                                                                           

jTime* jMessage::_getSentTime()
{
    return (new jTime(this->message->getSentTime()));
}

jAddressee* jMessage::getFrom()
{ 
    return (new jAddressee(this->message->getFrom()));
}

jAddressee* jMessage::getTo()
{
    return (new jAddressee(this->message->getTo()));
}

char* jMessage::getSubject()
{
	return (char*)this->message->getSubject().c_str();
}

char* jMessage::getBody()
{
    return (char*)this->message->getBody().c_str();
}

void jMessage::setTo(char* address)
{
	this->message->setTo(new Addressee(address));
}

void jMessage::setSubject(char* subject)
{
	this->message->setSubject(subject);
}

void jMessage::setBody(char* body)
{
	this->message->setBody(body);
}

/* jFolder function pointer example
cx_Cp* jFolder::r = NULL;
S_E* jFolder::r_getName = NULL;
V_Vp* jFolder::r_addMessage = NULL;
V_Vp* jFolder::r_addFolder = NULL;
Vp_SB* jFolder::r_getSubFolder = NULL;
V_I* jFolder::r_remove = NULL;
I_E* jFolder::r_getSize = NULL;
Vp_E* jFolder::r_getMessages = NULL;
Vp_E* jFolder::r_getParent = NULL;
Vp_E* jFolder::r_getSubFolders = NULL;
*/

jFolder::jFolder(void* f) : jType("jFolder", f), folder((Folder*)f), root((Root*)f)
{              
	//clog << "jFolder::jFolder: subfolders: " << this->folder->getSubFolders()->getSize() << endl;
}

jFolder::jFolder(char* name) : jType("jFolder")
{
	this->folder = new Folder(name);
	this->root = NULL;
	
//	this->root = (*r)(name);
//	this->folder = (Folder*)this->root;

	this->content = this->folder;
}

const char* jFolder::getName()
{
	return this->folder->getName().c_str();

//	or, with function pointers:
//	return ((this->root->**r_getName)()).c_str();
}

void jFolder::add(jMessage* message)
{
	this->folder->add(message->message);
//	(this->root->**r_addMessage)(message->root);
}

void jFolder::add(jFolder* f)
{
	this->folder->add(f->folder);
	//(this->root->**r_addFolder)(f->root);
}

jFolder* jFolder::getSubFolder(char* name)
{
	return (new jFolder(this->folder->getSubFolder(name, false)));
//	return (new jFolder((this->root->**r_getSubFolder)(name, false)));
}

jFolder* jFolder::getSubFolder(char* name, bool createIfNecessary)
{
	return (new jFolder(this->folder->getSubFolder(name, createIfNecessary)));
//	return (new jFolder((this->root->**r_getSubFolder)(name, createIfNecessary)));
}

void jFolder::remove(int index)
{
	this->folder->remove(index-1);
//	(this->root->**r_remove)(index-1);
}

int jFolder::getSize()
{
	return this->folder->getSize();
//	return (this->root->**r_getSize)();
}

uIterator* jFolder::getMessages()
{
    return jPluginLib::create_jIterator("jMessage", "SWIGTYPE_p_void", this->folder->getMessages()->iterator());
//    return jPluginLib::create_jIterator("jMessage", "SWIGTYPE_p_void", ((vVector*)(this->root->**r_getMessages)())->iterator());
}

jFolder* jFolder::getParent()
{
	Folder* parent = this->folder->getParent();
	if (parent == NULL)
	{
		return NULL;
	}
	return (new jFolder(parent));
//    return (new jFolder((this->root->**r_getParent)()));
}

uIterator* jFolder::getSubFolders()
{
    return jPluginLib::create_jIterator("jFolder", "SWIGTYPE_p_void", this->folder->getSubFolders()->iterator());
    //return jPluginLib::create_jIterator("jFolder", "SWIGTYPE_p_void", ((vVector*)(this->root->**r_getSubFolders)())->iterator());
}     

// jAccount
jAccount::jAccount(void* a) : jType("jAccount", a)
{
    this->account = (Account*)a;
}
    
jFolder* jAccount::getRoot()
{
    return (new jFolder(this->account->getRoot()));
}

jAddressee* jAccount::getUser()
{
	return (new jAddressee(this->account->getUser()));
}

void jAccount::log(char* message)
{
	clog << message << endl;
}

// jMenu
jMenu::jMenu(void* m) : jType("jMenu", m)
{
    this->menu = (Menu*)m;
}

int jMenu::getSize()
{
    return this->menu->getSize();
}

void jMenu::go()
{
	this->menu->go();
}

void jMenu::append(jType* option)
{
	this->menu->add((MenuOption*)option->getContent());
}

// jMenuOption
jMenuOption::jMenuOption(void* o) : jType("jMenuOption", o), option((PluginMenuOption*)o)
{
}

jMenuOption::jMenuOption(char* text, char* runMethodName) : jType("jMenuOption")
{
	this->option = new PluginMenuOption(text, runMethodName);
	jType::content = this->option;
}
	
const char* jMenuOption::getDisplayText()
{
	return this->option->getDisplayText().c_str();
}

// jFolderMenuOption
jFolderMenuOption::jFolderMenuOption(void* o) : jType("jFolderMenuOption", o), option((FolderMenuOption*)o)
{
}

jFolderMenuOption::jFolderMenuOption(char* text, jFolder* folder) : jType("jFolderMenuOption") 
{
	this->option = new FolderMenuOption(*new string(text), (Folder*)folder->getContent());
	jType::content = this->option;
}
	
const char* jFolderMenuOption::getDisplayText()
{
	return this->option->getDisplayText().c_str();
}

// jMessageMenuOption
jMessageMenuOption::jMessageMenuOption(void* o) : jType("jMessageMenuOption", o), option((MessageMenuOption*)o)
{
}

jMessageMenuOption::jMessageMenuOption(char* text, jMessage* message) : jType("jMessageMenuOption") 
{
	this->option = new MessageMenuOption(*new string(text), (Message*)message->getContent());
	jType::content = this->option;
}
	
const char* jMessageMenuOption::getDisplayText()
{
	return this->option->getDisplayText().c_str();
}

// jFolderMenuPlugin
jFolderMenuPlugin::jFolderMenuPlugin(void* o) : jType("jFolderMenuPlugin", o), option((PluginFolderMenuOption*)o)
{
}

jFolderMenuPlugin::jFolderMenuPlugin(char* text, jFolder* folder, char* runMethodName) : jType("jFolderMenuPlugin") 
{
	this->option = new PluginFolderMenuOption(*new string(text), (Folder*)folder->getContent(), *new string(runMethodName));
	jType::content = this->option;
}
	
const char* jFolderMenuPlugin::getDisplayText()
{
	return this->option->getDisplayText().c_str();
}

// jPrompt
jPrompt::jPrompt(void* p) : jType("jPrompt", p)
{
	this->prompt = (Prompt*)p;
}

jPrompt::jPrompt(char* display, bool isAtomic) : jType("jPrompt")
{
	this->prompt = new Prompt(display, isAtomic);
	jType::content = this->prompt;
}
 
char* jPrompt::askUser()
{
	return this->askUser("");
} 

char* jPrompt::askUser(char* defaultResponse)
{
	return (char*)this->prompt->askUser(defaultResponse).c_str();
} 

// jMessageInput
jMessageInput::jMessageInput(void* m) : jType("jMessageInput", m)
{
	this->messageInput = (MessageInput*)m;
}

jPrompt* jMessageInput::getToPrompt()
{
	return (new jPrompt(this->messageInput->to));
}
 
jPrompt* jMessageInput::getSubjectPrompt()
{
	return (new jPrompt(this->messageInput->subject));
}

jPrompt* jMessageInput::getBodyPrompt()
{
	return (new jPrompt(this->messageInput->body));
}

jMessage* jMessageInput::getMessage()
{
	return (new jMessage(this->messageInput->getMessage()));
}

// jDataDisplay
jDataDisplay::jDataDisplay(void* d) : jType("jDataDisplay", d)
{
	this->dataDisplay = (DataDisplay*)d;
}

void jDataDisplay::setLabel(char* l)
{
	this->dataDisplay->setLabel(l);
}

void jDataDisplay::setValue(char* v)
{
	this->dataDisplay->setValue(v);
}

void jDataDisplay::display()
{
	this->dataDisplay->display();
}

// jMessageDisplay
jMessageDisplay::jMessageDisplay(void* m) : jType("jMessageDisplay", m)
{
	//clog << "jMessageDisplay::jMessageDisplay" << endl;
	
	this->messageDisplay = (MessageDisplay*)m;
}

jDataDisplay* jMessageDisplay::getFrom()
{
	return (new jDataDisplay(this->messageDisplay->from));
}

jDataDisplay* jMessageDisplay::getSubject()
{
	return (new jDataDisplay(this->messageDisplay->subject));
}

jDataDisplay* jMessageDisplay::getBody()
{
	return (new jDataDisplay(this->messageDisplay->body));
}

jDataDisplay* jMessageDisplay::getSentTime()
{
	return (new jDataDisplay(this->messageDisplay->sentTime));
}

jMessage* jMessageDisplay::getMessage()
{
	return (new jMessage(this->messageDisplay->getMessage()));
}

void jMessageDisplay::initValues()
{
	this->messageDisplay->initValues();
}

void jMessageDisplay::align()
{
	this->messageDisplay->align();
}

/* Function pointer example:
void jMail::init()
{
	jFolder::r = (cx_Cp*)Plugin::getMethod("Folder");
	jFolder::r_getName = (S_E*)Plugin::getMethod("Folder_getName");
	jFolder::r_addMessage = (V_Vp*)Plugin::getMethod("Folder_addMessage");
	jFolder::r_addFolder = (V_Vp*)Plugin::getMethod("Folder_addFolder");
	jFolder::r_getSubFolder = (Vp_SB*)Plugin::getMethod("Folder_getSubFolder");
	jFolder::r_remove = (V_I*)Plugin::getMethod("Folder_remove");
	jFolder::r_getSize = (I_E*)Plugin::getMethod("Folder_getSize");
	jFolder::r_getMessages = (Vp_E*)Plugin::getMethod("Folder_getMessages");
	jFolder::r_getParent = (Vp_E*)Plugin::getMethod("Folder_getParent");
	jFolder::r_getSubFolders = (Vp_E*)Plugin::getMethod("Folder_getSubFolders");
}
*/
