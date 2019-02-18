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
import javax.swing.event.*;

import org.hs.jfc.*;
import org.hs.util.*;
import org.hs.generator.*;

import at.dms.kjc.*;

import com.bitwise.decaf.editor.config.TypeDetails;

/**
 * This static class provides facilities for a number of common Decaf operations
 * that are distinct from an instance of any particular thing, or that cannot be
 * attached to the appropriate source (Type methods, for instance, cannot be 
 * included in the interface Type, by language rule).  Also included are some 
 * specializations of common JDK constructs, which are declared as public members 
 * of this class to prevent package sprawl.  
 */
public class Utils
{
	/**
	 * The type <code>void</code> (not a singleton; identifiable by equals()).
	 */
	public static Type.VoidType voidType;

	/**
	 * The type <code>null</code> (not a singleton; identifiable by equals()).
	 */
	public static Type.NullType nullType;

	/**
	 * Placeholder for Type instances prior to the user's application of any 
	 * particular type.  I've chosen not to implement a default for this situation
	 * because it would cause problems with type-based rules.  
	 */
	public static Type.NoType noType;

	/**
	 * The type <code>byte</code> (not a singleton; identifiable by equals()).
	 */
	public static Type BYTE;

	/**
	 * The type <code>double</code> (not a singleton; identifiable by equals()).
	 */
	public static Type DOUBLE;

	/**
	 * The type <code>long</code> (not a singleton; identifiable by equals()).
	 */
	public static Type LONG;

	/**
	 * The type <code>short</code> (not a singleton; identifiable by equals()).
	 */
	public static Type SHORT;

	/**
	 * The type <code>char</code> (not a singleton; identifiable by equals()).
	 */
	public static Type CHAR;

	/**
	 * The type <code>int</code> (not a singleton; identifiable by equals()).
	 */
	public static Type INT;

	/**
	 * The type <code>float</code> (not a singleton; identifiable by equals()).
	 */
	public static Type FLOAT;

	/**
	 * The type <code>java.lang.String</code> (not a singleton; identifiable by equals()).
	 */
	public static Type STRING;

	/**
	 * The type <code>void</code> (not a singleton; identifiable by equals()).
	 */
	public static Type VOID;

	/**
	 * The type <code>boolean</code> (not a singleton; identifiable by equals()).
	 */
	public static Type BOOLEAN;

	/**
	 * Initialize the public static {@link Type} members.
	 */	
	public static void init()
	{
		voidType = new Type.VoidType();
		nullType = new Type.NullType();
		noType = new Type.NoType();

		BYTE = getType(boolean.class);
		DOUBLE = getType(double.class);
		LONG = getType(long.class);
		SHORT = getType(short.class);
		CHAR = getType(char.class);
		INT = getType(int.class);
		FLOAT = getType(float.class);
		STRING = getType(String.class);
		VOID = getType(void.class);
		BOOLEAN = getType(boolean.class);
	}

	/**
	 * Wrap a {@link GType} in a {@link Type}.
	 */	
	public static Type wrapType(GType source)
		throws ClassNotFoundException
	{/*
		if (source.isPrimitive())
		{
			return (new PrimitiveType(source));
		}
		*/
		return Utils.getType(source.getQualifiedName());
	}

	/**
	 * Get (creating if necessary) the {@link Type} corresponding to <code>type</code>.
	 */	
	public static Type getType(Class type)
	{
		return getType(type, 0);
	}
	
	/**
	 * Get (creating if necessary) the {@link Type} corresponding to an array of
	 * <code>type</code> with <code>dimensions</code>.
	 */	
	public static Type getType(Class type, int dimensions)
	{
		Type lookup = Type.cache.get(type, dimensions);
		if (lookup == null)
		{
			if (type.isPrimitive())
			{
				lookup = new PrimitiveType(type, dimensions);
				Type.cache.put(type, dimensions, lookup);
			}
			else
			{
				Type.cache.put(type, dimensions, lookup = new BinaryType(type, dimensions));
				((BinaryType)lookup).init();
			}
		}

		return lookup;
	}
	
	/**
	 * Get (creating if necessary) the {@link Type} corresponding to <code>clasname</code>.
	 */	
	public static Type getType(String classname)
		throws ClassNotFoundException
	{
		return getType(classname, 0);
	}
	
