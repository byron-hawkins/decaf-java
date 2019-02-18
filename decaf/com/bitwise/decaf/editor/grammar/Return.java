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

public class Return extends GrammarMask 
{
	static final long serialVersionUID = -5335027301170292820L;

	protected static Vector phraseGrammars;
	protected static Vector sentenceGrammars;
	
	static
	{
		sentenceGrammars = new Utils.SerialVector();
		sentenceGrammars.add(new NewAction());
	}
	
	protected Data data;
	
	public Return()
	{
		// for reference purposes only!
		super();
	}
	
	public Return(Grammar handle)
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
		
		super.addFormComponent(new PhraseBox("Return", this.data.value), new FormConstraints(20,0));
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
		super.apply(this.data);
	}
	
	static class ValueOfficer extends EnforcementOfficer implements Officer.Foreign
	{
		public void call()
		{
			Phrase data = (Phrase)super.data;
			if (data.getEnclosingMethod().getReturnType().isAssignableFrom(data.getType()))
			{
				super.noViolation();
			}
			else
			{
				super.violation("The value of this return statement is of type " + data.getType().getDisplayName() + ", which does not match the method's return type of " + data.getEnclosingMethod().getReturnType().getDisplayName());
			}
		}
	}
	
	static class NewAction extends ActionBase 
	{
		public NewAction()
		{
			super();
			
			super.putValue(Action.ACCELERATOR_KEY, "r");
			super.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_R));
			super.putValue(Action.NAME, "Return");
			super.putValue(Action.SHORT_DESCRIPTION, "Create a return statement");
		}
		
		public Object getValue(String key)
		{
			return super.getValue(key);
		}
		
		public void go(ActionEvent event)
		{
			Grammar grammar = ParagraphBox.getCurrentContext();
			MaskEditor editor = new MaskEditor(new Return(grammar));
			editor.assemble();
			editor.start();
		}
	}
	
	static class Data extends GReturn implements Grammar.Content
	{
		protected ValueOfficer officer;
		
		public Data(Grammar handle)
		{
			super(new Phrase(handle));

			this.officer = new ValueOfficer();
			handle.setData(this);
			((Phrase)super.value).patrol(officer);
		}
		
		public MaskEditor edit(Grammar handle)
		{
			MaskEditor editor = new MaskEditor(new Return(handle));
			editor.assemble();
			editor.start();
			
			return editor;
		}
		
		public GConstruct getSource()
		{
			//super.body = this.body.getData();
			return this;
		}

		public JComponent getComponent(Grammar handle)
		{
			Utils.RenderPanel panel = new Utils.RenderPanel();

			panel.add(new Utils.RenderLabel("Return ("));
			panel.add(((Phrase)super.value).render());
			panel.add(new Utils.RenderLabel(")"));
		
			return panel;
		}
		
		public void setHandle(Grammar handle)
		{
			((CodeNode)super.value).setParent(handle);
		}
		
		public Type getType()
		{
			return Utils.noType;
		}
	}
}

