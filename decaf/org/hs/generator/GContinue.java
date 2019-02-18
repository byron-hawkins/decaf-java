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
 * A continue statement in the context of a conditional loop.  A GContinue
 * may be labelled, thus causing termination of the same-labelled conditional
 * loop (same via java.lang.String.equals()) upon execution.  A null label
 * implies no label.
 */
public class GContinue extends GConstruct implements GStatement
{
	static final long serialVersionUID = -5343982269220092980L;

	protected String label;
	
	/**
	 * Construct a GContinue with no label.
	 */
	public GContinue()
	{
		this((GLoop)null);
	}
	
	/**
	 * Construct a GContinue with the label <code>target</code>, to refer to
	 * the same-labelled loop. 
	 */
	public GContinue(GLoop target)
	{
		super("???");
		
		if (target == null)
		{
			this.label = null;
		}
		else
		{
			this.label = target.label;
		}
	}
	
	GContinue(JStatement statement)
	{
		super(statement);
		statement.accept(new Decoder());
	}
	
	public JStatement encodeStatement()
	{
		return (new JContinueStatement(super.tokenReference,
									   this.label,
									   super.encodeComments()));
	}
	
	class Decoder extends GConstructDecoder
	{
		/**
		 * visits a continue statement
		 */
		public void visitContinueStatement(JContinueStatement self,
										   String label)
		{
			GContinue.this.label = label;
		}
	}

	public String toString()
	{
		return "label(" + this.label + ")";
	}
}
