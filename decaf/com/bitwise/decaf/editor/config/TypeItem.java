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


public class TypeItem
    extends MarshallableObject
    implements Element
{

    private IdentifiableElement _Uid;

    public IdentifiableElement getUid() {
        return _Uid;
    }

    public void setUid(IdentifiableElement _Uid) {
        this._Uid = _Uid;
        if (_Uid == null) {
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
        v.reference(_Uid);
    }

    public void marshal(Marshaller m)
        throws IOException
    {
        XMLWriter w = m.writer();
        w.start("TypeItem");
        w.attribute("uid", _Uid.id());
        w.end("TypeItem");
    }

    public void unmarshal(Unmarshaller u)
        throws UnmarshalException
    {
        XMLScanner xs = u.scanner();
        Validator v = u.validator();
        xs.takeStart("TypeItem");
        while (xs.atAttribute()) {
            String an = xs.takeAttributeName();
            if (an.equals("uid")) {
                if (_Uid!= null) {
                    throw new DuplicateAttributeException(an);
                }
                v.reference(xs.takeAttributeValue(), new UidVPatcher());
                continue;
            }
            throw new InvalidAttributeException(an);
        }
        xs.takeEnd("TypeItem");
    }

    public static TypeItem unmarshal(InputStream in)
        throws UnmarshalException
    {
        return unmarshal(XMLScanner.open(in));
    }

    public static TypeItem unmarshal(XMLScanner xs)
        throws UnmarshalException
    {
        return unmarshal(xs, newDispatcher());
    }

    public static TypeItem unmarshal(XMLScanner xs, Dispatcher d)
        throws UnmarshalException
    {
        return ((TypeItem) d.unmarshal(xs, (TypeItem.class)));
    }

    public boolean equals(Object ob) {
        if (this == ob) {
            return true;
        }
        if (!(ob instanceof TypeItem)) {
            return false;
        }
        TypeItem tob = ((TypeItem) ob);
        if (_Uid!= null) {
            if (tob._Uid == null) {
                return false;
            }
            if (!_Uid.equals(tob._Uid)) {
                return false;
            }
        } else {
            if (tob._Uid!= null) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int h = 0;
        h = ((179 *h)+((_Uid!= null)?_Uid.id().hashCode(): 0));
        return h;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("<<TypeItem");
        if (_Uid!= null) {
            sb.append(" uid=");
            sb.append(_Uid.id());
        }
        sb.append(">>");
        return sb.toString();
    }

    public static Dispatcher newDispatcher() {
        return ClassComponents.newDispatcher();
    }


    private class UidVPatcher
        extends Validator.Patcher
    {


        public void patch(IdentifiableElement target) {
            _Uid = target;
        }

    }

}
