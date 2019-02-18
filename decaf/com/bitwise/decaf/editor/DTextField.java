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

import org.hs.generator.*;
import org.hs.util.*;
import org.hs.jfc.*;

/**
 * A generic text field that implements the requisite interfaces for participation
 * in the Decaf masks.  All method implementations are undocumented because they
 * are duh-obvious.
 */
public class DTextField extends JTextField implements DecafTextComponent
{
	protected Color hotColor;
	protected Color coolColor;
	
	protected JLabel label;
	protected Field represents;
	
	public DTextField()
	{
		this(20);
	}
	
	public DTextField(int columns)
	{
		super(columns);
		
		this.coolColor = null;
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
	
	public void register(HotComponent.Arbiter arbiter)
	{
		arbiter.offer(String.class, COPY);
		arbiter.receive(String.class);
	}
	
	public boolean pick(boolean b)
	{
		if (b)
		{
			if (this.coolColor == null)
			{
				this.coolColor = super.getSelectionColor();
			}
			super.setSelectionColor(this.hotColor);
		}
		else
		{
			super.setSelectionColor(this.coolColor);
		}
		repaint();
		
		return true;
	}
	
	public Object give(Class type, int operation)
	{
		return super.getText();
	}
	
	public boolean isCompatible(Object hot)
	{
		return true;
	}
	
	public void take(Object hot)
	{
		super.setText((String)hot);
	}

	public Collection actions()
	{
		return (new Vector());
	}
	
	public void display(GConstruct code)
	{
		super.setText(code.string(this.represents));
	}
	
	public void apply(GConstruct code)
	{
		code.set(this.represents, super.getText());
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