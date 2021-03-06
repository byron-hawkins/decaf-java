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

// jmail.h

/*
uMail classes are made available to Java plugins via wrapper classes here in 
jMail.  Each jMail class is derived from <[a href="plugin.html#jType"]>jType<[/a]>, and by its 
policy contains a pointer to the single object it wraps.  Methods available to
Java on the wrapped object must be declared public on the jMail wrapper class
and routed accordingly.  jMail is the basis of the dynamic library loaded by 
Java.  A Java version of each jMail class is generated by SWIG, along with an
export method.  In this way, the user of jMail classes in Java has essentially
transparent access to the public methods of these native jMail classes.

In this rendition of uMail, the dynamic library links with the binary code of 
the main application.  See <[a href="plugin.html#jPluginLib"]>jPluginLib<[/a]> for the skinny on this odd predicament.
*/

#include <stdlib.h>
#include <jni.h>
#include <time.h>

#ifndef __jmail_h__
#define __jmail_h__

#include <forward.h>
#include <plugin.h>
#include <ui.h>

// <[a name="jTime"/]>
/*
jTime represents a date and time in a format that can be applied to a 
<[a href="uMail_i.html#jTime"]>java.util.Date<[/a]> and a tm* (from time.h).  
*/
class jTime 
{
private:
	tm* data;
public:
	jTime() {}; 	// bogus, not instantiated from Java

	int getSecond();
	int getMinute();
	int getHour();
	int getDay();
	int getMonth();
	int getYear();
	bool isDST();
	
// impl only
	jTime(tm* t);
};

// <[a name="jAddressee"/]>
class jAddressee : public jType
{
private:
    Addressee* addressee;
public:
    jAddressee(void* a);
    const char* getAddress();
};

// <[a name="jMessage"/]>
/*
Note how the sent time must be carefully <[a href="uMail_i.html#jMessage"]>initialized<[/a]>, as the constructor for
java.util.GregorianCalendar by which we represent the date/time in Java does
not accept any of our data in its constructors.
*/
class jMessage : public jType
{
private:
    Message* message;
   	Root* root;
public:
    jMessage(void* m);
    
    jAddressee* getFrom();
    jAddressee* getTo();
    char* getSubject();
    char* getBody();
    
    void setTo(char* address);
    void setSubject(char* subject);
    void setBody(char* body);

// impl only (getSentTime() must include call to init(), 
// so it is attached to jTime.java via SWIG typemap in uMail.i)
    jTime* _getSentTime();
    friend class jFolder;
};

// <[a name="function_pointer"/]>
/*
Function pointer example:

The following typedefs represent the method signatures of the members of 
<[a href="account.html#Folder"]>Folder<[/a]>, which is essentially represented by jFolder. Pointers to the 
actual methods of Folder can be passed to this library upon <[a href="plugin.html#jPluginLib"]>initialization<[/a]> and applied to 
the static members below.  In the process of plugin operation, method calls 
intended for Folder would then arrive at this class from Java with
a pointer to Folder.  To avoid linking with account.o, the pointer to Folder
is cast to an instance of its superclass Root, and the appropriate method, identified
by name, is called on that faux instance of <[a href="plugin.html#Root"]>Root<[/a]>.  

typedef void (Root::*V_E)();
typedef void  (Root::*V_Vp)(void*);
typedef void (Root::*V_I)(int);
typedef void (Root::*V_Cp)(char*);
typedef void (Root::*V_B)(bool);
typedef void (Root::*V_S)(string);

typedef void* (Root::*Vp_Cp)(char*);
typedef void* (Root::*Vp_SB)(string, bool);
typedef void* (Root::*Vp_E)();

typedef int (Root::*I_E)();
typedef string (Root::*S_E)();

typedef Root* (*cx_Vp)(void*);
typedef Root* (*cx_Cp)(char*);

Key for pointer names:
E: empty
V: void
I: int
S: string
B: bool
p: pointer
*/

