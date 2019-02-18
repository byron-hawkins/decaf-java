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

// umail.h

/*
This file contains the core implementation of uMail.
*/

#ifndef __umail_h__
#define __umail_h__

#include <forward.h>

using namespace ::std;

// <[a name="Activity"/]>
/*
uMail implements the Decaf principle "intrinsic accessibility" through the 
base class Activity.  Each uMail class that performs system activity derives 
from this Activity class, which maintains a plugin by which the user can modify 
that piece of system functionlity.  An Activity is "active" if it has been 
assigned a classname and Plugin*, with which it can run a plugin.  Here the term
"classname" refers to a class of Activity, and it is by this reference name that
a plugin is associated with an Activity for a particular user.  See the 
<[a href="account.html#Account"]>Account<[/a]> class for more information on plugin 
assignments.
*/
class Activity
{
protected:
    string classname;
    Plugin* pluginModel;
    string display;
    bool active;
    
    string getName();
    bool hasPlugin();
public:
	Activity() : active(false), classname("none") {};
	Activity(Activity* a);
    void activate(string cn, Plugin* model);
    string getDisplayText();
    void edit();
    void assignPlugin(string instanceName);
    Plugin* getPlugin();
    Plugin* getPlugin(Account* forAccount);
    bool isActive();
    
    friend class Account;
    friend class uMail;
};

// <[a name="DisplayMenu"/]>
/*
DisplayMenu maintains reference to a <[a href="ui.html#Menu"]>Menu<[/a]>, which it 
sends to its plugin for user access for calling its askUser() method to start its 
interaction with the user at the command line.  
*/
class DisplayMenu : public Activity
{
private:
    Menu* menu;
public:
    DisplayMenu(Menu* m);
    MenuOption* run();
};

// <[a name="SendMail"/]>
/*
SendMail sees a message to the "front door" of the currently active account.
It simply calls its plugin, which in the default implementation puts a copy
of the message in the inbox.  <[a href="#ReceiveMail"]>ReceiveMail<[/a]> 
(below) is responsible for putting the message in the recipient's account.
*/
class SendMail : public Activity
{
public:
	SendMail();
	void send(Message* message);
};

// <[a name="ReceiveMail"/]>
/*
ReceiveMail takes a message from the "front door" of the active account, gets
the recipient's account (creating it if necessary), and calls that user's 
ReceiveMail plugin with the message.  The default plugin implementation puts 
the message in the account's Inbox, though it is anticipated that users will
sort their mail into subfolders using the ReceiveMail plugin.
*/
class ReceiveMail : public Activity
{
public:
	ReceiveMail();
	void receive(Message* message);
};

// <[a name="uMail"/]>
/*
The uMail class represents the uMail application.  It logs users in and out,
initiates the main menu (delegated to member mainMenu), and oversees mail 
delivery (delegated to members sendMail and receiveMail via send(Message*)).
*/
class uMail
{
private:
    jVM* jvm;
    Account* activeAccount;
    Accounts* accounts;
    Menu* mainMenu;
    MailFile* mailFile;

    DisplayMenu* activity_mainMenu;    
    SendMail* sendMail;
    ReceiveMail* receiveMail;

    static uMail* _instance;

public:
    static uMail* instance();
    static Account* getActiveAccount();
    
    uMail(jVM* vm, string c);
    void run();
    void send(Message* message);
    
    friend class ReceiveMail;
};

#endif
