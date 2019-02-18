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
import javax.swing.event.*;

import org.hs.generator.*;
import org.hs.jfc.*;

abstract class DeclarationBox extends DListBox implements MaskComponent, HotComponent
{
	protected Field represents;
	
	DeclarationBox(String label)
	{
		this(null, label);
	}
	
	DeclarationBox(Field represents, String label)
	{
		super(label);
		
		super.actions.add(super.model.brewAction(this));
		
		this.represents = represents;
	}
	
	public void apply(GConstruct code)
	{
	}
	
	public void display(GConstruct code)
	{
		this.model.setBase((Utils.List)code.get(this.represents));
	}
}
