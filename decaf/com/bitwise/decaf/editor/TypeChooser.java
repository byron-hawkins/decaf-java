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
import javax.swing.event.*;
import javax.swing.tree.*;

import org.hs.jfc.*;
import org.hs.util.*;
import org.hs.generator.*;

import com.bitwise.decaf.editor.config.TypeDetails;
import com.bitwise.decaf.editor.config.TypeItem;
import com.bitwise.decaf.editor.config.TypeChooserConfig;
import com.bitwise.decaf.editor.config.TypeCategoryConfig;

import com.bitwise.decaf.editor.grammar.*;

public class TypeChooser extends JDialog
{
	static final long serialVersionUID = -5040046403713988670L;

	protected static final String TITLE = "Decaf - Type Tree";
	
	protected static TypeChooserConfig config;
	
	public static void setConfig(TypeChooserConfig config)
	{
		TypeChooser.config = config;
	}
	
	protected JPanel body;
	protected TypeTree tree;
	protected Details details;

	TypeChooser(Frame owner)	
	{
		super(owner);
		
		super.setTitle(TITLE);
	}
	
	public void assemble(CategoryNode typeRoot)//, Cup cup)
	{
		if (typeRoot == null)
		{
			typeRoot = createRoot();
		}

/*
		CategoryNode local = new CategoryNode("Local");
		//typeRoot.add(local);	// too complex for now
		local.add(new TypeNode(cup));
*/
		this.details = new Details();
		this.tree = new TypeTree(typeRoot);

		this.body = new JPanel();
		this.body.setLayout(new BoxLayout(this.body, BoxLayout.Y_AXIS));
		this.body.add(new JScrollPane(this.tree));
		this.body.add(new JScrollPane(this.details));
		
		// apply local files (cups)
		// add user's binary classes
		// also user's extended JDK types
	}
	
	public void start()
	{
		this.tree.expandTopCategories();
		
		DecafEditor.hotStation.register((HotComponent)this.tree);
		DecafEditor.registerWindow(this);

		super.getContentPane().add("Center", this.body);
		super.pack();

		DecafEditor.realEstateManager.place(Utils.RealEstateManager.LOWER_LEFT, this);
	}
	
	public void display()
	{
		super.show();
		super.toFront();
	}
	
	public TreeNode getRoot()
	{
		return (TreeNode)this.tree.getModel().getRoot();
	}
	
	class TypeTree extends JTree implements HotComponent, MouseListener
	{
		protected DefaultTreeCellRenderer renderer;
		protected DefaultTreeModel model;
		protected Color coolColor;
		protected Color hotColor;

		public TypeTree()
		{
			this(createRoot());
		}
		
		public TypeTree(TreeNode root)
		{
			super(root);
			
			super.setCellRenderer(this.renderer = new TypeNodeRenderer());
			super.setModel(this.model = new DefaultTreeModel(root));
			this.coolColor = null;
			super.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			super.addMouseListener(this);
			super.addTreeSelectionListener(TypeChooser.this.details);
			super.setToggleClickCount(14);
		}

		protected void expandTopCategories()
		{		
			TreeNode next;
			TreeNode root = (TreeNode)this.model.getRoot();
			Enumeration topCategories = root.children();
			while (topCategories.hasMoreElements())
			{
				next = (TreeNode)topCategories.nextElement();
				if (!next.isLeaf())
				{
					super.expandPath(new TreePath(new TreeNode[] {root, next}));
				}
			}
		}

		public void register(HotComponent.Arbiter arbiter)
		{
			arbiter.offer(Typed.class, LINK);
			arbiter.offer(GExpression.class, COPY);
		}
		
		public boolean isCompatible(Object hot)
		{
			return false;
		}
		
		public void take(Object hot)
		{
		}
		
		public Object give(Class type, int operation)
		{
			return getSelectionPath().getLastPathComponent();	// a TypeNode, as a Grammar.Content
		}
		
