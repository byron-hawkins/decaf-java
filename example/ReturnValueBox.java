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
import javax.swing.border.*;
import javax.swing.event.*;

import org.hs.jfc.*;
import org.hs.generator.*;

import com.bitwise.decaf.editor.*;

public class ReturnValueBox extends PhraseBox implements MaskComponent
{
	protected Sentence returnSentence;
	
	public ReturnValueBox()
	{
		super("Return Value", new Phrase(null));
		
		this.returnSentence = null;
	}

	public void display(GConstruct code)
	{
		DMethod method = (DMethod)code;
		super.data.setParent(method.bodyGrammar);
		
		Vector statements = method.bodyGrammar.getData().statements;
		Data data;
		if (statements.isEmpty() || (!(((Sentence)statements.lastElement()).getContent() instanceof Data)))
		{
			this.returnSentence = new Sentence(method.bodyGrammar);
			data = new Data(this.returnSentence);
			returnSentence.setData(data);
		}
		else
		{
			this.returnSentence = (Sentence)statements.lastElement();
			data = (Data)returnSentence.getContent();
			statements.removeElementAt(statements.size()-1);
		}
		super.setData((Phrase)data.value);
	}
	
	public void apply(GConstruct code)
	{
		DMethod method = (DMethod)code;
		
		if (super.data.isEmpty())
		{
			method.returnType = Utils.voidType;
		}
		else
		{
			Vector statements = method.bodyGrammar.getData().statements;
			method.returnType = super.data.getType().typeSource();
			statements.add(this.returnSentence);
		}
	}
	
	static class Data extends GReturn implements Grammar.Content
	{
		public Data(Grammar handle)
		{
			super(new Phrase(handle));
		}
		
		public JComponent getComponent(Grammar handle)
		{
			Utils.RenderPanel panel = new Utils.RenderPanel();
			panel.add(new Utils.RenderLabel("Return "));
			panel.add(((Phrase)super.value).render());
			return panel;
		}
		
		public MaskEditor edit(Grammar handle)
		{
			return null;	// can only edit the value, using the ReturnTypeBox itself
		}
		
		public void setHandle(Grammar handle)
		{
			((Phrase)super.value).setParent(handle);
		}
		
		public GConstruct getSource()
		{
			return this;
		}
		
		public Type getType()
		{
			return ((Phrase)super.value).getType();
		}
	}
}
