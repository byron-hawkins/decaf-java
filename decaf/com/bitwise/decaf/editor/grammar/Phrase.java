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
 * Phrase is a transparent wrapper for a GExpression, holding its
 * place in the code tree and providing a generic interface by which
 * the editor can interact with it.  Its type defaults to Utils.noType,
 * and null tolerance is a virtue in this class.  An external entity
 * may notify a Phrase that it is expecting a certain type to be in 
 * its place, via <code>setExpectedType(Type)</code>; if the Phrase's 
 * content implements TypeChameleon, <code>TypeChameleon.applyType(Type)</code>
 * will be called with the expected type.
 */
public class Phrase extends Grammar implements GExpression, Typed
{
	static final long serialVersionUID = 8768331074472093150L;

	protected boolean isClassReference;
	protected Type expectedType;

	/**
	 * Construct a phrase with the provided <code>parent</code> node
	 * in the code tree.  Initializes <code>this.content</code> to
	 * null and <code>this.expectedType</code> to Utils.noType.
	 */
	public Phrase(CodeNode parent)
	{
		super(parent);

		this.clear();
		this.isClassReference = false;
		this.expectedType = Utils.noType;
	}

	protected void applyMembers(Copy yourself)
	{
		super.applyMembers(yourself);
		
		Phrase myself = (Phrase)yourself;
		if (!myself.isEmpty())
		{
			if (!(myself.content instanceof DVariable))	// should really identify references intrinsically
			{
				myself.content = (Grammar.Content)((Copy)super.content).deepCopy();
			}
		}
	}

	/**
	 * @return the type of this Phrase's content, or Utils.noType if this
	 * Phrase is currently empty.
	 */	
	public Type getType()
	{
		if (this.content == null)
		{
			return Utils.noType;
		}
		return this.content.getType();
	}
	
	/**
	 * @return true if <code>this.content instanceof ClassReference</code>
	 * (in which case accessed members will be static).  
	 */
	public boolean isClassReference()
	{
		return this.isClassReference;
	}
	
	/**
	 * set <code>this.content</code> with the data provided as <code>o</code>
	 * by the following rules:
	 * <p>
	 * 1) if o is a Phrase, take its content<br>
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
		else if (o instanceof Phrase)
		{
			setData(((Phrase)o).content);
		}
		else if (o instanceof Grammar.Content)
		{
			setData((Grammar.Content)o);
		}
		else
		{
			throw (new IllegalArgumentException("Can't accept this data.  What is it? <" + o.getClass().getName() + ": " + o + ">"));
		}
		
		super.notifyNewData();
	}
	
	private void setData(Grammar.Content content)
	{
		if (super.content instanceof DVariable)
		{
			((DVariable)super.content).removeReferenceListener(this);
		}
		
		super.removeOfficers(false);

		if (content instanceof DVariable)
		{
			((DVariable)content).addReferenceListener(this);
		}
		
		//super.content = content;
		super.setContent(content);
		
		if (content instanceof TypeChameleon)
		{
			((TypeChameleon)content).applyType(this.expectedType);
		}

		this.isClassReference = (content instanceof ClassReference);
	}

	/**
	 * Set the expected type of this Phrase.  If <code>this.content</code>
	 * implements TypeChameleon, apply the new expected type to it.
	 */	
	public void setExpectedType(Type type)
	{
		this.expectedType = type;
		
		if (this.content instanceof TypeChameleon)
		{
			((TypeChameleon)this.content).applyType(this.expectedType);
		}
	}
	
	public void referenceUpdated(ReferenceEvent event)
	{
		super.referenceUpdated(event);
	}
	
	public JExpression encodeExpression()
	{
		return ((GExpression)super.content).encodeExpression();
	}
	
	/**
	 * Pass the collection to <code>this.parent</code>, since Phrase cannot
	 * declare any variables.  
	 */
	public void collectVariables(Collection variables, Object child)
	{
		super.parent.collectVariables(variables, this);
	}

	/**
	 * @return "<New Phrase>" if <code>this.content</code> is null; otherwise 
	 * return <code>this.content.toString()</code>
	 */	
	public String toString()
	{
		if (super.content == null)
		{
			return "<New Phrase>";
		}
		return super.content.toString();
	}
	
	/**
	 * Instances of Grammar.Content may additionally implement TypeChameleon
	 * if their content can be converted to other (most often primitive) types.
	 * When an expected type is applied to this Phrase, it will pass that type 
	 * to its content with <code>applyType(Type)</code> iff it implements 
	 * TypeChalemeon; otherwise the <code>expectedType</code> will be ignored.
	 */
	public static interface TypeChameleon
	{
		public void applyType(Type type);
	}
}