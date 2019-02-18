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
 *	GBinary represents an assortment of binary (i.e., two-sided) expressions which includes:<p>
 * <code>
 * =<br>
 * +, -, *, /<br>
 * +=, -=, *=, /=<br>
 * <<, >>, >>><br>
 * <<=, <<=, >>>=<br>
 * &, |, ^<br> 
 * &=, |=, ^=<br>
 *</code>
 *<p>
 *	Assignment is indicated with the boolean <code>compoundAssignment</code> field.  The static
 * methods add(GExpression, GExpresion), subtract(...), multiply(...), etc. serve to enhance the 
 * readability of non-interactive code generation.
 */
public class GBinary extends GConstruct implements GExpression, GStatement //, KjcTokenTypes?
{
	static final long serialVersionUID = -5278336335269421768L;

	/**
	 *	The left operand of the binary expression.
	 */
	public GExpression left;
	
	/**
	 *	The binary operator; choose a a static final field of this class for the purpose,
	 * or apply one of the static convenience construction methods.
	 */
	public int operator;
	
	/**
	 *	The right operand of the binary expression.
	 */
	public GExpression right;
	
	/**
	 *	An optional flag to request the assignment version of the operator specified 
	 * in the <code>operator</code> field; must be false for SIMPLE (=).
	 */
	public boolean compoundAssignment;
	protected boolean assign;	// old -- required for deserialization.  Nuke when possible!

	/**
	 * Operator -
	 */	
	public static final int SUBTRACT = Constants.OPE_MINUS;

	/**
	 * Operator +
	 */	
	public static final int ADD = Constants.OPE_PLUS;

	/**
	 * Operator *
	 */	
	public static final int MULTIPLY = Constants.OPE_STAR;

	/**
	 * Operator /
	 */	
	public static final int DIVIDE = Constants.OPE_SLASH;

	/**
	 * Operator %
	 */	
	public static final int MODULO = Constants.OPE_PERCENT;

	/**
	 * Operator &
	 */	
	public static final int AND = Constants.OPE_BAND;

	/**
	 * Operator ^
	 */	
	public static final int XOR = Constants.OPE_BXOR;

	/**
	 * Operator |
	 */	
	public static final int OR = Constants.OPE_BOR;

	/**
	 * Operator <<
	 */	
	public static final int LEFT = Constants.OPE_SL;

	/**
	 * Operator >>
	 */	
	public static final int RIGHT = Constants.OPE_SR;

	/**
	 * Operator >>>
	 */	
	public static final int URIGHT = Constants.OPE_BSR;

	/**
	 * Operator =
	 */	
	public static final int SIMPLE = Constants.OPE_SIMPLE;

	/**
	 * Useful for interactive code generation.  Does not compile.
	 */	
	public static final int UNKNOWN = -1;

	/**
	 *	Useful constant for external calling code, to avoid mysterious and magical boolean values.
	 */	
	public static final boolean ASSIGN = true;

	/**
	 * Default no-arg constructor, useful for interactive code generation.
	 */
	public GBinary()
	{
		this(null, UNKNOWN, null, false);
	}

	/**
	 * Convenience constructor.
	 */
	public GBinary(GExpression left, int operator, GExpression right)
	{
		this(left, operator, right, false);
	}

	/**
	 * Convenience constructor.
	 */
	public GBinary(GExpression left, int operator, GExpression right, boolean compoundAssignment)
	{
		super("???");
		
		this.left = left;
		this.operator = operator;
		this.right = right;
		this.compoundAssignment = compoundAssignment;
	}

	GBinary(JExpression expression)
	{
		super(expression);
		expression.accept(new Decoder());
	}

	/**
	 *	Determine whether this GBinary's operator specifies and assignment.
	 */	
	public boolean assigns()
	{
		return (this.compoundAssignment || (this.operator == SIMPLE));
	}
	
	public static GBinary subtract(GExpression left, GExpression right)
	{
		return (new GBinary(left, SUBTRACT, right));
	}
	
	public static GBinary add(GExpression left, GExpression right)
	{
		return (new GBinary(left, ADD, right));
	}
	
	public static GBinary multiply(GExpression left, GExpression right)
	{
		return (new GBinary(left, MULTIPLY, right));
	}
	
	public static GBinary divide(GExpression left, GExpression right)
	{
		return (new GBinary(left, DIVIDE, right));
	}
	
	public static GBinary modulo(GExpression left, GExpression right)
	{
		return (new GBinary(left, MODULO, right));
	}
	
	public static GBinary and(GExpression left, GExpression right)
	{
		return (new GBinary(left, AND, right));
	}
	
	public static GBinary or(GExpression left, GExpression right)
	{
		return (new GBinary(left, OR, right));
	}
	
	public static GBinary xor(GExpression left, GExpression right)
	{
		return (new GBinary(left, XOR, right));
	}
	
	public static GBinary left(GExpression left, GExpression right)
	{
		return (new GBinary(left, LEFT, right));
	}

	public static GBinary right(GExpression left, GExpression right)
	{
		return (new GBinary(left, RIGHT, right));
	}

	public static GBinary uright(GExpression left, GExpression right)
	{
		return (new GBinary(left, URIGHT, right));
	}
	
	public static GBinary assign(GExpression left, GExpression right)
	{
		return (new GBinary(left, SIMPLE, right, false));
	}
	
	public static GBinary subtract(GExpression left, GExpression right, boolean compoundAssignment)
	{
		return (new GBinary(left, SUBTRACT, right, compoundAssignment));
	}
	
	public static GBinary add(GExpression left, GExpression right, boolean compoundAssignment)
	{
		return (new GBinary(left, ADD, right, compoundAssignment));
	}
	
