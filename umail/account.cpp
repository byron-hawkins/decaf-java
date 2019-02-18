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

// account.cpp

#include <account.h>
#include <util.h>
#include <umail.h>
#include <plugin.h>


// MailFile
MailFile::MailFile(string f) : filename(f) 
{
	this->indentation = 0;
}

MailFile::~MailFile()
{
	if (this->in != NULL)
	{
		this->in->close();
	}
	
	if (this->out != NULL)
	{
		this->out->close();
	}
}               

void MailFile::writeIndent()
{
	for (int i = 0; i < this->indentation; i++)
	{
		*this->out << "\t";
	}
}

void MailFile::line(string c)
{
	this->writeIndent();
	*this->out << c << endl;
}

void MailFile::line(int i)
{
	this->writeIndent();
	*this->out << i << endl;
}

void MailFile::line(tm* t)
{
	this->writeIndent();
	*this->out << mktime(t) << endl;
}

void MailFile::indent()
{
	this->writeIndent();
	*this->out << "{" << endl;
	this->indentation++;
}

void MailFile::outdent()
{
	this->indentation--;
	this->writeIndent();
	*this->out << "}" << endl;
}

void MailFile::scan()
{
	getline(*this->in, this->buffer);
	this->buffer = this->buffer.substr(this->indentation);
}

string MailFile::consume()
{
	string line = this->buffer;
	this->scan();
	return line;
}

int MailFile::consumeInt()
{
	return atoi(this->consume().c_str());
}

tm* MailFile::consumeTime()
{
	long t = atol(this->consume().c_str());
	tm* time = (tm*)malloc(sizeof(tm));
	*time = *localtime(&t);
	return time;
}

void MailFile::consumeIndent()
{
	this->indentation++;
	this->consume();
}

void MailFile::consumeOutdent()
{
	this->indentation--;
	this->consume();
}

bool MailFile::isOutdent()
{
	return (this->buffer.rfind("}") == (this->buffer.size() - 1));
}

string MailFile::consumeBlock()
{
	string block;
	while (!isOutdent())
	{
		block += this->buffer + "\n";
		this->scan();
	}
	
	return block;
}

bool MailFile::beginRead()
{
	this->in = new ifstream(this->filename.c_str(), ios::in);
	
	if (this->in->is_open())
	{
		this->indentation = 0;
		this->scan();
		return true;
	}
	else
	{
		return false;
	}
}

void MailFile::endRead()
{
	this->in->close();
}

bool MailFile::beginWrite()
{
	this->out = new ofstream(this->filename.c_str(), ios::out | ios::trunc);
	return this->out->is_open();
}

void MailFile::endWrite()
{
	this->out->close();
}

// Addressee
Addressee::Addressee(MailFile* mailFile)
{
	read(mailFile);
}

string Addressee::getAddress()
{
    return this->address;
}

// Message
Message::Message(MailFile* mailFile)
{
	read(mailFile);
}

Message::Message(Addressee* f)
{
	time_t t = time(NULL);
	this->sentTime = localtime(&t);
 	this->from = f;
  	this->to = NULL;
   	this->subject = "";
    this->body = "";
}

tm* Message::getSentTime()
{
    return this->sentTime;
}

Addressee* Message::getFrom()
{
    return this->from;
}

Addressee* Message::getTo()
{
    return this->to;
}

string Message::getSubject()
{
    return this->subject;
}

string Message::getBody()
{
    return this->body;
}

void Message::setSentTime(tm* t)
{
	clog << "!!!! setting message sent time to: " << asctime(t) << endl;
	this->sentTime = t;
}
 
void Message::setFrom(Addressee* f)
{
	this->from = f;
}

void Message::setTo(Addressee* t)
{
	this->to = t;
}

void Message::setSubject(string s)
{
	this->subject = s;
}

void Message::setBody(string b)
{
	this->body = b;
}

// Folder
Folder::Folder(string n) : name(n), parent(NULL)
{
    this->messages = new Messages();
    this->subFolders = new Folders();
}

Folder::Folder(MailFile* mailFile)
{
	read(mailFile);
}

