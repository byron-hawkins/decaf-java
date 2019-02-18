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
import org.hs.util.*;

import com.bitwise.umail.*;

import com.bitwise.decaf.editor.config.*;

public class Cup extends GClass implements Type
{
	static final long serialVersionUID = 1105570254574830740L;

	protected transient MethodsByName methodsByName;
	protected transient FieldsByName fieldsByName;
	
	protected String displayName;
	protected String discussion;
	
	Cup(String name)
	{
		super(name);
		init();
	}
	
	Cup(GClass source)
		throws ClassNotFoundException
	{
		super();
		init();
		
		super.name = source.name;
		
		DMethod dMethod;
		GMethod gMethod;
		Iterator methods = source.methods.iterator();
		while (methods.hasNext())
		{
			gMethod = (GMethod)methods.next();
			if (gMethod instanceof DMethod)
			{
				dMethod = (DMethod)gMethod;
			}
			else
			{
				dMethod = new DMethod(this, gMethod);
			}
			super.methods.add(dMethod);
		}

		Iterator fields = source.fields.iterator();
		DVariable newField;
		while (fields.hasNext())
		{
			newField = new DVariable((GVariable)fields.next());
			super.fields.add(newField);
		}
	}
	
	private void init()
	{
		this.discussion = null; //"blarghomatic";
		
		Utils.List fields = new Utils.List(super.fields);
		fields.addListener(new FieldsListener());
		super.fields = fields;
		
		Utils.List methods = new Utils.List(super.methods);
		methods.addListener(new MethodsListener());
		super.methods = methods;
		
		this.displayName = super.name;
		
		this.methodsByName = new MethodsByName();
		this.fieldsByName = new FieldsByName();
	}
	
	public String getDiscussion()
	{
		return this.discussion;
	}
	
	public void apply(TypeDetails details)
	{
	}
	
	public void addHook(jHandle handle)
		throws ClassNotFoundException
	{
		DMethod hook = new DMethod(handle, this);
		for (int i = 0; i < methods.size(); i++)
		{
			if (((DMethod)super.methods.elementAt(i)).equals(hook))
			{
				// it's already here, don't clobber it
				return;
			}
		}
		
		super.methods.add(hook);
	}
	
	public GConstruct getSource()
	{
		return super.type();
	}

	public GType typeSource()
	{
		return super.type();
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
		return /*static.getLocalPackageName() + */ super.name;
	}
	
	public boolean isPrimitive()
	{
		return false;
	}
	
	public boolean isAssignableFrom(Type other)
	{
		// include super/interface check later
		if (other instanceof PrimitiveType)
		{
			return false;
		}
		
		return (other.getQualifiedName().equals(getQualifiedName()));
	}
	
	public Collection getMethods()
	{
		return super.methods;
	}
	
	public Collection getMethods(String name)
	{
		return this.methodsByName.get(name);
	}
	
	public Method getMethod(String name, Type[] parameterTypes)
	{
		return this.methodsByName.get(name, parameterTypes);
	}
	
	public Collection getConstructors()
	{
		return (new Vector());	// not implemented yet
	}
	
	public Collection getFields()
	{
		return super.fields;
	}
	
	public Variable getField(String name)
	{
		return this.fieldsByName.get(name);
	}
	
	class FieldsListener implements Utils.List.Listener
	{
		public void report(Utils.List.Event event)
		{
			Variable subject = (Variable)event.subject();
			if (event.action() == Utils.List.Event.ADD)
			{
				Cup.this.fieldsByName.add(subject);
			}
			else
			{
				Cup.this.fieldsByName.remove(subject);
			}
		}
	}
	
	class MethodsListener implements Utils.List.Listener
	{
		public void report(Utils.List.Event event)
		{
			Method subject = (Method)event.subject();
			if (event.action() == Utils.List.Event.ADD)
			{
				Cup.this.methodsByName.add(subject);
			}
			else
			{
				Cup.this.methodsByName.remove(subject);
			}
		}
	}
	
	private Cup()
	{
		super("deserialized");
	}

	protected Object readResolve()
		throws java.io.ObjectStreamException
	{
		((Utils.List)super.fields).addListener(new FieldsListener());
		((Utils.List)super.methods).addListener(new MethodsListener());
		
		//System.err.println(this.toString());
		
		init();
		
		return super.readResolve();
	}
	
	public String toString()
	{
		return "Cup " + super.name;
	}
}