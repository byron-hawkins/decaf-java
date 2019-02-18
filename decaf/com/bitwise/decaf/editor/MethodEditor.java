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

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.text.*;

import org.hs.jfc.*;

// need a static cache of these, 
// so that if an editor for a particular method is open,
// I'll be able to include it in an edit path

public class MethodEditor extends JDialog 
{
	protected static final String NAMED_TITLE = "Decaf - Method Editor | ";
	protected static final String TITLE = "Decaf - Method Editor";

	protected static Editors editors;
	
	public static void openingEditor(MethodEditor editor)
	{
		editors.openingEditor(editor);
	}
	
	public static MethodEditor getEditorFor(DMethod data)
	{
		return editors.getEditorFor(data);
	}
	
	static
	{
		editors = new Editors();
	}
	
	protected JPanel body;
	protected MethodMask mask;
	protected JPanel buttons;
	
	JButton done;
	
	protected DMethod data;
	protected transient Conclusion conclusion;
	
	public MethodEditor()
	{
		super(DecafEditor.frame);
		
		super.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		super.setResizable(false);
		super.setTitle(TITLE);
	}
	
	public void assemble(DMethod data)
	{
		this.data = data;

		this.body = new JPanel();
		this.body.setLayout(new BoxLayout(this.body, BoxLayout.Y_AXIS));
				
		this.mask = new MethodMask(this.data);
		this.mask.assemble();
		this.mask.addNameListener(new NameListener());
		this.body.add(this.mask);
		
		this.buttons = new JPanel();
		this.buttons.add(this.done = new JButton(new Ok()));
		this.body.add(this.buttons);  
	}
	
	public void start()
	{
		super.getContentPane().add("Center", this.body);

		DecafEditor.hotStation.registerAll(this);
		DecafEditor.registerWindow(this);

		super.pack();

		DecafEditor.realEstateManager.place(this);

		super.show();
	}
	
	public void nameChanged(String newName)
	{
		newName = newName.trim();
		
		if (newName.length() > 0)
		{
			super.setTitle(NAMED_TITLE + newName);
		}
		else
		{
			super.setTitle(TITLE);
		}
	}
	
	class NameListener implements KeyListener
	{
		public void keyPressed(KeyEvent event)
		{
		}
		
		public void keyReleased(KeyEvent event)
		{
			nameChanged(MethodEditor.this.mask.give(String.class, HotComponent.COPY).toString());
		}
		
		public void keyTyped(KeyEvent event)
		{
		}
	}

	class Ok extends ActionBase
	{
		public Ok()
		{
			super();
			
			super.putValue(Action.ACCELERATOR_KEY, "o");
			super.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
			super.putValue(Action.NAME, "Ok");
		}
		
		public void go(ActionEvent event)
		{
			MethodEditor.this.mask.apply(MethodEditor.this.data);
			MethodEditor.super.dispose();
		}
	} 

	static class Editors extends WindowAdapter
	{
		protected TreeMap editors;
		
		public Editors()
		{
			this.editors = new TreeMap();
		}
		
		public void openingEditor(MethodEditor editor)
		{
			this.editors.put(editor.mask.data, editor);
			editor.addWindowListener(this);
		}
		
		public MethodEditor getEditorFor(DMethod data)
		{
			return (MethodEditor)this.editors.get(data);
		}
		
		public void windowClosed(WindowEvent event)
		{
			this.editors.remove(((MethodEditor)event.getSource()).mask.data);
		}
	}

	
	public Object writeReplace()
		throws java.io.ObjectStreamException
	{
		throw (new Utils.SerialException(getClass()));
	}
}
