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

// main.cpp

#include <plugin.h>
#include <umail.h>
#include <util.h>

/*
The main entry point to uMail reads the configuration file uMail.conf, creates
the jvm (via class jVM from plugin.o), and starts uMail with an instance call 
to uMail::run().
*/
int main(int argc, char *argv[])
{                           
	Config* config = new Config("./uMail.conf");

 	string decaf_demo_dir = config->get("decaf_demo_dir");
 	//cout << "decaf_demo_dir: " << decaf_demo_dir << endl;
 	string classpath = config->get("classpath");
 	// cout << "classpath: " << classpath << endl;
 	string jvm_dll = config->get("jvm_dll");
 	//cout << "classpath: " << classpath << endl;
 	
	jVM* jvm = jVM::create(jvm_dll.c_str(), classpath);
	
	if (jvm == NULL)
	{
		cerr << "I'm sorry, I can't start Java.  I'll have to exit." << endl;
		exit(1);
	}
	
    uMail* app = new uMail(jvm, "com/bitwise/umail/");
    app->run();
}    

 

