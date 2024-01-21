/*
    Simple program that receives an XML document, representing a serialized Java object, from the "Sender" Java program running on the same computer.

    These two programs are used in conjunction as a means of testing the core serialization + deserialization functionality of this project.

    To keep things simple, this program (Receiver) and the Sender program have been configured to be run and used on the same machine. The purpose of these programs is just to verify the Serializer/Deserializer program functionality of this project works as intended. You could change the socket info below a to make it receive the XML document from another machine on the internet somewhere (and also change the corresponding socket info in the Sender program) if you wanted to, but I'm keeping things ultra simple here. The purpose of the Sender and Receiver programs isn't to test network code: it's to _simulate_ how the Serializer and Deserializer programs would be used in the real world.

    Written by Cody_NS | Fall 2023
*/

import java.io.*; 
import java.net.*; 
import org.jdom2.*;
import org.jdom2.output.XMLOutputter;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;


public class Receiver
{
    public Document receiveXMLdocument()
    {
        try
        {
            ServerSocket serverSocket = new ServerSocket(5000);
            System.out.println("Server started. Waiting to receive XML Document from client...");

            // wait for client to connect
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected.");
 
            // Create input stream for communication with clientsocket
            BufferedReader inStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
 
            // Rebuild the XML using saxBuilder
            Document doc = (new SAXBuilder()).build(inStream);

            // Close the input stream and the sockets
            System.out.print("Closing connection...");
            clientSocket.close();
            inStream.close();
            System.out.println(" DONE :)");

            return doc;

        } catch(Exception e){
            e.printStackTrace();  return null;
        }
    }

    public static void main(String args[])
    {
        Receiver server = new Receiver();
        Document doc = server.receiveXMLdocument();

        Deserializer d = new Deserializer();
        Object o = d.deserialize(doc);

        Visualizer v = new Visualizer();
        v.inspect(o, true);
    }


    // // These two are not needed, but...
    // // you could use them for testing deserialization if you don't want to use the Sender program in conjunction with this one to get an XML document to use when testing Deserializer's functinality:
    // Document xmlDocumentFromFilePath(String filePath)
    // {
    //     try {
    //         return (new SAXBuilder()).build(new File(filePath));
    //     } catch (Exception e) {
    //         e.printStackTrace();  return null;
    //     }
        
    // }

    // Document convertStringToXMLDocument(String xmlDocString)
    // {
    //     try {
    //         SAXBuilder sb = new SAXBuilder();
    //         return sb.build(new StringReader(xmlDocString));
    //     } catch (JDOMException e) {
    //         System.out.println("Uh oh. Exception thrown:\n" + e); return null;
    //     } catch (java.io.IOException e) {
    //         System.out.println("Uh oh. Exception thrown:\n" + e); return null;
    //     }
    // }
}