string Folder::getName()
{
    return this->name;
}

Messages* Folder::getMessages()
{
    return this->messages;
}

Folder* Folder::getParent()
{
    return this->parent;
}

Folders* Folder::getSubFolders()
{
    return this->subFolders;
}

void Folder::add(Message* message)
{
	this->messages->add(message);
}

void Folder::add(Folder* folder)
{
	this->subFolders->add(folder);
	folder->parent = this;
}

Folder* Folder::getSubFolder(string name, bool createIfMissing)
{
	string folderName;
	string remainder;
	int dot = name.find(".");
	if (dot == string::npos)
	{
		folderName = name;
		remainder = "";
	}
	else
	{
		folderName = name.substr(0, dot);
		remainder = name.substr(dot+1);
	}
	
	Folder* folder = NULL;
	for (int i = 0; i < this->subFolders->getSize(); i++)
	{
		if (this->subFolders->get(i)->name == folderName)
		{
			if (remainder.size() == 0)
			{
			    folder = this->subFolders->get(i);
			    break;
			}
			else
			{
				folder = this->subFolders->get(i)->getSubFolder(remainder, createIfMissing);
				break;
			}	
		}
	}
	
	if ((folder == NULL) && createIfMissing)
	{
		folder = new Folder(folderName);
		this->subFolders->add(folder);
		if (remainder.size() > 0)
		{
			folder = folder->getSubFolder(remainder, createIfMissing);
		}
	}
	
	return folder;
}

void Folder::remove(int index)
{
	if (index < this->messages->getSize())
	{
		this->messages->remove(index);
	}
	else if (index < this->getSize())
	{
		this->subFolders->remove(index - this->messages->getSize());
	}
}

int Folder::getSize()
{
	return (this->subFolders->getSize() + this->messages->getSize());
}

Folder* Folder::create(string n)
{
	return (new Folder(n));
}

/* Function pointer example:
typedef Folder* (*Folder_create)(string);
typedef string (Folder::*Folder_getName)();
typedef void (Folder::*Folder_addMessage)(Message*);
typedef void (Folder::*Folder_addFolder)(Folder*);
typedef Folder* (Folder::*Folder_getSubFolder)(string, bool);
typedef void (Folder::*Folder_remove)(int);
typedef int (Folder::*Folder_getSize)();
typedef Messages* (Folder::*Folder_getMessages)();
typedef Folder* (Folder::*Folder_getParent)();
typedef Folders* (Folder::*Folder_getSubFolders)();

void Folder::exportMethods(Methods* methods)
{
	Folder_create pFolder = &Folder::create;
	methods->add(new Method("Folder", &pFolder));

 	Folder_getName pGetName = &Folder::getName;
	methods->add(new Method("Folder_getName", &pGetName));

	Folder_addMessage pAddMessage = &Folder::add;
	methods->add(new Method("Folder_addMessage", &pAddMessage));

	Folder_addFolder pAddFolder = &Folder::add;
	methods->add(new Method("Folder_addFolder", &pAddFolder));
	
	Folder_getSubFolder pGetSubFolder = &Folder::getSubFolder;
	methods->add(new Method("Folder_getSubFolder", &pGetSubFolder));
	
	Folder_remove pRemove = &Folder::remove;
	methods->add(new Method("Folder_Remove", &pRemove));
	
	Folder_getSize pGetSize = &Folder::getSize;
	methods->add(new Method("Folder_getSize", &pGetSize));
	
	Folder_getMessages pGetMessages = &Folder::getMessages;
	methods->add(new Method("Folder_getMessages", &pGetMessages));
	
	Folder_getParent pGetParent = &Folder::getParent;
	methods->add(new Method("Folder_getParent", &pGetParent));
	
	Folder_getSubFolders pGetSubFolders = &Folder::getSubFolders;
	methods->add(new Method("Folder_getSubFolders", &pGetSubFolders));
}
*/

// PluginAssignment
PluginAssignment::PluginAssignment(MailFile* mailFile) 
{
	read(mailFile);
}

