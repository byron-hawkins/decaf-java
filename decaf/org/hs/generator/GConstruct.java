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
import java.lang.reflect.*;

import org.hs.util.*;

/**
 * The base class of all code constructs in this package, GConstruct allows
 * comments to be attached to it, and provides facilities for copying and 
 * convenient reflection access.
 */
public abstract class GConstruct implements PhylumListener, java.io.Serializable, Copy, Cloneable
{
	static final long serialVersionUID = 1883014599835943L;

	public transient TokenReference tokenReference;
	public TreeSet comments; 	// GComment
	public transient JavadocComment javadoc;
	
	protected SerializedTokenReference serializedTokenReference;
	
	public String description;
	
	//protected static Fields fieldsByClassname;
/*	
	static
	{
		fieldsByClassname = new Fields();
	}
*/	

	/**
	 * Construct a GConstruct with the given name for use as a token reference.
	 * 
	 * @param name The internal name of this GConstruct code token, to be used 
	 * by the default error reporting facility of the compiler in place of a line number
	 * (since there are no explicit line breaks in generated code).
	 */
	public GConstruct(String name)
	{
		this.tokenReference = new TokenReference(getClass().getName() + "@" + name, 0);
		init();
	}
	
	GConstruct(Phylum phylum)
	{
		init();
		phylum.addPhylumListener(this);
		this.tokenReference = phylum.getTokenReference();
		
		//System.err.println(this.tokenReference + ": phylum " + phylum.getClass().getName() + " rendered as a " + getClass().getName());
	}
	
	private void init()
	{
		comments = new TreeSet();
		javadoc = null;
		
		//fieldsByClassname.initClass(this);
	}
	
	/**
	 * Clone me.
	 */
	public Copy shallowCopy()
	{
		try
		{
			return (Copy)this.clone();
		}
		catch (CloneNotSupportedException noWay) { return null; }
	}

