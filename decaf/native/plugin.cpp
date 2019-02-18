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

#include <plugin.h> 

/* Function pointer example:
// Method
string Method::getName()
{
	return this->name;
}

void* Method::getHandle()
{
	return this->handle;
}
*/
 
// jVM
jVM* Plugin::jvm = NULL;
static const string classpathParameter = "-Djava.class.path=";
string jVM::previous_jvmLocation;
string jVM::previous_classpath;

jVM::~jVM()
{
    this->jvm->DestroyJavaVM();
}

jVM* jVM::create(const char* jvmLocation, string classpath)
{
//	char heapJvmLocation[jvmLocation.size()];
//	strcpy(heapJvmLocation, jvmLocation.c_str());

	//cerr << "Loading library from " << jvmLocation << endl;

    HINSTANCE handle;
    if ((handle = LoadLibrary(jvmLocation))==0)
    {
        cerr << "unable to load jvm.dll from filesystem at " << jvmLocation << endl;
        return NULL;
    } 
    
    //cerr << "Loaded library " << jvmLocation << endl;
    
    char* classpathArg = (char*)malloc(classpathParameter.size() + classpath.size());
    strcpy(classpathArg, classpathParameter.c_str());
    strcat(classpathArg, classpath.c_str());
    
    //cerr << "classpathArg: " << classpathArg << endl;
    
    JavaVMOption *options;
    options = (JavaVMOption*)malloc(sizeof(JavaVMOption));
    options[0].optionString = classpathArg;
    options[0].extraInfo = NULL;
    
    //cerr << "created options" << endl;

    JavaVMInitArgs initArgs;
    initArgs.version = JNI_VERSION_1_2;
    initArgs.nOptions = 1;
    initArgs.options = options;
    initArgs.ignoreUnrecognized = FALSE;

    //cerr << "created vm init args" << endl;

    typedef jint (JNICALL *PFJ3)(JavaVM **pvm, void **penv, void *args);
    PFJ3 pfnCreateJavaVM = (PFJ3)GetProcAddress(handle, "JNI_CreateJavaVM");

    //cerr << "creating pfn JNI_CreateJavaVM..." << endl;

    if (pfnCreateJavaVM == 0)
    {
        cerr << "Error: can't find function JNI_CreateJavaVM." << endl;
        return NULL;
    }

    //cerr << "created pfn JNI_CreateJavaVM" << endl;
	
	JavaVM* jvm;
	JNIEnv* env;
    jint res = (jint)pfnCreateJavaVM(&jvm,(void**)&env, &initArgs);

//    cerr << "creating jvm..." << endl;

    if (res < 0)
    {
        cerr << "Received error code " << res << " trying to create Java VM" << endl;
        return NULL;
    }
    
//    cerr << "created jvm" << endl;

	jVM::previous_jvmLocation = jvmLocation;
	jVM::previous_classpath = classpath;

 	jVM* wrapper = new jVM(jvm, env, classpath);
 	return wrapper;
}

const char* jVM::getClasspath()
{
	return this->classpath.c_str();
}

void jVM::reloadEnv()
{
	this->jvm->GetEnv((void**)&(this->env), JNI_VERSION_1_2);
}

jVM* jVM::reload()
{
 	clog << "reloading jvm" << endl;
	return create(jVM::previous_jvmLocation.c_str(), jVM::previous_classpath);
}

// jHandle
jClass* jHandle::decafEditor;
jMethod* jHandle::go;     
jClass* jHandle::jHandleClass;
jMethod* jHandle::jHandleCx;

void jHandle::init()
{
	jHandle::decafEditor = new jClass("com/bitwise/decaf/editor/DecafEditor");
	Plugin::require(decafEditor->isValid(), "Unable to find the class com.bitwise.decaf.editor.DecafEditor -- exiting.");

	jHandle::go = decafEditor->getStaticMethod("go", "(L" + Plugin::qualify("jHandle") + ";)V"); 
	Plugin::require(go->isValid(), "Unable to find the class " + Plugin::qualify("jHandle") + " -- exiting.");

	jHandle::jHandleClass = new jClass(Plugin::qualify("jHandle"));
	Plugin::require(jHandleClass->isValid(), "Unable to find the class com.bitwise.umail.jHandle -- exiting.");

	jHandle::jHandleCx = jHandleClass->getMethod("<init>", "(JZ)V");
	Plugin::require(jHandleCx->isValid(), "Unable to find the constructor com.bitwise.umail.jHandle(long, boolean) -- exiting.");
}