string PluginAssignment::getAssignment()
{
	//cout << "PluginAssignment::getAssignment(): I, " << this->activityName << ", am for " << this->pluginName << endl;
	return this->pluginName;
}

void PluginAssignment::assign(string plugin)
{
	this->pluginName = plugin;
}

bool PluginAssignment::isFor(string activity)
{
	return (this->activityName == activity);
}

// PluginAssignments
PluginAssignment* PluginAssignments::defaultAssignment = new PluginAssignment("<default>", "uMailPlugins"); // should be configurable

PluginAssignments::PluginAssignments(MailFile* mailFile) : uVector<PluginAssignment>()
{
	read(mailFile);
}

void PluginAssignments::assign(string activity, string plugin)
{
	for (int i = 0; i < this->size; i++)
	{
		if (this->get(i)->isFor(activity))
		{
			this->get(i)->assign(plugin);
			return;
		}
	}

	//cout << "adding PluginAssignment for " << activity << " - " << plugin << endl;
	this->add(new PluginAssignment(activity, plugin));
}

void PluginAssignments::unassign(string activity)
{
	for (int i = 0; i < this->size; i++)
	{
		if (this->get(i)->isFor(activity))
		{
			this->remove(i);
			return;
		}
	}
}

PluginAssignment* PluginAssignments::getAssignment(string activity)
{
	//cout << "PluginAssignments::getAssignment for " << activity << endl;
	
	for (int i = 0; i < this->size; i++)
	{
		if (this->get(i)->isFor(activity))
		{
			return this->get(i);
		}
	}

	return PluginAssignments::defaultAssignment;
}

bool PluginAssignments::hasAssignment(string activity)
{
	for (int i = 0; i < this->size; i++)
	{
		if (this->get(i)->isFor(activity))
		{
			return true;
		}
	}
	return false;
}

// Accounts
Accounts::Accounts(MailFile* mailFile) : uVector<Account> ()
{
	this->read(mailFile);
}

bool Accounts::has(string addressee)
{
	for (int i = 0; i < this->size; i++)
	{
		if (uVector<Account>::get(i)->getUser()->getAddress() == addressee)
		{
			return true;
		}
	}
	
	return false;
}

Account* Accounts::get(string addressee)
{
	Account* next;
	for (int i = 0; i < this->size; i++)
	{
		next = uVector<Account>::get(i);

		if (next->getUser()->getAddress() == addressee)
		{
			return next;
		}
	}
	
	return NULL;
}

// Account
Account::Account(JNIEnv* e, Addressee* u) : env(e), user(u)
{
	if (this->user == NULL)
	{
		cout << "Help, somebody is trying to create an account without a user!" << endl;
	}

    this->root = new Folder("<root>");
    this->root->add(new Folder("Inbox"));
    this->root->add(new Folder("Sent"));
    
    this->pluginAssignments = new PluginAssignments();
    this->pluginAssignments->assign("Exit", "uMailPlugins");
};

Account::Account(MailFile* mailFile) 
{
	this->read(mailFile);
}

Folder* Account::getRoot()
{
    return this->root;
}

Addressee* Account::getUser()
{
    return this->user;
}

void Account::assignPlugin(Activity* a, string n)
{
	this->pluginAssignments->assign(a->getName(), n);
}

void Account::removePlugin(Activity* a)
{
	this->pluginAssignments->unassign(a->getName());
}

bool Account::hasPlugin(Activity* a)
{
	//cout << "Account::hasPlugin for " << a << endl;
	return this->pluginAssignments->hasAssignment(a->getName());
}

PluginAssignment* Account::getPlugin(Activity* a)
{
	return (PluginAssignment*)this->pluginAssignments->getAssignment(a->getName());
}


// ++++++++++++ File I/O +++++++++++++++

void Addressee::read(MailFile* mailFile)
{
	//cout << "Addressee::read()" << endl;

	this->address = mailFile->consume();
}

void Addressee::write(MailFile* mailFile)
{
//	cout << "Addressee::write" << endl;

	mailFile->line(this->address);
}

