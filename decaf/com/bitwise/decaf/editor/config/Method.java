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


public class Method
    extends MarshallableObject
    implements Element
{

    private String _Name;
    private String _Hide;
    private boolean isDefaulted_Hide = true;
    private final static String DEFAULT_HIDE = String.valueOf("false");
    private String _Discussion;
    private List _Parameter = PredicatedLists.createInvalidating(this, new ParameterPredicate(), new ArrayList());
    private PredicatedLists.Predicate pred_Parameter = new ParameterPredicate();

    public String getName() {
        return _Name;
    }

    public void setName(String _Name) {
        this._Name = _Name;
        if (_Name == null) {
            invalidate();
        }
    }

    public boolean defaultedHide() {
        return (_Hide!= null);
    }

    public String getHide() {
        if (_Hide == null) {
            return DEFAULT_HIDE;
        }
        return _Hide;
    }

    public void setHide(String _Hide) {
        this._Hide = _Hide;
        if (_Hide == null) {
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

    public List getParameter() {
        return _Parameter;
    }

    public void deleteParameter() {
        _Parameter = null;
        invalidate();
    }

    public void emptyParameter() {
        _Parameter = PredicatedLists.createInvalidating(this, pred_Parameter, new ArrayList());
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
        for (Iterator i = _Parameter.iterator(); i.hasNext(); ) {
            v.validate(((ValidatableObject) i.next()));
        }
    }

    public void marshal(Marshaller m)
        throws IOException
    {
        XMLWriter w = m.writer();
        w.start("Method");
        w.attribute("name", _Name.toString());
        if (_Hide!= null) {
            w.attribute("hide", _Hide.toString());
        }
        if (_Discussion!= null) {
            w.leaf("Discussion", _Discussion.toString());
        }
        if (_Parameter.size()> 0) {
            for (Iterator i = _Parameter.iterator(); i.hasNext(); ) {
                m.marshal(((MarshallableObject) i.next()));
            }
        }
        w.end("Method");
    }

    public void unmarshal(Unmarshaller u)
        throws UnmarshalException
    {
        XMLScanner xs = u.scanner();
        Validator v = u.validator();
        xs.takeStart("Method");
        while (xs.atAttribute()) {
            String an = xs.takeAttributeName();
            if (an.equals("name")) {
                if (_Name!= null) {
                    throw new DuplicateAttributeException(an);
                }
                _Name = xs.takeAttributeValue();
                continue;
            }
            if (an.equals("hide")) {
                if (_Hide!= null) {
                    throw new DuplicateAttributeException(an);
                }
                _Hide = xs.takeAttributeValue();
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
            List l = PredicatedLists.create(this, pred_Parameter, new ArrayList());
            while (xs.atStart("Parameter")) {
                l.add(((Parameter) u.unmarshal()));
            }
            _Parameter = PredicatedLists.createInvalidating(this, pred_Parameter, l);
        }
        xs.takeEnd("Method");
    }

    public static Method unmarshal(InputStream in)
        throws UnmarshalException
    {
        return unmarshal(XMLScanner.open(in));
    }

    public static Method unmarshal(XMLScanner xs)
        throws UnmarshalException
    {
        return unmarshal(xs, newDispatcher());
    }

    public static Method unmarshal(XMLScanner xs, Dispatcher d)
        throws UnmarshalException
    {
        return ((Method) d.unmarshal(xs, (Method.class)));
    }

    public boolean equals(Object ob) {
        if (this == ob) {
            return true;
        }
        if (!(ob instanceof Method)) {
            return false;
        }
        Method tob = ((Method) ob);
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
        if (_Hide!= null) {
            if (tob._Hide == null) {
                return false;
            }
            if (!_Hide.equals(tob._Hide)) {
                return false;
            }
        } else {
            if (tob._Hide!= null) {
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
        if (_Parameter!= null) {
            if (tob._Parameter == null) {
                return false;
            }
            if (!_Parameter.equals(tob._Parameter)) {
                return false;
            }
        } else {
            if (tob._Parameter!= null) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int h = 0;
        h = ((127 *h)+((_Name!= null)?_Name.hashCode(): 0));
        h = ((127 *h)+((_Hide!= null)?_Hide.hashCode(): 0));
        h = ((127 *h)+((_Discussion!= null)?_Discussion.hashCode(): 0));
        h = ((127 *h)+((_Parameter!= null)?_Parameter.hashCode(): 0));
        return h;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("<<Method");
        if (_Name!= null) {
            sb.append(" name=");
            sb.append(_Name.toString());
        }
        sb.append(" hide=");
        sb.append(getHide().toString());
        if (_Discussion!= null) {
            sb.append(" Discussion=");
            sb.append(_Discussion.toString());
        }
        if (_Parameter!= null) {
            sb.append(" Parameter=");
            sb.append(_Parameter.toString());
        }
        sb.append(">>");
        return sb.toString();
    }

    public static Dispatcher newDispatcher() {
        return ClassComponents.newDispatcher();
    }


    private static class ParameterPredicate
        implements PredicatedLists.Predicate
    {


        public void check(Object ob) {
            if (!(ob instanceof Parameter)) {
                throw new InvalidContentObjectException(ob, (Parameter.class));
            }
        }

    }

}