char* jHandle::getMethodName()
{
	return (char*)this->methodName.c_str();
}

char* jHandle::getPluginName()
{
	clog << "jHandle::getPluginName(): returning " << this->pluginName << endl; 
	return (char*)this->pluginName.c_str();
}

char* jHandle::getDescription()
{
	return (char*)this->description.c_str();
}

uIterator* jHandle::getParameterTypes()
{
	return this->parameterTypes->iterator();
}

void jHandle::setPluginName(char* n)
{
	this->pluginName = n;
}

void jHandle::edit()
{
	jObject* editHandle = jHandleClass->new_pObject(jHandleCx, this);
	if (!editHandle->isValid())
	{
		cerr << "I'm unable to create a jHandle to the plugin method.  Editing " << this->pluginName << " is currently unavailable." << endl;
		return;
	}	
	
	cout << "Opening Decaf editor for " << this->pluginName << "." << this->methodName << "() (this may take a few moments)" << endl;
 	
	Plugin::jvm->env->CallStaticVoidMethod(jHandle::decafEditor->getClass(), jHandle::go->getMethod(), editHandle->getObject());
}

// jType
jType::jType(string u) : uName(u), content(NULL) 
{
}

jType::jType(string u, void* c) : uName(u), content(c) 
{ 
}

char* jType::unqualifiedName()
{
    return (char*)this->uName.c_str();
}                   

void* jType::getContent()
{
    return this->content;
}

// jString
jClass* jString::clazz;
jMethod* jString::cx;

void jString::init()
{
	jString::clazz = new jClass(Plugin::qualify("jString"));
	Plugin::require(clazz->isValid(), "Unable to find the class " + Plugin::qualify("jString") + " -- exiting.");
	
	//jString::clazz->makeGlobal();
	jString::cx = clazz->getMethod("<init>", "(JZ)V");
	Plugin::require(cx->isValid(), "Unable to find the constructor " + Plugin::qualify("jString") + "(long, boolean) -- exiting.");
}

jString::jString(string c) : content(c) 
{
    this->jContent = jString::clazz->new_pObject(jString::cx, this);
    //this->jContent->makeGlobal();
}

char* jString::get()
{
    return (char*)this->content.c_str();
}

jobject jString::getObject()
{
	return this->jContent->getObject();
}

// jClass
jClass::jClass(string c) : classname(c)
{
    this->clazz = Plugin::jvm->env->FindClass(classname.c_str());
    if (this->clazz == NULL)
    {
        clog << "Can't find class " << classname << endl;
    }
    else
    {
        this->makeGlobal();
    }
}

void jClass::makeGlobal()
{
	this->clazz = (jclass)Plugin::jvm->env->NewGlobalRef(this->clazz);
}

jclass jClass::getClass()
{
    return this->clazz;
}

jObject* jClass::newObject(jMethod* cx)
{
	jObject* o = new jObject(this->clazz, cx->getMethod());
	if (!o->isValid())
	{
		clog << "Can't instantiate " << this->classname << endl;
	}
    return o;
}

jObject* jClass::newObject(jMethod* cx, jObject* param)
{
	jObject* o = new jObject(this->clazz, cx->getMethod(), param);
	if (!o->isValid())
	{
		clog << "Can't instantiate " << this->classname << endl;
	}
    return o;
}

jObject* jClass::new_pObject(jMethod* cx, void* param1)
{
	jObject* o = new jObject(this->clazz, cx->getMethod(), param1, true);
	if (!o->isValid())
	{
		clog << "Can't instantiate " << this->classname << endl;
	}
    return o;
}

jMethod* jClass::getMethod(string name, string signature)
{
    return (new jMethod(this->clazz, name, signature, false));
}

jMethod* jClass::getStaticMethod(string name, string signature)
{
    return (new jMethod(this->clazz, name, signature, true));
}

bool jClass::isValid()
{
	return (this->clazz != NULL);
}

// jMethod
jMethod::jMethod(jclass clazz, string name, string parameters, bool isStatic)
{
	if (isStatic)
	{
	    this->method = Plugin::jvm->env->GetStaticMethodID(clazz, name.c_str(), parameters.c_str());
	}
	else
	{
	    this->method = Plugin::jvm->env->GetMethodID(clazz, name.c_str(), parameters.c_str());
	}
    if (this->method == NULL)
    {
        clog << "Can't find method " << name << parameters << endl;
    }
}

