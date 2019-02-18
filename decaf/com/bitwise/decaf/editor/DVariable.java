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

import com.bitwise.decaf.editor.grammar.*;

public class DVariable extends GVariable implements Variable, Grammar.Content
{
	static final long serialVersionUID = -4832667498103004121L;

	protected static int index = 0;
	
	protected static java.lang.reflect.Field typeField;
	
	public static DVariable getThis(Cup cup)
	{
		DVariable variable = new DVariable(false);
		variable.name = null;
		variable.type = GType.THIS;
		variable.translatePrefix();
		variable.dType = cup;
		
		return variable;
	}
	
	static
	{
		typeField = GConstruct.getField(GVariable.class, "type");
	}
	
	protected Type dType;
	protected int id;
	
	// apparently no ui components are in here yet...
	protected Vector referenceListeners;
	
	// deserialization only
	private DVariable()
	{
	}
	
	public DVariable(boolean isLocal)
	{
		super();
		
		super.isLocal = isLocal;
		this.dType = Utils.noType;
		super.type = (GType)Utils.noType;
		init();
	}
	
	public DVariable(GVariable data)
		throws ClassNotFoundException
	{
		super();
		
		super.name = data.name;
		super.type = data.type;
		
		this.dType = Utils.wrapType(data.type);

		init();
	}
	
	private void init()
	{
		this.id = index++;
		this.referenceListeners = new Utils.SerialVector();
	}
	
	public String getName()
	{
		return super.name;
	}
	
	public int modifiers()
	{
		return super.modifiers;
	}
	
	public Type getType()
	{
		if ((this.dType == null) && (super.type != null))
		{
			try
			{
				this.dType = Utils.wrapType(super.type);
			}
			catch (ClassNotFoundException alreadyBeenCheckedByNow)
			{
			}
		}
		return this.dType;
	}
	
	public void setLocal(boolean local)
	{
		if (super.isLocal != local)
		{
			super.isLocal = local;
			this.update();
		}
	}
	
	public JComponent render()
	{
		return renderer.getComponent(this);
	}
	
	public void setType(Type type)
	{
		this.dType = type;
		super.type = this.dType.typeSource();
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

	public void set(java.lang.reflect.Field field, Object value)
	{
		super.set(field, value);
		update();
		try
		{
			if (field.equals(typeField))
			{
				if (value instanceof Type)
				{
					this.dType = (Type)value;
				}
				else
				{
					DecafEditor.log("DVariable.set(): Setting a non-Type on DVariable: " + this);
					this.dType = Utils.wrapType(super.type);
				}
			}
		}
		catch (ClassNotFoundException alreadyBeenCheckedByNow)
		{
		}
	}
	
	public boolean conflictsWith(GVariable other)
	{
		return ((super.isLocal == other.isLocal) && (super.name.equals(other.name)));
	}
	
	public MaskEditor edit(Grammar handle)
	{
		DecafEditor.log("DVariable.edit(): " + getClass().getName() + ".edit(): not implemented -- returning null!");
		return null;
	}
	
	public void setHandle(Grammar handle)
	{
		DecafEditor.log("DVariable.setHandle(): " + getClass().getName() + ".setHandle(): not implemented -- these are for reference only!");
	}
	
	public JComponent getComponent(Grammar handle)
	{
		if (super.type.equals(GType.THIS))	
		{
			return (new Utils.RenderLabel("this"));
		}
	
		Utils.RenderPanel panel = new Utils.RenderPanel();
	
		if (super.prefix instanceof Phrase)
		{
			panel.add(((Phrase)super.prefix).render());
			panel.add(new Utils.RenderLabel("."));
		}

		Utils.RenderLabel name = new Utils.RenderLabel(super.name);
		panel.add(name);
		
		return panel;
	}
	
	public GConstruct getSource()
	{
		return this;
	}
		
	public boolean equals(Object o)
	{
		if (o instanceof DVariable)
		{
			return (this.id == ((DVariable)o).id);
		}
		return false;
	}
	
	protected Object readResolve()
		throws java.io.ObjectStreamException
	{
		this.init();
		return this;
	}
	
	public String toString()
	{
		return ((super.name == null) || (super.name.equals("")))?"<New Variable>":super.name + " <id " + this.id + ">";
	}
}