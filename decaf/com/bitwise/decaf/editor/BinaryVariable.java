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

public class BinaryVariable implements Variable
{
	static final long serialVersionUID = -8312923520655321485L;

	protected String name;
	protected int modifiers;
	protected Type type;
	
	public BinaryVariable(Class type)
	{
		int dimensions = 0;
		Class arrayType = type;
		while (arrayType.isArray())
		{
			dimensions++;
			arrayType = arrayType.getComponentType();
		}
		
		this.type = Utils.getType(arrayType, dimensions);
	}
	
	public BinaryVariable(java.lang.reflect.Field source)
	{
		this(source.getType());

		this.name = source.getName();
		this.modifiers = source.getModifiers();
	}

	public String getName()
	{
		return this.name;
	}
	
	public int modifiers()
	{
		return this.modifiers;
	}
	
	public Type getType()
	{
		return this.type;
	}

	public JComponent render()
	{
		return renderer.getComponent(this);
	}
	
	// BinaryVariable is static to this IDE's context, so generate no events
	public void addReferenceListener(ReferenceListener listener)
	{
	}
	
	public void removeReferenceListener(ReferenceListener listener)
	{
	}
}