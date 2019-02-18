package org.hs.util.file;

import java.io.*;
import java.util.*;
import gnu.regexp.*;

public class CodeStamp
{
	protected static String stamp;
	
	public static void main(String[] args)
	{
		try
		{
			SuperFile stampFile = new SuperFile("./stamp.txt");
			stamp = stampFile.read();
			
			SuperFile directory;
			
			if (args.length == 0)
			{
				directory = new SuperFile(".");
			}
			else
			{
				directory = new SuperFile(args[0]);
			}
			
			stampAll(directory);
		}
		catch (Exception e)
		{
			e.printStackTrace(System.err);
			//System.err.println(e);
		}
	}

	protected static void stampAll(SuperFile directory)
		throws Exception
	{
		SuperFile[] list = directory.list(".*");
		int i = 0;
		
		try
		{
			for (i = 0; i < list.length; i++)
			{
				if (list[i].isDirectory())
				{
					stampAll(list[i]);
				}
				else if (list[i].getName().endsWith(".java"))
				{
					stamp(list[i]);
				}
			}
		}
		catch (Exception e)
		{
			System.err.println("Error processing " + list[i].getAbsolutePath());
			throw e;
		}
	}
	
	protected static void stamp(SuperFile source)
		throws Exception
	{
		String contents = source.read();
		String fromPackageDecl = contents.substring(contents.indexOf("package"));
		String stamped = stamp + fromPackageDecl;
		source.write(stamped);
	}
}