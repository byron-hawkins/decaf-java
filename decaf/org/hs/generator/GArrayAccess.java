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

/**
 *	GArrayAccess represents an expression in which a base expression of 
 * array type is accessed with an int-valued expression in square brackets, e.g.: 
 * <P>
 * <code>
 *
 *	int[] numbers = new int[] {0,1,2,3,4,5};<br>
 *	System.err.println(numbers[3]);	// a GArrayAccess represents 'numbers[3]'
 *
 * </code><br>
 * <P>
 *	Multi-dimensional arrays are represented with nested GArrayAccess expressions,
 * such that the field <code>array</code> of one GArrayAccess is itself a GArrayAccess.
 */
public class GArrayAccess extends GConstruct implements GExpression
{
	static final long serialVersionUID = -4514885131948468642L;

	/**
	 *	The array field represents the base of the array access expression,
	 * and hence must evaluate to an array type.  Considering the example above,
	 * the local variable <code>numbers</code> would be an appropriate value
	 * for this field.  
	 */
	public GExpression array;
	
	/**
	 *	The index field represents the index within square brackets, and hence must
	 * evaluate to a primitive of type int.  Considering again
	 * the example above, the constant value 3 represents the <code>index</code> in that
	 * GArrayAccess expression.
	 */
	public GExpression index;

	/**
	 * Convenience constructor.
	 */
	public GArrayAccess(GExpression array, GExpression index)
	{
		super("???");
		
		this.array = array;
		this.index = index;
	}
	
	/**
	 * Decode a compiled GArrayAccess expression.
	 */
	GArrayAccess(JExpression expression)
	{
		super(expression);
		expression.accept(new Decoder());
	}

	/**
	 * Encode a GArrayAccess expression for printing or compilation.
	 */
	public JExpression encodeExpression()
	{
		return (new JArrayAccessExpression(super.tokenReference,
										   this.array.encodeExpression(),
										   this.index.encodeExpression()));
	}
		
	class Decoder extends GConstructDecoder
	{
		/**
		 * visits an array length expression
		 */
		public void visitArrayAccessExpression(JArrayAccessExpression self,
											   JExpression prefix,
											   JExpression accessor)
		{
			GArrayAccess.this.array = GDecoder.decode(prefix);
			GArrayAccess.this.index = GDecoder.decode(accessor);
		}
	}

	public String toString()
	{
		return "array(" + this.array + ") index(" + this.index + ")";
	}
}
