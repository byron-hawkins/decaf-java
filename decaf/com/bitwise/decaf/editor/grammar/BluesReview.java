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
 
package com.bitwise.decaf.editor.grammar;

import java.io.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.bitwise.decaf.editor.*;
import org.hs.generator.*;
import org.hs.jfc.*;
import org.hs.generator.*;

public class BluesReview extends JDialog implements Decaf
{
	static final long serialVersionUID = -1614703715316420434L;

	protected static EnforcementOfficer.ViolationWindow currentViolation;
	public static Color selectedColor = new Color(0x8888DD);
	
	protected BluesList bluesList;
	
	public BluesReview()
	{
		super(DecafEditor.frame);
		
		super.setTitle("Blues Review");
	}
	
	public void assemble()
	{
		JPanel panel = new JPanel();
		panel.add(new JScrollPane(this.bluesList = new BluesList()));
		super.getContentPane().add(panel);
	}
	
	public void start()
	{
		DecafEditor.registerWindow(this);
		super.pack();
		DecafEditor.realEstateManager.place(Utils.RealEstateManager.UPPER_RIGHT, this);
	}
	
	public void display()
	{
		super.setVisible(true);
		super.toFront();
	}
		
	static class BluesList extends JList implements ListCellRenderer, Decaf
	{
		protected Utils.ListModel model;
		
		public BluesList()
		{
			super();
			
			super.setModel(this.model = new Utils.ListModel(EnforcementOfficer.blues));
			super.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			super.addMouseListener(new BluesListener());
			super.setCellRenderer(this);
			super.setFixedCellWidth(DecafEditor.LIST_CELL_WIDTH);
		}

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean hasCellFocus)
		{
			Utils.Renderable renderable = (Utils.Renderable)value;
			JComponent render = renderable.render();
			
			if (isSelected)
			{
				render.setBackground(selectedColor);
			}
			
			return render;
		}
		
		protected void click()
		{
			if (this.model.base().isEmpty())
			{
				return;
			}
			
			if (currentViolation != null)
			{
				currentViolation.dispose();
			}

			EnforcementOfficer clicked = (EnforcementOfficer)super.getSelectedValue();
			currentViolation = clicked.explainViolation();
			currentViolation.start();
		}
			
		class BluesListener extends MouseAdapter
		{
			public void mouseClicked(MouseEvent e)
			{
				super.mouseClicked(e);
				BluesList.this.click();
			}
		}

		public Object writeReplace()
			throws ObjectStreamException
		{
			return (new Serialize(this.model.base()));
		}
		
		static class Serialize implements Decaf
		{
			protected Utils.List content;
			
			private Serialize() {}
			
			public Serialize(Utils.List content)
			{
				this.content = content;
			}
			
			public Object readResolve()
				throws ObjectStreamException
			{
				EnforcementOfficer.blues = this.content;
				return (new BluesList());
			}
		}
	}

	public Object writeReplace()
		throws ObjectStreamException
	{
		return (new Serialize(this.bluesList));
	}
	
	static class Serialize implements Decaf
	{
		protected BluesList content;
		
		private Serialize() {}
		
		public Serialize(BluesList content)
		{
			this.content = content;
		}
		
		public Object readResolve()
			throws ObjectStreamException
		{
			BluesReview review = new BluesReview();
			review.bluesList = this.content;
			review.assemble();
			review.start();
			return review;
		}
	}
	
/*
	public void report(Utils.List.Event event)
	{
		switch (event.action()) 
		{
			case Utils.List.Event.ADD:
				this.add((EnforcementOfficer)event.subject());
				break;
			case Utils.List.Event.REMOVE:
				this.remove((EnforcementOfficer)event.subject());
				break;
		}
	}
*/
}