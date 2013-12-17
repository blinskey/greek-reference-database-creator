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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

/**
 * Reads in an XML file containing the Overview of Greek Syntax text and stores 
 * sections of the text in an SQLite database.
 * <p>
 * Note that the Sources Cited section is omitted, as it is on Perseus.
 * @author Ben Linskey
 *
 */
public class SyntaxCreator {
	private final static String FILE = "../xml/Perseus_text_1999.04.0052.xml";
	private final static String DB = "syntax.db";
	private final static String TABLE_NAME = "syntax";
	private Connection connection;
	private PreparedStatement insertStatement;
	
	/**
	 * Class constructor. 
	 */
	public SyntaxCreator() {
		// Load driver.
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		// Connect to database.
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + DB);
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		// Use batch inserts for speed.
		try {
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		createDatabase();
		
		// Create a prepared statement to use when inserting entries.
		try {
			insertStatement = connection.prepareStatement("INSERT INTO " 
					+ TABLE_NAME + " VALUES (NULL, ?, ?, ?)");
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * Creates the Overview of Greek Syntax database.
	 */
	public void run() {
		addSections();
		try {
			insertStatement.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("Done.");
	}
	
	/**
	 * Resets the database if it already exists and creates a new, empty 
	 * database.
	 */
	private void createDatabase() {
		System.out.println("Creating lexicon database...");
		try {
			String dropTable = "DROP TABLE IF EXISTS " + TABLE_NAME;			
			String createTable = "CREATE TABLE " + TABLE_NAME + " (" + 
					"_ID 			INTEGER PRIMARY KEY, " +
					"chapter	 	VARCHAR(100), " +
					"section	 	VARCHAR(100), " +
					"xml			TEXT)";			
			Statement statement = connection.createStatement();
			statement.executeUpdate(dropTable);
			statement.executeUpdate(createTable);
			connection.commit();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * Parses the XML file, modifies the sections, and inserts the modified 
	 * data into the database.
	 */
	private void addSections() {
		System.out.println("Inserting data...");
		
		String chapter = null;
        String section = null;
        StringBuilder xml = new StringBuilder();
		Pattern pattern = Pattern.compile("<head>(.*?)</head>");
		
		try {
	        BufferedReader in = new BufferedReader(new FileReader(FILE));
	        while (in.ready()) {
	            String line = in.readLine();
	            if (line.startsWith("<div1")) {
	                // Get chapter title.
	                line = in.readLine(); // Next line is "head" element with title.
	                Matcher matcher = pattern.matcher(line);
	                matcher.find();
	                chapter = matcher.group(1);
	            } else if (line.startsWith("<div2")) {
	                // Get section title.
	                line = in.readLine(); // Next line is "head" element with title. 
	                Matcher matcher = pattern.matcher(line);
	                matcher.find();
	                section = matcher.group(1);
	                
	                // Reset XML and add "head" element.
	                xml.delete(0, xml.length());
	                xml.append("<section>");
	                xml.append(line);
	            } else if (line.contains("</div2>")) {
	                // Get any XML before the "</div2>" tag.
	                String[] split = line.split("</div2>");
	                xml.append(split[0]);
	                
	                // Add closing root tag.
	                xml.append("</section>");
	                
	                SyntaxParser parser = new SyntaxParser(xml.toString());
	                String transcodedXml = parser.transcode();
	                
	                // Add data to database.
	                insertStatement.setString(1, chapter);
	                insertStatement.setString(2, section);
	                insertStatement.setString(3, transcodedXml);
	                insertStatement.addBatch();
	            } else {
	                // Get next line of XML.
	                xml.append(line);
	            }
	        }
	        in.close();
	        
	        insertStatement.executeBatch();
	        connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (SAXException e) {
			e.printStackTrace();
			System.exit(1);
		}       
	}
}
