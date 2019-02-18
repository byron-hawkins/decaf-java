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
 * This chameleonic class can represent a local variable, a class field, a
 * parameter, or one of the keywords <code>this, super, class, length</code>.
 * See the instance fields for rules regarding each intended use of GVariable.
 */
public class GVariable extends GConstruct implements GExpression, GStatement, Comparable
{
	static final long serialVersionUID = -434959138234206161L;
	public static final String CLASS = "class";
	
	static GVariable arrayLength(GExpression prefix)
	{
		GVariable arrayLength = new GVariable(GType.LENGTH, "");
		arrayLength.prefix = prefix;
		return arrayLength;
	}

	/**
	 * Construct a reference to the keyword <code>this</code>, unqualified
	 */	
	public static GVariable plainThis()
	{
		return (new GVariable(GType.THIS, null));
	}
	
	/**
	 * Construct a qualified reference to the keyword <code>this</code>, 
	 * using <code>prefix</code> to determine the particular <code>this</code>
	 * to be applied.
	 *
	 * @param prefix prefix to the <code>this</code> expression which determines 
	 * which particular <code>this</code> will be applied.
	 */
	public static GVariable qualifiedThis(GExpression prefix)
	{
		GVariable qthis = new GVariable(GType.THIS, null);
		qthis.prefix = prefix;
		return qthis;
	}
	
	/**
	 * The modifiers of the variable, as defined by java.lang.reflect.Modifier.
	 * Only used in the case that this GVariable represents a class field.
	 */
	public int modifiers;	
	
	/**
	 * Specify whether this GVariable is local to its context or not.  Here are the rules:
	 * <p>
	 * Local:<br>
	 * - local variable
	 * - parameter
	 * <p>
	 * Not local:<br>
	 * - class field
	 * - <code>this</code> expression
	 * - <code>super</code> expression
	 * - <code>class</code> expression
	 * <p>
	 * Ignored for a <code>length</code> expression
	 */
	public boolean isLocal;
	
	/**
	 * The type of this GVariable.  For special keywords, static types apply thus:
	 * <p>
	 * <code>this</code>: GType.THIS<br>
	 * <code>super</code>: GType.SUPER<br>
	 * <code>class</code>: GType.CLASS<br>
	 * <code>length</code>: GType.LENGTH<br>
	 */
	public GType type;
	
	/**
	 * The name of the variable; ignored for special keywords.
	 */
	public String name;
	
	/**
	 * The initializer of the variable; ignored for parameters and special keywords.
	 */
	public GExpression initializer;
	
	/**
	 * The prefix of the variable; applies optionally to the keywords <code>this</code> and
	 * <code>super</code>, is required for the keywords <code>class</code>, <code>length</code>,
	 * and may not be applied to dynamic variables of any kind.
	 */
	public GExpression prefix;

	public GVariable(GType type, String name)
	{
		super(name);
		
		this.modifiers = 0;
		this.isLocal = false;
		this.type = type;
		this.name = name;
		init();
	}
	
	public GVariable()
	{
		super("???");
		init();
	}
	
	GVariable(JStatement statement)
	{
		super(statement);
		init();
		statement.accept(new Decoder());
	}

	GVariable(JExpression expression)
	{
		super(expression);
		init();
		expression.accept(new Decoder());
	}
	
	GVariable(JVariableDefinition variable)
	{
		super(variable);
		init();
		variable.accept(new Decoder());
	}
	
	GVariable(JFormalParameter parameter)
	{
		super(parameter);
		init();
		parameter.accept(new Decoder());
	}
	
	GVariable(JFieldDeclaration field)
	{
		super(field);
		init();
		field.accept(new Decoder());
	}
	
	private void init()
	{
		this.initializer = null;
		this.prefix = null;
		
		translatePrefix();
	}
	
