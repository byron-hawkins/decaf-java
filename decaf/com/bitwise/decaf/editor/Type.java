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

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.text.*;

import org.hs.generator.*;

import at.dms.kjc.*;

import com.bitwise.decaf.editor.config.TypeDetails;

/**
 * A Decaf UI representation of a <code>class</code>.  All types are representatives 
 * of a {@link GType}, available with {@link #typeSource()}.  A type may bear details,
 * i.e., documentation provided by the configuration file, which specified a display 
 * name, and optionally a discussion of the content and purpose of the type.
 */
public interface Type extends Comparable, Configurable, com.bitwise.decaf.editor.grammar.Grammar.ClassReference
{
	static Cache cache = new Cache();

	/**
	 * All types 
	 */
	public GType typeSource();
	public String getDisplayName();
	
	/**
	 * If this type has been detailed in the configuration file, the display name
	 * will be taken from those details.  Otherwise it will be the same as the classname.
	 */
	public void setDisplayName(String displayName);
	
	/**
	 * Returns the filename to which this type corresponds, or null if this type was constructed
	 * from a binary source.
	 */
	public String getFilename();
	
	/**
	 * Returns the short name of the <code>class</code> represented by this type.  
	 */
	public String getName();
	
	/**
	 * Returns the fully qualified classname of the <code>class</code> represented by this type.  
	 */
	public String getQualifiedName();
	
	/**
	 * Returns true if this is a primitive type, and not a subclass of {@link java.lang.Object}.
	 */
	public boolean isPrimitive();
	
	/**
	 * Returns true if an instance of <code>other</code> can legally be used when 
	 * an instance of this type is required.  
	 */
	public boolean isAssignableFrom(Type other);
	
	/**
	 * Returns a collection of {@link Method}s corresponding to the methods of this type.
	 */
	public Collection getMethods();
	
	/**
	 * Returns a collection of the <code>name</code>sake {@link Method}s of this type,
	 * or an empty collection if no such methods exist.
	 */
	public Collection getMethods(String name);
	
	/**
	 * Returns a particular {@link Method} of this type, or null if it doesn't exist.
	 */
	public Method getMethod(String name, Type[] parameters);
	
	/**
	 * Returns a collection of the constructors of this type, each represented as a {@link Method}.
	 */
	public Collection getConstructors();
	
	/**
	 * Returns a collection of the fields of this type, each represented as a {@link Variable}.
	 */
	public Collection getFields();
	
	/**
	 * Returns the field of this type with the specified <code>name</code>, or null if 
	 * there is no such field.
	 */
	public Variable getField(String name);
	
	/**
	 * Extract the discussion and display name for this type from <code>details</code>
	 */
	public void apply(TypeDetails details);
	
	/**
	 * Internal: a map of methodName::{@link java.util.Collection}, in which
	 * the collection contains all the methods with the given name
	 * (each with a unique signature).  
	 */
	class MethodsByName extends TreeMap 
	{
		protected TreeSet all;
		
		public MethodsByName()
		{
			super();
			
			this.all = new TreeSet(new Method.Comparator());
		}
		
		public Collection get(String name)
		{
			Collection methods = (Collection)super.get(name);
			if (methods == null)
			{
				return (new Vector());
			}
			return methods;
		}
		
		public void add(Method method)
		{
			this.all.add(method);
			
			Collection methods = get(method.getName());
			if (methods == null)
			{
				methods = new Vector();
				super.put(method.getName(), methods);
			}
			methods.add(method);
		}
		
		public Method get(String name, Type[] parameterTypes)
		{
			Method method;
			Iterator byName = this.get(name).iterator();
			Collection parameters;
			Iterator checkType;
			Method.Parameter parameter;
			int parameterIndex;
method:		while (byName.hasNext())
			{
				method = (Method)byName.next();
				parameters = method.getParameters();
				if (parameters.size() != parameterTypes.length)
				{
					continue;
				}
				checkType = parameters.iterator();
				parameterIndex = 0;
				while (checkType.hasNext())
				{
					parameter = (Method.Parameter)checkType.next();
					if (!parameter.getType().isAssignableFrom(parameterTypes[parameterIndex++]))
					{
						continue method;
					}
				}
				
				return method;
			}
			
			return null;
		}
		
		public Collection all()
		{
			return this.all;
		}
	}

	/**
	 * Internal: a map of fieldName:field, where field
	 * is a {@link Variable}.
	 */	
	class FieldsByName extends TreeMap
	{
		public Variable get(String name)
		{
			return (Variable)super.get(name);
		}
		
		public void add(Variable field)
		{
			super.put(field.getName(), field);
		}
		
