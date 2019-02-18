package org.hs.jfc;

import java.io.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import org.hs.util.*;
import org.hs.util.file.*;

// It would really be best if we listen to keyboard events, so we can 
// get the ctrl, shift, alt as they happen (not just on mouse in/out).

/**
 * The HotStation facilitates the various processes of HotComponent data exchange.
 * It is designed with the expectation that a single instance of a HotStation will
 * serve an application.  Configuration fo the HotStation is done through the 
 * <code>properties</code> parameter to its constructor, and with the methods
 * <code>setPickModifiers(int modifiers), setPutModifiers(int modifiers) and
 * setHoldModifiers(int modifiers)</code>.  Register your components and containers
 * with the <code>register()</code> methods.  Note that the HotStation can only apply
 * its current cursor when the mouse is over your container if that container is
 * registered with the station; even if it contains no HotComponent`s.
 * <p>
 * It may be possible to share a station across multiple
 * JVMs, or to create interaction between components registered in separate stations,
 * but I have tried neither.
 */
public class HotStation
{
	protected static final int KEYBOARD_MODIFIERS = (InputEvent.ALT_MASK | InputEvent.SHIFT_MASK | InputEvent.CTRL_MASK);

	protected static final int DEFAULT_PICK_MODIFIERS = InputEvent.ALT_MASK;
	protected static final int DEFAULT_PUT_MODIFIERS = InputEvent.ALT_MASK;
	protected static final int DEFAULT_HOLD_MODIFIERS = InputEvent.ALT_MASK | InputEvent.CTRL_MASK;	// not enough modifiers!

	protected static final int DEFAULT_MOVE_MODIFIERS = 0;
	protected static final int DEFAULT_COPY_MODIFIERS = InputEvent.SHIFT_MASK;
	protected static final int DEFAULT_LINK_MODIFIERS = InputEvent.CTRL_MASK;
	
	/**
	 * This color is used for the background of the cursor when no 
	 * component is currently hot-selected.  Change it if you must,
	 * but please try to maintain the intended contrast (e.g., puce and 
	 * mauve are not recommended choices for hot and cool colors).
	 */
	public static Color COOL_COLOR = Color.white;
		
	protected static Border emptyBorder = BorderFactory.createEmptyBorder();

	protected boolean hot;
	protected HotWatch selection;
	protected BitSet takingSet;
	protected Vector containers;
	protected Vector components;

	protected Color hotColor;
	protected Color warmColor;
	
	protected CursorCenter cursorCenter;
	
	protected CursorCenter.Descriptor plainCursor;
	protected CursorCenter.Descriptor moveCursor;
	protected CursorCenter.Descriptor copyCursor;
	protected CursorCenter.Descriptor linkCursor;

	protected Vector registeredClasses;
	
	// station standard values
	protected int pickModifiers;
	protected int putModifiers;
	protected int holdModifiers;

	protected int moveModifiers;
	protected int copyModifiers;
	protected int linkModifiers;
	
	protected Border hotBorder;
	protected transient Border previousBorder;
	protected Point lastPointPicked;
	protected Point lastPointPut;
	
	/**
	 * Properties key for the plain cursor; associate it with the java.io.File 
	 * containing your plain cursor image (must be in <u>standard</u> .gif format) in 
	 * the TreeMap of properties you pass to the station constructor.
	 */
	public static final String PLAIN_CURSOR_FILE = "plain_cursor_file";
	
	/**
	 * Properties key for the "move" cursor; associate it with the java.io.File 
	 * containing your "move" cursor image (must be in <u>standard</u> .gif format) in 
	 * the TreeMap of properties you pass to the station constructor.
	 */
	public static final String MOVE_CURSOR_FILE = "move_cursor_file";
	
	/**
	 * Properties key for the "copy" cursor; associate it with the java.io.File 
	 * containing your "copy" cursor image (must be in <u>standard</u> .gif format) in 
	 * the TreeMap of properties you pass to the station constructor.
	 */
	public static final String COPY_CURSOR_FILE = "copy_cursor_file";
	
	/**
	 * Properties key for the "link" cursor; associate it with the java.io.File 
	 * containing your "link" cursor image (must be in <u>standard</u> .gif format) in 
	 * the TreeMap of properties you pass to the station constructor.
	 */
	public static final String LINK_CURSOR_FILE = "link_cursor_file";

