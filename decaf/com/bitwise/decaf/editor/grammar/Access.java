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
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import com.bitwise.decaf.editor.*;
import com.bitwise.decaf.editor.config.FormConstraints;
import org.hs.generator.*;
import org.hs.jfc.*;
import org.hs.generator.*;

public class Access extends GrammarMask
{
	static final long serialVersionUID = -8689019983018564440L;

	protected static Vector phraseGrammars;
	protected static Vector sentenceGrammars;
	
	static
	{
		phraseGrammars = new Vector();
		phraseGrammars.add(new NewAccess());

		sentenceGrammars = new Vector();
		sentenceGrammars.add(new NewAccess());
	}
	
	protected Data data;
	protected MemberChooser chooser;
	protected boolean isPhrase;
	
	public Access()
	{
		// for reference purposes only!
		super();
	}
	
	// use a default DVariable of type GType.THIS (dType cup) and no name
	
	public Access(Grammar handle)
	{
		super(handle);
		
		this.isPhrase = (handle instanceof Phrase);
		
		Object content = handle.getContent();
		if ((content != null) && (content instanceof Data))
		{
			this.data = (Data)content;
			super.display(this.data);
		}
		else
		{
			this.data = new Data(handle);
		}

		PhraseBox recipient = new PhraseBox("Recipient", this.data.getRecipient());
		super.addFormComponent(recipient, new FormConstraints(10,0));
		
		this.chooser = new MemberChooser("Member", this.data.getRecipient(), handle);
		Parameters parameters = new Parameters("Parameters", this.chooser, this);
		super.addFormComponent(this.chooser, new FormConstraints(20,0,FormLayout.LABEL_ON_TOP));
		super.addFormComponent(parameters, new FormConstraints(30, 0, FormLayout.LABEL_ON_TOP));
		
		this.chooser.apply(this.data);
		parameters.apply(this.data);
	}
	
	public Collection phraseGrammars()
	{
		return phraseGrammars;
	}
	
	public Collection sentenceGrammars()
	{
		return sentenceGrammars;
	}
	
	public void apply()
	{
		super.apply(this.data);
		this.chooser.terminate();
	}
	
	static class MemberChooser extends JList implements FormComponent
	{
		protected JLabel label;
		protected Phrase recipient;
		protected Grammar handle;

		protected MemberListModel model;
		
		protected RecipientOfficer recipientOfficer;
		
		public MemberChooser(String label, Phrase recipient, Grammar handle)
		{
			super();
			
			this.label = new JLabel(label);
			this.recipient = recipient;
			this.handle = handle;
			
			this.model = new MemberListModel();
			super.setModel(this.model);
			super.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			super.setFixedCellWidth(DecafEditor.LIST_CELL_WIDTH);
			
			this.recipient.patrol(this.recipientOfficer = new RecipientOfficer(this));
			
			// listen to recipient and populate this with applicable methods 
			// as its content changes.  Also need to listen to target PhraseBox
			// (if any) to know which methods are applicable.
		}
		
		public void apply(Data data)
		{
			this.recipientOfficer.call();
			if (!data.isEmpty())
			{
				super.setSelectedValue(this.model.getItem(data), true);
			}
		}
		
		public void terminate()
		{
			this.recipient.retire(this.recipientOfficer);
		}

		public Grammar getHandle()
		{
			return this.handle;
		}

		public Component getLabel()
		{
			return this.label;
		}
		
		public Component getComponent()
		{
			return this;
		}
		
		public Component getComponentView()
		{
			return (new JScrollPane(this));
		}
		
		static class MemberListModel extends Utils.ListModel
		{
			protected Utils.CollectionMap byName;
			
			public MemberListModel()
			{
				super();
				
				this.byName = new Utils.CollectionMap();
			}
			
