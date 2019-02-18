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

public class Locals extends Variables
{
	public Locals()
	{
		super(GConstruct.getField(GMethod.class, "body"), "Local Variables");
	}

	public void apply(GConstruct code)
	{
		GBlock newBlock = new GBlock();
		
		for (int i = 0; i < this.model.getSize(); i++)
		{
			newBlock.statements.add(this.model.getElementAt(i));
		}
		
		GBlock block = ((GMethod)code).body;
		if (!block.isEmpty())
		{
			newBlock.append(block);
		}
		
		code.set(super.represents, newBlock);
	}

	public void display(GConstruct code)
	{
		Object next;
		Iterator iterator = ((GMethod)code).body.statements.iterator();
		while (iterator.hasNext())
		{
			if ((next = iterator.next()) instanceof GVariable)
			{
				super.add((GVariable)next);
			}
		}
	}

	protected boolean isLocal()
	{
		return true;
	}
}