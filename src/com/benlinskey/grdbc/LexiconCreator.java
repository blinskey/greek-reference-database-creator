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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

/**
 * Reads in an XML file containing a Greek lexicon and stores entries in an
 * SQLite database.
 * <p>
 * The entries in the XML file are not properly alphabetized, so we first read
 * them into a temporary table and then alphabetize them and copy them into the
 * final table in alphabetical order.
 * 
 * @author Ben Linskey
 */
public class LexiconCreator {
    private final static String FILE = "../xml/Perseus_text_1999.04.0058.xml";
    private final static String DB = "lexicon.db";
    private final static String TEMP_TABLE_NAME = "temp";
    private final static String FINAL_TABLE_NAME = "lexicon";
    private Connection connection;
    private PreparedStatement tempInsertStatement;
    private PreparedStatement finalInsertStatement;

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
            tempInsertStatement = connection.prepareStatement("INSERT INTO "
                    + TEMP_TABLE_NAME + " VALUES (?, ?, ?, ?, ?, ?)");
            finalInsertStatement = connection.prepareStatement("INSERT INTO "
                    + FINAL_TABLE_NAME + " VALUES (NULL, ?, ?, ?, ?, ?, ?)");
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
        alphabetize();
        dropTempTable();
        createIndex();
        vacuum();
        try {
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
            String dropTempTable = "DROP TABLE IF EXISTS " + TEMP_TABLE_NAME;
            String createTempTable = "CREATE TABLE " + TEMP_TABLE_NAME + " ("
                    + "betaNoSymbols 	VARCHAR(100), "
                    + "betaSymbols 	VARCHAR(100), "
                    + "greekFullWord 	VARCHAR(100), "
                    + "greekNoSymbols 	VARCHAR(100), "
                    + "greekLowercase VARCHAR(100), " + "entry			TEXT)";
            String dropFinalTable = "DROP TABLE IF EXISTS " + FINAL_TABLE_NAME;
            String createFinalTable = "CREATE TABLE " + FINAL_TABLE_NAME + " ("
                    + "_id          INTEGER PRIMARY KEY, "
                    + "betaNoSymbols    VARCHAR(100), "
                    + "betaSymbols  VARCHAR(100), "
                    + "greekFullWord    VARCHAR(100), "
                    + "greekNoSymbols   VARCHAR(100), "
                    + "greekLowercase VARCHAR(100), " + "entry          TEXT)";
            Statement statement = connection.createStatement();
            statement.executeUpdate(dropTempTable);
            statement.executeUpdate(createTempTable);
            statement.executeUpdate(dropFinalTable);
            statement.executeUpdate(createFinalTable);
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
                    xml.delete(0, xml.length()); // Reset XML.
                    xml.append(line); // Add this line to new chunk of XML.
                } else if (line.startsWith("</entry>")) {
                    xml.append(line);
                    processEntry(xml.toString());
                } else {
                    xml.append(line);
                }
            }
            in.close();

            tempInsertStatement.executeBatch();
            connection.commit();

            tempInsertStatement.close();
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
     * 
     * @param xml
     *            the XML containing the entry to process
     */
    private void processEntry(String xml) {
        try {
            LexiconParser parser = new LexiconParser(xml);
            tempInsertStatement.setString(1, parser.getBetaNoSymbols());
            tempInsertStatement.setString(2, parser.getBetaSymbols());
            tempInsertStatement.setString(3, parser.getGreekFullWord());
            tempInsertStatement.setString(4, parser.getGreekNoSymbols());
            tempInsertStatement.setString(5, parser.getGreekLowercase());
            tempInsertStatement.setString(6, parser.getEntry());
            tempInsertStatement.addBatch();
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
     * Copies the entries from the temporary table into the final table in
     * alphabetical order.
     */
    private void alphabetize() {
        try {
            String query = "SELECT * FROM " + TEMP_TABLE_NAME
                    + " ORDER BY greekLowercase ASC";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                finalInsertStatement.setString(1, rs.getString(1));
                finalInsertStatement.setString(2, rs.getString(2));
                finalInsertStatement.setString(3, rs.getString(3));
                finalInsertStatement.setString(4, rs.getString(4));
                finalInsertStatement.setString(5, rs.getString(5));
                finalInsertStatement.setString(6, rs.getString(6));
                finalInsertStatement.addBatch();
            }

            finalInsertStatement.executeBatch();
            connection.commit();

            statement.close();
            finalInsertStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Drops the temporary table.
     */
    private void dropTempTable() {
        String sql = "DROP TABLE " + TEMP_TABLE_NAME;
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates an index on the database to speed up searches.
     */
    private void createIndex() {
        System.out.println("Creating index...");

        // Create an index on the three columns matched against search queries.
        String sql = "CREATE INDEX searchIndex ON " + FINAL_TABLE_NAME
                + " (betaNoSymbols, betaSymbols, greekNoSymbols, "
                + "greekLowercase)";
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
            statement.close();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Rebuilds the database in order to reduce its size.
     */
    private void vacuum() {
        System.out.println("Rebuilding database...");
        try {
            connection.setAutoCommit(true);
            Statement statement = connection.createStatement();
            statement.executeUpdate("VACUUM");
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
