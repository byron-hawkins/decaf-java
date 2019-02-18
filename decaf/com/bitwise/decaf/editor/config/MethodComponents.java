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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.Dispatcher;
import javax.xml.bind.Element;
import javax.xml.bind.InvalidAttributeException;
import javax.xml.bind.InvalidContentObjectException;
import javax.xml.bind.LocalValidationException;
import javax.xml.bind.MarshallableObject;
import javax.xml.bind.Marshaller;
import javax.xml.bind.MissingContentException;
import javax.xml.bind.PredicatedLists;
import javax.xml.bind.PredicatedLists.Predicate;
import javax.xml.bind.StructureValidationException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidatableObject;
import javax.xml.bind.Validator;
import javax.xml.marshal.XMLScanner;
import javax.xml.marshal.XMLWriter;


public class MethodComponents
    extends MarshallableObject
    implements Element
{

    private NameComponent _NameComponent;
    private ReturnComponent _ReturnComponent;
    private List _CustomComponent = PredicatedLists.createInvalidating(this, new CustomComponentPredicate(), new ArrayList());
    private PredicatedLists.Predicate pred_CustomComponent = new CustomComponentPredicate();

    public NameComponent getNameComponent() {
        return _NameComponent;
    }

    public void setNameComponent(NameComponent _NameComponent) {
        this._NameComponent = _NameComponent;
        if (_NameComponent == null) {
            invalidate();
        }
    }

    public ReturnComponent getReturnComponent() {
        return _ReturnComponent;
    }

    public void setReturnComponent(ReturnComponent _ReturnComponent) {
        this._ReturnComponent = _ReturnComponent;
        if (_ReturnComponent == null) {
            invalidate();
        }
    }

    public List getCustomComponent() {
        return _CustomComponent;
    }

    public void deleteCustomComponent() {
        _CustomComponent = null;
        invalidate();
    }

    public void emptyCustomComponent() {
        _CustomComponent = PredicatedLists.createInvalidating(this, pred_CustomComponent, new ArrayList());
    }

    public void validateThis()
        throws LocalValidationException
    {
        if (_NameComponent == null) {
            throw new MissingContentException("NameComponent");
        }
        if (_ReturnComponent == null) {
            throw new MissingContentException("ReturnComponent");
        }
    }

    public void validate(Validator v)
        throws StructureValidationException
    {
        v.validate(_NameComponent);
        v.validate(_ReturnComponent);
        for (Iterator i = _CustomComponent.iterator(); i.hasNext(); ) {
            v.validate(((ValidatableObject) i.next()));
        }
    }

    public void marshal(Marshaller m)
        throws IOException
    {
        XMLWriter w = m.writer();
        w.start("MethodComponents");
        m.marshal(_NameComponent);
        m.marshal(_ReturnComponent);
        if (_CustomComponent.size()> 0) {
            for (Iterator i = _CustomComponent.iterator(); i.hasNext(); ) {
                m.marshal(((MarshallableObject) i.next()));
            }
        }
        w.end("MethodComponents");
    }

    public void unmarshal(Unmarshaller u)
        throws UnmarshalException
    {
        XMLScanner xs = u.scanner();
        Validator v = u.validator();
        xs.takeStart("MethodComponents");
        while (xs.atAttribute()) {
            String an = xs.takeAttributeName();
            throw new InvalidAttributeException(an);
        }
        _NameComponent = ((NameComponent) u.unmarshal());
        _ReturnComponent = ((ReturnComponent) u.unmarshal());
        {
            List l = PredicatedLists.create(this, pred_CustomComponent, new ArrayList());
            while (xs.atStart("CustomComponent")) {
                l.add(((CustomComponent) u.unmarshal()));
            }
            _CustomComponent = PredicatedLists.createInvalidating(this, pred_CustomComponent, l);
        }
        xs.takeEnd("MethodComponents");
    }

    public static MethodComponents unmarshal(InputStream in)
        throws UnmarshalException
    {
        return unmarshal(XMLScanner.open(in));
    }

    public static MethodComponents unmarshal(XMLScanner xs)
        throws UnmarshalException
    {
        return unmarshal(xs, newDispatcher());
    }

    public static MethodComponents unmarshal(XMLScanner xs, Dispatcher d)
        throws UnmarshalException
    {
        return ((MethodComponents) d.unmarshal(xs, (MethodComponents.class)));
    }

    public boolean equals(Object ob) {
        if (this == ob) {
            return true;
        }
        if (!(ob instanceof MethodComponents)) {
            return false;
        }
        MethodComponents tob = ((MethodComponents) ob);
        if (_NameComponent!= null) {
            if (tob._NameComponent == null) {
                return false;
            }
            if (!_NameComponent.equals(tob._NameComponent)) {
                return false;
            }
        } else {
            if (tob._NameComponent!= null) {
                return false;
            }
        }
        if (_ReturnComponent!= null) {
            if (tob._ReturnComponent == null) {
                return false;
            }
            if (!_ReturnComponent.equals(tob._ReturnComponent)) {
                return false;
            }
        } else {
            if (tob._ReturnComponent!= null) {
                return false;
            }
        }
        if (_CustomComponent!= null) {
            if (tob._CustomComponent == null) {
                return false;
            }
            if (!_CustomComponent.equals(tob._CustomComponent)) {
                return false;
            }
        } else {
            if (tob._CustomComponent!= null) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int h = 0;
        h = ((127 *h)+((_NameComponent!= null)?_NameComponent.hashCode(): 0));
        h = ((127 *h)+((_ReturnComponent!= null)?_ReturnComponent.hashCode(): 0));
        h = ((127 *h)+((_CustomComponent!= null)?_CustomComponent.hashCode(): 0));
        return h;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("<<MethodComponents");
        if (_NameComponent!= null) {
            sb.append(" NameComponent=");
            sb.append(_NameComponent.toString());
        }
        if (_ReturnComponent!= null) {
            sb.append(" ReturnComponent=");
            sb.append(_ReturnComponent.toString());
        }
        if (_CustomComponent!= null) {
            sb.append(" CustomComponent=");
            sb.append(_CustomComponent.toString());
        }
        sb.append(">>");
        return sb.toString();
    }

    public static Dispatcher newDispatcher() {
        return ClassComponents.newDispatcher();
    }


    private static class CustomComponentPredicate
        implements PredicatedLists.Predicate
    {


        public void check(Object ob) {
            if (!(ob instanceof CustomComponent)) {
                throw new InvalidContentObjectException(ob, (CustomComponent.class));
            }
        }

    }

}
