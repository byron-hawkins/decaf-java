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
import javax.swing.tree.*;

import com.bitwise.decaf.editor.*;
import com.bitwise.decaf.editor.config.*;
import org.hs.generator.*;
import org.hs.jfc.*;
import org.hs.generator.*;

// This class serves the declaration of local variables
// Variables can only be referenced by hot drop

public class LocalVariable extends GrammarMask 
{
	static final long serialVersionUID = -2375348876753804523L;

	protected static Vector sentenceGrammars;
	
	static
	{
		sentenceGrammars = new Vector();
		sentenceGrammars.add(new NewAction());
	}
	
	public static Sentence wrap(DVariable variable, CodeNode parent)
	{
		try
		{
			Sentence wrap = new Sentence(parent);
			
			variable.setLocal(true);
			wrap.setData(new Data(variable));
			
			return wrap;
		}
		catch (ClassNotFoundException checkedAlready) { return null; }
	}
	
	protected Data data;
	protected DTextField nameField;
	protected DTypeBox typeBox;
	
	public LocalVariable()
	{
		// for reference purposes only!
		super();
	}
	
	public LocalVariable(Sentence handle)
	{
		super(handle);
		
		Object content = handle.getContent();
		if ((content != null) && (content instanceof Data))
		{
			this.data = (Data)content;
			super.display(this.data);
		}
		else
		{
			this.data = new Data(handle);
		}
		
		this.nameField = new DTextField();
		nameField.setRepresents(GConstruct.getField(GVariable.class, "name"));
		nameField.setLabelText("Name");
		super.addFormComponent(nameField, new FormConstraints(10,0));
		
		this.typeBox = new DTypeBox();
		typeBox.setRepresents(GConstruct.getField(GVariable.class, "type"));
		typeBox.setLabelText("Type");
		super.addFormComponent(typeBox, new FormConstraints(20,0));
		
		this.typeBox.display(this.data);
		this.nameField.display(this.data);
	}
	
	public Collection phraseGrammars()
	{
		return (new Vector());
	}
	
	public Collection sentenceGrammars()
	{
		return sentenceGrammars;
	}
	
	public void apply()
	{
		this.data.setType(this.typeBox.getData());
		this.nameField.apply(this.data);
		super.apply(this.data);
	}
	
	static class NewAction extends ActionBase 
	{
		public NewAction()
		{
			super();
			
			super.putValue(Action.ACCELERATOR_KEY, "v");
			super.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_V));
			super.putValue(Action.NAME, "Declare a Variable");
			super.putValue(Action.SHORT_DESCRIPTION, "Declare a local variable.");
		}
		
		public void go(ActionEvent event)
		{
			Sentence sentence = ParagraphBox.getCurrentContext();
			MaskEditor editor = new MaskEditor(new LocalVariable(sentence));
			editor.assemble();
			
			editor.start();
		}
	}

	public static class Data extends DVariable implements Grammar.Content
	{
		public Data(Grammar handle)
		{
			super(true);
			
			handle.setData(this);
		}
		
		public Data(DVariable content)
			throws ClassNotFoundException
		{
			super(content);
		}
		
		public MaskEditor edit(Grammar handle)
		{
			if (handle instanceof Phrase)
			{
				throw (new RuntimeException(getClass().getName() + ".edit(Grammar): attempt to handle this as a Phrase"));
			}
			
			MaskEditor editor = new MaskEditor(new LocalVariable((Sentence)handle));
			editor.assemble();
			editor.start();
			
			return editor;
		}
		
		public JComponent getComponent(Grammar handle)
		{
			if (handle instanceof Phrase)
			{
				return super.getComponent(handle);
			}
			
			JPanel panel = new Utils.RenderPanel();
			if (super.getType() == null)
			{
				panel.add(new JLabel("<New Local Variable>"));
			}
			else
			{
				panel.add(new JLabel(super.getType().getDisplayName() + " " + super.getName()));
			}
			return panel;
		}
		
		public GConstruct getSource()
		{
			return this;
		}
		
		public void setHandle(Grammar handle)
		{
			// this is a terminus
		}
	}
}

