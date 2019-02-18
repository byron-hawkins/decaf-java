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

import java.io.*;
import java.util.*;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;

import javax.swing.*;

import org.hs.jfc.*;
import org.hs.generator.*;
import org.hs.util.file.*;

import com.bitwise.decaf.editor.config.*;
import com.bitwise.decaf.editor.grammar.*;

import com.bitwise.umail.*;

// exits on hide()!
public class ClassEditor extends JFrame 
{
	protected static final String NAMED_TITLE = "Decaf - Class Editor | ";
	protected static final String TITLE = "Decaf - Class Editor";
	protected static final SuperFile CURRENT_WORKING_DIR = new SuperFile("./");
	
	protected static CupFileFilter cupFileFilter;
	
	static
	{
		cupFileFilter = new CupFileFilter();
	}
	
	protected ClassMask mask;
	protected JPanel buttons;
	protected JPanel body;
	
	JButton open;
	JButton save;
	JButton compile;
	
	protected Cup cup;
	GSourceFile sourceFile;
	String packageName;

	protected TypeChooser typeChooser;
	protected BluesReview bluesReview;
	protected Percolator percolator;

	ClassEditor()
	{
		this(new Cup("Unnamed"));
	}
	
	ClassEditor(Cup cup)
	{
		this(cup, null);
	}
	
	ClassEditor(GSourceFile sourceFile)
	{
		this(null, sourceFile);
	}
	
	ClassEditor(Cup cup, GSourceFile sourceFile)
	{
		super();
		
		super.setResizable(false);
		super.setTitle(TITLE);

		this.typeChooser = new TypeChooser(this);
		
		init(cup, sourceFile);
	}
	
	private void init(Cup cup, GSourceFile sourceFile)
	{
		try
		{
			this.sourceFile = sourceFile;
	
			if (cup == null)
			{
				GClass source = (GClass)sourceFile.typeDeclarations.firstElement();
				if (source instanceof Cup)
				{
					this.cup = (Cup)source;
				}
				else
				{
					this.cup = new Cup(source);
				}
			}
			else
			{
				this.cup = cup;
			}
			this.packageName = DecafEditor.getPluginsPackage();
			
			DecafEditor.percolator = this.percolator = new Percolator(this);
			DecafEditor.bluesReview = this.bluesReview = new BluesReview();
		}
		catch (Exception e)
		{
			DecafEditor.log(e);
		}
	}
/*	
	private static Cup createDefaultCup()
	{
		Cup cup = ;
		
		DMethod method;
		InitialMethod methodConfig;
		while (methods.hasNext())
		{
			try
			{
				methodConfig = (InitialMethod)methods.next();
				method = new DMethod(cup);
				methodConfig.apply(method);
				cup.methods.add(method);
			}
			catch (ClassNotFoundException e)
			{
				System.err.println("ClassEditor.createDefaultCup(): parameter or return type was not found -- skipping");
			}
		}
		
		return cup;
	}
*/	
	public void addHook(jHandle handle)
	{
		try
		{
			this.cup.addHook(handle);
		}
		catch (ClassNotFoundException e)
		{
			DecafEditor.log("ClassEditor.addHook(): Attempt to add a hook to method " + handle + " failed with " + e.getClass().getName() + ": message[" + e.getMessage() + "]");
		}
	}

	public void assemble()
	{	
		this.body = new JPanel();
		this.body.setLayout(new BoxLayout(this.body, BoxLayout.Y_AXIS));
		
		this.body.add(this.mask = new ClassMask());
		this.mask.assemble();
		this.mask.display(this.cup);
		this.mask.addNameListener(new NameListener());
		
		this.buttons = new JPanel();
		this.buttons.add(this.open = new JButton(new Open()));
		this.buttons.add(this.save = new JButton(new Save()));
		this.buttons.add(this.compile = new JButton(new Compile()));
		
		this.body.add(this.buttons);
		
		super.getContentPane().add(this.body);

		this.typeChooser.assemble(null); //, this.cup);
		this.percolator.assemble(null);
		this.bluesReview.assemble();
	}
	
