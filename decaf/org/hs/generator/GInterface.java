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
 * A reference to an interface by classname.  Methods, fields and inner 
 * classes are applied to the members of the superclass GTypeDeclaration.
 */
public class GInterface extends GTypeDeclaration
{
	static final long serialVersionUID = -3265063521342418753L;

	/**
	 * Convenience constructor for an interface.
	 */
	public GInterface(String name)
	{
		super(name);
	}
	
	/**
	 * Convenience constructor for a derived interface.
	 */
	public GInterface(String name, String superInterface)
	{
		super(name, superInterface);
	}
	
	GInterface(JTypeDeclaration declaration)
	{
		super(declaration);
		declaration.accept(new Decoder());
	}
	
	public JTypeDeclaration encode()
	{
		return new JInterfaceDeclaration(super.tokenReference,
										 super.modifiers,
										 super.name,
										 CTypeVariable.EMPTY,
										 CReferenceType.EMPTY,
										 super.encodeFields(),
										 super.encodeMethods(),
										 super.encodeInnerClasses(),
										 new JPhylum[0],
										 super.javadoc,
										 super.encodeComments());
	}

	class Decoder extends TypeDeclarationDecoder
	{
		/**
		 * visits an interface declaration
		 */
		public void visitInterfaceDeclaration(JInterfaceDeclaration self,
											  int modifiers,
											  String ident,
											  CReferenceType[] interfaces,
											  JPhylum[] body,
											  JMethodDeclaration[] methods)
		{
			GInterface.super.modifiers = modifiers;
			GInterface.super.name = ident;
			
			if (interfaces.length > 0)
			{
				GInterface.super.setSuperClass(interfaces[0].toString());
			}
			
			GMethod method;
			for (int i = 0; i < methods.length; i++)
			{
				method = new GMethod(methods[i]);
				GInterface.this.methods.add(method);
			}
		}
	}
}
