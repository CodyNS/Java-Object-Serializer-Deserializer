/*
    Simple program that sends an XML document, representing a serialized Java object, to the "Receiver" Java program running on the same machine.
    It also prints out the XML document on the screen, so you can see what is being sent.

    These two programs are used in conjunction as a means of testing the core serialization + deserialization functionality of this project.

    To keep things simple, this program (Sender) and the Receiver program have been configured to be run and used on the same machine. The purpose of these programs is just to verify the Serializer/Deserializer program functionality of this project works as intended. You could change the socket info below a to make it send the XML document to another machine on the internet somewhere (and also change the corresponding socket info in the Receiver program) if you wanted to, but I'm keeping things ultra simple here. The purpose of the Sender and Receiver programs isn't to test network code: it's to _simulate_ how the Serializer and Deserializer programs would be used in the real world.

    Written by Cody_NS | Fall 2023
*/

import java.util.*;
import java.lang.reflect.*;
import java.io.*;
import java.net.*;
import org.jdom2.*;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;


public class Sender
{
    public void sendXMLdoc(Document doc) // many ways of doing this. Here's a simple one:
    {
        try {
            // Create socket and connect to server at the server address (localhost) and port 5000
            System.out.print("\n Preparing to send XML Document: Waiting for server... ");
            Socket socket = new Socket("localhost", 5000);  // ie: 127.0.0.1
            System.out.println("Connected.\n");
            
            // Create an output stream
            BufferedOutputStream bufferedStream = new BufferedOutputStream(socket.getOutputStream());

            // prepare ouput
            XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
            String xmlString = xmlOutputter.outputString(doc);
            
            // Stream we will use to output to the server
			ByteArrayOutputStream byteoutStream = new ByteArrayOutputStream();

			// set the XMLOutputter object to send output through our stream
       	    xmlOutputter.output(doc, byteoutStream);
			
			// all bytes should be fed into a byteList so it can be written
            byte[] byteList = byteoutStream.toByteArray();

            // Send the JDOM to the server
            bufferedStream.write(byteList);
			
			// flush the stream to get everything out of there, then close
            bufferedStream.flush();
			bufferedStream.close();	
			byteoutStream.close();
            socket.close();
			
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("\n There was a problem sending the serialized object.\n\n");
            return;
        }
        System.out.println(" Serialized object sent to server :)\n\n");
        // check https://www.geeksforgeeks.org/socket-programming-in-java/ if want to use DataInputStream instead
    }

    public static void main(String[] args)
    {
        ObjectCreator oc = new ObjectCreator();
        Serializer s = new Serializer();
        Sender sender = new Sender();

        Document doc = s.serialize(oc.createObject());
        s.printXML(doc);
        sender.sendXMLdoc(doc);
    }
}