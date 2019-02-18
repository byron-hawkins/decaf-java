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
import javax.xml.bind.ConversionException;
import javax.xml.bind.Dispatcher;
import javax.xml.bind.DuplicateAttributeException;
import javax.xml.bind.Element;
import javax.xml.bind.IdentifiableElement;
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


public class TypeDefinition
    extends MarshallableObject
    implements Element, IdentifiableElement
{

    private String _Classname;
    private String _Uid;
    private String _SuperMethods;
    private boolean isDefaulted_SuperMethods = true;
    private final static String DEFAULT_SUPERMETHODS = String.valueOf("DETAILED");
    private String _DeclaredMethods;
    private boolean isDefaulted_DeclaredMethods = true;
    private final static String DEFAULT_DECLAREDMETHODS = String.valueOf("ALL");
    private String _Discussion;
    private List _Field = PredicatedLists.createInvalidating(this, new FieldPredicate(), new ArrayList());
    private PredicatedLists.Predicate pred_Field = new FieldPredicate();
    private List _Method = PredicatedLists.createInvalidating(this, new MethodPredicate(), new ArrayList());
    private PredicatedLists.Predicate pred_Method = new MethodPredicate();

    public String getClassname() {
        return _Classname;
    }

    public void setClassname(String _Classname) {
        this._Classname = _Classname;
        if (_Classname == null) {
            invalidate();
        }
    }

    public String getUid() {
        return _Uid;
    }

    public void setUid(String _Uid) {
        this._Uid = _Uid;
        if (_Uid == null) {
            invalidate();
        }
    }

    public String id() {
        return _Uid.toString();
    }

    public boolean defaultedSuperMethods() {
        return (_SuperMethods!= null);
    }

    public String getSuperMethods() {
        if (_SuperMethods == null) {
            return DEFAULT_SUPERMETHODS;
        }
        return _SuperMethods;
    }

    public void setSuperMethods(String _SuperMethods) {
        this._SuperMethods = _SuperMethods;
        if (_SuperMethods == null) {
            invalidate();
        }
    }

    public boolean defaultedDeclaredMethods() {
        return (_DeclaredMethods!= null);
    }

    public String getDeclaredMethods() {
        if (_DeclaredMethods == null) {
            return DEFAULT_DECLAREDMETHODS;
        }
        return _DeclaredMethods;
    }

    public void setDeclaredMethods(String _DeclaredMethods) {
        this._DeclaredMethods = _DeclaredMethods;
        if (_DeclaredMethods == null) {
            invalidate();
        }
    }

    public String getDiscussion() {
        return _Discussion;
    }

    public void setDiscussion(String _Discussion) {
        this._Discussion = _Discussion;
        if (_Discussion == null) {
            invalidate();
        }
    }

    public List getField() {
        return _Field;
    }

    public void deleteField() {
        _Field = null;
        invalidate();
    }

    public void emptyField() {
        _Field = PredicatedLists.createInvalidating(this, pred_Field, new ArrayList());
    }

    public List getMethod() {
        return _Method;
    }

    public void deleteMethod() {
        _Method = null;
        invalidate();
    }

    public void emptyMethod() {
        _Method = PredicatedLists.createInvalidating(this, pred_Method, new ArrayList());
    }

    public void validateThis()
        throws LocalValidationException
    {
        if (_Classname == null) {
            throw new MissingAttributeException("Classname");
        }
        if (_Uid == null) {
            throw new MissingAttributeException("uid");
        }
    }

    public void validate(Validator v)
        throws StructureValidationException
    {
        for (Iterator i = _Field.iterator(); i.hasNext(); ) {
            v.validate(((ValidatableObject) i.next()));
        }
        for (Iterator i = _Method.iterator(); i.hasNext(); ) {
            v.validate(((ValidatableObject) i.next()));
        }
    }

    public void marshal(Marshaller m)
        throws IOException
    {
        XMLWriter w = m.writer();
        w.start("TypeDefinition");
        w.attribute("Classname", _Classname.toString());
        w.attribute("uid", _Uid.toString());
        if (_SuperMethods!= null) {
            w.attribute("superMethods", _SuperMethods.toString());
        }
        if (_DeclaredMethods!= null) {
            w.attribute("declaredMethods", _DeclaredMethods.toString());
        }
        if (_Discussion!= null) {
            w.leaf("Discussion", _Discussion.toString());
        }
        if (_Field.size()> 0) {
            for (Iterator i = _Field.iterator(); i.hasNext(); ) {
                m.marshal(((MarshallableObject) i.next()));
            }
        }
        if (_Method.size()> 0) {
            for (Iterator i = _Method.iterator(); i.hasNext(); ) {
                m.marshal(((MarshallableObject) i.next()));
            }
        }
        w.end("TypeDefinition");
    }

    public void unmarshal(Unmarshaller u)
        throws UnmarshalException
    {
        XMLScanner xs = u.scanner();
        Validator v = u.validator();
        xs.takeStart("TypeDefinition");
        while (xs.atAttribute()) {
            String an = xs.takeAttributeName();
            if (an.equals("Classname")) {
                if (_Classname!= null) {
                    throw new DuplicateAttributeException(an);
                }
                _Classname = xs.takeAttributeValue();
                continue;
            }
            if (an.equals("uid")) {
                if (_Uid!= null) {
                    throw new DuplicateAttributeException(an);
                }
                _Uid = xs.takeAttributeValue();
                continue;
            }
            if (an.equals("superMethods")) {
                if (_SuperMethods!= null) {
                    throw new DuplicateAttributeException(an);
                }
                _SuperMethods = xs.takeAttributeValue();
                continue;
            }
            if (an.equals("declaredMethods")) {
                if (_DeclaredMethods!= null) {
                    throw new DuplicateAttributeException(an);
                }
                _DeclaredMethods = xs.takeAttributeValue();
                continue;
            }
            throw new InvalidAttributeException(an);
        }
        if (xs.atStart("Discussion")) {
            xs.takeStart("Discussion");
            String s;
            if (xs.atChars(XMLScanner.WS_COLLAPSE)) {
                s = xs.takeChars(XMLScanner.WS_COLLAPSE);
            } else {
                s = "";
            }
            try {
                _Discussion = String.valueOf(s);
            } catch (Exception x) {
                throw new ConversionException("Discussion", x);
            }
            xs.takeEnd("Discussion");
        }
        {
            List l = PredicatedLists.create(this, pred_Field, new ArrayList());
            while (xs.atStart("Field")) {
                l.add(((Field) u.unmarshal()));
            }
            _Field = PredicatedLists.createInvalidating(this, pred_Field, l);
        }
        {
            List l = PredicatedLists.create(this, pred_Method, new ArrayList());
            while (xs.atStart("Method")) {
                l.add(((Method) u.unmarshal()));
            }
            _Method = PredicatedLists.createInvalidating(this, pred_Method, l);
        }
        xs.takeEnd("TypeDefinition");
    }

    public static TypeDefinition unmarshal(InputStream in)
        throws UnmarshalException
    {
        return unmarshal(XMLScanner.open(in));
    }

    public static TypeDefinition unmarshal(XMLScanner xs)
        throws UnmarshalException
    {
        return unmarshal(xs, newDispatcher());
    }

    public static TypeDefinition unmarshal(XMLScanner xs, Dispatcher d)
        throws UnmarshalException
    {
        return ((TypeDefinition) d.unmarshal(xs, (TypeDefinition.class)));
    }

    public boolean equals(Object ob) {
        if (this == ob) {
            return true;
        }
        if (!(ob instanceof TypeDefinition)) {
            return false;
        }
        TypeDefinition tob = ((TypeDefinition) ob);
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
        if (_SuperMethods!= null) {
            if (tob._SuperMethods == null) {
                return false;
            }
            if (!_SuperMethods.equals(tob._SuperMethods)) {
                return false;
            }
        } else {
            if (tob._SuperMethods!= null) {
                return false;
            }
        }
        if (_DeclaredMethods!= null) {
            if (tob._DeclaredMethods == null) {
                return false;
            }
            if (!_DeclaredMethods.equals(tob._DeclaredMethods)) {
                return false;
            }
        } else {
            if (tob._DeclaredMethods!= null) {
                return false;
            }
        }
        if (_Discussion!= null) {
            if (tob._Discussion == null) {
                return false;
            }
            if (!_Discussion.equals(tob._Discussion)) {
                return false;
            }
        } else {
            if (tob._Discussion!= null) {
                return false;
            }
        }
        if (_Field!= null) {
            if (tob._Field == null) {
                return false;
            }
            if (!_Field.equals(tob._Field)) {
                return false;
            }
        } else {
            if (tob._Field!= null) {
                return false;
            }
        }
        if (_Method!= null) {
            if (tob._Method == null) {
                return false;
            }
            if (!_Method.equals(tob._Method)) {
                return false;
            }
        } else {
            if (tob._Method!= null) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int h = 0;
        h = ((127 *h)+((_Classname!= null)?_Classname.hashCode(): 0));
        h = ((127 *h)+((_Uid!= null)?_Uid.hashCode(): 0));
        h = ((127 *h)+((_SuperMethods!= null)?_SuperMethods.hashCode(): 0));
        h = ((127 *h)+((_DeclaredMethods!= null)?_DeclaredMethods.hashCode(): 0));
        h = ((127 *h)+((_Discussion!= null)?_Discussion.hashCode(): 0));
        h = ((127 *h)+((_Field!= null)?_Field.hashCode(): 0));
        h = ((127 *h)+((_Method!= null)?_Method.hashCode(): 0));
        return h;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("<<TypeDefinition");
        if (_Classname!= null) {
            sb.append(" Classname=");
            sb.append(_Classname.toString());
        }
        if (_Uid!= null) {
            sb.append(" uid=");
            sb.append(_Uid.toString());
        }
        sb.append(" superMethods=");
        sb.append(getSuperMethods().toString());
        sb.append(" declaredMethods=");
        sb.append(getDeclaredMethods().toString());
        if (_Discussion!= null) {
            sb.append(" Discussion=");
            sb.append(_Discussion.toString());
        }
        if (_Field!= null) {
            sb.append(" Field=");
            sb.append(_Field.toString());
        }
        if (_Method!= null) {
            sb.append(" Method=");
            sb.append(_Method.toString());
        }
        sb.append(">>");
        return sb.toString();
    }

    public static Dispatcher newDispatcher() {
        return ClassComponents.newDispatcher();
    }


    private static class FieldPredicate
        implements PredicatedLists.Predicate
    {


        public void check(Object ob) {
            if (!(ob instanceof Field)) {
                throw new InvalidContentObjectException(ob, (Field.class));
            }
        }

    }


    private static class MethodPredicate
        implements PredicatedLists.Predicate
    {


        public void check(Object ob) {
            if (!(ob instanceof Method)) {
                throw new InvalidContentObjectException(ob, (Method.class));
            }
        }

    }

}
