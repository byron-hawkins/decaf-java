package org.hs.util.file;

import java.io.*;
import java.util.*;

//import org.hs.util.unittest.*;

public class Script extends SuperFile
{
	// no decent way to default this.  Too bad it can't be done in java.
	private static File EXECUTION_HARNESS = new File("/opt/flightconnect/bin/do");

	private static final String USER_SWITCH = " -u ";
	private static final String PASSWORD_SWITCH = " -p ";

	private Vector parameters;
	private Vector environment;

	private Script(String name)
	{
		super(name);
		parameters = new Vector();
		environment = new Vector();
	}

	public static Script get(String name)
	{
		Script script = new Script(name);
		if (!script.isFile())
		{
			return null;
		}
		return script;
	}

	public static boolean setHarnessLocation(File path)
	{
		if (path.isDirectory())
		{
			File test = new File(path, "do");
			if (test.isFile())
			{
				EXECUTION_HARNESS = test;
				return true;
			}
		}
		return false;
	}

	public SuperProcess execute()
		throws IOException
	{
		return execute(null, null);
	}

	public  SuperProcess execute(String asUser, String password)
		throws IOException
	{
		String[] runParameters = null;
		if (parameters.size() > 0)
		{
			runParameters = new String[parameters.size() + 1];
			runParameters[0] = getCommand(asUser, password);
			for (int i = 1; i < runParameters.length; i++)
			{
				runParameters[i] = (String)parameters.elementAt(i-1);
			}
		}

		String[] runEnvironment = null;
		if (environment.size() > 0)
		{
			runEnvironment = new String[environment.size()];
			environment.copyInto(runEnvironment);
		}

		Process result;
		Runtime runtime = Runtime.getRuntime();
		if (runParameters == null)
		{
			if (runEnvironment == null)
			{
				result = runtime.exec(getCommand(asUser, password));
			}
			else
			{
				result = runtime.exec(getCommand(asUser, password), runEnvironment);
			}
		}
		else
		{
			result = runtime.exec(runParameters, runEnvironment);
		}
		return (new SuperProcess(result));
	}

	public void addParameter(String parameter)
	{
		this.parameters.addElement(parameter);
	}

	public void addEnvironment(String environment)
	{
		this.environment.addElement(environment);
	}

	private String getCommand(String asUser, String password)
	{
		String command;
		if (asUser == null)
		{
			command = EXECUTION_HARNESS.getAbsolutePath() + " " + super.getAbsolutePath();
		}
		else
		{
			command = EXECUTION_HARNESS.getAbsolutePath() + " " + 
				USER_SWITCH + asUser + 
				PASSWORD_SWITCH + password + 
				super.getAbsolutePath();
		}
		return command;
	}
	/*
	public class UnitTest extends BaseTestCase
	{
		public UnitTest(String name)
		{
			super(name);
		}

		public void test()
		{
			try
			{
				// check the static get method
				Script ls = Script.get("./ls.sh");
				assert("Script ./ls.sh is null: please create an executable script name ls.sh in the current directory, containing the single command 'ls'.", (ls != null));

				// make sure we can execute it
				SuperProcess p = ls.execute();
				assert("Null process returned from execution of ./ls.sh", (p != null));

				// make sure ls exits with 0
				int exitValue = p.exitValue();
				assert("Execution of ./ls.sh returned with exit value " + exitValue, (exitValue == 0));

				// Check that there is output from ls, and that it contains the listing for itself
				String listing = p.getInput();

				if (listing.length() == 0)
				{
					String error = p.getErrorInput();
					if (error.length() == 0)
					{
						assert("No output returned from ./ls.sh", false);
					}
					else
					{
						assert("No output was returned from ./ls.sh, but the error stream contained: " + error, false);
					}
				}
				assert("Script ./ls.sh not listed in its execution: " + listing, (listing.indexOf("ls") != -1));
			}
			catch (Exception e)
			{
				assertException(e);
			}
		}
	}
	*/

	public Script()
	{
		super("./ls.sh");
		//Tracer.out(Tracer.WARN, "Instantiating " + getClass().getName() + " with unit test constructor.");
	}
}
