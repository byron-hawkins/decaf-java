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
import java.awt.event.*;

import javax.swing.*;
import javax.swing.text.*;

import com.bitwise.decaf.editor.config.*;
import com.bitwise.decaf.editor.grammar.*;

import org.hs.jfc.*;
import org.hs.util.file.*;
import org.hs.generator.*;

import com.bitwise.umail.*;

public class DecafEditor
{
	public static int LIST_CELL_WIDTH = 300;
	public static Color selectedColor = new Color(0x55FF66);

	protected static SuperFile configFile;
	protected static SuperFile pluginsDirectory;
	protected static DecafConfig config;
	protected static SuperFile cursorPath;
	protected static SuperFile javadocRoot;
	protected static Color hotColor = Color.red;
	protected static Color warmColor = Color.pink;
	protected static MouseMotionFocus mouseMotionFocus = new MouseMotionFocus();
	protected static ZOrder zOrder = new ZOrder();
	protected static TypeCatalog typeCatalog = null;
	protected static boolean nativeOn;
	protected static StringWriter exceptionContent;
	protected static PrintWriter exceptionOutputHandle;
	protected static DecafLog log;
	
	public static Browser browser;
	public static Utils.RealEstateManager realEstateManager;
	public static HotStation hotStation;
	public static Percolator percolator;
	public static BluesReview bluesReview;
	public static ClassEditor classEditor;
	public static Frame frame;
	
	public static void main(String[] args)
	{
		GSourceFile.setClasspath(System.getProperty("java.class.path"));
		
		assemble();

		nativeOn = false;

		classEditor.setDefaultCloseOperation(ClassEditor.EXIT_ON_CLOSE);
		classEditor.start();
	}
	
	public static void assemble()
	{
		try
		{
			log = new DecafLog("DecafEditor.log");
			
			DecafEditor.log("Starting Decaf\n\n");
			
			GSourceFile.init(log.getPrintWriter());
			Utils.init();

			configFile = new SuperFile("./config.xml");
			config = (DecafConfig)DecafConfig.unmarshal(configFile.getInputStream());
			config.initConstraints();
			
			GSourceFile.setCompileCommand(config.getInstallation().getCompileCommand());
			GSourceFile.setLog(log);

			GSourceFile.setCurrentDir(config.getInstallRoot().extend("decaf"));
			typeCatalog = config.getTypeCatalog();
			
			pluginsDirectory = config.getInstallRoot().extend("decaf").extend(config.getInstallation().getPluginsDirectory());
			GSourceFile.setWorkingDir(pluginsDirectory.getAbsolutePath());
			GSourceFile.init(log.getPrintWriter());
			
			browser = new Browser(config.getInstallation().getBrowserCommand());
			
			cursorPath = config.getInstallRoot().extend("decaf/lib/image/cursors");
			javadocRoot = config.getInstallRoot().extend("doc/api");

			realEstateManager = new Cascade();
			
			TreeMap hotProps = new TreeMap();
			hotProps.put(HotStation.HOT_COLOR, hotColor);
			hotProps.put(HotStation.WARM_COLOR, warmColor);
			hotProps.put(HotStation.BLANK_COLOR, new Color(0xc0c0c0));
			hotProps.put(HotStation.SWAP_COLOR, new Color(0xffffff));
			hotProps.put(HotStation.PLAIN_CURSOR_FILE, cursorPath.extend("plain.gif"));
			hotProps.put(HotStation.MOVE_CURSOR_FILE, cursorPath.extend("move.gif"));
			hotProps.put(HotStation.COPY_CURSOR_FILE, cursorPath.extend("copy.gif"));
			hotProps.put(HotStation.LINK_CURSOR_FILE, cursorPath.extend("link.gif"));
			hotStation = new HotStation(hotProps);
			
			ClassMask.config = config.getClassConfig();
			MethodMask.config = config.getMethodConfig();
			VariableMask.add(config.getParameterConfig());
			VariableMask.add(config.getFieldConfig());
			VariableMask.add(config.getLocalConfig());
			
			GrammarConfig grammars = config.getGrammarConfig();
			PhraseBox.initMenu(grammars);
			ParagraphBox.initMenu(grammars);
			
			TypeChooser.setConfig(config.getTypeChooserConfig());
			
			//ClassEditor.setDefaultMethods(config.getDefaultMethods());
			classEditor = new ClassEditor();
			frame = classEditor;

			percolator = classEditor.getPercolator();
			//bluesReview = new BluesReview();
			//bluesReview.assemble();
			//bluesReview.start();

			classEditor.assemble();
			//editor.testSetCursor();
			
			com.Ostermiller.util.Browser.init();
		}
		catch (Exception e)
		{
			log(e);
			System.exit(0);
		}
	}
	
