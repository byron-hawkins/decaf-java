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

public class Comparison extends GrammarMask
{
	static final long serialVersionUID = -8650841629870387909L;

	protected static Vector phraseGrammars;
	protected static Vector sentenceGrammars;
	
	static
	{
		phraseGrammars = new Vector();
		phraseGrammars.add(new NewComparison());
	}
	
	protected Data data;
	
	public Comparison()
	{
		// for reference purposes only!
		super();
	}
	
	public Comparison(Grammar handle)
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
		super.addFormComponent(new OperatorBox(OperatorBox.comparisonModel, null, handle), new FormConstraints(10,3));
		super.addFormComponent(new PhraseBox(null, this.data.right), new FormConstraints(10,5));
	}
	
	public Collection phraseGrammars()
	{
		return phraseGrammars;
	}
	
	public Collection sentenceGrammars()
	{
		return (new Vector());
	}
	
	public void apply()
	{
		super.apply(this.data);
	}
	
	static class NewComparison extends ActionBase 
	{
		public NewComparison()
		{
			super();
			
			super.putValue(Action.ACCELERATOR_KEY, "c");
			super.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_C));
			super.putValue(Action.NAME, "Comparison");
			super.putValue(Action.SHORT_DESCRIPTION, "Create a comparison expression");
		}
		
		public void go(ActionEvent event)
		{
			Phrase phrase = PhraseBox.getCurrentContext();
			MaskEditor editor = new MaskEditor(new Comparison(phrase));
			editor.assemble();
			
			editor.start();
		}
	}
	
	static class Officer extends TypeOfficer
	{
		protected Type type; // legacy (!) -- required to deserialize existing cup files :-p
		
		public Officer()
		{
			super();
		}
		
		public void setData(Grammar data)
		{
			super.setData(data);
			
			GComparison comparison = (GComparison)super.data.getContent();
			((Phrase)comparison.left).patrol(new Officer.Deputy(this));
			((Phrase)comparison.right).patrol(new Officer.Deputy(this));

			call();
		}
		
		public void call()
		{
			GComparison content = (GComparison)super.data.getContent();
			((Phrase)content.right).setExpectedType(((Phrase)content.left).getType());
		}
		
		public Type calculateType()
		{
			return Utils.BOOLEAN;
		}
	}

	static class Data extends GComparison implements Grammar.Content
	{
		protected Officer officer;
		
		public Data(Grammar handle)
		{
			super(new Phrase(handle), UNKNOWN, new Phrase(handle));
			
			this.officer = new Officer();
			handle.setData(this);
			handle.patrol(this.officer);
		}

		public MaskEditor edit(Grammar handle)
		{
			if (handle instanceof Sentence)
			{
				throw (new RuntimeException(getClass().getName() + ".edit(Grammar): attempt to edit this as a Sentence!"));
			}

			MaskEditor editor = new MaskEditor(new Comparison(handle));
			editor.assemble();
			editor.start();
			
			return editor;
		}

		public JComponent getComponent(Grammar handle)
		{
			Utils.RenderPanel panel = new Utils.RenderPanel();
			panel.add(((Phrase)super.left).render());
			panel.add(new Utils.RenderLabel(OperatorBox.operatorCache.get(this).toString()));
			panel.add(((Phrase)super.right).render());
			
			return panel;
		}
		
		public Type getType()
		{
			return this.officer.getType();
		}
		
		public GConstruct getSource()
		{
			return this;
		}
		
		public void setHandle(Grammar handle)
		{
			// need to remove this.officer from current duty, wherever that is
			handle.patrol(this.officer);
			((Phrase)super.left).setParent(handle);
			((Phrase)super.right).setParent(handle);
		}
	}
}

