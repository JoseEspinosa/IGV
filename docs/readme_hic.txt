=======================
HI-C BINARY DISTRIBUTION
=======================

Prerequisites:

Java 6.0 or greater (http://www.java.com).  For MACs this generally means Leopard or Snow Leopard is required.  If
you have "Tiger" you can try OpenJDK, although we haven't tested it:  http://landonf.bikemonkey.org/static/soylatte/.



Instructions:

1. Download and unzip the distribution file to a directory of your choice.

2. To start IGV execute the following from the command line,

     java -Xmx750m -jar hic.jar

Alternatively, you can start IGV with one of the following scripts.  You might have to make the script executable
(chmod a+x hic.sh).


hic.bat       (for Windows)
hic.sh        (for LINUX and MAC OsX)

The bat and shell scripts are configured to start IGV with 750MB of
memory.  This is a reasonable default for most machines.  If you are
working with very large datasets you can increase the amount of memory
by editing the first line of the startup script. Specifically change the
value of the "-Xmx" parameter.  For example, to start with 1 gigabyte of
memory  change the value

   -Xmx750m

to

   -Xmx1000m