	public static void go(jHandle handle)
	{
		try
		{
			GSourceFile.setClasspath(jPluginLib.getClasspath());
			
			assemble();
			
			nativeOn = true;

			exceptionContent = new StringWriter();
			exceptionOutputHandle = new PrintWriter(exceptionContent);

			DecafEditor.log("DecafEditor.go(jHandle): getPluginsDirectory(): " + getPluginsDirectory() + "; handle: " + handle);
			SuperFile pluginFile = getPluginsDirectory().extend(handle.getPluginName() + ".cup");
			classEditor.open(pluginFile);
			classEditor.addHook(handle);
			classEditor.setDefaultCloseOperation(ClassEditor.HIDE_ON_CLOSE);
			classEditor.start();
		}
		catch (Exception e)
		{
			log(e);
		}
	}
	
	public static void log(String message)
	{
		log.log(message);
	}
	
	public static void log(Exception e)
	{
		log.log(e);
	}
	
	public static String getPluginsPackage()
	{
		return config.getInstallation().getPluginsPackage();
	}
	
	public static SuperFile getPluginsDirectory()
	{
		return pluginsDirectory;
	}
	
	public static void registerWindow(Window window)
	{
		JRootPane rootPane;
		GlassPane glassPane = new GlassPane();
		if (window instanceof JDialog)
		{
			rootPane = ((JDialog)window).getRootPane();
			((JDialog)window).setGlassPane(glassPane);
		}
		else if (window instanceof JWindow)
		{
			rootPane = ((JWindow)window).getRootPane();
			((JWindow)window).setGlassPane(glassPane);
		}
		else if (window instanceof JFrame)
		{
			rootPane = ((JFrame)window).getRootPane();
			((JFrame)window).setGlassPane(glassPane);
		}
		else
		{
			DecafEditor.log("DecafEditor.registerWindow(): I can only register keystrokes on windows that use a JRootPane.  This " + window.getClass().getName() + " will not respond to the standard keystrokes.");
			return;
		}
		//glassPane.setVisible(true);
		
		InputMap input = rootPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		ActionMap action = rootPane.getActionMap();
		
		input.put(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_MASK, true), "BluesReview");
		action.put("BluesReview", new ReviewBlues());
		
