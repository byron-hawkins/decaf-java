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
 * An if statement, comprised of the expected <code>condition</code>
 * expression, <code>thenClause</code> and <code>elseClause</code>.
 */
public class GIf extends GConstruct implements GStatement, GExpression
{
	static final long serialVersionUID = 8426153124386775456L;

	/**
	 * The condition of the if statement; must evaluate to a boolean primitive,
	 * though it will not be enforced until compile time.
	 */
	public GExpression condition;
	
	/**
	 * The code to be executed under positive evaluation of the 
	 * <code>condition</code>.  It can be a single statement, or a block of code.
	 */
	public GConstruct thenClause;
	
	/**
	 * The code to be executed under negative evaluation of the <code>condition</code>.
	 * It can be a single statement, or a block of code.
	 */
	public GConstruct elseClause;
	
	/**
	 * Useful constructor for interactive code generation.
	 */
	public GIf()
	{
		super("???");
	}
	
	/**
	 * Useful constructor for the readability of static code generation.
	 */
	public GIf(GExpression condition, GConstruct thenClause, GConstruct elseClause)
	{
		super("???");
		
		this.condition = condition;
		this.thenClause = thenClause;
		this.elseClause = elseClause;
	}
	
	GIf(JStatement statement)
	{
		super(statement);
		statement.accept(new Decoder());
	}

	GIf(JExpression expression)
	{
		super(expression);
		expression.accept(new Decoder());
	}

	public JStatement encodeStatement()
	{
		return (new JIfStatement(super.tokenReference,
								 this.condition.encodeExpression(),
								 ((GStatement)this.thenClause).encodeStatement(),
								 (this.elseClause == null)?null:((GStatement)this.elseClause).encodeStatement(),
								 super.encodeComments()));
	}

	public JExpression encodeExpression()
	{
		return (new JConditionalExpression(super.tokenReference,
								 		   this.condition.encodeExpression(),
								 		   ((GExpression)this.thenClause).encodeExpression(),
								 		   ((GExpression)this.elseClause).encodeExpression()));
	}

	class Decoder extends GConstructDecoder
	{
		/**
		 * visits a if statement
		 */
		public void visitIfStatement(JIfStatement self,
									 JExpression cond,
									 JStatement thenClause,
									 JStatement elseClause)
		{
			GIf.this.condition = GDecoder.decode(cond);
			GIf.this.thenClause = (GConstruct)GDecoder.decode(thenClause);
			
			if (elseClause != null)
			{
				GIf.this.elseClause = (GConstruct)GDecoder.decode(elseClause);
			}
		}

		/**
		 * visits a conditional expression
		 */
		public void visitConditionalExpression(JConditionalExpression self,
											   JExpression cond,
											   JExpression left,
											   JExpression right)
		{
			GIf.this.condition = GDecoder.decode(cond);
			GIf.this.thenClause = (GConstruct)GDecoder.decode(left);
			GIf.this.elseClause = (GConstruct)GDecoder.decode(right);
		}
	}
	
	public String toString()
	{
		return "if(" + this.condition + ") then (" + this.thenClause + ") else(" + this.elseClause + ")";
	}
}

