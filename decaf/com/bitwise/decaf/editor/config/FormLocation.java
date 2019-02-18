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
 
package com.bitwise.decaf.editor.config;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.bind.ConversionException;
import javax.xml.bind.Dispatcher;
import javax.xml.bind.DuplicateAttributeException;
import javax.xml.bind.Element;
import javax.xml.bind.InvalidAttributeException;
import javax.xml.bind.LocalValidationException;
import javax.xml.bind.MarshallableObject;
import javax.xml.bind.Marshaller;
import javax.xml.bind.MissingContentException;
import javax.xml.bind.StructureValidationException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Validator;
import javax.xml.marshal.XMLScanner;
import javax.xml.marshal.XMLWriter;


public class FormLocation
    extends MarshallableObject
    implements Element
{

    private String _Mode;
    private boolean isDefaulted_Mode = true;
    private final static String DEFAULT_MODE = String.valueOf("DEFAULT");
    private String _Row;
    private String _Column;
    private String _FillRightPct;

    public boolean defaultedMode() {
        return (_Mode!= null);
    }

    public String getMode() {
        if (_Mode == null) {
            return DEFAULT_MODE;
        }
        return _Mode;
    }

    public void setMode(String _Mode) {
        this._Mode = _Mode;
        if (_Mode == null) {
            invalidate();
        }
    }

    public String getRow() {
        return _Row;
    }

    public void setRow(String _Row) {
        this._Row = _Row;
        if (_Row == null) {
            invalidate();
        }
    }

    public String getColumn() {
        return _Column;
    }

    public void setColumn(String _Column) {
        this._Column = _Column;
        if (_Column == null) {
            invalidate();
        }
    }

    public String getFillRightPct() {
        return _FillRightPct;
    }

    public void setFillRightPct(String _FillRightPct) {
        this._FillRightPct = _FillRightPct;
        if (_FillRightPct == null) {
            invalidate();
        }
    }

    public void validateThis()
        throws LocalValidationException
    {
        if (_Row == null) {
            throw new MissingContentException("row");
        }
        if (_Column == null) {
            throw new MissingContentException("column");
        }
    }

    public void validate(Validator v)
        throws StructureValidationException
    {
    }

    public void marshal(Marshaller m)
        throws IOException
    {
        XMLWriter w = m.writer();
        w.start("FormLocation");
        if (_Mode!= null) {
            w.attribute("mode", _Mode.toString());
        }
        w.leaf("row", _Row.toString());
        w.leaf("column", _Column.toString());
        if (_FillRightPct!= null) {
            w.leaf("fillRightPct", _FillRightPct.toString());
        }
        w.end("FormLocation");
    }

    public void unmarshal(Unmarshaller u)
        throws UnmarshalException
    {
        XMLScanner xs = u.scanner();
        Validator v = u.validator();
        xs.takeStart("FormLocation");
        while (xs.atAttribute()) {
            String an = xs.takeAttributeName();
            if (an.equals("mode")) {
                if (_Mode!= null) {
                    throw new DuplicateAttributeException(an);
                }
                _Mode = xs.takeAttributeValue();
                continue;
            }
            throw new InvalidAttributeException(an);
        }
        if (xs.atStart("row")) {
            xs.takeStart("row");
            String s;
            if (xs.atChars(XMLScanner.WS_COLLAPSE)) {
                s = xs.takeChars(XMLScanner.WS_COLLAPSE);
            } else {
                s = "";
            }
            try {
                _Row = String.valueOf(s);
            } catch (Exception x) {
                throw new ConversionException("row", x);
            }
            xs.takeEnd("row");
        }
        if (xs.atStart("column")) {
            xs.takeStart("column");
            String s;
            if (xs.atChars(XMLScanner.WS_COLLAPSE)) {
                s = xs.takeChars(XMLScanner.WS_COLLAPSE);
            } else {
                s = "";
            }
            try {
                _Column = String.valueOf(s);
            } catch (Exception x) {
                throw new ConversionException("column", x);
            }
            xs.takeEnd("column");
        }
        if (xs.atStart("fillRightPct")) {
            xs.takeStart("fillRightPct");
            String s;
            if (xs.atChars(XMLScanner.WS_COLLAPSE)) {
                s = xs.takeChars(XMLScanner.WS_COLLAPSE);
            } else {
                s = "";
            }
            try {
                _FillRightPct = String.valueOf(s);
            } catch (Exception x) {
                throw new ConversionException("fillRightPct", x);
            }
            xs.takeEnd("fillRightPct");
        }
        xs.takeEnd("FormLocation");
    }

    public static FormLocation unmarshal(InputStream in)
        throws UnmarshalException
    {
        return unmarshal(XMLScanner.open(in));
    }

    public static FormLocation unmarshal(XMLScanner xs)
        throws UnmarshalException
    {
        return unmarshal(xs, newDispatcher());
    }

    public static FormLocation unmarshal(XMLScanner xs, Dispatcher d)
        throws UnmarshalException
    {
        return ((FormLocation) d.unmarshal(xs, (FormLocation.class)));
    }

    public boolean equals(Object ob) {
        if (this == ob) {
            return true;
        }
        if (!(ob instanceof FormLocation)) {
            return false;
        }
        FormLocation tob = ((FormLocation) ob);
        if (_Mode!= null) {
            if (tob._Mode == null) {
                return false;
            }
            if (!_Mode.equals(tob._Mode)) {
                return false;
            }
        } else {
            if (tob._Mode!= null) {
                return false;
            }
        }
        if (_Row!= null) {
            if (tob._Row == null) {
                return false;
            }
            if (!_Row.equals(tob._Row)) {
                return false;
            }
        } else {
            if (tob._Row!= null) {
                return false;
            }
        }
        if (_Column!= null) {
            if (tob._Column == null) {
                return false;
            }
            if (!_Column.equals(tob._Column)) {
                return false;
            }
        } else {
            if (tob._Column!= null) {
                return false;
            }
        }
        if (_FillRightPct!= null) {
            if (tob._FillRightPct == null) {
                return false;
            }
            if (!_FillRightPct.equals(tob._FillRightPct)) {
                return false;
            }
        } else {
            if (tob._FillRightPct!= null) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int h = 0;
        h = ((127 *h)+((_Mode!= null)?_Mode.hashCode(): 0));
        h = ((127 *h)+((_Row!= null)?_Row.hashCode(): 0));
        h = ((127 *h)+((_Column!= null)?_Column.hashCode(): 0));
        h = ((127 *h)+((_FillRightPct!= null)?_FillRightPct.hashCode(): 0));
        return h;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("<<FormLocation");
        sb.append(" mode=");
        sb.append(getMode().toString());
        if (_Row!= null) {
            sb.append(" row=");
            sb.append(_Row.toString());
        }
        if (_Column!= null) {
            sb.append(" column=");
            sb.append(_Column.toString());
        }
        if (_FillRightPct!= null) {
            sb.append(" fillRightPct=");
            sb.append(_FillRightPct.toString());
        }
        sb.append(">>");
        return sb.toString();
    }

    public static Dispatcher newDispatcher() {
        return ClassComponents.newDispatcher();
    }

}
