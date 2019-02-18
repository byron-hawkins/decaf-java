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


public class jDataDisplay extends jType {
  private long swigCPtr;

  protected jDataDisplay(long cPtr, boolean cMemoryOwn) {
    super(uMailJNI.SWIGjDataDisplayTojType(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  protected jDataDisplay() {
    this(0, false);
  }

  protected void finalize() {
    delete();
  }

  public void delete() {
    if(swigCPtr != 0 && swigCMemOwn) {
      uMailJNI.delete_jDataDisplay(swigCPtr);
      swigCMemOwn = false;
      super.delete();
    }
    swigCPtr = 0;
  }

  protected static long getCPtr(jDataDisplay obj) {
    return obj.swigCPtr;
  }

  public jDataDisplay(SWIGTYPE_p_void d) {
    this(uMailJNI.new_jDataDisplay(SWIGTYPE_p_void.getCPtr(d)), true);
  }

  public void setLabel(String l) {
    uMailJNI.jDataDisplay_setLabel(swigCPtr, l);
  }

  public void setValue(String v) {
    uMailJNI.jDataDisplay_setValue(swigCPtr, v);
  }

  public void display() {
    uMailJNI.jDataDisplay_display(swigCPtr);
  }

}