	public void start()
	{
		super.getContentPane().add("Center", this.body);

		DecafEditor.hotStation.registerAll(this);
		DecafEditor.registerWindow(this);

		super.pack();
		DecafEditor.realEstateManager.place(Utils.RealEstateManager.CENTER, this);
		super.setVisible(true);
		
		this.typeChooser.start();
		this.percolator.start();
		this.bluesReview.start();
		
		//testSetCursor();
	}

	Percolator getPercolator()
	{
		return this.percolator;
	}
	
	TypeChooser getTypeChooser()
	{
		return this.typeChooser;
	}
	
	BluesReview getBluesReview()
	{
		return this.bluesReview;
	}

	public void nameChanged(String newName)
	{
		newName = newName.trim();
		
		if (newName.length() > 0)
		{
			super.setTitle(NAMED_TITLE + newName);
		}
		else
		{
			super.setTitle(TITLE);
		}
	}
	
	class NameListener implements KeyListener
	{
		public void keyPressed(KeyEvent event)
		{
		}
		
		public void keyReleased(KeyEvent event)
		{
			nameChanged(ClassEditor.this.mask.getName());
		}
		
		public void keyTyped(KeyEvent event)
		{
		}
	}

	protected void buildSourceFile()
	{
		this.mask.apply(this.cup);
		
		if (this.sourceFile == null)
		{
			this.sourceFile = new GSourceFile(this.cup.name, this.packageName.replace('.','/'));
		}
		else
		{
			this.sourceFile.classname = this.cup.name;
			this.sourceFile.packageName = this.packageName.replace('.','/');
		}

		this.sourceFile.typeDeclarations.clear();
		this.sourceFile.typeDeclarations.add(this.cup);
	}	
	
	protected void saveJava()
	{
		buildSourceFile();
		
		try
		{
			this.sourceFile.print();
			
			StringWriter code = new StringWriter();
			this.sourceFile.print(new BufferedWriter(code));
			new CodeWindow(code.toString());
		}
		catch (Exception e)
		{
			DecafEditor.log(e);
		}
	}
	
	protected void compile()
	{
		try
		{
			//System.err.println("Compiling source file " + this.sourceFile.classname);
			String error = this.sourceFile.compile();
			
			if (error != null)
			{
				new DecafEditor.Alert("Error in compilation", "Uh oh, this file hasn't compiled correctly.  Here is what the compiler told me: \n\n" + error + "\n\nIn a later version of Decaf, I'll locate the error in your plugin and give you detailed instructions about what is wrong, how you might fix it, and how to prevent similar errors from happening in the future.  For the prototype, I've unfortunately been required to punt on this particular topic.  Sorry.");
			}
		}
		catch (Exception e)
		{
			DecafEditor.log(e);
		}
	}
	
	protected void repair()
	{
		CodeNode blue;
		Iterator blues = EnforcementOfficer.blues.iterator();
		while (blues.hasNext())
		{
			blue = (CodeNode)blues.next();
			blue.editPathToCup();
			break;
		}
	}
	
	protected void saveCup()
	{
		buildSourceFile();
		
		try
		{
			SuperFile file = DecafEditor.getPluginsDirectory().extend(this.sourceFile.classname + ".cup");
			CupFile cupFile = new CupFile(file.getAbsolutePath(), this.sourceFile, this.bluesReview, this.percolator.getBrew());
			cupFile.save();
			
			/*
			JFileChooser chooser = new JFileChooser(DecafEditor.getPluginsDirectory());
			chooser.setFileFilter(cupFileFilter);
			chooser.setSelectedFile(new SuperFile(this.sourceFile.classname + ".cup"));
			if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
			{
				SuperFile selected = new SuperFile(chooser.getSelectedFile());
				CupFile cupFile = new CupFile(selected.getAbsolutePath(), this.sourceFile, this.typeChooser.getRoot(), this.percolator.getBrew());
				cupFile.save();
			}
			*/
		}
		catch (Exception e)
		{
			DecafEditor.log(e);
		}
	}
	
	protected void openFile()
	{
		try
		{
			SaveFirst check = new SaveFirst();
			if (check.cancel())
			{
				return;
			}
			
			//this.typeChooser.dispose();
			this.percolator.dispose();
			this.bluesReview.dispose();
			
			JFileChooser chooser = new JFileChooser(DecafEditor.getPluginsDirectory());
			chooser.setFileFilter(cupFileFilter);
			if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
			{
				SuperFile selected = new SuperFile(chooser.getSelectedFile());
				this.open(selected);
			}
		}
		catch (Exception e)
		{
			DecafEditor.log(e);
		}
	}

