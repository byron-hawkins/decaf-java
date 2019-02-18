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


public class jMessage extends jType {
  private long swigCPtr;

  protected jMessage(long cPtr, boolean cMemoryOwn) {
    super(uMailJNI.SWIGjMessageTojType(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  protected jMessage() {
    this(0, false);
  }

  protected void finalize() {
    delete();
  }

  public void delete() {
    if(swigCPtr != 0 && swigCMemOwn) {
      uMailJNI.delete_jMessage(swigCPtr);
      swigCMemOwn = false;
      super.delete();
    }
    swigCPtr = 0;
  }

  protected static long getCPtr(jMessage obj) {
    return obj.swigCPtr;
  }

  public jTime getSentTime() {
  	jTime time = new jTime(uMailJNI.jMessage__getSentTime(swigCPtr), false);
  	time.init();
  	return time;
  }

  public jMessage(SWIGTYPE_p_void m) {
    this(uMailJNI.new_jMessage(SWIGTYPE_p_void.getCPtr(m)), true);
  }

  public jAddressee getFrom() {
    return new jAddressee(uMailJNI.jMessage_getFrom(swigCPtr), false);
  }

  public jAddressee getTo() {
    return new jAddressee(uMailJNI.jMessage_getTo(swigCPtr), false);
  }

  public String getSubject() {
    return uMailJNI.jMessage_getSubject(swigCPtr);
  }

  public String getBody() {
    return uMailJNI.jMessage_getBody(swigCPtr);
  }

  public void setTo(String address) {
    uMailJNI.jMessage_setTo(swigCPtr, address);
  }

  public void setSubject(String subject) {
    uMailJNI.jMessage_setSubject(swigCPtr, subject);
  }

  public void setBody(String body) {
    uMailJNI.jMessage_setBody(swigCPtr, body);
  }

  public jTime _getSentTime() {
    return new jTime(uMailJNI.jMessage__getSentTime(swigCPtr), false);
  }

}
