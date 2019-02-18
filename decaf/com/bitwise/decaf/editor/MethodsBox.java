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

import org.hs.jfc.*;
import org.hs.generator.*;

import com.bitwise.decaf.editor.config.MethodDetails;

// put removed methods in the percolator
class MethodsBox extends DeclarationBox
{
	protected NewMethod newAction;
	protected EditMethod editAction;
	
	protected MethodListener methodListener;
	
	protected Cup forCup;
	
	MethodsBox()
	{
		super(GConstruct.getField(GClass.class, "methods"), "Methods");
		
		super.actions.add(this.newAction = new NewMethod());
		super.actions.add(this.editAction = new EditMethod());
		
		super.addListSelectionListener(this.editAction);
		
		this.forCup = null;
		this.methodListener = new MethodListener();
	}
	
	void addMethod(Object o)
	{
		this.forCup.methods.add(o);
	}

	public void register(HotComponent.Arbiter arbiter)
	{
		arbiter.offer(GMethod.class, MOVE, COPY | MOVE);
		arbiter.offer(GMethodCall.class, COPY);
		arbiter.offer(String.class, LINK);
	}
	
	public Object give(Class type, int operation)
	{
		DMethod selected = (DMethod)super.getSelectedValue();
		if (selected == null)
		{
			return null;
		}
		
		if (type == GMethod.class)
		{
			switch (operation)
			{
				case SAMPLE:
					return selected;
				case COPY:
					return selected.deepCopy();
				case MOVE:
					super.model.base().remove(selected);
					return selected;
			}
		}
		if (type == GMethodCall.class)
		{
			return (new GMethodCall(selected.getName()));
		}
		if (type == String.class)
		{
			return selected.getName();
		}
		
		return null;
	}
	
	public boolean isCompatible(Object hot)
	{
		return true;
	}
	
	public void take(Object hot)
	{
		addMethod(hot);
	}
	
	public void display(GConstruct code)
	{
		this.forCup = (Cup)code;

		DMethod next;
		MethodDetails details;
		Iterator iterator = ((Utils.List)code.get(super.represents)).iterator();
		while (iterator.hasNext())
		{
			next = (DMethod)iterator.next();
			details = MethodDetails.getUserMethodDetails(next);
			if (details != null)
			{
				next.apply(details);
			}
		}

		super.display(code);
	}

	protected void editSelection()
	{	
		DMethod data = (DMethod)MethodsBox.super.getSelectedValue();
		
		MethodEditor editor = MethodEditor.getEditorFor(data);
		if (editor == null)
		{
			editor = new MethodEditor();
			editor.assemble(data);
			data.addReferenceListener(methodListener);
			MethodEditor.openingEditor(editor);
			editor.start();
		}
		else
		{
			editor.toFront();
		}
	}	
	
	protected ClickListener getClickListener()
	{
		return (new MethodClickListener());
	}
	
	class MethodClickListener extends ClickListener
	{
		public void mouseClicked(MouseEvent e)
		{
			if (SwingUtilities.isLeftMouseButton(e) && (e.getClickCount() == 2))
			{
				MethodsBox.this.editSelection();
			}
			super.mouseClicked(e);
		}
	}
	
	class NewMethod extends ActionBase 
	{
		protected DMethod data;
		
		public NewMethod()
		{
			super();
			
			super.putValue(Action.SHORT_DESCRIPTION, "Create a new method");
			super.putValue(Action.NAME, "New");
		}
		
		public void go(ActionEvent event)
		{
			try
			{
				this.data = new DMethod(MethodsBox.this.forCup);
			}
			catch (ClassNotFoundException notThrownForCupArguments) {}
			
			MethodEditor editor = new MethodEditor();
			editor.assemble(this.data);
			MethodsBox.this.addMethod(this.data);
			this.data.addReferenceListener(MethodsBox.this.methodListener);
			MethodEditor.openingEditor(editor);
			editor.start();
		}
	}
	
	class EditMethod extends EditAction 
	{
		public EditMethod()
		{
			super();
			
			super.putValue(Action.SHORT_DESCRIPTION, "Edit the selected method");
		}
		
		public void go(ActionEvent event)
		{
			MethodsBox.this.editSelection();
		}
	}
	
	class MethodListener implements ReferenceListener
	{
		public void referenceUpdated(ReferenceEvent event)
		{
			MethodsBox.this.repaint();
		}
		
		public void referenceDeleted(ReferenceEvent event)
		{
			// not picking up this event here
		}
	}
}