	/**
	 * Properties key for the hot color; associate it with the java.awt.Color you 
	 * choose for your application's hot color in 
	 * the TreeMap of properties you pass to the station constructor.
	 * The hot color is applied to the current mouse cursor when a component 
	 * has been hot-selected, and the 
	 * mouse cursor is currently over a potential drop location.  It is also 
	 * used to create the hot border, which is applied to potential drop
	 * recipients (via <code>HotComponent.setBorder()</code>) when the hot mouse
	 * cursor is over them.
	 */
	public static final String HOT_COLOR = "hot_color";

	/**
	 * Properties key for the warm color; associate it with the java.awt.Color you 
	 * choose for your application's hot color in 
	 * the TreeMap of properties you pass to the station constructor.
	 * The warm color is applied to the current mouse cursor background when a component 
	 * has been hot-selected, but the 
	 * cursor is currently not over a potential drop location.
	 */
	public static final String WARM_COLOR = "warm_color";
	
	/**
	 * Properties key for the "blank color"; associate it with the java.awt.Color you 
	 * choose for your application's "blank color" in 
	 * the TreeMap of properties you pass to the station constructor.
	 * The "blank color" is the transparent backdrop of your cursor images.  Any pixels
	 * in those images that are set to the "blank color" will be made transparent by
	 * the HotStation in the instantiation of its cursors.  The "blank color" itself will 
	 * never appear in any HotStation cursor, so it may be visible in your raw cursor image.
	 */
	public static final String BLANK_COLOR = "blank_color";
	
	/**
	 * Properties key for the "swap color"; associate it with the java.awt.Color you 
	 * choose for your application's swap color in 
	 * the TreeMap of properties you pass to the station constructor.
	 * The swap color is the mutable background of your cursor images.  Any pixels
	 * in those images that are set to the swap color will be re-colored by
	 * the HotStation with the cool, warm and hot colors in the instantiation of 
	 * those three versions of each cursor type.  The swap color itself will never appear
	 * in any HotStation cursor, so it may be visible in your raw cursor images.
	 */
	public static final String SWAP_COLOR = "swap_color";
	
	// need to allow user-specified mouse buttons for pick/put

	/**
	 * The HotStation constructor expects the parameter <code>properties</code> to 
	 * contain the keys (public constants of this class) PLAIN_CURSOR_FILE, MOVE_CURSOR_FILE,
	 * COPY_CURSOR_FILE, LINK_CURSOR_FILE, HOT_COLOR, WARM_COLOR, BLANK_COLOR and SWAP_COLOR.
	 * See those constants for more information.  
	 */
	public HotStation(TreeMap properties)
	{
		this.containers = new Vector();
		this.components = new Vector();
		
		this.cursorCenter = new CursorCenter();
		
		Color blank = (Color)properties.get(BLANK_COLOR);
		Color swap = (Color)properties.get(SWAP_COLOR);
		this.plainCursor = new CursorCenter.Descriptor((File)properties.get(PLAIN_CURSOR_FILE), blank, swap);
		this.moveCursor = new CursorCenter.Descriptor((File)properties.get(MOVE_CURSOR_FILE), blank, swap);
		this.copyCursor = new CursorCenter.Descriptor((File)properties.get(COPY_CURSOR_FILE), blank, swap);
		this.linkCursor = new CursorCenter.Descriptor((File)properties.get(LINK_CURSOR_FILE), blank, swap);
	
		this.hotColor = (Color)properties.get(HOT_COLOR);
		this.warmColor = (Color)properties.get(WARM_COLOR);
		
		this.registeredClasses = new Vector();
		
		this.pickModifiers = DEFAULT_PICK_MODIFIERS;
		this.putModifiers = DEFAULT_PUT_MODIFIERS;
		this.holdModifiers = DEFAULT_HOLD_MODIFIERS;
		
		this.moveModifiers = DEFAULT_MOVE_MODIFIERS;
		this.copyModifiers = DEFAULT_COPY_MODIFIERS;
		this.linkModifiers = DEFAULT_LINK_MODIFIERS;
		
		this.hotBorder = BorderFactory.createLineBorder(this.hotColor, 1);
		
		this.lastPointPicked = null;
		this.lastPointPut = null;
	}

