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

// plugin.h

#include <jni.h>
//#include <windows.h>
#include <stdarg.h>
#include <iostream>
#include <fstream>
#include <string>

#define clog *Plugin::log

#ifndef __plugin_h__
#define __plugin_h__

#include <template.h>

using namespace ::std;

/* Anonymous class to serve as the base of function pointers passed to the
dynamic library from the native application.
class Root{};
*/

class jObject;
class jClass;
class jMethod;

// <[a name="Method"/]>
/*
The Method class identifies a native method by name and pointer reference.  When
wrapper classes in the dynamic library use function pointers to obtain reference
to the body of the native application, instances of this class are passed from
the application to the dynamic library.  The library casts the void* handle
to the appropriate function type (as a member of the anonymous class Root),
and uses it to call the functions that have been loaded in the main application's
address space.
*/
/* Function pointer example:
class Method
{
private:
	string name;
	void* handle;
public:
	Method(string n, void* h) : name(n), handle(h) {};
	string getName();
	void* getHandle();
};

typedef uVector<Method> Methods;
*/

typedef uVector<char> charVector;
typedef uVector<jObject> jObjects;
typedef uVector<_jobject> jVector;
typedef uVector<void*> vVector;

// <[a name="jVM"/]>
/*
jVM represents the Java virtual machine, where all plugins are run and edited.
*/
class jVM
{
private:
    JavaVM* jvm;
	string classpath;

	jVM::jVM(JavaVM* vm, JNIEnv* e, string c) : jvm(vm), env(e), classpath(c) {};

    static string previous_jvmLocation;
    static string previous_classpath;
public:
    JNIEnv* env;

 	jVM::~jVM();
 	const char* getClasspath();

 	void reloadEnv();

    static jVM* create(const char* jvmLocation, string classpath);
    static jVM* reload();
};

// <[a name="jString"/]>
/*
jString provides a string object that is both suitable for a collection and
recognizable to Java.
*/
class jString
{
private:
    string content;
    jObject* jContent;
public:
	jString() { cerr << "Warning!  Call to unsupported constructor jString()" << endl; };
    char* get();

    // hidden from Decaf
    static jClass* clazz;
    static jMethod* cx;

    jString(string s);
    jobject getObject();
    static void init();
};

// <[a name="jHandle"/]>
/*
jHandle serves as a handle to the Decaf editor, providing facilities to launch
the editor with any plugin and method.
*/
class jHandle
{
private:
	string methodName;
	string pluginName;
	string description;
	jVector* parameterTypes;

	static jClass* decafEditor;
	static jMethod* go;
	static jClass* jHandleClass;
	static jMethod* jHandleCx;
public:
	jHandle() { cerr << "Erroneous call to jHandle()!  This constructor is provided only for SWIG simplification (so it doesn't wrap all the unused parameters which are never accessed from Java anyway." << endl; };
	char* getMethodName();
	char* getPluginName();
	char* getDescription();
	uIterator* getParameterTypes();
	void setPluginName(char* n);

	// not available to Decaf
	jHandle(string m, string p, string d, jVector* pt) : methodName(m), pluginName(p), description(d), parameterTypes(pt) {};
	void edit();
	static void init();
};

// <[a name="jType"/]>
/*
To export a class from the native application to the Plugin type library, wrap
the native class in a subclass of jType.  In the wrapper's constructor, pass
along to jType the (unqualified) classname of the wrapper, along with a pointer
to the object being wrapped.
*/
class jType
{
protected:
    string uName;
    void* content;
public:
    jType(string u);
    jType(string u, void* c);
    char* unqualifiedName();
    void* getContent();

    jType() { cerr << "Warning!  Call to bogus empty constructor of jType!" << endl; };
};

// <[a name="jMethod"/]>
/*
jMethod wraps an instance of the JNI method identifier construct jmethodID.
*/
class jMethod
{
private:
    jmethodID method;
public:
    jMethod(jclass clazz, string name, string parameters, bool isStatic);
    jmethodID getMethod();
    bool isValid();
};

// <[a name="jObject"/]>
/*
jObject wraps an instance of the JNI class jobject.
*/
class jObject
{
private:
    jobject object;
public:
    jObject(jclass clazz, jmethodID cx);
    jObject(jclass clazz, jmethodID cx, jObject* cxParam);
    jObject(jclass clazz, jmethodID cx, void* cxParam1, bool cxParam2);
    void makeGlobal();
    jobject getObject();
    void call(jMethod* method, jObjects* args);
    bool isValid();
};

// <[a name="jClass"/]>
/*
jClass wraps an instance of the JNI jclass, a construct which contains the
definition of a Java class.
*/
class jClass
{
private:
    jclass clazz;
    string classname;

public:
    jClass(string c);
    void makeGlobal();
    jclass getClass();
    jMethod* getMethod(string name, string signature);
    jMethod* getStaticMethod(string name, string signature);
    jObject* newObject(jMethod* cx);
    jObject* newObject(jMethod* cx, jObject* param);
    jObject* new_pObject(jMethod* cx, void* param1);
    bool isValid();
};

