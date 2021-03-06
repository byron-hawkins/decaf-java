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

import org.hs.jfc.*;
import org.hs.util.*;
import org.hs.generator.*;

class Discussion extends JWindow
{
	protected JEditorPane content;
	
	public Discussion(String discussion)
	{
		super();
		
		super.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);

		this.content = new JEditorPane();
		this.content.setEditable(false);
		this.content.setText(discussion);
	}
	
	public void assemble()
	{
		super.addMouseListener(new ClickListener());
		
		JPanel container = new JPanel();
		container.add(new JScrollPane(this.content));
		super.getContentPane().add(container);
		super.pack();
	}
	
	public void displayFor(Component component)
	{
		super.setLocationRelativeTo(component);
		super.show();
	}

	class ClickListener extends MouseAdapter
	{
		public void mouseClicked(MouseEvent e)
		{
			Discussion.this.dispose();
		}
	}
	
	public Object writeReplace()
		throws java.io.ObjectStreamException
	{
		throw (new Utils.SerialException(getClass()));
	}
}