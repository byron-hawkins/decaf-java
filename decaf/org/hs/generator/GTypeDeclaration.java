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
 * A type declaration; i.e., a class or interface, or an inner class or interface.
 */
public abstract class GTypeDeclaration extends GConstruct
{
	static final long serialVersionUID = 1818082788625813346L;

	/**
	 * The modifiers of the type, as defined in java.lang.reflect.Modifier
	 */
	public int modifiers;
	
	/**
	 * The super type of this type; null implies java.lang.Object for classes
	 * or an absence of superinterface.
	 */
	public GType superClass;
	
	/**
	 * The name of the type.
	 */
	public String name;
	
	/**
	 * A collection of GVariable representing the fields of this type, which are 
	 * expected to have null <code>GVariable.prefix</code> and a value of false for 
	 * <code>GVariable.isLocal</code>.
	 */
	public Vector fields;				
	
	/**
	 * A collection of GMethod and/or GClassInit representing the methods and 
	 * initializers of this class.
	 */
	public Vector methods;		
	
	/**
	 * A collection of GClass representing the inner classes of this type.
	 */
	public Vector innerClasses;		
	
	protected GTypeDeclaration()
	{
		super("???");
		init();
	}
	
	public GTypeDeclaration(String name)
	{
		this(name, null);
	}
	
	public GTypeDeclaration(String name, String superClass)
	{
		super(name);
		init();
		setSuperClass(superClass);
		this.name = name;
	}
	
	GTypeDeclaration(JPhylum phylum)
	{
		super(phylum);
		init();
	}

	protected void setSuperClass(String superClass)
	{
		if (superClass == null)
		{
			this.superClass = null;
		}
		else
		{
			this.superClass = new GType(superClass);
		}
	}
	
	protected void setSuperClass(CReferenceType superClass)
	{
		if (superClass == null)
		{
			this.superClass = null;
		}
		else
		{
			this.superClass = new GType(superClass);
		}
	}
	
	private void init()
	{
		this.fields = new Vector();
		this.methods = new Vector();
		this.innerClasses = new Vector();
	}
	
	public JFieldDeclaration[] encodeFields()
	{
		GSourceFile.log("encoding fields " + this.fields);
		
		JFieldDeclaration[] encoded = new JFieldDeclaration[this.fields.size()];
		int i = 0;
		Iterator iterator = this.fields.iterator();
		while (iterator.hasNext())
		{
			encoded[i++] = ((GVariable)iterator.next()).encodeField();
		}
		
		return encoded;
	}
	
	public JMethodDeclaration[] encodeMethods()
	{
		JMethodDeclaration[] encoded = new JMethodDeclaration[this.methods.size()];
		int i = 0;
		Iterator iterator = this.methods.iterator();
		GConstruct next;
		while (iterator.hasNext())
		{
			next = (GConstruct)iterator.next();
			encoded[i++] = ((GMethod)next).encode();
		}
		
		return encoded;
	}
	
	public JTypeDeclaration[] encodeInnerClasses()
	{
		JTypeDeclaration[] encoded = new JTypeDeclaration[this.innerClasses.size()];
		int i = 0;
		Iterator iterator = this.innerClasses.iterator();
		while (iterator.hasNext())
		{
			encoded[i++] = ((GTypeDeclaration)iterator.next()).encode();
		}
		
		return encoded;
	}
	
	public abstract JTypeDeclaration encode();

	public static GTypeDeclaration decode(JTypeDeclaration declaration)
	{
		if (declaration instanceof JClassDeclaration)
		{
			return (new GClass(declaration));
		}
		else if (declaration instanceof JInterfaceDeclaration)
		{
			return (new GInterface(declaration));
		}
		
		throw (new IllegalArgumentException("What kind of JTypeDeclaration is a " + declaration.getClass().getName()));
	}

	class TypeDeclarationDecoder extends GConstructDecoder
	{
	}

	public String toString()
	{
		return "modifiers(" + this.modifiers + ") super(" + this.superClass + ") name(" + this.name + ") fields(" + this.fields + ") methods(" + this.methods + ") inners(" + this.innerClasses + ")";
	}
}
