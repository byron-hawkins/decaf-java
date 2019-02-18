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

public class Paragraph extends CodeNode implements GStatement, Utils.List.Listener
{
	static final long serialVersionUID = -8848998956533378054L;

	public static final Field statementsField = GConstruct.getField(GBlock.class, "statements");

	protected GBlock content;
	
	public Paragraph(CodeNode parent)
	{
		super(parent);

		this.content = new GBlock();
		this.content.statements = new Utils.List();
		((Utils.List)this.content.statements).addListener(this);
	}

	public void report(Utils.List.Event event)
	{
		super.notifyNewData();
	}
	
	public Utils.List getContent()
	{
		return (Utils.List)this.content.statements;
	}

	public GBlock getData()
	{
		return this.content;
	}
	
	public GConstruct getSource()
	{
		return this.content;
	}
	
	protected void applyMembers(Copy yourself)
	{
		Paragraph myself = (Paragraph)yourself;
		myself.content = (GBlock)((Copy)this.content).deepCopy();
	}
	
	public boolean isEmpty()
	{
		return this.content.statements.isEmpty();
	}
	
	// A little facile, perhaps...
	public void setData(Object o)
	{
		if (o instanceof GBlock)
		{
			this.setData((GBlock)o);
		}
		
		throw (new IllegalArgumentException("Paragraph only accepts data in the form of a GBlock."));
	}
	
	public void setData(GBlock content)
	{
		this.content = content;
		if (!(this.content.statements instanceof Utils.List))
		{
			this.content.statements = new Utils.List(this.content.statements);
		}
		super.notifyNewData();
	}
	
	public void add(GStatement statement)
	{
		this.content.statements.add(statement);
	}
	
	public void add(int index, GStatement statement)
	{
		this.content.statements.add(index, statement);
	}
	
	public void addAll(Collection c)
	{
		this.content.statements.addAll(c);
	}
	
	public Object get(Field field)
	{
		return ((GConstruct)this.content).get(field);
	}
	
	public void set(Field field, Object value)
	{
		((GConstruct)this.content).set(field, value);
	}
	
	public JStatement encodeStatement()
	{
		return this.content.encodeStatement();
	}
	
	public MaskEditor edit()
	{
		return null;	
	}
	
	public CodeNode getParent()
	{
		return this.parent;
	}
	
	public boolean isInScope(DVariable variable, Object child)
	{
		Iterator sentences = this.content.statements.iterator();
		Sentence next;
		Grammar.Content content;
		while (sentences.hasNext())
		{
			next = (Sentence)sentences.next();
			
			if ((!next.isEmpty()) && next.getContent().equals(variable))
			{
				return true;
			}
			
			if (next.equals(child))
			{
				if (this.parent == null)
				{
					return false;
				}
				else
				{
					return this.parent.isInScope(variable, this);
				}
			}
		}
		
		throw (new RuntimeException("CodeNode " + child + " claims to be my (" + this + ") child, but it is not in my contents!"));
	}
	
	public void collectVariables(Collection variables, Object child)
	{
		Object next;
		Iterator sentences = this.content.statements.iterator();
		while (sentences.hasNext())
		{
			next = sentences.next();
			if (next instanceof DVariable)
			{
				variables.add(next);
			}

			if (next.equals(child))
			{
				break;
			}
		}
		
		this.parent.collectVariables(variables, child);
	}
	
	public Cup identifyCup()
	{
		return this.parent.identifyCup();
	}
	
	public String toString()
	{
		return "Paragraph<" + this.content.toString() + ">";
	}
}