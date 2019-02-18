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


public class NameComponent
    extends MarshallableObject
    implements Element
{

    private String _Classname;
    private String _Label;
    private FormLocation _FormLocation;

    public String getClassname() {
        return _Classname;
    }

    public void setClassname(String _Classname) {
        this._Classname = _Classname;
        if (_Classname == null) {
            invalidate();
        }
    }

    public String getLabel() {
        return _Label;
    }

    public void setLabel(String _Label) {
        this._Label = _Label;
        if (_Label == null) {
            invalidate();
        }
    }

    public FormLocation getFormLocation() {
        return _FormLocation;
    }

    public void setFormLocation(FormLocation _FormLocation) {
        this._FormLocation = _FormLocation;
        if (_FormLocation == null) {
            invalidate();
        }
    }

    public void validateThis()
        throws LocalValidationException
    {
        if (_Classname == null) {
            throw new MissingContentException("classname");
        }
        if (_FormLocation == null) {
            throw new MissingContentException("FormLocation");
        }
    }

    public void validate(Validator v)
        throws StructureValidationException
    {
        v.validate(_FormLocation);
    }

    public void marshal(Marshaller m)
        throws IOException
    {
        XMLWriter w = m.writer();
        w.start("NameComponent");
        w.leaf("classname", _Classname.toString());
        if (_Label!= null) {
            w.leaf("Label", _Label.toString());
        }
        m.marshal(_FormLocation);
        w.end("NameComponent");
    }

    public void unmarshal(Unmarshaller u)
        throws UnmarshalException
    {
        XMLScanner xs = u.scanner();
        Validator v = u.validator();
        xs.takeStart("NameComponent");
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
        if (xs.atStart("Label")) {
            xs.takeStart("Label");
            String s;
            if (xs.atChars(XMLScanner.WS_COLLAPSE)) {
                s = xs.takeChars(XMLScanner.WS_COLLAPSE);
            } else {
                s = "";
            }
            try {
                _Label = String.valueOf(s);
            } catch (Exception x) {
                throw new ConversionException("Label", x);
            }
            xs.takeEnd("Label");
        }
        _FormLocation = ((FormLocation) u.unmarshal());
        xs.takeEnd("NameComponent");
    }

    public static NameComponent unmarshal(InputStream in)
        throws UnmarshalException
    {
        return unmarshal(XMLScanner.open(in));
    }

    public static NameComponent unmarshal(XMLScanner xs)
        throws UnmarshalException
    {
        return unmarshal(xs, newDispatcher());
    }

    public static NameComponent unmarshal(XMLScanner xs, Dispatcher d)
        throws UnmarshalException
    {
        return ((NameComponent) d.unmarshal(xs, (NameComponent.class)));
    }

    public boolean equals(Object ob) {
        if (this == ob) {
            return true;
        }
        if (!(ob instanceof NameComponent)) {
            return false;
        }
        NameComponent tob = ((NameComponent) ob);
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
        if (_Label!= null) {
            if (tob._Label == null) {
                return false;
            }
            if (!_Label.equals(tob._Label)) {
                return false;
            }
        } else {
            if (tob._Label!= null) {
                return false;
            }
        }
        if (_FormLocation!= null) {
            if (tob._FormLocation == null) {
                return false;
            }
            if (!_FormLocation.equals(tob._FormLocation)) {
                return false;
            }
        } else {
            if (tob._FormLocation!= null) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int h = 0;
        h = ((127 *h)+((_Classname!= null)?_Classname.hashCode(): 0));
        h = ((127 *h)+((_Label!= null)?_Label.hashCode(): 0));
        h = ((127 *h)+((_FormLocation!= null)?_FormLocation.hashCode(): 0));
        return h;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("<<NameComponent");
        if (_Classname!= null) {
            sb.append(" classname=");
            sb.append(_Classname.toString());
        }
        if (_Label!= null) {
            sb.append(" Label=");
            sb.append(_Label.toString());
        }
        if (_FormLocation!= null) {
            sb.append(" FormLocation=");
            sb.append(_FormLocation.toString());
        }
        sb.append(">>");
        return sb.toString();
    }

    public static Dispatcher newDispatcher() {
        return ClassComponents.newDispatcher();
    }

}
