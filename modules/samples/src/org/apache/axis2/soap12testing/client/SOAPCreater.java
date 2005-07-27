/*
* Copyright 2001-2004 The Apache Software Foundation.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.apache.axis2.soap12testing.client;

import org.apache.axis2.soap.SOAPEnvelope;
import org.apache.axis2.soap.impl.llom.builder.StAXSOAPModelBuilder;
import org.apache.axis2.soap.impl.llom.soap12.SOAP12Constants;
import org.apache.axis2.soap.impl.llom.soap11.SOAP11Constants;
import org.apache.axis2.om.OMXMLParserWrapper;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.engine.AxisFault;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLInputFactory;
import java.io.*;
import java.net.URL;

public class SOAPCreater {
    public String getStringFromSOAPMessage(String testNumber , URL url) {
        //D:\Projects\LSF\Axis2\Axis1.0\modules\samples\target\Repository
//        File file = new File("D:\\Projects\\LSF\\Axis2\\Axis1.0\\modules\\samples/test-resources\\SOAP12Testing\\RequestMessages\\SOAP12ReqT" + testNumber + ".xml");
        File file = new File("./test-resources\\SOAP12Testing\\RequestMessages\\SOAP12ReqT" + testNumber + ".xml");
        try {
            FileInputStream stream = new FileInputStream(file);
            BufferedInputStream bf = new BufferedInputStream(stream);
            DataInputStream ds = new DataInputStream(bf);
            StringBuffer sb = new StringBuffer();

            sb.append(HTTPConstants.HEADER_POST).append(" ");
            sb.append(url.getFile()).append(" ").append(HTTPConstants.HEADER_PROTOCOL_10).append(
                    "\n");
            sb.append(HTTPConstants.HEADER_CONTENT_TYPE).append(": ")
                    .append(SOAP12Constants.SOAP_12_CONTENT_TYPE);
            sb.append("; charset=utf-8\n");
            sb.append("\n");

            String record;
            while ((record = ds.readLine()) != null) {
                sb.append(record.trim());
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }

    public SOAPEnvelope getEnvelopeFromSOAPMessage(String pathAndFileName) {
        File file = new File(pathAndFileName);
        try {
            XMLStreamReader parser = XMLInputFactory.newInstance().createXMLStreamReader(new FileReader(file));
            OMXMLParserWrapper builder = new StAXSOAPModelBuilder(parser,null);
            return (SOAPEnvelope) builder.getDocumentElement();            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}