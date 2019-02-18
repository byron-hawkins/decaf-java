package org.hs.jfc;

import java.util.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.border.*;

import org.hs.util.*;

/**
 * Welcome to HotComponent, a new alternative to drag-and-drop that is 
 * substantially less processor intensive, allows free use of the
 * mouse during the object association process, and offers a much
 * more concise and reliable API.
 * <p>
 * Here's how it works: to move, copy or link data from one component
 * to another, the user will "hot pick" the source component and 
 * "hot put" at the destination component.  A "hot pick" is indicated
 * by a left click with the current "pick" modifiers (default is ALT).
 * A "hot put" is indicated by a right-click with the current "put" 
 * modifiers (default is also ALT).  When the source component is 
 * picked, you are expected to apply the current hot color to the selected
 * portion of your component.  The HotStation will paint the mouse cursor
 * a "warm" color, and as the user mouses over potential drop locations, 
 * the station will turn the cursor "hot" and also highlight the drop 
 * site with a hot border.  You will have full control over all of these
 * operations as they apply to your components.  See the HotStation for 
 * more information on setting colors, cursors and keyboard modifiers.
 * <p>
 * Each component wishing to participate in this exchange of objects 
 * must implement this HotComponent interface and register itself with the local 
 * HotStation.  Upon registration, the station will call setHotColor(Color)
 * with the currently effective hot color, and will call register(Arbiter)
 * to negotiate how this HotComponent will integrate into the system.
 * <p>
 * When the user hot-clicks your component, the station will call pick(boolean)
 * with a flag indicating whether the component is being selected or de-selected.
 * You have the option to deny the pick by returning false.  When the user
 * hot-drops the contents of this component somewhere, the station will call
 * give(Class, int) on your component with some details about what kind of data 
 * is being asked for at the drop site.  
 * <p>
 * When the user has hot-selected another component and does a mouse-over your 
 * component, the hot station will (after checking the arbiter's general compatibility
 * listing) call isCompatible(Object) with the potentially droppable hot data.
 * Return true if you can accept this data, or false if not.  If you return true, 
 * the hot station will apply a hot border to your component via setBorder(Border), 
 * and give the user the option to drop the data.  Should that drop occur, the station 
 * will call take(Object) on your component with the hot data.  Be sure to return 
 * your "cool" border in the getBorder() method, so that the hot station can replace
 * it when the hot border no longer applies.
 */
public interface HotComponent
{
	// Registration and setup
	
	/**
	 * Called by the HotStation upon registration, to notify you 
	 * of the current hot color.
	 */
	public void setHotColor(Color hot);
	
	/**
	 * Used by the HotStation to listen as the user mouses
	 * around your component.
	 */
	public void addMouseListener(MouseListener l);
	
	/**
	 * Called by the HotStation upon registration to establish the kinds
	 * of data your component is available to give and take.  See the 
	 * Arbiter for more information.
	 */
	public void register(Arbiter arbiter);	


	// Data exchange
	
	/**
	 * Called by the HotStation when the user hot-selects your component, with
	 * a flag specifying whether this is a select or de-select.  In the case of
	 * a select, you may deny the selection by returning false.  The return value
	 * in the case of a de-select is ignored.  Upon agreed select, you are expected
	 * to highlight your component with the current hot color (passed to you at registration 
	 * time via <code>setHotColor(Color)</code>), so that the user can see what has 
	 * been selected.  If your component gives a wide variety of hot data, you might
	 * also make a visual note of exactly what kind of data is being currently offered
	 * (it may change as the HotStation calls <code>give(Class, int)</code> with the 
	 * operation SAMPLE).
	 *
	 * @param b specifies select (true) or de-select (false) 
	 */
	public boolean pick(boolean b);
	
	/**
	 * Called by the HotStation to get an instance of your hot data.  The 
	 * <code>operation</code> specifies the context in which the request is being made
	 * of you; it will be one of SAMPLE, MOVE, COPY and LINK.  See those constants for
	 * more information.  The <code>type</code> specifies the kind of data the arbiter
	 * deems most appropriate for the current context.  
	 *
	 * @param type the type of data the HotStation is currently interested to obtain from you.
	 * This will be one of the types you registered with the Arbiter in your register(Arbiter) method.
	 * @param operation one of SAMPLE, MOVE, COPY and LINK; see those constants for more information.
	 * @return the data requested by the HotStation
	 */
	public Object give(Class type, int operation);	
	