	/**
	 * Get (creating if necessary) the {@link Type} corresponding to an array of
	 * <code>classname</code> with <code>dimensions</code>.
	 */	
	public static Type getType(String classname, int dimensions)
		throws ClassNotFoundException
	{
		if (classname.indexOf(".") < 0)
		{
			if (classname.equals("null"))
			{
				return wrapType(GType.NULL);
			}
			if (classname.equals("void"))
			{
				return Utils.voidType;
			}
			if (classname.equals("int"))
			{
				return getType(int.class, dimensions);
			}
			if (classname.equals("boolean"))
			{
				return getType(boolean.class, dimensions);
			}
			if (classname.equals("long"))
			{
				return getType(long.class, dimensions);
			}
			if (classname.equals("short"))
			{
				return getType(short.class, dimensions);
			}
			if (classname.equals("double"))
			{
				return getType(double.class, dimensions);
			}
			if (classname.equals("float"))
			{
				return getType(float.class, dimensions);
			}
			if (classname.equals("char"))
			{
				return getType(char.class, dimensions);
			}
			if (classname.equals("byte"))
			{
				return getType(byte.class, dimensions);
			}
		}
		return getType(Class.forName(classname), dimensions);
	}
	
	/**
	 * Bundle <code>list</code> in a JPanel with up and down arrow buttons
	 * attached to the right side.
	 */
	public static JPanel createListView(JList list)
	{
		JPanel view = new JPanel();
		ListModel model = (ListModel)list.getModel();
		JPanel arrowPanel = new JPanel(new GridLayout(2,1));
		arrowPanel.add(new JButton(model.upAction(list)));
		arrowPanel.add(new JButton(model.downAction(list)));
		view.add(new JScrollPane(list));
		view.add(arrowPanel);
		return view;
	}
	
	/**
	 * Assuming <code>c</code> is a collection of {@link org.hs.util.Copy}, this method
	 * iterates the collection and adds a {@link org.hs.util.Copy#deepCopy()} of each 
	 * element to the returned collection.
	 */
	public static Collection deepCopy(Collection c)
	{
		Vector copy = new Vector();
		Iterator iterator = c.iterator();
		while (iterator.hasNext())
		{
			copy.add(((Copy)iterator.next()).deepCopy());
		}
		return copy;
	}

	/**
	 * CollectionMap stores key-collection pairs, and allows a single value to
	 * be added to a collection by key reference.
	 */	
	public static class CollectionMap extends TreeMap
	{
		/**
		 * Add <code>value</code> to the collection associated with <code>key</code>,
		 * creating a new collection if necessary.
		 */
		public void add(Object key, Object value)
		{
			Collection values = (Collection)super.get(key);
			if (values == null)
			{
				values = new Vector();
				super.put(key, values);
			}
			values.add(value);
		}
		
		/**
		 * Just like it says:
		 */
		public Collection getCollection(Object key)
		{
			return (Collection)super.get(key);
		}
	}

	/**
	 * The empty_vector serves as a response to abstract and interface methods 
	 * that require the return of a collection in the case that the implementing 
	 * class has nothing to return.  
	 */	
	public static final EmptyVector empty_vector = new EmptyVector();
	
	/**
	 * The EmptyVector rejects all attempts to add elements to it, such that a single instance 
	 * of it may be used universally as a "no thanks" reponse from implementations of abstract
	 * and interface methods requiring a return value of collection.
	 */
	public static class EmptyVector extends Vector
	{
		public void add(int index, Object o)
		{
			throw (new RuntimeException("Can't add an object to " + getClass().getName() + ".  Go get your own vector  :P"));
		}
		
		public boolean add(Object o)
		{
			throw (new RuntimeException("Can't add an object to " + getClass().getName() + ".  Go get your own vector  :P"));
		}
		
		public void addElement(Object o)
		{
			throw (new RuntimeException("Can't add an object to " + getClass().getName() + ".  Go get your own vector  :P"));
		}
		
		public boolean addAll(Collection c)
		{
			throw (new RuntimeException("Can't add a collection to " + getClass().getName() + ".  Go get your own vector  :P"));
		}
		
		public boolean addAll(int index, Collection c)
		{
			throw (new RuntimeException("Can't add a collection to " + getClass().getName() + ".  Go get your own vector  :P"));
		}
	}
	
