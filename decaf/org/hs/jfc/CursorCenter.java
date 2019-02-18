package org.hs.jfc;

import java.io.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import javax.swing.*;

/**
 * The <code>CursorCenter</code> creates a cache of colorizable 
 * java.awt.Cursor templates.  Cursors are cached by Descriptor,
 * and may be retrieved with or without a special background color.
 *
 * @see Descriptor
 */
public class CursorCenter extends TreeMap
{
	protected static Toolkit toolkit;
	protected static Component component;	
	// java.awt falsely presumes that this code is inside a Component
	
	protected Images images;
	
	static
	{
		toolkit = Toolkit.getDefaultToolkit();
		component = new JPanel();
	}

	/**
	 * Create an empty <code>CursorCenter</code>
	 */	
	public CursorCenter()
	{
		super();
		
		this.images = new Images();
	}
	
	/**
	 * Create, cache and return the cursor specified by <code>descriptor</code>, 
	 * or retrieve it from the cache if it has been previously requested by a 
	 * <code>get</code> method.  
	 */
	public Cursor getCursor(Descriptor descriptor)
	{
		return this.getCursor(descriptor, null);
	}
	
	/**
	 * Create and cache the cursor specified by <code>descriptor</code>, or obtain
	 * it from the cache if it has already been created; and substitute all pixels
	 * of the <code>chameleonColor</code> provided by <code>descriptor</code> with
	 * <code>color</code>; then return the colorized cursor.  Does not cache the 
	 * colorization (which is a trivial process).
	 */
	public Cursor getCursor(Descriptor descriptor, Color color)
	{
		CursorKey key = new CursorKey(descriptor, color);
		Cursor cursor = (Cursor)super.get(key);
		if (cursor == null)
		{
			cursor = createCursor(descriptor, color);
			super.put(key, cursor);
		}
		return cursor;
	}

	protected Cursor createCursor(Descriptor descriptor, Color color)
	{
		CursorColorizer colorizer = new CursorColorizer(descriptor.blankColor, descriptor.chameleonColor, color);
		FilteredImageSource cursorProducer = new FilteredImageSource(this.images.getImage(descriptor).getSource(), colorizer);

		Image cursorImage = component.createImage(cursorProducer);
		return toolkit.createCustomCursor(cursorImage, new Point(0,0), descriptor.imageFile.getAbsolutePath() + "_" + color.getRGB());
	}
	
	protected static class Images extends TreeMap
	{
		public Image getImage(Descriptor descriptor)
		{
			Image image = (Image)super.get(descriptor);
			if (image == null)
			{
				image = toolkit.getImage(descriptor.imageFile.getAbsolutePath());
		
				MediaTracker tracker = new MediaTracker(component);
				try 
				{
					tracker.addImage(image, 0);
					tracker.waitForID(0);
				}
				catch(InterruptedException e) 
				{ 
				}
				
				super.put(descriptor, image);
			}
			return image;
		}
	}

	/**
	 * Describes a colorizable java.awt.Cursor with its image file (which must be in
	 * <u>standard<u> .gif format: funny compressions don't work), the color
	 * of the transparent regions, and the color of the colorizable regions.  
	 * Since color is the only means of specially distinguishing any pixel in 
	 * a raw .gif image (which must be the basis of any cursor in the AWT), I've
	 * decided that reducing the color spectrum for these cursors by two shades is worth 
	 * the simplicity of encoding these two special regions directly into the 
	 * image file.  The alternative is to require maps for the transparent and 
	 * colorizable regions in the file, which I may implement later but which sounds
	 * like a total pain to code on the client side.   
	 */	
	public static class Descriptor implements Comparable
	{
		protected File imageFile;
		protected Color blankColor;
		protected Color chameleonColor;
		
		/**
		 * Convenience constructor: <code>this(new File(imageFilename), blankColor, chameleonColor);</code>
		 */
		public Descriptor(String imageFilename, Color blankColor, Color chameleonColor)
		{
			this(new File(imageFilename), blankColor, chameleonColor);
		}
		
		/**
		 * Describe a cursor whose image file resides at <code>imageFile</code>,
		 * and for which the <code>blankColor</code> should be made transparent, and the 
		 * <code>chameleonColor</code> should be colorized (when that function is requested).
		 */
		public Descriptor(File imageFile, Color blankColor, Color chameleonColor)
		{
			this.imageFile = imageFile;
			this.blankColor = blankColor;
			this.chameleonColor = chameleonColor;
		}

		/**
		 * This comparison mechanism for the internal cache considers two 
		 * <code>Descriptor</code>s to be equal when they use the same image
		 * file (by filesystem location), and have exactly the same 
		 * <code>blankColor</code> and <code>chameleonColor</code>.
		 */		
		public int compareTo(Object o)
		{
			Descriptor other = (Descriptor)o;
			
			int result = this.imageFile.compareTo(other.imageFile);
			if (result == 0)
			{
				result = this.blankColor.getRGB() - other.blankColor.getRGB();
				if (result == 0)
				{
					result = this.chameleonColor.getRGB() - other.chameleonColor.getRGB();
				}
			}
			
			return result;
		}
		
		public boolean equals(Object o)
		{
			return (compareTo(o) == 0);
		}
	}
	
	static class CursorKey implements Comparable
	{
		protected Descriptor descriptor;
		protected Color color;
		
		public CursorKey(Descriptor descriptor, Color color)
		{
			this.descriptor = descriptor;
			this.color = color;
		}
		
		public int compareTo(Object o)
		{
			CursorKey other = (CursorKey)o;
			
			int result = this.descriptor.compareTo(other.descriptor);
			if (result == 0)
			{
				if (this.color == null)
				{
					return (other.color == null)?0:1;
				}
				else
				{
					result = this.color.getRGB() - other.color.getRGB();
				}
			}
			return result;
		}

		public boolean equals(Object o)
		{
			return (compareTo(o) == 0);
		}
	}
	
	class CursorColorizer extends RGBImageFilter
	{
		protected Color blank;
		protected Color from;
		protected Color to;
		
		public CursorColorizer(Color blank, Color from, Color to)
		{
			super();
			
			this.blank = blank;
			this.from = from;
			this.to = to;
		}
		
		public int filterRGB(int x, int y, int rgb)
		{
			if ((this.from != null) && ((rgb & 0xFFFFFF) == (this.from.getRGB() & 0xFFFFFF)))
			{
				return (0xFF000000 | this.to.getRGB());
			}
			else if ((rgb & 0xFFFFFF) == (this.blank.getRGB() & 0xFFFFFF))
			{
				return 0xc0c0c0;
			}
			else
			{
				return rgb;
			}
		}
	}
}