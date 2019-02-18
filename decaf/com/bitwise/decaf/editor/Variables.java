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

abstract class Variables extends DeclarationBox
{
	protected NewVariable newAction;
	protected EditVariable editAction;
	
	protected VariableListener variableListener;
	
	Variables(Field represents, String label)
	{
		super(represents, label);
		
		super.actions.add(this.newAction = new NewVariable());
		super.actions.add(this.editAction = new EditVariable());
		
		super.addListSelectionListener(this.editAction);
		
		this.variableListener = new VariableListener();
	}
	
	public void register(HotComponent.Arbiter arbiter)
	{
		arbiter.receive(GVariable.class, MOVE | COPY);
		
		arbiter.offer(GVariable.class, LINK, ANY);
		arbiter.offer(String.class, COPY);
	}

	public Object give(Class type, int operation)
	{
		if (super.hasMultiple())
		{
			return super.give(type, operation);
		}
		else if (super.hasSingle())
		{
			DVariable selected = (DVariable)super.selection();
			switch (operation)
			{
				case MOVE:
					super.model.base().remove(selected);
				case SAMPLE:
				case LINK:
					return selected;
				case COPY:
					return selected.deepCopy();
			}
		}
		
		return null;
	}

	public boolean isCompatible(Object hot)
	{
		if (hot instanceof HotCollection)
		{
			HotCollection collection = (HotCollection)hot;
			return (collection.isHomogenous() && (collection.getType() == GVariable.class));
		}
				
		return true;
	}
	
	protected void add(Object o)
	{
		if (o instanceof DVariable)
		{
			((DVariable)o).setLocal(this.isLocal());
		}
		super.add(o);
	}

	protected void editSelection()
	{
		DVariable data = (DVariable)Variables.super.getSelectedValue();
		
		VariableEditor editor = new VariableEditor(Variables.this.getClass().getName());
		data.addReferenceListener(Variables.this.variableListener);
		editor.assemble(data);
		editor.start();
	}

	protected ClickListener getClickListener()
	{
		return (new VariableClickListener());
	}
	
	protected abstract boolean isLocal();
			
	class VariableClickListener extends ClickListener
	{
		public void mouseClicked(MouseEvent e)
		{
			if (SwingUtilities.isLeftMouseButton(e) && (e.getClickCount() == 2))
			{
				Variables.this.editSelection();
			}
			super.mouseClicked(e);
		}
	}
	
	class NewVariable extends ActionBase 
	{
		protected DVariable data;
		
		public NewVariable()
		{
			super();
			
			String shortDescription = "Create a new variable";
			super.putValue(Action.SHORT_DESCRIPTION, shortDescription);
			super.putValue(Action.NAME, "New");
		}
		
		public void go(ActionEvent event)
		{
			this.data = new DVariable(Variables.this.isLocal());
			
			VariableEditor editor = new VariableEditor(Variables.this.getClass().getName());
			Variables.this.add(this.data);
			this.data.addReferenceListener(Variables.this.variableListener);
			editor.assemble(this.data);
			editor.start();
		}
	}
	
	class EditVariable extends EditAction 
	{
		protected DVariable data;

		public EditVariable()
		{
			super();
			
			String shortDescription = "Edit the selected variable";
			
			super.putValue(Action.SHORT_DESCRIPTION, shortDescription);
		}
		
		public void go(ActionEvent event)
		{
			Variables.this.editSelection();
		}
	}
	
	class VariableListener implements ReferenceListener 
	{
		public void referenceUpdated(ReferenceEvent event)
		{
			Variables.this.repaint();
		}
		
		public void referenceDeleted(ReferenceEvent event)
		{
		}
	}	
}