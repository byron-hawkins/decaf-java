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
 *	A type cast expression, consisting of an GExpression to cast and 
 * a GType to cast it to.  Impossible casts may be printed if type checking
 * is disabled (including package name resolution), but will of course
 * fail to compile.  
 */
public class GCast extends GConstruct implements GExpression
{
	static final long serialVersionUID = -7893577085713186678L;

	/**
	 * The expression to cast.
	 */
	public GExpression expression;
	
	/**
	 * The type to cast <code>expression</code> to.
	 */
	public GType type;

	/**
	 * Convenience constructor.
	 */	
	public GCast(GExpression expression, GType type)
	{
		super("???");
		
		this.expression = expression;
		this.type = type;
	}
	
	GCast(JExpression expression)
	{
		super(expression);
		expression.accept(new Decoder());
	}
	
	public JExpression encodeExpression()
	{
		return (new JCastExpression(super.tokenReference,
								 	this.expression.encodeExpression(),
								 	this.type.encode()));
	}

	class Decoder extends GConstructDecoder
	{
		/**
		 * visits a cast expression
		 */
		public void visitCastExpression(JCastExpression self,
										JExpression expr,
										CType type)
		{
			GCast.this.type = new GType(type);
			GCast.this.expression = GDecoder.decode(expr);
		}
	}
	
	public String toString()
	{
		return "casting(" + this.expression + ") to(" + this.type + ")";
	}
}