	/**
	 * Called by the HotStation to find out whether the object <code>hot</code> can be 
	 * dropped on your component.  Return your opinion.
	 * 
	 * @param hot the potential delivery
	 * @return your choice, do you want the object <code>hot</code> or not
	 */
	public boolean isCompatible(Object hot);
	
	/**
	 * Called by the HotStation when the user has chosen to hot drop the object <code>hot</code> 
	 * onto your component.  If a COPY operation has been specified, the giving component will
	 * have already performed the copy.  You may simply take what's here and apply it in the 
	 * manner of your component.
	 *
	 * @param hot the delivery
	 */
	public void take(Object hot);
	
	/**
	 * Called by the HotStation when applying a hot border to your component.  It will keep a 
	 * reference to the border you return, and put it back when the hot border no longer applies.
	 *
	 * @return your component's ordinary border.
	 */
	public Border getBorder();
	
	/**
	 * Called by the HotStation to change the border of your component, either to a hot border,
	 * or to your original border that you returned in a prior call to <code>getBorder</code>.
	 */
	public void setBorder(Border border);

	/**
	 * One of the <code>give()</code> operations, the HotStation uses this value when it is 
	 * testing compatibility with the <code>isCompatible()</code> method on the potential 
	 * recipient.  
	 */	
	public static final int SAMPLE = 0;
	
	/**
	 * One of the <code>give()</code> operations, the HotStation uses this value when it is 
	 * requesting to move the selected data from your component to the destination component.
	 * You should remove the hot-selected data from your component at such time.
	 */
	public static final int MOVE = 1;
	
	/**
	 * One of the <code>give()</code> operations, the HotStation uses this value when it is 
	 * requesting to copy data from your component to the destination component.  You should
	 * make a copy of your hot-selected data at such time.
	 */
	public static final int COPY = 2;
	
	/**
	 * One of the <code>give()</code> operations, the HotStation uses this value when it is 
	 * requesting to link data from your component with the destination component.  You 
	 * should return the instance of your hot-selected data to the station, or provide for
	 * some explicit linkage external to that instance and return its handle to the station.
	 */
	public static final int LINK = 4;
	
	/**
	 * A convenience constant for specifying to the Arbiter that your component will give 
	 * or take the specified class under any circumstances.
	 */
	public static final int ANY = 7;
	
	// used by the HotStation as it maintains delivery context
	static final int NONE = 0x8000;

	static class Constraints
	{
		public static String translate(int delivery)
		{
			switch (delivery)
			{
				case SAMPLE: return "sample";
				case MOVE: return "move";
				case COPY: return "copy";
				case LINK: return "link";
				case ANY: return "any";
				case NONE: return "none";
			}
			return "unknown (" + delivery + ")";
		}

		protected Class type;	// null for collection
		protected int defaultOperation;
		protected int allowedOperations;
		
		// this constructor implies a collection
		public Constraints(int allowedOperations)
		{
			this(null, NONE, allowedOperations);
		}
		
		// this constructor implies a collection
		public Constraints(int defaultOperation, int allowedOperations)
		{
			this(null, defaultOperation, allowedOperations);
		}
		
		public Constraints(Class type, int allowedOperations)
		{
			this(type, NONE, allowedOperations);
		}
		
		public Constraints(Class type, int defaultOperation, int allowedOperations)
		{
			this.type = type;
			this.defaultOperation = defaultOperation;
			this.allowedOperations = allowedOperations;
		}
		
		public boolean isCollection()
		{
			return (this.type == null);
		}
		
		public String getClassname()
		{
			if (this.type == null)
			{
				return "";
			}
			
			return this.type.getName();
		}
		
		public Class getType()
		{
			return this.type;
		}
		
		public int getDefaultOperation()
		{
			return this.defaultOperation;
		}
		
		public boolean allows(int operation)
		{
			return ((this.allowedOperations & operation) > 0);
		}
		
