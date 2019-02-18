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
import javax.xml.bind.Element;
import javax.xml.bind.InvalidAttributeException;
import javax.xml.bind.LocalValidationException;
import javax.xml.bind.MarshallableObject;
import javax.xml.bind.Marshaller;
import javax.xml.bind.MissingContentException;
import javax.xml.bind.StructureValidationException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Validator;
import javax.xml.marshal.XMLScanner;
import javax.xml.marshal.XMLWriter;


public class Setup
    extends MarshallableObject
    implements Element
{

    private String _InstallRoot;
    private String _BrowserCommand;
    private String _PluginsPackage;
    private String _CompileCommand;

    public String getInstallRoot() {
        return _InstallRoot;
    }

    public void setInstallRoot(String _InstallRoot) {
        this._InstallRoot = _InstallRoot;
        if (_InstallRoot == null) {
            invalidate();
        }
    }

    public String getBrowserCommand() {
        return _BrowserCommand;
    }

    public void setBrowserCommand(String _BrowserCommand) {
        this._BrowserCommand = _BrowserCommand;
        if (_BrowserCommand == null) {
            invalidate();
        }
    }

    public String getPluginsPackage() {
        return _PluginsPackage;
    }

    public void setPluginsPackage(String _PluginsPackage) {
        this._PluginsPackage = _PluginsPackage;
        if (_PluginsPackage == null) {
            invalidate();
        }
    }

    public String getCompileCommand() {
        return _CompileCommand;
    }

    public void setCompileCommand(String _CompileCommand) {
        this._CompileCommand = _CompileCommand;
        if (_CompileCommand == null) {
            invalidate();
        }
    }

    public void validateThis()
        throws LocalValidationException
    {
        if (_InstallRoot == null) {
            throw new MissingContentException("InstallRoot");
        }
        if (_BrowserCommand == null) {
            throw new MissingContentException("BrowserCommand");
        }
        if (_PluginsPackage == null) {
            throw new MissingContentException("PluginsPackage");
        }
        if (_CompileCommand == null) {
            throw new MissingContentException("CompileCommand");
        }
    }

    public void validate(Validator v)
        throws StructureValidationException
    {
    }

    public void marshal(Marshaller m)
        throws IOException
    {
        XMLWriter w = m.writer();
        w.start("Setup");
        w.leaf("InstallRoot", _InstallRoot.toString());
        w.leaf("BrowserCommand", _BrowserCommand.toString());
        w.leaf("PluginsPackage", _PluginsPackage.toString());
        w.leaf("CompileCommand", _CompileCommand.toString());
        w.end("Setup");
    }

    public void unmarshal(Unmarshaller u)
        throws UnmarshalException
    {
        XMLScanner xs = u.scanner();
        Validator v = u.validator();
        xs.takeStart("Setup");
        while (xs.atAttribute()) {
            String an = xs.takeAttributeName();
            throw new InvalidAttributeException(an);
        }
        if (xs.atStart("InstallRoot")) {
            xs.takeStart("InstallRoot");
            String s;
            if (xs.atChars(XMLScanner.WS_COLLAPSE)) {
                s = xs.takeChars(XMLScanner.WS_COLLAPSE);
            } else {
                s = "";
            }
            try {
                _InstallRoot = String.valueOf(s);
            } catch (Exception x) {
                throw new ConversionException("InstallRoot", x);
            }
            xs.takeEnd("InstallRoot");
        }
        if (xs.atStart("BrowserCommand")) {
            xs.takeStart("BrowserCommand");
            String s;
            if (xs.atChars(XMLScanner.WS_COLLAPSE)) {
                s = xs.takeChars(XMLScanner.WS_COLLAPSE);
            } else {
                s = "";
            }
            try {
                _BrowserCommand = String.valueOf(s);
            } catch (Exception x) {
                throw new ConversionException("BrowserCommand", x);
            }
            xs.takeEnd("BrowserCommand");
        }
        if (xs.atStart("PluginsPackage")) {
            xs.takeStart("PluginsPackage");
            String s;
            if (xs.atChars(XMLScanner.WS_COLLAPSE)) {
                s = xs.takeChars(XMLScanner.WS_COLLAPSE);
            } else {
                s = "";
            }
            try {
                _PluginsPackage = String.valueOf(s);
            } catch (Exception x) {
                throw new ConversionException("PluginsPackage", x);
            }
            xs.takeEnd("PluginsPackage");
        }
        if (xs.atStart("CompileCommand")) {
            xs.takeStart("CompileCommand");
            String s;
            if (xs.atChars(XMLScanner.WS_COLLAPSE)) {
                s = xs.takeChars(XMLScanner.WS_COLLAPSE);
            } else {
                s = "";
            }
            try {
                _CompileCommand = String.valueOf(s);
            } catch (Exception x) {
                throw new ConversionException("CompileCommand", x);
            }
            xs.takeEnd("CompileCommand");
        }
        xs.takeEnd("Setup");
    }

    public static Setup unmarshal(InputStream in)
        throws UnmarshalException
    {
        return unmarshal(XMLScanner.open(in));
    }

    public static Setup unmarshal(XMLScanner xs)
        throws UnmarshalException
    {
        return unmarshal(xs, newDispatcher());
    }

    public static Setup unmarshal(XMLScanner xs, Dispatcher d)
        throws UnmarshalException
    {
        return ((Setup) d.unmarshal(xs, (Setup.class)));
    }

    public boolean equals(Object ob) {
        if (this == ob) {
            return true;
        }
        if (!(ob instanceof Setup)) {
            return false;
        }
        Setup tob = ((Setup) ob);
        if (_InstallRoot!= null) {
            if (tob._InstallRoot == null) {
                return false;
            }
            if (!_InstallRoot.equals(tob._InstallRoot)) {
                return false;
            }
        } else {
            if (tob._InstallRoot!= null) {
                return false;
            }
        }
        if (_BrowserCommand!= null) {
            if (tob._BrowserCommand == null) {
                return false;
            }
            if (!_BrowserCommand.equals(tob._BrowserCommand)) {
                return false;
            }
        } else {
            if (tob._BrowserCommand!= null) {
                return false;
            }
        }
        if (_PluginsPackage!= null) {
            if (tob._PluginsPackage == null) {
                return false;
            }
            if (!_PluginsPackage.equals(tob._PluginsPackage)) {
                return false;
            }
        } else {
            if (tob._PluginsPackage!= null) {
                return false;
            }
        }
        if (_CompileCommand!= null) {
            if (tob._CompileCommand == null) {
                return false;
            }
            if (!_CompileCommand.equals(tob._CompileCommand)) {
                return false;
            }
        } else {
            if (tob._CompileCommand!= null) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int h = 0;
        h = ((127 *h)+((_InstallRoot!= null)?_InstallRoot.hashCode(): 0));
        h = ((127 *h)+((_BrowserCommand!= null)?_BrowserCommand.hashCode(): 0));
        h = ((127 *h)+((_PluginsPackage!= null)?_PluginsPackage.hashCode(): 0));
        h = ((127 *h)+((_CompileCommand!= null)?_CompileCommand.hashCode(): 0));
        return h;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("<<Setup");
        if (_InstallRoot!= null) {
            sb.append(" InstallRoot=");
            sb.append(_InstallRoot.toString());
        }
        if (_BrowserCommand!= null) {
            sb.append(" BrowserCommand=");
            sb.append(_BrowserCommand.toString());
        }
        if (_PluginsPackage!= null) {
            sb.append(" PluginsPackage=");
            sb.append(_PluginsPackage.toString());
        }
        if (_CompileCommand!= null) {
            sb.append(" CompileCommand=");
            sb.append(_CompileCommand.toString());
        }
        sb.append(">>");
        return sb.toString();
    }

    public static Dispatcher newDispatcher() {
        return ClassComponents.newDispatcher();
    }

}
