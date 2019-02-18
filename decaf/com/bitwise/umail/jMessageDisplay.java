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


public class jMessageDisplay extends jType {
  private long swigCPtr;

  protected jMessageDisplay(long cPtr, boolean cMemoryOwn) {
    super(uMailJNI.SWIGjMessageDisplayTojType(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  protected jMessageDisplay() {
    this(0, false);
  }

  protected void finalize() {
    delete();
  }

  public void delete() {
    if(swigCPtr != 0 && swigCMemOwn) {
      uMailJNI.delete_jMessageDisplay(swigCPtr);
      swigCMemOwn = false;
      super.delete();
    }
    swigCPtr = 0;
  }

  protected static long getCPtr(jMessageDisplay obj) {
    return obj.swigCPtr;
  }

  public void init()
  {
  	this.initValues();
  	jTime sentTime = this.getMessage().getSentTime();
  	sentTime.init();
  	this.getSentTime().setValue(sentTime.format());
  }

  public jMessageDisplay(SWIGTYPE_p_void m) {
    this(uMailJNI.new_jMessageDisplay(SWIGTYPE_p_void.getCPtr(m)), true);
  }

  public jDataDisplay getFrom() {
    return new jDataDisplay(uMailJNI.jMessageDisplay_getFrom(swigCPtr), false);
  }

  public jDataDisplay getSubject() {
    return new jDataDisplay(uMailJNI.jMessageDisplay_getSubject(swigCPtr), false);
  }

  public jDataDisplay getBody() {
    return new jDataDisplay(uMailJNI.jMessageDisplay_getBody(swigCPtr), false);
  }

  public jDataDisplay getSentTime() {
    return new jDataDisplay(uMailJNI.jMessageDisplay_getSentTime(swigCPtr), false);
  }

  public jMessage getMessage() {
    return new jMessage(uMailJNI.jMessageDisplay_getMessage(swigCPtr), false);
  }

  public void initValues() {
    uMailJNI.jMessageDisplay_initValues(swigCPtr);
  }

  public void align() {
    uMailJNI.jMessageDisplay_align(swigCPtr);
  }

}
