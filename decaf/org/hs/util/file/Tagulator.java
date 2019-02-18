package org.hs.util.file;

import java.io.*;
import java.util.*;
import gnu.regexp.*;

public class Tagulator
{
	public static void main(String[] args)
	{
		try
		{
			
			SuperFile directory;
			
			if (args.length == 0)
			{
				directory = new SuperFile(".");
			}
			else
			{
				directory = new SuperFile(args[0]);
			}
			
			SuperFile[] list = directory.list(".*\\.ht");
			for (int i = 0; i < list.length; i++)
			{
				tagulate(list[i]);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace(System.err);
			//System.err.println(e);
		}
	}
	
	protected static void tagulate(SuperFile source)
		throws Exception
	{
		String contents = source.read();
		String parsed = contents.replaceAll("&lt;\\[", "<");
		parsed = parsed.replaceAll("\\]&gt;", ">");
		parsed = parsed + "<p>&nbsp;<p>&nbsp;<p>&nbsp;<p>&nbsp;<p>&nbsp;<p>&nbsp;<p>&nbsp;<p>";
		SuperFile output = new SuperFile(source.getAbsolutePath() + "ml");
		output.write(parsed);
	}
}