		public boolean pick(boolean b)
		{
			if (getSelectionPath().getLastPathComponent() instanceof TypeNode)
			{
				if (b)
				{
					if (this.coolColor == null)
					{
						this.coolColor = this.renderer.getBackgroundSelectionColor();
					}
					this.renderer.setBackgroundSelectionColor(this.hotColor);
				}
				else
				{
					this.renderer.setBackgroundSelectionColor(this.coolColor);
				}
				super.repaint();
			
				return true;
			}
			else
			{
				this.renderer.setBackgroundSelectionColor(this.coolColor);
				super.repaint();
				return false;
			}
		}
		
		public void setHotColor(Color hot)
		{
			this.hotColor = hot;
		}

		public void mouseEntered(MouseEvent e)
		{
		}
		
		public void mouseExited(MouseEvent e)
		{
		}
		
		public void mousePressed(MouseEvent e)
		{
			if (SwingUtilities.isRightMouseButton(e))
			{
				Node clicked = (Node)this.getPathForLocation(e.getX(), e.getY()).getLastPathComponent();
				clicked.popMenu();
			}
			else if (SwingUtilities.isLeftMouseButton(e) && (e.getClickCount() == 2))
			{
				TreePath path = this.getPathForLocation(e.getX(), e.getY());
				if (path == null)
				{
					return;
				}
				Node clicked = (Node)path.getLastPathComponent();
				String javadocUrl = clicked.getJavadocUrl();
				if (javadocUrl != null)
				{
					DecafEditor.browser.display(javadocUrl);
				}
			}
		}
		
		public void mouseReleased(MouseEvent e)
		{
		}
		
		public void mouseClicked(MouseEvent e)
		{
		}
	
		public Object writeReplace()
			throws java.io.ObjectStreamException
		{
			throw (new Utils.SerialException(getClass()));
		}
	}
	
	static class TypeNodeRenderer extends DefaultTreeCellRenderer
	{
		public Component getTreeCellRendererComponent(
			JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
		{
			try
			{
				JComponent render = (JComponent)super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
				if (value instanceof CategoryNode)
				{
					render.setFont(render.getFont().deriveFont(Font.BOLD));
				}
				else
				{
					render.setFont(render.getFont().deriveFont(Font.PLAIN));
					// should apply current L&F
					((JLabel)render).setIcon(new javax.swing.plaf.metal.MetalIconFactory.TreeLeafIcon());
				}
				
				return render;
			}
			catch (ClassCastException e)
			{
				return super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
			}
		}
	}

	CategoryNode createRoot()
	{
		CategoryNode root = new CategoryNode();
		
		root.add(new TypeNode(Utils.nullType));
		
		if (config == null)
		{
			CategoryNode basic = new CategoryNode("Basic");
			root.add(basic);
			
			basic.add(new TypeNode(Utils.getType(Object.class)));
			basic.add(new TypeNode(Utils.getType(String.class)));
			basic.add(new TypeNode(Utils.getType(Vector.class)));
			basic.add(new TypeNode(Utils.getType(Collection.class)));
			basic.add(new TypeNode(Utils.getType(int.class)));
			basic.add(new TypeNode(Utils.getType(boolean.class)));
		}
		else
		{
			TypeCategoryConfig categoryConfig;
			CategoryNode category;
			Iterator types;
			Iterator categories = config.getTypeCategories().iterator();
			TypeDetails type = null;
			while (categories.hasNext())
			{
				categoryConfig = (TypeCategoryConfig)categories.next();
				category = new CategoryNode(categoryConfig.getName());
				types = categoryConfig.getTypes().iterator();
				while (types.hasNext())
				{
					try
					{
						type = (TypeDetails)((TypeItem)types.next()).getUid();
						category.add(new TypeNode(Utils.getType(type.getClassname())));
					}
					catch (ClassNotFoundException e)
					{
						DecafEditor.log("TypeChooser.createRoot(): Can't find class " + type.getClassname() + " -- skipping.");
					}
				}
				root.add(category);
			}
		}
		
		return root;
	}

