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
 * A very convoluded representation of types.  
 */
public class GType extends GConstruct implements GExpression 
{
	static final long serialVersionUID = 8359689584672015313L;

	/**
	 * The primitive type byte
	 */
	public static GType BYTE;

	/**
	 * The primitive type double
	 */
	public static GType DOUBLE;

	/**
	 * The primitive type long
	 */
	public static GType LONG;

	/**
	 * The primitive type short
	 */
	public static GType SHORT;

	/**
	 * The primitive type char
	 */
	public static GType CHAR;

	/**
	 * The primitive type int
	 */
	public static GType INT;

	/**
	 * The primitive type float
	 */
	public static GType FLOAT;

	/**
	 * The type java.lang.String, which honestly does not belong in this list.  It's
	 * here because it is backed by a static KJC entity, just like the others here
	 * (pass the buck!)
	 */
	public static GType STRING;

	/**
	 * The primitive type void
	 */
	public static GType VOID;

	/**
	 * The primitive type boolean
	 */
	public static GType BOOLEAN;

	/**
	 * The primitive type null
	 */
	public static GType NULL;
	
	/**
	 * A bogus type used by GMethod as a return type to indicate that the method
	 * is a constructor.  
	 */
	public static GType CONSTRUCTOR;
	
	/**
	 * A bogus type used by GVariable as a variable type to indicate that the 
	 * variable is a reference to the keyword 'this'.
	 */
	public static GType THIS;

	/**
	 * A bogus type used by GVariable as a variable type to indicate that the 
	 * variable is a reference to the keyword 'super'.
	 */
	public static GType SUPER;

	/**
	 * A bogus type used by GVariable as a variable type to indicate that the 
	 * variable is a reference to the keyword 'class'.
	 */
	public static GType CLASS;

	/**
	 * A bogus type used by GVariable as a variable type to indicate that the 
	 * variable is a reference to the keyword 'length' as applied to an array.
	 */
	public static GType LENGTH;
	
	/**
	 * Useful for dynamic code generation -- does not compile.
	 */
	public static GType UNKNOWN;

	protected static final int PRIMITIVE_ID_EXTENT = 99;
	protected static final int STRUCTURAL_ID_EXTENT = 199;
	protected static final int DERIVATIVE_ID = 200;

	protected static CType identifyPrimitive(int id)
	{
		switch (id)
		{
			case 0: return CStdType.Byte;
			case 1: return CStdType.Double;
			case 2: return CStdType.Long;
			case 3: return CStdType.Short;
			case 4: return CStdType.Char;
			case 5: return CStdType.Integer;
			case 6: return CStdType.Float;
			case 7: return CStdType.String;
			case 8: return CStdType.Void;
			case 9: return CStdType.Boolean;
			case 10: return CStdType.Null;
		}
		
		return null;
	}
	
	/**
	 * Initialize the primitive types with their corresponding KJC CStdType constructs.
	 */
	public static void establishPrimitives()
	{
		BYTE = new GType(CStdType.Byte, 0);
		DOUBLE = new GType(CStdType.Double, 1);
		LONG = new GType(CStdType.Long, 2);
		SHORT = new GType(CStdType.Short, 3);
		CHAR = new GType(CStdType.Char, 4);
		INT = new GType(CStdType.Integer, 5);
		FLOAT = new GType(CStdType.Float, 6);
		STRING = new GType(CStdType.String, 7);
		VOID = new GType(CStdType.Void, 8);
		BOOLEAN = new GType(CStdType.Boolean, 9);
		NULL = new GType(CStdType.Null, 10);
		CONSTRUCTOR = new GType(100);
		THIS = new GType(101);
		SUPER = new GType(102);
		CLASS = new GType(103);
		LENGTH = new GType(104);
		UNKNOWN = new GType(-1);
	}

