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
import javax.xml.bind.Validator;
import javax.xml.marshal.XMLScanner;
import javax.xml.marshal.XMLWriter;


public class Grammars
    extends MarshallableObject
    implements Element
{

    private List _Phrase = PredicatedLists.createInvalidating(this, new PhrasePredicate(), new ArrayList());
    private PredicatedLists.Predicate pred_Phrase = new PhrasePredicate();
    private List _Sentence = PredicatedLists.createInvalidating(this, new SentencePredicate(), new ArrayList());
    private PredicatedLists.Predicate pred_Sentence = new SentencePredicate();

    public List getPhrase() {
        return _Phrase;
    }

    public void deletePhrase() {
        _Phrase = null;
        invalidate();
    }

    public void emptyPhrase() {
        _Phrase = PredicatedLists.createInvalidating(this, pred_Phrase, new ArrayList());
    }

    public List getSentence() {
        return _Sentence;
    }

    public void deleteSentence() {
        _Sentence = null;
        invalidate();
    }

    public void emptySentence() {
        _Sentence = PredicatedLists.createInvalidating(this, pred_Sentence, new ArrayList());
    }

    public void validateThis()
        throws LocalValidationException
    {
    }

    public void validate(Validator v)
        throws StructureValidationException
    {
    }

    public void marshal(Marshaller m)
        throws IOException
    {
        XMLWriter w = m.writer();
        w.start("Grammars");
        for (Iterator i = _Phrase.iterator(); i.hasNext(); ) {
            w.leaf("Phrase", ((String) i.next()).toString());
        }
        for (Iterator i = _Sentence.iterator(); i.hasNext(); ) {
            w.leaf("Sentence", ((String) i.next()).toString());
        }
        w.end("Grammars");
    }

    public void unmarshal(Unmarshaller u)
        throws UnmarshalException
    {
        XMLScanner xs = u.scanner();
        Validator v = u.validator();
        xs.takeStart("Grammars");
        while (xs.atAttribute()) {
            String an = xs.takeAttributeName();
            throw new InvalidAttributeException(an);
        }
        {
            List l = new ArrayList();
            while (xs.atStart()) {
                if (xs.atStart("Phrase")) {
                    xs.takeStart("Phrase");
                    String s;
                    if (xs.atChars(XMLScanner.WS_COLLAPSE)) {
                        s = xs.takeChars(XMLScanner.WS_COLLAPSE);
                    } else {
                        s = "";
                    }
                    String uf;
                    try {
                        uf = String.valueOf(s);
                    } catch (Exception x) {
                        throw new ConversionException("Phrase", x);
                    }
                    l.add(uf);
                    xs.takeEnd("Phrase");
                } else {
                    break;
                }
            }
            _Phrase = PredicatedLists.createInvalidating(this, pred_Phrase, l);
        }
        {
            List l = new ArrayList();
            while (xs.atStart()) {
                if (xs.atStart("Sentence")) {
                    xs.takeStart("Sentence");
                    String s;
                    if (xs.atChars(XMLScanner.WS_COLLAPSE)) {
                        s = xs.takeChars(XMLScanner.WS_COLLAPSE);
                    } else {
                        s = "";
                    }
                    String uf;
                    try {
                        uf = String.valueOf(s);
                    } catch (Exception x) {
                        throw new ConversionException("Sentence", x);
                    }
                    l.add(uf);
                    xs.takeEnd("Sentence");
                } else {
                    break;
                }
            }
            _Sentence = PredicatedLists.createInvalidating(this, pred_Sentence, l);
        }
        xs.takeEnd("Grammars");
    }

    public static Grammars unmarshal(InputStream in)
        throws UnmarshalException
    {
        return unmarshal(XMLScanner.open(in));
    }

    public static Grammars unmarshal(XMLScanner xs)
        throws UnmarshalException
    {
        return unmarshal(xs, newDispatcher());
    }

    public static Grammars unmarshal(XMLScanner xs, Dispatcher d)
        throws UnmarshalException
    {
        return ((Grammars) d.unmarshal(xs, (Grammars.class)));
    }

    public boolean equals(Object ob) {
        if (this == ob) {
            return true;
        }
        if (!(ob instanceof Grammars)) {
            return false;
        }
        Grammars tob = ((Grammars) ob);
        if (_Phrase!= null) {
            if (tob._Phrase == null) {
                return false;
            }
            if (!_Phrase.equals(tob._Phrase)) {
                return false;
            }
        } else {
            if (tob._Phrase!= null) {
                return false;
            }
        }
        if (_Sentence!= null) {
            if (tob._Sentence == null) {
                return false;
            }
            if (!_Sentence.equals(tob._Sentence)) {
                return false;
            }
        } else {
            if (tob._Sentence!= null) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int h = 0;
        h = ((127 *h)+((_Phrase!= null)?_Phrase.hashCode(): 0));
        h = ((127 *h)+((_Sentence!= null)?_Sentence.hashCode(): 0));
        return h;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("<<Grammars");
        if (_Phrase!= null) {
            sb.append(" Phrase=");
            sb.append(_Phrase.toString());
        }
        if (_Sentence!= null) {
            sb.append(" Sentence=");
            sb.append(_Sentence.toString());
        }
        sb.append(">>");
        return sb.toString();
    }

    public static Dispatcher newDispatcher() {
        return ClassComponents.newDispatcher();
    }


    private static class PhrasePredicate
        implements PredicatedLists.Predicate
    {


        public void check(Object ob) {
            if (!(ob instanceof String)) {
                throw new InvalidContentObjectException(ob, (String.class));
            }
        }

    }


    private static class SentencePredicate
        implements PredicatedLists.Predicate
    {


        public void check(Object ob) {
            if (!(ob instanceof String)) {
                throw new InvalidContentObjectException(ob, (String.class));
            }
        }

    }

}
