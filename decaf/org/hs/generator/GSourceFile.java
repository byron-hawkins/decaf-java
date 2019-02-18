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
import at.dms.compiler.Compiler;
import at.dms.compiler.*;
import at.dms.compiler.tools.antlr.extra.InputBuffer;

import java.io.*;
import java.util.*;

import org.hs.util.file.*;

/**
 * A source file, as expected by a standard Java compiler.
 */
public class GSourceFile extends GConstruct implements Serializable
{
	static final long serialVersionUID = -4646820003265335380L;

	static int idIndex = 1;

	static KjcOptions s_options;
	static KjcClassReader s_classReader;
	static KjcTypeFactory s_typeFactory;
	static KjcEnvironment s_environment;
	static Compiler s_compiler;
	
	static CBinaryTypeContext s_typeContext;	
	
	static String s_compilingClassname;
	static String s_compilingShortName;
	static SuperFile s_currentDir;
	
	protected static String s_workingDir;
	protected static String s_classpath;
	protected static String s_extDirs;
	protected static String s_compileCommand;
	
	static Log log;
	
	static
	{
		s_classpath = null;
		s_extDirs = null;
		s_currentDir = null; 
	}
	
	/**
	 * Set the compiler's working directory.  The KJC compiler runs on the command
	 * line, and responds to its working directory as expected.
	 */
	public static void setWorkingDir(String workingDir)
	{
		s_workingDir = workingDir;
	}
	
	/**
	 * The classpath available to the compiler.  Defaults to java.class.path
	 * when using KJC -- this is currently not working properly, and so I've rigged a 
	 * call to the command line to compile the KJC generated .java file externally.  
	 * In this latter context, the classpath does not default to java.class.path.
	 */
	public static void setClasspath(String classpath)
	{
		//System.err.println("\nUsing classpath: " + classpath + "\n\n");
		s_classpath = classpath;
	}

	/**
	 * Set the extension directories.  Defaults to java.ext.dirs.
	 */	
	public static void setExtensionDirs(String extDirs)
	{
		s_extDirs = extDirs;
	}
	
	/**
	 * GSourceFile currently does not use the KJC compiler, but instead will generate
	 * a .java file corresponding to the contents of this GSourceFile, and make a call
	 * to System.exec with <code>compileCommand</code> to compile that file. 
	 *
	 * @param compileCommand The absolute path to the command line compiler executable.
	 */
	public static void setCompileCommand(String compileCommand)
	{
		s_compileCommand = compileCommand;
	}
	
	/**
	 * Set the root directory from which the package name of this GSourceFile is resolved
	 * in the filesystem; provides a default value for the instance field directory, and 
	 * may be overridden on an instance basis.
	 */
	public static void setCurrentDir(SuperFile currentDir)
	{
		s_currentDir = currentDir;
	}
	
	/**
	 * Set a custom log file; must implement the interface GSourceFile.Log.
	 */
	public static void setLog(Log newLog)
	{
		log = newLog;
	}

	static void log(String message)
	{
		log.log(message);
	}
	
	static void log(Throwable t)
	{
		log.log(t);
	}

	/**
	 * Initialize KJC, providing a log output writer for it.
	 */	
	public static void init(PrintWriter log)
	{
		s_options = new KjcOptions();
		s_classReader = new KjcClassReader(s_workingDir, s_classpath, s_extDirs, new KjcSignatureParser());
		s_typeFactory = new KjcTypeFactory(s_classReader, false); // generic disabled
		s_environment = new KjcEnvironment(s_classReader, s_typeFactory, s_options);

		Main main = new Main("./", log);
		s_compiler = main;
		main.parseArguments(new String[] {});

		CStdType.init(s_compiler, s_environment);
		GType.establishPrimitives();
		
		KjcPrettyPrinter.TAB_SIZE = 4;
		KjcPrettyPrinter.ANSI_BRACES = true;
		KjcPrettyPrinter.ALWAYS_BRACE = true;
		KjcPrettyPrinter.printQualifiedNames = true;
		
		s_typeContext = new CBinaryTypeContext(s_classReader, s_typeFactory);
	}		

	/**
	 * Load a KJC CClass construct by fully qualified classname.
	 */	
	public static CClass loadClass(String name)
	{
		return s_classReader.loadClass(s_typeFactory, name);
	}
	
