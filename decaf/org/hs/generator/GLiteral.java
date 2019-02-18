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
 * A literal expression, explicitly typed by the field <code>type</code>,
 * and encoded with a java.lang.String.
 */
public class GLiteral extends GConstruct implements GExpression 
{
	static final long serialVersionUID = -3265063521342418753L;

	/**
	 * The type of this literal; must be primitive.
	 */
	public GType type;
	
	/**
	 * The value of this literal, which must of course be parseable as <code>type</code>.
	 */
	public String value;
	
	/**
	 * Convenience constructor for the readability of static code generation.
	 */
	public GLiteral(GType type, String value)
	{
		super("Literal[" + type + "]: " + value);
		
		this.type = type;
		this.value = value;
	}
	
	/**
	 * Convenience constructor for interactive code generation.
	 */
	public GLiteral(GType type)
	{
		this(type, "");
	}
	
	public GLiteral()
	{
		this(GType.STRING, "");
	}
	
	GLiteral(JExpression expression)
	{
		super(expression);
		expression.accept(new Decoder());
	}
	
	public JExpression encodeExpression()
	{
		try
		{
			if (this.type.equals(GType.BYTE))
			{
				return (new JByteLiteral(super.tokenReference, Byte.parseByte(this.value)));
			}
			else if (this.type.equals(GType.BOOLEAN))
			{
				return (new JBooleanLiteral(super.tokenReference, (new Boolean(this.value)).booleanValue()));
			}
			else if (this.type.equals(GType.DOUBLE))
			{
				return (new JDoubleLiteral(super.tokenReference, this.value));
			}
			else if (this.type.equals(GType.LONG))
			{
				return (new JLongLiteral(super.tokenReference, this.value));
			}
			else if (this.type.equals(GType.SHORT))
			{
				return (new JShortLiteral(super.tokenReference, Short.parseShort(this.value)));
			}
			else if (this.type.equals(GType.CHAR))
			{
				return (new JCharLiteral(super.tokenReference, this.value));
			}
			else if (this.type.equals(GType.INT))
			{
				return (new JIntLiteral(super.tokenReference, this.value));
			}
			else if (this.type.equals(GType.FLOAT))
			{
				return (new JFloatLiteral(super.tokenReference, this.value));
			}
			else if (this.type.equals(GType.NULL))
			{
				return (new JNullLiteral(super.tokenReference));
			}
			else if (this.type.equals(GType.STRING) || this.type.toString().equals("java.lang.String"))	// it's clugerific!
			{
				return (new JStringLiteral(super.tokenReference, this.value));
			}
		}
		catch (at.dms.compiler.PositionedError notGonnaDoIt)
		{
		}
		
		throw (new IllegalArgumentException("What type is " + this.type + "?"));
	}

	class Decoder extends GConstructDecoder
	{
		/**
		 * visits a boolean literal
		 */
		public void visitBooleanLiteral(boolean value)
		{
			GLiteral.this.type = GType.BOOLEAN;
			GLiteral.this.value = String.valueOf(value);
		}
	
		/**
		 * visits a byte literal
		 */
		public void visitByteLiteral(byte value)
		{
			GLiteral.this.type = GType.BYTE;
			GLiteral.this.value = String.valueOf(value);
		}
	
		/**
		 * visits a character literal
		 */
		public void visitCharLiteral(char value)
		{
			GLiteral.this.type = GType.CHAR;
			GLiteral.this.value = String.valueOf(value);
		}
	
		/**
		 * visits a double literal
		 */
		public void visitDoubleLiteral(double value)
		{
			GLiteral.this.type = GType.DOUBLE;
			GLiteral.this.value = String.valueOf(value);
		}
	
		/**
		 * visits a float literal
		 */
		public void visitFloatLiteral(float value)
		{
			GLiteral.this.type = GType.FLOAT;
			GLiteral.this.value = String.valueOf(value);
		}
	
		/**
		 * visits a int literal
		 */
		public void visitIntLiteral(int value)
		{
			GLiteral.this.type = GType.INT;
			GLiteral.this.value = String.valueOf(value);
		}
	
		/**
		 * visits a long literal
		 */
		public void visitLongLiteral(long value)
		{
			GLiteral.this.type = GType.LONG;
			GLiteral.this.value = String.valueOf(value);
		}
	
		/**
		 * visits a short literal
		 */
		public void visitShortLiteral(short value)
		{
			GLiteral.this.type = GType.SHORT;
			GLiteral.this.value = String.valueOf(value);
		}
	
		/**
		 * visits a string literal
		 */
		public void visitStringLiteral(String value)
		{
			GLiteral.this.type = GType.STRING;
			GLiteral.this.value = String.valueOf(value);
		}
	
		/**
		 * visits a null literal
		 */
		public void visitNullLiteral()
		{
			GLiteral.this.type = GType.NULL;
			GLiteral.this.value = String.valueOf(value);
		}
	}

	public String toString()
	{
		return "type(" + this.type + ") value(" + this.value + ")";
	}
}
	
