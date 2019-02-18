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

import com.bitwise.decaf.editor.grammar.*;

// when the model is set, we need to flip through its contents
// and add this (subclass specific) to appropriate items

// not a problem for ParagraphBox, because the GrammarMask will instantiate it
// with data, which will automatically be listened to.  

public abstract class DListBox extends JList implements FormComponent, HotComponent, ListCellRenderer, ListSelectionListener
{
	protected static final double thinColorExtent = 0.3;

	protected Utils.ListModel model;
	protected ClickListener clickListener;
	protected Vector actions;
	
	protected int[] previousSelection;
	protected int[] currentSelection;
	
	protected Color hotColor;
	protected boolean hot;
	
	protected JLabel label;

	public DListBox(String label)
	{
		this(label, new Utils.ListModel());
	}
	
	public DListBox(String label, Utils.ListModel model)
	{
		super();
		
		this.model = model;
		super.setModel(this.model);
		super.setFixedCellWidth(DecafEditor.LIST_CELL_WIDTH);
		
		this.actions = new Vector();
		
		this.label = new JLabel(label);
		this.hot = false;
		
		this.previousSelection = new int[] {};
		this.currentSelection = new int[] {};

		super.addListSelectionListener(this);
		super.setCellRenderer(this);
		super.addMouseListener(this.clickListener = this.getClickListener());
	}
	
	protected ClickListener getClickListener()
	{
		return (new ClickListener());
	}
	
	protected void add(Object o)
	{
		this.model.base().add(o);
	}
	
	protected void insert(Object o)
	{
		this.model.base().add(this.clickListener.getInsertIndex(), o);
	}
	
	protected void add(Iterator i)
	{
		while (i.hasNext())
		{
			this.add(i.next());
		}
	}
	
	protected void insert(Iterator i)
	{
		while (i.hasNext())
		{
			this.insert(i.next());
		}
	}
	
	public Object give(Class type, int operation)
	{
		if (this.hasMultiple())
		{
			Collection give;
			switch (operation)
			{
				case LINK:
					return null;
				case MOVE:
					give = this.model.remove(super.getSelectedIndices());
					break;
				case COPY:
					give = Utils.deepCopy(this.collectSelection());
					break;
				case SAMPLE:
					give = this.collectSelection();
					break;
				default:
					return null;
			}
			return (new HotCollection(GStatement.class, give));
		}

		return null;
	}
	
	public boolean isCompatible(Object hot)
	{
		return true;
	}
	
	public void take(Object hot)
	{
		this.clickListener.notifyPutAt(DecafEditor.hotStation.getLastPointPut());
		if (hot instanceof HotCollection)
		{
			insert(((HotCollection)hot).iterator());
		}
		else
		{
			insert(hot);
		}
		super.repaint();
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
		return Utils.createListView(this);
	}
	
	protected boolean hasSelection()
	{
		return (super.getSelectedValues().length > 1);
	}
	
	protected boolean hasMultiple()
	{
		return (super.getSelectedValues().length > 1);
	}
	
	protected boolean hasSingle()
	{
		return (super.getSelectedValues().length == 1);
	}
	
	protected Collection collectSelection()
	{
		Vector selection = new Vector();
		Object[] selected = super.getSelectedValues();
		for (int i = 0; i < selected.length; i++)
		{
			selection.add(selected[i]);
		}
		return selection;
	}
	
	protected Object selection()
	{
		return super.getSelectedValue();
	}
	
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean hasCellFocus)
	{
		Utils.Renderable renderable = (Utils.Renderable)value;
		JComponent render = renderable.render();
		
		Background background = new Background();
		
		if (isSelected)
		{
			if (this.hot)
			{
				background.hot();
			}
			else
			{
				background.selected();
			}
		}
		else
		{
			background.unselected();
		}
	
		render.setBackground(background.color());
		
		if (hasCellFocus)
		{
			render.setBorder(CodeNode.selectedBorder);
		}
		
		return render;
	}
	
	class Background
	{
		protected Color color;
		protected double shadeFactor;
		
		public Background()
		{
			this.color = null;
			this.shadeFactor = ((double)DListBox.this.currentSelection.length / (double)DListBox.this.model.getSize()) * DListBox.thinColorExtent;
		}
	
		public void hot()
		{
			this.color = DListBox.this.hotColor;
			this.gradeColor();
		}
		
		public void selected()
		{
			this.color = DecafEditor.selectedColor;
			this.gradeColor();
		}
		
		public void unselected()
		{
			this.color = Color.white;
		}
		
		public Color color()
		{
			return this.color;
		}
		
		// really need to increase the surrounding colors
		protected void gradeColor()
		{
			int red = limit((int)(this.color.getRed() + (this.color.getRed() * this.shadeFactor)));
			int green = limit((int)(this.color.getGreen() + (this.color.getGreen() * this.shadeFactor)));
			int blue = limit((int)(this.color.getBlue() + (this.color.getBlue() * this.shadeFactor)));
			
			this.color = new Color(red, green, blue);
		}
		
		protected int limit(int spectrum)
		{
			return (spectrum > 0xFF)?0xFF:spectrum;
		}
	}
	
	public void valueChanged(ListSelectionEvent event)
	{
		this.previousSelection = this.currentSelection;
		this.currentSelection = super.getSelectedIndices();
	}
	
	public boolean pick(boolean b)
	{
		boolean retainSelection = false;
		int lastIndexClicked = super.locationToIndex(DecafEditor.hotStation.getLastPointPicked());
		for (int i = 0; i < this.previousSelection.length; i++)
		{
			if (this.previousSelection[i] == lastIndexClicked)
			{
				retainSelection = true;
			}
		}
		
		if (retainSelection)
		{
			int[] keepPreviousSelection = this.previousSelection;
			super.setSelectedIndices(this.previousSelection);
			this.previousSelection = keepPreviousSelection;
		}
		
		this.hot = b;
		super.repaint();
		return true;
	}
	
	public Collection actions()
	{
		return this.actions;
	}

	public void setHotColor(Color hot)
	{
		this.hotColor = hot;
	}
	
	protected class ClickListener extends MouseAdapter
	{
		protected int insertIndex;
		
		public ClickListener()
		{
			this.insertIndex = 0;
		}
		
		public int getInsertIndex()
		{
			return this.insertIndex;
		}
		
		public void notifyPutAt(Point put)
		{
			this.insertIndex = locationToIndex(put);
			if (this.insertIndex < 0)
			{
				this.insertIndex = 0;
			}
		}
		
		public void mousePressed(MouseEvent e)
		{
			this.insertIndex = locationToIndex(new Point(e.getX(), e.getY()));
			if (this.insertIndex < 0)
			{
				this.insertIndex = 0;
			}
		}
	}

	abstract class EditAction extends ActionBase implements ListSelectionListener
	{
		public EditAction()
		{
			super();
			
			super.setEnabled(false);
			
			super.putValue(Action.NAME, "Edit");
		}
		
		public void valueChanged(ListSelectionEvent event)
		{
			super.setEnabled(DListBox.this.getSelectedIndex() >= 0);
		}
	}

	public Object writeReplace()
		throws java.io.ObjectStreamException
	{
		throw (new Utils.SerialException(getClass()));
	}
}
