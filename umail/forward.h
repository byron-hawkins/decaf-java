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

// forward.h

#include <template.h>
#include <string>
#include <stdlib.h>
#include <iostream>

#include <jni.h>

using namespace ::std;

// umail.h
class uException;
class NewMessage;
class DisplayMenu;
class uMail;

// ui.h
class MenuOption;
class TextMenuOption;
class Prompt;
class ActivityMenuOption;
class FolderMenuOption;
class Menu;
class Prompt;
class MessageInput;
class DisplayContext;
class DataDisplay;
class MessageDisplay;
class CreateMessage;
class MailViewer;
class PluginViewer;
class FrontDoor;
class Exit;

// plugin.h
class jMethod;
class jObject;
class jClass;
class PluginParameter;
class Plugin;
class Root;

// util.h
class uIterator;
class List;
class ListIterator;	// : uIterator
class util;
class uString;

// jMail.h
class jString; // : public jType
class jAddressee; // : public jType
class jMessage; // : public jType
class jFolder; // : public jType
class jAccount; // : public jType
class jMenu; // : public jType

// account.h
class MailFile;
class Addressee;
class Message;
class Folder;
class PluginAssignment;
class PluginAssignments;
class Account;
class Accounts;

typedef uVector<Message> Messages;
typedef uVector<Folder> Folders;

// activity.h
class Activity;

// util.t
template<class T>
class uVector;

template<class T>
class VectorIterator; // : uIterator

typedef uVector<char> charVector;

#define clog *Plugin::log
                           
class jVM;
