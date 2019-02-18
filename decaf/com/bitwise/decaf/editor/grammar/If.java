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
import com.bitwise.decaf.editor.config.FormConstraints;
import org.hs.generator.*;
import org.hs.jfc.*;
import org.hs.generator.*;

public class If extends GrammarMask 
{
	static final long serialVersionUID = -1080019895827451821L;

	protected static Vector phraseGrammars;
	protected static Vector sentenceGrammars;
	
	static
	{
		phraseGrammars = new Utils.SerialVector();
		phraseGrammars.add(new NewAction());

		sentenceGrammars = new Utils.SerialVector();
		sentenceGrammars.add(new NewAction());
	}
	
	protected GIf data;
	
	public If()
	{
		// for reference purposes only!
		super();
	}
	
	public If(Grammar handle)
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
		
		PhraseBox condition = new PhraseBox("If", this.data.condition);
		condition.setRequiredType(Utils.BOOLEAN);
		super.addFormComponent(condition, new FormConstraints(10,0));

		if (handle instanceof Sentence)
		{
			super.addFormComponent(new ParagraphBox("Then", this.data.thenClause), new FormConstraints(20,0,FormLayout.LABEL_ON_TOP));
			super.addFormComponent(new ParagraphBox("Else", this.data.elseClause), new FormConstraints(30,0,FormLayout.LABEL_ON_TOP));
		}
		else
		{
			super.addFormComponent(new PhraseBox("Then", this.data.thenClause), new FormConstraints(20,0));
			super.addFormComponent(new PhraseBox("Else", this.data.elseClause), new FormConstraints(30,0));
		}
	}
	
	public Collection phraseGrammars()
	{
		return phraseGrammars;
	}
	
	public Collection sentenceGrammars()
	{
		return sentenceGrammars;
	}
	
	public void apply()
	{
		super.apply(this.data);
	}
	
	static class ConditionOfficer extends EnforcementOfficer implements Officer.Foreign
	{
		public void call()
		{
			Phrase data = (Phrase)super.data;
			if (data.getType().equals(Utils.BOOLEAN))
			{
				super.noViolation();
			}
			else
			{
				super.violation("The condition of an if statement needs to be of type boolean (primitive only).  The " + ((Phrase)super.data).getType().getDisplayName() + " here can't be used.");
			}
		}
	}
	
	static class NewAction extends ActionBase 
	{
		public NewAction()
		{
			super();
			
			super.putValue(Action.ACCELERATOR_KEY, "i");
			super.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_I));
			super.putValue(Action.NAME, "Conditional Section");
			super.putValue(Action.SHORT_DESCRIPTION, "Create an if-then-else statement");
		}
		
		public Object getValue(String key)
		{
			return super.getValue(key);
		}
		
		public void go(ActionEvent event)
		{
			int menuId = ((GrammarMenu.GrammarMenuItem)event.getSource()).getId();
			Grammar grammar;
			if (menuId == GrammarMenu.SENTENCE_MENU_ID)
			{
				grammar = ParagraphBox.getCurrentContext();
			}
			else if (menuId == GrammarMenu.PHRASE_MENU_ID)
			{
				grammar = PhraseBox.getCurrentContext();
			}
			else
			{
				return;
			}
			
			MaskEditor editor = new MaskEditor(new If(grammar));
			editor.assemble();
			editor.start();
		}
	}
	
	static class Data extends GIf implements Grammar.Content
	{
		protected ConditionOfficer officer;
		protected boolean asSentence;
		
		public Data(Grammar handle)
		{
			super(new Phrase(handle), 
				  (handle instanceof Sentence)?((CodeNode)new Paragraph(handle)):((CodeNode)new Phrase(handle)), 
				  (handle instanceof Sentence)?((CodeNode)new Paragraph(handle)):((CodeNode)new Phrase(handle)));
			
			this.asSentence = (handle instanceof Sentence);
			
			((Phrase)super.condition).patrol(this.officer = new ConditionOfficer());
			handle.setData(this);
		}
		
		public MaskEditor edit(Grammar handle)
		{
			MaskEditor editor = new MaskEditor(new If(handle));
			editor.assemble();
			editor.start();
			
			return editor;
		}
		
		public GConstruct getSource()
		{
			return this;
		}

		public JComponent getComponent(Grammar handle)
		{
			Utils.RenderPanel panel = new Utils.RenderPanel();

			if (this.asSentence)
			{
				if ((super.description == null) || (super.description.length() == 0))
				{
					panel.add(new Utils.RenderLabel("If ("));
					panel.add(((Phrase)super.condition).render());
					panel.add(new Utils.RenderLabel(")"));
				}
				else
				{
					panel.add(new Utils.RenderLabel(super.description));
				}
			}
			else
			{
				panel.add(((Phrase)super.condition).render());
				panel.add(new Utils.RenderLabel("?"));
				panel.add(((Phrase)super.thenClause).render());
				panel.add(new Utils.RenderLabel(":"));
				panel.add(((Phrase)super.elseClause).render());
			}
			
			return panel;
		}
		
		public void setHandle(Grammar handle)
		{
			((CodeNode)super.condition).setParent(handle);
			((CodeNode)super.thenClause).setParent(handle);
			((CodeNode)super.elseClause).setParent(handle);
		}
		
		public Type getType()
		{
			if (this.asSentence)
			{
				return Utils.noType;
			}
			else
			{
				return ((Phrase)super.thenClause).getType();
			}
		}
	}
}

// conflicting purposes for dbl-click on PhraseBox: edit literal and edit contents
// edit literal needs to take focus immediately, and save results on mask close