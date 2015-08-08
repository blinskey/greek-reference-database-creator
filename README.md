# Greek Reference Database Creator

This program creates the SQLite database used in the [Greek Reference][] app for Android.

Please report all issues to the [Greek Reference issue tracker][].

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

This project includes the text of *An Intermediate Greek-English Lexicon*, by Henry George Liddell and Robert Scott. Text provided by Perseus Digital Library, with funding from The Annenberg CPB/Project. Original version available for viewing and download at http://www.perseus.tufts.edu/hopper/. I have made a number of corrections to the original text.

The project also includes the text of *Overview of Greek Syntax*, by Jeffrey A. Rydberg-Cox. Text provided by Perseus Digital Library, with funding from The Annenberg CPB/Project. Original version available for viewing and download at http://www.perseus.tufts.edu/hopper/.

The above texts are licensed under a Creative Commons Attribution-ShareAlike 3.0 United States License. See http://creativecommons.org/licenses/by-sa/3.0/us/ for details.

## License

This project is licensed under the [Apache License, version 2.0][Apache], with the exception of the texts of *An Intermediate Greek-English Lexicon* and *Overview of Greek Syntax*, which are distributed under Creative Commons licenses as [described above](https://github.com/blinskey/greek-reference-database-creator#texts).

[Greek Reference]: https://github.com/blinskey/greek-reference
[Greek Reference issue tracker]: https://github.com/blinskey/greek-reference/issues
[EpiDoc TransCoder]: http://sourceforge.net/projects/epidoc/
[SQLite JDBC Driver]: https://bitbucket.org/xerial/sqlite-jdbc
[Apache]: http://www.apache.org/licenses/LICENSE-2.0