	/**
	 * Traverses all fields of <code>this</code> accessible to the org.hs.generator 
	 * package, applying deepCopy() to instances of Copy, and otherwise applying direct
	 * reference (as does the java.lang.Cloneable.clone() mechanism). 
	 */	
	public Copy deepCopy()
	{
		try
		{
			Copy copy = shallowCopy();
			
			this.applyMembers(copy);
	
			java.lang.reflect.Field[] fields = getClass().getFields();
			Object fieldValue;
			int modifiers;
			for (int i = 0; i < fields.length; i++)
			{
				if (!fields[i].getDeclaringClass().getPackage().getName().equals("org.hs.generator"))
				{
					continue;
				}
				
				modifiers = fields[i].getModifiers();
				if (Modifier.isPrivate(modifiers) || Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers))
				{
					continue;
				}
				
				fieldValue = fields[i].get(this);
				if (fieldValue == null)
				{
					continue;
				}
				
				if (fieldValue instanceof Collection)
				{
					fields[i].set(this, deepCopy((Collection)fieldValue));
				}
				else if (fieldValue instanceof Copy)
				{
					fields[i].set(this, ((Copy)fieldValue).deepCopy());
				}
				else
				{
					fields[i].set(this, fieldValue);
				}
			}
			
			return copy;
		}
		catch (Exception e)
		{
			GSourceFile.log(e);
			return null;
		}
	}
	
	protected void applyMembers(Copy yourself)
	{
	}
	
	protected Collection deepCopy(Collection source)
	{
		try
		{
			// get an empty instance of the right kind of collection
			Collection destination = (Collection)source.getClass().newInstance();
			
			Iterator iterator = source.iterator();
			Object next;
			while (iterator.hasNext())
			{
				next = iterator.next();
				if (next instanceof Collection)
				{
					destination.add(deepCopy((Collection)next));
				}
				else if (next instanceof Copy)
				{
					destination.add(((Copy)next).deepCopy());
				}
				else 
				{
					destination.add(next);
				}
			}
			
			return destination;
		}
		catch (Exception e)
		{
			GSourceFile.log(e);
			return null;
		}
	}

	/**
	 * Warning!  Not implemented.
	 */	
	public void assume(GConstruct base)
	{
		System.err.println("Warning!  Call to assume(GConstruct), which has not been implemented.");
		// put all values for fields of base onto this
	}

	/**
	 * Get the value of the field named <code>fieldname</code> from this GConstruct,
	 * or return null if no such field exists.
	 *
	 * @param fieldname The name of the field (obtained via java.lang.Class.getField(String))
	 * to query on this GConstruct.
	 */	
	public Object get(String fieldname)
	{
		try
		{
			return getClass().getField(fieldname).get(this);
		}
		catch (Exception e)
		{
			GSourceFile.log(e);
			return null;
		}
	}
	
	/**
	 * Set the value of the field named <code>fieldname</code> on this GConstruct.
	 * Does nothing if no such field exists.
	 *
	 * @param fieldname The name of the field (obtained via java.lang.Class.getField(String))
	 * to apply <code>value</code> to on this GConstruct.
	 *
	 * @param value The new value to be applied to the field on this GConstruct named 
	 * <code>fieldname</code>, if any such field exists.
	 */	
	public void set(String fieldname, Object value)
	{
		try
		{
			getClass().getField(fieldname).set(this, value);
		}
		catch (Exception e)
		{
			GSourceFile.log(e);
		}
	}

	/**
	 * Generates the internal form of the contents of <code>this.comments</code>.
	 */
	public JavaStyleComment[] encodeComments()
	{
		if (this.comments == null)
		{
			return (new JavaStyleComment[0]);
		}
		
		JavaStyleComment[] encoded = new JavaStyleComment[this.comments.size()];
		int i = 0;
		Iterator iterator = this.comments.iterator();
		while (iterator.hasNext())
		{
			encoded[i++] = ((GComment)iterator.next()).encode();	
		}
		return encoded;	
	}
	
	public static JStatement expressionAsStatement(GExpression expression)
	{
		return (new JExpressionStatement(((GConstruct)expression).tokenReference,
										 expression.encodeExpression(),
										 ((GConstruct)expression).encodeComments()));
	}
	
	public static JStatement expressionAsStatement(JExpression expression)
	{
		return (new JExpressionStatement(expression.getTokenReference(),
										 expression,
										 new JavaStyleComment[0]));
	}
	
	public void reportError(CompileAgent agent)
	{
	}
	
	class GConstructDecoder extends GDecoder
	{
		/**
		 * visits an array length expression
		 */
		public void visitComments(JavaStyleComment[] comments)
		{
			for (int i = 0; i < comments.length; i++)
			{
				GConstruct.this.comments.add(new GComment(comments[i]));
			}
		}
	
		/**
		 * visits an array length expression
		 */
		public void visitComment(JavaStyleComment comment)
		{
			GConstruct.this.comments.add(new GComment(comment));
		}
	
		/**
		 * visits an array length expression
		 */
		public void visitJavadoc(JavadocComment comment)
		{
			GConstruct.this.javadoc = comment;
		}
	}

	protected static Class resolveClass(String classname)
	{
		try
		{
			return Class.forName(classname);
		}
		catch (ClassNotFoundException e)
		{
			throw (new IllegalArgumentException("Can't find class for name " + classname));
		}
	}
	
	public static Field getField(String classname, String fieldname)
	{
		return getField(resolveClass(classname), fieldname);
	}
	
	public static Field getField(Class owner, String fieldname)
	{
		try
		{
			return owner.getField(fieldname);
		}
		catch (NoSuchFieldException e)
		{
			throw (new IllegalArgumentException("Can't find field for name " + fieldname));
		}
	}
	
	public Object get(Field field)
	{
		try
		{
			return field.get(this);
		}
		catch (Exception e)
		{
			throw (new RuntimeException(e.getClass().getName() + ": " + e.getMessage()));
		}
	}
	
	public void set(Field field, Object value)
	{
		try
		{
			field.set(this, value);
		}
		catch (IllegalArgumentException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw (new RuntimeException(e.getClass().getName() + ": " + e.getMessage()));
		}
	}
	
	public String string(Field field)
	{
		return (String)this.get(field);
	}
	
	public Iterator iterator(Field field)
	{
		return ((Collection)this.get(field)).iterator();
	}
	
	public GBlock block(Field field)
	{
		return (GBlock)this.get(field);
	}

	private GConstruct()
	{
		this.tokenReference = new TokenReference("blah", 0);
	}
	
	protected static class SerializedTokenReference implements java.io.Serializable
	{
		protected String file;
		protected int line;
		
		public SerializedTokenReference(TokenReference source)
		{
			if (source == null)
			{
				this.file = "blah";
				this.line = 0;
			}
			else
			{
				this.file = source.getFile();
				this.line = source.getLine();
			}
		}
		
		public TokenReference resolve()
		{
			return (new TokenReference(this.file, this.line));
		}
	}
	
	protected Object writeReplace()
		throws java.io.ObjectStreamException
	{
		GSourceFile.log(getClass().getName() + ".writeReplace()");
		
		this.serializedTokenReference = new SerializedTokenReference(this.tokenReference);
		//System.err.println("writing a " + getClass().getName());
		return this;
	}
	
	protected Object readResolve()
		throws java.io.ObjectStreamException
	{
		this.tokenReference = this.serializedTokenReference.resolve();
		//System.err.println("reading a " + getClass().getName());
		return this;
	}
}
