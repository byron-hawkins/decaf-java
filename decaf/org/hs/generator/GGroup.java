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
 * An expression delimited by parentheses, such as:
 * <p><code>
 * (index <= 0)
 * </code><p>
 * May not be used to simulate a type cast, except maybe via the printer
 * when type checking is disabled (but I recommend using a GCast instead).
 */
public class GGroup extends GConstruct implements GExpression
{
	static final long serialVersionUID = -456752993805303054L;

	/**
	 * The expression to be delimited by parentheses.
	 */
	public GExpression expression;

	/**
	 * Convenience constructor.
	 */	
	public GGroup(GExpression expression)
	{
		super("???");
		
		this.expression = expression;
	}
	
	GGroup(JExpression expression)
	{
		super(expression);
		expression.accept(new Decoder());
	}

	public JExpression encodeExpression()
	{
		return (new JParenthesedExpression(super.tokenReference,
										   this.expression.encodeExpression()));
	}

	class Decoder extends GConstructDecoder
	{
		/**
		 * visits a parenthesed expression
		 */
		public void visitParenthesedExpression(JParenthesedExpression self,
											   JExpression expr)
		{
			GGroup.this.expression = GDecoder.decode(expr);
		}
	}
	
	public String toString()
	{
		return "grouping(" + this.expression + ")";
	}
}

