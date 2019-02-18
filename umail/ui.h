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

// ui.h

/*
This file contains the implementation of uMail's user interface details.  It is
full of menus, prompts and display facilities.
*/

#ifndef __ui_h__
#define __ui_h__

#include <umail.h>
#include <forward.h>

using namespace ::std;

// <[a name="MenuItem"/]>
/*
Abstraction of a menu item, which consists either of a user option, labeled with
a number, or simply a message to be displayed as part of the menu text.
*/
class MenuItem
{
public:
	static const int OPTION;
	static const int MESSAGE;
	
	virtual int getType() = 0;
    virtual int display() = 0;
};

// <[a name="MenuOption"/]>
/*
A menu option, labeled with a number for user selection.  Subclasses implement
choose(), which is called with this option is chosen by the user.  If the option
implies system exit, return true from requiresExit(); if the option indicates 
a step up to the previous menu in the stack, return true from done().  Implements
MenuItem::getType() by returning OPTION.
*/
class MenuOption : public Activity, MenuItem
{
public:
    MenuOption() : Activity() {};
    MenuOption(Activity* a) : Activity(a) {};
    virtual void choose() = 0;
    virtual bool requiresExit();
    virtual bool done();
    int getType();
};

// <[a name="MenuMessage"/]>
/*
Message text for display with a menu.  Implements MenuItem::getType() by 
returning MESSAGE.  I'm not using this class at the moment, but feel free
to export it to Java for additional plugin mayhem.
*/
class MenuMessage : public MenuItem
{
private:
	string message;
public:
	MenuMessage(string m) : message(m) {};
	int display();
    int getType();
};

// <[a name="TextMenuOption"/]>
/*
A menu option with display text, optionally activating the 
<[a href="uMail.html#Activity"]>Activity<[/a]> superclass
in TextMenuOption(string, string).  
*/
class TextMenuOption : public MenuOption
{
private:
    void init(string text);
protected:
    string displayText;
public:
    TextMenuOption(string text);
    TextMenuOption(string text, string identifier);
    int display();
};

// <[a name="PluginMenuOption"/]>
/*
A menu option representing a user plugin, which it runs upon its selection.
*/
class PluginMenuOption : public TextMenuOption
{
public:
	PluginMenuOption(string text, string pluginMethodName);
	void choose();
	string getDisplayText();
};	

// <[a name="MessageMenuOption"/]>
/*
A menu option representing a mail message.  Choosing this option runs its 
plugin with the message and a MessageDisplay (below, providing user facilities
for displaying the message).
*/
class MessageMenuOption : public TextMenuOption
{
private:
	Message* message;
	
public:
	MessageMenuOption(string text, Message* m);
	void choose();
};

// <[a name="FolderMenuOption"/]>
/*
A menu option representing a mail folder.  Runs a FolderMenu (below) with 
its folder member when chosen.
*/
class FolderMenuOption : public TextMenuOption
{
private:
	Folder* folder;
public:
    FolderMenuOption(Folder* folder);
    FolderMenuOption(string d, Folder* folder);
    void choose();
};

// <[a name="PluginFolderMenuOption"/]>
/*
A plugin that resides in a folder menu.  It displays itself in the menu as the 
name of the plugin, and runs its plugin with the active account and the folder
when chosen.  For example, the "delete" folder menu option is implemented as a
PluginFolderMenuOption.
*/
class PluginFolderMenuOption : public TextMenuOption
{
private:
	Folder* folder;
public:
	PluginFolderMenuOption(string text, Folder* f, string runMethodName);
	void choose();
};

// <[a name="Menu"/]>
/*
An abstraction of a menu that maintains the list of menu items, and the question 
with which the user is prompted.  It is responsible for running the menu in 
askUser(), which routes the user's choice to the selected menu option, to Decaf
for plugin editing, or to Account (see account.o) with a new plugin assignment 
(depending on the user's response).  
*/
class Menu
{
private:
    string title;
protected:
    List* items;
    string question;

	Menu(string t) : title(t) {};
public:
    Menu(string t, List* i, string q) : title(t), items(i), question(q) {};
    Menu(const Menu& m);
    string getTitle();
    MenuOption* askUser();
    void add(MenuOption* option);
    int getSize();
    virtual bool go();
};

// <[a name="FolderMenu"/]>
/*
The folder menu, which calls its plugin to establish its menu items, and always
appends an item "Done" to the end of the list (which ends this menu and goes
one step up the menu stack when chosen).
*/
class FolderMenu : public Menu, Activity
{
private:
	Folder* folder; 
public:
	FolderMenu(string t, Folder* f, Activity* a); 
    
