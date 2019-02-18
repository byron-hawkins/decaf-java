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

/**
 * This class serves as a router for the enormous interface at.dms.kjc.KjcVisitor.
 * All of its instance methods are null implemented (to throw a RuntimeException),
 * and it is assumed that an actual decoder facility will implement the relevant 
 * KjcVisitor methods.  Unfortunately, this class lacks a proper registration or
 * hook mechanism by which to include a new handler of any particular JPhylum.
 * That will be amended in the future.
 */
public class GDecoder implements KjcVisitor, java.io.Serializable
{
	static final long serialVersionUID = 6868188798402635302L;

	protected transient GConstruct temporary;
	protected static GDecoder instance = new GDecoder();

	protected static GStatement decode(JStatement statement)
	{
		if (statement instanceof JBlock)
		{
			return (new GBlock(statement));
		}

		if (statement instanceof JBreakStatement)
		{
			return (new GBreak(statement));
		}

		if (statement instanceof JClassFieldDeclarator)
		{
			return (new GVariable(statement));
		}

		if (statement instanceof JCompoundStatement)
		{
			throw (new IllegalArgumentException("Decoding JCompoundStatement is not currently supported."));
		}

		if (statement instanceof JContinueStatement)
		{
			return (new GContinue(statement));
		}

		if (statement instanceof JDoStatement)
		{
			return (new GWhile(statement));
		}

		if (statement instanceof JEmptyStatement)
		{
			// will this crash?
			return null;
		}

		if (statement instanceof JExpressionListStatement)
		{
			throw (new IllegalArgumentException("Attempt to decode a JExpressionListStatement as a generic JStatement (it can only be the increment of a 'for' loop)."));
		}

		if (statement instanceof JExpressionStatement)
		{
			statement.accept(instance);
			return (GStatement)instance.temporary;
		}

		if (statement instanceof JForStatement)
		{
			return (new GFor(statement));
		}

		if (statement instanceof JIfStatement)
		{
			return (new GIf(statement));
		}

		if (statement instanceof JLabeledStatement)
		{
			statement.accept(instance);
			return (GStatement)instance.temporary;
		}

		if (statement instanceof JWhileStatement)
		{
			return (new GWhile(statement));
		}

		if (statement instanceof JSwitchStatement)
		{
			return (new GSwitch(statement));
		}

		if (statement instanceof JSynchronizedStatement)
		{
			return (new GSynchronized(statement));
		}

		if (statement instanceof JThrowStatement)
		{
			return (new GThrow(statement));
		}

		if (statement instanceof JTryCatchStatement)
		{
			return (new GTry(statement));
		}

		if (statement instanceof JTryFinallyStatement)
		{
			return (new GTry(statement));
		}

		if (statement instanceof JTypeDeclarationStatement)
		{
			statement.accept(instance);
			return (GStatement)instance.temporary;
		}

		if (statement instanceof JVariableDeclarationStatement)
		{
			return (new GVariableList(statement));
		}

		if (statement instanceof JReturnStatement)
		{
			return (new GReturn(statement));
		}
		
		if (statement instanceof JCode)
		{
			return (new GCode(statement));
		}
		
		throw (new IllegalArgumentException("Can't decode JStatement: " + statement.getClass().getName()));
	}

