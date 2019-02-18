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
 
package com.bitwise.decaf.editor;

import java.util.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.tree.*;

import org.hs.jfc.*;
import org.hs.util.*;
import org.hs.generator.*;

import com.bitwise.decaf.editor.grammar.*;
import com.bitwise.decaf.editor.config.*;

public class Percolator extends JDialog
{
	static final long serialVersionUID = -6007545221336312151L;

	protected static final String TITLE = "Decaf - Percolator";
	
	protected JPanel body;
	protected Brew brew;

	Percolator(Frame owner)	
	{
		super(owner);
		
		super.setTitle(TITLE);
	}
	
	public void assemble(Brew brew)
	{
		this.body = new JPanel(new GridLayout(1,1));
		
		if (brew == null)
		{
			this.brew = new Brew();
		}
		else
		{
			this.brew = brew;
		}
		
		this.body.add(this.brew.render());
	}
	
	public void start()
	{
		DecafEditor.registerWindow(this);

		super.getContentPane().add("Center", this.body);
		super.pack();
		DecafEditor.realEstateManager.place(Utils.RealEstateManager.LOWER_RIGHT, this);
	}
	
	public void display()
	{
		super.show();
		super.toFront();
	}
	
	public void perk(Phrase phrase)
	{
		if (phrase.getContent() instanceof DVariable)
		{
			DVariable content = (DVariable)phrase.getContent();
			if ((content.type != null) && content.type.equals(GType.THIS))
			{
				// lots o' these
				return;
			}
		}
		
		phrase.perk(true);
		
		this.brew.perk(phrase);
	}
	
	public void perk(DVariable variable)
	{
		this.brew.perk(variable);
	}
	
	public void perk(Collection brew)
	{
		if (brew.isEmpty())
		{
			return;
		}
		
		this.brew.perk(brew);
	}
	
	public Brew getBrew()
	{
		return this.brew;
	}
	
	// maybe set received items to "perk" mode, so they don't get all blue over nothing

	static class Brew implements Decaf
	{
		protected Utils.List variables;
		protected Utils.List phrases;
		protected Utils.List paragraphs;
		
		public Brew()
		{
			this.variables = new Utils.List();
			this.phrases = new Utils.List();
			this.paragraphs = new Utils.List();
		}
		
		public void perk(DVariable variable)
		{
			this.variables.add(variable);
		}
		
		public void perk(Phrase phrase)
		{
			this.phrases.add(phrase);
		}
		
		public void perk(Paragraph paragraph)
		{
			this.paragraphs.add(paragraph);
		}
		
		public void perk(Collection brew)
		{
			Object sample = brew.toArray()[0];
			if (sample instanceof Sentence)
			{
				this.paragraphs.add(new Fragment(brew));
			}
			else if (sample instanceof DVariable)
			{
				this.variables.addAll(brew);
			}
			else if (sample instanceof Phrase)
			{
				this.phrases.addAll(brew);
			}
		}
		
		public Form render()
		{
			Form render = new Form();
			
			render.addFormComponent(new StrayVariables(this.variables), new FormConstraints(0,0,FormLayout.LABEL_ON_TOP));
			render.addFormComponent(new PhraseListBox(this.phrases), new FormConstraints(10,0,FormLayout.LABEL_ON_TOP));
			render.addFormComponent(new ParagraphListBox(this.paragraphs), new FormConstraints(20,0,FormLayout.LABEL_ON_TOP));

			DecafEditor.hotStation.registerAll(render);
			
			return render;
		}
	}
	
	// would be nice to discard obvious duplicates
	static class PhraseListBox extends DListBox implements CodeNode.Listener, HotComponent
	{
		public PhraseListBox(Utils.List phrases)
		{
			super("Expressions", new PercolatorListModel(phrases));
			
			super.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			super.model.brewAction(this);
		}
		
		protected ClickListener getClickListener()
		{
			return (new JunkEditListener());
		} 
		
		protected void add(Object o)
		{
			Phrase add = (Phrase)o;
			add.setParent(new StrayPhraseParent(add));
			this.model.base().add(add);
		}
		
