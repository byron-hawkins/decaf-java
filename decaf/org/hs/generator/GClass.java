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
 * A class declaration.
 */
public class GClass extends GTypeDeclaration implements Comparable
{
	static final long serialVersionUID = 1048473698670051630L;

	/**
	 * A collection of java.lang.String representing the fully qualified
	 * classnames of the interfaces implemented by this GClass.
	 */
	public TreeSet interfaces;			
	
	/**
	 * A collection of GMethod representing the class initializers of this GClass.
	 */
	public Vector classInits;
	
	public GClass(String name)
	{
		this(name, null);
	}
	
	public GClass(String name, String superClass)
	{
		super(name, superClass);
		init();
	}
	
	protected GClass()
	{
		super();
		init();
	}
	
	GClass(JTypeDeclaration declaration)
	{
		super(declaration);
		init();
		declaration.accept(new Decoder());
	}
	
	private void init()
	{
		this.interfaces = new TreeSet();
		this.classInits = new Vector();
	}
	
	public JTypeDeclaration encode()
	{
		JFieldDeclaration[] encodedFields = super.encodeFields();
		return (new JClassDeclaration(super.tokenReference,
									  super.modifiers,
									  super.name,
									  CTypeVariable.EMPTY,
									  //(this.superClass == null)?null:CReferenceType.lookup(this.superClass.replace('.','/')),
									  (this.superClass == null)?null:this.superClass.getClassnameType(),
									  encodeInterfaces(),
									  encodedFields,
									  super.encodeMethods(),
									  super.encodeInnerClasses(),
									  encodeClassInits(encodedFields),
									  super.javadoc,
									  super.encodeComments()));
	}
	
	public CReferenceType[] encodeInterfaces()
	{
		CReferenceType[] encoded = new CReferenceType[this.interfaces.size()];
		int i = 0;
		Iterator iterator = this.interfaces.iterator();
		while (iterator.hasNext())
		{
			encoded[i++] = CReferenceType.lookup(((String)iterator.next()).replace('.','/')); 
		}
		
		return encoded;
	}
	
	protected JPhylum[] encodeClassInits(JFieldDeclaration[] encodedFields)
	{
		int classInitsSize = this.classInits.size();
		int bodySize = classInitsSize + encodedFields.length;
		JPhylum[] encoded = new JPhylum[bodySize];
		
		for (int i = 0; i < classInitsSize; i++)
		{
			encoded[i] = ((GClassInit)this.classInits.elementAt(i)).encodeStatement();
		}
		
		for (int i = classInitsSize; i < bodySize; i++)
		{
			encoded[i] = encodedFields[i-classInitsSize];
		}
		
		return encoded;
	}
	
	public GNew createNew()
	{
		return (new GNew(this.name));
	}
	
	public GType type()
	{
		return (new GType(this.name));
	}

	class Decoder extends TypeDeclarationDecoder
	{
		/**
		 * visits a class declaration
		 */
		public void visitClassDeclaration(JClassDeclaration self,
										  int modifiers,
										  String ident,
										  CTypeVariable[] typeVariables,
										  CReferenceType superClass,
										  CReferenceType[] interfaces,
										  JPhylum[] body,
										  JMethodDeclaration[] methods,
										  JFieldDeclaration[] fields,
										  JTypeDeclaration[] decls)
		{
			GClass.super.modifiers = modifiers;
			GClass.super.name = ident;
			GClass.super.setSuperClass(superClass);
			
			for (int i = 0; i < interfaces.length; i++)
			{
				GClass.this.interfaces.add(interfaces[i].toString());
			}
			
			GConstruct method;
			for (int i = 0; i < methods.length; i++)
			{
				method = new GMethod(methods[i]);
				GClass.this.methods.add(method);
			}
			
			GVariable field;
			for (int i = 0; i < fields.length; i++)
			{
				field = new GVariable(fields[i]);
				GClass.this.fields.add(field);
			}
			
			GTypeDeclaration inner;
			for (int i = 0; i < decls.length; i++)
			{
				inner = GTypeDeclaration.decode(decls[i]);
				GClass.this.innerClasses.add(inner);
			}
			
			for (int i = 0; i < body.length; i++)
			{
				if (body[i] instanceof JClassBlock)
				{
					GClass.this.classInits.add(new GClassInit((JClassBlock)body[i]));
				}
			}
		}
	
		/**
		 * visits a class declaration
		 */
		public void visitInnerClassDeclaration(JClassDeclaration self,
											   int modifiers,
											   String ident,
											   String superClass,
											   CReferenceType[] interfaces,
											   JTypeDeclaration[] decls,
											   JPhylum[] body,
											   JMethodDeclaration[] methods)
		{
			GClass.super.modifiers = modifiers;
			GClass.super.name = ident;
			GClass.super.setSuperClass(superClass);
			
			for (int i = 0; i < interfaces.length; i++)
			{
				GClass.this.interfaces.add(interfaces[i].toString());
			}
			
			GConstruct method;
			String name;
			for (int i = 0; i < methods.length; i++)
			{
				method = new GMethod(methods[i]);
				GClass.this.methods.add(method);
			}
			
			GTypeDeclaration inner;
			for (int i = 0; i < decls.length; i++)
			{
				inner = GTypeDeclaration.decode(decls[i]);
				GClass.this.innerClasses.add(inner);
			}
		}
	}
	
	public int compareTo(Object o)
	{
		return -1;
		
		//GClass other = (GClass)o;
	}

	/*	
	public boolean equals(Object o)
	{
		if (!(o instanceof GClass))
		{
			return false;
		}
		
		return (compareTo(o) == 0);
	}
	*/
	
	public String toString()
	{
		return super.name;
	}
}
