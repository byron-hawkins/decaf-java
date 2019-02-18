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
import javax.xml.bind.PredicatedLists;
import javax.xml.bind.PredicatedLists.Predicate;
import javax.xml.bind.StructureValidationException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidatableObject;
import javax.xml.bind.Validator;
import javax.xml.marshal.XMLScanner;
import javax.xml.marshal.XMLWriter;


public class TypeChooserContent
    extends MarshallableObject
    implements Element
{

    private List _TypeCategory = PredicatedLists.createInvalidating(this, new TypeCategoryPredicate(), new ArrayList());
    private PredicatedLists.Predicate pred_TypeCategory = new TypeCategoryPredicate();

    public List getTypeCategory() {
        return _TypeCategory;
    }

    public void deleteTypeCategory() {
        _TypeCategory = null;
        invalidate();
    }

    public void emptyTypeCategory() {
        _TypeCategory = PredicatedLists.createInvalidating(this, pred_TypeCategory, new ArrayList());
    }

    public void validateThis()
        throws LocalValidationException
    {
    }

    public void validate(Validator v)
        throws StructureValidationException
    {
        for (Iterator i = _TypeCategory.iterator(); i.hasNext(); ) {
            v.validate(((ValidatableObject) i.next()));
        }
    }

    public void marshal(Marshaller m)
        throws IOException
    {
        XMLWriter w = m.writer();
        w.start("TypeChooserContent");
        if (_TypeCategory.size()> 0) {
            for (Iterator i = _TypeCategory.iterator(); i.hasNext(); ) {
                m.marshal(((MarshallableObject) i.next()));
            }
        }
        w.end("TypeChooserContent");
    }

    public void unmarshal(Unmarshaller u)
        throws UnmarshalException
    {
        XMLScanner xs = u.scanner();
        Validator v = u.validator();
        xs.takeStart("TypeChooserContent");
        while (xs.atAttribute()) {
            String an = xs.takeAttributeName();
            throw new InvalidAttributeException(an);
        }
        {
            List l = PredicatedLists.create(this, pred_TypeCategory, new ArrayList());
            while (xs.atStart("TypeCategory")) {
                l.add(((TypeCategory) u.unmarshal()));
            }
            _TypeCategory = PredicatedLists.createInvalidating(this, pred_TypeCategory, l);
        }
        xs.takeEnd("TypeChooserContent");
    }

    public static TypeChooserContent unmarshal(InputStream in)
        throws UnmarshalException
    {
        return unmarshal(XMLScanner.open(in));
    }

    public static TypeChooserContent unmarshal(XMLScanner xs)
        throws UnmarshalException
    {
        return unmarshal(xs, newDispatcher());
    }

    public static TypeChooserContent unmarshal(XMLScanner xs, Dispatcher d)
        throws UnmarshalException
    {
        return ((TypeChooserContent) d.unmarshal(xs, (TypeChooserContent.class)));
    }

    public boolean equals(Object ob) {
        if (this == ob) {
            return true;
        }
        if (!(ob instanceof TypeChooserContent)) {
            return false;
        }
        TypeChooserContent tob = ((TypeChooserContent) ob);
        if (_TypeCategory!= null) {
            if (tob._TypeCategory == null) {
                return false;
            }
            if (!_TypeCategory.equals(tob._TypeCategory)) {
                return false;
            }
        } else {
            if (tob._TypeCategory!= null) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int h = 0;
        h = ((127 *h)+((_TypeCategory!= null)?_TypeCategory.hashCode(): 0));
        return h;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("<<TypeChooserContent");
        if (_TypeCategory!= null) {
            sb.append(" TypeCategory=");
            sb.append(_TypeCategory.toString());
        }
        sb.append(">>");
        return sb.toString();
    }

    public static Dispatcher newDispatcher() {
        return ClassComponents.newDispatcher();
    }


    private static class TypeCategoryPredicate
        implements PredicatedLists.Predicate
    {


        public void check(Object ob) {
            if (!(ob instanceof TypeCategory)) {
                throw new InvalidContentObjectException(ob, (TypeCategory.class));
            }
        }

    }

}
