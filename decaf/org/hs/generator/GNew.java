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
 * An constructor invocation through the keyword 'new', by classname and optionally
 * including a subclass definition (in the case of an anonymous instance creation).
 * The <code>prefix</code> refers to the enclosing class of a public inner class.
 * There are funny rules involving the <code>classnaem</code> and <code>prefix</code>:
 * <p>
 * 1) If you use the prefix, you must provide it as an expression, and it must be
 * fully qualified in whatever way you identify it.  The classname itself in this case 
 * is not qualified at all; so to create an instance of a java.lang.Character.Subset,
 * use a <code>prefix</code> value to represent java.lang.Character, and the 
 * <code>classname</code> to represent Subset.<br>
 * 2) If you don't use the prefix, then the classname should be fully qualified; so to
 * create in instance of java.lang.Character, supply the same in its entirety as the
 * <code>classname</code>.
 */
public class GNew extends GConstruct implements GExpression, GStatement
{
	static final long serialVersionUID = -2883670867619199980L;

	/**
	 * The classname of the class to create an instance of; use the fully qualified
	 * classname when <code>prefix</code> is null, or use only the short classname when
	 * <code>prefix</code> is non-null (it specifies the enclosing class).
	 */
	public String classname;
	protected CReferenceType type;
	
	/**
	 * The arguments to the constructor, in the form of GExpression.  
	 * Types will be checked at compile time.
	 */
	public Vector arguments;			
	
	/**
	 * A reference to the enclosing class of the class to instantiate.
	 */
	public GExpression prefix;
	
	/**
	 * The subclass definition in the case of an anonymous instance creation; null 
	 * indicates that this GNew is not instantiating an anonymous class.
	 * 
	 */
	public GClass body;

	/**
	 * If you find the above to be confusing, just use this constructor with the 
	 * fully qualified classname and you'll get an ordinary constructor call to it.  
	 */
	public GNew(String classname)
	{
		super(classname);
		init();
	
		this.classname = classname.replace('.','/');
		this.type = new CClassNameType(this.classname, false);
		this.prefix = null;
		this.body = null;
	}

	GNew(JExpression expression)
	{
		super(expression);
		init();
		expression.accept(new Decoder());
	}
	
	private void init()
	{
		this.arguments = new Vector();
	}
	
	public JExpression encodeExpression()
	{
		if (this.body == null)
		{
			if (this.prefix == null)
			{
				return (new JUnqualifiedInstanceCreation(super.tokenReference,
														 this.type,
														 encodeArguments()));
			}
			else
			{
				return (new JQualifiedInstanceCreation(super.tokenReference,
													   this.prefix.encodeExpression(),
													   this.classname,
													   encodeArguments()));
			}
		}
		else
		{
			if (this.prefix == null)
			{
				return (new JUnqualifiedAnonymousCreation(super.tokenReference,
														  this.type,
														  encodeArguments(),
														  (JClassDeclaration)this.body.encode()));
			}
			else
			{
				return (new JQualifiedAnonymousCreation(super.tokenReference,
													    this.prefix.encodeExpression(),
													    this.classname,
														encodeArguments(),
														(JClassDeclaration)this.body.encode()));
			}
		}
	}
	
	public JStatement encodeStatement()
	{
		return expressionAsStatement(encodeExpression());
	}

	public JExpression[] encodeArguments()
	{
		JExpression[] encoded = new JExpression[this.arguments.size()];
		
		for (int i = 0; i < this.arguments.size(); i++)
		{
			encoded[i] = ((GExpression)this.arguments.elementAt(i)).encodeExpression();
		}
		
		return encoded;
	}
	
	class Decoder extends GConstructDecoder
	{
		/**
		 * Visits an unqualified anonymous class instance creation expression.
		 */
		public void visitUnqualifiedAnonymousCreation(JUnqualifiedAnonymousCreation self,
													  CReferenceType type,
													  JExpression[] params,
													  JClassDeclaration decl)
		{
			decode(null, null, type, params, decl);
		}
	
	
		/**
		 * Visits an unqualified instance creation expression.
		 */
		public void visitUnqualifiedInstanceCreation(JUnqualifiedInstanceCreation self,
													 CReferenceType type,
													 JExpression[] params)
		{
			decode(null, null, type, params, null);
		}
		/**
		 * Visits an unqualified anonymous class instance creation expression.
		 */
		public void visitQualifiedAnonymousCreation(JQualifiedAnonymousCreation self,
													JExpression prefix,
													String ident,
													JExpression[] params,
													JClassDeclaration decl)
		{
			decode(prefix, ident, null, params, decl);
		}
	
		/**
		 * Visits an unqualified instance creation expression.
		 */
		public void visitQualifiedInstanceCreation(JQualifiedInstanceCreation self,
												   JExpression prefix,
												   String ident,
												   JExpression[] params)
		{
			decode(prefix, ident, null, params, null);
		}

		protected void decode(JExpression prefix, 
							  String classname, 
							  CReferenceType type, 
							  JExpression[] arguments, 
							  JClassDeclaration decl)
		{
			if (prefix != null)
			{
				GNew.this.prefix = GDecoder.decode(prefix);
			}
			GNew.this.classname = (classname == null)?null:classname.replace('.','/');
			
			String typeName = (classname == null)?type.toString().replace('.','/'):classname;
			boolean binary = !(typeName.equals(GSourceFile.s_compilingClassname) || typeName.equals(GSourceFile.s_compilingShortName));
			
			GNew.this.type = new CClassNameType(typeName, binary);
			
			for (int i = 0; i < arguments.length; i++)
			{
				GNew.this.arguments.add(GDecoder.decode(arguments[i]));
			}
			
			if (decl != null)
			{
				GNew.this.body = new GClass(decl);
			}
		}
	}

	public String toString()
	{
		return "prefix(" + this.prefix + ") type(" + this.type + ") classname(" + this.classname + ") arguments(" + this.arguments + ") body(" + this.body + ")";
	}
}