	public void open(SuperFile file)
	{
		try
		{
			String extension = file.getExtension();
			if (extension.equals("java"))
			{
				this.init(null, GSourceFile.decode(file));
			}
			else if (extension.equals("cup"))
			{
				CupFile cupFile = new CupFile(file.getAbsolutePath());
				try
				{
					cupFile.open();
					this.init(null, cupFile.getSourceFile());	// sets this.cup... kinda obtuse
					//this.typeChooser.assemble((TypeChooser.CategoryNode)cupFile.getTypeRoot(), this.cup);
					this.percolator.assemble((Percolator.Brew)cupFile.getPercolatorBrew());
					this.bluesReview.assemble();
				}
				catch (FileNotFoundException e)
				{
					this.cup.name = file.getBaseName();
				}
				catch (CupFile.DirtyCupException e)
				{
					this.cup.name = file.getBaseName();
				}
			}
			else
			{
				DecafEditor.log("ClassEditor.open(): Attempt to open an unrecognized file: " + file.getAbsolutePath() + " -- ignoring.");
				return;
			}
			
			this.mask.display(this.cup);
			//this.typeChooser.start();
			this.percolator.start();
			this.bluesReview.start();
		}
		catch (Exception e)
		{
			DecafEditor.log(e);
		}
	}
	
	protected String getFilename()
	{
		int dot = this.sourceFile.classname.indexOf(".");
		if (dot >= 0)
		{
			return this.sourceFile.classname.substring(dot, this.sourceFile.classname.length());
		}
		return (this.sourceFile.classname + ".cup");
	}

	class Open extends ActionBase
	{
		public Open()
		{
			super();
			
			super.putValue(Action.ACCELERATOR_KEY, "o");
			super.putValue(Action.LONG_DESCRIPTION, "+++++++++++");
			super.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
			super.putValue(Action.SHORT_DESCRIPTION, "Open a new class");
			super.putValue(Action.NAME, "Open");
		}
		
		public void go(ActionEvent event)
		{
			ClassEditor.this.openFile();
		}
	} 

