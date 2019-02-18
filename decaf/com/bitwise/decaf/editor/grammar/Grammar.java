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
import javax.swing.tree.*;
import javax.swing.border.*;

import org.hs.generator.*;
import org.hs.util.*;
import org.hs.jfc.*;

import com.bitwise.decaf.editor.*;

/**
 * Grammar is a secondary base class of {@link CodeNode}, representing both {@link Phrase}
 * and {@link Sentence}.  It keeps a {@link #content} member that implements the
 * {@link Content} interface, and maintains state in three boolean statuses:
 * <p>
 * 1) blue, when it contains errors<br>
 * 2) hasFocus, when its content editor has focus<br>
 * 3) perking, when in the Percolator<br>
 * <p>
 * A collection of {@link Officer}, known as the Beat, is maintained by each Grammar 
 * instance; the officers in turn maintain the Grammar's <code>blue</code>, 
 * status.  Grammar is responsible for rendering its contents, and it limits 
 * Grammar editor windows to a single concurrent instance.
 */
public abstract class Grammar extends CodeNode implements Utils.Renderable
{
	static final long serialVersionUID = -1766529245200981601L;

	public static Color gotTheBlues = new Color(0x000088);
	
	protected static Editors editors;

	static
	{
		editors = new Editors();
	}
	
	protected Beat beat;	
	protected boolean blue;
	protected boolean hasFocus;
	protected Content content;
			
	public Grammar(CodeNode parent)
	{
		super(parent);
		
		this.blue = false;
		this.hasFocus = false;
		this.perking = false;
		this.beat = new Beat(this);
		this.content = null;
	}
	
	public abstract void setData(Object o);
	
	public GConstruct getSource()
	{
		return this.content.getSource();
	}
	
	public Content getContent()
	{
		return this.content;
	}
	
	public void setContent(Content content)
	{
		this.clear();
		this.content = content;
		this.content.setHandle(this);
	}

	protected void applyMembers(Copy yourself)
	{
		super.applyMembers(yourself);
		
		this.beat = (Beat)this.beat.clone();
	}

	public void perk(boolean b)
	{
		if (this.perking != b)
		{
			this.perking = b;
			fireEvent(new Event(this, Event.STATE_CHANGED));
		}
	}
	
	public boolean isPerking()
	{
		return this.perking;
	}
	
	public boolean isEmpty()
	{
		return (this.content == null);
	}
	
	public boolean isExpression()
	{
		return (getSource() instanceof GExpression);
	}
	
	public boolean isStatement()
	{
		return (getSource() instanceof GStatement);
	}

	protected void clear()
	{
		removeOfficers(false);
		this.content = null;
	}
	
	protected void addOfficers(boolean foreign, Phrase from)
	{
		Officer joe;
		for (int i = from.beat.size()-1; i >= 0; i--)
		{
			joe = (Officer)from.beat.elementAt(i);
			if (foreign == (joe instanceof Officer.Foreign))
			{
				this.beat.add(joe);
			}
		}
	}
	
	protected void removeOfficers(boolean foreign)
	{
		Officer joe;
		for (int i = this.beat.size()-1; i >= 0; i--)
		{
			joe = (Officer)this.beat.elementAt(i);
			if (foreign == (joe instanceof Officer.Foreign))
			{
				this.beat.remove(joe);
				if (joe instanceof EnforcementOfficer)
				{
					((EnforcementOfficer)joe).retire();
				}
			}
		}
	}
	
	public MaskEditor edit()
	{
		/*
		if (this.beat != null)
		{
			this.beat.violations.clear();
		}
		*/
		
		MaskEditor editor = editors.getEditorFor(this);
		if (editor == null)
		{
			if (this.content == null)
			{
				this.content = new Literal.Data();
			}
			editor = this.content.edit(this);
			if (editor != null)
			{
				editors.openingEditor(editor);
			}
		}
		else
		{
			editor.toFront();
		}
		
		return editor;
	}
	
	public JComponent render()
	{
		if (this.isEmpty())
		{
			return (new Utils.RenderLabel("<none>"));
		}
		
		JComponent render = this.content.getComponent(this);
		
		if (this.blue)
		{
			render.setForeground(gotTheBlues);
		}

		if (this.hasFocus)
		{
			render.setBorder(focusedBorder);
		}
		
		return render;
	}

	public void setBlue(boolean blue)
	{
		if (this.blue != blue)
		{
			this.outgoingEvent.applyStateChange();
			this.blue = blue;
		}
	}
	
	public boolean isBlue()
	{
		return this.blue;
	}
	
