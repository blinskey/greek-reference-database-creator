# Greek Reference Database Creator

This program creates the SQLite database used in the [Greek Reference][] app for Android.

## Important Note

This version of GRDBC is designed to work with a new version of Greek Reference that will be hosted on GitHub. It replaces the old version of GRDBC currently hosted on Bitbucket and is incompatible with the version of Greek Reference currently hosted on Bitbucket.

## Dependencies

- SQLite3
- Apache Ant (for compilation)

## Compilation

Run `ant` from the project's root directory to build the JAR.

## Usage

    java -jar grdbc.jar [option]

    Options:
    -a       Create all databases
    -l       Create lexicon database
    -g       Create grammar database

## Third-Party Libraries

This program uses the following third-party libraries. Thanks to their authors for making their work available.

- [EpiDoc TransCoder][]
- [SQLite JDBC Driver][]

## Texts

This project includes the text of *An Intermediate Greek-English Lexicon*, by Henry George Liddell and Robert Scott. Text provided by Perseus Digital Library, with funding from The Annenberg CPB/Project. Original version available for viewing and download at http://www.perseus.tufts.edu/hopper/.

The project also includes the text of *Overview of Greek Syntax*, by Jeffrey A. Rydberg-Cox. Text provided by Perseus Digital Library, with funding from The Annenberg CPB/Project. Original version available for viewing and download at http://www.perseus.tufts.edu/hopper/.

The above texts are licensed under a Creative Commons Attribution-ShareAlike 3.0 United States License. See http://creativecommons.org/licenses/by-sa/3.0/us/ for details.

[Greek Reference]: https://github.com/blinskey/greek-reference
[EpiDoc TransCoder]: http://sourceforge.net/projects/epidoc/
[SQLite JDBC Driver]: https://bitbucket.org/xerial/sqlite-jdbc
