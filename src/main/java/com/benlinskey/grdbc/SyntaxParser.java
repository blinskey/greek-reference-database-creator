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

import org.xml.sax.SAXException;

/**
 * Parses XML from the Overview of Greek Syntax text and converts Beta Code to
 * Greek characters.
 * 
 * @author Ben Linskey
 * 
 */
public class SyntaxParser extends GreekTextParser {
    /**
     * Class constructor.
     * 
     * @param xml
     *            the XML to parse
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public SyntaxParser(String xml) throws ParserConfigurationException,
            SAXException, IOException {
        super(xml);
    }

    /**
     * Returns the XML for this section, with all Beta Code converted to Greek
     * characters.
     * 
     * @return the XML for this section with all Beta Code converted to Greek
     *         characters
     */
    public String transcode() {
        transcodeInElements("quote");
        transcodeInElements("foreign");
        return getUpdatedXML();
    }
}
