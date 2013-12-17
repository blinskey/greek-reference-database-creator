/* Copyright 2013 Benjamin Linskey
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.benlinskey.grdbc;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import edu.unc.epidoc.transcoder.TransCoder;

/**
 * An abstract class for parsing Greek text encoded in an XML document.
 * 
 * @author Ben Linskey
 * 
 */
public abstract class GreekTextParser {
    protected Document doc;
    protected TransCoder transcoder;

    /**
     * Class constructor.
     * 
     * @param xml
     *            the XML to parse
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    protected GreekTextParser(String xml) throws ParserConfigurationException,
            SAXException, IOException {
        // Parse the XML and create a Document.
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        doc = db.parse(is);

        // Create a TransCoder for converting Beta Code to Greek characters.
        try {
            transcoder = new TransCoder("BetaCode", "UnicodeC");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Transcodes beta code to Greek in elements with the given name.
     * 
     * @param element
     *            the name of the element to search for
     */
    protected void transcodeInElements(String element) {
        NodeList nodeList = doc.getElementsByTagName(element);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node elementNode = nodeList.item(i);
            Node langAttr = elementNode.getAttributes().getNamedItem("lang");
            if (langAttr != null) {
                String lang = langAttr.getTextContent();
                if (lang.equals("greek")) {
                    String greek = betaToGreek(elementNode.getTextContent());
                    elementNode.setTextContent(greek);
                }
            }
        }
    }

    /**
     * Returns a string containing an XML representation of the document in its
     * current state.
     * 
     * @return a string containing an XML representation of the document in its
     *         current state
     */
    protected String getUpdatedXML() {
        StringWriter writer = new StringWriter();
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(writer);
            transformer.transform(source, result);
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (TransformerException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return writer.toString();
    }

    /**
     * Converts Beta Code to Greek characters.
     * 
     * @param beta
     *            the Beta Code to transcode
     * @return the Greek equivalent of the specified Beta Code
     */
    protected String betaToGreek(String beta) {
        String greek = null;
        try {
            greek = transcoder.getString(beta);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return greek;
    }
}
