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
 * A method call expression or statement.  See GNew for the invocation of 
 * constructors. 
 */
public class GMethodCall extends GConstruct implements GExpression, GStatement
{
	static final long serialVersionUID = 2851543116725880123L;
	
	public static final String THIS = "this";
	public static final String SUPER = "super";
	
	/**
	 * The recipient of the method call.  Use a GType as the recipient to call 
	 * a static method.
	 */
	public GExpression recipient;
	
	/**
	 * The method name.
	 */
	public String methodName;
	
	/**
	 * A collection of GExpression.  Will by evaluated against the types of
	 * the method parameters at compile time.
	 */
	public Vector arguments;	
	
	public GMethodCall(GExpression recipient, String methodName)
	{
		super((recipient == null)?"":(recipient.toString() + ".") + methodName + "(...)");
		
		this.recipient = recipient;
		this.methodName = methodName;
		init();
	}
	
	/**
	 * Convenience constructor for dynamic code generation: recipient is initialized
	 * to null.  
	 */
	public GMethodCall(String methodName)
	{
		this((GExpression)null, methodName);
	}
	
	/**
	 * Call a static method on the class referred to by fully qualified
	 * <code>classname</code>.
	 */
	public GMethodCall(String classname, String staticMethodName)	 // is this really right?
	{
		this(new GReference(classname), staticMethodName);
	}
	
	GMethodCall(JExpression expression)
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
		if ((this.methodName == THIS) || (this.methodName == SUPER))
		{
			return (new JConstructorCall(super.tokenReference,
										 (this.methodName == THIS),
										 (this.recipient == null)?null:this.recipient.encodeExpression(),
										 encodeArguments()));
		}
		
		GSourceFile.log("arguments: " + this.arguments);
		
		return (new JMethodCallExpression(super.tokenReference,
										  (this.recipient == null)?null:this.recipient.encodeExpression(), 
										  this.methodName,
										  encodeArguments()));
	}
	
	public JStatement encodeStatement()
	{
		return expressionAsStatement(this);
	}
	
	protected JExpression[] encodeArguments()
	{
		JExpression[] encoded = new JExpression[this.arguments.size()];
		for (int i = 0; i < this.arguments.size(); i++)
		{
			encoded[i] = ((GExpression)this.arguments.elementAt(i)).encodeExpression();
		}
		return encoded;
	}
	
	public static GMethodCall report(String report)
	{
		return report(new GLiteral(GType.STRING, report));
	}
	
	public static GMethodCall report(GExpression sayWhat)
	{
		GMethodCall reportCall = new GMethodCall("java/lang/System/err", "println");
		reportCall.arguments.add(sayWhat);
		return reportCall;
	}

	class Decoder extends GConstructDecoder
	{
		/**
		 * visits a method call expression
		 */
		public void visitMethodCallExpression(JMethodCallExpression self,
											  JExpression prefix,
											  String ident,
											  JExpression[] args)
		{
			GMethodCall.this.recipient = GDecoder.decode(prefix);
			GMethodCall.this.methodName = ident;
			
			for (int i = 0; i < args.length; i++)
			{
				GMethodCall.this.arguments.add(GDecoder.decode(args[i]));
			}
		}

		/**
		 * visits a constructor call invoked via 'this' or 'super'
		 */
		public void visitConstructorCall(JConstructorCall self,
										 boolean functorIsThis,
										 JExpression[] params)
		{
			if (functorIsThis)
			{
				GMethodCall.this.methodName = THIS;
			}
			else
			{
				GMethodCall.this.methodName = SUPER;
			}
			
			for (int i = 0; i < params.length; i++)
			{
				GMethodCall.this.arguments.add(GDecoder.decode(params[i]));
			}
		}
	}

	public String toString()
	{
		return "calling(" + this.methodName + ") on(" + this.recipient + ") with(" + this.arguments + ")";
	}
}
