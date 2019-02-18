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

public class Binary extends GrammarMask
{
	static final long serialVersionUID = 212854734551393306L;

	protected static Vector phraseGrammars;
	protected static Vector sentenceGrammars;
	
	static
	{
		phraseGrammars = new Vector();
		phraseGrammars.add(new NewArithmetic());
		//phraseGrammars.add(new NewBitwise());

		sentenceGrammars = new Vector();
		sentenceGrammars.add(new NewAssignArithmetic());
		//sentenceGrammars.add(new NewAssignBitwise());
	}
	
	protected Data data;
	
	public Binary()
	{
		// for reference purposes only!
		super();
	}
	
	public Binary(Grammar handle)
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

		super.addFormComponent(new PhraseBox(null, this.data.left), new FormConstraints(10,1));
		super.addFormComponent(new PhraseBox(null, this.data.right), new FormConstraints(10,5));
		super.addFormComponent(new OperatorBox((handle instanceof Phrase)?OperatorBox.binaryModel:OperatorBox.binaryAssignModel, null, handle), new FormConstraints(10,3));
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

	static class Officer extends TypeOfficer
	{
		protected Type type;
		
		public Officer()
		{
			super();
			this.type = Utils.noType;
		}
		
		public void setData(Grammar data)
		{
			super.setData(data);
			
			GBinary binary = (GBinary)super.data.getContent();
			((Phrase)binary.left).patrol(new Officer.Deputy(this));
			((Phrase)binary.right).patrol(new Officer.Deputy(this));

			call();
		}
		
		public void call()
		{
			//System.err.println(getClass().getName() + ".call(): <" + super.data + ">");
			
			GBinary binary = (GBinary)super.data.getSource();
			
			Phrase left = (Phrase)binary.left;
			Phrase right = (Phrase)binary.right;
			
			right.setExpectedType(left.getType());
			
			if (left.isEmpty() || right.isEmpty())
			{
				super.violation("I got the blues 'till somebody puts data on the left and right sides o' my operator!");
			}
			else
			{
				Type enforce = left.getType();
				Type patrol = right.getType();
				
				switch (binary.operator)
				{
					case GBinary.SIMPLE:
						checkCompatibility(enforce, patrol, "The left-hand expression is a " + enforce.getDisplayName() + ", which can't be assigned to a " + patrol.getDisplayName() + ".");
						break;
					case GBinary.ADD:
						if (enforce.isAssignableFrom(Type.cache.get(String.class)) || enforce.isPrimitive())
						{
							checkCompatibility(enforce, patrol, "add", "to");
						}
						else
						{
							super.violation("I only know how to add strings and primitive types");
							// provide reference info for "string" and "primitive types"
						}
						break;
					case GBinary.SUBTRACT:
						checkCompatibility(enforce, patrol, "subtract", "from");
						break;
					case GBinary.MULTIPLY:
						checkCompatibility(enforce, patrol, "multiply", "with");
						break;
					case GBinary.DIVIDE:
						checkCompatibility(enforce, patrol, "divide", "with");
						break;
					case GBinary.MODULO:
						checkCompatibility(enforce, patrol, "I don't know how to take the remainder of a " + enforce.getDisplayName() + " divided by a " + patrol.getDisplayName() + ".");
						break;
				}
			}
			
			// if we're patrolling the Phrase `right`, we should call appropriately via violation
			// right.evaluate(this);
		}
		
		private void checkCompatibility(Type enforce, Type patrol, String operation, String preposition)
		{
			checkCompatibility(enforce, patrol, "I can't " + operation + " a " + enforce.getDisplayName() + " " + preposition + " a " + patrol.getDisplayName() + ".");
		}
		
		private void checkCompatibility(Type enforce, Type patrol, String violation)
		{
			if (enforce.isAssignableFrom(patrol))
			{
				this.type = enforce;
				super.noViolation();
			}
			else if (patrol.isAssignableFrom(enforce))
			{
				this.type = enforce;
				((Chameleon)((GBinary)super.data.getSource()).right).setExpectedType(this.type);
				super.noViolation();
			}
			else
			{
				this.type = Utils.noType;
				super.violation(violation); 
			}
		}

		public Type calculateType()
		{
			if (this.type == null)
			{	
				call();
			}
			return this.type;
		}
	}
	
	static class NewArithmetic extends ActionBase 
	{
		public NewArithmetic()
		{
			super();
			
			super.putValue(Action.ACCELERATOR_KEY, "a");
			super.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_A));
			super.putValue(Action.NAME, "Assignment and Arithmetic");
			super.putValue(Action.SHORT_DESCRIPTION, "Create an arithmetic expression");
		}
		
		public void go(ActionEvent event)
		{
			Phrase phrase = PhraseBox.getCurrentContext();
			MaskEditor editor = new MaskEditor(new Binary(phrase));
			editor.assemble();
			
			editor.start();
		}
	}
	
	static class NewAssignArithmetic extends ActionBase 
	{
		public NewAssignArithmetic()
		{
			super();
			
			super.putValue(Action.ACCELERATOR_KEY, "a");
			super.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_A));
			super.putValue(Action.NAME, "Assignment and Arithmetic");
			super.putValue(Action.SHORT_DESCRIPTION, "Create an arithmetic statement");
		}
		
		public void go(ActionEvent event)
		{
			Sentence sentence = ParagraphBox.getCurrentContext();
			MaskEditor editor = new MaskEditor(new Binary(sentence));
			editor.assemble();
			
			editor.start();
		}
	}

	static class Chameleon extends Phrase
	{
		public Chameleon(CodeNode parent)
		{
			super(parent);
		}
		
		public at.dms.kjc.JExpression encodeExpression()
		{
			if ((super.expectedType.equals(Utils.noType)) || (super.content.getType().equals(super.expectedType.typeSource())))
			{
				return super.encodeExpression();
			}
			
			
			GCast cast = new GCast((GExpression)super.content, super.expectedType.typeSource());
			return cast.encodeExpression();
		}
	}

	static class Data extends GBinary implements Grammar.Content 
	{
		protected Officer officer;
		
		public Data(Grammar handle)
		{
			super(new Phrase(handle), GBinary.UNKNOWN, new Chameleon(handle));
			
			this.officer = new Officer();
			handle.setData(this);
			handle.patrol(this.officer);
		}
		
		public void setHandle(Grammar handle)
		{
			handle.patrol(this.officer);
			((Phrase)this.left).setParent(handle);
			((Phrase)this.right).setParent(handle);
		}
		
		public JComponent getComponent(Grammar handle)
		{
			Utils.RenderPanel panel = new Utils.RenderPanel();
			panel.add(((Phrase)super.left).render());
			
			OperatorBox.Operator operator = OperatorBox.operatorCache.get(this);
			panel.add(new Utils.RenderLabel((operator == null)?"?":operator.toString()));
			
			panel.add(((Phrase)super.right).render());
			
			return panel;
		}
		
		public Type getType()
		{
			if (this.officer == null)
			{
				return Utils.noType;
			}
			return this.officer.getType();
		}
		
		public MaskEditor edit(Grammar handle)
		{
			MaskEditor editor = new MaskEditor(new Binary(handle));
			editor.assemble();
			editor.start();
				
			return editor;
		}
		
		public GConstruct getSource()
		{
			return this;
		}
	}
}

