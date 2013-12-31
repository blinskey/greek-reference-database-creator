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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

/**
 * Reads in an XML file containing a Greek lexicon and stores entries in an 
 * SQLite database.
 * @author Ben Linskey
 */
public class LexiconCreator {
	private final static String FILE = "../xml/Perseus_text_1999.04.0058.xml";
	private final static String DB = "lexicon.db";
	private final static String TABLE_NAME = "lexicon";
	private Connection connection;
	private PreparedStatement insertStatement;
	
	/**
	 * Class constructor. 
	 */
	public LexiconCreator() {
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
					+ TABLE_NAME + " VALUES (NULL, ?, ?, ?, ?, ?, ?)");
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * Creates the lexicon database.
	 */
	public void run() {
		addEntries();
		createIndex();
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
					"_id 			INTEGER PRIMARY KEY, " +
					"betaNoSymbols 	VARCHAR(100), " +
					"betaSymbols 	VARCHAR(100), " +
					"greekFullWord 	VARCHAR(100), " +
					"greekNoSymbols 	VARCHAR(100), " +
					"greekLowercase VARCHAR(100), " +
					"entry			TEXT)";			
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
	 * Parses the XML file, modifies the lexicon entries, and inserts the
	 * modified entries into the database.
	 */
	private void addEntries() {
		System.out.println("Inserting entries...");
		
		try {
			BufferedReader in = new BufferedReader(new FileReader(FILE));
			StringBuilder xml = new StringBuilder();
			
			// Extract the XML for each lexicon entry, then process it.
			while (in.ready()) {
				String line = in.readLine();
				if (line.startsWith("<entry ")) {
					xml.delete(0,  xml.length()); // Reset XML.
					xml.append(line); // Add this line to new chunk of XML.
				} else if (line.startsWith("</entry>")) {
					xml.append(line);
					processEntry(xml.toString());
				} else {
					xml.append(line);
				}
			}
			in.close();
			
			insertStatement.executeBatch();
			connection.commit();
		} catch (FileNotFoundException e) {
			System.err.println("Error: Lexicon file not found.");
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * Modifies the specified entry and inserts it into the database.
	 * @param xml	the XML containing the entry to process
	 */
	private void processEntry(String xml) {
		try {
			LexiconParser parser = new LexiconParser(xml);
			insertStatement.setString(1, parser.getBetaNoSymbols());
			insertStatement.setString(2, parser.getBetaSymbols());
			insertStatement.setString(3, parser.getGreekFullWord());
			insertStatement.setString(4, parser.getGreekNoSymbols());
			insertStatement.setString(5, parser.getGreekLowercase());
			insertStatement.setString(6, parser.getEntry());
			insertStatement.addBatch();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (SAXException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * Creates an index on the database to speed up searches.
	 */
	private void createIndex() {
		System.out.println("Creating index...");
		
		// Create an index on the three columns matched against search queries.
		String sql = "CREATE INDEX searchIndex ON " + TABLE_NAME + 
				" (betaNoSymbols, betaSymbols, greekNoSymbols)";
		try {
			Statement statement = connection.createStatement();
			statement.executeUpdate(sql);
			statement.close();
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
