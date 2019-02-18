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
import at.dms.compiler.*;

import java.util.*;

/**
 * A mutable block of Java style comment (non-javadoc).  This class need to 
 * be ammended such that the comment attaches itself to the corresponding 
 * GConstruct (via its <code>comments</code> field) in visually specific ways.
 * In the present implementation, the comments drift around the generated code a bit.
 */
public class GComment
{
	/**
	 * The text of the comment.
	 */
	public String text;
	
	/**
	 * Determines whether to notate the comment as a line or as a block
	 */
	public boolean isLineComment;
	
	/**
	 * Convenience constructor
	 */
	protected GComment(String comment, boolean isLineComment)
	{
		this.text = comment;
		this.isLineComment = isLineComment;
	}
	
	GComment(JavaStyleComment comment)
	{
		this(comment.getText(), comment.isLineComment());
	}
	
	/**
	 * Convenience method to append <code>text</code> to this GComment.  
	 * Will change to a block comment with the addition of the first newline.
	 */
	public void add(String text)
	{
		if (this.text.indexOf("\n") >= 0)
		{
			this.isLineComment = false;
		}
		
		this.text += text;
	}

	/**
	 * Convenience method to add <code>text</code> to this GComment, delimited
	 * by a newline.
	 */	
	public void addLine(String text)
	{
		this.isLineComment = false;
		this.text += "\n" + text;
	}

	/**
	 * Constructs a new GComment and checks the <code>text</code> for newline
	 * to determine the value of <code>this.isLineComment</code>
	 */	
	public static GComment comment(String text)
	{
		return new GComment(text, (text.indexOf("\n") < 0));
	}
	
	/**
	 * Constructs a new GComment as a block comment, regardless of content.
	 */	
	public static GComment commentBlock(String text)
	{
		return new GComment(text, false);
	}
	
	public JavaStyleComment encode()
	{
		return (new JavaStyleComment(this.text, this.isLineComment, true, true));
	}
}

