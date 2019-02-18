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

public class Report extends GrammarMask
{
	static final long serialVersionUID = 7866332535632204154L;

	protected static Vector phraseGrammars;
	protected static Vector sentenceGrammars;
	
	static
	{
		sentenceGrammars = new Vector();
		sentenceGrammars.add(new NewReport());
	}
	
	protected Data data;
	
	public Report()
	{
		// for reference purposes only!
		super();
	}
	
	public Report(Grammar handle)
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

		super.addFormComponent(new PhraseBox(null, this.data.message), new FormConstraints(10,1));
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

	static class NewReport extends ActionBase 
	{
		public NewReport()
		{
			super();
			
			super.putValue(Action.ACCELERATOR_KEY, "4");
			super.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_R));
			super.putValue(Action.NAME, "Report to Command Line");
			super.putValue(Action.SHORT_DESCRIPTION, "Report something to the command line");
		}
		
		public void go(ActionEvent event)
		{
			Sentence sentence = ParagraphBox.getCurrentContext();
			MaskEditor editor = new MaskEditor(new Report(sentence));
			editor.assemble();
			
			editor.start();
		}
	}

	static class Data extends GMethodCall implements Grammar.Content 
	{
		protected Grammar message;
		
		public Data(Grammar handle)
		{
			super(createSystemErr(), "println");
			
			this.message = new Phrase(handle);
			
			super.arguments.add(this.message);
			
			handle.setData(this);
		}
		
		protected static GExpression createSystemErr()
		{
			DVariable systemErr = new DVariable(false);
			systemErr.prefix = Utils.getType(System.class).typeSource();
			systemErr.name = "err";
			try
			{
				systemErr.setType((new BinaryVariable(System.class.getField("err"))).getType());
			}
			catch (Exception e)
			{
				DecafEditor.log(e);
			}
			return systemErr;
		}
		
		public void setHandle(Grammar handle)
		{
			this.message.setParent(handle);
		}
		
		public JComponent getComponent(Grammar handle)
		{
			Utils.RenderPanel panel = new Utils.RenderPanel();
			panel.add(new Utils.RenderLabel("report("));
			panel.add(this.message.render());
			panel.add(new Utils.RenderLabel(")"));
			
			return panel;
		}
		
		public Type getType()
		{
			return Utils.noType;
		}
		
		public MaskEditor edit(Grammar handle)
		{
			MaskEditor editor = new MaskEditor(new Report(handle));
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