	protected static GExpression decode(JExpression expression)
	{
		if (expression == null)
		{
			return null;
		}
		
		if (expression instanceof JArrayAccessExpression)
		{
			return (new GArrayAccess(expression));
		}

		if (expression instanceof JArrayInitializer)
		{
			throw (new IllegalArgumentException("Attempt to decode a JArrayInitializer as a generic expression.  Must be handled within a GNewArray."));
		}

		if (expression instanceof JArrayLengthExpression)
		{
			return (new GVariable(expression));
		}

		if (expression instanceof JBinaryArithmeticExpression)
		{
			return (new GBinary(expression));
		}

		if (expression instanceof JCompoundAssignmentExpression)
		{
			return (new GBinary(expression));
		}

		if (expression instanceof JAssignmentExpression)
		{
			return (new GBinary(expression));
		}

		if (expression instanceof JCastExpression)
		{
			return (new GCast(expression));
		}

		if (expression instanceof JCheckedExpression)
		{
			throw (new IllegalArgumentException("Can't decode a JCheckedExpression."));
		}

		if (expression instanceof JClassExpression)
		{
			return (new GVariable(expression));
		}

		if (expression instanceof JConditionalExpression)
		{
			return (new GIf(expression));
		}

		if (expression instanceof JConstructorCall)
		{
			return (new GMethodCall(expression));
		}

		if (expression instanceof JInstanceofExpression)
		{
			return (new GInstanceOf(expression));
		}

		if (expression instanceof JLocalVariableExpression)
		{
			return (new GReference(expression));
//			return (new GVariable(expression));
		}

		if (expression instanceof JNewArrayExpression)
		{
			return (new GNewArray(expression));
		}

		if (expression instanceof JParenthesedExpression)
		{
			return (new GGroup(expression));
		}

		if (expression instanceof JPostfixExpression)
		{
			return (new GStepwise(expression));
		}

		if (expression instanceof JPrefixExpression)
		{
			return (new GStepwise(expression));
		}

		if (expression instanceof JQualifiedAnonymousCreation)
		{
			return (new GNew(expression));
		}

		if (expression instanceof JQualifiedInstanceCreation)
		{
			return (new GNew(expression));
		}

		if (expression instanceof JLiteral)
		{
			return (new GLiteral(expression));
		}

		if (expression instanceof JRelationalExpression)
		{
			return (new GComparison(expression));
		}

		if (expression instanceof JSuperExpression)
		{
			return (new GVariable(expression));
		}

		if (expression instanceof JThisExpression)
		{
			return (new GVariable(expression));
		}

		if (expression instanceof JTypeNameExpression)
		{
			return (new GType(expression));
		}

		if (expression instanceof JUnaryExpression)
		{
			return (new GUnary(expression));
		}

		if (expression instanceof JUnaryPromote)
		{
			expression.accept(instance);
			return (GExpression)instance.temporary;
		}

		if (expression instanceof JUnqualifiedAnonymousCreation)
		{
			return (new GNew(expression));
		}
    
		if (expression instanceof JUnqualifiedInstanceCreation)
		{
			return (new GNew(expression));
		}

		if (expression instanceof JNameExpression)
		{
			return (new GReference(expression));
		}

		if (expression instanceof JMethodCallExpression)
		{
			return (new GMethodCall(expression));
		}

		if (expression instanceof JFieldAccessExpression)
		{
			return (new GVariable(expression));
		}
		
		throw (new IllegalArgumentException("Can't decode JExpression: " + expression.getClass().getName()));
	}
	
	GDecoder()
	{
	}
	
	public void visitCompilationUnit(JCompilationUnit self,
									 JPackageName packageName,
									 JPackageImport[] importedPackages,
									 JClassImport[] importedClasses,
									 JTypeDeclaration[] typeDeclarations)
	{
		throw (new RuntimeException("Not implemented"));
	}


	// ----------------------------------------------------------------------
	// TYPE DECLARATION
	// ----------------------------------------------------------------------

	/**
	 * visits a class declaration
	 */
	public void visitClassDeclaration(JClassDeclaration self,
									  int modifiers,
									  String ident,
									  CTypeVariable[] typeVariables,
									  CReferenceType superClass,
									  CReferenceType[] interfaces,
									  JPhylum[] body,
									  JMethodDeclaration[] methods,
									  JFieldDeclaration[] fields,
									  JTypeDeclaration[] decls)
	{
		throw (new RuntimeException("Not implemented"));
	}