jmethodID jMethod::getMethod()
{
    return this->method;
}

bool jMethod::isValid()
{
	return (this->method != NULL);
}

// jObject
jObject::jObject(jclass clazz, jmethodID cx)
{
    this->object = Plugin::jvm->env->NewObject(clazz, cx);
    this->makeGlobal();
}

jObject::jObject(jclass clazz, jmethodID cx, jObject* cxParam)
{
    this->object = Plugin::jvm->env->NewObject(clazz, cx, cxParam->getObject());
    this->makeGlobal();
}

jObject::jObject(jclass clazz, jmethodID cx, void* cxParam1, bool cxParam2)
{
    this->object = Plugin::jvm->env->NewObject(clazz, cx, cxParam1, cxParam2);
    this->makeGlobal();
}

void jObject::makeGlobal()
{
	this->object = (jobject)Plugin::jvm->env->NewGlobalRef(this->object);
}

jobject jObject::getObject()
{
    return this->object;
}

void jObject::call(jMethod* method, jObjects* args)
{
    switch (args->getSize())
    {
        case 0:
            Plugin::jvm->env->CallObjectMethod(this->object, method->getMethod());
            break;
        case 1:
            Plugin::jvm->env->CallObjectMethod(this->object, method->getMethod(), args->get(0)->object);
            break;
        case 2:
            Plugin::jvm->env->CallObjectMethod(this->object, method->getMethod(), args->get(0)->object, args->get(1)->object);
            break;
        case 3:
            Plugin::jvm->env->CallObjectMethod(this->object, method->getMethod(), args->get(0)->object, args->get(1)->object, args->get(2)->object);
            break;
    }
}

bool jObject::isValid()
{
	return (this->object != NULL);
}

// PluginParameter
jClass* PluginParameter::pVoidType;
jMethod* PluginParameter::pVoidCx;

void PluginParameter::init()
{
    PluginParameter::pVoidType = new jClass(Plugin::qualify("SWIGTYPE_p_void"));
    Plugin::require(pVoidType->isValid(), "Unable to find class " + Plugin::qualify("SWIGTYPE_p_void") + " -- exiting");
    
    //PluginParameter::pVoidType->makeGlobal();
    PluginParameter::pVoidCx = pVoidType->getMethod("<init>", "(JZ)V");
    Plugin::require(pVoidCx->isValid(), "Unable to find constructor " + Plugin::qualify("SWIGTYPE_p_void") + "(long, boolean) -- exiting");
}

PluginParameter::PluginParameter(jType* content) 
{
    this->jName = Plugin::qualify(content->unqualifiedName());
    this->jDotName = Plugin::dottify(content->unqualifiedName());

    this->jDeclaration = "L";
    this->jDeclaration += this->jName + ";";
}

void PluginParameter::setContent(jType* content)
{
    //clog << "PluginParameter::setContent" << endl;
    
    this->pVoid = PluginParameter::pVoidType->new_pObject(PluginParameter::pVoidCx, content->getContent());
    this->paramClass = new jClass(this->jName);
	this->paramCx = this->paramClass->getMethod("<init>", "(L" + Plugin::qualify("SWIGTYPE_p_void") + ";)V"); 
}

string PluginParameter::get_jName()
{
    return this->jName;
}

string PluginParameter::get_jDotName()
{
    return this->jDotName;
}

string PluginParameter::getDeclaration()
{
    return this->jDeclaration;
}
    
jObject* PluginParameter::wrap()
{
    jObject* wrapper = this->paramClass->newObject(this->paramCx, this->pVoid);
    if (!wrapper->isValid())
    {
    	clog << "Unable to wrap " << this->jName << endl;
	}

	return wrapper;
}

bool PluginParameter::isValid()
{
	return (this->pVoid->isValid() && this->paramCx->isValid() && this->paramClass->isValid());
}

//Plugin
string Plugin::NONE = "<none>";
string Plugin::classnameBase = "";
void* Plugin::appHandle = NULL;
string Plugin::classnameDottedBase = "";
ofstream* Plugin::log;
//Methods* Plugin::methods;