		public int operationIntersection(Constraints other)
		{
			int intersection = this.allowedOperations & other.allowedOperations;
			if (intersection == 0)
			{
				return NONE;
			}
			
			// arbitrary... user will have to specify with the keyboard if they don't like it
			if ((intersection & MOVE) > 0)
			{
				return MOVE;
			}
			if ((intersection & COPY) > 0)
			{
				return COPY;
			}
			if ((intersection & LINK) > 0)
			{
				return LINK;
			}
			
			System.err.println("Help!  How did we get here?");
			Thread.dumpStack();
			return NONE;
		}
	}
	
	// should really put offers and receives in a map, so duplicates get booted
	// -- but remember they are in preference order!
	
	/**
	 * The Arbiter pays a visit to your component upon registration with the HotStation, 
	 * in the callback method register(Arbiter).  It collects information from you about
	 * the objects you are available to give and/or take as a HotComponent.  For each 
	 * type you are offering to other components, call one of the offer() methods, supplying
	 * along with the Object class the operations by which you will offer it.  The operations are
	 * constants of HotComponent: any combination of MOVE, COPY, LINK (ALL is a convenience
	 * constant of value MOVE|COPY|LINK, and SAMPLE must always be provided for the 
	 * delivery negotiation).  For each type you can receive from other components, call
	 * one of the receive() methods in the same manner.  In both cases, the HotStation will
	 * choose a method of delivery based on:
	 * <p>
	 * 1) The offer and receive operations available<br>
	 * 2) The user's request, expressed with the keyboard modifiers SHIFT/CTRL/ALT
	 */
	public static class Arbiter
	{
		protected Vector offers;
		protected Vector receives;
		
		Arbiter()
		{
			this.offers = new Vector();
			this.receives = new Vector();
		}
		
		/**
		 * Inform the Arbiter that you can give objects of <code>type</code> with 
		 * the operation specified in <code>defaultOperation</code>.  (Operations are 
		 * a combination of the HotComponent constants MOVE|COPY|LINK).  
		 */
		public void offer(Class type, int defaultOperation)
		{
			offer(type, defaultOperation, defaultOperation);
		}
		
		/**
		 * Inform the Arbiter that you can give objects of <code>type</code> with 
		 * the operations specified in <code>allowedOperations</code>, and that you prefer  
		 * the operation <code>defaultOperation</code>.  (Operations are a combination
		 * of the HotComponent constants MOVE|COPY|LINK).  
		 */
		public void offer(Class type, int defaultOperation, int allowedOperations)
		{
			this.offers.add(new Constraints(type, defaultOperation, allowedOperations));
		}
		
		/**
		 * Inform the Arbiter that you can give a collection of objects with 
		 * the operations specified in <code>allowedOperations</code>.  Collections must
		 * be delivered as instances of HotComponent.HotCollection, a typed collection
		 * wrapper.  (Operations are a combination of the HotComponent constants MOVE|COPY|LINK).  
		 */
		public void offerCollection(int allowedOperations)
		{
			this.offers.add(new Constraints(allowedOperations));
		}
		
		/**
		 * Inform the Arbiter that you can receive objects of <code>type</code> with 
		 * any operations.
		 */
		public void receive(Class type)
		{
			receive(type, ANY);
		}
		
		/**
		 * Inform the Arbiter that you can receive objects of <code>type</code> with 
		 * the operations specified in <code>allowedOperations</code>.  (Operations are 
		 * a combination of the HotComponent constants MOVE|COPY|LINK).  
		 */
		public void receive(Class type, int allowedOperations)
		{
			this.receives.add(new Constraints(type, allowedOperations));
		}
		
		/**
		 * Inform the Arbiter that you can receive collections of objects with 
		 * the operations specified in <code>allowedOperations</code>.  Collections
		 * will be delivered as instances of HotComponent.HotCollection, a typed collection
		 * wrapper, and you will of course be able to examine their contents in your 
		 * <code>isCompatible</code> method before accepting.  (Operations are 
		 * a combination of the HotComponent constants MOVE|COPY|LINK).  
		 */
		public void receiveCollection(int allowedOperations)
		{
			this.receives.add(new Constraints(allowedOperations));
		}
		