		public void register(HotComponent.Arbiter arbiter)
		{
			arbiter.offer(GExpression.class, COPY, MOVE | COPY);
			
			arbiter.receive(GVariable.class, LINK);
			arbiter.receive(GExpression.class, MOVE | COPY);
		}

		public Object give(Class type, int operation)
		{
			if (super.hasMultiple())
			{
				return super.give(type, operation);
			}
			else if (super.hasSingle())
			{
				Phrase selection = (Phrase)super.selection();
				switch (operation)
				{
					case MOVE:
						super.model.base().remove(selection);
					case SAMPLE:
						return selection;
					case COPY:
						return selection.deepCopy();
				}
			}
			
			return null;
		}

		public boolean isCompatible(Object hot)
		{
			return (!(hot instanceof HotCollection));
		}

		public void contentChanged(CodeNode.Event event)
		{
			super.repaint();
		}
		
		protected class JunkEditListener extends ClickListener
		{
			public void mouseClicked(MouseEvent e)
			{
				if (SwingUtilities.isLeftMouseButton(e) && (e.getClickCount() == 2))
				{
					int index = locationToIndex(new Point(e.getX(), e.getY()));
					if (index >= 0)
					{
						Phrase data = (Phrase)PhraseListBox.this.model.getElementAt(index);
						data.edit();
					}
				}
			}
		}
	}

	// make these uneditable... because, what are they?  fields?  locals?	
	static class StrayVariables extends Variables
	{
		public StrayVariables(Utils.List variables)
		{
			super(null, "Variables");
			
			super.model = new PercolatorListModel(variables);
			super.model.brewAction(this);
		}
		
		public void register(HotComponent.Arbiter arbiter)
		{
			arbiter.receive(GVariable.class, MOVE | COPY);
			
			arbiter.offer(GVariable.class, MOVE, COPY | MOVE);
			arbiter.offer(String.class, COPY);
		}

		public boolean isLocal()
		{
			throw (new RuntimeException(getClass().getName() + ".isLocal() should never be called!"));
		}
	}
	
	static class StrayPhraseParent extends Paragraph 
	{
		protected Cup forCup;
		
		public StrayPhraseParent(Phrase stray)
		{
			super(null);
			this.forCup = stray.identifyCup();
		}
		
		public boolean isInScope(DVariable variable, Object child)
		{
			return false;
		}
		
		public void collectVariables(Collection variables, Object child)
		{
		}
		
		public Cup identifyCup()
		{
			return this.forCup;
		}
	}
	
	static class Fragment extends Paragraph implements Grammar.Content, Utils.Renderable
	{
		protected static int index = 1;
		
		protected Cup forCup;
		protected int id = index++;
		
		public Fragment(Collection sentences)
		{
			super(null);
			
			init((Sentence)sentences.toArray()[0]);

			Sentence next;
			Iterator iterator = sentences.iterator();
			while (iterator.hasNext())
			{
				next = (Sentence)iterator.next();
				next.setParent(this);
				next.perk(true);
				super.add(next);
			}
		}
		
		public Fragment(Sentence sentence)
		{
			super(null);
			init(sentence);
			sentence.setParent(this);
			super.add(sentence);
		}
			
		private void init(Sentence sample)
		{
			this.forCup = sample.identifyCup();
			super.content.description = "Fragment " + this.id;
		}
		
		public boolean isInScope(DVariable variable, Object child)
		{
			return true;
		}
		
		public void collectVariables(Collection variables, Object child)
		{
		}
		
		public Cup identifyCup()
		{
			return this.forCup;
		}
		
		// handle will be null
		public MaskEditor edit(Grammar handle)
		{
			Sentence container = new Sentence(null);
			container.setContent(this);
			FragmentMask mask = new FragmentMask(container);
			mask.display(this);
			return (new MaskEditor(mask));
		}
		
		public JComponent render()
		{
			return (new Utils.RenderLabel("Fragment " + this.id));
		}

		public JComponent getComponent(Grammar handle)
		{
			return render();
		}
		
		public void setHandle(Grammar handle)
		{
		}
		
		public GConstruct getSource()
		{
			return super.content;
		}
		
		public Object get(java.lang.reflect.Field field)
		{
			return super.content.get(field);
		}
		
