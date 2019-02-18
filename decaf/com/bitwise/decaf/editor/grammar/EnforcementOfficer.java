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
import javax.swing.border.*;

import org.hs.generator.*;
import org.hs.jfc.*;

import com.bitwise.decaf.editor.Utils;
import com.bitwise.decaf.editor.DecafEditor;

/**
 * Realtime code validation is performed by subclasses of the 
 * <code>EnforcementOfficer</code> class, each of which patrols a 
 * single Grammar instance for compliance with a particular
 * rule or set of rules (typically from the 
 * <a href="http://java.sun.com/docs/books/jls/second_edition/html/jTOC.doc.html">JLS</a>.  
 * An <code>EnforcementOfficer</code>
 * maintains a boolean violation status, which, when true, is
 * accompanied by a String description of the violation.  
 * When called, subclasses are expected to call back to this
 * class with either {@link violation(String)} or 
 * {@link noViolation()}, thereby establishing the appropriate
 * current status.  <code>EnforcementOfficer</code> reports
 * its changes in status to its chief, which by convention is 
 * a member of the Grammar it patrols.
 *
 * @see Officer.call()
 */
public abstract class EnforcementOfficer extends Officer implements Utils.Renderable, Grammar.Listener
{
	static final long serialVersionUID = 8168833259071894224L;

	protected static final Renderer renderer = new Renderer();
	protected static int index = 0;
	
	/**
	 * A collection of the <code>EnforecementOfficer</code>s who
	 * currently are declaring a state of violation.  The associated
	 * Grammars have been turned blue (by Grammar.render()), and
	 * hence this list comprises the model for the {@link BluesReview}.
	 */
	public static Utils.List blues;
	
	static
	{
		blues = new Utils.List();
	}
	
	protected String violation;
	protected Grammar data;
	protected int id;
		
	protected Chief chief;

	/**
	 * Abstract constructor; initially declares a state of no violation,
	 * as it is not patrolling anything.
	 */	
	public EnforcementOfficer()
	{
		this.violation = null;
		this.data = null;
		this.id = index++;
	}
	
	/**
	 * Set the <code>chief</code> to which this officer reports.
	 */
	public void reportTo(Chief chief)
	{
		/*
		if (this.chief != null)
		{
			this.chief.endAssociation(this);
		}
		*/
		
		this.chief = chief;
	}

	/**
	 * @return true if this officer is currently declaring a state of violation.
	 */	
	public boolean report()
	{
		return (this.violation == null);
	}
	
	/**
	 * @param data the object of this officer's scrutiny.
	 */
	public void setData(Grammar data)
	{
		this.data = data;
		this.data.addListener(this);
	}

	/**
	 * Obtain a window containing and explanation of the violation currently
	 * being declared by this officer.  Behavior is undefined if this officer
	 * is not currently declaring a violation.
	 */
	public ViolationWindow explainViolation()
	{
		// ask ClassEditor to close all other open code windows
		
		if (true)	// some setting
		{
			this.data.editPathToCup();	
		}
		else
		{
			this.data.edit();	
		}
		
		
		return (new ViolationWindow(this));
	}
	
	protected void noViolation()
	{
		this.violation = null;
		//blues.remove(this);
		this.chief.report();
	}
	
	protected void violation(String violation)
	{
		this.violation = violation;
		
		if (this.data.isPerking())
		{
			blues.remove(this);
		}
		else
		{
			blues.add(this);
		}
		
		this.chief.report();
	}

	/**
	 * Obtain a displayable text representation of the path from the
	 * method body to the Grammar that this officer patrols.
	 */
	public JComponent render()
	{
		return renderer.render(this);
	}

	/**
	 * Watch for grammars being placed in the Percolator.
	 */
	public void contentChanged(Grammar.Event event)
	{
		if (this.data.isPerking())
		{
			blues.remove(this);
		}
		else if (this.violation != null)
		{
			blues.add(this);
		}
	}

	/**
	 * @return true if <code>o</code> has the same id 
	 * as <code>this.id</code> (a protected member, set
	 * uniquely per constructor call).
	 */	
	public boolean equals(Object o)
	{
		if (o instanceof EnforcementOfficer)
		{
			return (((EnforcementOfficer)o).id == this.id);
		}
		return false;
	}
	
	/**
	 * Retire this officer, removing him from the blues list and releasing
	 * his references to other objects.
	 */
	public void retire()
	{
		blues.remove(this);
	}
	
	/**
	 * Displays the violation declared by an {@link EnforcementOfficer}
	 */
	public static class ViolationWindow extends JWindow
	{
		protected EnforcementOfficer officer;
		
		/**
		 * Create a <code>ViolationWindow</code> to display the violation currently being 
		 * declared by <code>officer</code>.  Defers construction of the 
		 * violation text component until {@link start()}.
		 */
		public ViolationWindow(EnforcementOfficer officer)
		{
			super(DecafEditor.bluesReview);
			this.officer = officer;
			super.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
		}
		
		/**
		 * Build the window's main panel and display it on screen.
		 */
		public void start()
		{
			DecafEditor.registerWindow(this);
	
			JPanel panel = new JPanel();
			
			JTextArea text = new JTextArea(this.officer.violation, 10, 30);
			text.setLineWrap(true);
			text.setWrapStyleWord(true);
			text.addMouseListener(new ClickListener());
			panel.add(new JScrollPane(text));
			
			super.getContentPane().add(panel);
			super.pack();
			super.show();
		}
		
		class ClickListener extends MouseAdapter
		{
			public void mouseClicked(MouseEvent e)
			{
				ViolationWindow.this.dispose();
			}
		}
	
		public Object writeReplace()
			throws java.io.ObjectStreamException
		{
			throw (new Utils.SerialException(getClass()));
		}
	}
	
	static class Renderer 
	{
		public JComponent render(EnforcementOfficer officer)
		{
			return officer.data.describePathToCup();
		}
	}
	
	static interface Chief extends java.io.Serializable
	{
		public void report();
		//public void endAssociation(EnforcementOfficer officer);
	}
}

