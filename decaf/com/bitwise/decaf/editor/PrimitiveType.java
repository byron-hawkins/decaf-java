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

public class PrimitiveType extends GType implements Type
{
	static final long serialVersionUID = -500124364577885070L;

	protected String displayName;
	
	public PrimitiveType(GType source)
	{
		this(source, source.toString());
	}
	
	public PrimitiveType(GType source, String displayName)
	{
		super(source);
		this.displayName = displayName;
	}
	
	public PrimitiveType(Class type)
	{
		this(type, 0);
	}
	
	public PrimitiveType(Class type, int dimensions)
	{
		super(GType.getPrimitive(type, dimensions));
		
		this.displayName = type.getName();
		for (int i = 0; i < dimensions; i++)
		{
			this.displayName += "[]";
		}
	}
	
	public void apply(TypeDetails details)
	{
		if (details != null)
		{
			this.displayName = details.getUid();
		}
	}
	
	public String getDiscussion()
	{
		return "Discussion of PrimitiveType";
	}

	public GConstruct getSource()
	{
		return this;
	}
	
	public GType typeSource()
	{
		return this;
	}
	
	public String getName()
	{
		return this.toString();
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
		return this.toString();
	}
	
	public boolean isPrimitive()
	{
		return true;
	}
	
	public boolean isAssignableFrom(Type other)
	{
		// more rules later...
		if (other instanceof PrimitiveType)
		{
			if (this.type == ((PrimitiveType)other).type)
			{
				return (this.dimensions == ((PrimitiveType)other).dimensions);
			}
		}
		return false;
	}
	
	public Collection getMethods()
	{
		return null;
	}
	
	public Collection getMethods(String name)
	{
		return null;
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
		return null;
	}
	
	public Variable getField(String name)
	{
		return null;
	}
}