	class Save extends ActionBase
	{
		public Save()
		{
			super();
			
			super.putValue(Action.ACCELERATOR_KEY, "s");
			super.putValue(Action.LONG_DESCRIPTION, "Generates a .java file in the current directory with the same name as the classname, encoding all declarations as specified in the visual controls, and inserting the user's method body code.");
			super.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_S));
			super.putValue(Action.SHORT_DESCRIPTION, "Save the current class");
			super.putValue(Action.NAME, "Save");
		}
		
		public void go(ActionEvent event)
		{
			ClassEditor.this.saveCup();
		}
	} 

	class Compile extends ActionBase implements Utils.List.Listener
	{
		public Compile()
		{
			super();
			
			super.putValue(Action.ACCELERATOR_KEY, "c");
			super.putValue(Action.LONG_DESCRIPTION, "+++++++++++");
			super.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_C));
			super.putValue(Action.SHORT_DESCRIPTION, "Compile the current class");
			super.putValue(Action.NAME, "Compile");
			
			EnforcementOfficer.blues.addListener(this);
		}
		
		public void report(Utils.List.Event event)
		{
			if (true) //(Grammar.blues().isEmpty())
			{
				super.putValue(Action.ACCELERATOR_KEY, "c");
				super.putValue(Action.NAME, "Compile");
				super.putValue(Action.SHORT_DESCRIPTION, "Compile the current class");
			}
			else
			{
				super.putValue(Action.ACCELERATOR_KEY, "r");
				super.putValue(Action.NAME, "Repair");
				super.putValue(Action.SHORT_DESCRIPTION, "Repair grammars in the current class");
			}
		}
		
		public void go(ActionEvent event)
		{
			if (true) //Grammar.blues().isEmpty())
			{
				ClassEditor.this.saveJava();
				ClassEditor.this.compile();
			}
			else
			{
				ClassEditor.this.repair();
			}
		}
	} 

	class New extends ActionBase
	{
		public New()
		{
			super();
			
			super.putValue(Action.ACCELERATOR_KEY, "n");
			super.putValue(Action.LONG_DESCRIPTION, "+++++++++++");
			super.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
			super.putValue(Action.SHORT_DESCRIPTION, "Create a new cup of Decaf");
			super.putValue(Action.NAME, "New");
		}
		
		public void go(ActionEvent event)
		{
		}
	} 

	class Yes extends ActionBase
	{
		protected JDialog dialog;
		
		public Yes(JDialog dialog)
		{
			super();
			
			this.dialog = dialog;
			
			super.putValue(Action.ACCELERATOR_KEY, "y");
			super.putValue(Action.LONG_DESCRIPTION, "+++++++++++");
			super.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_Y));
			super.putValue(Action.SHORT_DESCRIPTION, "Save the most recent edition");
			super.putValue(Action.NAME, "Yes");
		}
		
		public void go(ActionEvent event)
		{
			ClassEditor.this.saveCup();
			this.dialog.dispose();
		}
	} 

	class No extends ActionBase
	{
		protected JDialog dialog;
		
		public No(JDialog dialog)
		{
			super();
			
			this.dialog = dialog;
			
			super.putValue(Action.ACCELERATOR_KEY, "n");
			super.putValue(Action.LONG_DESCRIPTION, "+++++++++++");
			super.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
			super.putValue(Action.SHORT_DESCRIPTION, "Discard the most recent edition");
			super.putValue(Action.NAME, "No");
		}
		
		public void go(ActionEvent event)
		{
			this.dialog.dispose();
		}
	} 
	
	class Cancel extends ActionBase
	{
		protected SaveFirst saveFirst;
		
		public Cancel(SaveFirst saveFirst)
		{
			super();
			
			this.saveFirst = saveFirst;
			
			super.putValue(Action.ACCELERATOR_KEY, "c");
			super.putValue(Action.LONG_DESCRIPTION, "+++++++++++");
			super.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_C));
			super.putValue(Action.SHORT_DESCRIPTION, "Never mind, don't close this file.");
			super.putValue(Action.NAME, "Cancel");
		}
		
		public void go(ActionEvent event)
		{
			this.saveFirst.cancelled = true;
			this.saveFirst.dispose();
		}
	} 
	
	static class CupFileFilter extends javax.swing.filechooser.FileFilter
	{
		public boolean accept(File f)
		{
			SuperFile file = new SuperFile(f);
			return file.getExtension().equals("cup");
		}
		
		public String getDescription()
		{
			return "*.cup (Cup Files)";
		}
	}
	
	public Object writeReplace()
		throws java.io.ObjectStreamException
	{
		throw (new Utils.SerialException(getClass()));
	}
	
	class SaveFirst extends JDialog
	{
		protected boolean cancelled;
		
		public SaveFirst()
		{
			super();
			
			this.cancelled = false;
			
			super.setModal(true);
			super.setTitle("Save before closing?");
			super.setLocationRelativeTo(null);
			
			JLabel message = new JLabel("Save " + ClassEditor.this.mask.name.getText() + " before closing?");
			JPanel messagePanel = new JPanel();
			messagePanel.add(message);
			
			JPanel buttonPanel = new JPanel();
			buttonPanel.add(new JButton(new Yes(this)));
			buttonPanel.add(new JButton(new No(this)));
			buttonPanel.add(new JButton(new Cancel(this)));
			
			JPanel mainPanel = new JPanel();
			mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
			mainPanel.add(messagePanel);
			mainPanel.add(buttonPanel);

			super.getContentPane().add(mainPanel);
			super.pack();
			super.show();
		}
		
		public boolean cancel()
		{
			return this.cancelled;
		}
	}
	
	static class CodeWindow extends JDialog
	{
		public CodeWindow(String code)
		{
			super();
			
			super.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			super.setLocationRelativeTo(null);
			
			JTextArea codeArea = new JTextArea(20, 30);
			codeArea.setText(code);
			JScrollPane codePane = new JScrollPane(codeArea);
			JPanel mainPanel = new JPanel();
			mainPanel.add(codePane);
			super.getContentPane().add(mainPanel);
			
			super.pack();
			super.show();
		}
	}
}