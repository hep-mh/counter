# Counter

![Language: Java](https://img.shields.io/badge/language-Java-orange.svg?style=flat-square)
![Version: 1.0](https://img.shields.io/badge/current_version-0.1-orange.svg?style=flat-square)

A set of tools for calculating cosmological constraints on decoupled MeV-scale particles

## How to build?

The build process has been tested with OpenJDK version 11 and higher.

### Ubuntu, Debian

First install the OpenJDK version 11 (or higher) via the command

    sudo apt install openjdk-11-jdk

Afterwards an executable .jar file can be build using the following set of commands

    javac main.java
    jar cfm counter.jar MANIFEST.MF *.class res/*
    chmod 755 counter.jar

### Windows

On Windows it is recommended to first install scoop and to set up an appropriate build environment by running

    scoop install openssh, git
    scoop bucket add java

Afterwards install the OpenJDK version 11 (or higher) via the command

    scoop install openjdk11

Using this setup, it is possible to build a .jar file by running

    javac main.java
    jar cfm counter.jar MANIFEST.MF *.class res/*

which afterwards can be executed via

    java -jar counter.jar

### macOS

coming soon
