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
 * An instanceof expression, comprised of the <code>expression</code> 
 * to evaluate, and the <code>type</code> against which to check it.
 */
public class GInstanceOf extends GConstruct implements GExpression
{
	static final long serialVersionUID = 8426153124386775456L;
		
	/**
	 * The expression to evaluate for derivation from <code>type</code>.
	 */
	public GExpression expression;
	
	/**
	 * The type to check <code>expression</code> against.
	 */
	public GType type;
	
	/**
	 * Convenience constructor.
	 */
	public GInstanceOf(GExpression expression, GType type)
	{
		super("???");
		
		this.expression = expression;
		this.type = type;
	}
	
	GInstanceOf(JExpression expression)
	{
		super(expression);
		expression.accept(new Decoder());
	}
	
	public JExpression encodeExpression()
	{
		return (new JInstanceofExpression(super.tokenReference,
								 		  this.expression.encodeExpression(),
								 		  this.type.encode()));
	}

	class Decoder extends GConstructDecoder
	{
		/**
		 * visits an instanceof expression
		 */
		public void visitInstanceofExpression(JInstanceofExpression self,
											  JExpression expr,
											  CType dest)
		{
			GInstanceOf.this.expression = GDecoder.decode(expr);
			GInstanceOf.this.type = new GType(dest);
		}
	}

	public String toString()
	{
		return "expression(" + this.expression + ") instanceof(" + this.type + ")";
	}
}
