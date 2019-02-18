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


public class Represents
    extends MarshallableObject
    implements Element
{

    private String _Classname;
    private String _Fieldname;

    public String getClassname() {
        return _Classname;
    }

    public void setClassname(String _Classname) {
        this._Classname = _Classname;
        if (_Classname == null) {
            invalidate();
        }
    }

    public String getFieldname() {
        return _Fieldname;
    }

    public void setFieldname(String _Fieldname) {
        this._Fieldname = _Fieldname;
        if (_Fieldname == null) {
            invalidate();
        }
    }

    public void validateThis()
        throws LocalValidationException
    {
        if (_Classname == null) {
            throw new MissingContentException("classname");
        }
        if (_Fieldname == null) {
            throw new MissingContentException("fieldname");
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
        w.start("Represents");
        w.leaf("classname", _Classname.toString());
        w.leaf("fieldname", _Fieldname.toString());
        w.end("Represents");
    }

    public void unmarshal(Unmarshaller u)
        throws UnmarshalException
    {
        XMLScanner xs = u.scanner();
        Validator v = u.validator();
        xs.takeStart("Represents");
        while (xs.atAttribute()) {
            String an = xs.takeAttributeName();
            throw new InvalidAttributeException(an);
        }
        if (xs.atStart("classname")) {
            xs.takeStart("classname");
            String s;
            if (xs.atChars(XMLScanner.WS_COLLAPSE)) {
                s = xs.takeChars(XMLScanner.WS_COLLAPSE);
            } else {
                s = "";
            }
            try {
                _Classname = String.valueOf(s);
            } catch (Exception x) {
                throw new ConversionException("classname", x);
            }
            xs.takeEnd("classname");
        }
        if (xs.atStart("fieldname")) {
            xs.takeStart("fieldname");
            String s;
            if (xs.atChars(XMLScanner.WS_COLLAPSE)) {
                s = xs.takeChars(XMLScanner.WS_COLLAPSE);
            } else {
                s = "";
            }
            try {
                _Fieldname = String.valueOf(s);
            } catch (Exception x) {
                throw new ConversionException("fieldname", x);
            }
            xs.takeEnd("fieldname");
        }
        xs.takeEnd("Represents");
    }

    public static Represents unmarshal(InputStream in)
        throws UnmarshalException
    {
        return unmarshal(XMLScanner.open(in));
    }

    public static Represents unmarshal(XMLScanner xs)
        throws UnmarshalException
    {
        return unmarshal(xs, newDispatcher());
    }

    public static Represents unmarshal(XMLScanner xs, Dispatcher d)
        throws UnmarshalException
    {
        return ((Represents) d.unmarshal(xs, (Represents.class)));
    }

    public boolean equals(Object ob) {
        if (this == ob) {
            return true;
        }
        if (!(ob instanceof Represents)) {
            return false;
        }
        Represents tob = ((Represents) ob);
        if (_Classname!= null) {
            if (tob._Classname == null) {
                return false;
            }
            if (!_Classname.equals(tob._Classname)) {
                return false;
            }
        } else {
            if (tob._Classname!= null) {
                return false;
            }
        }
        if (_Fieldname!= null) {
            if (tob._Fieldname == null) {
                return false;
            }
            if (!_Fieldname.equals(tob._Fieldname)) {
                return false;
            }
        } else {
            if (tob._Fieldname!= null) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int h = 0;
        h = ((127 *h)+((_Classname!= null)?_Classname.hashCode(): 0));
        h = ((127 *h)+((_Fieldname!= null)?_Fieldname.hashCode(): 0));
        return h;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("<<Represents");
        if (_Classname!= null) {
            sb.append(" classname=");
            sb.append(_Classname.toString());
        }
        if (_Fieldname!= null) {
            sb.append(" fieldname=");
            sb.append(_Fieldname.toString());
        }
        sb.append(">>");
        return sb.toString();
    }

    public static Dispatcher newDispatcher() {
        return ClassComponents.newDispatcher();
    }

}
