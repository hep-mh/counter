#! /bin/bash

cp -r assets build/
javac com/hepmh/Main.java
jar cfm build/counter.jar MANIFEST.MF com/hepmh/*.class
chmod 755 build/counter.jar
tar czf counter-vintage-java.tar.gz --directory=build/ .
