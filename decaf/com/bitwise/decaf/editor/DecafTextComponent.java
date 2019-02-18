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

import java.awt.event.*;

import org.hs.generator.*;

/**
 * A further specification of {@link MaskComponent} for declaration fields that
 * must provide the user with a plain text input mechanism.  For example, the 
 * {@link MethodMask} looks for a DecafTextComponent as its name field, with the
 * assumption that the user should be able tot type in whatever name they like.
 */
public interface DecafTextComponent extends MaskComponent, GenericComponent
{
	/**
	 * Set the <code>columns</code> of text that are visible in the component
	 * (not a text extent limit).  
	 */
	public void setColumns(int columns);
	
	/**
	 * Return the text currently occupying this components input area.
	 */
	public String getText();
	
	/**
	 * Add a key listener to the text input component of this DecafTextComponent.
	 */
	public void addKeyListener(KeyListener listener);
}