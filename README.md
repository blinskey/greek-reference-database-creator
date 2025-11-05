# Greek Reference Database Creator

This program creates the SQLite database used in the [Greek Reference][] app
for Android.

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

This program uses the following third-party libraries, distributed with this
repository in the `lib` directory. Thanks to their authors for making their
work available.

- [EpiDoc TransCoder][] ([source][transcoder-source])
- [SQLite JDBC Driver][]

## Texts

This project includes the text of *An Intermediate Greek-English Lexicon*, by
Henry George Liddell and Robert Scott. Text provided by Perseus Digital
Library, with funding from The Annenberg CPB/Project. Original version
available for viewing and download at http://www.perseus.tufts.edu/hopper/.
I have made a number of corrections to the original text.

The project also includes the text of *Overview of Greek Syntax*, by Jeffrey A.
Rydberg-Cox. Text provided by Perseus Digital Library, with funding from The
Annenberg CPB/Project. Original version available for viewing and download at
http://www.perseus.tufts.edu/hopper/.

The above texts are licensed under a [Creative Commons Attribution-ShareAlike
3.0 United States License][CC-BY-SA 3.0].

## License

This project is licensed under the [Apache License, Version 2.0][Apache]. 
See the `LICENSE` file for this project's license and the licenses of the
third-party resources included in this repository.

[Greek Reference]: https://github.com/blinskey/greek-reference
[EpiDoc TransCoder]: https://sourceforge.net/projects/epidoc/files/Transcoder/
[transcoder-source]: https://sourceforge.net/p/epidoc/code/HEAD/tree/trunk/transcoder/
[SQLite JDBC Driver]: https://github.com/xerial/sqlite-jdbc/
[Apache]: http://www.apache.org/licenses/LICENSE-2.0
[CC-BY-SA 3.0]: https://creativecommons.org/licenses/by-sa/3.0/us/
