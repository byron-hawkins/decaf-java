// File : uMail.i 


%module uMail


%{
#include "../../../native/template.h"
#include "../../../../umail/jMail.h"
#include "../../../../umail/util.h"
#include "../../../native/plugin.h"
#include "time.h"
%}


// <[a name="jMessageDisplay"/]>
// ********** jMessageDisplay *********** //
%typemap(javacode) jMessageDisplay
%{
  public void init()
  {
  	this.initValues();
  	jTime sentTime = this.getMessage().getSentTime();
  	sentTime.init();
  	this.getSentTime().setValue(sentTime.format());
  }
%}



// <[a name="jPluginLib"/]>
// ********** jPluginLib *********** //
%typemap(javacode) jPluginLib
%{
  static
  {
    System.loadLibrary("jMail");
  }
%}


// <[a name="jHandle"/]>
// ********** jHandle *********** //
%typemap(javaimports) jHandle
%{
import java.util.Iterator;
%}

%typemap(javacode) jHandle
%{
  public String toString()
  {
  	StringBuffer buffer = new StringBuffer();
  	buffer.append(this.getMethodName());
  	buffer.append("(");
  	
  	Iterator parameters = this.getParameterTypes();
  	while (parameters.hasNext())
  	{
  		buffer.append(parameters.next().toString());
    }
    buffer.append(")");
    return buffer.toString();
  }
%}



// <[a name="uIterator"/]>
// ********** uIterator *********** //
%typemap(javainterfaces) uIterator "java.util.Iterator"

class uIterator
{
public:
	virtual bool hasNext() = 0;
	virtual jobject next() = 0;
	virtual void* getNext() = 0;
	void remove();
};

class jString 
{
public:
    char* get();
};

class jHandle
{
public:
	jHandle();
	char* getMethodName();
	char* getPluginName();
	char* getDescription();
	uIterator* getParameterTypes();
	void setPluginName(char* n);
};

class jType
{
public:
    char* unqualifiedName();
    void* getContent();
};


// <[a name="jTime"/]>
// ********** jTime *********** //
%typemap(javaimports) jTime
%{
import java.text.*;
%}

%typemap(javabase) jTime "java.util.GregorianCalendar"

%typemap(javacode) jTime
%{
  public void init()
  {
    super.set(this.getYear() + 1900, this.getMonth(), this.getDay(), this.getHour(), this.getMinute(), this.getSecond());
  }
  
  protected static final DateFormat s_format = new SimpleDateFormat("MM/dd/yyyy hh:mm");
  
  public String format()
  {
  	return s_format.format(this.getTime());
  }
%}

class jTime 
{
public:
	int getSecond();
	int getMinute();
	int getHour();
	int getDay();
	int getMonth();
	int getYear();
	bool isDST();
};



// <[a name="jPluginLib"/]>
// ********** jPluginLib *********** //
class jPluginLib
{
public:
	static void init(void* lib);
    static void log(char* message);
    static const char* getClasspath();
};



// <[a name="jMessage"/]>
// ************ jMessage *********** //
%typemap(javacode) jMessage
%{
  public jTime getSentTime() {
  	jTime time = new jTime(uMailJNI.jMessage__getSentTime(swigCPtr), false);
  	time.init();
  	return time;
  }
%}


class jMessage : public jType
{
public:
    jMessage(void* m);
    
    jAddressee* getFrom();
    jAddressee* getTo();
    char* getSubject();
    char* getBody();
    
    void setTo(char* address);
    void setSubject(char* subject);
    void setBody(char* body);

    jTime* _getSentTime();
};

%import "../../../native/plugin.h"
%include "../../../../umail/jMail.h"


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
//    bool contains(Comparable* c);
    ListIterator* iterator();
    void shrink();
};


/*
// <[a name="ListIterator"/]>
class ListIterator : public uIterator
{
private:
    Node* current;
public:
    //ListIterator(Node* first);
    bool hasNext();
    jobject next();
    void* getNext();
    void remove();
};
*/

// <[a name="jVector"/]>
class jVector 
{
public:
	jVector();
	jobject get(int index);
	void set(int index, jobject value);
	void add(jobject value);
	void remove(int index);
	int getSize();                   
	uIterator* iterator();
};


