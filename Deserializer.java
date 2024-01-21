/*
    Deserializes a serialized Java Object in XML form and outputs the original Object. 

    Makes use of the JDOM library (http://www.jdom.org/docs/apidocs/) to do this.

    So:  input  = an XML Document (JDOM library's "Document" type)
         output = the java Object reprsented by the input XML document (Object type).

    Usage:  
        Deserializer d = new Deserializer();
        Object o = d.derialize(<an org.jdom2.Document XML document>);


    Written by Cody_NS | Fall 2023
*/

import java.util.*;
import java.lang.reflect.*;
import java.io.File;
import java.lang.IllegalAccessException;
import org.jdom2.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.input.SAXBuilder;


public class Deserializer
{
    private static final int MAX_OBJECTS = 1000;
    private Object[] deserializedObjs = new Object[MAX_OBJECTS];

    public Deserializer()
    {
        for (int i = 0; i < MAX_OBJECTS; i++)
            deserializedObjs[i] = null;  // Array.fill(deserializedObjs, null)  would probably work, no?
    }

    Object wrappedPrimitiveTypeFromString(Class c, String value) // move method on this to a utility class. This meth is in ObjectCreator.
    {
        if (int.class == c)     return Integer.parseInt(value);
        if (long.class == c)    return Long.parseLong(value);
        if (short.class == c)   return Short.parseShort(value);
        if (byte.class == c)    return Byte.parseByte(value);
        if (char.class == c)    return value.charAt(0);
        if (float.class == c)   return Float.parseFloat(value);
        if (double.class == c)  return Double.parseDouble(value);
        if (boolean.class == c) return Boolean.parseBoolean(value);
        if (String.class == c)  return value;
        // execution shouldn't reach here
        System.out.println("\n----- ERROR: primitive type wasn't parsed correctly from input -----");
        return "(ERROR: primitive type wasn't parsed correctly from input)";
    }

    Object instantiateObjectOfClass(Class<?> c)  // move method on this to a utility class. This meth is in ObjectCreator.
    {
        try {
            return c.getDeclaredConstructor().newInstance();
        } catch (Exception e) {e.printStackTrace(); return null;}
    }

    boolean isCollectionClass(Class c)  // * duplicate from Serializer; refactor if there's time
    {   
        return Collection.class.isAssignableFrom(c);
    }

    public Object instantiateArray(Class c, int length)
    {
        return Array.newInstance(c.getComponentType(), length);
    }

    @SuppressWarnings("unchecked") // for the collection class, casting it to a List
    public Object deserialize(org.jdom2.Document document)  // Long method: refactor if have time. 
    {
        // get the root element (serializable)
        Element rootEl = document.getRootElement();

        // convert object element list to an array... don't really need to do this, but I want [] access
        List<Element> objectElements = rootEl.getChildren("object");
        Element[] objEl = new Element[objectElements.size()];
        for (int i = 0; i < objEl.length; i++)  
            objEl[i] = objectElements.get(i);

        Object[] objects = new Object[objectElements.size()];

        try {  
            // instantiate each object first, so they all exist before moving on to next step. Important.
            for (int i = 0; i < objEl.length; i++) {
                Class objClass = Class.forName(objEl[i].getAttribute("class").getValue());
                int objectID = Integer.valueOf(objEl[i].getAttribute("id").getValue());
                Object o = objClass.isArray() ? 
                                instantiateArray(objClass, Integer.valueOf(objEl[i].getAttribute("length").getValue())) :
                                instantiateObjectOfClass(objClass);
                objects[objectID] = o;
            }
            // Now iterate through each object and set its values
            for (int i = 0; i < objEl.length; i++) {
                Class objClass = Class.forName(objEl[i].getAttribute("class").getValue());
                int objectID = Integer.valueOf(objEl[i].getAttribute("id").getValue());

                for (Element field : objEl[i].getChildren("field")) {
                    Attribute fieldName = field.getAttribute("name");
                    Field f = objClass.getDeclaredField(fieldName.getValue());
                    f.setAccessible(true);
                    if (field.getChild("value") != null)
                        f.set(objects[objectID], wrappedPrimitiveTypeFromString(f.getType(), field.getChild("value").getValue()));
                    if (field.getChild("reference") != null)
                        f.set(objects[objectID], objects[Integer.valueOf(field.getChild("reference").getValue())]);
                }
                if (objClass.isArray()) {
                    List<Element> valueElements = objEl[i].getChildren("value");
                    for (int index = 0; index < valueElements.size(); index++)
                        Array.set(objects[objectID], index, wrappedPrimitiveTypeFromString(objClass.getComponentType(), valueElements.get(index).getValue()));
                    List<Element> referenceElements = objEl[i].getChildren("reference");
                    for (int index = 0; index < referenceElements.size(); index++) 
                        Array.set(objects[objectID], index, objects[Integer.valueOf(referenceElements.get(index).getValue())]); // point to the reconstructed object the id refers to
                }
                if (isCollectionClass(objClass)) {
                    List<Element> valueElements = objEl[i].getChildren("value");
                    for (int index = 0; index < valueElements.size(); index++)
                        ((List)objects[objectID]).add(wrappedPrimitiveTypeFromString(objClass.getComponentType(), valueElements.get(index).getValue()));
                    List<Element> referenceElements = objEl[i].getChildren("reference");
                    for (int index = 0; index < referenceElements.size(); index++)
                        ((List)objects[objectID]).add(objects[Integer.valueOf(referenceElements.get(index).getValue())]); // point to the reconstructed object this id refers to
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return objects[0];
    }
    


    public static void main(String[] args){
    // Some test code for this program, if you have an XML file (output.xml) in the same directory.
    // Uncomment the code below to use it:

        // try {
        //  File inputFile = new File("output.xml");
        //  SAXBuilder saxBuilder = new SAXBuilder();
        //  Document doc = saxBuilder.build(inputFile);

        //  ObjectCreator oc = new ObjectCreator();
        //  Serializer ser = new Serializer();
        //  Deserializer test = new Deserializer();
        //  Object o = test.deserialize(doc);

        //  ser.printXML(ser.serialize(oc.createObjWithCollectionOfObjects(true)));

        //  Visualizer v = new Visualizer();
        //  v.inspect(o, true);

        // } catch (Exception e) {e.printStackTrace();}
    }
}