		input.put(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK, true), "Percolator");
		action.put("Percolator", new Percolate());
		
		input.put(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_MASK, true), "TypeChooser");
		action.put("TypeChooser", new ChooseType());
		
		input.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK, false), "zOrderForward");
		action.put("zOrderForward", zOrder.forwardAction());
		
		input.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK, false), "zOrderReverse");
		action.put("zOrderReverse", zOrder.reverseAction());
		
		zOrder.register(window);
	}
	
	public static SuperFile getJavadocRoot()
	{
		return javadocRoot;
	}
	
	public static TypeDetails getTypeDetails(String classname)
	{
		if (typeCatalog == null)
		{
			return null;
		}
		
		return typeCatalog.getTypeDetails(classname);
	}
	
	static class GlassPane extends JComponent 
	{
		public GlassPane()
		{
			super();
			
			super.addMouseListener(new MouseMotionFocus());
		}

	
		public Object writeReplace()
			throws java.io.ObjectStreamException
		{
			throw (new Utils.SerialException(getClass()));
		}
	}
	
	static class MouseMotionFocus extends MouseAdapter 
	{
		public void mouseEntered(MouseEvent event)
		{
			if (event.getModifiers() == InputEvent.CTRL_MASK)
			{
				DecafEditor.log(getClass().getName() + ".mouseEntered(): " + event.getSource());
				
				//((JLayeredPane)event.getSource()).toFront();
			}
		}
	}
	
	static class ReviewBlues extends AbstractAction
	{
		public void actionPerformed(ActionEvent event)
		{
			DecafEditor.bluesReview.display();
		}
	}
	
	static class ChooseType extends AbstractAction
	{
		public void actionPerformed(ActionEvent event)
		{
			DecafEditor.classEditor.getTypeChooser().display();
		}
	}
	
	static class Percolate extends AbstractAction
	{
		public void actionPerformed(ActionEvent event)
		{
			DecafEditor.classEditor.getPercolator().display();
		}
	}
	
	static class ZOrder extends LinkedList
	{
		protected int depth;
		
		protected ForwardAction forwardAction;
		protected ReverseAction reverseAction;
		protected WindowWatch windowWatch;
		
		public ZOrder()
		{
			super();
			
			this.depth = 0;
			
			this.forwardAction = new ForwardAction();
			this.reverseAction = new ReverseAction();
			this.windowWatch = new WindowWatch();
		}
		
		public void register(Window window)
		{
			window.addWindowListener(this.windowWatch);
		}
		
		public Action forwardAction()
		{
			return this.forwardAction;
		}
		
		public Action reverseAction()
		{
			return this.reverseAction;
		}
		
		protected void forward()
		{
			super.addLast(super.removeFirst());
			((Window)super.getFirst()).toFront();
		}
		
		protected void reverse()
		{
			super.addFirst(super.removeLast());
			((Window)super.getFirst()).toFront();
		}
		
		protected void focus(Window window)
		{
			super.remove(window);
			super.addFirst(window);
		}
		
		protected void ignore(Window window)
		{
			super.remove(window);
		}
		
		class ForwardAction extends AbstractAction
		{
			public void actionPerformed(ActionEvent event)
			{
				ZOrder.this.forward();
			}
		}
		
		class ReverseAction extends AbstractAction
		{
			public void actionPerformed(ActionEvent event)
			{
				ZOrder.this.reverse();
			}
		}
		
		class WindowWatch extends WindowAdapter
		{
			public void windowActivated(WindowEvent event)
			{
				ZOrder.this.focus((Window)event.getSource());
			}
			
			public void windowGainedFocus(WindowEvent event)
			{
				ZOrder.this.focus((Window)event.getSource());
			}
			
			public void windowClosed(WindowEvent event)
			{
				Window window = (Window)event.getSource();
				window.removeWindowListener(this);
				ZOrder.this.ignore(window);
			}
		}
	}
	
	static class Cascade extends Utils.RealEstateManager
	{
		protected Utils.Location current;
		
		public Cascade()
		{
			this.current = offset.copy();
		}
		
		public void place(Window window)
		{
			window.setLocationRelativeTo(null);

			if (this.current.equals(offset))
			{
				_place(window);
			}
			
			Utils.Location extent = this.current.copy();
			extent.add(new Utils.Location(window.getSize()));
			
			if (extent.exceeds(screenExtent))
			{
				this.current = offset.copy();
			}

			_place(window);
		}
		
		private void _place(Window window)
		{
			window.setLocation(this.current);
			this.current.add(offset);
		}
	}
	
	public static class Browser
	{
		protected String[] command;
		
		public Browser(String command)
		{
			this.command = new String[] {command, null};
		}
		
		public void display(String doc)
		{
			/*
			this.command[1] = javadocRoot.extend(doc).getAbsolutePath();
			
			try
			{
				Runtime.getRuntime().exec(this.command);
			}
			catch (java.io.IOException e)
			{
				DecafEditor.log("DecafEditor.Browser.display(): Unable to open document " + doc + "; IOException: " + e.getMessage());
			}
			*/
			
			try
			{
				String url = "file://" + javadocRoot.extend(doc).getAbsolutePath();
				
				DecafEditor.log("Attempting to display url " + url);
				
				com.Ostermiller.util.Browser.displayURL(url);
			}
			catch (IOException e)
			{
				DecafEditor.log(e);
			}
		}
	}
	
	public static class Alert extends JDialog
	{
		public Alert(String title, String message)
		{
			super();
			
			super.setTitle(title);
			
			JPanel panel = new JPanel();
			JTextArea text = new JTextArea(10,40);
			text.setLineWrap(true);
			text.setWrapStyleWord(true);
			text.setText(message);
			text.setEditable(false);
			JScrollPane scrollPane = new JScrollPane(text);
			panel.add(scrollPane);
			scrollPane.getVerticalScrollBar().setValue(0);
			
			super.getContentPane().add(panel);
			super.setLocationRelativeTo(null);
			
			super.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			super.pack();
			super.show();
		}

	
		public Object writeReplace()
			throws java.io.ObjectStreamException
		{
			throw (new Utils.SerialException(getClass()));
		}
	}
	
	static class DecafLog extends SuperFile implements GSourceFile.Log
	{
		public DecafLog(String filename)
		{
			super(filename);
		}
		
		public void log(String message)
		{
			if (nativeOn)
			{
				jPluginLib.log(message);
			}
			else
			{
				super.entry(message);
			}
		}
		
		public void log(Throwable t)
		{
			if (nativeOn)
			{
				jPluginLib.log(t.getClass().getName() +  ": " + t.getMessage());
				exceptionContent.flush();
				t.printStackTrace(exceptionOutputHandle);
				jPluginLib.log(exceptionContent.toString());
			}
			else
			{
				System.err.println(t);
				//t.printStackTrace(System.err);
				super.entry(t);
			}
		}
	}
}