	/**
	 * visits a class body
	 */
	public void visitClassBody(JTypeDeclaration[] decls,
							   JMethodDeclaration[] methods,
							   JFieldDeclaration[] fields,
							   JPhylum[] body)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits a class declaration
	 */
	public void visitInnerClassDeclaration(JClassDeclaration self,
										   int modifiers,
										   String ident,
										   String superClass,
										   CReferenceType[] interfaces,
										   JTypeDeclaration[] decls,
										   JPhylum[] body,
										   JMethodDeclaration[] methods)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits an interface declaration
	 */
	public void visitInterfaceDeclaration(JInterfaceDeclaration self,
										  int modifiers,
										  String ident,
										  CReferenceType[] interfaces,
										  JPhylum[] body,
										  JMethodDeclaration[] methods)
	{
		throw (new RuntimeException("Not implemented"));
	}


	// ----------------------------------------------------------------------
	// METHODS AND FIELDS
	// ----------------------------------------------------------------------

	/**
	 * visits a field declaration
	 */
	public void visitFieldDeclaration(JFieldDeclaration self,
									  int modifiers,
									  CType type,
									  String ident,
									  JExpression expr)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits a method declaration
	 */
	public void visitMethodDeclaration(JMethodDeclaration self,
									   int modifiers,
									   CTypeVariable[] typeVariables,
									   CType returnType,
									   String ident,
									   JFormalParameter[] parameters,
									   CReferenceType[] exceptions,
									   JBlock body)
	{
		throw (new RuntimeException("Not implemented"));
	}

	/**
	 * prints a constrained method declaration
	 */
	public void visitKopiMethodDeclaration(JMethodDeclaration self,
												  int modifiers,
												  CType returnType,
												  String ident,
												  JFormalParameter[] parameters,
												  CReferenceType[] exceptions,
												  JBlock body,
												  JBlock ensure,
												  JBlock require)
	{
		throw (new RuntimeException("Not implemented"));
	}

	/**
	 * visits a method declaration
	 */
	public void visitConstructorDeclaration(JConstructorDeclaration self,
											int modifiers,
											String ident,
											JFormalParameter[] parameters,
											CReferenceType[] exceptions,
											JConstructorBlock body)
	{
		throw (new RuntimeException("Not implemented"));
	}


	// ----------------------------------------------------------------------
	// STATEMENTS
	// ----------------------------------------------------------------------

	/**
	 * visits a while statement
	 */
	public void visitWhileStatement(JWhileStatement self,
									JExpression cond,
									JStatement body)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits a variable declaration statement
	 */
	public void visitVariableDeclarationStatement(JVariableDeclarationStatement self,
												  JVariableDefinition[] vars)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits a variable declaration statement
	 */
	public void visitVariableDefinition(JVariableDefinition self,
										int modifiers,
										CType type,
										String ident,
										JExpression expr)
	{
		throw (new RuntimeException("Not implemented"));
	}

	/**
	 * visits a try-catch statement
	 */
	public void visitTryCatchStatement(JTryCatchStatement self,
									   JBlock tryClause,
									   JCatchClause[] catchClauses)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits a try-finally statement
	 */
	public void visitTryFinallyStatement(JTryFinallyStatement self,
										 JBlock tryClause,
										 JBlock finallyClause)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits a throw statement
	 */
	public void visitThrowStatement(JThrowStatement self,
									JExpression expr)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits a synchronized statement
	 */
	public void visitSynchronizedStatement(JSynchronizedStatement self,
										   JExpression cond,
										   JStatement body)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits a switch statement
	 */
	public void visitSwitchStatement(JSwitchStatement self,
									 JExpression expr,
									 JSwitchGroup[] body)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits a return statement
	 */
	public void visitReturnStatement(JReturnStatement self,
									 JExpression expr)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits a labeled statement
	 */
	public void visitLabeledStatement(JLabeledStatement self,
									  String label,
									  JStatement stmt)
	{
		if (stmt instanceof JWhileStatement)
		{
			this.temporary = new GWhile(stmt, label);
		}
		if (stmt instanceof JDoStatement)
		{
			this.temporary = new GWhile(stmt, label);
		}
		else if (stmt instanceof JSwitchStatement)
		{
			this.temporary = new GSwitch(stmt, label);
		}
		else if (stmt instanceof JForStatement)
		{
			this.temporary = new GFor(stmt, label);
		}
	}