	public void translatePrefix()
	{
		if ((this.type != null) && ((this.type.equals(GType.THIS)) || (this.type.equals(GType.SUPER)) || (this.type.equals(GType.CLASS))))
		{
			if (this.name == null)
			{
				this.prefix = null;
			}
			else
			{
				this.prefix = new GReference(this.name);
			}
		}
		else
		{
			this.prefix = plainThis();	// default
		}
	}
	
	public JVariableDefinition encodeVariable()
	{
		if ((this.type == GType.THIS) || (this.type == GType.SUPER) || (this.type == GType.CLASS))
		{
			throw (new IllegalArgumentException("Can't declare this, super, or class as a variable!"));
		}
		
		return (new JVariableDefinition(super.tokenReference,
										this.modifiers,
										this.type.encode(),
										this.name,
										(this.initializer == null)?null:this.initializer.encodeExpression()));
	}
	
	public JFieldDeclaration encodeField()
	{
		if ((this.type == GType.THIS) || (this.type == GType.SUPER) || (this.type == GType.CLASS))
		{
			throw (new IllegalArgumentException("Can't declare this, super or class as a field!"));
		}
		
		return (new JFieldDeclaration(super.tokenReference,
									  encodeVariable(),
									  super.javadoc,
									  super.encodeComments()));
	}

	public JFormalParameter encodeParameter()
	{
		if ((this.type == GType.THIS) || (this.type == GType.SUPER) || (this.type == GType.CLASS))
		{
			throw (new IllegalArgumentException("Can't declare this, super or class as a parameter!"));
		}
		
		return (new JFormalParameter(super.tokenReference,
									 JLocalVariable.DES_PARAMETER,
									 this.type.encode(),
									 this.name,
									 java.lang.reflect.Modifier.isFinal(this.modifiers)));
	}

	public JExpression encodeExpression()
	{
		JExpression expression;
		
		if (this.type.equals(GType.THIS))
		{
			if (this.prefix == null)
			{
				return (new JThisExpression(super.tokenReference));
			}
			else
			{
				return (new JThisExpression(super.tokenReference, 
											this.prefix.encodeExpression()));
			}
		}
		
		if (this.type.equals(GType.SUPER))
		{
			if (this.prefix == null)
			{
				return (new JSuperExpression(super.tokenReference));
			}
			else
			{
				return (new JSuperExpression(super.tokenReference, 
											 this.prefix.encodeExpression()));
			}
		}
			
		if (this.name.equals(CLASS))
		{
			CReferenceType classnameType = this.type.getClassnameType();
			if (classnameType == null)
			{
				return (new JClassExpression(super.tokenReference,
											 this.type.encode(),
											 this.type.getArrayBound()));
			}
			else
			{
				return (new JClassExpression(super.tokenReference,
											 new JTypeNameExpression(super.tokenReference, classnameType),
											 this.type.getArrayBound()));
			}
		}
		
		if (this.isLocal)
		{
			return (new JLocalVariableExpression(super.tokenReference,
												 encodeVariable()));
		}
		else
		{
			return (new JFieldAccessExpression(super.tokenReference,
	                                		   this.prefix.encodeExpression(),	
	                                		   this.name));
		}
	}
	
	public JStatement encodeStatement()
	{
		if ((this.type == GType.THIS) || (this.type == GType.SUPER) || (this.type == GType.CLASS))
		{
			throw (new IllegalArgumentException("Can't declare this, super or class as a variable!"));
		}
		
		JVariableDefinition[] variables = new JVariableDefinition[] {encodeVariable()};
		return (new JVariableDeclarationStatement(super.tokenReference,
								   			 	  variables,
								   			 	  super.encodeComments()));
	}

	public GExpression reference()
	{
		return (new GReference(this.name));
	}
	
	public GArrayAccess access(GExpression arrayIndex)
	{
		return (new GArrayAccess(this, arrayIndex));
	}
	