	static abstract class Node implements TreeNode, Decaf
	{
		static final long serialVersionUID = 5466927670846102188L;

		protected Vector children;
		protected Node parent;
		protected String javadocUrl;
		
		protected static StringBuffer buffer = new StringBuffer();
		
		public Node()
		{
			this.children = new Vector();
			this.javadocUrl = null;
		}
		
		protected Node(Node parent)
		{
			this();
			this.parent = parent;
		}
		
		public void add(Node child)
		{
			child.parent = this;
			this.children.add(child);
		}
		
		public void remove()
		{
			this.parent.children.remove(this);
			// caller: TypeChooser.this.tree.model.nodeStructureChanged(this.parent);
		}
		
		public Enumeration children()
		{
			return this.children.elements();
		}
		
		public boolean getAllowsChildren()
		{
			return (!isLeaf());
		}
		
		public TreeNode getChildAt(int index)
		{
			return (TreeNode)this.children.elementAt(index);
		}
		
		public int getChildCount()
		{
			return this.children.size();
		}
		
		public int getIndex(TreeNode node)
		{
			return this.children.indexOf(node);
		}
		
		public TreeNode getParent()
		{
			return this.parent;
		}
		
		public String getJavadocUrl()
		{
			return this.javadocUrl;
		}

		public abstract void popMenu();
	}

	static class CategoryNode extends Node
	{
		protected String name;

		public CategoryNode()
		{
			super();
			this.name = "";
		}

		public CategoryNode(String name)
		{
			super();
			this.name = name;
		}
		
		public boolean isLeaf()
		{
			return false;
		}
		
		public void popMenu()
		{
			
		}
		
		public String toString()
		{
			return this.name;
		}
	}

	static class TypeNode extends Node implements Grammar.Content, GExpression, Configurable, Grammar.ClassReference
	{
		protected Type content;
		protected Vector children;
		
		public TypeNode(Type content)
		{
			super();
			
			this.content = content;
			
			if (!this.content.isPrimitive())
			{
				super.javadocUrl = this.content.getQualifiedName().replace('.','/') + ".html";
				
				Iterator fields = this.content.getFields().iterator();
				while (fields.hasNext())
				{
					super.children.add(new VariableNode((Variable)fields.next(), super.javadocUrl));
				}
				Iterator methods = this.content.getConstructors().iterator();
				while (methods.hasNext())
				{
					super.children.add(new MethodNode((Method)methods.next(), super.javadocUrl));
				}
				methods = this.content.getMethods().iterator();
				while (methods.hasNext())
				{
					super.children.add(new MethodNode((Method)methods.next(), super.javadocUrl));
				}
			}
		}
		
		public String getDiscussion()
		{
			return this.content.getDiscussion();
		}

		public boolean isLeaf()
		{
			return super.children.isEmpty();
		}
		
		public Copy deepCopy()
		{
			return this;
		}
		
		public Copy shallowCopy()
		{
			return this;
		}
		
		public void popMenu()
		{
			Discussion discussion = new Discussion(this.content.getDiscussion());
			if (discussion != null)
			{
				discussion.assemble();
				discussion.displayFor(DecafEditor.classEditor);
			}
			// display some info in a popup window about the type
			// link to javadoc html
			// -- first, genericize the name of this popMenu() method
		}
		
		public JComponent getComponent(Grammar handle)
		{
			return (new Utils.RenderLabel(this.content.getDisplayName()));
		}
			
		public MaskEditor edit(Grammar handle)
		{
			return null;
		}
		
		public void setHandle(Grammar handle)
		{
			DecafEditor.log("TypeChooser.setHandle(): Attempting to setHandle on a " + getClass().getName() + "!");
		}
		
		public Object get(java.lang.reflect.Field field)
		{
			if (this.content instanceof GType)
			{
				return ((GConstruct)this.content).get(field);
			}
			return null;
		}
		