void Plugin::init(jVM* vm, string c, void* app, string logname) //, Methods* m)
{
	Plugin::init(vm, c, app, new ofstream(logname.c_str(), ios::out | ios::ate | ios::app)); // , m);

	char* classnameBase = (char*)malloc(strlen(c.c_str()));
 	strcpy(classnameBase, c.c_str());	
 	jPluginLib* lib = new jPluginLib(vm, classnameBase, app, Plugin::log); //, m);
 	
 	// calling jPluginLib.init(), to pass the runtime context to the plugin library
 	jObject* pPluginLib = PluginParameter::pVoidType->new_pObject(PluginParameter::pVoidCx, lib);
 	
 	jClass* jPluginLibClass = new jClass(qualify("jPluginLib"));
	string signature = "(L" + qualify("SWIGTYPE_p_void") + ";)V"; 
	jmethodID method = Plugin::jvm->env->GetStaticMethodID(jPluginLibClass->getClass(), "init", signature.c_str());
	Plugin::jvm->env->CallStaticVoidMethod(jPluginLibClass->getClass(), method, pPluginLib->getObject());
}

void Plugin::init(jVM* vm, string c, void* app, ofstream* logfile) //, Methods* m)
{
    Plugin::jvm = vm;
    Plugin::classnameDottedBase = Plugin::classnameBase = c;
    Plugin::appHandle = app;
    Plugin::log = logfile;
    
    /* Function pointer example:
    Plugin::methods = m;
    */
    
	jHandle::init();
	jString::init();
	PluginParameter::init();
    
    int slash = classnameDottedBase.find("/");
    while (slash != string::npos)
    {
    	classnameDottedBase.replace(slash, 1, ".");
    	slash = classnameDottedBase.find("/");
    }
}

void Plugin::callEmptyStaticMethod(string className, string methodName)
{
	jClass* recipient = new jClass(qualify(className));
	jmethodID method = Plugin::jvm->env->GetStaticMethodID(recipient->getClass(), methodName.c_str(), "()V");
	Plugin::jvm->env->CallStaticVoidMethod(recipient->getClass(), method);                           
} 

void Plugin::require(bool truth, string consequences)
{
	if (!truth)
	{
		cout << consequences << endl;
		exit(1);
	}
}

string Plugin::qualify(string classname)
{
    return classnameBase + classname;
}

string Plugin::dottify(string classname)
{
	return classnameDottedBase + classname;
}

/* Function pointer example:
void* Plugin::getMethod(string name)
{
	for (int i = 0; i < Plugin::methods->getSize(); i++)
	{
		if (methods->get(i)->getName() == name)
		{
			return methods->get(i)->getHandle();
		}
	}
	
	return NULL;
}
*/

Plugin::Plugin(string runMethodName, int jParamTypeCount, ...)
{
	clog << "Plugin::Plugin: " << runMethodName << endl;

    this->name = NONE;
    this->runMethodName = runMethodName;

    this->params = new PluginParameters();

    va_list args;
    va_start(args, jParamTypeCount);
    jType* content;
    for (int i = 0; i < jParamTypeCount; i++)
    {
        content = (jType*)va_arg(args, jType*);
        this->params->add(new PluginParameter(content));
    }
    va_end(args);

    this->paramDescription = "(";
    this->paramTypes = new jVector();
    jString* paramString;

    for (int i = 0; i < jParamTypeCount; i++)
    {
        if (i > 0)
        {
		    this->paramDescription += ", ";
        }

        this->paramDescription += this->params->get(i)->get_jName();
        paramString = new jString(this->params->get(i)->get_jDotName());
        this->paramTypes->add(paramString->getObject());
    }
    this->paramDescription += ")";

	this->signature = "(";
    for (int i = 0; i < jParamTypeCount; i++)
    {
        this->signature += this->params->get(i)->getDeclaration();
    }
    this->signature += ")V";

	clog << "Plugin::Plugin: " << this->signature << endl;
}

Plugin::Plugin(Plugin* model)
{
	this->name = model->name;
	this->runMethodName = model->runMethodName;
	this->paramDescription = model->paramDescription;
	this->signature = model->signature;
	this->plugin = model->plugin;
	this->runMethod = model->runMethod;
	this->params = model->params;
	this->args = model->args;
	this->runnable = false;
}