			public void addMethod(Method method)
			{
				MethodListItem item = new MethodListItem(method);
				super.base.add(item);
				String methodName = method.getName();
				if (methodName == null)
				{
					methodName = "<unnamed>";
				}
				this.byName.add(methodName, item);
			}
			
			public void addVariable(Variable variable)
			{
				FieldListItem item = new FieldListItem(variable);
				super.base.add(item);
				this.byName.add(variable.getName(), item);
			}
			
			public Object getItem(Data data)
			{
				Collection namesakes = this.byName.getCollection(data.getName());
				if (namesakes == null)
				{
					return null;
				}
				
				Iterator iterator = namesakes.iterator();
				Object next;
				while (iterator.hasNext())
				{
					next = iterator.next();
					if (data.matchesListItem(next))
					{
						return next;
					}
				}
				return null;
			}
			
			public void clear()
			{
				this.byName.clear();
				super.base.clear();
			}
		}
		
		static class MethodListItem 
		{
			protected static StringBuffer buffer = new StringBuffer();
			
			protected String display;
			protected Method data;
			
			public MethodListItem(Method method)
 			{
 				this.data = method;
 				
 				buffer.setLength(0);
 				
 				if (method.isConstructor())
 				{
 					buffer.append("new ");
 				}
 				
 				buffer.append(method.getReturnType().getDisplayName());
 				
 				if (!method.isConstructor())
 				{
	 				buffer.append(" ");
	 				buffer.append(method.getName());
	 			}
 				
 				Method.Parameter parameter;
 				Collection parameters = method.getParameters();
 				if (!parameters.isEmpty())
 				{
 					buffer.append("(");
 					Iterator iterator = parameters.iterator();
 					while (iterator.hasNext())
 					{
 						parameter = (Method.Parameter)iterator.next();
 						buffer.append(parameter.getType().getDisplayName());
 						if (parameter.getName() != null)
 						{
 							buffer.append(" ");
 							buffer.append(parameter.getName());
 						}
 						if (iterator.hasNext())
 						{
 							buffer.append(", ");
 						}
 					}
 					buffer.append(")");
 				}
 				
 				this.display = buffer.toString();
 			}
 			
 			public Method getData()
 			{
 				return this.data;
 			}
 			
 			public String toString()
 			{
 				return this.display;
 			}
 		}
 			
		static class FieldListItem
		{
			protected Variable data;
			protected String display;

			public FieldListItem(Variable variable)
			{
				this.data = variable;
				this.display = variable.getType().getDisplayName() + " " + variable.getName();
			}
			
			public Variable getData()
			{
				return this.data;
			}
			
			public String toString()
			{
				return this.display;
			}
		}
		
		static class RecipientOfficer extends Officer implements Officer.Foreign
		{
			// do need to refresh on readResolve()
			protected transient MemberChooser chooser;
			
			public RecipientOfficer(MemberChooser chooser)
			{
				this.chooser = chooser;
			}
			
			public void call()
			{
				this.chooser.model.clear();
				
				if (!this.chooser.recipient.getType().isPrimitive())
				{
					Method method;
					boolean staticOnly = this.chooser.recipient.isClassReference();
					if (staticOnly)
					{
						Iterator constructors = this.chooser.recipient.getType().getConstructors().iterator();
						while (constructors.hasNext())
						{
							method = (Method)constructors.next();
							this.chooser.model.addMethod(method);
						}
					}

					Iterator methods = this.chooser.recipient.getType().getMethods().iterator();
					while (methods.hasNext())
					{
						method = (Method)methods.next();
						if ((!staticOnly) || java.lang.reflect.Modifier.isStatic(method.modifiers()))
						{
							this.chooser.model.addMethod(method);
						}
					}
					
					Iterator fields = this.chooser.recipient.getType().getFields().iterator();
					Variable variable;
					while (fields.hasNext())
					{
						variable = (Variable)fields.next();
						if ((!staticOnly) || java.lang.reflect.Modifier.isStatic(variable.modifiers()))
						{
							this.chooser.model.addVariable(variable);
						}
					}
				}
			}
		}
	