	/**
	 * Decode a .java file into GConstruct`s.
	 */
	public static GSourceFile decode(File file)
		throws Exception
	{
		return decode(new InputBuffer(file, null));
	}

	/**
	 * Decode the contents of a .java file available in <code>input</code>
	 * into GConstruct`s.
	 */
	public static GSourceFile decode(BufferedReader input)
		throws Exception
	{
		return decode(new InputBuffer(input));
	}
	
	/**
	 * Decode the contents of a .java file available in <code>input</code>
	 * into GConstruct`s.
	 */
	public static GSourceFile decode(InputBuffer input)
		throws Exception
	{
		KjcParser parser = new KjcParser(s_compiler, input, s_environment);
		JCompilationUnit unit = parser.jCompilationUnit();

		return (new GSourceFile(unit));
	}

	private static void setCompilingNames(String packageName, String classname)
	{
		if (classname.lastIndexOf('/') > 0)
		{
			s_compilingShortName = classname.substring(classname.lastIndexOf('/')+1, classname.length()-5);
		}
		else if (classname.lastIndexOf('\\') > 0)
		{
			s_compilingShortName = classname.substring(classname.lastIndexOf('\\')+1, classname.length()-5);
		}
		else
		{
			s_compilingShortName = classname;
		}
		
		s_compilingClassname = (packageName.replace('/','.') + "." + s_compilingShortName).replace('.','/');
	}

	/**
	 * Print the GStatement or Collection of GStatement <code>o</code> 
	 * in standard Java to <code>writer</code>.
	 *
	 * @param writer the output mechanism for the java code.
	 * @param o the GStatement or collection of GStatement to print.
	 */	
	public static void print(Writer writer, Object o)
	{
		if (o instanceof GStatement)
		{
			printStatement(writer, (GStatement)o);
		}
		else if (o instanceof Collection)
		{
			printStatements(writer, (Collection)o);
		}
		else
		{
			throw (new IllegalArgumentException(GSourceFile.class.getName() + ".print(): Can't print this object: " + o.getClass().getName()));
		}
	}
	
	/**
	 * Print the Collection of GStatement <code>statements</code> 
	 * in standard Java to <code>writer</code>.
	 *
	 * @param writer the output mechanism for the java code.
	 * @param statement the collection of GStatement to print.
	 */	
	public static void printStatements(Writer writer, Collection statements)
	{
  		TabbedPrintWriter tabbed = new TabbedPrintWriter(new BufferedWriter(writer));
		KjcPrettyPrinter printer = new KjcPrettyPrinter(tabbed, s_typeFactory);
		
		Iterator iterator = statements.iterator();
		while (iterator.hasNext())
		{
			printStatement(printer, (GStatement)iterator.next());
		}
	}

	/**
	 * Print the GStatement <code>statement</code> 
	 * in standard Java to <code>writer</code>.
	 *
	 * @param writer the output mechanism for the java code.
	 * @param statement the GStatement to print.
	 */	
	public static void printStatement(Writer writer, GStatement statement)
	{
  		TabbedPrintWriter tabbed = new TabbedPrintWriter(new BufferedWriter(writer));
		KjcPrettyPrinter printer = new KjcPrettyPrinter(tabbed, s_typeFactory);
		
		printStatement(printer, statement);
	}
	
	public static void printStatement(KjcPrettyPrinter printer, GStatement statement)
	{
		statement.encodeStatement().accept(printer);
	}

	/**
	 * The directory from which to resolve the package name of this GSourceFile.
	 * Used only for compiler and printer output.
	 */	
	public SuperFile directory;
	
	/**
	 * The classname of the outermost class declaration in this GSourceFile.
	 */
	public String classname;
	
	/**
	 * The package name of this GSourceFile.
	 */
	public String packageName;
	
	/**
	 * The imports of this GSourceFile.
	 */
	public GImportList importList;
	
	/**
	 * The type declarations in this GSourceFile.
	 */
	public Vector typeDeclarations;
	// need to collect imports as stuff is added to this.typeDeclarations
	
	public GSourceFile(String classname, String packageName)
	{
		super("???");
		init();
		
		this.directory = s_currentDir;
		this.classname = classname;
		this.packageName = packageName.replace('.','/');
	}
	
	/**
	 * Instantiate a GSourceFile from a JCompilationUnit and decode its contents
	 * into GConstruct`s.
	 *
	 * @param unit the compiled constructs to decode into GConstruct`s
	 */
	public GSourceFile(JCompilationUnit unit)
		throws Exception
	{
		super(unit);
		init();
		
		this.decode(unit);
		this.classname = ((GTypeDeclaration)this.typeDeclarations.firstElement()).name;
	}
	