	/**
	 * Set the keyboard modifiers (i.e., SHIFT/CTRL/ALT) that will indicate a
	 * HotComponent pick to the HotStation; default is InputEvent.VK_ALT.
	 */	
	public void setPickModifiers(int modifiers)
	{
		this.pickModifiers = modifiers;
	}
	
	/**
	 * Set the keyboard modifiers (i.e., SHIFT/CTRL/ALT) that will indicate a
	 * HotComponent put to the HotStation; default is InputEvent.VK_ALT.
	 */	
	public void setPutModifiers(int modifiers)
	{
		this.putModifiers = modifiers;
	}
	
	/**
	 * Set the keyboard modifiers (i.e., SHIFT/CTRL/ALT) that will indicate to the 
	 * HotStation that the current hot selection should be held beyond the 
	 * forthcoming put operation; default is InputEvent.VK_ALT | InputEvent.CTRL_MASK.
	 */	
	public void setHoldModifiers(int modifiers)
	{
		this.holdModifiers = modifiers;
	}

	/**
	 * Register <code>container</code> with the HotStation, so that it obtains the
	 * HotStation's updates to the cursor.
	 */	
	public void register(Container container)
	{
		if (!this.containers.contains(container))
		{
			this.containers.add(container);
		}
		// need to apply the current state to this new container
	}
	
	/** 
	 * Register <code>container</code>, and all of the containers and HotComponent`s
	 * it contains (HotStation will traverse the entire tree).
	 */
	public void registerAll(Container container)
	{
		register(container);
		register(container.getComponents());
	}
	
	protected void register(Component[] components)
	{
		Component[] nested;
		for (int i = 0; i < components.length; i++)
		{
			if (components[i] instanceof HotComponent)
			{
				register((HotComponent)components[i]);
			}
			
			if (components[i] instanceof Container)
			{
				nested = ((Container)components[i]).getComponents();
				register(nested);
			}
		}
	}
	
	/**
	 * Register <code>component</code> with the HotStation.
	 */
	public void register(HotComponent component)
	{
		if (!this.components.contains(component))
		{
			this.components.add(component);
			component.setHotColor(this.hotColor);
			HotComponent.Arbiter arbiter = new HotComponent.Arbiter();
			component.register(arbiter);
			HotWatch watch = new HotWatch(component, arbiter);
		}
	}
	
	protected void unregister(Component[] components)
	{
		Component[] nested;
		for (int i = 0; i < components.length; i++)
		{
			if (components[i] instanceof HotComponent)
			{
				unregister((HotComponent)components[i]);
			}
			
			if (components[i] instanceof Container)
			{
				nested = ((Container)components[i]).getComponents();
				unregister(nested);
			}
		}
	}
	
	/**
	 * Remove <code>container</code> and all its components (containers and
	 * HotComponent`s) from the HotStation.  They will no longer respond to 
	 * station activity.
	 */
	public void unregister(Container container)
	{	
		this.containers.remove(container);
		unregister(container.getComponents());
	}
	
	/**
	 * Remove <code>component</code> from the HotStation; it will no longer 
	 * respond to station activity.
	 */
	public void unregister(HotComponent component)
	{
		this.components.remove(component);
	}

	/**
	 * Get the coordinates of the point at which the last HotStation pick
	 * operation occurred.  Useful for lists and trees.
	 */	
	public Point getLastPointPicked()
	{
		return this.lastPointPicked;
	}
	
	/**
	 * Get the coordinates of the point at which the last HotStation put
	 * operation occurred.  Useful for dropping into lists and trees.
	 */
	public Point getLastPointPut()
	{
		return this.lastPointPut;
	}
	
	// user may unregister a HotComponent by removing the 
	// MouseListener that is an instanceof HotStation.HotWatch
	
	// ... unless they don't have access to their own mouse listeners.

