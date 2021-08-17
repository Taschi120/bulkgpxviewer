# About

![Screenshot](https://github.com/Taschi120/bulkgpxviewer/blob/master/github-resources/screenshot_01.png?raw=true)

This is a small Java program for displaying multiple GPX tracks recorded by a GPS receiver and / or smartphone app. It can be used, for example, to display an archive of your bike trips, roadtrips, hiking trips or other sports activities. Uses map data from OpenStreetMap.

Download the latest version here: https://github.com/Taschi120/bulkgpxviewer/releases/latest

A Java Runtime Environment, version 14 or higher, is required. The software is developed and tested under Windows 10 x64 only because this is a one-person project, but should run on any reasonably current version of Windows, Linux and Mac OS X.

Available languages are English and German.

This program is released under GNU GPL 3. See THIRD-PARTY.txt for info on third-party libraries used, and COPYING for the license.

# Building the program

Maven 3.8.1 or higher, Launch4j 3.14 or higher, and a Java Development Kit (JDK) 14 or higher are required. I recommend the JDK distribution from https://adoptopenjdk.net/.

To build the program without creating an installer, run `mvn install -Dnsis.disabled=true`.

To build the program and create an installer (for Windows only), NSIS 3.0.7 or higher is required. `makensis` (Linux, OSX) or `makensis.exe` (on Windows) has to be on your system's `PATH` environment variable. Simply run `mvn install`.

If `makensis` is not on your `PATH` environment variable, use the `-Dnsis.makensis.executable=<your path goes here>` parameter on the mvn call.