		public void set(java.lang.reflect.Field field, Object value)
		{
			if (this.content instanceof GType)
			{
				((GConstruct)this.content).set(field, value);
			}
		}
		
		public at.dms.kjc.JExpression encodeExpression()
		{
			if (this.content instanceof GType)
			{
				return ((GType)this.content).encodeExpression();
			}
			return null;
		}
		
		public GConstruct getSource()
		{
			if (this.content instanceof GType)
			{
				return (GType)this.content;
			}
			else
			{
				DecafEditor.log(getClass().getName() + ".getSource(): content is a " + this.content.getClass().getName() + "; returning null");
				return null;
			}
		}
		
		public Type getType()
		{
			return this.content;
		}

		public String toString()
		{
			return this.content.getDisplayName();
		}
	}
	
	static class MethodNode extends Node implements Configurable
	{
		protected Method content;
		
		protected Vector children;
		
		public MethodNode(Method content, String javadocBaseUrl)
		{
			super();
			
			this.content = content;
			
			buffer.setLength(0);
			buffer.append(javadocBaseUrl);
			buffer.append("#");
			
			if (this.content.isConstructor())
			{
				buffer.append(this.content.getReturnType().getName());
			}
			else
			{
				buffer.append(this.content.getName());
			}
			
			buffer.append("(");
			
			Method.Parameter parameter;
			Iterator parameters = this.content.getParameters().iterator();
			while (parameters.hasNext())
			{
				parameter = (Method.Parameter)parameters.next();
				buffer.append(parameter.getType().getQualifiedName());
				if (parameters.hasNext())
				{
					buffer.append(", ");
				}
			}
			
			buffer.append(")");
			
			super.javadocUrl = buffer.toString();
			
			parameters = this.content.getParameters().iterator();
			while (parameters.hasNext())
			{
				parameter = (Method.Parameter)parameters.next();
				super.children.add(new VariableNode(parameter, super.javadocUrl));
			}
		}
		
		public boolean isLeaf()
		{
			return super.children.isEmpty();
		}
		
		public void popMenu()
		{
		}
	
		public String getDiscussion()
		{
			return this.content.getDiscussion();
		}

		public String toString()
		{
			return Method.renderer.describe(this.content);
		}
	}
	
	static class VariableNode extends Node implements Configurable
	{
		protected Variable content;
		
		public VariableNode(Variable content, String javadocBaseUrl)
		{
			super();
			
			this.content = content;
			
			if (this.content instanceof Method.Parameter)
			{
				super.javadocUrl = javadocBaseUrl;
			}
			else
			{
				buffer.setLength(0);
				buffer.append(javadocBaseUrl);
				buffer.append("#");
				buffer.append(this.content.getName());
				
				super.javadocUrl = buffer.toString();
			}
		}
		
		public boolean isLeaf()
		{
			return true;
		}
		
		public void popMenu()
		{
		}
	
		public String getDiscussion()
		{
			return ((Configurable)this.content).getDiscussion();
		}
		
		public String toString()
		{
			buffer.setLength(0);
			
			if (this.content instanceof GVariable)
			{
				buffer.append(this.content.getType().getDisplayName());
				buffer.append(" ");
			}
			buffer.append(this.content.getName());
			
			return buffer.toString();
		}
	}
	
	static class Details extends JTextArea implements TreeSelectionListener
	{
		public Details()
		{
			super(5, 30);
			super.setEditable(false);
			super.setLineWrap(true);
			super.setWrapStyleWord(true);
		}
		
		public void valueChanged(TreeSelectionEvent event)
		{
			Node node = (Node)event.getPath().getLastPathComponent();
			
			if (node instanceof Configurable)
			{
				super.setText(((Configurable)node).getDiscussion());
			}
		}
	
		public Object writeReplace()
			throws java.io.ObjectStreamException
		{
			throw (new Utils.SerialException(getClass()));
		}
	}
	
	public Object writeReplace()
		throws java.io.ObjectStreamException
	{
		throw (new Utils.SerialException(getClass()));
	}
}
