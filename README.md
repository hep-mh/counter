# Counter

![Language: Java](https://img.shields.io/badge/language-Java-orange.svg?style=flat-square)
![Version: 1.0](https://img.shields.io/badge/current_version-1.0-orange.svg?style=flat-square)

## How to build?

The build process has been tested on Ubuntu, Windows and macOS using OpenJDK version 11 (and higher).

### Ubuntu, Debian

First install the OpenJDK version 11 (or higher) via the command

    sudo apt install openjdk-11-jdk

Afterwards an executable .jar file can be build using the following command

    ./build.sh

Which will be created in the build/ folder.

### Windows

On Windows it is recommended to first install scoop and to set up an appropriate build environment by running

    scoop install openssh, git
    scoop bucket add java

Afterwards install the OpenJDK version 11 (or higher) via the command

    scoop install openjdk11

### macOS

coming soon (maybe...)