	/**
	 * Set the root directory from which the package name of this GSourceFile
	 * is resolved in the filesystem.
	 */
	public void setDirectory(String directory)
	{
		this.directory = new SuperFile(directory);
	}
	
	public void applyDefaultDirectory()
	{
		this.directory = s_currentDir;
	}
	
	private void init()
	{
		this.importList = new GImportList();
		this.typeDeclarations = new Vector();
	}

	public void print(java.io.BufferedWriter writer)
		throws IOException, PositionedError
	{
		KjcPrettyPrinter printer = new KjcPrettyPrinter(writer, s_typeFactory);
		printer.addImportedPackage(packageName);
		this.importList.apply(printer);
		JCompilationUnit unit = encode(s_compiler);

		unit.accept(printer);
		
		printer.close();
	}		
	
	/**
	 * Print this GSourceFile to the .java filename specified by <code>directory</code> and 
	 * <code>packageName</code> and <code>classname</code>.
	 */
	public void print()
		throws IOException, PositionedError
	{
		SuperFile outputFile = this.directory.extend(this.packageName).extend(this.classname + ".java");
		KjcPrettyPrinter printer = new KjcPrettyPrinter(outputFile.getAbsolutePath(), s_typeFactory);
		printer.addImportedPackage(packageName);
		this.importList.apply(printer);
		JCompilationUnit unit = encode(s_compiler);

		unit.accept(printer);
		
		printer.close();
	}
	
	/**
	 * Compile this GSourceFile to the .class filename specified by <code>directory</code> and 
	 * <code>packageName</code> and <code>classname</code>.  Currently only uses KJC to print a 
	 * .java file corresponding to the contents of this GConstruct, and compiles the file using
	 * <code>s_compileCommand</code> via System.exec.
	 */
	public String compile()
		throws IOException, PositionedError, at.dms.classfile.ClassFileFormatException
	{
		this.print();
		
		try
		{
			SuperFile outputFile = this.directory.extend(this.packageName).extend(this.classname + ".java");
			
			String commandLine = s_compileCommand + " -classpath " + s_classpath + " " + outputFile.getAbsolutePath();
			
			log.log(getClass().getName() + ".compile(): " + commandLine);
			SuperProcess process = new SuperProcess(Runtime.getRuntime().exec(commandLine));
			
			String error = null;
			try
			{
				error = process.getErrorInput();
			}
			catch (InterruptedException e)
			{
			}
			
			if ((error == null) || (error.length() == 0))
			{
				return null;
			}

			return error;
		}
		catch (Exception e)
		{
			GSourceFile.log(e);
			return "An exception occurred during compilation.  Please see the log file for more details.";
		}
		
		/*
		Vector bin = new Vector();
		encode(s_compiler, bin);
	    BytecodeOptimizer optimizer = new BytecodeOptimizer(s_options.optimize);
	    Iterator iterator = bin.iterator();
	    while (iterator.hasNext())
	    {
         	((CSourceClass)iterator.next()).genCode(optimizer, s_options.destination, s_typeFactory);
        }
        */
	}

	/**
	 * Encode the contents of this GSourceFile into KJC compiler constructs.
	 */	
	public JCompilationUnit encode(Compiler compiler)
		throws PositionedError
	{
		return encode(compiler, new Vector());
	}
		
	/**
	 * Encode the contents of this GSourceFile into KJC compiler constructs.
	 * 
	 * @param bin a place to put the KJC binary constructs (an empty Vector will do).
	 */	
	public JCompilationUnit encode(Compiler compiler, Vector bin)
		throws PositionedError
	{
		JCompilationUnit encoded = new JCompilationUnit(super.tokenReference,
														s_environment,
														this.encodePackageName(),
														this.importList.encodePackages(),
														this.importList.encodeClasses(),
														this.encodeTypeDeclarations());

/*								
		encoded.join(compiler);
		encoded.checkInterface(compiler);	 					
		encoded.checkInitializers(compiler, bin);
		encoded.checkBody(compiler, bin);
*/		
		return encoded;
	}
	
	public JPackageName encodePackageName()
	{
		return (new JPackageName(super.tokenReference,
								 this.packageName,
								 super.encodeComments()));
	}