// <[a name="jFolder"/]>
class jFolder : public jType
{
private:
    Folder* folder;
    Root* root;

/* Function pointer example
    static cx_Cp* r;
    static S_E* r_getName;
    static V_Vp* r_addMessage;
    static V_Vp* r_addFolder;
    static Vp_SB* r_getSubFolder;
    static V_I* r_remove;
    static I_E* r_getSize;
    static Vp_E* r_getMessages;
    static Vp_E* r_getParent;
    static Vp_E* r_getSubFolders;
*/    
public:
    jFolder(void* f);
    jFolder(char* name);
    
    const char* getName();
    void add(jMessage* message);
    void add(jFolder* f);
    jFolder* getSubFolder(char* name);
	jFolder* getSubFolder(char* name, bool createIfNecessary);
    void remove(int index);
    int getSize();
    uIterator* getMessages();
    jFolder* getParent();
    uIterator* getSubFolders();
    
    friend class jMail;
};
    
// <[a name="jAccount"/]>
class jAccount : public jType
{
private:
    Account* account;
public:
    jAccount(void* a); 
    jFolder* getRoot();
    jAddressee* getUser();
    void log(char* message);
};

// <[a name="jMenu"/]>
class jMenu : public jType
{
private:
    Menu* menu;
public:
    jMenu(void* m);
    int getSize();
    void go();
    void append(jType* option);
};

// <[a name="jMenuOption"/]>
class jMenuOption : public jType
{
private:
	PluginMenuOption* option;
public:
	jMenuOption(void* o);
	jMenuOption(char* text, char* runMethodName);
	
	const char* getDisplayText();
};

// <[a name="jFolderMenuOption"/]>
class jFolderMenuOption : public jType
{
private:
	FolderMenuOption* option;
public:
	jFolderMenuOption(void* o);
	jFolderMenuOption(char* text, jFolder* folder);
	
	const char* getDisplayText();
};

// <[a name="jMessageMenuOption"/]>
class jMessageMenuOption : public jType
{
private:
	MessageMenuOption* option;
public:
	jMessageMenuOption(void* o);
	jMessageMenuOption(char* text, jMessage* message);
	
	const char* getDisplayText();
};

// <[a name="jFolderMenuPlugin"/]>
class jFolderMenuPlugin : public jType
{
private:
	PluginFolderMenuOption* option;
public:
	jFolderMenuPlugin(void* o);
	jFolderMenuPlugin(char* text, jFolder* folder, char* runMethodName);
	
	const char* getDisplayText();
};

// <[a name="jPrompt"/]>
class jPrompt : public jType
{
private:
	Prompt* prompt;
public:
	jPrompt(void* p);
	jPrompt(char* display, bool isAtomic);
	char* askUser();
	char* askUser(char* defaultResponse);
};

// <[a name="jMessageInput"/]>
class jMessageInput : public jType
{
private:
	MessageInput* messageInput;
public:
	jMessageInput(void* m);
	jPrompt* getToPrompt();
	jPrompt* getSubjectPrompt();
	jPrompt* getBodyPrompt();
	jMessage* getMessage();
};

// <[a name="jDataDisplay"/]>
class jDataDisplay : public jType
{
private:
	DataDisplay* dataDisplay;
public:
	jDataDisplay(void* d);
	void setLabel(char* l);
	void setValue(char* v);
	void display();
};

// <[a name="jMessageDisplay"/]>
class jMessageDisplay : public jType
{
private:
	MessageDisplay* messageDisplay;
public:
	jMessageDisplay(void* m);
	jDataDisplay* getFrom();
	jDataDisplay* getSubject();
	jDataDisplay* getBody();
	jDataDisplay* getSentTime();
	jMessage* getMessage();
	void initValues();
	void align();
};

/* Function pointer example:
class jMail
{
public:
	static void init();
};
*/

#endif                                                          	
