# Java Object Serializer-Deserializer
A Java Object Serializer-Deserializer program pair + testing code.

Allows you to serialize a Java Object, send it over a network, and deserialize it on the other side in your program to reconstruct the Object.

Includes testing code.

The main two pieces of the project are Serializer.java and Deserializer.java

All the other files included are for testing those two pieces out.

PLEASE NOTE: this will probably not work if you are using JDK 17 or newer. I believe you have to use JDK 16 or older for this to work, due to how newer versions have removed some of Java's reflection capabilities. I personally used JDK 16 when developing this and it worked fine, but when testing it on JDK 17 and newer versions: it did not.

Assuming you've got that handled...

How to use this:

    1) compile with:  javac *.java

    2) open 2 command line windows:

    	in one, run Receiver (ie: "java Receiver")

    	in the other, run Sender  ("java Sender")


    The Sender program will have you create an Object, then it will send it to the Receiver program you have running, deserialize it, then visualize the content of that Object on the screen so you can verify the object is the same before and after the process.