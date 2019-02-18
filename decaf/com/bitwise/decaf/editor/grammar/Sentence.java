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
import java.lang.reflect.*;

import javax.swing.*;
import javax.swing.text.*;

import org.hs.util.*;
import org.hs.generator.*;

import at.dms.kjc.*;

import com.bitwise.decaf.editor.*;

/**
 * Sentence is a transparent wrapper for GStatement, holding its
 * place in the code tree and providing a generic interface by which
 * the editor can interact with it.  
 */
public class Sentence extends Grammar implements GStatement
{
	static final long serialVersionUID = 6281858926606802568L;

	/**
	 * Create a new placeholder for a sentence with the specified
	 * <code>parent</code> in the code tree.
	 */
	public Sentence(CodeNode parent)
	{
		super(parent);
	}

	protected void applyMembers(Copy yourself)
	{
		Sentence myself = (Sentence)yourself;
		myself.content = (Grammar.Content)((Copy)this.content).deepCopy();
	}
	
	/**
	 * set <code>this.content</code> with the data provided as <code>o</code>
	 * by the following rules:
	 * <p>
	 * 1) if o is a Sentence, take its content<br>
	 * 2) if o implements Grammar.Content, take o<br>
	 * 3) if o is null, set content to null<br>
	 * 4) no other forms of o are acceptable: throw an IllegalArgumentException 
	 * asking what the object is (by classname, as class is the criterium)
	 */
	public void setData(Object o)
	{
		if (o == null)
		{
			this.content = null;
		}
		else if (o instanceof Sentence)
		{
			//setData(((Sentence)o).content);
			super.setContent(((Sentence)o).content);
		}
		else if (o instanceof Grammar.Content)
		{
			//setData((Grammar.Content)o);
			super.setContent((Grammar.Content)o);
		}
		else
		{
			throw (new IllegalArgumentException("Can't accept this data; what is it? " + o.getClass().getName() + ": " + o));
		}

		super.notifyNewData();
	}

	/**
	 * set <code>this.content</code> with the provided <code>content</code>
	 *
	 * @param content the new content of this Sentence
	 */	
	public void setData(Grammar.Content content)
	{
		super.content = content;
	}
	
	public JStatement encodeStatement()
	{
		return ((GStatement)super.content).encodeStatement();
	}

	/**
	 * If <code>this.content</code> is a DVariable, add it to the collection 
	 * of variables in the scope being checked (the <code>variables</code>
	 * parameter).  Pass the collection to <code>this.parent</code>.
	 */	
	public void collectVariables(Collection variables, Object child)
	{
		if (this.content instanceof DVariable)
		{
			variables.add(this.content);
		}
		
		this.parent.collectVariables(variables, this);
	}

	/**
	 * @return "<New Sentence>" if <code>this.content</code> is null; otherwise
	 * return <code>this.content.toString()</code>.
	 */	
	public String toString()
	{
		if (this.content == null)
		{
			return "<New Sentence>";
		}
		return ((GConstruct)this.content).toString();
	}
}