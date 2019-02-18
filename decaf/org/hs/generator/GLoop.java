/*
 * Copyright (C) 2003 HawkinsSoftware
 *
 * This Java code generator package is free 
 * software.  You can redistribute it and/or modify it under the terms 
 * of the GNU General Public License as published by the Free Software 
 * Foundation.  However, no compilation of this code or a derivative
 * of it may be used with or integrated into any commercial application,
 * except by the written permisson of HawkinsSoftware.  Future versions 
 * of this product will be sold commercially under a different license.  
 * HawkinsSoftware retains all rights to this product, including its
 * concepts, design and implementation.
 *
 * This package is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
 
package org.hs.generator;

import at.dms.kjc.*;

import java.util.*;

/**
 * An abstract base class for conditional loops.
 * 
 * @see GFor
 * @see GWhile
 */
public abstract class GLoop extends GConstruct implements GStatement
{
	static final long serialVersionUID = 4423803251661280777L;

	/**
	 * The label of the subclass' loop; null implies no label.
	 */
	public String label;
	
	public GLoop(String name)
	{
		super(name);
		
		this.label = null;
	}
	
	GLoop(JStatement statement)
	{
		super(statement);
	}
	
	protected JStatement encodeStatement(JStatement main)
	{
		if (this.label == null)
		{
			return main;
		}
		
		return (new JLabeledStatement(super.tokenReference,
									  this.label,
									  main,
									  super.encodeComments()));
	}
	
	public String toString()
	{
		return "label(" + this.label + ")";
	}
}