		public void set(java.lang.reflect.Field field, Object value)
		{
			super.content.set(field, value);
		}
		
		public Type getType()
		{
			return null;
		}
	}
	
	// a fragment is really a sentence consisting of a paragraph, isn't it?
	
	
	static class FragmentMask extends GrammarMask
	{
		public FragmentMask(Sentence fragment)
		{
			super(fragment);
			
			super.addFormComponent(new ParagraphBox("Statements", (Fragment)fragment.getContent()), new FormConstraints(10,0,FormLayout.LABEL_ON_TOP));
		}

		public void apply()
		{
			super.apply(this.data);
		}
		
		public Collection phraseGrammars() { return null; }
		public Collection sentenceGrammars() { return null; }
	}

	static class ParagraphListBox extends DListBox implements CodeNode.Listener, HotComponent
	{
		public ParagraphListBox(Utils.List paragraphs)
		{
			super("Code Fragments", new PercolatorListModel(paragraphs));
			
			super.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			super.model.brewAction(this);
		}
		
		protected ClickListener getClickListener()
		{
			return (new EditListener());
		}
		
		public void register(HotComponent.Arbiter arbiter)
		{
			arbiter.receive(GStatement.class, MOVE | COPY);
			
			arbiter.offer(GStatement.class, MOVE, COPY | MOVE);
		}

		public Object give(Class type, int operation)
		{
			Paragraph selected = (Paragraph)this.selection();
			Collection statements;
			boolean departing = false;
			switch (operation)
			{
				case MOVE:
					super.model.base().remove(selected);
					departing = true;
				case SAMPLE:
					statements = selected.getData().statements;
					break;
				case COPY:
					statements = Utils.deepCopy(selected.getData().statements);
					departing = true;
					break;
				default:
					return null;
			}
			
			if (departing)
			{
				Iterator iterator = statements.iterator();
				while (iterator.hasNext())
				{
					((Grammar)iterator.next()).perk(false);
				}
			}
			
			return (new HotCollection(GStatement.class, statements));
		}
		
		public void take(Object hot)
		{
			Fragment fragment;
			if (hot instanceof HotCollection)
			{
				Collection sentences = ((HotCollection)hot).collection();
				if (sentences.isEmpty())
				{
					return;
				}
				else
				{
					fragment = new Fragment(sentences);
				}
			}
			else
			{
				fragment = new Fragment((Sentence)hot);
			}
			
			super.add(fragment);
			fragment.addListener(this);
			super.repaint();
		}
		
		public boolean isCompatible(Object hot)
		{
			if (hot instanceof HotCollection)
			{
				HotCollection collection = (HotCollection)hot;
				return (collection.isHomogenous() && (collection.getType() == GStatement.class));
			}
			
			return true;
		}
		
		public void contentChanged(CodeNode.Event event)
		{
			Fragment source = (Fragment)event.getSource();

			if (source.isEmpty())
			{
				super.model.base().remove(source);
			}
			
			this.repaint();
		}
		
		protected class EditListener extends ClickListener
		{
			public void mouseClicked(MouseEvent e)
			{
				if (SwingUtilities.isLeftMouseButton(e) && (e.getClickCount() == 2))
				{
					int index = locationToIndex(new Point(e.getX(), e.getY()));
					if (index >= 0)
					{
						Fragment fragment = (Fragment)ParagraphListBox.this.model.getElementAt(index);
						MaskEditor editor = fragment.edit(null);
			 			editor.assemble();
						editor.start();
					}
				}
			}
		}
	}
	
	static class PercolatorListModel extends Utils.ListModel
	{
		public PercolatorListModel(Utils.List paragraphs)
		{
			super(paragraphs);
		}
		
		public Action brewAction(JList list)
		{
			return (new VaporizeAction(list));
		}

		class VaporizeAction extends BrewAction 
		{
			public VaporizeAction(JList list)
			{
				super(list);
			}
			
			protected void act(int[] upon)
			{
				PercolatorListModel.this.remove(upon);
			}
		}
	}
	
	public Object writeReplace()
		throws java.io.ObjectStreamException
	{
		throw (new Utils.SerialException(getClass()));
	}
}