	protected JTypeDeclaration[] encodeTypeDeclarations()
		throws PositionedError
	{
		JTypeDeclaration[] encoded = new JTypeDeclaration[this.typeDeclarations.size()];
		for (int i = 0; i < this.typeDeclarations.size(); i++)
		{
			encoded[i] = ((GTypeDeclaration)this.typeDeclarations.elementAt(i)).encode();
			encoded[i].generateInterface(s_classReader, null, this.packageName + "/");
		}
		return encoded;
	}
	
	public void decode(JCompilationUnit unit)
	{
		Decoder decoder = new Decoder();
		unit.accept(decoder);
	}
	
	class GImportList extends GConstruct implements Serializable
	{
		protected Vector packages;
		protected Vector classes;
		
		protected Decoder decoder;
		
		public GImportList()
		{
			super("???");
			
			this.packages = new Vector();
			this.classes = new Vector();
			
			this.decoder = new Decoder();
		}
		
		public void add(String importName)
		{
			if (importName.indexOf("*") < 0)
			{
				this.classes.add(importName);
			}
			else
			{
				this.classes.add(importName);
			}
		}
		
		public void apply(KjcPrettyPrinter printer)
		{
			printer.addImportedPackages(this.packages);
			printer.addImportedClasses(this.classes);
		}
		
		public GDecoder decoder()
		{
			return this.decoder;
		}
		
		public JPackageImport[] encodePackages()
		{
			JPackageImport[] encoded = new JPackageImport[this.packages.size()];
			for (int i = 0; i < this.packages.size(); i++)
			{
				encoded[i] = new JPackageImport(super.tokenReference,
												(String)this.packages.elementAt(i),
												super.encodeComments());
			}
			return encoded;
		}
		
		public JClassImport[] encodeClasses()
		{
			JClassImport[] encoded = new JClassImport[this.classes.size()];
			for (int i = 0; i < this.classes.size(); i++)
			{
				encoded[i] = new JClassImport(super.tokenReference,
											  (String)this.classes.elementAt(i),
											  super.encodeComments());
			}
			return encoded;
		}
		
		class Decoder extends GConstructDecoder implements Serializable
		{
			/**
			 * visits a package import declaration
			 */
			public void visitPackageImport(String name)
			{
				GImportList.this.packages.add(name);
			}
		
			/**
			 * visits a class import declaration
			 */
			public void visitClassImport(String name)
			{
				GImportList.this.classes.add(name);
			}
		}
	}

	class Decoder extends GConstructDecoder implements Serializable
	{
		public void visitCompilationUnit(JCompilationUnit self,
										 JPackageName packageName,
										 JPackageImport[] importedPackages,
										 JClassImport[] importedClasses,
										 JTypeDeclaration[] typeDeclarations)
		{
			packageName.accept(this);
			
			setCompilingNames(GSourceFile.this.packageName, self.getFileName());
			
			for (int i = 0; i < typeDeclarations.length; i++)
			{
				GSourceFile.this.typeDeclarations.add(GTypeDeclaration.decode(typeDeclarations[i]));
			}
			
			for (int i = 0; i < importedPackages.length; i++)
			{
				importedPackages[i].accept(GSourceFile.this.importList.decoder());
			}
			
			for (int i = 0; i < importedClasses.length; i++)
			{
				importedClasses[i].accept(GSourceFile.this.importList.decoder());
			}
		}
		
		/**
		 * visits a package name declaration
		 */
		public void visitPackageName(String name)
		{
			GSourceFile.this.packageName = name.replace('.','/'); // + "/";
		}

		/**
		 * visits a type declaration statement
		 */
		public void visitTypeDeclarationStatement(JTypeDeclarationStatement self,
												  JTypeDeclaration decl)
		{
			GSourceFile.this.typeDeclarations.add(GTypeDeclaration.decode(decl));
		}
	}

	public String toString()
	{
		return "classname(" + this.classname + ") package(" + this.packageName + ") importing(" + this.importList + ") declaring(" + this.typeDeclarations + ")";
	}
	
	public static interface Log
	{
		public void log(String message);
		public void log(Throwable t);
	}
	
	static class SystemErrLog implements Log
	{
		public void log(String message)
		{
			System.err.println(message);
		}
		
		public void log(Throwable t)
		{
			System.err.println(t);
			t.printStackTrace(System.err);
		}
	}
}

