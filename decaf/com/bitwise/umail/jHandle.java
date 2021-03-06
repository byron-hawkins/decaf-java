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


import java.util.Iterator;

public class jHandle {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected jHandle(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected void finalize() {
    delete();
  }

  public void delete() {
    if(swigCPtr != 0 && swigCMemOwn) {
      uMailJNI.delete_jHandle(swigCPtr);
      swigCMemOwn = false;
    }
    swigCPtr = 0;
  }

  protected static long getCPtr(jHandle obj) {
    return obj.swigCPtr;
  }

  public String toString()
  {
  	StringBuffer buffer = new StringBuffer();
  	buffer.append(this.getMethodName());
  	buffer.append("(");
  	
  	Iterator parameters = this.getParameterTypes();
  	while (parameters.hasNext())
  	{
  		buffer.append(parameters.next().toString());
    }
    buffer.append(")");
    return buffer.toString();
  }

  public jHandle() {
    this(uMailJNI.new_jHandle(), true);
  }

  public String getMethodName() {
    return uMailJNI.jHandle_getMethodName(swigCPtr);
  }

  public String getPluginName() {
    return uMailJNI.jHandle_getPluginName(swigCPtr);
  }

  public String getDescription() {
    return uMailJNI.jHandle_getDescription(swigCPtr);
  }

  public uIterator getParameterTypes() {
    return new uIterator(uMailJNI.jHandle_getParameterTypes(swigCPtr), false);
  }

  public void setPluginName(String n) {
    uMailJNI.jHandle_setPluginName(swigCPtr, n);
  }

}
