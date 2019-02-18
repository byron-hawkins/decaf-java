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
 * A <code>while</code> or <code>do</code> loop.
 */
public class GWhile extends GLoop implements GStatement
{
	static final long serialVersionUID = -6615130378320743694L;
	
	/**
	 * The condition of the while or do loop; must resolve to a primitive boolean.
	 */
	public GExpression condition;
	
	/**
	 * The body of the loop.
	 */
	public GBlock body;
	
	/**
	 * A flag to specify whether the condition shall be pre- or post-applied
	 * (i.e., a 'while' or a 'do' loop).
	 */
	public boolean postCondition;
	
	public GWhile(GExpression condition)
	{
		this(condition, false);
	}
	
	public GWhile (GExpression condition, boolean postCondition)
	{
		super("???");
		
		this.condition = condition;
		this.body = new GBlock();
		this.postCondition = postCondition;
	}
	
	GWhile(JStatement statement)
	{
		this(statement, null);
	}
	
	GWhile(JStatement statement, String label)
	{
		super(statement);
		super.label = label;
		statement.accept(new Decoder());
	}

	public JStatement encodeStatement()
	{
		JStatement statement;
		if (this.postCondition)
		{
			statement = (new JDoStatement(super.tokenReference,
									 	  this.condition.encodeExpression(),
									 	  this.body.encodeStatement(),
									 	  super.encodeComments()));
		}
		else
		{
			statement = (new JWhileStatement(super.tokenReference,
									    	 this.condition.encodeExpression(),
									    	 this.body.encodeStatement(),
									    	 super.encodeComments()));
		}
		
		return super.encodeStatement(statement);
	}

	
	class Decoder extends GConstructDecoder
	{
		/**
		 * visits a while statement
		 */
		public void visitWhileStatement(JWhileStatement self,
										JExpression cond,
										JStatement body)
		{
			GWhile.this.condition = GDecoder.decode(cond);
			GWhile.this.body = new GBlock(body);
		}

		/**
		 * visits a do statement
		 */
		public void visitDoStatement(JDoStatement self,
									 JExpression cond,
									 JStatement body)
		{
			GWhile.this.postCondition = true;
			GWhile.this.condition = GDecoder.decode(cond);
			GWhile.this.body = new GBlock(body);
		}
	}

	public String toString()
	{
		return "condition(" + this.condition + ") body(" + this.body + ") do?(" + this.postCondition + ")";
	}
}