	protected synchronized void mouseIn(HotWatch entrance)
	{
		if ((entrance != this.selection) && 
			 entrance.canReceiveFrom(this.selection))
		{
			int delivery = entrance.chooseDelivery(this.selection);
			if (delivery != HotComponent.NONE)
			{
				this.hot = true;
				this.setCursor(delivery, this.hotColor);
				this.previousBorder = entrance.subject.getBorder();
				entrance.subject.setBorder(this.hotBorder);
			}
		}
		else
		{
			this.setCursor(HotComponent.NONE, (this.selection == null)?COOL_COLOR:this.warmColor);
		}
	}
	
	protected synchronized void mouseOut(HotWatch departure)
	{
		if (this.hot)
		{
			this.hot = false;
			this.setCursor(HotComponent.NONE, (this.selection == null)?COOL_COLOR:this.warmColor);
			departure.subject.setBorder(this.previousBorder);
		}
	}
	
	protected synchronized void mousePick(HotWatch pick)
	{
		if (this.selection == null)
		{
			if (pick.pick(true))
			{
				this.selection = pick;
				setCursor(HotComponent.NONE, this.warmColor);
				pick.subject.setBorder(this.previousBorder);
			}
		}
		else
		{
			deselect(pick);
		}
	}
	
	protected synchronized void mousePut(HotWatch destination, boolean keepSelection)
	{
		if (this.hot)
		{
			destination.takeFrom(this.selection);
			if (!keepSelection)
			{
				deselect(destination);
			}
		}
	}
	
	protected void deselect(HotWatch watch)
	{
		if (this.hot)
		{
			watch.subject.setBorder(this.previousBorder);
		}
		
		this.selection.pick(false);
		this.selection = null;
		this.setCursor(HotComponent.NONE, COOL_COLOR);
		this.hot = false;
	}
	
	protected void setCursor(int delivery, Color color)
	{
		Cursor cursor;
		switch (delivery)
		{
			case HotComponent.NONE:
				cursor = this.cursorCenter.getCursor(this.plainCursor, color);
				break;
			case HotComponent.MOVE:
				cursor = this.cursorCenter.getCursor(this.moveCursor, color);
				break;
			case HotComponent.COPY:
				cursor = this.cursorCenter.getCursor(this.copyCursor, color);
				break;
			case HotComponent.LINK:
				cursor = this.cursorCenter.getCursor(this.linkCursor, color);
				break;
			default: 
				return;
		}
		
		for (int i = 0; i < this.containers.size(); i++)
		{
			((Container)this.containers.elementAt(i)).setCursor(cursor);
		}
	}
	
	protected class HotWatch implements MouseListener
	{
		protected HotComponent subject;
		protected HotComponent.Arbiter arbiter;
		
		protected BitSet offers;
		protected BitSet receives;

		protected int pickModifiers;
		protected int putModifiers;
		protected int holdModifiers;
		
		protected MouseEvent latestEntryEvent;

		public HotWatch(HotComponent component, HotComponent.Arbiter arbiter)
		{
			this.subject = component;
			
			this.arbiter = arbiter;
			this.offers = encode(this.arbiter.offers().iterator());
			this.receives = encode(this.arbiter.receives().iterator());
			
			setPickModifiers(HotStation.this.pickModifiers);
			setPutModifiers(HotStation.this.putModifiers);
			setHoldModifiers(HotStation.this.holdModifiers);

			this.subject.addMouseListener(this);
			
			this.latestEntryEvent = null;
		}
		
		public void setPickModifiers(int modifiers)
		{
			this.pickModifiers = modifiers;
		}
		
		public void setPutModifiers(int modifiers)
		{
			this.putModifiers = modifiers;
		}
		
		public void setHoldModifiers(int modifiers)
		{
			this.holdModifiers = modifiers;
		}
		
		public boolean canReceiveFrom(HotWatch other)
		{
			if (other == null)
			{
				return false;
			}
			return this.receives.intersects(other.offers);
		}
		
		public HotComponent.Constraints preference(HotWatch other)
		{
			BitSet intersection = (BitSet)this.receives.clone();
			intersection.and(other.offers);

			TreeSet compatible = new TreeSet();
			int index;
			for (int i = intersection.nextSetBit(0); i >= 0; i = intersection.nextSetBit(i+1))
			{
				compatible.add(HotStation.this.registeredClasses.elementAt(i));
			}
			
			String next;
			Iterator preference = this.arbiter.receives().iterator();
			while (preference.hasNext())
			{
				next = ((HotComponent.Constraints)preference.next()).getClassname();
				if (compatible.contains(next))
				{
					return other.arbiter.offers(next);
				}
			}
			
			System.err.println("Help!  How did we get here?");
			Thread.dumpStack();
			return null;
		}
		
