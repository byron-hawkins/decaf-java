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
import at.dms.compiler.*;

import java.util.*;

/**
 * A method definition.
 */
public class GMethod extends GConstruct implements Comparable
{
	static final long serialVersionUID = 4423803251661280777L;

	/**
	 * The method modifiers, consistent with java.lang.reflect.Modifier
	 */
	public int modifiers;
	
	/**
	 * The method return type; may not be null: use GType.VOID.
	 */
	public GType returnType;
	
	/**
	 * The method name.
	 */
	public String name;
	
	/**
	 * The parameters, a collection of GVariable with the anticipated conditions:
	 * <p>
	 * 1) GVariable.isLocal is true<br>
	 * 2) GVariable.initializer is null<br>
	 * 3) GVariable.prefix is null<br>
	 * <p>
	 * GVariable.modifiers will be ignored.
	 */
	public Vector parameters;	
	
	/**
	 * The excpetions thrown by the method, represented here by qualified classname.
	 */
	public TreeSet exceptions;	
	
	/**
	 * The method body.
	 */
	public GBlock body;

	/**
	 * Create a public void instance method named <code>name</code>.
	 */	
	public GMethod(String name)
	{
		this(0, GType.VOID, name);
	}
	
	/**
	 * Convenience method for the readability of static code generation.
	 */
	public GMethod(int modifiers, GType returnType, String name)
	{
		super(name);
		init();
		this.modifiers = modifiers;
		this.returnType = returnType;
		this.name = name;
	}
	
	public GMethod()
	{
		super("???");
		init();
	}
	
	GMethod (JMethodDeclaration declaration)
	{
		super(declaration);
		init();
		declaration.accept(new Decoder());
	}
	
	private void init()
	{
		this.parameters = new Vector();
		this.exceptions = new TreeSet();
		this.body = new GBlock();
	}
	
	public JMethodDeclaration encode()
	{
		if (this.returnType == GType.CONSTRUCTOR)
		{
			JConstructorBlock constructorBody = new JConstructorBlock(super.tokenReference,
																	  this.body.encodeConstructorCall(),
																	  this.body.encodeStatements());
																	   
			return (new JConstructorDeclaration(super.tokenReference,
										   		this.modifiers,
										   		this.name,
										   		encodeParameters(),
										   		encodeExceptions(),
										   		constructorBody,
										   		super.javadoc,
										   		super.encodeComments(),
										   		GSourceFile.s_typeFactory));
		}
		else
		{
			return (new JMethodDeclaration(super.tokenReference,
										   this.modifiers,
										   CTypeVariable.EMPTY,
										   this.returnType.encode(),
										   this.name,
										   encodeParameters(),
										   encodeExceptions(),
										   (this.body == null)?null:this.body.encodeBlock(),
										   super.javadoc,
										   super.encodeComments()));
		}
	}
	
	protected JFormalParameter[] encodeParameters()
	{
		JFormalParameter[] encoded = new JFormalParameter[this.parameters.size()];
		for (int i = 0; i < this.parameters.size(); i++)
		{
			encoded[i] = ((GVariable)this.parameters.elementAt(i)).encodeParameter();
		}
		return encoded;
	}
	
	// same as in GClass.encodeInterfaces() -- utility somewhere?
	protected CReferenceType[] encodeExceptions()
	{
		CReferenceType[] encoded = new CReferenceType[this.exceptions.size()];
		int i = 0;
		Iterator iterator = this.exceptions.iterator();
		while (iterator.hasNext())
		{
			encoded[i++] = CReferenceType.lookup((String)iterator.next());
		}
		
		return encoded;
	}
	
	class Decoder extends GConstructDecoder
	{
		/**
		 * visits a method declaration
		 */
		public void visitMethodDeclaration(JMethodDeclaration self,
										   int modifiers,
										   CTypeVariable[] typeVariables,
										   CType returnType,
										   String ident,
										   JFormalParameter[] parameters,
										   CReferenceType[] exceptions,
										   JBlock body)
		{
			GMethod.this.modifiers = modifiers;
			GMethod.this.returnType = new GType(returnType);
			GMethod.this.name = ident;
			
			for (int i = 0; i < parameters.length; i++)
			{
				GMethod.this.parameters.add(new GVariable(parameters[i]));
			}
			
			for (int i = 0; i < exceptions.length; i++)
			{
				GMethod.this.exceptions.add(exceptions[i].toString());
			}
				
			if (body == null)
			{
				GMethod.this.body = null;
			}
			else
			{
				GMethod.this.body = new GBlock(body);
			}
		}

		/**
		 * visits a method declaration
		 */
		public void visitConstructorDeclaration(JConstructorDeclaration self,
												int modifiers,
												String ident,
												JFormalParameter[] parameters,
												CReferenceType[] exceptions,
												JConstructorBlock body)
		{
			GMethod.this.modifiers = modifiers;
			GMethod.this.returnType = GType.CONSTRUCTOR;
			GMethod.this.name = ident;
			
			for (int i = 0; i < parameters.length; i++)
			{
				GMethod.this.parameters.add(new GVariable(parameters[i]));
			}
			
			for (int i = 0; i < exceptions.length; i++)
			{
				GMethod.this.exceptions.add(exceptions[i].toString());
			}
				
			GMethod.this.body = new GBlock(body);
		}
	}
	
	public int compareTo(Object o)
	{
		GMethod other = (GMethod)o;
		
		if (this.name == null)
		{
			return (other.name == null)?0:1;
		}
		else if (other.name == null)
		{
			return -1;
		}
		
		int result = this.name.compareTo(other.name);
		if (result == 0)
		{
			if (this.parameters.size() != other.parameters.size())
			{
				return (this.parameters.size() - other.parameters.size());
			}
			
			GVariable mine;
			GVariable others;
			for (int i = 0; i < this.parameters.size(); i++)
			{
				mine = (GVariable)this.parameters.elementAt(i);
				others = (GVariable)other.parameters.elementAt(i);
				result = mine.compareTo(others);
				if (result != 0)
				{
					return result;
				}
			}
		}
		return result;
	}
	
	public boolean equals(Object o)
	{
		return (compareTo(o) == 0);
	}

	public String toString()
	{
		return this.name;
	}
}
