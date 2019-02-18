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
import javax.swing.border.*;

import org.hs.jfc.*;
import org.hs.util.*;
import org.hs.generator.*;

import com.bitwise.decaf.editor.config.*;

/**
 * A specialization of Form that extracts a list of components from 
 * a configuration file entry and places them into the superclass.
 */
public abstract class Mask extends Form implements MaskComponent
{
	protected MaskConfig config;
	protected DiscussionArea discussionArea;
	protected Vector maskComponents;
	protected Vector receives;
	protected Vector offers;
	protected Vector actions;
	protected String label;
	protected Border hotBorder;
	
	protected Mask()
	{
		this(null);
	}
	
	Mask(MaskConfig config)
	{
		super();
		
		this.config = config;
		this.maskComponents = new Vector();
		this.receives = new Vector();
		this.offers = new Vector();
		this.actions = new Vector();
		this.label = null;

		this.discussionArea = new DiscussionArea();
		super.addFormComponent(this.discussionArea, new FormConstraints(-1000,0));
	}
	
	protected void addMaskComponents()
	{
		try
		{
			Iterator componentConfigs = this.config.getOptionalComponents().iterator();
			OptionalComponent componentConfig;
			FormConstraints constraints;
			Class componentClass;
			MaskComponent component;
			while (componentConfigs.hasNext())
			{
				componentConfig = (OptionalComponent)componentConfigs.next();
				constraints = componentConfig.getFormConstraints();
				
				componentClass = Class.forName(componentConfig.getClassname());
				component = (MaskComponent)componentClass.newInstance();
	
				addMaskComponent(component, constraints);
			}
		}
		catch (Exception e)
		{
			DecafEditor.log(e);
		}
	}

	protected void addMaskComponent(MaskComponent component, FormConstraints constraints)
	{
		this.maskComponents.add(component);
		super.addFormComponent(component, constraints);
	}
	
	protected void setDiscussion(String discussion)
	{
		this.discussionArea.setText(discussion);
	}

	public void register(HotComponent.Arbiter arbiter)
	{
	}
	
	public boolean pick(boolean b)
	{
		return false;
	}
	
	public Object give(Class type, int operation)
	{
		return null;
	}

	public boolean isCompatible(Object hot)
	{
		return false;
	}
	
	public void take(Object hot)
	{
	}

	public Component getLabel()
	{
		return (new JLabel(this.label));
	}
	
	public Component getComponent()
	{
		return this;
	}
	
	public Component getComponentView()
	{
		// maybe the Editor?
		return this;
	}
	
	public Collection actions()
	{
		return this.actions;
	}
	
	/**
	 * if <code>code</code> implements {@link Configurable}, place its discussion
	 * content in a text area at the top of the mask.
	 */
	public void display(GConstruct code)
	{
		if (code instanceof Configurable)
		{
			String discussion = ((Configurable)code).getDiscussion();
			if ((discussion != null) && (discussion.length() > 0))
			{
				this.discussionArea.setVisible(true);
				this.discussionArea.setText(discussion);
			}
			else
			{
				this.discussionArea.setVisible(false);
			}
		}
		
		Iterator iterator = this.maskComponents.iterator();
		while (iterator.hasNext())
		{
			((MaskComponent)iterator.next()).display(code);
		}
		
		super.revalidate();
	}
	
	public void apply(GConstruct code)
	{
		Iterator iterator = this.maskComponents.iterator();
		while (iterator.hasNext())
		{
			((MaskComponent)iterator.next()).apply(code);
		}
	}
	
	public void setHotColor(Color hot)
	{
		this.hotBorder = BorderFactory.createLineBorder(hot, 1);
	}
	
	public abstract void assemble();
	
	static class DiscussionArea extends JTextArea implements FormComponent
	{
		protected JScrollPane pane;
		
		public DiscussionArea()
		{
			super(100, 30);
			
			//super.setColumns();
			super.setWrapStyleWord(true);
			super.setLineWrap(true);
			super.setEditable(false);
			super.setText("");
			
			this.pane = new JScrollPane(this);
			this.setVisible(false);
		}
		
		public Component getLabel()
		{
			return null;
		}
		
		public Component getComponent()
		{
			return this;
		}
		
		public Component getComponentView()
		{
			return this.pane;
		}
		
		public void setVisible(boolean visible)
		{
			if (visible)
			{
				//this.pane.setPreferredSize(super.getPreferredSize());
				this.pane.setPreferredSize(new Dimension(350, 70));
			}
			else
			{
				this.pane.setPreferredSize(new Dimension(0,0));
				super.setPreferredSize(new Dimension(0,0));
			}
			this.pane.setVisible(visible);
			super.setVisible(visible);

			//System.err.println("Vertical scrollbar value is " + this.pane.getVerticalScrollBar().getValue());
		}

	
		public Object writeReplace()
			throws java.io.ObjectStreamException
		{
			throw (new Utils.SerialException(getClass()));
		}
	}
}