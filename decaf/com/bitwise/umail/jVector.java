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


public class jVector {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected jVector(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected void finalize() {
    delete();
  }

  public void delete() {
    if(swigCPtr != 0 && swigCMemOwn) {
      uMailJNI.delete_jVector(swigCPtr);
      swigCMemOwn = false;
    }
    swigCPtr = 0;
  }

  protected static long getCPtr(jVector obj) {
    return obj.swigCPtr;
  }

  public jVector() {
    this(uMailJNI.new_jVector(), true);
  }

  public Object get(int index) {
    return uMailJNI.jVector_get(swigCPtr, index);
  }

  public void set(int index, Object value) {
    uMailJNI.jVector_set(swigCPtr, index, value);
  }

  public void add(Object value) {
    uMailJNI.jVector_add(swigCPtr, value);
  }

  public void remove(int index) {
    uMailJNI.jVector_remove(swigCPtr, index);
  }

  public int getSize() {
    return uMailJNI.jVector_getSize(swigCPtr);
  }

  public uIterator iterator() {
    return new uIterator(uMailJNI.jVector_iterator(swigCPtr), false);
  }

}
