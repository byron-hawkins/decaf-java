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


import java.text.*;

public class jTime extends java.util.GregorianCalendar {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected jTime(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected void finalize() {
    delete();
  }

  public void delete() {
    if(swigCPtr != 0 && swigCMemOwn) {
      uMailJNI.delete_jTime(swigCPtr);
      swigCMemOwn = false;
    }
    swigCPtr = 0;
  }

  protected static long getCPtr(jTime obj) {
    return obj.swigCPtr;
  }

  public void init()
  {
    super.set(this.getYear() + 1900, this.getMonth(), this.getDay(), this.getHour(), this.getMinute(), this.getSecond());
  }
  
  protected static final DateFormat s_format = new SimpleDateFormat("MM/dd/yyyy hh:mm");
  
  public String format()
  {
  	return s_format.format(this.getTime());
  }

  public int getSecond() {
    return uMailJNI.jTime_getSecond(swigCPtr);
  }

  public int getMinute() {
    return uMailJNI.jTime_getMinute(swigCPtr);
  }

  public int getHour() {
    return uMailJNI.jTime_getHour(swigCPtr);
  }

  public int getDay() {
    return uMailJNI.jTime_getDay(swigCPtr);
  }

  public int getMonth() {
    return uMailJNI.jTime_getMonth(swigCPtr);
  }

  public int getYear() {
    return uMailJNI.jTime_getYear(swigCPtr);
  }

  public boolean isDST() {
    return uMailJNI.jTime_isDST(swigCPtr);
  }

  public jTime() {
    this(uMailJNI.new_jTime(), true);
  }

}
