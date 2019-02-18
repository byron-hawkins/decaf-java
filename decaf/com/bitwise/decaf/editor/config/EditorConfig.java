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
import javax.xml.bind.InvalidAttributeException;
import javax.xml.bind.LocalValidationException;
import javax.xml.bind.MarshallableRootElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.MissingContentException;
import javax.xml.bind.RootElement;
import javax.xml.bind.StructureValidationException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Validator;
import javax.xml.marshal.XMLScanner;
import javax.xml.marshal.XMLWriter;


public class EditorConfig
    extends MarshallableRootElement
    implements RootElement
{

    private Setup _Setup;
    private ClassComponents _ClassComponents;
    private MethodComponents _MethodComponents;
    private ParameterComponents _ParameterComponents;
    private FieldComponents _FieldComponents;
    private LocalComponents _LocalComponents;
    private Grammars _Grammars;
    private TypeChooserContent _TypeChooserContent;
    private TypeDefinitions _TypeDefinitions;

    public Setup getSetup() {
        return _Setup;
    }

    public void setSetup(Setup _Setup) {
        this._Setup = _Setup;
        if (_Setup == null) {
            invalidate();
        }
    }

    public ClassComponents getClassComponents() {
        return _ClassComponents;
    }

    public void setClassComponents(ClassComponents _ClassComponents) {
        this._ClassComponents = _ClassComponents;
        if (_ClassComponents == null) {
            invalidate();
        }
    }

    public MethodComponents getMethodComponents() {
        return _MethodComponents;
    }

    public void setMethodComponents(MethodComponents _MethodComponents) {
        this._MethodComponents = _MethodComponents;
        if (_MethodComponents == null) {
            invalidate();
        }
    }

    public ParameterComponents getParameterComponents() {
        return _ParameterComponents;
    }

    public void setParameterComponents(ParameterComponents _ParameterComponents) {
        this._ParameterComponents = _ParameterComponents;
        if (_ParameterComponents == null) {
            invalidate();
        }
    }

    public FieldComponents getFieldComponents() {
        return _FieldComponents;
    }

    public void setFieldComponents(FieldComponents _FieldComponents) {
        this._FieldComponents = _FieldComponents;
        if (_FieldComponents == null) {
            invalidate();
        }
    }

    public LocalComponents getLocalComponents() {
        return _LocalComponents;
    }

    public void setLocalComponents(LocalComponents _LocalComponents) {
        this._LocalComponents = _LocalComponents;
        if (_LocalComponents == null) {
            invalidate();
        }
    }

    public Grammars getGrammars() {
        return _Grammars;
    }

    public void setGrammars(Grammars _Grammars) {
        this._Grammars = _Grammars;
        if (_Grammars == null) {
            invalidate();
        }
    }

    public TypeChooserContent getTypeChooserContent() {
        return _TypeChooserContent;
    }

    public void setTypeChooserContent(TypeChooserContent _TypeChooserContent) {
        this._TypeChooserContent = _TypeChooserContent;
        if (_TypeChooserContent == null) {
            invalidate();
        }
    }

    public TypeDefinitions getTypeDefinitions() {
        return _TypeDefinitions;
    }

    public void setTypeDefinitions(TypeDefinitions _TypeDefinitions) {
        this._TypeDefinitions = _TypeDefinitions;
        if (_TypeDefinitions == null) {
            invalidate();
        }
    }

    public void validateThis()
        throws LocalValidationException
    {
        if (_Setup == null) {
            throw new MissingContentException("Setup");
        }
        if (_ClassComponents == null) {
            throw new MissingContentException("ClassComponents");
        }
        if (_MethodComponents == null) {
            throw new MissingContentException("MethodComponents");
        }
    }

    public void validate(Validator v)
        throws StructureValidationException
    {
        v.validate(_Setup);
        v.validate(_ClassComponents);
        v.validate(_MethodComponents);
        v.validate(_ParameterComponents);
        v.validate(_FieldComponents);
        v.validate(_LocalComponents);
        v.validate(_Grammars);
        v.validate(_TypeChooserContent);
        v.validate(_TypeDefinitions);
    }

    public void marshal(Marshaller m)
        throws IOException
    {
        XMLWriter w = m.writer();
        w.start("EditorConfig");
        m.marshal(_Setup);
        m.marshal(_ClassComponents);
        m.marshal(_MethodComponents);
        if (_ParameterComponents!= null) {
            m.marshal(_ParameterComponents);
        }
        if (_FieldComponents!= null) {
            m.marshal(_FieldComponents);
        }
        if (_LocalComponents!= null) {
            m.marshal(_LocalComponents);
        }
        if (_Grammars!= null) {
            m.marshal(_Grammars);
        }
        if (_TypeChooserContent!= null) {
            m.marshal(_TypeChooserContent);
        }
        if (_TypeDefinitions!= null) {
            m.marshal(_TypeDefinitions);
        }
        w.end("EditorConfig");
    }

    public void unmarshal(Unmarshaller u)
        throws UnmarshalException
    {
        XMLScanner xs = u.scanner();
        Validator v = u.validator();
        xs.takeStart("EditorConfig");
        while (xs.atAttribute()) {
            String an = xs.takeAttributeName();
            throw new InvalidAttributeException(an);
        }
        _Setup = ((Setup) u.unmarshal());
        _ClassComponents = ((ClassComponents) u.unmarshal());
        _MethodComponents = ((MethodComponents) u.unmarshal());
        if (xs.atStart("ParameterComponents")) {
            _ParameterComponents = ((ParameterComponents) u.unmarshal());
        }
        if (xs.atStart("FieldComponents")) {
            _FieldComponents = ((FieldComponents) u.unmarshal());
        }
        if (xs.atStart("LocalComponents")) {
            _LocalComponents = ((LocalComponents) u.unmarshal());
        }
        if (xs.atStart("Grammars")) {
            _Grammars = ((Grammars) u.unmarshal());
        }
        if (xs.atStart("TypeChooserContent")) {
            _TypeChooserContent = ((TypeChooserContent) u.unmarshal());
        }
        if (xs.atStart("TypeDefinitions")) {
            _TypeDefinitions = ((TypeDefinitions) u.unmarshal());
        }
        xs.takeEnd("EditorConfig");
    }

    public static EditorConfig unmarshal(InputStream in)
        throws UnmarshalException
    {
        return unmarshal(XMLScanner.open(in));
    }

    public static EditorConfig unmarshal(XMLScanner xs)
        throws UnmarshalException
    {
        return unmarshal(xs, newDispatcher());
    }

    public static EditorConfig unmarshal(XMLScanner xs, Dispatcher d)
        throws UnmarshalException
    {
        return ((EditorConfig) d.unmarshal(xs, (EditorConfig.class)));
    }

    public boolean equals(Object ob) {
        if (this == ob) {
            return true;
        }
        if (!(ob instanceof EditorConfig)) {
            return false;
        }
        EditorConfig tob = ((EditorConfig) ob);
        if (_Setup!= null) {
            if (tob._Setup == null) {
                return false;
            }
            if (!_Setup.equals(tob._Setup)) {
                return false;
            }
        } else {
            if (tob._Setup!= null) {
                return false;
            }
        }
        if (_ClassComponents!= null) {
            if (tob._ClassComponents == null) {
                return false;
            }
            if (!_ClassComponents.equals(tob._ClassComponents)) {
                return false;
            }
        } else {
            if (tob._ClassComponents!= null) {
                return false;
            }
        }
        if (_MethodComponents!= null) {
            if (tob._MethodComponents == null) {
                return false;
            }
            if (!_MethodComponents.equals(tob._MethodComponents)) {
                return false;
            }
        } else {
            if (tob._MethodComponents!= null) {
                return false;
            }
        }
        if (_ParameterComponents!= null) {
            if (tob._ParameterComponents == null) {
                return false;
            }
            if (!_ParameterComponents.equals(tob._ParameterComponents)) {
                return false;
            }
        } else {
            if (tob._ParameterComponents!= null) {
                return false;
            }
        }
        if (_FieldComponents!= null) {
            if (tob._FieldComponents == null) {
                return false;
            }
            if (!_FieldComponents.equals(tob._FieldComponents)) {
                return false;
            }
        } else {
            if (tob._FieldComponents!= null) {
                return false;
            }
        }
        if (_LocalComponents!= null) {
            if (tob._LocalComponents == null) {
                return false;
            }
            if (!_LocalComponents.equals(tob._LocalComponents)) {
                return false;
            }
        } else {
            if (tob._LocalComponents!= null) {
                return false;
            }
        }
        if (_Grammars!= null) {
            if (tob._Grammars == null) {
                return false;
            }
            if (!_Grammars.equals(tob._Grammars)) {
                return false;
            }
        } else {
            if (tob._Grammars!= null) {
                return false;
            }
        }
        if (_TypeChooserContent!= null) {
            if (tob._TypeChooserContent == null) {
                return false;
            }
            if (!_TypeChooserContent.equals(tob._TypeChooserContent)) {
                return false;
            }
        } else {
            if (tob._TypeChooserContent!= null) {
                return false;
            }
        }
        if (_TypeDefinitions!= null) {
            if (tob._TypeDefinitions == null) {
                return false;
            }
            if (!_TypeDefinitions.equals(tob._TypeDefinitions)) {
                return false;
            }
        } else {
            if (tob._TypeDefinitions!= null) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int h = 0;
        h = ((127 *h)+((_Setup!= null)?_Setup.hashCode(): 0));
        h = ((127 *h)+((_ClassComponents!= null)?_ClassComponents.hashCode(): 0));
        h = ((127 *h)+((_MethodComponents!= null)?_MethodComponents.hashCode(): 0));
        h = ((127 *h)+((_ParameterComponents!= null)?_ParameterComponents.hashCode(): 0));
        h = ((127 *h)+((_FieldComponents!= null)?_FieldComponents.hashCode(): 0));
        h = ((127 *h)+((_LocalComponents!= null)?_LocalComponents.hashCode(): 0));
        h = ((127 *h)+((_Grammars!= null)?_Grammars.hashCode(): 0));
        h = ((127 *h)+((_TypeChooserContent!= null)?_TypeChooserContent.hashCode(): 0));
        h = ((127 *h)+((_TypeDefinitions!= null)?_TypeDefinitions.hashCode(): 0));
        return h;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("<<EditorConfig");
        if (_Setup!= null) {
            sb.append(" Setup=");
            sb.append(_Setup.toString());
        }
        if (_ClassComponents!= null) {
            sb.append(" ClassComponents=");
            sb.append(_ClassComponents.toString());
        }
        if (_MethodComponents!= null) {
            sb.append(" MethodComponents=");
            sb.append(_MethodComponents.toString());
        }
        if (_ParameterComponents!= null) {
            sb.append(" ParameterComponents=");
            sb.append(_ParameterComponents.toString());
        }
        if (_FieldComponents!= null) {
            sb.append(" FieldComponents=");
            sb.append(_FieldComponents.toString());
        }
        if (_LocalComponents!= null) {
            sb.append(" LocalComponents=");
            sb.append(_LocalComponents.toString());
        }
        if (_Grammars!= null) {
            sb.append(" Grammars=");
            sb.append(_Grammars.toString());
        }
        if (_TypeChooserContent!= null) {
            sb.append(" TypeChooserContent=");
            sb.append(_TypeChooserContent.toString());
        }
        if (_TypeDefinitions!= null) {
            sb.append(" TypeDefinitions=");
            sb.append(_TypeDefinitions.toString());
        }
        sb.append(">>");
        return sb.toString();
    }

    public static Dispatcher newDispatcher() {
        return ClassComponents.newDispatcher();
    }

}
