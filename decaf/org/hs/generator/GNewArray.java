/*
 * Copyright (C) 2003 HawkinsSoftware
 *
 * This Java code generator package is free 
 * software.  You can redistribute it and/or modify it under the terms 
 * of the GNU General Public License as published by the Free Software 
 * Foundation.  However, no compilation of this code or a derivative
 * of it may be used with or integrated into any commercial application,
 * except by the written permisson of HawkinsSoftware.  Future versions 
 * of this product will be sold commercially under a different license.  
 * HawkinsSoftware retains all rights to this product, including its
 * concepts, design and implementation.
 *
 * This package is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
 
package org.hs.generator;

import at.dms.kjc.*;

import java.util.*;

/**
 * An instantiation of an array; empty (with <code>dimensions</code>), or 
 * <code>initialized</code>.  Note that the array dimensions are (in either
 * case) instantiated linearly, such that nesting of single-dimensioned 
 * arrays in the base type is not possible (as it is with java.lang.Class).
 */
public class GNewArray extends GConstruct implements GExpression
{
	static final long serialVersionUID = 6519657230548353207L;

	/**
	 * The array base type.  
	 */
	public GType type;
	
	/**
	 * The dimensions of the array, as a collection of GExpression (each of which
	 * must of course evaluate to a primtive int).  
	 * These will be ignored in the presence 
	 * of a non-empty collection of <code>initializers</code>.
	 */
	public Vector dimensions;	
	
	/**
	 * The initializer of this array instantiation; precludes and makes void any 
	 * value in <code>dimensions</code>.  Dimensions of initializers may be nested
	 * with the recognized class java.util.Vector (a subclass will be accepted).
	 */
	public Vector initializers;	// GExpression
	
	protected transient int initializerDimensions;	
	
	// initializers preclude dimensions... need to enforce?

	/**
	 * Convenience constructor for interactive code generation.
	 */	
	public GNewArray(GType type)
	{
		super("???");
		init();
		this.type = type;
	}
	
	GNewArray(JExpression expression)
	{
		super(expression);
		init();
		expression.accept(new Decoder());
	}
	
	private void init()
	{
		this.dimensions = new Vector();
		this.initializers = new Vector();
	}
	
	public JExpression encodeExpression()
	{
		this.initializerDimensions = 1;
		JArrayInitializer encodedInitializers = encodeInitializers();
		return (new JNewArrayExpression(super.tokenReference,
									    this.type.encode(),
									    encodeDimensions(),
									    encodedInitializers));
	}
	
	protected JExpression[] encodeDimensions()
	{
		JExpression[] encoded;
		
		if (!this.dimensions.isEmpty())
		{
			encoded = new JExpression[this.dimensions.size()];
			Iterator iterator = this.dimensions.iterator();
			int i = 0;
			while (iterator.hasNext())
			{
				encoded[i++] = ((GExpression)iterator.next()).encodeExpression();
			}
		}		
		else 
		{
			encoded = new JExpression[this.initializerDimensions];
			for (int i = 0; i < this.initializerDimensions; i++)
			{
				encoded[i] = null;
			}
		}
		return encoded;
	}
	
	protected JArrayInitializer encodeInitializers()
	{
		if (this.initializers.isEmpty())
		{
			return null;
		}
		
		return encodeInitializers(this.initializers);
	}
	
	protected JArrayInitializer encodeInitializers(Vector content)
	{
		JExpression[] encoded = new JExpression[content.size()];
		boolean anotherInitializer = false;
		Object next;
		for (int i = 0; i < content.size(); i++)
		{
			next = content.elementAt(i);
			if (i == 0)
			{
				this.initializerDimensions += (anotherInitializer = (next instanceof Vector))?1:0;
			}
			
			if (anotherInitializer)
			{
				encoded[i] = encodeInitializers((Vector)next);
			}
			else
			{
				encoded[i] = ((GExpression)next).encodeExpression();
			}
		}
		
		return (new JArrayInitializer(super.tokenReference, encoded));
	}

	class Decoder extends GConstructDecoder
	{
		/**
		 * visits an array allocator expression
		 */
		public void visitNewArrayExpression(JNewArrayExpression self,
											CType type,
											JExpression[] dims,
											JArrayInitializer init)
		{
			GNewArray.this.type = new GType(type);
			
			if ((dims.length > 0) && (dims[0] != null))
			{
				for (int i = 0; i < dims.length; i++)
				{
					GNewArray.this.dimensions.add(GDecoder.decode(dims[i]));
				}
			} // else it's initialized
			
			init.accept(this);
		}

		/**
		 * visits an array initializer expression
		 */
		public void visitArrayInitializer(JArrayInitializer self,
										  JExpression[] elems)
		{
			for (int i = 0; i < elems.length; i++)
			{
				GNewArray.this.initializers.add(GDecoder.decode(elems[i]));
			}
		}
	}

	public String toString()
	{
		return "type(" + this.type + ") dimensions(" + this.dimensions + ") initializers(" + this.initializers + ")";
	}
}

