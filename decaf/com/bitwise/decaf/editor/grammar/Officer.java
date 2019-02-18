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
import javax.swing.*;
import javax.swing.tree.*;

import org.hs.generator.*;
import org.hs.jfc.*;

import com.bitwise.decaf.editor.*;

/**
 * Derivations of the <code>Officer</code> class patrol instances
 * of {@link Grammar}, which uses the method {@link call()} to set this 
 * officer to task.
 */
public abstract class Officer implements Decaf
{
	static final long serialVersionUID = 2863849519455078336L;

	/**
	 * Call this <code>Officer</code> to perform its duty.
	 */
	public abstract void call();
	
	/**
	 * This empty interface denotes an officer that patrols one {@link Grammar}
	 * at the behest of another; typically such officers are delegates, 
	 * such as the {@link Deputy} class. 
	 */
	public static interface Foreign {}
	
	/**
	 * A <code>Deputy</code> is an officer that reports all calls to 
	 * its sherriff, which typically is the Officer that created it.
	 */
	public static class Deputy extends Officer implements Foreign
	{
		protected Officer sheriff;
		
		/**
		 * Create a <code>Deputy</code> to report to <code>sherriff</code>.
		 */
		public Deputy(Officer sheriff)
		{
			this.sheriff = sheriff;
		}
		
		/**
		 * Forwards the call to <code>this.sherriff</code>.
		 */
		public void call() 
		{
			this.sheriff.call();
		}
	}
}

