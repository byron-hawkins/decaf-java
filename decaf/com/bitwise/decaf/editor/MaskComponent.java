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
import javax.swing.*;

import org.hs.generator.*;
import org.hs.jfc.*;

import org.hs.generator.*;

/** 
 * A MaskComponent is designed for use in one of the declaration masks: {@link ClassMask}, 
 * {@link MethodMask} and {@link VariableMask}.  It represents a single field of a {@link GConstruct},
 * and is expected to make that field available to the user for editing.  A MaskComponent may also produce other code,
 * and need not necessarily participate in the declaration that its containing mask serves.
 * A MaskComponent is installed into the specified mask with an entry in the configuration file.  
 */
public interface MaskComponent extends HotComponent, FormComponent
{
	/**
	 * For each {@link javax.swing.Action} returned, the containing mask will
	 * create a button and place it next to this MaskComponent.
	 */
	public Collection actions();
	
	/**
	 * Called by the containing mask to indicate that this MaskComponent should
	 * obtain a value for the field it represents from <code>code</code> and display it.
	 */
	public void display(GConstruct code);	
	
	/**
	 * Called by the containing mask to indicate that this MaskComponent should
	 * apply its current value to the field it represents on <code>code</code>.
	 */
	public void apply(GConstruct code);
	
	// need to have: 
	// public void setReadOnly(boolean readOnly)
 
/*	
	public void register(Arbiter arbiter);	
	public boolean pick();
	public Object give(Class type, int operation);
	public void take(Object hot);
*/ 	
}


