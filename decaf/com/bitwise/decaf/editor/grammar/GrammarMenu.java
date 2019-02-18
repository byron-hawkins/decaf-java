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
 
package com.bitwise.decaf.editor.grammar;

import java.io.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.text.*;

import org.hs.util.file.*;
import org.hs.generator.*;

public class GrammarMenu extends JPopupMenu
{
	public static final String SUBMENU_KEY = "submenu";
	public static final String LIAISON = "menu liaison";

	public static final int SENTENCE_MENU_ID = 0;
	public static final int PHRASE_MENU_ID = 1;
	
	protected TreeMap submenus;
	protected Vector menuItems;
	
	protected int id;

	public GrammarMenu(int id)
	{
		super();

		this.id = id;

		menuItems = new Vector();
		submenus = new TreeMap();
	}
	
	public void addGrammar(Action grammarAction)
	{
		String submenuName = (String)grammarAction.getValue(SUBMENU_KEY);
		if (submenuName == null)
		{
			menuItems.add(grammarAction);
		}
		else
		{
			JMenu submenu = (JMenu)submenus.get(submenuName);
			if (submenu == null)
			{
				submenu = new JMenu(submenuName);
				this.submenus.put(submenuName, submenu);
			}
			GrammarMenuItem menuItem = new GrammarMenuItem(grammarAction, this);
			submenu.add(menuItem);
		}
	}
	
	public void close()
	{
		Iterator iterator = submenus.values().iterator();
		while (iterator.hasNext())
		{
			super.add((JMenu)iterator.next());
		}
		if (submenus.values().size() > 0)
		{
			super.addSeparator();
		}
		Action action;
		iterator = this.menuItems.iterator();
		GrammarMenuItem menuItem;
		while (iterator.hasNext())
		{
			action = (Action)iterator.next();
			menuItem = new GrammarMenuItem(action, this);
			super.add(menuItem);
		}
		super.pack();
	}
	
	public static class GrammarMenuItem extends JMenuItem
	{
		protected GrammarMenu owner;
		
		public GrammarMenuItem(Action action, GrammarMenu owner)
		{
			super((String)action.getValue(Action.NAME));
			
			this.owner = owner;
			
			super.addActionListener(action);
		}

		public int getId()
		{
			return this.owner.id;
		}
		
	
		public Object writeReplace()
			throws java.io.ObjectStreamException
		{
			throw (new com.bitwise.decaf.editor.Utils.SerialException(getClass()));
		}
	}
	
	public Object writeReplace()
		throws java.io.ObjectStreamException
	{
		throw (new com.bitwise.decaf.editor.Utils.SerialException(getClass()));
	}
}