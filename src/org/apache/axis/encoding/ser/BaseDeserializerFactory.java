/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Axis" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.axis.encoding.ser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import javax.xml.rpc.namespace.QName;
import javax.xml.rpc.JAXRPCException;

import java.io.IOException;
import java.util.Vector;
import java.util.Iterator;

import org.apache.axis.Constants;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.encoding.SerializerFactory;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.DeserializerFactory;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.DeserializerImpl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Base class for Axis Deserialization Factory classes for code reuse
 *
 * @author Rich Scheuerle <scheu@us.ibm.com>
 */
public abstract class BaseDeserializerFactory implements DeserializerFactory {

    static Vector mechanisms = null;
    
    protected Class deserClass = null;
    protected boolean share = false;
    protected Deserializer deser = null;
    protected QName xmlType = null;
    protected Class javaType = null;
    protected Constructor dserClassConstructor = null;

    /**
     * Constructor
     * @param deserClass is the class of the Deserializer
     * @param share indicates if deserializers can be shared...i.e. getDeserializerAs 
     * will always return the same deserializer object if share is true.  Sharing is
     * only valid for xml primitives.
     */
    public BaseDeserializerFactory(Class deserClass, boolean share) {
        this.deserClass = deserClass;
        this.share = share;
    }
    public BaseDeserializerFactory(Class deserClass, boolean share, QName xmlType, Class javaType) {
        this.deserClass = deserClass;
        this.share = share;
        this.xmlType = xmlType;
        this.javaType = javaType;
    }

    public javax.xml.rpc.encoding.Deserializer getDeserializerAs(String mechanismType)
        throws JAXRPCException {
        // Need to add code to check against mechanisms vector.
        if (share && deser != null) {
            return deser;
        }
        // First try to new up the deserializer with qname, class arguments
        deser = null;
        if (javaType != null && xmlType != null) {
            try {
                if (dserClassConstructor == null) {
                    dserClassConstructor = 
                        deserClass.getConstructor(new Class[] {Class.class, QName.class});
                }
                deser = (Deserializer) 
                    dserClassConstructor.newInstance(new Object[] {javaType, xmlType});
            } catch (Exception e) {}
        }

        // If not successfull, try newInstance
        if (deser == null) {
            try {
                deser = (Deserializer) deserClass.newInstance();
            } catch (Exception e) {}
        }
        if (deser == null) {
            throw new JAXRPCException();
        }
        return deser;
    }

    /**
     * Returns a list of all XML processing mechanism types supported by this DeserializerFactory.
     *
     * @return List of unique identifiers for the supported XML processing mechanism types
     */
    public Iterator getSupportedMechanismTypes() {
        if (mechanisms == null) {
            mechanisms = new Vector();
            mechanisms.add(Constants.AXIS_SAX);
        }
        return mechanisms.iterator();
    }

    /**
     * Utility method that intospects on a factory class to decide how to 
     * create the factory.  Tries in the following order:
     * public static create(Class javaType, QName xmlType)
     * public <constructor>(Class javaType, QName xmlType)
     * public <constructor>()
     * @param factory class
     * @param QName xmlType
     * @param Class javaType
     */
    public static DeserializerFactory createFactory(Class factory, Class javaType, QName xmlType) {

        DeserializerFactory df = null;
        try {
            Method method = factory.getMethod("create", new Class[] {Class.class, QName.class});
            df = (DeserializerFactory) method.invoke(null, new Object[] {javaType, xmlType});
        } catch (Exception e) {}

        if (df == null) {
            try {
                Constructor constructor =  
                    factory.getConstructor(new Class[] {Class.class, QName.class});
                df = (DeserializerFactory) 
                    constructor.newInstance(new Object[] {javaType, xmlType});
            } catch (Exception e) {}
        }
        
        if (df == null) {
            try {
                df = (DeserializerFactory) factory.newInstance();
            } catch (Exception e) {}
        }
        return df;
    }
}
