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


public class jFolder extends jType {
  private long swigCPtr;

  protected jFolder(long cPtr, boolean cMemoryOwn) {
    super(uMailJNI.SWIGjFolderTojType(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  protected jFolder() {
    this(0, false);
  }

  protected void finalize() {
    delete();
  }

  public void delete() {
    if(swigCPtr != 0 && swigCMemOwn) {
      uMailJNI.delete_jFolder(swigCPtr);
      swigCMemOwn = false;
      super.delete();
    }
    swigCPtr = 0;
  }

  protected static long getCPtr(jFolder obj) {
    return obj.swigCPtr;
  }

  public jFolder(SWIGTYPE_p_void f) {
    this(uMailJNI.new_jFolder__SWIG_0(SWIGTYPE_p_void.getCPtr(f)), true);
  }

  public jFolder(String name) {
    this(uMailJNI.new_jFolder__SWIG_1(name), true);
  }

  public String getName() {
    return uMailJNI.jFolder_getName(swigCPtr);
  }

  public void add(jMessage message) {
    uMailJNI.jFolder_add__SWIG_0(swigCPtr, jMessage.getCPtr(message));
  }

  public void add(jFolder f) {
    uMailJNI.jFolder_add__SWIG_1(swigCPtr, jFolder.getCPtr(f));
  }

  public jFolder getSubFolder(String name) {
    return new jFolder(uMailJNI.jFolder_getSubFolder__SWIG_0(swigCPtr, name), false);
  }

  public jFolder getSubFolder(String name, boolean createIfNecessary) {
    return new jFolder(uMailJNI.jFolder_getSubFolder__SWIG_1(swigCPtr, name, createIfNecessary), false);
  }

  public void remove(int index) {
    uMailJNI.jFolder_remove(swigCPtr, index);
  }

  public int getSize() {
    return uMailJNI.jFolder_getSize(swigCPtr);
  }

  public uIterator getMessages() {
    return new uIterator(uMailJNI.jFolder_getMessages(swigCPtr), false);
  }

  public jFolder getParent() {
  	long parentPtr = uMailJNI.jFolder_getParent(swigCPtr);
  	if (parentPtr == 0)
  	{
  		return null;
  	}
    return new jFolder(parentPtr, false);
  }

  public uIterator getSubFolders() {
    return new uIterator(uMailJNI.jFolder_getSubFolders(swigCPtr), false);
  }

}
