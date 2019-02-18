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

// util.h

/*
This file contains a few nice utilities which are duplicates of standard library
offerings.  I've implemented my own here to try out the various aspects of 
integrating them with Java.
*/

#ifndef _util_h 
#define _util_h

#include <forward.h>

using namespace ::std;

#include <template.h>

// <[a name="Node"/]>
/*
A Node of class List, below.
*/
class Node
{
private:
    void* content;
    Node* next;
    Node* prev;
    List* owner;
public:
    Node(List* owner, void* content);
    void* getContent();
    void follow(Node* next);
    void remove();
    Node* getNext();
    Node* getPrev();
    Node* getFirst();
};

// <[a name="List"/]>
/*
A linked list.
*/
class List
{
private:
    int size;
    Node* first;
    Node* last;
public:
    List();
    void append(void* addMe);
    int getSize();
    ListIterator* iterator();
    void shrink();
    
    friend class Node;
};

// <[a name="ListIterator"/]>
/*
An iterator over a List (above), derived from uIterator such that it can be
exported to Java as a java.util.Iterator.
*/
class ListIterator : public uIterator
{
private:
    Node* current;
public:
	ListIterator();	// bogus, for SWIG
    bool hasNext();
    jobject next();
    void* getNext();
    void remove();
    void reset();

    // hidden from Decaf
    ListIterator(Node* first);
};

// <[a name="Property"/]>
/*
A name-value association, as commonly found in a properties file.
*/
struct Property
{
	string name;
	string value;
};

typedef uVector<Property> Properties;

// <[a name="Config"/]>
/*
A file of properties.
*/
class Config
{
private:
	Properties* properties;
public:
	Config(string filename);
	string get(string property);
};

#endif
