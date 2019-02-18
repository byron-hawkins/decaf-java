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

import org.hs.jfc.*;

import com.bitwise.decaf.editor.config.*;

/**
 * A form specialized to take contraints information from the configuration file.
 * This allows the declaration masks to be displayed in whatever manner developer 
 * chooses.
 */
public class Form extends FormPanel 
{
	public Form()
	{
		super(5,8,5,5);
	}		
	
	protected void addFormComponent(FormComponent component, FormConstraints constraints)
	{
		int row = constraints.row;
		Component label = component.getLabel();
		
		if (label == null)
		{
			super.add(present(component),
					  row,
					  constraints.column,
					  constraints.fillRightPct);
		}
		else
		{
			super.add(label, 
					  present(component),
					  row,
					  constraints.column,
					  constraints.mode,
					  constraints.fillRightPct);
		}
	}

	protected Component present(FormComponent component)
	{
		Component content = component.getComponentView();

		Collection actions = null;
		if (component instanceof MaskComponent)
		{
			actions = ((MaskComponent)component).actions();
		}
		
		if ((actions == null) || actions.isEmpty())
		{
			return content;
		}
		else
		{
			JPanel withButtons = new JPanel();
			withButtons.setLayout(new BoxLayout(withButtons, BoxLayout.Y_AXIS));
			withButtons.add(content);
			withButtons.add(createButtonPanel(actions));
			return withButtons;
		}
	}
	
	protected JPanel createButtonPanel(Collection actions)
	{
		JPanel panel = new JPanel();
		Iterator iterator = actions.iterator();
		Action next;
		while (iterator.hasNext())
		{
			next = (Action)iterator.next();
			panel.add(new JButton(next));
		}
		return panel; 
	}
}