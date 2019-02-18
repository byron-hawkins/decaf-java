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

import java.util.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;

import org.hs.jfc.*;
import org.hs.util.*;
import org.hs.generator.*;

import com.bitwise.decaf.editor.*;
import com.bitwise.decaf.editor.config.GrammarConfig;

/**
 * The standard editor component for a Paragraph.
 */
public class ParagraphBox extends DListBox implements GrammarComponent, CodeNode.Listener, HotComponent
{
	protected static final int KEYBOARD_MODIFIERS = (InputEvent.ALT_MASK | InputEvent.SHIFT_MASK | InputEvent.CTRL_MASK);
	
	public static GrammarMenu grammarMenu;
	
	protected static Sentence currentContext;
	
	/**
	 * @return a handle to the {@link Sentence} that has most
	 * recently been created in response to a user's invocation 
	 * of the sentence menu.
	 */
	public static Sentence getCurrentContext()
	{
		return currentContext;
	}
	
	/**
	 * internal
	 */
	public static void initMenu(GrammarConfig config)
		throws Exception
	{
		Iterator sentences = config.getSentenceGrammars().iterator();
		grammarMenu = new GrammarMenu(GrammarMenu.SENTENCE_MENU_ID);
		String grammarClassname;
		Class grammarClass;
		GrammarMask node;
		Iterator actions;
		Action action;
		while (sentences.hasNext())
		{
			grammarClassname = (String)sentences.next();
			grammarClass = Class.forName(grammarClassname);
			node = (GrammarMask)grammarClass.newInstance();

			actions = node.sentenceGrammars().iterator();
			while (actions.hasNext())
			{
				action = (Action)actions.next();
				grammarMenu.addGrammar(action);
			}
		}
		grammarMenu.close();
	}
	
	protected MenuListener menuListener;

	protected Paragraph data;
	
	protected JLabel label;
	
	/**
	 * Convenience constructor; routes to {@link ParagraphBox(String, Paragraph)}.
	 */
	public ParagraphBox(String label, GConstruct data)
	{
		this(label, (Paragraph)data);
	}
	
