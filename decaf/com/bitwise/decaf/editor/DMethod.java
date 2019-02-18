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

// must compare by some kind of id
public class DMethod extends GMethod implements Method
{
	static final long serialVersionUID = 8677171757544597951L;

	// will need to unload some of these 
	protected Vector referenceListeners;
	
	protected Cup forCup;
	protected String discussion;
	
	public BodyGrammar bodyGrammar;
	
	// deserialization only
	private DMethod()
	{
		super();
	}
	
	DMethod(Cup forCup)
		throws ClassNotFoundException
	{
		super();
		init();
		this.forCup = forCup;
	}

	DMethod(Cup forCup, GMethod data)
		throws ClassNotFoundException
	{
		this(forCup);
		
		if (data != null)
		{
			super.name = data.name;
			
			if (data.returnType == null)
			{
				super.returnType = Utils.voidType;
			}
			else
			{
				if (data.returnType instanceof Type)
				{
					super.returnType = data.returnType;
				}
				else
				{
					super.returnType = (GType)Utils.wrapType(data.returnType);
				}
			}
			
			DVariable next;
			super.parameters = new Utils.List();
			for (int i = 0; i < data.parameters.size(); i++)
			{
				next = new DVariable((GVariable)data.parameters.elementAt(i));
				super.parameters.add(next);
			}
			
			this.bodyGrammar.apply(data);
		}
	}
	
	public DMethod(jHandle handle, Cup forCup)
		throws ClassNotFoundException
	{
		this(forCup);
		
		super.name = handle.getMethodName();
		
		DParameter parameter;
		Iterator iterator = handle.getParameterTypes();
		
		jString next;
		char parameterName = 'a';
		while(iterator.hasNext())
		{
			parameter = new DParameter(true);
			next = (jString)iterator.next();
			parameter.setType(Utils.getType(Class.forName(next.get())));
			parameter.name = String.valueOf(parameterName++);
			super.parameters.add(parameter);
		}
		
		MethodDetails details = MethodDetails.getUserMethodDetails(this);
		if (details == null)
		{
			this.discussion = null;
		}
		else
		{
			this.apply(details);
		}
	}
	
	private void init()
	{
		this.discussion = null;
		
		super.modifiers |= java.lang.reflect.Modifier.PUBLIC;
		
		super.returnType = Utils.voidType;
		super.parameters = new Utils.List();

		this.referenceListeners = new Utils.SerialVector();
		
		this.bodyGrammar = new BodyGrammar(this);
		
		//super.description = super.returnType
	}
	
	public void apply(MethodDetails details)
	{
		DParameter parameter;
		this.discussion = details.getDiscussion();
		for (int i = 0; i < super.parameters.size(); i++)
		{
			parameter = (DParameter)super.parameters.elementAt(i);
			parameter.apply(details.getParameterDetails(i), false);	// figure out if we have javadoc for this
		}
	}
	
	public String getDiscussion()
	{
		return this.discussion;
	}
	
	public boolean hasDetails()
	{
		return (this.discussion != null);
	}
	
	public boolean isConstructor()
	{
		return false;	// not implemented yet
	}
	
	public String getName()
	{
		return super.name;
	}
	
	public Type getReturnType()
	{
		return (Type)super.returnType;
	}
	
	public Collection getParameters()
	{
		try
		{
			DVariable next;
			Vector parameters = new Vector();
			for (int i = 0; i < super.parameters.size(); i++)
			{
				next = (DVariable)super.parameters.elementAt(i);
				parameters.add(new DParameter(next));
			}
			return parameters;
		}
		catch (ClassNotFoundException nuhUh)
		{
			return null;
		}
	}
	
	public int modifiers()
	{
		return super.modifiers;
	}
	
	public void set(java.lang.reflect.Field field, Object value)
	{
		if (field.getName().equals("returnType"))
		{
			if (!(value instanceof Type))
			{
				try
				{
					value = Utils.wrapType((GType)value);
				}
				catch (ClassNotFoundException e)
				{
					throw (new IllegalArgumentException("Can't set the return type to " + value + " because I can't find it."));
				}
			}
		}

		super.set(field, value);
	}
	
	public JComponent render()
	{
		return renderer.getComponent(this);
	}

	public void update()
	{
		for (int i = 0; i < this.referenceListeners.size(); i++)
		{
			((ReferenceListener)this.referenceListeners.elementAt(i)).referenceUpdated(new ReferenceEvent(this));
		}
	}
	
	public void delete()
	{
		for (int i = 0; i < this.referenceListeners.size(); i++)
		{
			((ReferenceListener)this.referenceListeners.elementAt(i)).referenceDeleted(new ReferenceEvent(this));
		}
	}
	
	public void addReferenceListener(ReferenceListener listener)
	{
		this.referenceListeners.add(listener);
	}
	
	public void removeReferenceListener(ReferenceListener listener)
	{
		this.referenceListeners.remove(listener);
	}
	
	public String toString()
	{
		return ((this.name == null)||(this.name.equals("")))?"<New DMethod>":this.name;
	}
	
	class DParameter extends DVariable implements Parameter
	{
		protected String discussion;
		protected boolean hasJavadoc;
		
		public DParameter(DVariable variable)
			throws ClassNotFoundException
		{
			super(variable);
			
			this.init();
		}
		
		public DParameter(boolean isLocal)
		{
			super(isLocal);
			
			this.init();
		}
		
		private void init()
		{
			this.discussion = null;
			this.hasJavadoc = false;
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

	public static class BodyGrammar extends com.bitwise.decaf.editor.grammar.Paragraph
	{
		static final long serialVersionUID = 2051034027266861831L;
		
		protected DMethod source;
		
		public BodyGrammar(DMethod source)
		{
			super(null);
			
			this.source = source;
			this.apply(this.source);
		}
		
		public void apply(GMethod data)
		{
			super.content = data.body;
			if (!(data.body.statements instanceof Utils.List))
			{
				super.content.statements = new Utils.List(data.body.statements);
			}
		}

		protected void applyMembers(Copy yourself)
		{
			Thread.dumpStack();
		}
		
		public JComponent render()
		{
			return this.source.render();
		}
		
		public DMethod getEnclosingMethod()
		{
			return this.source;
		}
		
		public boolean isInScope(DVariable variable, Object child)
		{
			if (super.isInScope(variable, child))
			{
				return true;
			}
			
			// got to check all the custom components on the mask as well
			// also, custom class components could create fields, though I 
			// guess they need to be applied in real-time, which puts them 
			// on the cup where we find them below.  Well, so we need any
			// parameter-creating component to apply in real-time, such that
			// this.source contains the parameters.
			
			if (this.source.parameters.contains(variable))
			{
				return true;
			}
			
			if (variable.type.equals(GType.THIS))
			{
				return true;
			}
			
			return this.source.forCup.fields.contains(variable);
		}
		
		public void collectVariables(Collection variables, Object child)
		{
			super.collectVariables(variables, child);
			Iterator fields = this.source.forCup.fields.iterator();
			while (fields.hasNext())
			{
				variables.add(fields.next());
			}
		}
		
		public Cup identifyCup()
		{
			return this.source.forCup;
		}

		public MethodEditor editMethod()
		{
			MethodEditor editor = MethodEditor.getEditorFor(this.source);
			if (editor == null)
			{
				editor = new MethodEditor();
				editor.assemble(this.source); 
				MethodEditor.openingEditor(editor);
				editor.start();
			}
			
			return editor;
		}
	}
}