	// assumes each entry is unique, and that modifying methods
	// not overridden here will not be called
	/**
	 * <code>List</code> maintains an ordered set (rejecting duplicate additions
	 * on the basis of equals()), and notifies {@link List.Listener}s when its 
	 * contents change.  Its methods do nothing more than the obvious, so I'm
	 * neglecting to document them all.
	 */
	public static class List extends SerialVector
	{
		protected transient Vector listeners;
		
		public List()
		{
			super();
			this.listeners = new Vector();
		}
		
		public List(Collection c)
		{
			this();
			
			addAll(c);
		}
		
		public void addListener(Listener listener)
		{
			this.listeners.add(listener);
		}
		
		public void removeListener(Listener listener)
		{
			this.listeners.remove(listener);
		}
		
		public void add(int index, Object o)
		{
			super.add(index, o);
			report(new Event(this, Event.ADD, index, o));
		}
		
		public boolean add(Object o)
		{
			if (super.contains(o))
			{
				return false;
			}
			else
			{
				super.add(o);
				report(new Event(this, Event.ADD, super.indexOf(o), o));
				return true;
			}
		}
		
		public boolean addAll(Collection c)
		{
			boolean added = false;
			Iterator iterator = c.iterator();
			while (iterator.hasNext())
			{
				added |= this.add(iterator.next());
			}
			return added;
		}
		
		public boolean addAll(int index, Collection c)
		{
			Iterator iterator = c.iterator();
			boolean first = true;
			while (iterator.hasNext())
			{
				if (first)
				{
					this.add(index, iterator.next());
					first = false;
				}
				else
				{
					this.add(index+1, iterator.next());
				}
			}
			return !first;
		}
		
		public Object remove(int index)
		{
			Object o = super.remove(index);
			if (o != null)
			{
				report(new Event(this, Event.REMOVE, index, o));
			}
			return o;
		}
		
		public boolean remove(Object o)
		{
			int index = super.indexOf(o);
			boolean removed = super.remove(o);
			if (removed)
			{
				report(new Event(this, Event.REMOVE, index, o));
			}
			return removed;
		}
			
		public boolean removeAll(Collection c)
		{
			boolean added = false;
			Iterator iterator = c.iterator();
			while (iterator.hasNext())
			{
				added |= this.remove(iterator.next());
			}
			return added;
		}
			
		protected void report(Event event)
		{
			Iterator iterator = this.listeners.iterator();
			while (iterator.hasNext())
			{
				((Listener)iterator.next()).report(event);
			}
		}
		
		protected Object readResolve()
			throws java.io.ObjectStreamException
		{
			this.listeners = new Vector();
			return this;
		}
		
		/**
		 * This <code>Event</code> is passed to {@link Listener}s of {@link List}
		 * when its contents change.
		 */
		public static class Event extends EventObject
		{
			/**
			 * An option for {@link #action()}, indicating an addition to 
			 * the corresponding {@link List}.
			 */
			public static final int ADD = 0;

			/**
			 * An option for {@link #action()}, indicating a removal from  
			 * the corresponding {@link List}.
			 */
			public static final int REMOVE = 1;
			
			protected int action;
			protected int index;
			protected Object subject;
			
			Event(List source, int action, int index, int subject)
			{
				this(source, action, index, new Integer(subject));
			}
			
			Event(List source, int action, int index, Object subject)
			{
				super(source);
				
				this.action = action;
				this.index = index;
				this.subject = subject;
			}

			/**
			 * Return the action this Event is reporting: one of {@link ADD}, {@link REMOVE}.
			 */			
			public int action()
			{
				return this.action;
			}
			
			/**
			 *	The index at which the associated action took place.  
			 */
			public int index()
			{
				return this.index;
			}
			
			/**
			 * Returns the object that was added or removed (per {@link #action()}).
			 */
			public Object subject()
			{
				return this.subject;
			}
			
			/**
			 * Vague description of this event.
			 */
			public String toString()
			{
				return "source <" + this.source + "> " + ((this.action == 0)?"add":"remove") + " at " + index + ": " + subject;
			}
		}
		
		/**
		 * Radio frequency for {@link List}.
		 */
		public static interface Listener 
		{
			public void report(Event event);
		}
	}
	