	/**
	 * Create a ParagraphBox to display <code>data</code>.
	 */
	public ParagraphBox(String label, Paragraph data)
	{
		super(label, new Utils.ListModel(data.getContent()));

		this.label = new JLabel(label);
		this.data = data;
		this.data.addListener(this);
		
		this.hot = false;

		this.menuListener = (MenuListener)super.clickListener;
		grammarMenu.addPopupMenuListener(menuListener);
		super.model.brewAction(this);

		super.getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), "EnterEditsSentence");
		super.getActionMap().put("EnterEditsSentence", new EnterEditsSentence());
	}

	/**
	 * Call this upon disposal of the container in which this ParagraphBox resides.
	 */	
	public void terminate()
	{
		this.data.removeListener(this);
	}
	
	protected void add(Object o)
	{
		Sentence data;
		if (o instanceof DVariable)
		{
			data = LocalVariable.wrap((DVariable)o, this.data);
		}
		else
		{
			data = (Sentence)o;
		}
		data.addListener(this);
		data.setParent(this.data);
		
		this.model.base().add(this.menuListener.getInsertIndex(), data);
	}
	
	protected void insert(Object o)
	{
		this.add(o);
	}

	/**
	 * @return the Paragraph this ParagraphBox displays
	 */	
	public Paragraph getData()
	{
		return this.data;
	}
	
	/**
	 * @return the Paragraph this ParagraphBox displays
	 */
	public CodeNode getGrammar()
	{
		return this.data;
	}
	
	protected void insertNew()
	{
		currentContext = new Sentence(this.data);
		currentContext.addListener(new NewListener());
	}

	/**
	 * Register this ParagraphBox with <code>arbiter</code> as follows:
	 * <p>
	 * <u>offer</u><br>
	 * 1) GVariable by any operation, defaulting to {@link HotComponent#LINK}<br>
	 * 2) GExpression by {@link HotComponent#MOVE} | {@link HotComponent#COPY}, defaulting to {@link HotComponent#COPY}<br>
	 * 3) GStatement by {@link HotComponent#MOVE} | {@link HotComponent#COPY}, defaulting to {@link HotComponent#MOVE}<br>
	 * 4) Typed by {@link HotComponent#COPY}<br>
	 * <p>
	 * <u>receive</u><br>
	 * 1) GVariable by {@link HotComponent#MOVE} | {@link HotComponent#COPY}<br>
	 * 2) GStatement by {@link HotComponent#MOVE} | {@link HotComponent#COPY}<br>
	 *
	 * @see HotComponent#register(HotComponent.Arbiter)
	 * @see HotComponent#Arbiter
	 */
	public void register(HotComponent.Arbiter arbiter)
	{
		arbiter.offer(GVariable.class, LINK, ANY);
		arbiter.offer(GExpression.class, COPY, MOVE | COPY);	
		arbiter.offer(GStatement.class, MOVE, COPY | MOVE);
		arbiter.offer(Typed.class, COPY);
		
		arbiter.receive(GVariable.class, COPY | MOVE);
		arbiter.receive(GStatement.class, COPY | MOVE);
	}

	/**
	 * Returns the current selection in the following manner:
	 * <p>
	 * &middot; if multiple items are selected, give a HotCollection
	 * containing the selected items and the type GStatement<br>
	 * &middot; if a Sentence containing a DVariable is selected, 
	 * give the variable<br>
	 * &middot; if a Sentence containing anything else is selected, 
	 * give the sentence
	 */
	public Object give(Class type, int operation)
	{
		if (super.hasMultiple())
		{
			return super.give(type, operation);
		}
		else if (super.hasSingle())
		{
			Sentence selection = (Sentence)super.selection();
			Copy give;
			Object data = selection.getContent();
			if (data instanceof DVariable)
			{
				give = (Copy)data;
			}
			else
			{
				give = selection;
			}
			
			switch (operation)
			{
				case LINK:
				case SAMPLE:
					return give;
				case MOVE:
					super.model.base().remove(selection);
					return give;
				case COPY:
					return give.deepCopy();
			}
		}
		
		return null;
	}

	/**
	 * Repaint this component.
	 */
	public void contentChanged(CodeNode.Event event)
	{
		super.repaint();
	}
	
	protected ClickListener getClickListener()
	{
		return (new MenuListener());
	}
	
	class NewListener implements CodeNode.Listener
	{
		public void contentChanged(CodeNode.Event event)
		{
			ParagraphBox.this.add(currentContext);
			((CodeNode)event.getSource()).removeListener(this);
		}
	}
	
	class MenuListener extends ClickListener implements PopupMenuListener
	{
		protected int yClicked;
		
		public MenuListener()
		{
			this.yClicked = -1;
			super.insertIndex = -1;
		}
		
		public void mouseEntered(MouseEvent e)
		{
		}
		
		public void mouseExited(MouseEvent e)
		{
		}
		
		public void mousePressed(MouseEvent e)
		{
			super.mousePressed(e);

			if (SwingUtilities.isRightMouseButton(e) && (e.getModifiers() & KEYBOARD_MODIFIERS) == 0)
			{
	        	clearLine();

				if (ParagraphBox.this.model.getSize() == 0)
				{
					this.yClicked = 1;
				}
				else
				{
					Point cellTop = indexToLocation(super.insertIndex);
					Rectangle cell = getCellBounds(super.insertIndex, super.insertIndex);
					if (e.getY() > (cellTop.y + (cell.height / 2)))
					{
						super.insertIndex++;
					}
					if (super.insertIndex == getModel().getSize())
					{
						this.yClicked = cellTop.y + cell.height;
					}
					else
					{
						this.yClicked = indexToLocation(super.insertIndex).y;
					}
				}
				
				Graphics g = getGraphics();
				g.setColor(Color.red);
				g.drawLine(0, this.yClicked, getWidth(), this.yClicked);
				ParagraphBox.this.insertNew();
				grammarMenu.show(ParagraphBox.this, e.getX(), e.getY());
			}
		}
		
		public void mouseReleased(MouseEvent e)
		{
		}
		
		public void mouseClicked(MouseEvent e)
		{
			if (SwingUtilities.isLeftMouseButton(e) && (e.getClickCount() == 2))
			{
				int index = locationToIndex(new Point(e.getX(), e.getY()));
				if (index >= 0)
				{
					Sentence data = (Sentence)ParagraphBox.this.model.getElementAt(index);
					data.edit();
				}
			}
		}

		public void popupMenuCanceled(PopupMenuEvent e)
		{
        	clearLine();
		}
		
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e)
        {
        	clearLine();
        }
        
        public void popupMenuWillBecomeVisible(PopupMenuEvent e)
        {
        }
        
        protected void clearLine()
        {
        	if (this.yClicked >= 0)
        	{
				Graphics g = getGraphics();
				g.setColor(getBackground());
				g.drawLine(0, this.yClicked, getWidth(), this.yClicked);
				this.yClicked = -1;
			}
        }
	}
	
	class EnterEditsSentence extends AbstractAction
	{
		public void actionPerformed(ActionEvent e)
		{
			((Sentence)ParagraphBox.super.getSelectedValue()).edit();
		}
	}
	
}