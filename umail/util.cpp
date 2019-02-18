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

// util.cpp

#include <util.h>
#include <plugin.h>

// Node
Node::Node(List* owner, void* content)
{
    this->owner = owner;
    this->content = content;
    this->next = this->prev = NULL;
}

void* Node::getContent()
{
    return this->content;
}

void Node::follow(Node* previous)
{
    previous->next = this;
    this->prev = previous;
}

void Node::remove()
{
    if (this->prev != NULL)
    {
        this->prev->next = this->next;
    }
    else if (this->next != NULL)
    {
        this->next->prev = this->prev;
    }

    this->owner->shrink();

    delete this;
}

Node* Node::getNext()
{
    return this->next;
}

Node* Node::getPrev()
{
    return this->prev;
}

Node* Node::getFirst()
{
	return this->owner->first;
}

// List
List::List()
{
    this->size = 0;
    this->first = this->last = NULL;
}

void List::append(void* addMe)
{
    Node* last = new Node(this, addMe);

    if (this->last == NULL)
    {
        this->first = this->last = last;
    }
    else
    {
        last->follow(this->last);
        this->last = last;
    }
    this->size++;
}

int List::getSize()
{
    return this->size;
}

void List::shrink()
{
    this->size--;
    if (this->size == 0)
    {
    	this->first = this->last = NULL;
   	}
}

// ListIterator
ListIterator::ListIterator()	// bogus, for SWIG
{
	cerr << "Erroneous call to ListIterator::ListIterator(); this constructor is only provided for compilation with SWIG generated code." << endl;
}

ListIterator* List::iterator()
{
    return (new ListIterator(this->first));
}

ListIterator::ListIterator(Node* first)
{
    this->current = first;
};

bool ListIterator::hasNext()
{
    return (this->current != NULL);
}

jobject ListIterator::next()
{
    void* currentContent = this->current->getContent();
    this->current = this->current->getNext();
    return (jobject)currentContent;
}

void* ListIterator::getNext()
{
    void* currentContent = this->current->getContent();
    this->current = this->current->getNext();
    return currentContent;
}

void ListIterator::remove()
{
    this->current->getPrev()->remove();
}

void ListIterator::reset()
{
	if (this->current != NULL)
	{
		this->current = this->current->getFirst();
	}
}

Config::Config(string filename)
{
	ifstream* in = new ifstream(filename.c_str(), ios::in);
	if (!in->is_open())
	{
		cerr << "Unable to open config file " << filename << endl;
		return;
	}
	
	this->properties = new Properties();
	
	Property* property;
	string input;
	int mark;
	while (true)
	{
		*in >> input;
		if (in->eof())
		{
			break;
		}
		//cerr << "read: " << input << endl;
		property = new Property();
		mark = input.find("=");
		property->name = input.substr(0, mark);
		property->value = input.substr(mark+1);
		//cerr << "name: " << property->name << "; value: " << property->value << endl;
		this->properties->add(property);
	}
}

string Config::get(string property)
{
	for (int i = 0; i < this->properties->getSize(); i++)
	{
		if (this->properties->get(i)->name == property)
		{
			return this->properties->get(i)->value;
		}
	}
	
	return "";
}

