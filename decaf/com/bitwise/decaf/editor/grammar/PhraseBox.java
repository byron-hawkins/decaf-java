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
import javax.swing.border.*;
import javax.swing.event.*;

import org.hs.jfc.*;
import org.hs.generator.*;

import com.bitwise.decaf.editor.*;
import com.bitwise.decaf.editor.config.GrammarConfig;

/**
 * The standard component for displaying {@link Phrase}.
 */
public class PhraseBox extends JPanel implements GrammarComponent, CodeNode.Listener, Conclusion
{
	protected static final int KEYBOARD_MODIFIERS = (InputEvent.ALT_MASK | InputEvent.SHIFT_MASK | InputEvent.CTRL_MASK);
	
	public static GrammarMenu grammarMenu;
	
	protected static Phrase currentContext;
	protected static PhraseBox currentSubject;
	
	/**
	 * @return a handle to the {@link Phrase} contained in the 
	 * PhraseBox in which the user most recently selected an 
	 * item from the popup phrase menu.
	 */
	public static Phrase getCurrentContext()
	{
		return currentContext;
	}
	
	/**
	 * internal
	 */
	public static void initMenu(GrammarConfig config)
		throws Exception
	{
		Iterator phrases = config.getPhraseGrammars().iterator();
		String grammarClassname;
		Class grammarClass;
		GrammarMask node;
		grammarMenu = new GrammarMenu(GrammarMenu.PHRASE_MENU_ID);
		Iterator actions;
		Action action;
		while (phrases.hasNext())
		{
			grammarClassname = (String)phrases.next();
			grammarClass = Class.forName(grammarClassname);
			node = (GrammarMask)grammarClass.newInstance();
			
			actions = node.phraseGrammars().iterator();
			while (actions.hasNext())
			{
				action = (Action)actions.next();
				grammarMenu.addGrammar(action);
			}
		}
		grammarMenu.addGrammar(new EditLiteralAction());
		grammarMenu.addGrammar(new ClearAction());
		grammarMenu.close();
	}
	
	protected JLabel label;
	
	protected Phrase data;
	protected Type requiredType;
	
	protected Color hotColor;
	
	protected Border previousBorder;

	/**
	 * Convenience constructor: forwards to {@link PhraseBox(String, Phrase)}
	 */	
	public PhraseBox(String label, GConstruct data)
	{
		this(label, (Phrase)data);
	}
	
	/**
	 * Convenience constructor: forwards to {@link PhraseBox(String, Phrase)}
	 */	
	public PhraseBox(String label, GExpression data)
	{
		this(label, (Phrase)data);
	}
	
