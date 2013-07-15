jaws ![build status](https://travis-ci.org/bmcdorman/jaws.png)
====

Lightweight and transparent JAva WebSockets.

Jaws hopes to provide a fast, robust, and compliant WebSocket implementation in Java. The end goal for
jaws is to transparently support WebSocket clients in an unmodified (and possibly closed source) TCP server. That
said, jaws should also support other use cases.

Current implementations of WebSockets in Java such as Jetty provide high-level abstractions at the cost of flexibility.
Jaws is designed to be as abstract or as close to the metal as the developer wishes.

Building and Installing 
------------------------

jaws is built using maven. Use your system's package manager to install maven if you don't have it already.

Once you have maven installed and the jaws repository cloned, run:

    cd jaws
    mvn clean install
    
That's it! Maven will download the required dependencies, build the project, run unit tests, and install it to your system
for use by other projects.

Usage
-----

Coming soon!

License
-------

Jaws is released under the MIT License. For more information, see the LICENSE file at the root of this project.

Acknowledgements
----------------

This project is a part of a [Google Summer of Code 2013](http://www.google-melange.com/gsoc/homepage/google/gsoc2013) proposal to bring networking support to the open source Doppio JVM.
Thank you to Google for funding and [PLASMA@UMass](http://plasma.cs.umass.edu/) for guidance on this project.

