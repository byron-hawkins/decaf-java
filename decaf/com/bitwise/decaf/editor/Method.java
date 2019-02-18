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

public interface Method extends Utils.Renderable, Decaf, Configurable
{
	static final Renderer renderer = new Renderer();
	
	public String getName();
	public Type getReturnType();
	public Collection getParameters();
	public int modifiers();
	public boolean hasDetails();
	public boolean isConstructor();
	
	public void addReferenceListener(ReferenceListener listener);
	public void removeReferenceListener(ReferenceListener listener);
	
	static class Renderer 
	{
		private static StringBuffer buffer;
		
		public JComponent getComponent(Method data)
		{
			return (new Utils.RenderLabel(describe(data)));
		}
		
		public String describe(Method data)
		{
			buffer = new StringBuffer();
			
			String name = data.getName();

			if (data.isConstructor())
			{
				buffer.append("new ");
			}
			
			buffer.append(data.getReturnType().getDisplayName());
			
			if (!data.isConstructor())
			{
				buffer.append(" ");
				buffer.append(name);
			}
			
			buffer.append("(");
			
			Parameter next;
			
			Iterator parameters = data.getParameters().iterator();
			while (parameters.hasNext())
			{
				next = (Parameter)parameters.next();
				buffer.append(next.getType().getDisplayName());
				buffer.append(" ");
				buffer.append(next.getName());
				if (parameters.hasNext())
				{
					buffer.append(", ");
				}
			}
			
			buffer.append(")");
			
			return buffer.toString();
		}
	}
	
	public static interface Parameter extends Variable, Configurable
	{
		public void apply(ParameterDetails details, boolean hasJavadoc);
		public String getName();
	}
	
	public static class Comparator implements java.util.Comparator, java.io.Serializable
	{
		public int compare(Object one, Object two)
		{
			if ((one instanceof Comparable) && (two instanceof Comparable))
			{
				return ((Comparable)one).compareTo(two);
			}
			
			Method aMethod = (Method)one;
			Method bMethod = (Method)two;
			
			int result;
			
			if (aMethod.hasDetails())
			{
				if (!bMethod.hasDetails())
				{
					return -1;
				}
			}
			else if (bMethod.hasDetails())
			{
				return 1;
			}
			
			if (aMethod.getName() == null)
			{
				if (bMethod.getName() != null)
				{
					return 1;
				}
			}
			else if (bMethod.getName() == null)
			{
				return -1;
			}
			else
			{
				result = aMethod.getName().compareTo(bMethod.getName());
				if (result != 0)
				{
					return result;
				}
			}
			
			Collection a = aMethod.getParameters();
			Collection b = bMethod.getParameters();
			
			if (a.size() > b.size())
			{
				return 1;
			}
			if (a.size() < b.size())
			{
				return -1;
			}
			
			Type aType;
			Type bType;
			Iterator ai = a.iterator();
			Iterator bi = b.iterator();
			while (ai.hasNext())
			{
				aType = ((Method.Parameter)ai.next()).getType();
				bType = ((Method.Parameter)bi.next()).getType();
				
				result = aType.getQualifiedName().compareTo(bType.getQualifiedName());
				if (result != 0)
				{
					return result;
				}
				
				result = aType.typeSource().getDimensions() - bType.typeSource().getDimensions();
				if (result != 0)
				{
					return result;
				}
			}

			return 0;
		}
		
		public boolean compareTo(Object o)
		{
			return false;
		}
	}
}