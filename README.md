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
dev dependency in your project.clj:

```clj
:profiles {:dev {:dependencies [[radagast "2.0.1"]]}}
```

Then you can use it:

    $ lein run -m radagast.coverage "whitelist.*pattern" ns.one [ns.two ...]

You can also define an alias to run it as `lein radagast ...` where it
will take test namespaces as arguments.

```clj
:aliases {"radagast" ["run" "-m" "radagast.coverage" "whitelist.pattern"]}
```

## License

Copyright © 2010-2014 Phil Hagelberg and contributors

Distributed under the Eclipse Public License, the same as Clojure.
