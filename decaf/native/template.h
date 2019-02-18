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

// template.h

#include <jni.h>
#include <stdlib.h>
#include <iostream>

#ifndef __template_t__
#define __template_t__

using namespace ::std;



// ****** Declaration ******* //



// <[a name="uIterator"/]>
/*
This virtual iterator has an annoying header in C++ because it is
exported to Java as an implementation of the interface java.util.Iterator.
It is backed by <[a href="#VectorIterator"]>VectorIterator<[/a]> below.
The Java interface association is made in the SWIG generator file 
<[a href="uMail_i.html#uIterator"]>uMail.i<[/a]>, such that Decaf users
see uIterator as a standard collection type.  
*/
class uIterator
{
public:
	virtual bool hasNext() = 0;
	virtual jobject next() = 0;
	virtual void* getNext() = 0;
	virtual void remove() = 0;
	virtual void reset() = 0;
};

template<class T>
class VectorIterator;


// <[a name="uVector"/]>
/*
A simple vector template for uMail, which I used to experiment with exporting
templated classes to Java.  The <[a href="uMail_i.html#jVector"]>export<[/a]> 
seems to work normally, and ideally one
would implement the java.util.Collection and java.util.List interfaces with 
this class, such that uMail vectors appear in Java as standard collection types.
*/
// uVector 
template<class T> 
class uVector 
{
private:
	T** data;
	void ensureCapacity(int index);
	int bufSize;

protected:	
	int size;
	
public:
	uVector();
	T* get(int index);
	void set(int index, T* value);
	void add(T* value);
	void remove(int index);
	int getSize();                   
	VectorIterator<T>* iterator();
	VectorIterator<T>* iterator(uIterator* originalContent);
};


// VectorIterator
template<class T>
class VectorIterator : public uIterator
{
private:
	uVector<T>* data;
	uIterator* originalContent;
	int index;
public:
	VectorIterator(uVector<T>* d, uIterator* o) : data(d), index(0), originalContent(o) {};
	bool hasNext();
	void* getNext();
	jobject next();
	void remove();
	void reset();
};




// ****** Implementation ******* //



// uVector
template <class T>
uVector<T>::uVector()
{
	this->size = 0;
	this->bufSize = 10;
	this->data = (T**)malloc(sizeof(T*) * this->bufSize);
}

template <class T>
T* uVector<T>::get(int index)
{
	if (index >= this->size)
	{
		return NULL;
	}
	return this->data[index];
}

template <class T>
void uVector<T>::add(T* value)
{
	ensureCapacity(this->size);

	this->data[this->size] = value;
	this->size++;
}

template <class T>
void uVector<T>::remove(int index)
{
	if (index >= this->size)
	{
		return;
	}
	
	for (int i = index; i < (this->size-1); i++)
	{
		this->data[i] = this->data[i+1];
	}
	this->size--;
}

template <class T>
int uVector<T>::getSize()
{
	return this->size;
}

template <class T>
void uVector<T>::set(int index, T* value)
{
	ensureCapacity(index+1);
	
	this->data[index] = value;
}

template <class T>
void uVector<T>::ensureCapacity(int index)
{
	if (index >= this->bufSize)
	{
		this->bufSize += 10;
		T** old = this->data;
		this->data = (T**)malloc(sizeof(T*) * this->bufSize);
		for (int i = 0; i < this->size; i++)
		{
			this->data[i] = old[i];
		}
		delete(old);
	}
}

template <class T>
VectorIterator<T>* uVector<T>::iterator()
{
	return (new VectorIterator<T>(this, NULL));
}

template <class T>
VectorIterator<T>* uVector<T>::iterator(uIterator* originalContent)
{
	return (new VectorIterator<T>(this, originalContent));
}


// <[a name="VectorIterator"/]>
// VectorIterator
template <class T>
bool VectorIterator<T>::hasNext()
{
	return (this->index < this->data->getSize());
}

template <class T>
jobject VectorIterator<T>::next()
{
	if (this->originalContent != NULL)
	{
		this->originalContent->next();
	}
	return (jobject)this->data->get(this->index++);
}

template <class T>
void* VectorIterator<T>::getNext()
{
	if (this->originalContent != NULL)
	{
		this->originalContent->getNext();
	}
	return this->data->get(this->index++);
}

template <class T>
void VectorIterator<T>::remove()
{
	if (this->originalContent != NULL)
	{
		this->originalContent->remove();
	}
	this->data->remove(this->index-1);
}

template <class T>
void VectorIterator<T>::reset()
{
	this->index = 0;
}

/*
// uVector for jobject
template <>
uVector<_jobject>::uVector()
{
	this->size = 0;
	this->bufSize = 10;
	this->data = (jobject*)malloc(sizeof(jobject) * this->bufSize);
}

template <>
jobject uVector<_jobject>::get(int index)
{
	if (index >= this->size)
	{
		return NULL;
	}
	return this->data[index];
}

template <>
void uVector<_jobject>::add(jobject value)
{
	ensureCapacity(this->size);

	cout << "adding " << value << endl;
	
	value = uVector::env->NewGlobalRef(value);
	
	this->data[this->size] = value;
	this->size++;
}

template <>
void uVector<_jobject>::remove(int index)
{
	if (index >= this->size)
	{
		return;
	}
	
	for (int i = index; i < (this->size-1); i++)
	{
		this->data[i] = this->data[i+1];
	}
	this->size--;
}

template <>
int uVector<_jobject>::getSize()
{
	return this->size;
}

template <>
void uVector<_jobject>::set(int index, jobject value)
{
	ensureCapacity(index+1);
	
	this->data[index] = value;
}

template <>
void uVector<_jobject>::ensureCapacity(int index)
{
	if (index >= this->bufSize)
	{
		this->bufSize += 10;
		jobject* old = this->data;
		this->data = (jobject*)malloc(sizeof(jobject) * this->bufSize);
		for (int i = 0; i < this->size; i++)
		{
			this->data[i] = old[i];
		}
		delete(old);
	}
}
*/
#endif