void Message::read(MailFile* mailFile)
{
	//cout << "Message::read()" << endl;

	this->sentTime = mailFile->consumeTime();
	this->from = new Addressee(mailFile);
	this->to = new Addressee(mailFile);
	this->subject = mailFile->consume();
	this->body = mailFile->consume(); //consumeBlock();
}

void Message::write(MailFile* mailFile)
{
//	cout << "Message::write" << endl;

	mailFile->line(this->sentTime);
	this->from->write(mailFile);                  
	this->to->write(mailFile);
	mailFile->line(this->subject);
	mailFile->line(this->body); //delimitedLines(this->body, "\n");
}

void Folder::read(MailFile* mailFile)
{
	//cout << "Folder::read()" << endl;

	this->name = mailFile->consume();
	this->parent = NULL;
	this->messages = new Messages();
	mailFile->consumeIndent();
	while (!mailFile->isOutdent())
	{
		this->messages->add(new Message(mailFile));
	}
	mailFile->consumeOutdent();
	
	this->subFolders = new Folders();
	mailFile->consumeIndent();
	Folder* subFolder;
	while (!mailFile->isOutdent())
	{
		subFolder = new Folder(mailFile);
		this->subFolders->add(subFolder);
		subFolder->parent = this;
	}
	mailFile->consumeOutdent();
}

void Folder::write(MailFile* mailFile)
{
//	cout << "Folder::write" << endl;

	mailFile->line(this->name);
	mailFile->indent();
	for (int i = 0; i < this->messages->getSize(); i++)
	{
		this->messages->get(i)->write(mailFile);
	}
	mailFile->outdent();
	mailFile->indent();
	for (int i = 0; i < this->subFolders->getSize(); i++)
	{
		this->subFolders->get(i)->write(mailFile);
	}
	mailFile->outdent();
}

void PluginAssignment::read(MailFile* mailFile)
{
	//cout << "PluginAssignment::read" << endl;
	this->activityName = mailFile->consume();
	this->pluginName = mailFile->consume();
}

void PluginAssignment::write(MailFile* mailFile)
{
	//cout << "PluginAssignment::write" << endl;

	mailFile->line(this->activityName);
	mailFile->line(this->pluginName);
}

void PluginAssignments::read(MailFile* mailFile)
{
	//cout << "PluginAssignments::read" << endl;
	int nextType;
	mailFile->consumeIndent();
	while (!mailFile->isOutdent())
	{
		this->add(new PluginAssignment(mailFile));
	}
	mailFile->consumeOutdent();
}

void PluginAssignments::write(MailFile* mailFile)
{
	//cout << "PluginAssignments::write" << endl;

	mailFile->indent();
	for (int i = 0; i < this->size; i++)
	{
		this->get(i)->write(mailFile);
	}
	mailFile->outdent();
}


void Account::read(MailFile* mailFile)
{
	this->user = new Addressee(mailFile);
	this->root = new Folder(mailFile);
	this->pluginAssignments = new PluginAssignments(mailFile);
	
	//cout << "read account for " << ((this->user == NULL)?"null":this->user->getAddress()) << endl;
}

void Account::write(MailFile* mailFile)
{
//	cout << "Account::write" << endl;

	this->user->write(mailFile);
	this->root->write(mailFile);
	this->pluginAssignments->write(mailFile);
}

void Accounts::read(MailFile* mailFile)
{
	clog << "Accounts::read" << endl;
	     
	if (mailFile->beginRead())
	{
		mailFile->consume();
		mailFile->consumeIndent();
		while (!mailFile->isOutdent())
		{
			this->add(new Account(mailFile));
		}
		mailFile->consumeOutdent();
		
		mailFile->endRead();
	}
}

void Accounts::write(MailFile* mailFile)
{
	clog << "Accounts::write" << endl;
	
	if (mailFile->beginWrite())
	{
		mailFile->line("uMail data");
		mailFile->indent();
		for (int i = 0; i < this->size; i++)
		{
			uVector<Account>::get(i)->write(mailFile);
		}
		mailFile->outdent();
		
		mailFile->endWrite();
	}
}






