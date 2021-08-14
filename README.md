# About

This is a small Java program for displaying multiple GPX tracks recorded by a GPS receiver and / or smartphone app. It can be used, for example, to display an archive of your bike trips, roadtrips, hiking trips or other sports activities. Uses map data from OpenStreetMap.

A Java Runtime Environment, version 14 or higher, is required.

This program is released under GNU GPL 3. See THIRD-PARTY.txt for info on third-party libraries used, and COPYING for the license.

# Building the program

Maven 3.8.1 or higher, Launch4j 3.14 or higher, and a Java Development Kit (JDK) 14 or higher are required. I recommend the JDK distribution from https://adoptopenjdk.net/.

To build the program without creating an installer, run `maven install -Dnsis.disabled=true`.

To build the program and create an installer (Windows only), NSIS 3.0.7 or higher is required. Run `maven install -DnsisBinDir="C:\Program Files (x86)\NSIS\Bin"`, replacing the file path as appropriate to your installation.