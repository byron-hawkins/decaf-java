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

import java.util.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.text.*;

import org.hs.jfc.*;
import org.hs.generator.*;

import com.bitwise.decaf.editor.*;

// need a static cache of these, 
// so that if an editor for a particular node (by id) is open,
// I'll be able to include it in an edit path

public class MaskEditor extends JDialog implements WindowFocusListener
{
	protected static final String TITLE = "Decaf - Code Editor";
	
	protected JPanel body;
	protected GrammarMask mask;
	protected JPanel buttons;
	
	JButton done;
	
	public MaskEditor(GrammarMask mask)
	{
		super(DecafEditor.frame);
		
		super.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		super.setResizable(false);
		super.addWindowFocusListener(this);
		
		this.mask = mask;
	}
	
	public void assemble()
	{
		super.setTitle(TITLE);

		this.body = new JPanel();
		this.body.setLayout(new BoxLayout(this.body, BoxLayout.Y_AXIS));

		this.body.add(this.mask);
		
		this.buttons = new JPanel();
		this.buttons.add(this.done = new JButton(new Ok()));
		this.body.add(buttons);
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
	
	public GrammarComponent getGrammarComponent(int index)
	{
		// what about labels?
		return (GrammarComponent)this.mask.getComponent(index);
	}
	
	public void windowGainedFocus(WindowEvent e)
	{
		this.mask.focus(true);
	}
	
	public void windowLostFocus(WindowEvent e)
	{
		this.mask.focus(false);
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
			MaskEditor.this.mask.apply();
			MaskEditor.super.dispose();
		}
	} 
	
	public Object writeReplace()
		throws java.io.ObjectStreamException
	{
		throw (new Utils.SerialException(getClass()));
	}
}