	/**
	 * Get a reference to a primitive GType from the corresponding java.lang.Class; 
	 * e.g., GType.getPrimitive(boolean.class, 0) returns a reference to GType.BOOLEAN.
	 * This method serves only as a convenience shortcut when you don't know what kind
	 * of java.lang.Class you've got hold of, and you don't especially care.
	 */	
	public static GType getPrimitive(Class type, int dimensions)
	{
		if (type == Boolean.TYPE)
		{
			return (new GType(BOOLEAN, dimensions));
		}
		else if (type == Byte.TYPE)
		{
			return (new GType(BYTE, dimensions));
		}
		else if (type == Character.TYPE)
		{
			return (new GType(CHAR, dimensions));
		}
		else if (type == Double.TYPE)
		{
			return (new GType(DOUBLE, dimensions));
		}
		else if (type == Float.TYPE)
		{
			return (new GType(FLOAT, dimensions));
		}
		else if (type == Integer.TYPE)
		{
			return (new GType(INT, dimensions));
		}
		else if (type == Long.TYPE)
		{
			return (new GType(LONG, dimensions));
		}
		else if (type == Short.TYPE)
		{
			return (new GType(SHORT, dimensions));
		}
		else if (type == Void.TYPE)
		{
			return (new GType(VOID, dimensions));
		}
		else
		{
			return (new GType((Object)type, dimensions));
		}
	}
	
	protected transient CType type;	// need to identify these transients on deserialization
	protected transient CReferenceType classnameType;
	protected String classname;
	protected int dimensions;
	protected int id;
	
	protected GType(int id)
	{
		super("<structural type>");
		
		init(id);
	}
	
	GType(CType type, int id)
	{
		this(type, 0, id);
	}

	/**
	 * This seemingly frivolous constructor serves to allow subclasses to 
	 * instantiate themselves on the basis of existing GType references.
	 * 
	 * @param copy the GType to copy.
	 */	
	public GType(GType copy)
	{
		this(copy.type, copy.dimensions, copy.id);
	}

	/**
	 * Create an array GType from <code>type</code> with the specified array dimensions.
	 * 
	 * @param type the array base type.
	 * @param dimensions the array dimensions; excluding, of course, the depth of each dimension.
	 * 0 indicates that the created type will in fact not be an array.
	 */	
	public GType(GType type, int dimensions)
	{
		this(type.type, dimensions, type.id);
	}
	
	protected GType(CType type, int dimensions, int id)
	{
		super("<primitive type>");
		
		init(type, dimensions, id);
	}

	/**
	 * Create a GType to represent the class of <code>o</code>.
	 * 
	 * @param o an instance of the class to represent.
	 */	
	public GType(Object o)
	{
		this(o, 0);
	}
	
	/**
	 * Create a GType to represent <code>type</code>.
	 * 
	 * @param type the type to represent.
	 */
	public GType(Class type)
	{
		this(type, 0);
	}
	
	/**
	 * Create a GType to represent an array of the class of <code>o</code>.
	 * 
	 * @param o an instance of class that will be the base of the array.
	 * @param dimensions the dimensions of the array (0 indicates that this type is 
	 * in fact not an array).
	 */	
	public GType(Object o, int dimensions)
	{
		this((o instanceof Class)?((Class)o).getName():o.getClass().getName(), dimensions);
	}

	/**
	 * Create a reference to the class described by the fully qualified classname <code>type</code>
	 *
	 * @param type the fully qualified classname to represent with the new GType.
	 */	
	public GType(String type)
	{
		this(type, 0);
	}
	
	/**
	 * Create a reference to an array with base type described by the fully qualified
	 * classname <code>type</code>.
	 *
	 * @param type the fully qualified classname of the base of the array that will 
	 * be represented by the new GType.
	 * @param the dimensions of the array, excluding depth (of course); 0 indicates that 
	 * this type will in fact not be an array.
	 */
	public GType(String type, int dimensions)
	{
		super("<derivative type>");
		
		init(type, dimensions);
	}
	
	protected void init(int id)
	{
		this.id = id;
		this.classname = null;
		this.type = null;
		this.classnameType = null;
	}
		
	protected void init(CType type, int dimensions, int id)
	{
		this.dimensions = dimensions;
		
		if (dimensions > 0)
		{
			this.type = new CArrayType(type, dimensions);
		}
		else
		{
			this.type = type;
		}
		this.classname = (type == null)?"<none>":type.toString();
		this.id = id;
		this.classnameType = null;
	}
	
	protected void init(String type, int dimensions)
	{
		this.dimensions = dimensions;
				
		// is this correct for references as well as declarations?
		this.classnameType = new CClassNameType(type.replace('.','/'), true);
		CType typeVariable = new CTypeVariable(type.replace('.','/'), new CReferenceType[] {this.classnameType});
		if (dimensions > 0)
		{
			this.type = new CArrayType(typeVariable, dimensions);
		}
		else
		{
			this.type = typeVariable;
		}
		this.classname = type;
		this.id = DERIVATIVE_ID;
	}