    static void activate(Activity* a);
};

// <[a name="PluginMenu"/]>
/*
The menu of user plugins, which may be run independently of any system activity
(each of these plugins always takes a single Account parameter).  
*/
class PluginMenu : public Menu, Activity
{
public:
	PluginMenu(string t, Activity* a);
};

// <[a name="Prompt"/]>
/*
A device for prompting the user.  It does obvious things that could easily be 
done from within a plugin, but it is meant to represent user interface features
that are not completely exposed to user plugins (i.e., pretend its some fancy
graphical thing the users aren't allowed to touch).  
*/
class Prompt 
{
protected:
    string display;
    int maxLength;
    bool atomic;
public:
    Prompt(string d, bool a);
    string askUser(string defaultResponse = "");
};

// <[a name="MessageInput"/]>
/*
A device for obtaining the contents of new mail messages from the user.
*/
class MessageInput
{
private:
	Message* message;
public:
    static Prompt* to;
    static Prompt* subject;
    static Prompt* body;
    
    MessageInput(Message* m) : message(m) {};
    Message* getMessage();
};

// <[a name="DisplayContext"/]>
/*
A base class providing facilities for aligning labeled text in nice columns.
*/
class DisplayContext
{
protected:
	int shortest;
	int longest;
public:
	DisplayContext();
	
	// notify this DisplayContext that a label of (length) has been added to
	// the context
 	void apply(int length);	
};

// <[a name="DataDisplay"/]>
/*
Provides facilities for displaying a label-value pair
*/
class DataDisplay
{
private:
	DisplayContext* context;
	string label;
	string value;
	int valueOffset;
public:
	DataDisplay(DisplayContext* c, string l);
	DataDisplay(DisplayContext* c);
	void setLabel(string l);
	void setValue(string v);
	int display();
	
	friend class MessageDisplay;
};

// <[a name="MessageDisplay"/]>
/*
Provides facilities for displaying a message.
*/
class MessageDisplay : public DisplayContext
{
private:
	Message* message;
public:
	DataDisplay* from;
	DataDisplay* subject;
	DataDisplay* body;
	DataDisplay* sentTime;
	
	MessageDisplay(Message* m);
	Message* getMessage();
	void initValues();
	void align();
};

// <[a name="CreateMessage"/]>
/*
Menu option for sending a new message.  When chosen, calls its plugin with a new 
mail message, initialized as being sent from the active account, and a 
MessageInput (above).
*/
class CreateMessage : TextMenuOption
{
public:
    CreateMessage();
    void choose();
};

// <[a name="MailViewer"/]>
/*
A menu option that, when chosen, initiates the stack of folder menus.
*/
class MailViewer : TextMenuOption
{
public:
    MailViewer();
    void choose();
};

// <[a name="PluginViewer"/]>
/*
A menu option displaying two items: system and user plugins.
*/
class PluginViewer : TextMenuOption
{
private:
	Menu* menu;
public:
    PluginViewer();
    void choose();
};

// <[a name="SystemOperations"/]>
/*
A menu option which, when chosen, shows the system operations not directly 
attached to any ui event.
*/
class SystemOperations : public MenuOption
{
private:
	Menu* menu;
public:
	SystemOperations();
	void choose();
	int display();
};

// <[a name="SystemOperation"/]>
/*
A menu option representing a particular system operation; allows its plugin to
be set, and gives a description of the operation on choose().
*/
class SystemOperation : public MenuOption
{
private:
	string displayText;
	string description;
public:
	SystemOperation(string disp, string desc, Activity* activity);
	void choose();
	int display();
};

// <[a name="Plugins"/]>
/*
A menu option that, when chosen, displays a mneu of user plugins.
*/
class Plugins : public TextMenuOption
{
public:
	Plugins();
	void choose();
};

// <[a name="StepUp"/]>
/*
The "Done" menu option (or any other wishing to indicate termination of the 
current menu and a step up to the previous menu in the menu stack).
*/
class StepUp : TextMenuOption
{
public:
    StepUp(string t, string n);
    void choose();
    bool done();
};

// <[a name="Exit"/]>
/*
A menu option representing the end.
*/
class Exit : public TextMenuOption
{
public:
    Exit();
    void choose();
    bool requiresExit();
    bool done();
};

#endif