	public GArrayAccess access(GExpression[] arrayIndexes)
	{
		GArrayAccess access = new GArrayAccess(this, arrayIndexes[0]);
		for (int i = 1; i < arrayIndexes.length; i++)
		{
			access = new GArrayAccess(access, arrayIndexes[i]);
		}
		return access;
	}

	class Decoder extends GConstructDecoder
	{
		/**
		 * visits a variable declaration statement
		 */
		public void visitVariableDefinition(JVariableDefinition self,
											int modifiers,
											CType type,
											String ident,
											JExpression expr)
		{
			GVariable.this.modifiers = modifiers;
			GVariable.this.isLocal = true;
			GVariable.this.type = new GType(type);
			GVariable.this.name = ident;
			GVariable.this.initializer = GDecoder.decode(expr);
		}
		
		/**
		 * visits a field declaration
		 */
		public void visitFieldDeclaration(JFieldDeclaration self,
										  int modifiers,
										  CType type,
										  String ident,
										  JExpression expr)
		{
			GVariable.this.modifiers = modifiers;
			GVariable.this.isLocal = false;
			GVariable.this.type = new GType(type);
			GVariable.this.name = ident;
			GVariable.this.initializer = GDecoder.decode(expr);
		}

		/**
		 * visits a formal parameter 
		 */
		public void visitFormalParameter(JFormalParameter self,
										  boolean isFinal,
										  CType type,
										  String ident)
		{
			GVariable.this.modifiers = 0 | (isFinal?java.lang.reflect.Modifier.FINAL:0);
			GVariable.this.isLocal = true;	// won't be used
			GVariable.this.type = new GType(type);
			GVariable.this.name = ident;
		}
	
		/**
		 * visits a field expression
		 */
		public void visitFieldExpression(JFieldAccessExpression self,
										 JExpression left,
										 String ident)
		{
			GVariable.this.modifiers = 0;
			GVariable.this.isLocal = false;
			GVariable.this.type = null;
			GVariable.this.name = ident;
			GVariable.this.prefix = GDecoder.decode(left);
		}

		/**
		 * visits a this expression
		 */
		public void visitThisExpression(JThisExpression self,
										JExpression prefix)
		{
			{
				GVariable.this.type = GType.THIS;
				GVariable.this.prefix = GDecoder.decode(prefix);
			}
		}

		/**
		 * visits a super expression
		 */
		public void visitSuperExpression(JSuperExpression self,
								  JExpression prefix)
		{
			GVariable.this.type = GType.SUPER;
			GVariable.this.prefix = GDecoder.decode(prefix);
		}
		
		/**
		 * visits a class expression
		 */
		public void visitClassExpression(JClassExpression self,
										 CType type,
										 JExpression prefix,
										 int bounds)
		{
			if (type == null)
			{
				GVariable.this.type = new GType(prefix);
			}
			else
			{
				GVariable.this.type = new GType(type, bounds);
			}
			GVariable.this.name = CLASS;
		}

		/**
		 * visits an array length expression
		 */
		public void visitArrayLengthExpression(JArrayLengthExpression self,
											   JExpression prefix)
		{
			GVariable.this.type = GType.LENGTH;
			GVariable.this.prefix = GDecoder.decode(prefix); 
		}
	}

	public int compareTo(Object o)
	{
		GVariable other = (GVariable)o;
		
		int result;
		if (this.type == null) 
		{
			if (other.type == null)
			{
				result = 0;
			}
			else
			{
				result = 1;
			}
		}
		else
		{
			if (other.type == null)
			{
				result = -1;
			}
			else
			{
		 		result = this.type.compareTo(other.type);
		 	}
		}
		if (result == 0)
		{
			return this.name.compareTo(other.name);
		}
		return result;
	}
	
	public boolean equals(Object o)
	{
		if (o == null)
		{
			return false;
		}
		
		if (!(o instanceof GVariable))
		{
			return false;
		}
		
		return (compareTo(o) == 0);
	}

	public String toString()
	{
		return this.name;
	}
}
