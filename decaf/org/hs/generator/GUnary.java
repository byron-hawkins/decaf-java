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
 * A unary operation, one of:
 * <p>
 * !, -, +, ^
 * <p>
 * The static constructor methods are useful for the readability of static
 * code generation.
 */
public class GUnary extends GConstruct implements GExpression
{
	static final long serialVersionUID = -1062574211307070478L;

	/**
	 * E.g., given <code>int x</code>:<br>
	 * ~x
	 */
	public static final int BITWISE = Constants.OPE_BNOT;

	/**
	 * E.g., given <code>boolean b</code>:<br>
	 * !b
	 */
	public static final int LOGICAL = Constants.OPE_LNOT;

	/**
	 * E.g., given <code>int x</code>:<br>
	 * -x
	 */
	public static final int NEGATE = Constants.OPE_MINUS;

	/**
	 * It's the clugerific unary plus operator!<br>
	 * E.g., given <code>int x</code>:<br>
	 * +x
	 */
	public static final int PLUS = Constants.OPE_PLUS;

	/**
	 * The recipient of the unary operation; must be type compatible with the operator chosen.
	 */	
	public GExpression subject;
	
	/**
	 * The operation to perform; choose one of the class constants.
	 */
	protected int operator;
	
	public static GUnary bitwise(GExpression subject)
	{
		return (new GUnary(subject, BITWISE));
	}

	public static GUnary logical(GExpression subject)
	{
		return (new GUnary(subject, LOGICAL));
	}
	
	public static GUnary negate(GExpression subject)
	{
		return (new GUnary(subject, NEGATE));
	}
	
	public static GUnary plus(GExpression subject)
	{
		return (new GUnary(subject, PLUS));
	}
	
	protected GUnary(GExpression subject, int operator)
	{
		super("???");
		
		this.subject = subject;
		this.operator = operator;
	}
	
	GUnary(JExpression expression)
	{
		super(expression);
		expression.accept(new Decoder());
	}

	public JExpression encodeExpression()
	{
		switch (this.operator)
		{
			case BITWISE:
				return (new JBitwiseComplementExpression(super.tokenReference,
										 				this.subject.encodeExpression()));
			case LOGICAL:
				return (new JLogicalComplementExpression(super.tokenReference,
										 				 this.subject.encodeExpression()));
			case NEGATE:
				return (new JUnaryMinusExpression(super.tokenReference,
										 		  this.subject.encodeExpression()));
			case PLUS:
				return (new JUnaryPlusExpression(super.tokenReference,
										 		 this.subject.encodeExpression()));
		}
		
		throw (new IllegalArgumentException("What kind of complement expression has operator number " + this.operator + "?"));
	}
	
	class Decoder extends GConstructDecoder
	{
		/**
		 * visits an unary plus expression
		 */
		public void visitUnaryPlusExpression(JUnaryExpression self,
											 JExpression expr)
		{
			GUnary.this.operator = PLUS;
			GUnary.this.subject = GDecoder.decode(expr);
		}
		/**
		 * visits an unary minus expression
		 */
		public void visitUnaryMinusExpression(JUnaryExpression self,
											  JExpression expr)
		{
			GUnary.this.operator = NEGATE;
			GUnary.this.subject = GDecoder.decode(expr);
		}
		/**
		 * visits a bitwise complement expression
		 */
		public void visitBitwiseComplementExpression(JUnaryExpression self,
													   JExpression expr)
		{
			GUnary.this.operator = BITWISE;
			GUnary.this.subject = GDecoder.decode(expr);
		}
	
	
		/**
		 * visits a logical complement expression
		 */
		public void visitLogicalComplementExpression(JUnaryExpression self,
													   JExpression expr)
		{
			GUnary.this.operator = LOGICAL;
			GUnary.this.subject = GDecoder.decode(expr);
		}
	}
}
