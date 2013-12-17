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

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * This class provides methods to parse a chunk of XML containing a lexicon
 * entry, modify the data contained therein, and return data to be inserted into
 * the database.
 * 
 * @author Ben Linskey
 */
public class LexiconParser extends GreekTextParser {
    /**
     * Class constructor.
     * 
     * @param xml
     *            the XML to parse
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public LexiconParser(String xml) throws ParserConfigurationException,
            SAXException, IOException {
        super(xml);
    }

    /**
     * Returns a Beta Code representation of this entry's word, stripped of all
     * diacritics.
     * 
     * @return this entry's word in Beta Code without diacritics
     */
    public String getBetaNoSymbols() {
        // Get the word and replace all symbols with an empty string.
        return getBetaSymbols().replaceAll("[^a-zA-Z]", "");
    }

    /**
     * Returns a Beta Code representation of this entry's word.
     * 
     * @return this entry's word in Beta Code
     */
    public String getBetaSymbols() {
        // We just need the "key" attribute from the "entry" element.
        Node entry = doc.getElementsByTagName("entry").item(0);
        return entry.getAttributes().getNamedItem("key").getTextContent();
    }

    /**
     * Returns this entry's word in Greek characters.
     * 
     * @return this entry's word in Greek characters
     */
    public String getGreekFullWord() {
        // Use the transcoder to convert the beta code to Greek.
        return betaToGreek(getBetaSymbols());
    }

    /**
     * Returns this entry's word in Greek characters, stripped of all
     * diacritics.
     * 
     * @return this entry's word in Greek characters without diacritics
     */
    public String getGreekNoSymbols() {
        // Get beta code with no symbols other than the capital letter marker.
        String beta = getBetaSymbols().replaceAll("[^a-zA-Z\\*]", "");

        // Use the transcoder to convert the beta code to Greek.
        return betaToGreek(beta);
    }

    /**
     * Returns this entry's word in all lowercase Greek characters, stripped of
     * all diacritics.
     * 
     * @return this entry's word in lowercase Greek characters without
     *         diacritics
     */
    public String getGreekLowercase() {
        return getGreekNoSymbols().toLowerCase();
    }

    /**
     * Returns the XML for this entry, with all Beta Code converted to Greek
     * characters.
     * 
     * @return the XML for this entry with all Beta Code converted to Greek
     *         characters
     */
    public String getEntry() {
        transcodeEntryKey();
        transcodeInElements("orth");
        transcodeInElements("ref");
        transcodeInElements("foreign");
        transcodeInElements("note");
        return getUpdatedXML();
    }

    /**
     * Converts the value of the entry element's "key" attribute from Beta Code
     * to Greek.
     */
    private void transcodeEntryKey() {
        Node entryNode = doc.getElementsByTagName("entry").item(0);
        Node keyAttr = entryNode.getAttributes().getNamedItem("key");
        String beta = keyAttr.getTextContent();
        String greek = betaToGreek(beta);
        keyAttr.setTextContent(greek);
    }
}
