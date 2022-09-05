#! /bin/bash

javac main.java
jar cfm counter.jar MANIFEST.MF *.class lib/* assets/*
chmod 755 counter.jar
