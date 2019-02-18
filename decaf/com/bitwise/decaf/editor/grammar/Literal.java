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

public class Literal extends GrammarMask
{
	static final long serialVersionUID = -2445025011554501144L;

	protected Data data;
	
	public static LiteralEditor edit(Conclusion conclusion, Phrase handle) //, Type type)
	{
		Data data = null;
		LiteralEditor editor;
		
		Grammar.Content content = handle.getContent();
		if ((content != null) && (content instanceof Data))
		{
			data = (Data)content;
			editor = new LiteralEditor(conclusion, data);
		}
		else
		{
			editor = new LiteralEditor(conclusion);
			data = editor.getData();
		}
		
		//data.applyType(type);
		
		handle.setData(data);
		editor.grabFocus();
		return editor;
	}
	
	public Literal()
	{
		// for reference purposes only!
		super();
	}
	
	public Literal(Phrase handle)
	{
		super(handle);
	}
	
	public Collection phraseGrammars()
	{
		return (new Vector());
	}
	
	public Collection sentenceGrammars()
	{
		return (new Vector());
	}
	
	public void apply()
	{
	}
	
	// how to start one of these in a PhraseBox with some other kind of data already in it?
	static class LiteralEditor extends JTextField implements KeyListener
	{
		protected Data data;
		protected Conclusion conclusion;

		public LiteralEditor(Conclusion conclusion)
		{
			this(conclusion, new Data());
		}
		
		public LiteralEditor(Conclusion conclusion, Data data)
		{
			super(20);
			
			this.conclusion = conclusion;
			this.data = data;
			
			super.setText(data.value);
			
			super.addKeyListener(this);
			super.grabFocus();
		}
		
		public void keyPressed(KeyEvent event)
		{
			if (event.getKeyCode() == KeyEvent.VK_ESCAPE)
			{
				this.conclusion.cancel();
			}
			
			if (event.getKeyCode() == KeyEvent.VK_ENTER)
			{
				this.data.value = super.getText();
				this.conclusion.ok();
			}
		}
		
		public void keyReleased(KeyEvent event)
		{
		}
		
		public void keyTyped(KeyEvent event)
		{
		}
		
		public Data getData()
		{
			if (this.data.value == null)
			{
				this.data.value = super.getText();
			}
			return this.data;
		}

		public Object writeReplace()
			throws java.io.ObjectStreamException
		{
			throw (new Utils.SerialException(getClass()));
		}
	}
	
	static class Data extends GLiteral implements Grammar.Content, Phrase.TypeChameleon
	{
		public Data()
		{
			super();
		}
		
		public JComponent getComponent(Grammar handle)
		{
			return (new Utils.RenderLabel(super.value));
		}
		
		public MaskEditor edit(Grammar handle)
		{
			return null;
		}
		
		public void setHandle(Grammar handle)
		{
		}
		
		public GConstruct getSource()
		{
			return this;
		}
		
		public void applyType(Type type)
		{
			if ((type == null) || (type.equals(Utils.noType)))
			{
				super.type = GType.STRING;
			}
			else
			{
				super.type = type.typeSource();
			}
		}

		public Type getType()
		{
			try
			{
				return Utils.wrapType(super.type);
			}
			catch (ClassNotFoundException e)
			{
				DecafEditor.log(e);
				return Utils.noType;
			}
		}
	}	
}

