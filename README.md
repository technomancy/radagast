# Radagast

    Radagast the Brown! ... Radagast the Bird-tamer! Radagast the
    Simple! Radagast the Fool! Yet he had just the wit to play the
    part that I set him.
    -- Saruman, Lord of the Rings

Radagast is a simplistic test coverage tool. It will let you know if
you've got huge holes in your test coverage. However, you should not
rely on Radagast to tell you if your tests are good!

## Usage

For now it only works as a Leiningen plugin. Include it as a
dev-dependency in your project.clj:

    :dev-dependencies [[radagast "1.2.1"]]

Then you can use it:

    lein deps && lein radagast my.test.namespace my.other.tests

You can also set the <tt>:radagast/ns-whitelist</tt> key in
project.clj to a regex to cause it to skip coverage checks for all
functions in namespaces that match.

## License

Copyright (C) 2010 Phil Hagelberg

Distributed under the Eclipse Public License, the same as Clojure.