	/**
	 * A {@link javax.swing.ListModel} based on a {@link List}, which may be applied
	 * to the model at any time.  Numerous convenience methods are implemented.  The 
	 * modification of a region is reported to the listeners as a series of individual
	 * modifications, which is so much easier than the suggested mechanism that I 
	 * wonder why anyone would implement anything different.  If you know of a good 
	 * reason, please <a href="mailto:bitwise@cablespeed.com">tell me</a>.
	 */
	public static class ListModel implements javax.swing.ListModel, List.Listener
	{
		protected List base;
		protected Vector listeners;

		/**
		 * Create a ListModel with a new {@link List} as its basis.
		 */		
		public ListModel()
		{
			this(new List());
		}
		
		/**
		 * Create a ListModel with <code>base</code> as its basis.
		 */
		public ListModel(List base)
		{
			super();
			
			this.base = base;
			this.listeners = new Vector();
			
			this.base.addListener(this);
		}
		
		/**
		 * Obtain reference to this ListModel's base.
		 */
		public List base()
		{
			return this.base;
		}
		
		/**
		 * Move the elements at the specified <code>indices</code> up one
		 * step if possible.  Will move as many elements in a multiple 
		 * discontiguous selection as have a place to go.  Will of course
		 * never change the order of any items within their respective 
		 * selected or non-selected set.  
		 */
		public synchronized void moveUp(int[] indices)
		{
			for (int i = 0; i < indices.length; i++)
			{
				if (indices[i] == i)
				{
					// no where run to...
					continue;
				}
				this.base.add(indices[i]-1, this.base.remove(indices[i]));
				indices[i]--;
			}
		}
		
		/**
		 * Move the elements at the specified <code>indices</code> down one
		 * step if possible.  Will move as many elements in a multiple 
		 * discontiguous selection as have a place to go.  Will of course
		 * never change the order of any items within their respective 
		 * selected or non-selected set.  
		 */
		public synchronized void moveDown(int[] indices)
		{
			int max = this.base.size()-1;
			int maxIndex = indices.length-1;
			for (int i = maxIndex; i >= 0; i--)
			{
				if (indices[i] == (max + i - maxIndex))
				{
					// no where to hide!
					continue;
				}
				this.base.add(indices[i]+1, this.base.remove(indices[i]));
				indices[i]++;
			}
		}

		/**
		 * Remove the list elements at <code>indices</code>.
		 */		
		public synchronized Collection remove(int[] indices)
		{
			Vector removal = new Vector();
			for (int i = (indices.length-1); i >= 0; i--)
			{
				removal.add(this.base.remove(indices[i]));
			}
			return removal;
		}
		
		/**
		 * Replace the existing basis of this ListModel with <code>base</code>, notifying 
		 * listeners as if the entire interval <code>[0, base.size()-1]</code> has been added.
		 */
		public void setBase(List base)
		{
			this.base = base;
			this.base.addListener(this);
			
			if (!this.base.isEmpty())
			{
				Iterator iterator = this.listeners.iterator();
				ListDataEvent outgoing = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, 0, this.base.size()-1);
				while (iterator.hasNext())
				{
					((ListDataListener)iterator.next()).intervalAdded(outgoing);
				}
			}
		}
		
		public Object getElementAt(int index)
		{
			return this.base.elementAt(index);
		}
		
		public int getSize()
		{
			return this.base.size();
		}
		
		public void addListDataListener(ListDataListener listener)
		{
			this.listeners.add(listener);
		}
		
		public void removeListDataListener(ListDataListener listener)
		{
			this.listeners.remove(listener);
		}
		