		public int latestHotOperation()
		{
			int modifiers = latestModifiers();
			if (modifiers == HotStation.this.moveModifiers)
			{
				return HotComponent.MOVE;
			}
			if (modifiers == HotStation.this.copyModifiers)
			{
				return HotComponent.COPY;
			}
			if (modifiers == HotStation.this.linkModifiers)
			{
				return HotComponent.LINK;
			}
			return 0;
		}
		
		public int latestModifiers()
		{
			return (this.latestEntryEvent.getModifiers() & KEYBOARD_MODIFIERS);
		}
		
		public int chooseDelivery(HotComponent.Constraints takingFrom)
		{
			int hotOperation = latestHotOperation();
			HotComponent.Constraints recipient = this.arbiter.receives(takingFrom.getClassname());
			if (takingFrom.allows(hotOperation) && recipient.allows(hotOperation))
			{
				return hotOperation;
			}

			int defaultOperation = takingFrom.getDefaultOperation();
			if (recipient.allows(defaultOperation))
			{
				return defaultOperation;
			}
			
			return takingFrom.operationIntersection(recipient);
		}
		
		public void takeFrom(HotWatch other)
		{
			try
			{
				HotComponent.Constraints takingFrom = preference(other);
				this.subject.take(other.give(takingFrom.getType(), chooseDelivery(takingFrom)));
			}
			catch (IllegalArgumentException e)
			{
				// what to do with it?
				// could feed a log (by interface) on init
			}
		}
		
		public int chooseDelivery(HotWatch other)
		{
			HotComponent.Constraints takingFrom = preference(other);
			int delivery = chooseDelivery(takingFrom);
			if ((delivery != HotComponent.NONE) &&
				this.subject.isCompatible(other.give(takingFrom.getType(), HotComponent.SAMPLE)))
			{
				return delivery;
			}
			return HotComponent.NONE;
		}
		
		protected Object give(Class type, int operation)
		{
			Object give = this.subject.give(type, operation);
			if (give instanceof Collection)
			{
				return (new HotComponent.HotCollection((Collection)give));
			}
			return give;
		}
		
		public boolean pick(boolean b)
		{
			return this.subject.pick(b);
		}
		
		protected BitSet encode(Iterator constraints)
		{
			int index;
			String next;
			BitSet encoding = new BitSet();
			while (constraints.hasNext())
			{
				next = ((HotComponent.Constraints)constraints.next()).getClassname().intern();
				index = HotStation.this.registeredClasses.indexOf(next);
				if (index < 0)
				{
					index = HotStation.this.registeredClasses.size();
					HotStation.this.registeredClasses.add(next);
				}
				encoding.set(index);
			}
			
			return encoding;
		}
		
		public void mousePressed(MouseEvent e)
		{
			if (SwingUtilities.isLeftMouseButton(e) && ((e.getModifiers() & KEYBOARD_MODIFIERS) == this.pickModifiers))
			{
				HotStation.this.lastPointPicked = e.getPoint();
				HotStation.this.mousePick(this);
			}
			else if (SwingUtilities.isRightMouseButton(e) && ((e.getModifiers() & KEYBOARD_MODIFIERS) == this.putModifiers))		 
			{
				HotStation.this.lastPointPut = e.getPoint();
				HotStation.this.mousePut(this, ((e.getModifiers() & KEYBOARD_MODIFIERS) == this.holdModifiers));
			}
		}
		
		public void mouseEntered(MouseEvent e)
		{
			this.latestEntryEvent = e;
			HotStation.this.mouseIn(this);
		}
		
		public void mouseExited(MouseEvent e)
		{
			HotStation.this.mouseOut(this);
		}
		
		public void mouseClicked(MouseEvent e)
		{
		}
		
		public void mouseReleased(MouseEvent e) 
		{
		}
	}
}