		public Object writeReplace()
			throws java.io.ObjectStreamException
		{
			throw (new Utils.SerialException(getClass()));
		}
	}
	
	static class Parameters extends Form implements FormComponent, ListSelectionListener
	{
		protected JLabel label;
		protected MemberChooser chooser;
		protected JComponent container;
		protected Vector parameterBoxes;
		
		public Parameters(String label, MemberChooser chooser, JComponent container)
		{
			super();
			
			this.label = new JLabel(label);
			
			this.chooser = chooser;
			this.chooser.addListSelectionListener(this);
			
			this.container = container;
			
			this.parameterBoxes = new Vector();
		}
		
		public void apply(Data data)
		{
			if (!data.hasArguments())
			{
				super.removeAll();
				this.parameterBoxes.clear();
				return;
			}
			
			PhraseBox parameterBox;
			Iterator parameters = this.parameterBoxes.iterator();
			Iterator arguments = data.getArguments().iterator();
			Phrase argument;
			while (parameters.hasNext())
			{
				if (!arguments.hasNext())
				{
					break;
				}
				parameterBox = (PhraseBox)parameters.next();
				argument = (Phrase)arguments.next();
				if (!argument.isEmpty())
				{
					parameterBox.set(argument);
				}
			}
		}
		
		public Component getLabel()
		{
			return this.label;
		}
		
		public Component getComponent()
		{
			return this;
		}
		
		public Component getComponentView()
		{
			return this;
		}
		
		public void valueChanged(ListSelectionEvent event)
		{
			super.removeAll();
			this.parameterBoxes.clear();
			
			Object listItem = this.chooser.getSelectedValue();
			if (listItem == null) 
			{
				return;
			}
			
			Grammar handle = this.chooser.getHandle();
			Data source = (Data)handle.getContent();
			source.applyListItem(listItem);
			
			Arguments arguments = source.getArguments();
			if (arguments != null)
			{
				Method.Parameter parameter;
				Type type;
				PhraseBox parameterBox;
				Iterator parameters = source.getParameters();
				Argument argument;
				int index = 0;
				while (parameters.hasNext())
				{
					parameter = (Method.Parameter)parameters.next();
					type = parameter.getType();
					argument = arguments.applyType(handle, type, index);
					parameterBox = new PhraseBox(type.getDisplayName() + " " + parameter.getName(), argument);
					parameterBox.setRequiredType(type);
					this.parameterBoxes.add(parameterBox);
					super.addFormComponent(parameterBox, new FormConstraints(index++, 0));
				}
				arguments.setSize(index);
			}
				
			DecafEditor.hotStation.registerAll(this);
			this.container.revalidate();
		}
	}
	
	static class ParameterOfficer extends EnforcementOfficer
	{
		protected Type type;
		
		public ParameterOfficer(Type type)
		{
			this.type = type;
		}
		
		public void call()
		{
			Phrase data = (Phrase)super.data;
			if (data.isEmpty())
			{
				super.violation("Some data is required for this parameter.");
			}
			else
			{
				Type suspect = data.getType();
				if (suspect == null)
				{
					super.violation("The value for this parameter has no type.  Please complete the phrase applied to this parameter.");
				}
				else
				{
					if (this.type.isAssignableFrom(suspect))
					{
						super.noViolation();
					}
					else
					{
						super.violation("I can't use this expression of type " + suspect.getDisplayName() + " for this parameter of type " + this.type.getDisplayName() + ".");
					}
				}
			}
		}
	}
	
