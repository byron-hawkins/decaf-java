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


public class TypeDefinitions
    extends MarshallableObject
    implements Element
{

    private List _TypeDefinition = PredicatedLists.createInvalidating(this, new TypeDefinitionPredicate(), new ArrayList());
    private PredicatedLists.Predicate pred_TypeDefinition = new TypeDefinitionPredicate();

    public List getTypeDefinition() {
        return _TypeDefinition;
    }

    public void deleteTypeDefinition() {
        _TypeDefinition = null;
        invalidate();
    }

    public void emptyTypeDefinition() {
        _TypeDefinition = PredicatedLists.createInvalidating(this, pred_TypeDefinition, new ArrayList());
    }

    public void validateThis()
        throws LocalValidationException
    {
    }

    public void validate(Validator v)
        throws StructureValidationException
    {
        for (Iterator i = _TypeDefinition.iterator(); i.hasNext(); ) {
            v.validate(((ValidatableObject) i.next()));
        }
    }

    public void marshal(Marshaller m)
        throws IOException
    {
        XMLWriter w = m.writer();
        w.start("TypeDefinitions");
        if (_TypeDefinition.size()> 0) {
            for (Iterator i = _TypeDefinition.iterator(); i.hasNext(); ) {
                m.marshal(((MarshallableObject) i.next()));
            }
        }
        w.end("TypeDefinitions");
    }

    public void unmarshal(Unmarshaller u)
        throws UnmarshalException
    {
        XMLScanner xs = u.scanner();
        Validator v = u.validator();
        xs.takeStart("TypeDefinitions");
        while (xs.atAttribute()) {
            String an = xs.takeAttributeName();
            throw new InvalidAttributeException(an);
        }
        {
            List l = PredicatedLists.create(this, pred_TypeDefinition, new ArrayList());
            while (xs.atStart("TypeDefinition")) {
                l.add(((TypeDefinition) u.unmarshal()));
            }
            _TypeDefinition = PredicatedLists.createInvalidating(this, pred_TypeDefinition, l);
        }
        xs.takeEnd("TypeDefinitions");
    }

    public static TypeDefinitions unmarshal(InputStream in)
        throws UnmarshalException
    {
        return unmarshal(XMLScanner.open(in));
    }

    public static TypeDefinitions unmarshal(XMLScanner xs)
        throws UnmarshalException
    {
        return unmarshal(xs, newDispatcher());
    }

    public static TypeDefinitions unmarshal(XMLScanner xs, Dispatcher d)
        throws UnmarshalException
    {
        return ((TypeDefinitions) d.unmarshal(xs, (TypeDefinitions.class)));
    }

    public boolean equals(Object ob) {
        if (this == ob) {
            return true;
        }
        if (!(ob instanceof TypeDefinitions)) {
            return false;
        }
        TypeDefinitions tob = ((TypeDefinitions) ob);
        if (_TypeDefinition!= null) {
            if (tob._TypeDefinition == null) {
                return false;
            }
            if (!_TypeDefinition.equals(tob._TypeDefinition)) {
                return false;
            }
        } else {
            if (tob._TypeDefinition!= null) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int h = 0;
        h = ((127 *h)+((_TypeDefinition!= null)?_TypeDefinition.hashCode(): 0));
        return h;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("<<TypeDefinitions");
        if (_TypeDefinition!= null) {
            sb.append(" TypeDefinition=");
            sb.append(_TypeDefinition.toString());
        }
        sb.append(">>");
        return sb.toString();
    }

    public static Dispatcher newDispatcher() {
        return ClassComponents.newDispatcher();
    }


    private static class TypeDefinitionPredicate
        implements PredicatedLists.Predicate
    {


        public void check(Object ob) {
            if (!(ob instanceof TypeDefinition)) {
                throw new InvalidContentObjectException(ob, (TypeDefinition.class));
            }
        }

    }

}
