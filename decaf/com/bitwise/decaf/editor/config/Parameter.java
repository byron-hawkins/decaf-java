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
import javax.xml.bind.IdentifiableElement;
import javax.xml.bind.InvalidAttributeException;
import javax.xml.bind.LocalValidationException;
import javax.xml.bind.MarshallableObject;
import javax.xml.bind.Marshaller;
import javax.xml.bind.StructureValidationException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Validator;
import javax.xml.bind.Validator.Patcher;
import javax.xml.marshal.XMLScanner;
import javax.xml.marshal.XMLWriter;


public class Parameter
    extends MarshallableObject
    implements Element
{

    private IdentifiableElement _Type;
    private String _Name;
    private boolean isDefaulted_Name = true;
    private final static String DEFAULT_NAME = String.valueOf("");
    private String _Dimensions;
    private boolean isDefaulted_Dimensions = true;
    private final static String DEFAULT_DIMENSIONS = String.valueOf("0");
    private String _Content;

    public IdentifiableElement getType() {
        return _Type;
    }

    public void setType(IdentifiableElement _Type) {
        this._Type = _Type;
        if (_Type == null) {
            invalidate();
        }
    }

    public boolean defaultedName() {
        return (_Name!= null);
    }

    public String getName() {
        if (_Name == null) {
            return DEFAULT_NAME;
        }
        return _Name;
    }

    public void setName(String _Name) {
        this._Name = _Name;
        if (_Name == null) {
            invalidate();
        }
    }

    public boolean defaultedDimensions() {
        return (_Dimensions!= null);
    }

    public String getDimensions() {
        if (_Dimensions == null) {
            return DEFAULT_DIMENSIONS;
        }
        return _Dimensions;
    }

    public void setDimensions(String _Dimensions) {
        this._Dimensions = _Dimensions;
        if (_Dimensions == null) {
            invalidate();
        }
    }

    public String getContent() {
        return _Content;
    }

    public void setContent(String _Content) {
        this._Content = _Content;
        if (_Content == null) {
            invalidate();
        }
    }

    public void validateThis()
        throws LocalValidationException
    {
    }

    public void validate(Validator v)
        throws StructureValidationException
    {
        v.reference(_Type);
    }

    public void marshal(Marshaller m)
        throws IOException
    {
        XMLWriter w = m.writer();
        w.start("Parameter");
        w.attribute("type", _Type.id());
        if (_Name!= null) {
            w.attribute("name", _Name.toString());
        }
        if (_Dimensions!= null) {
            w.attribute("dimensions", _Dimensions.toString());
        }
        if (_Content!= null) {
            w.chars(_Content.toString());
        }
        w.end("Parameter");
    }

    public void unmarshal(Unmarshaller u)
        throws UnmarshalException
    {
        XMLScanner xs = u.scanner();
        Validator v = u.validator();
        xs.takeStart("Parameter");
        while (xs.atAttribute()) {
            String an = xs.takeAttributeName();
            if (an.equals("type")) {
                if (_Type!= null) {
                    throw new DuplicateAttributeException(an);
                }
                v.reference(xs.takeAttributeValue(), new TypeVPatcher());
                continue;
            }
            if (an.equals("name")) {
                if (_Name!= null) {
                    throw new DuplicateAttributeException(an);
                }
                _Name = xs.takeAttributeValue();
                continue;
            }
            if (an.equals("dimensions")) {
                if (_Dimensions!= null) {
                    throw new DuplicateAttributeException(an);
                }
                _Dimensions = xs.takeAttributeValue();
                continue;
            }
            throw new InvalidAttributeException(an);
        }
        {
            String s;
            if (xs.atChars(XMLScanner.WS_COLLAPSE)) {
                s = xs.takeChars(XMLScanner.WS_COLLAPSE);
            } else {
                s = "";
            }
            try {
                _Content = String.valueOf(s);
            } catch (Exception x) {
                throw new ConversionException("content", x);
            }
        }
        xs.takeEnd("Parameter");
    }

    public static Parameter unmarshal(InputStream in)
        throws UnmarshalException
    {
        return unmarshal(XMLScanner.open(in));
    }

    public static Parameter unmarshal(XMLScanner xs)
        throws UnmarshalException
    {
        return unmarshal(xs, newDispatcher());
    }

    public static Parameter unmarshal(XMLScanner xs, Dispatcher d)
        throws UnmarshalException
    {
        return ((Parameter) d.unmarshal(xs, (Parameter.class)));
    }

    public boolean equals(Object ob) {
        if (this == ob) {
            return true;
        }
        if (!(ob instanceof Parameter)) {
            return false;
        }
        Parameter tob = ((Parameter) ob);
        if (_Type!= null) {
            if (tob._Type == null) {
                return false;
            }
            if (!_Type.equals(tob._Type)) {
                return false;
            }
        } else {
            if (tob._Type!= null) {
                return false;
            }
        }
        if (_Name!= null) {
            if (tob._Name == null) {
                return false;
            }
            if (!_Name.equals(tob._Name)) {
                return false;
            }
        } else {
            if (tob._Name!= null) {
                return false;
            }
        }
        if (_Dimensions!= null) {
            if (tob._Dimensions == null) {
                return false;
            }
            if (!_Dimensions.equals(tob._Dimensions)) {
                return false;
            }
        } else {
            if (tob._Dimensions!= null) {
                return false;
            }
        }
        if (_Content!= null) {
            if (tob._Content == null) {
                return false;
            }
            if (!_Content.equals(tob._Content)) {
                return false;
            }
        } else {
            if (tob._Content!= null) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int h = 0;
        h = ((179 *h)+((_Type!= null)?_Type.id().hashCode(): 0));
        h = ((127 *h)+((_Name!= null)?_Name.hashCode(): 0));
        h = ((127 *h)+((_Dimensions!= null)?_Dimensions.hashCode(): 0));
        h = ((127 *h)+((_Content!= null)?_Content.hashCode(): 0));
        return h;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("<<Parameter");
        if (_Type!= null) {
            sb.append(" type=");
            sb.append(_Type.id());
        }
        sb.append(" name=");
        sb.append(getName().toString());
        sb.append(" dimensions=");
        sb.append(getDimensions().toString());
        if (_Content!= null) {
            sb.append(" content=");
            sb.append(_Content.toString());
        }
        sb.append(">>");
        return sb.toString();
    }

    public static Dispatcher newDispatcher() {
        return ClassComponents.newDispatcher();
    }


    private class TypeVPatcher
        extends Validator.Patcher
    {


        public void patch(IdentifiableElement target) {
            _Type = target;
        }

    }

}
