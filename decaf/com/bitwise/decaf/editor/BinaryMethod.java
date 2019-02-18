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

import com.bitwise.decaf.editor.config.*;

public class BinaryMethod implements Method
{
	static final long serialVersionUID = -5569292929018216454L;

	protected String name;
	protected Type returnType;
	protected Vector parameters;
	protected int modifiers;
	protected String discussion;
	protected boolean hasDetails;
	protected boolean hasJavadoc;
	protected boolean isConstructor;
	
	protected char defaultParameterName;
	
	public BinaryMethod(java.lang.reflect.Method source)
	{
		this.name = source.getName();
		this.returnType = Utils.getType(source.getReturnType());
		
		this.hasDetails = false;
		
		this.defaultParameterName = 'a';
		
		BinaryParameter next;
		this.parameters = new Vector();
		java.lang.Class[] binaryParameters = source.getParameterTypes();
		for (int i = 0; i < binaryParameters.length; i++)
		{
			this.parameters.add(new BinaryParameter(binaryParameters[i]));
		}
		
		this.modifiers = source.getModifiers();
		this.isConstructor = false;
	}
	
	public BinaryMethod(Type type, java.lang.reflect.Constructor source)
	{
		this.name = "<init>";
		this.returnType = type;
		this.hasDetails = false;
		this.defaultParameterName = 'a';

		BinaryParameter next;
		this.parameters = new Vector();
		java.lang.Class[] binaryParameters = source.getParameterTypes();
		for (int i = 0; i < binaryParameters.length; i++)
		{
			this.parameters.add(new BinaryParameter(binaryParameters[i]));
		}
		
		this.modifiers = source.getModifiers();
		this.isConstructor = true;
	}
	
	public boolean isConstructor()
	{
		return this.isConstructor;
	}
	
	public boolean hasDetails()
	{
		return this.hasDetails;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public Type getReturnType()
	{
		return this.returnType;
	}
	
	public Collection getParameters()
	{
		return this.parameters;
	}

	public int modifiers()
	{
		return this.modifiers;
	}
	
	public JComponent render()
	{
		System.err.println("rendering " + this.name + " as constructor? " + this.isConstructor);
		return renderer.getComponent(this);
	}
	
	public void apply(MethodDetails details, boolean hasJavadoc)
	{
		this.hasJavadoc = hasJavadoc;

		if (details != null)
		{
			this.hasDetails = true;
			this.discussion = details.getDiscussion();
			
			// once a method is detailed, so must each of its parameters be
			for (int i = 0; i < this.parameters.size(); i++)
			{
				((BinaryParameter)this.parameters.elementAt(i)).apply(details.getParameterDetails(i), hasJavadoc);
			}
		}
	}

	public String getDiscussion()
	{
		return this.discussion;
	}
	
	// BinaryMethod is static to this IDE's context, so generate no events
	public void addReferenceListener(ReferenceListener listener)
	{
	}
	
	public void removeReferenceListener(ReferenceListener listener)
	{
	}
	
	public class BinaryParameter extends BinaryVariable implements Parameter
	{
		protected String discussion;
		boolean hasJavadoc;
		
		public BinaryParameter(Class type)
		{
			super(type);
			
			super.modifiers = 0;
			super.name = String.valueOf(BinaryMethod.this.defaultParameterName++);
			
			this.discussion = null;
		}
		
		public void apply(ParameterDetails details, boolean hasJavadoc)
		{
			super.name = details.getName();
			this.discussion = details.getDiscussion();
			this.hasJavadoc = hasJavadoc;
		}
		
		public String getDiscussion()
		{
			return this.discussion;
		}
	}
}