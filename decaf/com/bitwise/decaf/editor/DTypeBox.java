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
import java.lang.reflect.*;

import javax.swing.*;
import javax.swing.text.*;

import org.hs.jfc.*;
import org.hs.generator.*;

/**
 * A generic type box (consisting of a {@link javax.swing.JPanel)) that implements 
 * the requisite interfaces for participation in the Decaf masks.  Starts out containing
 * {@link Utils.noType}.  All method undocumented implementations are thus because they
 * are duh-obvious.
 */
public class DTypeBox extends JPanel implements DecafTypeComponent
{
	protected Field represents;
	protected JLabel label;
	protected JLabel display;
	protected Color hotColor;
	protected Color coolColor;
	
	protected Type data;
	
	/**
	 * Initialize this DTypeBox with {@link Utils.noType}.
	 */
	public DTypeBox()
	{
		super();
		
		super.setLayout(new FlowLayout(FlowLayout.LEFT));

		this.coolColor = null;
		
		this.data = Utils.noType;
		this.display = new JLabel("");
		
		super.add(this.display);
	}
	
	public void setType(Type type)
	{
		this.data = type;
	}
	
	public Type getData()
	{
		return this.data;
	}
	
	public void setLabelText(String label)
	{
		this.label = new JLabel(label);
	}
	
	public void setRepresents(Field represents)
	{
		this.represents = represents;
	}
	
	public Component getLabel()
	{
		return this.label;
	}
	
	public Component getComponent()
	{
		return this;
	}
	
	public Component getComponentView()
	{
		return this;
	}

	/**
	 * Offers and receives {@link Type}, receives {@link Typed}.
	 */	
	public void register(HotComponent.Arbiter arbiter)
	{
		arbiter.offer(Type.class, LINK);
		arbiter.receive(Type.class);
		arbiter.receive(Typed.class);
	}
 
	public boolean pick(boolean b)
	{
		if (b)
		{
			if (this.coolColor == null)
			{
				this.coolColor = super.getBackground();
			}
			super.setBackground(this.hotColor);
		}
		else
		{
			super.setBackground(this.coolColor);
		}
		
		return true;
	}
	
	public Object give(Class type, int operation)
	{
		return this.data;
	}
	
	public boolean isCompatible(Object hot)
	{
		return true;
	}

	/**
	 * Extracts a {@link Type} from <code>hot</code>
	 */	
	public void take(Object hot)
	{
		if (hot instanceof Type)
		{
			this.data = (Type)hot;
		}
		else if (hot instanceof Typed)
		{
			this.data = ((Typed)hot).getType();
		}
		this.display.setText(this.data.getDisplayName());
	}
	
	public Collection actions()
	{
		return (new Vector());
	}

	/**
	 * Displays the relevant {@link Type} on <code>code</code> if it is not null; 
	 * otherwise displays "<none>".
	 */
	public void display(GConstruct code)
	{
		if (code == null)
		{
			this.display.setText("<none>");
		}
		else
		{
			this.data = (Type)code.get(this.represents);
			if (this.data == null)
			{
				this.data = Utils.noType;
			}
			this.display.setText(this.data.getDisplayName());
		}
	}
	
	public void apply(GConstruct code)
	{
		code.set(this.represents, this.data.typeSource());
	}
	
	public void setHotColor(Color hotColor)
	{
		this.hotColor = hotColor;
	}

	public Object writeReplace()
		throws java.io.ObjectStreamException
	{
		throw (new Utils.SerialException(getClass()));
	}
}