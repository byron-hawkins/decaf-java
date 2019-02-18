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
 * A boolean comparison between two values, consisting of two GExpression`s and
 * one of the following operators:
 * <p><code>
 * <, >, <=, >=, ==, !=, &&, ||
 * </code><p>
 * Compatibility of the expressions will be checked at compile time (or during
 * code generation if type checking is enabled).  Static code generators may find 
 * the static construction methods will make for more readable code.
 */
public class GComparison extends GConstruct implements GExpression 
{
	static final long serialVersionUID = -2602662385064311177L;

	/**
	 * The left operand of the comparison.
	 */
	public GExpression left;
	
	/**
	 * The operator, chosen from one of the public constants declared by this class.
	 */
	public int operator;
	
	/**
	 * The right operand of the comparison.
	 */
	public GExpression right;
	
	/**
	 * The operator <
	 */
	public static final int LT = Constants.OPE_LT;

	/**
	 * The operator <=
	 */
	public static final int LE = Constants.OPE_LE;

	/**
	 * The operator >
	 */
	public static final int GT = Constants.OPE_GT;

	/**
	 * The operator >=
	 */
	public static final int GE = Constants.OPE_GE;

	/**
	 * The operator ==
	 */
	public static final int EQ = Constants.OPE_EQ;

	/**
	 * The operator !=
	 */
	public static final int NE = Constants.OPE_NE;

	/**
	 * The operator &&
	 */
	public static final int AND = 100;

	/**
	 * The operator ||
	 */
	public static final int OR = 101;

	/**
	 * Placeholder for <code>this.operator</code> when it is unknown; this is
	 * useful for interactive code generation.
	 */
	public static final int UNKNOWN = -1;
	
	/**
	 * Convenience constructor.
	 */
	public GComparison(GExpression left, int operator, GExpression right)
	{
		super("???");
		
		this.left = left;
		this.operator = operator;
		this.right = right;
	}
	
	GComparison(JExpression expression)
	{
		super(expression);
		expression.accept(new Decoder());
	}
	
	public GComparison()
	{
		this(null, UNKNOWN, null);
	}
	
	public static GComparison lessThan(GExpression left, GExpression right)
	{
		return (new GComparison(left, Constants.OPE_LT, right));
	}
	
	public static GComparison lessEquals(GExpression left, GExpression right)
	{
		return (new GComparison(left, Constants.OPE_LE, right));
	}
	
	public static GComparison greaterThan(GExpression left, GExpression right)
	{
		return (new GComparison(left, Constants.OPE_GT, right));
	}
	
	public static GComparison greaterEquals(GExpression left, GExpression right)
	{
		return (new GComparison(left, Constants.OPE_GE, right));
	}
	
	public static GComparison equals(GExpression left, GExpression right)
	{
		return (new GComparison(left, Constants.OPE_EQ, right));
	}
	
	public static GComparison notEquals(GExpression left, GExpression right)
	{
		return (new GComparison(left, Constants.OPE_NE, right));
	}
	
	public static GComparison and(GExpression left, GExpression right)
	{
		return (new GComparison(left, AND, right));
	}
	
	public static GComparison or(GExpression left, GExpression right)
	{
		return (new GComparison(left, OR, right));
	}
	
	public JExpression encodeExpression()
	{
		switch (this.operator)
		{
			case LT:
			case LE:
			case GT:
			case GE:
				return (new JRelationalExpression(super.tokenReference,
												  this.operator,
												  this.left.encodeExpression(),
												  this.right.encodeExpression()));
			case EQ:
			case NE:
				return (new JEqualityExpression(super.tokenReference,
												(this.operator == EQ),
												this.left.encodeExpression(),
												this.right.encodeExpression()));
			case AND:
				return (new JConditionalAndExpression(super.tokenReference,
													  this.left.encodeExpression(),
													  this.right.encodeExpression()));
			case OR:
				return (new JConditionalOrExpression(super.tokenReference,
													 this.left.encodeExpression(),
													 this.right.encodeExpression()));
		}
		
		throw (new IllegalArgumentException());
	}

	class Decoder extends GConstructDecoder
	{
		/**
		 * visits a shift expressiona
		 */
		public void visitRelationalExpression(JRelationalExpression self,
											  int oper,
											  JExpression left,
											  JExpression right)
		{
			GComparison.this.operator = oper;
			GComparison.this.left = GDecoder.decode(left);
			GComparison.this.right = GDecoder.decode(right);
		}
	}

	public String toString()
	{
		return "left(" + this.left + ") operator(" + this.operator + ") right(" + this.right + ")";
	}
}
