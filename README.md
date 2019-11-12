# timscript
Music DSL

### Usage

Current, we can parse a string argument to an abstract representation. We will
update this when we can interpret it too!

You need the standalone `jar` (see below) or Leiningen (also see below).

    $ java -jar target/uberjar/timscript-0.1.0-SNAPSHOT-standalone.jar "<Musical Expression>"
    or
    $ lien run "<Musical Expression>"

_N.B._, `<Musical Expression>` should be a single string, not multiple CLI arguments.

### Developing

1. Install [Leiningen](https://leiningen.org/)
1. Clone the repo

To run `main`, use

    $ lein run "<Musical Expression>"

To open a REPL in the context of the project

    $ lein repl

To build

    $ lein uberjar

Which results in a `jar` that can be run using `java`

    $ java -jar target/uberjar/timscript-0.1.0-SNAPSHOT-standalone.jar "<Musical Expression>"