	/**
	 * visits a if statement
	 */
	public void visitIfStatement(JIfStatement self,
								 JExpression cond,
								 JStatement thenClause,
								 JStatement elseClause)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits a for statement
	 */
	public void visitForStatement(JForStatement self,
								  JStatement init,
								  JExpression cond,
								  JStatement incr,
								  JStatement body)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits a compound statement
	 */
	public void visitCompoundStatement(JCompoundStatement self,
									   JStatement[] body)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits an expression statement
	 */
	public void visitExpressionStatement(JExpressionStatement self,
										 JExpression expr)
	{
		this.temporary = (GConstruct)decode(expr);
	}


	/**
	 * visits an expression list statement
	 */
	public void visitExpressionListStatement(JExpressionListStatement self,
											 JExpression[] expr)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits an expression list 
	 */
	public void visitExpressionList(JExpressionListStatement self,
									JExpression[] expr)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits a empty statement
	 */
	public void visitEmptyStatement(JEmptyStatement self)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits a do statement
	 */
	public void visitDoStatement(JDoStatement self,
								 JExpression cond,
								 JStatement body)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits a continue statement
	 */
	public void visitContinueStatement(JContinueStatement self,
									   String label)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits a break statement
	 */
	public void visitBreakStatement(JBreakStatement self,
									String label)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * prints an constructor body
	 */
	public void visitConstructorBlockStatement(JBlock self,
											   JExpression constructorCall,
											   JStatement[] body,
											   JavaStyleComment[] comments)
	{
		throw (new RuntimeException("Not implemented"));
	}

	
	/**
	 * visits an expression statement
	 */
	public void visitBlockStatement(JBlock self,
									JStatement[] body,
									JavaStyleComment[] comments)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits a type declaration statement
	 */
	public void visitTypeDeclarationStatement(JTypeDeclarationStatement self,
											  JTypeDeclaration decl)
	{
		this.temporary = GTypeDeclaration.decode(decl);
	}


 /**
	 * prints an assert statement
	 */
	public void visitAssertStatement(KopiAssertStatement self,
									 JExpression cond,
									 JExpression expr)
	{
		throw (new RuntimeException("Not implemented"));
	}


 /**
	 * prints a fail statement
	 */
	public void visitFailStatement(KopiFailStatement self,
								   JExpression expr)
	{
		throw (new RuntimeException("Not implemented"));
	}

	// ----------------------------------------------------------------------
	// EXPRESSION
	// ----------------------------------------------------------------------

	/**
	 * visits an unary plus expression
	 */
	public void visitUnaryPlusExpression(JUnaryExpression self,
										 JExpression expr)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits an unary minus expression
	 */
	public void visitUnaryMinusExpression(JUnaryExpression self,
										  JExpression expr)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits a bitwise complement expression
	 */
	public void visitBitwiseComplementExpression(JUnaryExpression self,
												   JExpression expr)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits a logical complement expression
	 */
	public void visitLogicalComplementExpression(JUnaryExpression self,
												   JExpression expr)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits a type name expression
	 */
	public void visitTypeNameExpression(JTypeNameExpression self,
										CReferenceType type)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits a this expression
	 */
	public void visitThisExpression(JThisExpression self,
									JExpression prefix)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits a super expression
	 */
	public void visitSuperExpression(JSuperExpression self,
							  		 JExpression prefix)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits a shift expression
	 */
	public void visitShiftExpression(JShiftExpression self,
									 int oper,
									 JExpression left,
									 JExpression right)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits a shift expressiona
	 */
	public void visitRelationalExpression(JRelationalExpression self,
										  int oper,
										  JExpression left,
										  JExpression right)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits a prefix expression
	 */
	public void visitPrefixExpression(JPrefixExpression self,
									  int oper,
									  JExpression expr)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits a postfix expression
	 */
	public void visitPostfixExpression(JPostfixExpression self,
									   int oper,
									   JExpression expr)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits a parenthesed expression
	 */
	public void visitParenthesedExpression(JParenthesedExpression self,
										   JExpression expr)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * Visits an unqualified anonymous class instance creation expression.
	 */
	public void visitQualifiedAnonymousCreation(JQualifiedAnonymousCreation self,
												JExpression prefix,
												String ident,
												JExpression[] params,
												JClassDeclaration decl)
	{
		throw (new RuntimeException("Not implemented"));
	}