	GType(JExpression expression)
	{
		super(expression);
		expression.accept(new Decoder());
	}
	
	public boolean isPrimitive()
	{
		return (this.id < PRIMITIVE_ID_EXTENT);
	}
	
	public String getQualifiedName()
	{
		return this.classname;
	}
	
	public int getDimensions()
	{
		return this.dimensions;
	}
	
	public void setDimensions(int dimensions)
	{
		this.dimensions = dimensions;
	}
	
	public CType encode()
	{
		/*
		if (this.type instanceof CReferenceType)
		{
			this.resolve();
			System.err.println("GType.encode(): fqn: " + ((CReferenceType)this.type).getQualifiedName());
			System.err.println("GType.encode(): name: " + ((CReferenceType)this.type).getIdent());
		}
		*/
		return this.type;
	}
	
	public JExpression encodeExpression()
	{
		return (new JTypeNameExpression(super.tokenReference,
										(CReferenceType)this.type));
	}
	
	public int getArrayBound()
	{
		if (this.type instanceof CArrayType)
		{
			return ((CArrayType)this.type).getArrayBound();
		}
		else
		{
			return 0;
		}
	}
	
	public CReferenceType getClassnameType()
	{
		return this.classnameType;
	}
	
	public void resolve()
	{
		try
		{
			this.type.checkType(GSourceFile.s_typeContext);
		}
		catch (UnpositionedError e)
		{
			GSourceFile.log(e);
			throw (new RuntimeException("Unable to resolve type " + toString() + ": " + e.getClass().getName() + ": " + e.getMessage()));
		}
 	}

	public int compareTo(Object o)
	{
		GType other = (GType)o;
		
		int result;
		
		// junky:
		if ((this.classname != null) && (other.classname != null) && this.classname.equals("java.lang.String") && other.classname.equals("java.lang.String"))
		{
			result = 0;
		}
		else
		{
			if (this.id == other.id)
			{
				if (this.id == DERIVATIVE_ID)
				{
					result = this.classname.compareTo(other.classname);
				}
				else
				{
					result = 0;
				}
			}
			else
			{
				result = this.id - other.id;
			}
		}
		
		if (result != 0)
		{
			return result;
		}

		return (this.dimensions - other.dimensions);
	}
	
	public boolean equals(Object o)
	{
		if (o == null)
		{
			return false;
		}
		
		return (compareTo(o) == 0);
	}

	class Decoder extends GConstructDecoder
	{
		/**
		 * visits a type name expression
		 */
		public void visitTypeNameExpression(JTypeNameExpression self,
											CReferenceType type)
		{
			GType.this.type = type;
		}
	}
	
	protected Object writeReplace() 
		throws java.io.ObjectStreamException
	{
		//System.err.println("I'm a GType going to disk: (id " + this.id + ") (classname " + classname + ") (CType " + ((this.type == null)?"null":this.type.toString()) + ") (dimensions " + this.dimensions + ")");
		return this;
	}
	
	protected Object readResolve() 
		throws java.io.ObjectStreamException
	{
		if (this.classname == null)
		{
			init(this.id);
			//System.err.println("I'm a structural GType from disk: (id " + this.id + ") (classname " + classname + ") (CType " + ((this.type == null)?"null":this.type.toString()) + ") (dimensions " + this.dimensions + ")");
		}
		else if (this.id < PRIMITIVE_ID_EXTENT)
		{
			init(identifyPrimitive(this.id), this.dimensions, this.id);
			//System.err.println("I'm a primitive GType from disk: (id " + this.id + ") (classname " + classname + ") (CType " + ((this.type == null)?"null":this.type.toString()) + ") (dimensions " + this.dimensions + ")");
		}
		else 
		{
			init(this.classname, this.dimensions);
			//System.err.println("I'm a derivative GType from disk: (id " + this.id + ") (classname " + classname + ") (CType " + ((this.type == null)?"null":this.type.toString()) + ") (dimensions " + this.dimensions + ")");
		}
		
		return this;
	}

	public String toString()
	{
		return this.classname;
	}
}
	
