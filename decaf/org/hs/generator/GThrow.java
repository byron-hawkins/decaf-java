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
 * A throw statement.
 */
public class GThrow extends GConstruct implements GStatement
{
	static final long serialVersionUID = 3759168500078370023L;

	/**
	 * The expression to throw.
	 */
	public GExpression expression;
	
	public GThrow(GExpression expression)
	{
		super("???");
		
		this.expression = expression;
	}
	
	GThrow(JStatement statement)
	{
		super(statement);
		statement.accept(new Decoder());
	}

	
	public JStatement encodeStatement()
	{
		return (new JThrowStatement(super.tokenReference,
								    this.expression.encodeExpression(),
								    super.encodeComments()));
	}

	class Decoder extends GConstructDecoder
	{
		/**
		 * visits a throw statement
		 */
		public void visitThrowStatement(JThrowStatement self,
										JExpression expr)
		{
			GThrow.this.expression = GDecoder.decode(expr);
		}
	}

	public String toString()
	{
		return "throws(" + this.expression + ")";
	}
}