		public void remove(Variable field)
		{
			super.remove(field.getName());
		}

		public Collection all()
		{
			return super.values();
		}
	}

	/**
	 * Internal: Cache of all {@link Type}s in the system.  Types are not maintained
	 * as singletons, but any call to resolve a classname, {@link java.lang.Class}, etc.
	 * will be routed to this master table.  It is organized as a map of {@link CacheKey}:{@link Type}.
	 */
	static class Cache extends TreeMap
	{
		public Type get(Class type)
		{
			return (Type)super.get(new CacheKey(type));
		}
	
		public Type get(String classname)
		{
			return (Type)super.get(new CacheKey(classname));
		}
		
		public Type get(Class type, int dimensions)
		{
			return (Type)super.get(new CacheKey(type, dimensions));
		}
		
		public void put(Class key, Object value)
		{
			super.put(new CacheKey(key), value);
		}
		
		public void put(Class key, int dimensions, Object value)
		{
			super.put(new CacheKey(key, dimensions), value);
		}
	}

	/**
	 * Internal: key class for the {@link TypeCache}, comprised of the type's classname
	 * and its array dimensions.
	 */	
	static class CacheKey implements Comparable
	{
		protected int dimensions;
		protected String classname;
		
		public CacheKey(Class type)
		{
			this(type.getName(), 0);
		}
		
		public CacheKey(String classname)
		{
			this(classname, 0);
		}
		
		public CacheKey(Class type, int dimensions)
		{
			this(type.getName(), dimensions);
		}
		
		public CacheKey(String classname, int dimensions)
		{
			this.classname = classname;
			this.dimensions = dimensions;
		}
		
		public int compareTo(Object o)
		{
			CacheKey other = (CacheKey)o;
			
			int result = this.classname.compareTo(other.classname);
			if (result != 0)
			{
				return result;
			}
			
			return (this.dimensions - other.dimensions);
		}
		
		public boolean equals(Object o)
		{
			CacheKey other = (CacheKey)o;

			return (this.classname.equals(other.classname) && (this.dimensions == other.dimensions));
		}
	}

	/**
	 * A {@link Type} that represents a user interface state in which the user
	 * has neglected to set a type on a particular field, or has removed a type 
	 * without replacing it.  Decaf does not apply any default for types (except
	 * in the case of the method return type, which defaults to {@link VoidType}).  
	 */	
	static class NoType extends GType implements Type
	{
		public NoType()
		{
			super(GType.UNKNOWN);
		}
		
		public NoType(GType root)
		{
			super(root);
		}
		
		public void apply(TypeDetails details)
		{
		}
		
		public String getDiscussion()
		{
			return "Discussion of Utils.noType";
		}

		public GType typeSource()
		{
			return this;
		}
		
		public String getDisplayName()
		{
			return "none";
		}
		
		public void setDisplayName(String displayName)
		{
		}
		
		public String getFilename()
		{
			return null;
		}
		
		public String getName()
		{
			return "none";
		}
		
		public String getQualifiedName()
		{
			return "none";
		}
		
		public boolean isPrimitive()
		{
			return true;
		}
		
		public boolean isAssignableFrom(Type other)
		{
			return false;
		}
		
		public Collection getMethods()
		{
			return Utils.empty_vector;
		}
		
		public Collection getMethods(String name)
		{
			return Utils.empty_vector;
		}

		public Method getMethod(String name, Type[] parameters)
		{
			return null;
		}
		
		public Collection getConstructors()
		{
			return null;
		}
		
		public Collection getFields()
		{
			return Utils.empty_vector;
		}
		
		public Variable getField(String name)
		{
			return null;
		}
	}

	/**
	 * A {@link Type} to represents the Java keyword <code>void</code>.
	 */	
	static class VoidType extends NoType
	{
		public VoidType()
		{
			super(GType.VOID);
		}

		public GType typeSource()
		{
			return this;
		}
		
		public String getDisplayName()
		{
			return "void";
		}
		
		public String getName()
		{
			return "void";
		}
	}

	/**
	 * A {@link Type} to represents the Java keyword <code>null</code>.
	 */	
	static class NullType extends NoType
	{
		public NullType()
		{
			super(GType.NULL);
		}

		public GType typeSource()
		{
			return this;
		}
		
		public String getDisplayName()
		{
			return "null";
		}
		
		public String getName()
		{
			return "null";
		}
	}

	/**
	 * A {@link Variable} that is designated by convention to be a class field,
	 * and therefore is {@link Configurable} with documentation.
	 */
	public static interface Field extends Variable, Configurable
	{
	}
}