	public static GBinary multiply(GExpression left, GExpression right, boolean compoundAssignment)
	{
		return (new GBinary(left, MULTIPLY, right, compoundAssignment));
	}
	
	public static GBinary divide(GExpression left, GExpression right, boolean compoundAssignment)
	{
		return (new GBinary(left, DIVIDE, right, compoundAssignment));
	}
	
	public static GBinary modulo(GExpression left, GExpression right, boolean compoundAssignment)
	{
		return (new GBinary(left, MODULO, right, compoundAssignment));
	}
	
	public static GBinary left(GExpression left, GExpression right, boolean compoundAssignment)
	{
		return (new GBinary(left, LEFT, right, compoundAssignment));
	}

	public static GBinary right(GExpression left, GExpression right, boolean compoundAssignment)
	{
		return (new GBinary(left, RIGHT, right, compoundAssignment));
	}

	public static GBinary uright(GExpression left, GExpression right, boolean compoundAssignment)
	{
		return (new GBinary(left, URIGHT, right, compoundAssignment));
	}
	
	public static GBinary and(GExpression left, GExpression right, boolean compoundAssignment)
	{
		return (new GBinary(left, AND, right, compoundAssignment));
	}
	
	public static GBinary or(GExpression left, GExpression right, boolean compoundAssignment)
	{
		return (new GBinary(left, OR, right, compoundAssignment));
	}
	
	public static GBinary xor(GExpression left, GExpression right, boolean compoundAssignment)
	{
		return (new GBinary(left, XOR, right, compoundAssignment));
	}
	
	public JExpression encodeExpression()
	{
		if (compoundAssignment)
		{
			return (new JCompoundAssignmentExpression(super.tokenReference,
                                   					  this.operator,
													  this.left.encodeExpression(),
													  this.right.encodeExpression()));
		}
		else
		{
			switch (this.operator)
			{
				case SUBTRACT:
					return (new JMinusExpression(super.tokenReference,
												 this.left.encodeExpression(),
												 this.right.encodeExpression()));
				case ADD:
					return (new JAddExpression(super.tokenReference,
												 this.left.encodeExpression(),
												 this.right.encodeExpression()));
				case MULTIPLY:
					return (new JMultExpression(super.tokenReference,
												 this.left.encodeExpression(),
												 this.right.encodeExpression()));
				case DIVIDE:
					return (new JDivideExpression(super.tokenReference,
												 this.left.encodeExpression(),
												 this.right.encodeExpression()));
				case MODULO:
					return (new JModuloExpression(super.tokenReference,
												  this.left.encodeExpression(),
												  this.right.encodeExpression()));
				case AND:
				case XOR:
				case OR:
					return (new JBitwiseExpression(super.tokenReference,
												   this.operator,
												   this.left.encodeExpression(),
												   this.right.encodeExpression()));
				case LEFT:
				case RIGHT:
				case URIGHT:
					return (new JShiftExpression(super.tokenReference,
												 this.operator,
												 this.left.encodeExpression(),
												 this.right.encodeExpression()));
				case SIMPLE:
					return (new JAssignmentExpression(super.tokenReference,
													  this.left.encodeExpression(),
													  this.right.encodeExpression()));
			}
		}
		throw (new IllegalArgumentException("What is operator " + this.operator + "?"));
	}
	
	public JStatement encodeStatement()
	{
		if ((!compoundAssignment) && (this.operator != SIMPLE))
		{
			throw (new IllegalArgumentException("Can't encode a non-assigning math expression as a statement."));
		}
		return expressionAsStatement(this);
	}
	
	class Decoder extends GConstructDecoder
	{
		/**
		 * visits an array allocator expression
		 */
		public void visitBinaryExpression(JBinaryExpression self,
										  String oper,
										  JExpression left,
										  JExpression right)
		{
			switch (oper.charAt(0))
			{
				case '-':
					GBinary.this.operator = SUBTRACT;
					break;
				case '+':
					GBinary.this.operator = ADD;
					break;
				case '*':
					GBinary.this.operator = MULTIPLY;
					break;
				case '/':
					GBinary.this.operator = DIVIDE;
					break;
				case '%':
					GBinary.this.operator = MODULO;
					break;
			}
			
			GBinary.this.compoundAssignment = false;	// "*" for mult, 
			GBinary.this.left = GDecoder.decode(left);
			GBinary.this.right = GDecoder.decode(right);
		}

		/**
		 * visits a compound expression
		 */
		public void visitCompoundAssignmentExpression(JCompoundAssignmentExpression self,
													  int oper,
													  JExpression left,
													  JExpression right)
		{
			GBinary.this.compoundAssignment = true;
			GBinary.this.operator = oper;
			GBinary.this.left = GDecoder.decode(left);
			GBinary.this.right = GDecoder.decode(right);
		}
		
		/**
		 * visits an assignment expression
		 */
		public void visitAssignmentExpression(JAssignmentExpression self,
											  JExpression left,
											  JExpression right)
		{
			GBinary.this.compoundAssignment = false;
			GBinary.this.operator = SIMPLE;
			GBinary.this.left = GDecoder.decode(left);
			GBinary.this.right = GDecoder.decode(right);
		}

		/**
		 * visits a shift expressiona
		 */
		public void visitRelationalExpression(JRelationalExpression self,
											  int oper,
											  JExpression left,
											  JExpression right)
		{
			GBinary.this.compoundAssignment = false;
			GBinary.this.operator = oper;
			GBinary.this.left = GDecoder.decode(left);
			GBinary.this.right = GDecoder.decode(right);
		}
	}

	protected Object readResolve()
		throws java.io.ObjectStreamException 
	{
		this.compoundAssignment = this.assign;
		return super.readResolve();
	}
	
	public String toString()
	{
		return this.left + " " + this.operator + " " + this.right;
	}
}