	static class NewAccess extends ActionBase 
	{
		public NewAccess()
		{
			super();
			
			super.putValue(Action.ACCELERATOR_KEY, "m");
			super.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_M));
			super.putValue(Action.NAME, "Class Member");
			super.putValue(Action.SHORT_DESCRIPTION, "Call a method");
		}
		
		public void go(ActionEvent event)
		{
			int menuId = ((GrammarMenu.GrammarMenuItem)event.getSource()).getId();
			MaskEditor editor;
			if (menuId == GrammarMenu.SENTENCE_MENU_ID)
			{
				Sentence sentence = ParagraphBox.getCurrentContext();
				editor = new MaskEditor(new Access(sentence));
				editor.assemble();
			}
			else if (menuId == GrammarMenu.PHRASE_MENU_ID)
			{
				Phrase phrase = PhraseBox.getCurrentContext();
				editor = new MaskEditor(new Access(phrase));
				editor.assemble();
			}
			else
			{
				return;
			}
			
			editor.start();
		}
	}
	
	static class Arguments extends Vector
	{
		public boolean applicableTo(Method method)
		{
			if (method.getParameters().size() != this.size())
			{
				return false;
			}
			
			Method.Parameter parameter;
			Iterator parameters;
			Argument argument;
			Iterator arguments;

			parameters = method.getParameters().iterator();
			arguments = this.iterator();
			while (parameters.hasNext())
			{
				parameter = (Method.Parameter)parameters.next();
				argument = (Argument)arguments.next();
				
				if (argument.isEmpty())
				{
					continue;
				}
				
				// eventually ought to allow isAssignableTo() as a secondary match
				if (!parameter.getType().equals(argument.getArgumentType()))
				{
					return false;
				}
			}
			
			return true;
		}
		
		public Argument getArgument(int index)
		{
			return (Argument)this.elementAt(index);
		}
		
		public Argument applyType(CodeNode parent, Type type, int index)
		{
			if (index >= super.size())
			{
				while (index > (size()+1))
				{
					super.add(null);
				}
				Argument add = new Argument(parent, type);
				super.add(add);
				return add;
			}
			else
			{
				Argument found = this.getArgument(index);
				found.setType(type);
				return found;
			}
		}
	}
	
	public static class Argument extends Phrase
	{
		protected Type type;
		
		public Argument(CodeNode parent, Type type)
		{
			super(parent);
			
			this.type = type;
			this.patrol(new ParameterOfficer(type));
		}
		
		public void setType(Type type)
		{
			if (!this.type.equals(type))
			{
				super.content = null;
				this.type = type;
			}
		}
		
		public Type getArgumentType()
		{
			return this.type;
		}
	}
	
	static class Data extends GConstruct implements Grammar.Content, GExpression, GStatement
	{
		protected Phrase accessee;
		protected String name;
		protected Arguments arguments;
		protected Type type;
		protected Collection parameters;
		
		protected Grammar handle;
		
		protected transient StringBuffer buffer;
		
		private Data()	// no parameters please: for deserialization
		{
			super("Access$Data");
			this.buffer = new StringBuffer();
		}
		
		public Data(Grammar handle)
		{
			this();
			
			this.handle = handle;
			
			Grammar.Content content = handle.getContent();
			if (content == null)
			{
				this.accessee = handle.createThisReference();
			}
			else
			{
				this.accessee = new Phrase(handle);
				this.accessee.setData(content);
				
				if (content instanceof GMethodCall)
				{
					this.arguments = (Arguments)((GMethodCall)content).arguments;
				}
				else if (content instanceof GNew)
				{
					this.arguments = (Arguments)((GNew)content).arguments;
				}
				else
				{
					this.arguments = null;
				}
			}
			
			this.parameters = null;
			this.name = null;
			this.type = Utils.noType;
			
			handle.setData(this);
			
		}
		
		public Type getType()
		{
			return this.type;
		}
		
		public MaskEditor edit(Grammar handle)
		{
			MaskEditor editor = new MaskEditor(new Access(handle));
			editor.assemble();
			editor.start();
			
			return editor;
		}
		
		public JComponent getComponent(Grammar handle)
		{
			if (this.arguments == null)
			{
				Utils.RenderPanel panel = new Utils.RenderPanel();
				
				if (!this.accessee.isEmpty())
				{
					panel.add(this.accessee.render());
					panel.add(new Utils.RenderLabel("."));
				}
				
				panel.add(new Utils.RenderLabel(this.name));
				
				return panel;
			}
			else
			{
				Utils.RenderPanel panel = new Utils.RenderPanel();
				
				boolean constructor = this.name.equals("<init>");
				
				if (constructor)
				{
					panel.add(new Utils.RenderLabel("new "));
				}
				
				panel.add(this.accessee.render());
				
				buffer.setLength(0);
				
				if (!constructor)
				{
					buffer.append(".");
					buffer.append(this.name);
				}
				
				buffer.append("(");
				panel.add(new Utils.RenderLabel(buffer.toString()));
				
				Iterator arguments = this.arguments.iterator();
				while (arguments.hasNext())
				{
					panel.add(((Phrase)arguments.next()).render());
					if (arguments.hasNext())
					{
						panel.add(new Utils.RenderLabel(", "));
					}
				}
				panel.add(new Utils.RenderLabel(")"));
				
				return panel;
			}
		}
		
		public at.dms.kjc.JExpression encodeExpression()
		{
			return ((GExpression)this.getSource()).encodeExpression();
		}
		
		public at.dms.kjc.JStatement encodeStatement()
		{
			return ((GStatement)this.getSource()).encodeStatement();
		}
		
		public GConstruct getSource()
		{
			if (this.arguments == null)
			{
				DVariable source = new DVariable(this.accessee == null);
				source.prefix = this.accessee;
				source.setType(this.type);
				source.name = this.name;
				source.description = this.description;
				return source;
			}
			else
			{
				if (this.name.equals("<init>"))
				{
					GNew source = new GNew(this.type.getQualifiedName());
					source.arguments = this.arguments;
					source.description = this.description;
					return source;
				}
				else
				{
					GMethodCall source = new GMethodCall(this.accessee, this.name);
					source.arguments = this.arguments;
					source.description = this.description;
					return source;
				}
			}
		}
		
		public void setHandle(Grammar handle)
		{
			this.handle = handle;
			((Phrase)this.accessee).setParent(handle);
		}
		
		public boolean isEmpty()
		{
			return (this.name == null);
		}

		public String getName()
		{
			return this.name;
		}
		
		public Phrase getRecipient()
		{
			return this.accessee;
		}
		
		public Arguments getArguments()
		{
			return this.arguments;
		}

		public boolean matchesListItem(Object listItem)
		{
			if (listItem instanceof MemberChooser.MethodListItem)
			{
				if (this.arguments == null)
				{
					return false;
				}
				return this.arguments.applicableTo(((MemberChooser.MethodListItem)listItem).data);
			}
			else
			{
				return (this.arguments == null);
			}
		}

		public boolean hasArguments()
		{
			return ((this.arguments != null) && !this.arguments.isEmpty());
		}
		
		public Iterator getParameters()
		{
			if (this.parameters == null)
			{
				return (new Vector()).iterator();
			}
			return this.parameters.iterator();
		}
		
		public void applyListItem(Object listItem)
		{
			if (listItem instanceof MemberChooser.FieldListItem)
			{
				this.arguments = null;
				this.parameters = null;
				Variable variable = ((MemberChooser.FieldListItem)listItem).getData();
				this.name = variable.getName();
				this.type = variable.getType();
			}
			else
			{
				if (this.arguments == null)
				{
					this.arguments = new Arguments();
				}
				Method method = ((MemberChooser.MethodListItem)listItem).getData();
				this.parameters = method.getParameters();
				this.name = method.getName();
				this.type = method.getReturnType();
			}
			
			this.handle.notifyNewData();
		}
		
		public Object readResolve()
			throws java.io.ObjectStreamException
		{
			this.buffer = new StringBuffer();
			return this;
		}
	}
}