	public void focus(boolean b)
	{
		if (this.hasFocus != b)
		{
			this.hasFocus = b;
			fireEvent(new Event(this, Event.STATE_CHANGED));
		}
	}
	
	public boolean hasFocus()
	{
		return this.hasFocus;
	}
	
	public Cup identifyCup()
	{
		return this.parent.identifyCup();
	}

	public boolean isInScope(DVariable variable, Object child)
	{
		return super.parent.isInScope(variable, this);
	}
	
	public Object get(java.lang.reflect.Field field)
	{
		return this.content.get(field);
	}
	
	public void set(java.lang.reflect.Field field, Object value)
	{
		this.content.set(field, value);
	}
	
	public void refresh()
	{
		fireEvent(new Event(this, Event.STATE_CHANGED));
	}
	
	protected void fireEvent(Event event)
	{
		super.fireEvent(event);

		callOfficers();
	}
	
	public void notifyNewData()
	{
		callOfficers();
		super.notifyNewData();
	}
	
	private void callOfficers()
	{
		Iterator iterator = this.beat.iterator();
		while (iterator.hasNext())
		{
			((Officer)iterator.next()).call();
		}
	}

	public void patrol(Officer officer)
	{
		if (officer == null)
		{
			Thread.dumpStack();
			DecafEditor.log("Grammar.patrol(): Attempt to add null officer to " + getClass().getName());
			return;
		}
		
		this.beat.patrol(officer);
		if (officer instanceof EnforcementOfficer)
		{
			((EnforcementOfficer)officer).setData(this);
		}
	}
	
	public void retire(Officer officer)
	{
		this.beat.remove(officer);
	}
	
	public void checkPatrol()
	{
		// cluge zone		
		for (int i = 0; i < this.beat.size(); i++)
		{
			((Officer)this.beat.elementAt(i)).call();
		}
		// end warning
	}
	
	public Object readResolve()
		throws java.io.ObjectStreamException 
	{
		return super.readResolve();
	}
	
	static class Beat extends Utils.SerialVector
	{
		protected int index = 0;
		protected BitSet violations = new BitSet();
		protected Grammar client = null;

		public Beat(Grammar client)
		{
			this.client = client;
		}
		
		public void patrol(Officer officer)
		{
			super.add(officer);
			
			if (officer instanceof EnforcementOfficer)
			{
				EnforcementOfficer e = (EnforcementOfficer)officer;
				if (e.chief != null)
				{
					this.violations.clear(((Patrol)e.chief).id);
				}
				Patrol patrol = new Patrol(e, this.index++);
				patrol.apply(this.violations);
			}
		}
		
		protected void report(Patrol patrol)
		{
			patrol.apply(this.violations);
			this.client.setBlue(this.violations.nextSetBit(0) >= 0);
		}
		
		class Patrol implements EnforcementOfficer.Chief
		{
			protected EnforcementOfficer officer;
			protected int id;
			
			public Patrol(EnforcementOfficer officer, int id)
			{
				this.id = id;
				this.officer = officer;
				this.officer.reportTo(this);
			}
			
			public void apply(BitSet violations)
			{
				if ((this.officer == null) || (this.officer.report()))
				{
					violations.clear(this.id);
				}
				else
				{
					violations.set(this.id);
				}
			}
			
			public void report()
			{
				Beat.this.report(this);
			}
			/*
			public void endAssociation(EnforcementOfficer officer)
			{
				this.officer = null;
				Beat.this.report(this);
			}
			*/
		}
	}

	static class Editors extends WindowAdapter
	{
		protected TreeMap editors;
		
		public Editors()
		{
			this.editors = new TreeMap();
		}
		
		public MaskEditor getEditorFor(CodeNode node)
		{
			Integer key = new Integer(node.id);
			return (MaskEditor)this.editors.get(key);
		}
		
		public void openingEditor(MaskEditor editor)
		{
			editor.addWindowListener(this);
			Integer key = new Integer(editor.mask.data.id);
			this.editors.put(key, editor);
		}
		
		public void windowClosed(WindowEvent event)
		{
			Integer key = new Integer(((MaskEditor)event.getSource()).mask.data.id);
			this.editors.remove(key);
		}
	}
	
	// sometimes a Variable declaration (sentence), other times a variable reference (phrase)
	public static interface Content extends Typed, Decaf, Copy
	{
		public JComponent getComponent(Grammar handle);
		public MaskEditor edit(Grammar handle);
		public void setHandle(Grammar handle);
		public Object get(java.lang.reflect.Field field);
		public void set(java.lang.reflect.Field field, Object value);
		public GConstruct getSource();	// I'm not convinced about this one
	}
	
	public static interface ClassReference
	{
	}
}
