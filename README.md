firefly-simulator
=================

Scala GUI application for experimenting with the firefly algorithm.

*The firefly algorithm (FA) is a metaheuristic algorithm, inspired by the flashing behaviour of fireflies.*
[Wikipedia](http://en.wikipedia.org/wiki/Firefly_algorithm)

The [Computational Intelligence Library](http://www.cilib.net/) ([Github project](https://github.com/cilib/cilib)) is a Java library that supports amongst others the basic firefly algorithm. This project provides a GUI for a small subset of the functionality provided by the CIlib library.

###Building the project

####Prerequisites
* Installed [Simple Build Tool](http://www.scala-sbt.org/)
* Put JavaFX 2.2 Library (`jfxrt.jar`) in `lib` folder
* Java 7+ installed

####Creating Executable GUI Application

```
sbt assembly
java -jar firefly-simulator.jar
```

###Download Binary Version
A compiled version of application **may** also be available at http://amerbauer.at/firefly-simulator.jar
 




