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
 
package com.bitwise.umail;


public class jPrompt extends jType {
  private long swigCPtr;

  protected jPrompt(long cPtr, boolean cMemoryOwn) {
    super(uMailJNI.SWIGjPromptTojType(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  protected jPrompt() {
    this(0, false);
  }

  protected void finalize() {
    delete();
  }

  public void delete() {
    if(swigCPtr != 0 && swigCMemOwn) {
      uMailJNI.delete_jPrompt(swigCPtr);
      swigCMemOwn = false;
      super.delete();
    }
    swigCPtr = 0;
  }

  protected static long getCPtr(jPrompt obj) {
    return obj.swigCPtr;
  }

  public jPrompt(SWIGTYPE_p_void p) {
    this(uMailJNI.new_jPrompt__SWIG_0(SWIGTYPE_p_void.getCPtr(p)), true);
  }

  public jPrompt(String display, boolean isAtomic) {
    this(uMailJNI.new_jPrompt__SWIG_1(display, isAtomic), true);
  }

  public String askUser() {
    return uMailJNI.jPrompt_askUser__SWIG_0(swigCPtr);
  }

  public String askUser(String defaultResponse) {
    return uMailJNI.jPrompt_askUser__SWIG_1(swigCPtr, defaultResponse);
  }

}
