/*
 * Copyright (C) 2003 HawkinsSoftware
 *
 * This prototype of the Decaf Java development environment is free 
 * software.  You can redistribute it and/or modify it under the terms 
 * of the GNU General Public License as published by the Free Software 
 * Foundation.  However, no compilation of this code or a derivative
 * of it may be used with or integrated into any commercial application,
 * except by the written permisson of HawkinsSoftware.  Future versions 
 * of this product will be sold commercially under a different license.  
 * HawkinsSoftware retains all rights to this product, including its
 * concepts, design and implementation.
 *
 * This prototype is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
 
package com.bitwise.decaf.editor;

import java.util.*;
import java.lang.reflect.Constructor;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.text.*;

import org.hs.generator.*;

import at.dms.kjc.*;

import com.bitwise.decaf.editor.config.TypeDetails;
import com.bitwise.decaf.editor.config.MethodDetails;
import com.bitwise.decaf.editor.config.FieldDetails;

public class BinaryType extends GType implements Type, Typed
{
	static final long serialVersionUID = 765854277677526998L;

	protected Class type;
	protected String name;
	protected String displayName;
	protected String qualifiedName;
	protected String discussion;
	
	protected transient Vector methods;
	protected transient Vector fields;
	
	protected transient MethodsByName methodsByName;
	protected transient FieldsByName fieldsByName;
	protected transient Collection constructors;
	
	// protected transient TypeConfig ...?

	public BinaryType(Class type)
	{
		this(type, 0);
	}
	
	public BinaryType(Class type, String displayName)
	{
		this(type, parseName(type), displayName, 0);
	}
	
	public BinaryType(Class type, int dimensions)
	{
		this(type, parseName(type), parseName(type), dimensions);
	}
	
	public BinaryType(Class type, String name, String displayName, int dimensions)
	{
		super(type.getName(), dimensions);
		
		this.type = type;
		this.name = name;
		this.displayName = parseArrayNotation(displayName);
		this.qualifiedName = parseArrayNotation(type.getName());
		this.dimensions = dimensions;
		
		for (int i = 0; i < dimensions; i++)
		{
			this.displayName += "[]";
		}
	}
	
	private static String parseName(Class type)
	{
		String classname = type.getName();
		int dot = classname.lastIndexOf(".");
		if (dot > 0)
		{
			return classname.substring(dot+1, classname.length());
		}
		else
		{
			return classname;
		}
	}
	
	private static String parseArrayNotation(String typeName)
	{
		if (typeName.startsWith("["))
		{
			switch (typeName.charAt(1))
			{
				case 'B': typeName = "byte"; break;
				case 'C': typeName = "char"; break;
				case 'D': typeName = "double"; break;
				case 'F': typeName = "float"; break;
				case 'I': typeName = "int"; break;
				case 'J': typeName = "long"; break;
				case 'S': typeName = "short"; break;
				case 'Z': typeName = "boolean"; break;
				case 'L': 
					typeName = typeName.substring(1, typeName.length()-1);
					break;
			}
			typeName += "[]";
		}
		
		return typeName;
	}
	
	void init()
	{
		this.apply(DecafEditor.getTypeDetails(this.type.getName()));
	}
	
	public void apply(TypeDetails details)
	{
		// use all methods/fields?
		// use only declared?
		// use declared + noted?
		boolean declaredMethodsRequireDetails = true;
		boolean superMethodsRequireDetails = true;	
		
		if (details != null)
		{
			this.displayName = details.getUid();
			this.discussion = details.getDiscussion();
			declaredMethodsRequireDetails = details.declaredMethodsRequireDetails();
			superMethodsRequireDetails = details.superMethodsRequireDetails();
		}
		
		this.methods = new Vector();
		this.methodsByName = new MethodsByName();
		this.addMethods(this.type.getDeclaredMethods(), details, declaredMethodsRequireDetails);
		Class superType = this.type.getSuperclass();
		if (superType != null)
		{
			this.addMethods(superType.getMethods(), details, superMethodsRequireDetails);
		}
		
		this.constructors = new TreeSet(new Method.Comparator());
		this.addConstructors(this.type.getDeclaredConstructors(), details, declaredMethodsRequireDetails);
		if (superType != null)
		{
			this.addConstructors(superType.getConstructors(), details, superMethodsRequireDetails);
		}
		
		this.fields = new Vector();
		BinaryField field;
		FieldDetails fieldDetails;
		java.lang.reflect.Field[] binaryFields = this.type.getFields();
		this.fieldsByName = new FieldsByName();
		for (int i = 0; i < binaryFields.length; i++)
		{
			if (!java.lang.reflect.Modifier.isPublic(binaryFields[i].getModifiers()))
			{
				continue;
			}
			
			field = new BinaryField(binaryFields[i]);
			if (details != null)
			{
				fieldDetails = details.getFieldDetails(field);
				if ((fieldDetails != null) && fieldDetails.hide())
				{
					continue;
				}
				field.apply(fieldDetails, details.hasJavadoc());
			}
			this.fieldsByName.add(field);
		}
	}

	private void addConstructors(java.lang.reflect.Constructor[] binaryConstructors, TypeDetails typeDetails, boolean requiresDetails)
	{
		BinaryMethod constructor;
		MethodDetails constructorDetails;
		for (int i = 0; i < binaryConstructors.length; i++)
		{
			if (!java.lang.reflect.Modifier.isPublic(binaryConstructors[i].getModifiers()))
			{
				continue;
			}
			
			constructor = new BinaryMethod(this, binaryConstructors[i]);
			if (typeDetails != null)
			{
				constructorDetails = typeDetails.getMethodDetails(constructor);
				if (constructorDetails == null) 
				{
					if (requiresDetails)
					{
						continue;
					}
				}
				else if (constructorDetails.hide())
				{
					continue;
				}
				constructor.apply(constructorDetails, typeDetails.hasJavadoc());
			}
			this.constructors.add(constructor);
		}
	}

	private void addMethods(java.lang.reflect.Method[] binaryMethods, TypeDetails typeDetails, boolean requiresDetails)
	{
		BinaryMethod method;
		MethodDetails methodDetails;
		for (int i = 0; i < binaryMethods.length; i++)
		{
			if (!java.lang.reflect.Modifier.isPublic(binaryMethods[i].getModifiers()))
			{
				continue;
			}
			
			method = new BinaryMethod(binaryMethods[i]);
			if (typeDetails != null)
			{
				methodDetails = typeDetails.getMethodDetails(method);
				if (methodDetails == null)
				{
					if (requiresDetails) 
					{
						continue;
					}
				}
				else if (methodDetails.hide())
				{
					continue;
				}
				method.apply(methodDetails, typeDetails.hasJavadoc());
			}
		
			this.methodsByName.add(method);
		}
	}

	public String getDiscussion()
	{
		return this.discussion;
	}
	
	public Type getType()
	{
		return this;
	}
	
	public GType typeSource()
	{
		return this;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public String getFilename()
	{
		return null;
	}

	public String getDisplayName()
	{
		return this.displayName;
	}
	
	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}
	
	public String getQualifiedName()
	{
		return this.qualifiedName;
	}
	
	public boolean isPrimitive()
	{
		return false;
	}
	
	public boolean isAssignableFrom(Type o)
	{
		if (o instanceof BinaryType)
		{
			BinaryType other = (BinaryType)o;
			if (!this.type.isAssignableFrom(other.type))
			{
				return false;
			}
			
			return (this.dimensions == other.dimensions);
		}
		
		if (o instanceof PrimitiveType)
		{
			return false;
		}
		
		return getQualifiedName().equals(o.getQualifiedName());
	}
	
	public Collection getMethods()
	{
		return this.methodsByName.all();
	}
	
	public Collection getMethods(String name)
	{
		return this.methodsByName.get(name);
	}
	
	public Method getMethod(String name, Type[] parameters)
	{
		return this.methodsByName.get(name, parameters);
	}
	
	public Collection getConstructors()
	{
		return this.constructors;
	}
	
	public Collection getFields()
	{
		return this.fieldsByName.all();
	}
	
	public Variable getField(String name)
	{
		return this.fieldsByName.get(name);
	}
	
	protected Object readResolve()
		throws java.io.ObjectStreamException
	{
		this.init();
		return super.readResolve();
	}
	
	public class BinaryField extends BinaryVariable implements Field
	{
		protected String discussion;
		
		public BinaryField(java.lang.reflect.Field field)
		{
			super(field);
			
			super.modifiers = field.getModifiers();
			super.name = field.getName(); 
			this.discussion = null;
		}
		
		public void apply(FieldDetails details, boolean hasJavadoc)
		{
			if (details != null)
			{
				this.discussion = details.getDiscussion();
				hasJavadoc = hasJavadoc;
			}
		}
		
		public String getDiscussion()
		{
			return this.discussion;
		}
	}
}