		Vector offers()
		{
			return this.offers;
		}
		
		Vector receives()
		{
			return this.receives;
		}
		
		Constraints offers(String classname)
		{
			return match(classname, this.offers);
		}
		
		Constraints receives(String classname)
		{
			return match(classname, this.receives);
		}
		
		private Constraints match(String classname, Vector constraints)
		{
			Constraints offering;
			Iterator offerings = constraints.iterator();
			while (offerings.hasNext())
			{
				offering = (Constraints)offerings.next();
				if (offering.getClassname().equals(classname))
				{
					return offering;
				}
			}
			return null;
		}
	}

	/**
	 *	The HotCollection is actually a collection wrapper that provides a catalog
	 * of the types contained in the wrapped collection.  It is used in the delivery
	 * of collections between HotComponent`s so that the receiving component can
	 * effeciently decide in its <code>isCompatible()</code> method whether the collection
	 * applies to its data scope.  This class does nothing more than hand back to the 
	 * caller the data you have given it.
	 */	
	public static class HotCollection 
	{
		protected Collection collection;
		protected TreeMap types;
		
		/**
		 * Instantiate a HotCollection with the data in <code>collection</code>, each element
		 * of which is of a type found in <code>types</code>.  Further types may be appended
		 * with <code>addType()</code>.
		 *
		 * @param types the catalog of types contained within <code>collection</code>
		 * @param collection the HotCollection data
		 */
		public HotCollection(Class[] types, Collection collection)
		{
			init();
			
			for (int i = 0; i < types.length; i++)
			{
				this.types.put(types[i].getName(), types[i]);
			}
			this.collection = collection;
		}
		
		/**
		 * Instantiate a HotCollection with the data in <code>collection</code>, each element
		 * of which is of <code>type</code>.  Further types may be appended
		 * with <code>addType()</code>.
		 *
		 * @param type the type of object found in <code>collection</code>.
		 * @param collection the HotCollection data
		 */
		public HotCollection(Class type, Collection collection)
		{
			init();
			
			this.types.put(type.getName(), type);
			this.collection = collection;
		}
		
		/**
		 * Instantiate a HotCollection without type information.  Types may be added
		 * with <code>addType()</code>.
		 *
		 * @param collection the HotCollection data
		 */
		public HotCollection(Collection collection)
		{
			init();
			
			Object next;
			Iterator iterator = collection.iterator();
			while (iterator.hasNext())
			{
				next = iterator.next();
				addType(next.getClass());
			}
			this.collection = collection;
		}
		
		/**
		 * Get the HotCollection data.
		 *
		 * @return the contents of this HotCollection, unmodified or otherwise
		 * meddled with upon instantiation of this object.
		 */
		public Collection collection()
		{
			return this.collection;
		}
		
		private void init()
		{
			this.types = new TreeMap();
		}
		
		/**
		 * Get an iterator of the HotCollection data.
		 *
		 * @return an iterator over the contents of this HotCollection, which remain 
		 * unmodified or otherwise meddled with upon instantiation of this object.
		 */
		public Iterator iterator()
		{
			return this.collection.iterator();
		}
		
		/**
		 * Add a type to the HotCollection.
		 *
		 * @param type the type to add to the collection.
		 */
		public void addType(Class type)
		{
			this.types.put(type.getName(), type);
		}
		
		/**
		 * If you know that this HotCollection is homogenous,
		 * use this method to get its single type.  
		 *
		 * @return the single type with which all contents of this HotCollection 
		 * are compatible.
		 */
		public Class getType()
		{
			return (Class)this.types.values().toArray()[0];
		}
		
		/**
		 * Find out whether this HotCollection is homogenous.
		 *
		 * @return true if this HotCollection has only one type in its catalog.
		 */
		public boolean isHomogenous()
		{
			return (this.types.size() == 1);
		}
		
		/**
		 * Find out if this HotCollection lists <code>type</code> in its catalog.  
		 *
		 * @param type the type in question
		 * @return true if this HotCollection contains <code>type</code> in its catalog.
		 */
		public boolean hasType(Class type)
		{
			return (this.types.get(type.getName()) != null);
		}
	}
}