// <[a name="PluginParameter"/]>
/*
The PluginParameter class represents the declaration and Java constructs of
a parameter to a plugin.
*/
class PluginParameter
{
private:
    string jName;
    string jDotName;
    string jDeclaration;
    jClass* paramClass;
    jMethod* paramCx;
    jObject* pVoid;

    static jClass* pVoidType;
    static jMethod* pVoidCx;
public:
    PluginParameter(jType* content);
    string getDeclaration();
    string get_jName();
    string get_jDotName();
    void setContent(jType* content);
    jObject* wrap();
    bool isValid();

    static void init();

    friend class Plugin;
};

typedef uVector<PluginParameter> PluginParameters;

// <[a name="Plugin"/]>
/*
The Plugin class represents a method of a Java class (which is presumed to be a
plugin).  It has three phases to its life-cycle:

1) instantiation: creating an instance of Plugin is like saying, "somewhere
out there is a Java class containing a method 'x' with parameters 'y,z,...'".
The plugin is now available to edit(), but cannot yet be run(): it must first
be resolved to a Java class (step 2).

2) class resolution: call the createFor(string) method to resolve this plugin to
an actual Java class file (whose name is the string parameter); a new instance
of Plugin with the specified name will be returned.

3) parameter resolution: call the instantiate(int, ...) method to resolve the
parameters of this Plugin to actual object values.  The values types must each
be a subclass of jType (above), and each must have a constructor with a single
void* parameter by which its data can be passed.  If more complex types are
required, some modifications to this wrapper will need to be made.

After step 3 has been completed, the Plugin is ready to run().
*/
class Plugin
{
private:
    string name;
    string runMethodName;
    string paramDescription;
    string signature;
    jObject* plugin;
    jMethod* runMethod;
    PluginParameters* params;
    jVector* paramTypes;
    jObjects* args;
    bool runnable;

	Plugin(Plugin* model);

    static string NONE;
    static string classnameBase;
    static string classnameDottedBase;
public:
    static ofstream* log;
    static jVM* jvm;
    static void* appHandle;

    Plugin(string runMethodName, int jParamTypeCount, ...);
    string getName();
    const string describe();
    void instantiate(int jParamCount, ...);
    void run();
    void edit(string pluginName);
    Plugin* createFor(string name);

    static void init(jVM* vm, string classnameBase, void* app, string logname);
    static void init(jVM* vm, string classnameBase, void* app, ofstream* logfile);
    static void callEmptyStaticMethod(string className, string methodName);
    static void require(bool truth, string consequences);
    static string qualify(string classname);
    static string dottify(string classname);

    /* Function pointer example
    static Methods* methods;
    static void* getMethod(string name);
    static void init(jVM* vm, string classnameBase, void* app, string logname, Methods* m);
    static void init(jVM* vm, string classnameBase, void* app, ofstream* logfile, Methods* m);
    */
};

// <[a name="jPluginLib"/]>
/*
The jPluginLib class serves as a bridge between the native application and the
dynamic library loaded by Java.  Ideally, we would have the Java plugins call
directly back to the native application.  But Java will only call native functions
that are exported from a dynamic library, even when the <[a href="../../Glossary.html#JVM"]>JVM<[/a]> is running in the
process space of the native application (!).  The developer has two options:

1) build the entire application into a dynamic library.
2) build the application into a standard executable, and pass a block of
<[a href="jMail.html#function_pointer"]>function pointers<[/a]> through the JVM to the dynamic library.

In my reckless experimentation, I've left the current implementation of uMail halfway
in between these two options, demonstrating the worst of both worlds.  Oops.  Well,
at least you can see what I'm talking about.

Note that jPluginLib loads the dynamic library from its Java code in <[a href="uMail_i.html#jPluginLib"]>this<[/a]> manner.
*/
class jPluginLib
{
private:
	jVM* jvm;
	char* classnameBase;
	void* app;
	ofstream* logfile;

	/* Function pointer example:
	Methods* methods; */
public:
	jPluginLib() { cout << "Warning!  Constructor jPluginLib() called.  This constructor exists only for compilation with an erroneous assumption made by SWIG.  It should never be called." << endl; };
	jPluginLib(jVM* vm, char* c, void* a, ofstream* l) : //, Methods* m)
 		jvm(vm), classnameBase(c), app(a), logfile(l) /*, methods(m)*/ {};

    static uIterator* create_jIterator(string jName, string pName, uIterator* content);

	// this are available to JNI
	static void init(void* lib);
    static void log(char* message);	// available to plugins
    static const char* getClasspath();
};

#endif
