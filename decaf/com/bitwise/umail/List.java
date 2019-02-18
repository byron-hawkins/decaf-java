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


public class List {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected List(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected void finalize() {
    delete();
  }

  public void delete() {
    if(swigCPtr != 0 && swigCMemOwn) {
      uMailJNI.delete_List(swigCPtr);
      swigCMemOwn = false;
    }
    swigCPtr = 0;
  }

  protected static long getCPtr(List obj) {
    return obj.swigCPtr;
  }

  public List() {
    this(uMailJNI.new_List(), true);
  }

  public void append(SWIGTYPE_p_void addMe) {
    uMailJNI.List_append(swigCPtr, SWIGTYPE_p_void.getCPtr(addMe));
  }

  public int getSize() {
    return uMailJNI.List_getSize(swigCPtr);
  }

  public ListIterator iterator() {
    return new ListIterator(uMailJNI.List_iterator(swigCPtr), false);
  }

  public void shrink() {
    uMailJNI.List_shrink(swigCPtr);
  }

}
