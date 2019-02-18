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
 * A stepwise operator, one of:
 * <p>
 * ++i, --i, i++, i--	
 */
public class GStepwise extends GConstruct implements GExpression, GStatement
{
	static final long serialVersionUID = -3594297290075410301L;

	/**
	 * ++i
	 */
	public static final int PRE_INCREMENT = Constants.OPE_PREINC;

	/**
	 * --i
	 */
	public static final int PRE_DECREMENT = Constants.OPE_PREDEC;

	/**
	 * i++
	 */
	public static final int POST_INCREMENT = Constants.OPE_POSTINC;

	/**
	 * i--
	 */
	public static final int POST_DECREMENT = Constants.OPE_POSTDEC;
	
	/**
	 * The recipient of the increment/decrement; must evaluate to a primitive 
	 * integral type (short, int, long, char, byte).
	 */
	public GExpression operand;
	
	/**
	 * The method of incrememnt/decrement; choose from one of the static fields.
	 */
	public int operator;

	/**
	 * Convenience constructor.
	 */	
	public GStepwise(GExpression operand, int operator)
	{
		super("???");
		
		this.operand = operand;
		this.operator = operator;
	}
	
	GStepwise(JExpression expression)
	{
		super(expression);
		expression.accept(new Decoder());
	}
	
	public JExpression encodeExpression()
	{
		switch (this.operator)
		{
			case PRE_INCREMENT:
			case PRE_DECREMENT:
				return (new JPrefixExpression(super.tokenReference,
											  this.operator,
											  this.operand.encodeExpression()));
			case POST_INCREMENT:
			case POST_DECREMENT:
				return (new JPostfixExpression(super.tokenReference,
											   this.operator,
											   this.operand.encodeExpression()));
		}
		
		throw (new IllegalArgumentException("What kind of stepwise operator is numbered " + this.operator + "?"));
	}

	public JStatement encodeStatement()
	{
		return expressionAsStatement(this);
	}

	class Decoder extends GConstructDecoder
	{
		/**
		 * visits a postfix expression
		 */
		public void visitPostfixExpression(JPostfixExpression self,
										   int oper,
										   JExpression expr)
		{
			GStepwise.this.operator = oper;
			GStepwise.this.operand = GDecoder.decode(expr);
		}
		/**
		 * visits a prefix expression
		 */
		public void visitPrefixExpression(JPrefixExpression self,
										  int oper,
										  JExpression expr)
		{
			GStepwise.this.operator = oper;
			GStepwise.this.operand = GDecoder.decode(expr);
		}
	}

	public String toString()
	{
		return "operand(" + this.operand + ") operator(" + this.operator + ")";
	}
}
