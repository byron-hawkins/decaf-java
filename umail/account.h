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

// account.h

/*
This file contains all the umail data structures, and their facilities for 
reading from and writing to the offline storage file uMail.dat (which are all
bunched together, out of class context, at the end of the file).  
*/

#include <iostream>
#include <fstream> 

#ifndef __account_h__
#define __account_h__

#include <forward.h>
#include <template.h>
#include <plugin.h>

using namespace ::std;

// <[a name="MailFile"/]>
/*
Represents the offline storage file uMail.dat, which is loaded on system startup
and printed on (normal) system exit.
*/
class MailFile 
{
private:
	string filename;
	string buffer;
	ifstream* in;
	ofstream* out;
	int indentation;
	
	void scan();
	void writeIndent();
public:
	MailFile(string f);
	~MailFile();
	void line(string c);
	void line(int i);
	void line(tm* l);
	void indent();
	void outdent();
	
	string consume();
	int consumeInt();
	tm* consumeTime();
	void consumeIndent();
	void consumeOutdent();
	string consumeBlock();
	
	bool beginRead();
	void endRead();
	bool beginWrite();
	void endWrite();
	
	bool isOutdent();
};

// <[a name="Addressee"/]>
/*
A uMail user.
*/
class Addressee
{
private:
    string address;
public:
    Addressee(string a) : address(a) {};
    Addressee(MailFile* mailFile);
    string getAddress();
    
    void read(MailFile* mailFile);
    void write(MailFile* mailFile);
};

// <[a name="Message"/]>
/*
An email message.
*/
class Message
{
private:
    tm* sentTime;
    Addressee* from;
    Addressee* to;
    string subject;
    string body;
public:
    Message(Addressee* f);
    Message(MailFile* mailFile);
    
    tm* getSentTime();
    Addressee* getFrom();
    Addressee* getTo();
    string getSubject();
    string getBody();

    void setSentTime(tm* t);
    void setFrom(Addressee* f);
    void setTo(Addressee* t);
    void setSubject(string s);
    void setBody(string b);
    
    void read(MailFile* mailFile);
    void write(MailFile* mailFile);
};

// <[a name="Folder"/]>
/*
A mail folder.  This class has been used as an example of the steps required to
autonomously export a core class to the <[a href="jMail.html#function_pointer"]>dynamic library<[/a]>, such that the library
does not have to link with the object code of this class.  The essential factor
is that its methods are catalogued by pointer reference in a vector of 
<[a href="plugin.html#Method"]>Method<[/a]> and handed to the dynamic library, 
which uses the methods to 
call into the executable code base (i.e., the object code generated from this 
file).  It's cumbersome and hard to read, so for prototype purposes I've 
remained conventional, and included this as an example.
*/
class Folder // : public Root
{
private:
    string name;
    Messages* messages;
    Folder* parent;
    Folders* subFolders;
public:
    Folder(string n);
    Folder(string n, Folder* p) : name(n), parent(p) { };
    Folder(MailFile* mailFile);
    
    string getName();
    Messages* getMessages();
    Folder* getParent();
    Folders* getSubFolders();
    Folder* getSubFolder(string name, bool createIfMissing);
    void add(Message* message);
    void add(Folder* folder);
    void remove(int index);
    int getSize();
    
    void read(MailFile* mailFile);
    void write(MailFile* mailFile);
    
    static Folder* create(string n);
    
    // Function pointer example:
    // static void exportMethods(Methods* methods);
};

// <[a name="PluginAssignment"/]>
/*
An assignment of a plugin to a specific class of activity for a particular 
account.  
*/
class PluginAssignment 
{
private:
	string activityName;
	string pluginName;
public:
	PluginAssignment(string a, string p) : activityName(a), pluginName(p) {};
	PluginAssignment(MailFile* mailFile);
	string getAssignment();
	void assign(string plugin);
	bool isFor(string activity);

	void read(MailFile* mailFile);
	void write(MailFile* mailFile);
};

// <[a name="PluginAssignments"/]>
/*
The set of plugin assignments for a particular account.  The parameter (activity)
represents the name of a class of <[a href="uMail.html#Activity"]>Activity<[/a]>.  
Note that the 
method getAssignment(string) returns the defaultAssignment when there is no 
corresponding assignment; however, the method hasAssignment(string) returns 
false in the same case.  
*/
class PluginAssignments : public uVector<PluginAssignment>
{
public:
	PluginAssignments() : uVector<PluginAssignment>() {};
	PluginAssignments(MailFile* mailFile);
	void assign(string activity, string plugin);
	void unassign(string activity);
	PluginAssignment* getAssignment(string activity);
	bool hasAssignment(string activity);
	
	void read(MailFile* mailFile);
	void write(MailFile* mailFile);
	
	static PluginAssignment* defaultAssignment;
};

// <[a name="Account"/]>
/*
A user account, complete with the user, his or her root folder, and the plugin
assignments he or she has chosen.
*/
class Account 
{
private:
    Folder* root;
    Addressee* user;
    PluginAssignments* pluginAssignments;
    
public:
    JNIEnv *env;

    Account(JNIEnv* e, Addressee* u);
    Account(MailFile* mailFile);
    Folder* getRoot();
    Addressee* getUser();
    
    void assignPlugin(Activity* a, string n);
    bool hasPlugin(Activity* a);
    void removePlugin(Activity* a);
    PluginAssignment* getPlugin(Activity* a);
	
	void read(MailFile* mailFile);
	void write(MailFile* mailFile);
};

// <[a name="Accounts"/]>
/*
All the accounts in the system live here.  
*/
class Accounts : public uVector<Account> 
{
public:
	Accounts(MailFile* mailFile);
	
	bool has(string addressee);
	Account* get(string addressee);
	
	void read(MailFile* mailFile);
	void write(MailFile* mailFile);
};

#endif