	/**
	 * Visits an unqualified instance creation expression.
	 */
	public void visitQualifiedInstanceCreation(JQualifiedInstanceCreation self,
											   JExpression prefix,
											   String ident,
											   JExpression[] params)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * Visits an unqualified anonymous class instance creation expression.
	 */
	public void visitUnqualifiedAnonymousCreation(JUnqualifiedAnonymousCreation self,
												  CReferenceType type,
												  JExpression[] params,
												  JClassDeclaration decl)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * Visits an unqualified instance creation expression.
	 */
	public void visitUnqualifiedInstanceCreation(JUnqualifiedInstanceCreation self,
												 CReferenceType type,
												 JExpression[] params)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits an array allocator expression
	 */
	public void visitNewArrayExpression(JNewArrayExpression self,
										CType type,
										JExpression[] dims,
										JArrayInitializer init)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits a name expression
	 */
	public void visitNameExpression(JNameExpression self,
									JExpression prefix,
									String ident)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits an array allocator expression
	 */
	public void visitBinaryExpression(JBinaryExpression self,
									  String oper,
									  JExpression left,
									  JExpression right)
	{
		throw (new RuntimeException("Not implemented"));
	}

	/**
	 * visits a method call expression
	 */
	public void visitMethodCallExpression(JMethodCallExpression self,
										  JExpression prefix,
										  String ident,
										  JExpression[] args)
	{
		throw (new RuntimeException("Not implemented"));
	}

	/**
	 * visits a local variable expression
	 */
	public void visitLocalVariableExpression(JLocalVariableExpression self,
											 String ident)
	{
		throw (new RuntimeException("Not implemented"));
	}

	/**
	 * visits an instanceof expression
	 */
	public void visitInstanceofExpression(JInstanceofExpression self,
										  JExpression expr,
										  CType dest)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits an equality expression
	 */
	public void visitEqualityExpression(JEqualityExpression self,
										boolean equal,
										JExpression left,
										JExpression right)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits a conditional expression
	 */
	public void visitConditionalExpression(JConditionalExpression self,
										   JExpression cond,
										   JExpression left,
										   JExpression right)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits a compound expression
	 */
	public void visitCompoundAssignmentExpression(JCompoundAssignmentExpression self,
												  int oper,
												  JExpression left,
												  JExpression right)
	{
		throw (new RuntimeException("Not implemented"));
	}

	/**
	 * visits a field expression
	 */
	public void visitFieldExpression(JFieldAccessExpression self,
									 JExpression left,
									 String ident)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits a class expression
	 */
	public void visitClassExpression(JClassExpression self,
									 CType type,
									 JExpression prefix,
									 int bounds)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits a cast expression
	 */
	public void visitCastExpression(JCastExpression self,
									JExpression expr,
									CType type)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits a cast expression
	 */
	public void visitUnaryPromoteExpression(JUnaryPromote self,
											JExpression expr,
											CType type)
	{
		this.temporary = (GConstruct)GDecoder.decode(expr);	// never mind the implicit cast
	}


	/**
	 * visits a compound assignment expression
	 */
	public void visitBitwiseExpression(JBitwiseExpression self,
									   int oper,
									   JExpression left,
									   JExpression right)
	{
		throw (new RuntimeException("Not implemented"));
	}

