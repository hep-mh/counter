#! /bin/bash

cp -r assets build/
javac com/hepmh/Main.java
jar cfm build/counter.jar MANIFEST.MF com/hepmh/*.class assets/*
chmod 755 build/counter.jar
