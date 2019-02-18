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
 
package com.bitwise.decaf.editor;

import java.util.*;
import java.util.prefs.*;
import java.text.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.text.*;

import org.hs.util.file.*;

import com.bitwise.decaf.editor.config.*;

public class test
{
	//protected SuperFile log;
	
	public static void main(String[] args)
	{
		(new test()).go();
	}
	
  protected static final DateFormat s_format = new SimpleDateFormat("MM/DD/yyyy hh:mm");

	protected void go()
	{
		try
		{
			Preferences p = Preferences.userRoot();
			
			p = p.parent();
			
			String[] keys = p.keys();
			System.err.println("found " + keys.length + " keys");
			for (int i = 0; i < keys.length; i++)
			{
				System.err.println(i + ") " + keys[i]);
			}
			
			System.err.println(p.nodeExists("Software"));	// /JavaSoft/Java Runtime Environment/1.4.1
			
			
			
			//System.err.println(System.getProperty("java.util.prefs.PreferencesFactory"));
			//System.err.println(Boolean.getBoolean("true"));
		}
		catch (Exception e)
		{
			DecafEditor.log(e);
		}
	}
	
	protected void tryCalender()
		throws Exception
	{
		GregorianCalendar c = new GregorianCalendar();
		System.err.println("The year is " + c.get(Calendar.YEAR));
		System.err.println("The day is " + c.get(Calendar.DAY_OF_MONTH));
		s_format.format(c.getTime());
	}
	/*
	protected void config()
		throws Exception
	{
		SuperFile configFile = new SuperFile("./config.xml");
		
		System.err.println(DecafConfig.unmarshal(configFile.getInputStream()).getClass().getName());
	}
	*/
	protected void compatible()
	{
		System.err.println(short.class.isAssignableFrom(int.class));
	}
}