/*
    Serializes a Java Object (can be anything... mostly) into an XML document. It uses the JDOM library (http://www.jdom.org/docs/apidocs/) to do this.

    So:  input  = (an Object of any kind)
         output = a Document object, which is an XML document, again, handled by the JDOM library.

    Usage:  
        Serializer s = new Serializer();
        Document doc = s.serialize(<a Java object of some kind>);


    PLEASE NOTE: You must be using JDK 16 or earlier for this to work. This program uses reflection and, specifically, a call to the setAccessible(true) method to access private Object fields. JDK 17+ do not allow use of this method. In fact, starting with JDK 9 and anything newer, there seem to be restrictions on the use of this method. If the program does not work for you when you us it on its own, that may be the issue.


    Written by Cody_NS | Fall 2023
*/

import org.jdom2.*;
import java.lang.reflect.*;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import java.lang.IllegalAccessException;
import java.util.*;


public class Serializer
{
    CodysSimpleIDmap idMap = new CodysSimpleIDmap(); // using a simple one I wrote; the built-in one has issues
    private Element rootEl = new Element("serialized");


    public void printXML(Document doc)
    {
        print("_______________________________ SERIALIZED OBJECT ______________________________\n\n");
        XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat().setIndent("    "));
        String xmlString = xmlOutputter.outputString(doc);
        xmlString = xmlString.substring(xmlString.indexOf("\n")+1); // strip off the header line (it's junk)
        System.out.println(xmlString);
        print("________________________________________________________________________________");
    }

    boolean isCollectionClass(Class c) {
        return Collection.class.isAssignableFrom(c);
    }

    boolean isWrappedPrimitive(Class c) {
        if (c == Integer.class || c == Long.class  || c == Short.class  || c == Byte.class  ||
            c == Boolean.class || c == Float.class || c == Double.class || c == Character.class  )
            return true;
        return false;
    }

    int idForObject(Object obj) {
        if (idMap.put(System.identityHashCode(obj)))
            return idMap.size()-1;  // it was just added, so its ID will be size - 1 (first one is 0)

        return idMap.get(System.identityHashCode(obj));
    }

    public void serializeObject(Object obj) // This is huge: refactor if have time.
    {
        ArrayList <Object> objectReferences = new ArrayList<Object>();

        if (obj == null) {
            print("\nInside 'serializeObject()' - obj is null!\n"); // * actually yes: what should it do with null objects?
        }

        Class objClass = obj.getClass();
        Element objectEl = new Element("object");

        objectEl.setAttribute("class", objClass.getName());
        objectEl.setAttribute("id", "" + idForObject(obj)); // * use IdentityHashMap instead, if have time
        
        if (objClass.isArray()) {
            objectEl.setAttribute("length", "" + Array.getLength(obj));
            // for each element index
            for (int i = 0; i < Array.getLength(obj); i++) {
                Element indexEl = null;
                if ( objClass.getComponentType().isPrimitive() || objClass.getComponentType() == String.class) {
                    indexEl = new Element("value");
                    indexEl.setText("" + Array.get(obj, i));
                    objectEl.addContent(indexEl);
                }
                else {
                    indexEl = new Element("reference");
                    indexEl.setText("" + idForObject(Array.get(obj, i)));
                    objectEl.addContent(indexEl);
                    objectReferences.add(Array.get(obj, i));
                }
            }
        }

        // * could probably combine this with the section above, or make separate method
        else if (isCollectionClass(objClass)) {
            objectEl.setAttribute("length", "" + ((Collection)obj).size());
            // for each element index
            for (Object o : (Collection)obj) {
                Element indexEl = null;
                if (o.getClass().isPrimitive() || isWrappedPrimitive(o.getClass()) || o.getClass() == String.class) {
                    indexEl = new Element("value");
                    indexEl.setText("" + o);
                    objectEl.addContent(indexEl);
                }
                else {
                    indexEl = new Element("reference");
                    indexEl.setText("" + idForObject(o));
                    objectEl.addContent(indexEl);
                    objectReferences.add(o);
                }
            }    
        }

        else {
            Field[] fields = objClass.getDeclaredFields();
            for (Field f : fields){
                f.setAccessible(true);  // needed? Think so, but not sure. Probably yes, if the object has private fields, no?
                Element fieldEl = new Element("field");
                fieldEl.setAttribute("name", f.getName());
                fieldEl.setAttribute("declaringclass", f.getDeclaringClass().getName());

                if (f.getType().isPrimitive() || f.getType() == String.class) {  // I'm treating Strings as primitives for this assignment
                    Element valueEl = new Element("value");
                    valueEl.setText(getFieldValueInStringForm(f, obj));
                    fieldEl.addContent(valueEl);
                } 
                else { // f refers to an object
                    Element referenceEl = new Element("reference");
                    referenceEl.setText("" + idForObject(getFieldValue(f, obj)));
                    fieldEl.addContent(referenceEl);
                    if (idForObject(getFieldValue(f, obj)) != idForObject(obj))
                        objectReferences.add(getFieldValue(f, obj)); // we'll serialize this field later
                }
                
                objectEl.addContent(fieldEl);
            }
        }
        
        rootEl.addContent(objectEl);

        for (Object o : objectReferences)
            serializeObject(o);
    }

    static String getFieldValueInStringForm(Field f, Object obj)
    {
        try { return "" + f.get(obj); } 
            catch(IllegalAccessException e){ return "IllegalAccessException thrown :("; }
    }
    static Object getFieldValue(Field f, Object obj)
    {
        try { return f.get(obj); } 
            catch(IllegalAccessException e){ return "IllegalAccessException thrown :("; }
    }

    public org.jdom2.Document serialize(Object obj)
    {
        Document doc = new Document();
        doc.setRootElement(rootEl);
        serializeObject(obj);

        return doc;
    }



    public static void main(String[] args){
        Serializer s = new Serializer();
        ObjectCreator oc = new ObjectCreator();
        Object obj = oc.createObjWithCollectionOfObjects(true);
        s.printXML(s.serialize(obj));
    }



    // to simplify print statements...
    void print(String s){System.out.println(s);}
    void print(){System.out.println();}
    void prnt(String s){System.out.print(s);} // no newline
}