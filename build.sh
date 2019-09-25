#! /bin/bash

javac main.java
jar cfm counter.jar MANIFEST.MF *.class res/*
chmod 755 counter.jar