		public void report(List.Event incoming)
		{
			ListDataEvent outgoing;
			Iterator iterator;
			switch (incoming.action())
			{
				case List.Event.ADD:
					outgoing = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, incoming.index(), incoming.index());
					iterator = this.listeners.iterator();
					while (iterator.hasNext())
					{
						((ListDataListener)iterator.next()).intervalAdded(outgoing);
					}
					break;
				case List.Event.REMOVE:
					outgoing = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, incoming.index(), incoming.index());
					iterator = this.listeners.iterator();
					while (iterator.hasNext())
					{
						((ListDataListener)iterator.next()).intervalRemoved(outgoing);
					}
					break;
			}
		}

		/**
		 * Obtain an {@link Action} implemented to call {@link #moveUp()} 
		 * with the current selection.
		 */		
		public Action upAction(JList list)
		{
			return (new UpAction(list));
		}
		
		/**
		 * Obtain an {@link Action} implemented to call {@link #moveDown()} 
		 * with the current selection.
		 */		
		public Action downAction(JList list)
		{
			return (new DownAction(list));
		}
		
		/**
		 * Obtain an {@link Action} implemented to send the current
		 * selection to the Percolator.
		 */		
		public Action brewAction(JList list)
		{
			return (new BrewAction(list));
		}

		abstract class ListAction extends AbstractAction implements ListSelectionListener
		{
			protected JList list;
			
			public ListAction(JList list)
			{
				this.list = list;
				this.list.addListSelectionListener(this);
				super.setEnabled(false);
			}
			
			public void actionPerformed(ActionEvent event)
			{
				int[] selection = this.list.getSelectedIndices();
				this.act(selection);
				this.list.setSelectedIndices(selection);
			}
			
			public void valueChanged(ListSelectionEvent event)
			{
				super.setEnabled(this.list.getSelectedIndex() >= 0);
			}
			
			protected void registerKeyStroke(int keyCode, boolean whenPressed, int modifiers, String name)
			{
				this.list.getInputMap(this.list.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(keyCode, modifiers, whenPressed), name);
				this.list.getActionMap().put(name, this);
			}

			protected abstract void act(int[] upon);
		}
		
		class UpAction extends ListAction
		{
			public UpAction(JList list)
			{
				super(list);
				super.putValue(NAME, "^");
				super.putValue(ACCELERATOR_KEY, new Integer(KeyEvent.VK_UP));
				super.putValue(SHORT_DESCRIPTION, "Move selection up");
				super.registerKeyStroke(KeyEvent.VK_UP, true, InputEvent.CTRL_MASK, "up_arrow");
			}

			protected void act(int[] upon)
			{
				ListModel.this.moveUp(upon);
			}
		}
		
		class DownAction extends ListAction
		{
			public DownAction(JList list)
			{
				super(list);
				super.putValue(NAME, "v");
				super.putValue(ACCELERATOR_KEY, new Integer(KeyEvent.VK_DOWN));
				super.putValue(SHORT_DESCRIPTION, "Move selection down");
				super.registerKeyStroke(KeyEvent.VK_DOWN, true, InputEvent.CTRL_MASK, "down_arrow");
			}

			protected void act(int[] upon)
			{
				// could retain selection
				ListModel.this.moveDown(upon);
			}
		}
		
		class BrewAction extends ListAction 
		{
			public BrewAction(JList list)
			{
				super(list);
				super.putValue(NAME, "Remove");
				super.putValue(ACCELERATOR_KEY, new Integer(KeyEvent.VK_DELETE));
				super.putValue(SHORT_DESCRIPTION, "Remove the selection to the Percolator");
				super.setEnabled(false);
				super.registerKeyStroke(KeyEvent.VK_DELETE, false, 0, "delete");
			}
			
			protected void act(int[] upon)
			{
				DecafEditor.percolator.perk(ListModel.this.remove(upon));
			}
		}
	}

	/**
	 * Implemented by visible components.
	 */
	public static interface Renderable
	{
		public JComponent render();
	}
	
	/**
	 * Adds some convenience to JPanel, and sets all contained components 
	 * to be transparent, such that the background color of the RenderPanel
	 * always shows through.
	 */
	public static class RenderPanel extends JPanel
	{
		public RenderPanel()
		{
			super();
			super.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			super.setBackground(Color.white);
			super.setOpaque(true);
		}
		
		/**
		 * Add <code>component</code>, setting it transparent.
		 */
		public void add(JComponent component)
		{
			component.setOpaque(false);
			super.add(component);
		}
		
		/**
		 * Recursively set the foreground color of each contained component.
		 */
		public void setForeground(Color color)
		{
			Component[] contents = super.getComponents();
			for (int i = 0; i < contents.length; i++)
			{
				contents[i].setForeground(color);
			}
		}

		/**
		 * This class is NOT serializable!  Throws a {@link Utils.SerialException}.
		 */	
		public Object writeReplace()
			throws java.io.ObjectStreamException
		{
			throw (new Utils.SerialException(getClass()));
		}
	}

	/**
	 * A JLabel customized for use in a RenderPanel; defaults to 
	 * opaque with white background.
	 */	
	public static class RenderLabel extends JLabel
	{
		public RenderLabel()
		{
			super();
			init();
		}
		
		public RenderLabel(String text)
		{
			super(text);
			init();
		}
		
		private void init()
		{
			super.setOpaque(true);
			super.setBackground(Color.white);
			//super.setFont(font);
		}
	
		/**
		 * This class is NOT serializable!  Throws a {@link Utils.SerialException}.
		 */	
		public Object writeReplace()
			throws java.io.ObjectStreamException
		{
			throw (new Utils.SerialException(getClass()));
		}
	}

	/**
	 * A {@link Vector} that attempts to weed out non-serializable components
	 * before going to disk.
	 */	
	public static class SerialVector extends Vector //implements Copy
	{
		public Object writeReplace()
			throws java.io.ObjectStreamException
		{
			Object next;
			for (int i = 0; i < super.size();)
			{
				next = super.elementAt(i);
				if ((next instanceof Component) || !(next instanceof Decaf))
				{
					super.remove(i);
				}
				else
				{
					i++;
				}
			}
			return this;
		}
/*		
		public Copy shallowCopy()
		{
			try
			{
				return (SerialVector)super.clone();
			}
			catch (Exception e)
			{
				return null;
			}
		}
		
		public Copy deepCopy()
		{
			System.err.println("Warning, call to " + getClass().getName() + ".deepCopy(), which is not implemented.  Forwarding to shallowCopy()");
			return shallowCopy();
		}
*/
	}

	/**
	 * A diagonally incrementable {@link Dimension}.
	 */	
	public static class Location extends Point
	{
		public Location(int x, int y)
		{
			super(x, y);
		}
		
		public Location(Dimension d)
		{
			super(d.width, d.height);
		}
		
		public Location(Point p)
		{
			super(p.x, p.y);
		}
		
		public void add(Location increment)
		{
			super.x += increment.x;
			super.y += increment.y;
		}
		
		public Location copy()
		{
			return (new Location(super.x, super.y));
		}
		
		public boolean exceeds(Location other)
		{
			return ((super.x > other.x) || (super.y > other.y));
		}
	}

	/**
	 * Facilitates the placement of windows on the desktop.
	 */
	public static abstract class RealEstateManager 
	{
		protected static Utils.Location offset = new Utils.Location(20, 20);
		protected static Utils.Location screenExtent = new Utils.Location(Toolkit.getDefaultToolkit().getScreenSize());

		public static final int UPPER_LEFT = 0;
		public static final int UPPER_RIGHT = 1;
		public static final int LOWER_LEFT = 2;
		public static final int LOWER_RIGHT = 3;
		public static final int CENTER = 4;
		
		public abstract void place(Window window);
		
		/**
		 * Place <code>window</code> at </code>spot</code>, one of {@link UPPER_LEFT},
		 * {@link UPPER_RIGHT}, {@link LOWER_LEFT}, {@link LOWER_RIGHT}, {@link CENTER}.
		 */
		public void place(int spot, Window window)
		{
			Utils.Location size = new Utils.Location(window.getSize());
			
			switch (spot)
			{
				case UPPER_LEFT:
					window.setLocation(0, 0);
					break;
				case UPPER_RIGHT:
					window.setLocation(screenExtent.x - size.x, 0);
					break;
				case LOWER_LEFT:
					window.setLocation(0, screenExtent.y - size.y);
					break;
				case LOWER_RIGHT:
					window.setLocation(screenExtent.x - size.x, screenExtent.y - size.y);
					break;
				case CENTER:
					Utils.Location center = new Utils.Location(screenExtent.x / 2, screenExtent.y / 2);
					window.setLocation(center.x - (size.x / 2), center.y = (size.y / 2));
					break;
			}
		}
	}
	
	/**
	 * Reports an attempt to serialize an instance of a class that has been
	 * determined to be non-serializable (by policy or by definition)
	 */
	public static class SerialException extends java.io.ObjectStreamException
	{
		public SerialException(Class offender)
		{
			super(offender.getName());
		}
	}
}