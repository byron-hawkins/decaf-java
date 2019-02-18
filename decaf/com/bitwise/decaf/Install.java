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
 
package com.bitwise.decaf;

import java.io.*;
import java.util.*;

import com.bitwise.decaf.editor.config.*;

import org.hs.util.file.*;

public class Install
{
	protected static final SuperFile[] classpath = 
		SuperFile.getClasspath
		(
			new String[] {
				"decaf", 
				"decaf/lib", 
				"decaf/lib/jars/gnu-regexp.jar", 
				"decaf/lib/jars/jaxb.jar", 
				"decaf/lib/jars/xerces.jar", 
				"decaf/lib/jars/xml4j.jar", 
				"decaf/lib/jars/kopi.jar"
			}
		);
	
	public static void main(String[] args)
	{
		try
		{
			System.out.println("Welcome to the Decaf Installer\n\n");
			
			String javaVersion = System.getProperty("java.version");
			String javaHome = System.getProperty("java.home");
			System.out.println("Ah, I see you're using Java version " + javaVersion + ",");
			System.out.println("   and your Java home seems to be " + javaHome + ".\n");
			
			int pf = javaHome.indexOf("Program Files"); 
			if (pf < 0)
			{
				pf = javaHome.indexOf("PROGRAM FILES");
			}
			if (pf >= 0)
			{
				javaHome = javaHome.substring(0, pf) + "progra~1" + javaHome.substring(pf+13);
			}
			
			if (!javaVersion.startsWith("1.4"))
			{
				System.out.println("I'm sorry, this application requires Java version 1.4.");
				System.out.println("Please run the Java Runtime Environment installer provided in the decaf_demo directory.");
				System.exit(1);
			}
	
			SuperFile log = new SuperFile("Install.log");
			org.hs.generator.GSourceFile.init(log.getPrintWriter());

			System.out.println("Initializing utilities...");
			com.bitwise.decaf.editor.Utils.init();

			SuperFile decaf_demo_dir = new SuperFile(".");
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			SuperFile configFile;
			
			while (true)
			{
				configFile = new SuperFile(decaf_demo_dir).extend("umail/config.xml");
				System.out.println("Looking for config.xml at " + decaf_demo_dir.getAbsolutePath() + "...");
	
				if (configFile.exists())
				{
					break;
				}
				
				System.out.println("I can't find the config file " + configFile.getAbsolutePath() + ".");
				System.out.print("Please enter the location of the decaf_demo directory: ");
				decaf_demo_dir = new SuperFile(in.readLine());
			}
				
			System.out.println("Reading config file " + configFile.getAbsolutePath());
			DecafConfig decafConfig = (DecafConfig)DecafConfig.unmarshal(configFile.getInputStream());
			
			Installation installation = decafConfig.getInstallation();
			
			System.out.println("Setting <EditorConfig><Setup><InstallRoot> to " + decaf_demo_dir.getAbsolutePath());
			installation.setInstallRoot(decaf_demo_dir.getAbsolutePath());

			String compileCommand = decaf_demo_dir.extend("bin/jikes").getAbsolutePath();
			System.out.println("Setting <EditorConfig><Setup><CompileCommand> to " + compileCommand);
			installation.setCompileCommand(compileCommand);
			
			System.out.println("Writing config file...");
			decafConfig.marshal(configFile.getOutputStream());
			System.out.println("The Decaf editor has been successfully configured.");
			
			
			
			
			
			//  uMail -------------------
			
			
			
			SuperFile uMail_conf = new SuperFile(decaf_demo_dir.extend("umail/uMail.conf"));
			System.out.println("\n\nLooking for uMail configuration from " + uMail_conf.getAbsolutePath());
			
			ConfigFile uMailConfig = new ConfigFile(uMail_conf);
			if (!uMail_conf.exists())
			{
				SuperFile uMail = new SuperFile(decaf_demo_dir.extend("umail/uMail.exe"));
				if (uMail.exists())
				{
					System.out.println("I can't find the uMail configuration file " + uMail_conf.getAbsolutePath() + ", ");
					System.out.println("so I'm creating a new one.");
				}
				else
				{
					System.out.println("I can't find uMail at all.  It should be in " + uMail.getAbsolutePath() + ".");
					System.out.println("I'm giving up.  Please contact the author at bitwise@cablespeed.com.");
					System.exit(1);
				}
			}
			
			System.out.println("Setting decaf_demo_dir to " + decaf_demo_dir.getAbsolutePath());
			uMailConfig.setProperty("decaf_demo_dir", decaf_demo_dir.getAbsolutePath());

			String runtimeJar;
			while (true)
			{
				String defaultRuntimeJar = javaHome + "\\lib\\rt.jar";			
				System.out.println("\nPlease enter the location of the java runtime jar rt.jar:");
				System.out.print("    [" + defaultRuntimeJar + "]: ");
				runtimeJar = in.readLine();
				if (runtimeJar.length() == 0)
				{
					runtimeJar = defaultRuntimeJar;
				}
				
				if ((new SuperFile(runtimeJar)).exists())
				{
					System.out.println("Java runtime library located at " + runtimeJar);
					break;
				}
				
				System.out.println("I'm sorry, I can't find the file " + runtimeJar);
			}
			
			StringBuffer uMailClasspath = new StringBuffer();
			
			for (int i = 0; i < classpath.length; i++)
			{
				uMailClasspath.append(decaf_demo_dir.extend(classpath[i]).getAbsolutePath());
				uMailClasspath.append(";");
			}
			
			uMailClasspath.append(runtimeJar);
			uMailClasspath.append(";");
			
			SuperFile DecafLaunch = new SuperFile("./DecafLaunch.bat");
			StringBuffer bat = new StringBuffer();
			bat.append("@echo off\r\n");
			bat.append("cd umail\r\n");
			bat.append("java -classpath ");
			bat.append(uMailClasspath.toString());
			bat.append(" com.bitwise.decaf.editor.DecafEditor\r\n");
			bat.append("cd ..\r\n");
			DecafLaunch.write(bat.toString());
			
			System.out.println("Setting uMail classpath to\n " + uMailClasspath.toString() + "\n");
			uMailConfig.setProperty("classpath", uMailClasspath.toString());

			String jvmDllLocation = javaHome + "\\bin\\client\\jvm.dll";
			String newJvmDllLocation;
			
			while (true)
			{
				System.out.println("Please enter the location of the library jvm.dll");
				System.out.print("    [" + jvmDllLocation + "]: ");
				newJvmDllLocation = in.readLine();
				
				if (newJvmDllLocation.length() == 0)
				{
					newJvmDllLocation = jvmDllLocation;
				}
				
				if ((new SuperFile(newJvmDllLocation)).exists())
				{
					System.out.println("Setting the location of jvm.dll to " + newJvmDllLocation);
					uMailConfig.setProperty("jvm_dll", newJvmDllLocation);
					break;
				}
				
				System.out.println("I'm sorry, I can't find the file " + newJvmDllLocation + ".");
			}
			
			System.out.println("Storing new configuration to " + uMail_conf.getAbsolutePath());
			uMailConfig.store();
			
			System.out.println("uMail installation successfully completed.");
			System.out.println("\n\nPlease begin your tour of Decaf by running uMail.exe in the umail directory.");
		}		
		catch (Exception e)
		{
			System.out.println("I'm sorry, a " + e.getClass().getName() + " has occurred during installation.");
			System.out.println("Please contact the author at bitwise@cablespeed.com for help.\n\n");
			e.printStackTrace(System.err);
		}
	}
	
	static class ConfigFile extends Properties
	{
		protected SuperFile file;
		
		public ConfigFile(SuperFile file)
			throws IOException
		{
			super();
			
			this.file = file;
			
			if (this.exists())
			{
				try
				{
					super.load(file.getInputStream());
				}
				catch (FileNotFoundException checkedAlready)
				{
				}
			}
		}
		
		public boolean exists()
		{
			return this.file.exists();
		}
		
		public void store()
			throws FileNotFoundException, IOException
		{
			String key;
			String value;
			
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(this.file.getOutputStream()));
			
			Enumeration properties = super.propertyNames();
			while (properties.hasMoreElements())
			{
				key = (String)properties.nextElement();
				value = super.getProperty(key);
				
				out.write(key + "=" + value);
				out.newLine();
			}
			
			out.flush();
			out.close();
		}
	}
}