	/**
	 * visits an assignment expression
	 */
	public void visitAssignmentExpression(JAssignmentExpression self,
										  JExpression left,
										  JExpression right)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits an array length expression
	 */
	public void visitArrayLengthExpression(JArrayLengthExpression self,
										   JExpression prefix)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits an array length expression
	 */
	public void visitArrayAccessExpression(JArrayAccessExpression self,
										   JExpression prefix,
										   JExpression accessor)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits an array length expression
	 */
	public void visitComments(JavaStyleComment[] comments)
	{
		if ((comments != null) && (comments.length > 0))
		{
			GSourceFile.log("Losing " + comments.length + " comments in GDecoder.");
		}
	}


	/**
	 * visits an array length expression
	 */
	public void visitComment(JavaStyleComment comment)
	{
		if ((comment != null) && (comment.getText() != null) && (comment.getText().length() > 0))
		{
			GSourceFile.log("Losing comment: \"" + comment.getText() + "\"");
		}
	}


	/**
	 * visits an array length expression
	 */
	public void visitJavadoc(JavadocComment comment)
	{
		if ((comment != null) && (comment.getText() != null) && (comment.getText().length() > 0))
		{
			GSourceFile.log("Losing comment: \"" + comment.getText() + "\"");
		}
	}


	// ----------------------------------------------------------------------
	// OTHERS
	// ----------------------------------------------------------------------

	/**
	 * visits an array length expression
	 */
	public void visitSwitchLabel(JSwitchLabel self,
								 JExpression expr)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits an array length expression
	 */
	public void visitSwitchGroup(JSwitchGroup self,
								 JSwitchLabel[] labels,
								 JStatement[] stmts)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits an array length expression
	 */
	public void visitCatchClause(JCatchClause self,
								 JFormalParameter exception,
								 JBlock body)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits an array length expression
	 */
	public void visitFormalParameter(JFormalParameter self,
									  boolean isFinal,
									  CType type,
									  String ident)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits an array length expression
	 */
	public void visitConstructorCall(JConstructorCall self,
									 boolean functorIsThis,
									 JExpression[] params)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits an array initializer expression
	 */
	public void visitArrayInitializer(JArrayInitializer self,
									  JExpression[] elems)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits a boolean literal
	 */
	public void visitBooleanLiteral(boolean value)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits a byte literal
	 */
	public void visitByteLiteral(byte value)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits a character literal
	 */
	public void visitCharLiteral(char value)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits a double literal
	 */
	public void visitDoubleLiteral(double value)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits a float literal
	 */
	public void visitFloatLiteral(float value)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits a int literal
	 */
	public void visitIntLiteral(int value)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits a long literal
	 */
	public void visitLongLiteral(long value)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits a short literal
	 */
	public void visitShortLiteral(short value)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits a string literal
	 */
	public void visitStringLiteral(String value)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits a null literal
	 */
	public void visitNullLiteral()
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits a package name declaration
	 */
	public void visitPackageName(String name)
	{
		throw (new RuntimeException("Not implemented"));
	}


	/**
	 * visits a package import declaration
	 */
	public void visitPackageImport(String name)
	{
		throw (new RuntimeException("Not implemented"));
	}

	/**
	 * visits a class import declaration
	 */
	public void visitClassImport(String name)
	{
		throw (new RuntimeException("Not implemented"));
	}

	/**
	 * visits a code statement
	 */
	public void visitCodeStatement(JCode self, 
								   String code,
								   JavaStyleComment[] comments)
	{
		throw (new RuntimeException("Not implemented"));
	}
}
/*
	G(JExpression expression)
	{
		super(expression);
		expression.accept(new Decoder());
	}
	
	class Decoder extends GConstructDecoder
	{



	G(JStatement statement)
	{
		super(statement);
		statement.accept(new Decoder());
	}

	class Decoder extends GConstructDecoder
	{
*/