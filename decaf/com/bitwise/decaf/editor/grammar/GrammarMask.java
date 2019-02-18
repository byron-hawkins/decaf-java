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

import com.bitwise.decaf.editor.*;
import com.bitwise.decaf.editor.config.*;
import org.hs.generator.*;
import org.hs.jfc.*;
import org.hs.generator.*;

/**
 * Within the context of a method body, the user has free reign to create
 * whatever statements and expressions they wish (known here as phrases and 
 * sentences, respectively, and classified as grammars) .  Sentences are 
 * visually contained in the 
 * ParagraphBox component, and each phrase in a PhraseBox.  Given such a 
 * box on screen, the user may right-click to view a menu of the available
 * grammars they can create there.  Selecting any of those options opens
 * a dialog whose main panel is a subclass of this GrammarMask class.  The 
 * essential purpose of a mask, then, is to take the various concepts and 
 * pitfalls of a particular Java code construct and hide them from the user, 
 * such that they only see a friendly face that they can 
 * easily figure out how to work with.  Sounds like Disneyland, doesn't it?
 * Admittedly, working with a restrictive mask can be cumbersome, but it 
 * beats using a new programming language that you don't even know where to 
 * start with.
 * <p>Creating a new subclass of GrammarMask involves a number of steps which
 * are not complicated, but must be closely followed.  (I'm sure this 
 * architecture can be made substantially more generic, and am pondering the
 * best means as I write).  The mask instance makes itself available in the 
 * grammar menus by 
 * returning instances of javax.swing.Action from the abstract methods 
 * <code>phraseGrammars</code> and <code>sentenceGrammars</code>.  
 * (See com/bitwise/decaf/editor/config/config.dtd, element <Grammars>, 
 * for the entry point of grammar masks into the editor).
 * These actions should obtain a handle to the current grammar (a Phrase or
 * a Sentence) by calling <code>PhraseBox.getCurrentContext()</code> or 
 * <code>ParagraphBox.getCurrentContext()</code>, respectively.  Set the data
 * of your mask in the handle using the <code>setContent()</code> method;
 * your data is expected to contain one of the org.hs.generator classes, and 
 * to implement the interface Grammar.Content.  I recommend subclassing the 
 * generator construct with an inner class and implementing Grammar.Content
 * from there.  Your must apply empty handles (Phrase`s and Paragraph`s)
 * to the members of your data class, so that the user can put whatever they
 * like in there.  Make those handles available to the user with PhraseBox
 * and ParagraphBox in your dialog.  Beyond these requirements, a mask is 
 * expected to follow certain patterns established in this package:
 * <p>
 * 1) If the handle you start out with is not empty, check its contents to 
 * see if you can make some use of them.  Perhaps they are from a previous 
 * instance of your component, in which case you might want to apply the old 
 * values immediately.</br>
 * 2) grammar content should be updated immediately with user activity.<br>
 * 3) Perform as much syntax and content checking as possible by adding
 * EnforcementOfficer`s (of your derivation) to the handle of and handles 
 * within your data.  
 */
public abstract class GrammarMask extends Form
{
	protected DTextField description;
	protected FormConstraints descriptionConstraints;
	
	protected Grammar data;
	
	GrammarMask()
	{
		super();
	}

	/**
	 * Abstract constructor, to be called with the (possibly empty) handle to
	 * your mask's current context.
	 *
	 * @param data the handle to the mask's current context.
	 */	
	public GrammarMask(Grammar data)
	{
		super();
		
		this.data = data;
		
		this.description = new DTextField(25);
		this.description.setLabelText("Description");
		this.description.setRepresents(GConstruct.getField(GConstruct.class, "description"));
		
		super.addFormComponent(this.description, new FormConstraints(0,0));
	}

	/**
	 * Displays the description field that is inherent to every GConstruct.
	 * 
	 * @param code the code construct to display
	 */	
	public void display(GConstruct code)
	{
		this.description.display(code);
	}
	
	/**
	 * Applies the user's description entry to the current code construct.
	 *
	 * @param code the code construct currently being described by the user
	 */
	public void apply(GConstruct code)
	{
		this.description.apply(code);
	}
	
	void focus(boolean b)
	{
		this.data.focus(b);
	}

	/**
	 * Called by Decaf at startup to obtain instances of javax.swing.Action
	 * for assembly into the phrase menu, which pops up when the user right-
	 * clicks in a PhraseBox.  
	 *
	 * @return a Collection of actions, one for each menu item you would like
	 * to have in the phrase menu.
	 */
	public abstract Collection phraseGrammars();
	
	/**
	 * Called by Decaf at startup to obtain instances of javax.swing.Action
	 * for assembly into the sentence menu, which pops up when the user right-
	 * clicks in a ParagraphBox.
	 *
	 * @return a Collection of actions, one for each menu item you would like
	 * to have in the sentence menu.
	 */
	public abstract Collection sentenceGrammars();

	/**
	 * Called by this class when the user clicks the OK button.  Your component
	 * is expected to update its data immediately with user activity, but you may
	 * perform any closing activities at this time.  Be sure to call my method
	 * <code>apply(GConstruct)</code> with your handle in your implementation of
	 * this method.
	 */	
	public abstract void apply();
}