	/**
	 * Create a PhraseBox to display <code>data</code>, visually identified in 
	 * the {@link GrammarMask} by <code>label</code>.
	 */
	public PhraseBox(String label, Phrase data)
	{
		super();
		
		this.data = data;
		this.data.addListener(this);
		this.requiredType = null;
		this.label = new JLabel(label);
		
		MenuListener menuListener = new MenuListener();
		addMouseListener(menuListener);
		
		super.setBorder(BorderFactory.createLineBorder(Color.black, 1));
		super.setBackground(Color.white);
		super.setPreferredSize(new Dimension(200, 30));
		super.setLayout(new FlowLayout(FlowLayout.LEFT));
		super.setFocusable(true);

		InputMap input = super.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		ActionMap action = super.getActionMap();
		
		input.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, true), "DeletePhraseBoxContents");
		action.put("DeletePhraseBoxContents", new Delete());

		render();
	}

	/**
	 * Establish a type requirement in this PhraseBox; calls
	 * {@link Grammar.Content#setExpectedType(Type)} on <code>this.data</code>.
	 */	
	public void setRequiredType(Type requiredType)
	{
		this.requiredType = requiredType;
		
		this.data.setExpectedType(requiredType);
	}
	
	/**
	 * Return the label by which this PhraseBox will be visually 
	 * identified in the {@link GrammarMask}.
	 */
	public Component getLabel()
	{
		return this.label;
	}
	
	/**
	 * @return <code>this</code>
	 */
	public Component getComponent()
	{
		return this;
	}
	
	/**
	 * @return <code>this</code> (no special view container is required
	 * for a PhraseBox).
	 */
	public Component getComponentView()
	{
		return this;
	}
	
	protected Phrase getData()
	{
		return this.data;
	}

	/**
	 * @return the Phrase being displayed
	 */
	public CodeNode getGrammar()
	{
		return this.data;
	}
	
	void set(Object o)
	{
		this.data.setData(o);
		
		render();
	}
	
	protected void setData(Phrase data)
	{
		this.data = data;
		this.render();
	}
	
	protected void render()
	{
		render(this.data.render());
	}
	
	protected void edit()
	{
		if (this.data.isEmpty() || (this.data.getContent() instanceof Literal.Data))
		{
			editLiteral();
		}
		else
		{
			this.data.edit();
		}
	}
	
	protected void editLiteral()
	{
		render(Literal.edit(this, this.data));
	}
	
	protected void render(Component component)
	{
		super.removeAll();

		super.add(component);

		super.revalidate();
		super.repaint();
	}

	/*
	 * <u>offer</u><br>
	 * 1) GExpression by {@link HotComponent#MOVE} | {@link HotComponent#COPY}, defaulting to {@link HotComponent#COPY}<br>
	 * <p>
	 */
	 
	/**
	 * Register this PhraseBox with <code>arbiter</code> as follows:
	 * <p>
	 * <u>receive</u><br>
	 * 1) GVariable by {@link HotComponent#LINK}<br>
	 * 2) GExpression by {@link HotComponent#MOVE} | {@link HotComponent#COPY}
	 *
	 * @see HotComponent#register(HotComponent.Arbiter)
	 * @see HotComponent$Arbiter
	 */
	public void register(HotComponent.Arbiter arbiter)
	{
		arbiter.offer(GExpression.class, COPY, MOVE | COPY);
		
		arbiter.receive(GVariable.class, LINK);
		arbiter.receive(GExpression.class, MOVE | COPY);
	}

	/**
	 * @return an empty Vector (PhraseBox is a generic display container 
	 * with no associated buttons.
	 */	
	public Collection actions()
	{
		return (new Vector());
	}
	
	/**
	 * @return false (PhraseBox can not be hot selected at this time).
	 */
	public boolean pick(boolean b)
	{
		return false;
	}
	
	/**
	 * @return null
	 */
	public Object give(Class type, int operation)
	{
		Phrase give = this.data;
		if (operation == MOVE)
		{
			set(null);
		}
		
		return give;
	}

	/**
	 * Considser the <code>hot</code> object as follows:
	 * <p>
	 * &middot; if it's a HotCollection, reject it<br>
	 * &middot; if a required type has been applied to this PhraseBox,
	 * accept <code>hot</code> only if it can be assigned to that type
	 * (which implies that <code>hot</code> must be {@link Typed}).<br>
	 * &middot; if <code>hot</code> is a DVariable, accept it only if 
	 * the declaration of <code>hot</code> lies along the path from 
	 * this PhraseBox's data to the enclosing method body (i.e., if the 
	 * variable is in scope).
	 * &middot; accept <code>hot</code> under any other conditions.
	 */	
	public boolean isCompatible(Object hot)
	{
		if (hot instanceof HotCollection)
		{
			return false;
		}
		
		if (this.requiredType != null) 
		{
			if (!(hot instanceof Typed))
			{
				return false;
			}
			
			Typed typed = (Typed)hot;
			if (!this.requiredType.isAssignableFrom(typed.getType()))
			{
				return false;
			}
		}
		
		if (hot instanceof DVariable)
		{
			DVariable hotVariable = (DVariable)hot;
			return this.data.isInScope(hotVariable, null);
		}
		else
		{
			return true;
		}
	}

	/**
	 * Apply <code>hot</code> to this PhraseBox's data, sending any
	 * current contents to the Percolator.
	 */	
	public void take(Object hot)
	{
		if (!this.data.isEmpty())
		{
			DecafEditor.percolator.perk((Phrase)this.data.deepCopy());
		}
		
		set(hot);
	}
	
	/**
	 * Make note of the current hot color.
	 */
	public void setHotColor(Color hotColor)
	{
		this.hotColor = hotColor;
	}

	/**
	 * Repaint this component.
	 */
	public void contentChanged(CodeNode.Event event)
	{
		this.render();
	}
	
	/**
	 * Repaint this component (called when an editor of this PhraseBox's data has closed).
	 */
	public void ok()
	{
		render();
	}
	
	/**
	 * Repaint this component (should never be called because Phrase editors
	 * do not support cancel).
	 */
	public void cancel()
	{
		render();
	}
	
	class Delete extends AbstractAction
	{
		public void actionPerformed(ActionEvent e)
		{
			PhraseBox.this.data.clear();
		}
	}
	
	class MenuListener implements MouseListener
	{
		public void mouseEntered(MouseEvent e)
		{
		}
		
		public void mouseExited(MouseEvent e)
		{
		}
		
		public void mousePressed(MouseEvent e)
		{
			if (SwingUtilities.isRightMouseButton(e) && ((e.getModifiers() & KEYBOARD_MODIFIERS) == 0))
			{
				PhraseBox.currentContext = PhraseBox.this.data;
				PhraseBox.currentSubject = PhraseBox.this;
				grammarMenu.show(PhraseBox.this, e.getX(), e.getY());
			}
		}
		
		public void mouseReleased(MouseEvent e)
		{
		}
		
		public void mouseClicked(MouseEvent e)
		{
			if (SwingUtilities.isLeftMouseButton(e) && (e.getClickCount() == 2))
			{
				PhraseBox.this.edit();
			}
		}
	}
	
	static class EditLiteralAction extends AbstractAction
	{
		public EditLiteralAction()
		{
			super();
			
			super.putValue(NAME, "Constant Value");
		}
		
		public void actionPerformed(ActionEvent event)
		{
			PhraseBox.currentSubject.editLiteral();
		}
	}

	static class ClearAction extends AbstractAction
	{
		public ClearAction()
		{
			super();
			
			super.putValue(NAME, "Clear");
		}
		
		public void actionPerformed(ActionEvent event)
		{
			PhraseBox.currentContext.clear();
			PhraseBox.currentContext.notifyNewData();
		}
	}

/*
		public void setInlineEditor(Component editor)
		{
			this.source.render(editor);
			
			if (editor instanceof JComponent)
			{
				((JComponent)editor).grabFocus();
			}
		}
*/
	
	public Object writeReplace()
		throws java.io.ObjectStreamException
	{
		throw (new Utils.SerialException(getClass()));
	}
}