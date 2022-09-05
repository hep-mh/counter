#! /bin/bash

cp -r assets build/assets
javac main.java
jar cfm build/counter.jar MANIFEST.MF *.class assets/*
chmod 755 build/counter.jar
