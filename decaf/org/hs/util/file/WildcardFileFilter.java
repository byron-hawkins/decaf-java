package org.hs.util.file;

import java.util.*;
import java.io.IOException;
import java.io.File;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;

import gnu.regexp.*;

import org.hs.jfc.*;

public class WildcardFileFilter extends FileFilter
{
	protected Vector filters;
	protected String description;

	public WildcardFileFilter(String description)
		throws IOException
	{
		super();
		
		this.description = description;
		this.filters = new Vector();
	}
	
	public void addFilter(String filter)
		throws IOException
	{
		try
		{
			char next;
			StringBuffer reFilter = new StringBuffer();
			for (int i = 0; i < filter.length(); i++)
			{
				next = filter.charAt(i);
				switch (next)
				{
					case '.':
						reFilter.append("\\.");
						break;
					case '*':
						reFilter.append(".*");
						break;
					default: 
						reFilter.append(next);
				}
			}
			this.filters.add(new RE(reFilter.toString()));
		}
		catch (REException e)
		{
			throw (new IOException("Illegal regular expression: " + filter));
		}
	}
	
	public boolean accept(File f)
	{
		if (f.isDirectory())
		{
			return true;
		}
		
		RE filter;
		
		for (int i = 0; i < this.filters.size(); i++)
		{
			filter = (RE)this.filters.elementAt(i);
			if (filter.isMatch(f.getName()))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public String getDescription()
	{
		return this.description;
	}
}