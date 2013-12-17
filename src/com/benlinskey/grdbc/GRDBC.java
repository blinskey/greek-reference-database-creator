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

/**
 * This class provides a command line interface for the program.
 * @author Ben Linskey
 */
public class GRDBC {
	public static void main(String[] args) {
		if (args.length != 1) {
			displayUsage();
			System.exit(1);
		}
		
		String opt = args[0];
		if (opt.equals("-a")) {
			(new LexiconCreator()).run();
			(new SyntaxCreator()).run();
		} else if (opt.equals("-l")) {
			(new LexiconCreator()).run();
		} else if (opt.equals("-g")) {
			(new SyntaxCreator()).run();
		} else {
			displayUsage();
		}
	}
	
	/**
	 * Displays usage information for the program.
	 */
	private static void displayUsage() {
		System.out.println("Usage: java -jar grdbc.jar [option]\n");
		System.out.println("Options:");
		System.out.printf("%5s\t\t%20s\n", "-a", "Create all databases");
		System.out.printf("%5s\t\t%20s\n", "-l", "Create lexicon database");
		System.out.printf("%5s\t\t%20s\n", "-g", "Create grammar database");
	}
}
