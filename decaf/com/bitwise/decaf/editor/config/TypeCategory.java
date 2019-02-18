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
import javax.xml.bind.DuplicateAttributeException;
import javax.xml.bind.Element;
import javax.xml.bind.InvalidAttributeException;
import javax.xml.bind.InvalidContentObjectException;
import javax.xml.bind.LocalValidationException;
import javax.xml.bind.MarshallableObject;
import javax.xml.bind.Marshaller;
import javax.xml.bind.MissingAttributeException;
import javax.xml.bind.PredicatedLists;
import javax.xml.bind.PredicatedLists.Predicate;
import javax.xml.bind.StructureValidationException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidatableObject;
import javax.xml.bind.Validator;
import javax.xml.marshal.XMLScanner;
import javax.xml.marshal.XMLWriter;


public class TypeCategory
    extends MarshallableObject
    implements Element
{

    private String _Name;
    private List _TypeItem = PredicatedLists.createInvalidating(this, new TypeItemPredicate(), new ArrayList());
    private PredicatedLists.Predicate pred_TypeItem = new TypeItemPredicate();

    public String getName() {
        return _Name;
    }

    public void setName(String _Name) {
        this._Name = _Name;
        if (_Name == null) {
            invalidate();
        }
    }

    public List getTypeItem() {
        return _TypeItem;
    }

    public void deleteTypeItem() {
        _TypeItem = null;
        invalidate();
    }

    public void emptyTypeItem() {
        _TypeItem = PredicatedLists.createInvalidating(this, pred_TypeItem, new ArrayList());
    }

    public void validateThis()
        throws LocalValidationException
    {
        if (_Name == null) {
            throw new MissingAttributeException("name");
        }
    }

    public void validate(Validator v)
        throws StructureValidationException
    {
        for (Iterator i = _TypeItem.iterator(); i.hasNext(); ) {
            v.validate(((ValidatableObject) i.next()));
        }
    }

    public void marshal(Marshaller m)
        throws IOException
    {
        XMLWriter w = m.writer();
        w.start("TypeCategory");
        w.attribute("name", _Name.toString());
        if (_TypeItem.size()> 0) {
            for (Iterator i = _TypeItem.iterator(); i.hasNext(); ) {
                m.marshal(((MarshallableObject) i.next()));
            }
        }
        w.end("TypeCategory");
    }

    public void unmarshal(Unmarshaller u)
        throws UnmarshalException
    {
        XMLScanner xs = u.scanner();
        Validator v = u.validator();
        xs.takeStart("TypeCategory");
        while (xs.atAttribute()) {
            String an = xs.takeAttributeName();
            if (an.equals("name")) {
                if (_Name!= null) {
                    throw new DuplicateAttributeException(an);
                }
                _Name = xs.takeAttributeValue();
                continue;
            }
            throw new InvalidAttributeException(an);
        }
        {
            List l = PredicatedLists.create(this, pred_TypeItem, new ArrayList());
            while (xs.atStart("TypeItem")) {
                l.add(((TypeItem) u.unmarshal()));
            }
            _TypeItem = PredicatedLists.createInvalidating(this, pred_TypeItem, l);
        }
        xs.takeEnd("TypeCategory");
    }

    public static TypeCategory unmarshal(InputStream in)
        throws UnmarshalException
    {
        return unmarshal(XMLScanner.open(in));
    }

    public static TypeCategory unmarshal(XMLScanner xs)
        throws UnmarshalException
    {
        return unmarshal(xs, newDispatcher());
    }

    public static TypeCategory unmarshal(XMLScanner xs, Dispatcher d)
        throws UnmarshalException
    {
        return ((TypeCategory) d.unmarshal(xs, (TypeCategory.class)));
    }

    public boolean equals(Object ob) {
        if (this == ob) {
            return true;
        }
        if (!(ob instanceof TypeCategory)) {
            return false;
        }
        TypeCategory tob = ((TypeCategory) ob);
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
        if (_TypeItem!= null) {
            if (tob._TypeItem == null) {
                return false;
            }
            if (!_TypeItem.equals(tob._TypeItem)) {
                return false;
            }
        } else {
            if (tob._TypeItem!= null) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int h = 0;
        h = ((127 *h)+((_Name!= null)?_Name.hashCode(): 0));
        h = ((127 *h)+((_TypeItem!= null)?_TypeItem.hashCode(): 0));
        return h;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("<<TypeCategory");
        if (_Name!= null) {
            sb.append(" name=");
            sb.append(_Name.toString());
        }
        if (_TypeItem!= null) {
            sb.append(" TypeItem=");
            sb.append(_TypeItem.toString());
        }
        sb.append(">>");
        return sb.toString();
    }

    public static Dispatcher newDispatcher() {
        return ClassComponents.newDispatcher();
    }


    private static class TypeItemPredicate
        implements PredicatedLists.Predicate
    {


        public void check(Object ob) {
            if (!(ob instanceof TypeItem)) {
                throw new InvalidContentObjectException(ob, (TypeItem.class));
            }
        }

    }

}
