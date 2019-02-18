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
import org.hs.generator.*;

public class VariableEditor extends JDialog
{
	protected static final String TITLE = "Decaf - Variable Editor";
	
	protected JPanel body;
	protected VariableMask mask;
	protected JPanel buttons;
	
	JButton done;
	
	protected DVariable data;
	
	VariableEditor(String destination)
	{
		super(DecafEditor.frame);

		super.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		super.setResizable(false);
		super.setTitle(TITLE);
		
		this.mask = new VariableMask(destination);
	}
	
	public void assemble(DVariable data)
	{
		this.data = data;

		this.body = new JPanel();
		this.body.setLayout(new BoxLayout(this.body, BoxLayout.Y_AXIS));
		
		this.mask.assemble();
		this.mask.display(data);
		this.body.add(this.mask);
		
		this.buttons = new JPanel();
		this.buttons.add(this.done = new JButton(new Ok()));
		this.body.add(this.buttons);
	}
	
	public void display(GConstruct code)
	{
		this.mask.display(code);
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
			VariableEditor.this.mask.apply(VariableEditor.this.data);
			VariableEditor.super.dispose();
		}
	} 
	
	public Object writeReplace()
		throws java.io.ObjectStreamException
	{
		throw (new Utils.SerialException(getClass()));
	}
}