void Plugin::instantiate(int jParamCount, ...) 
{    
    //clog << "Plugin::instantiate" << endl;

    this->runnable = false;

    jClass* pluginClass = new jClass(qualify(this->name));
    if (!pluginClass->isValid())
    {
    	//Plugin::jvm = jVM::reload();
    	//Plugin::jvm->reloadEnv();	
    	return;
	}
    this->runMethod = pluginClass->getMethod(runMethodName, this->signature); 
    if (!this->runMethod->isValid())
    {
    	//Plugin::jvm = jVM::reload();
    	//Plugin::jvm->reloadEnv();	
    	return;
	}

    //clog << "seeking constructor for class " << qualify(this->name) << endl;

    jMethod* pluginCx = pluginClass->getMethod("<init>", "()V");
    if (!pluginCx->isValid())
    {
    	return;
	}
    this->plugin = pluginClass->newObject(pluginCx);
    if (!this->plugin->isValid())
    {
    	return;
	}

    //clog << "collecting args " << endl;

    va_list args;
    va_start(args, jParamCount);
    jType* content;
    PluginParameter* parameter;
    for (int i = 0; i < jParamCount; i++)
    {
        content = (jType*)va_arg(args, jType*);
        parameter = this->params->get(i);
        parameter->setContent(content);
        if (!parameter->isValid())
        {
        	return;
    	}
    }
    va_end(args);
    
    //clog << "wrapping args " << endl;

	jObject* arg;
    this->args = new jObjects();
    for (int i = 0; i < jParamCount; i++)
    {
    	arg = this->params->get(i)->wrap();
    	if (!arg->isValid())
    	{
    		return;
		}
		else
		{
        	this->args->add(arg);
        }
    }
    
    this->runnable = true;
    
    //clog << "Plugin is runnable" << endl;
}

string Plugin::getName()
{
    return this->name;      
}

const string Plugin::describe()
{
    return this->paramDescription.c_str();
}

void Plugin::run()
{ 
	clog << "Plugin::run()" << endl;
	if (this->runnable)
	{
		clog << "Plugin::run(): " << this->name << "." << this->runMethodName << "() with " << this->args->getSize() << " arguments." << endl;
	    this->plugin->call(this->runMethod, this->args);
	}
	else
	{
		cerr << "Unable to call plugin method " << this->name << "." << this->runMethodName << "()" << endl;
	}
}

void Plugin::edit(string pluginName)
{
	jHandle* handle = new jHandle(this->runMethodName, pluginName, "arfy arf", this->paramTypes);
	handle->edit();
}

Plugin* Plugin::createFor(string name)
{
	Plugin* plugin = new Plugin(this);
	plugin->name = name;
	return plugin;
}
  
// jPluginLib
void jPluginLib::init(void* lib)
{
	jPluginLib* pluginLib = (jPluginLib*)lib;
	
	Plugin::init(pluginLib->jvm, pluginLib->classnameBase, pluginLib->app, pluginLib->logfile); //, pluginLib->methods);
	clog << "jPluginLib::init()" << endl;
}

uIterator* jPluginLib::create_jIterator(string jName, string pName, uIterator* content)
{
	clog << "util::create_jIterator" << endl;

	string jClassname = Plugin::qualify(jName);
	string parameter = "(L" + Plugin::qualify(pName) + ";)V";

 	jClass* iterClass = new jClass(jClassname);
 	if (!iterClass->isValid())
 	{
 		return NULL;
	}
 	jMethod* iterCx = iterClass->getMethod("<init>", parameter);
 	if (!iterCx->isValid())
 	{
 		return NULL;
	}
    
    jClass* pClass = new jClass(Plugin::qualify(pName));
 	if (!pClass->isValid())
 	{
 		return NULL;
	}
    jMethod* pCx = pClass->getMethod("<init>", "(JZ)V");
 	if (!pCx->isValid())
 	{
 		return NULL;
	}
      
 	jVector* wrapped = new jVector();
    jObject* pObject;
    jObject* iterObject;
    while (content->hasNext())
    {
        pObject = pClass->new_pObject(pCx, content->next());
	 	if (!pObject->isValid())
	 	{
	 		return NULL;
		}
        iterObject = iterClass->newObject(iterCx, pObject);
	 	if (!iterObject->isValid())
	 	{
	 		return NULL;
		}
        wrapped->add(iterObject->getObject());
    }
    
    content->reset();
    return wrapped->iterator(content);
}
 
void jPluginLib::log(char* message)
{
	clog << message << endl;
}

const char* jPluginLib::getClasspath()
{
	clog << "jPluginLib::getClasspath(): " << Plugin::jvm->getClasspath() << endl;
	return Plugin::jvm->getClasspath();
} 
