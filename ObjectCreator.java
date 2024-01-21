/* 
    Simple program used when testing the core Serializer/Deserializer functionality of this project. 
    Lets you create a few different types of objects to use with the Serializer/Deserializer.

    PLEASE NOTE: You must be using JDK 16 or earlier for this to work. This program uses reflection and, specifically, a call to the setAccessible(true) method to access private Object fields. JDK 17+ do not allow use of this method. In fact, starting with JDK 9 and anything newer, there seem to be restrictions on the use of this method. If the program does not work for you when you us it on its own, that may be the issue.


    Written by Cody_NS | Fall 2023
*/

import java.util.*;
import java.lang.reflect.*;


public class ObjectCreator
{
    public static final String SIMPLE_CLASS_NAME = "Person";
    public static final String PRIMITIVE_ARRAY_CLASS_NAME = "Superstitious";
    public static final int SIMPLE_CLASS_TYPE = 1;
    public static final int CLASS_WITH_OBJECTS_TYPE = 2;
    public static final int CLASS_WITH_PRIMITIVE_ARRAY_TYPE = 3;
    public static final int CLASS_WITH_OBJECT_ARRAY_TYPE = 4;
    public static final int CLASS_WITH_COLLECTION_TYPE = 5;
    public static final String MENU_EXIT_OPTION = "done";

    private Scanner keyboard = new Scanner(System.in);

    
    public Business createObjWithCollectionOfObjects(boolean isTest)
    {
        // Make a Business object
        if (isTest) {
            ArrayList<Object> customers = new ArrayList<Object>(); // quick testing
            customers.add(new Person("Mr. McFarlington", 67));
            customers.add(new Person("Charlie Munger", 99));
            customers.add(new Superstitious("Gerry", new int[]{3,17,23}));
            return new Business("Charlie's House 'O Chungus", "123 Chubbo Factory Street NW", customers);
        }
        
        print("\n\n -----  Create an instance of \"Business\"  -------------\n");
        print(" Business has 3 fields:\n");
        print(" String name");
        print(" String address");
        print(" ArrayList<Object> customers");
        print("\n Set their values:\n");
        prnt(" name = ");
        String bizName = keyboard.nextLine();
        prnt(" address = ");
        String bizAddress = keyboard.nextLine();
        int i = 0;
        ArrayList<Object> customers = new ArrayList<Object>();
        String customerType = null;
        do {
            print("\n Enter customer " + i + "'s info (or \"done\" when finished)");
            prnt(" Is this customer a Person or a Superstitious person?\n 1) Person  2) Superstitious (or \"done\"): ");
            customerType = getValidMenuSelection(keyboard.nextLine(), 1, 2);
            if (customerType.equals("1")) {
                prnt(" customers[" + i + "]'s name = ");
                String name = keyboard.nextLine();
                prnt(" customers[" + i + "]'s age = ");
                int age = Integer.parseInt(keyboard.nextLine());
                customers.add(new Person(name, age));
            }
            else if (customerType.equals("2")) {
                prnt(" customers[" + i + "]'s name = ");
                String name = keyboard.nextLine();
                prnt(" customers[" + i + "]'s lucky numbers:\n    number 1 of 3 = ");
                int num1 = Integer.parseInt(keyboard.nextLine());
                prnt("    number 2 of 3 = ");
                int num2 = Integer.parseInt(keyboard.nextLine());
                prnt("    number 3 of 3 = ");
                int num3 = Integer.parseInt(keyboard.nextLine());
                customers.add(new Superstitious(name, new int[]{num1, num2, num3}));
            }
            i++;
        } while(! customerType.equals(MENU_EXIT_OPTION));
        return new Business(bizName, bizAddress, customers);
    }

    public Duplex createObjWithArrayOfObjects(boolean isTest)
    {
        // Make a Duplex object
        if (isTest)
            return new Duplex(700000.00, new Person[]{new Person("Cody", 69), new Person("John Carmack", 53)});

        print("\n\n -----  Create an instance of \"Duplex\"  -------------\n");
        print(" Duplex has 2 fields:\n");
        print(" double price");
        print(" Person[] owners (length: 2)");
        print("\n Set their values:\n");
        prnt(" price = ");
        double price = Double.parseDouble(keyboard.nextLine());
        prnt(" owners[0]\n    name = ");
        String nameP1 = keyboard.nextLine();
        prnt("    age = ");
        int ageP1 = Integer.parseInt(keyboard.nextLine());
        prnt(" owners[1]\n    name = ");
        String nameP2 = keyboard.nextLine();
        prnt("    age = ");
        int ageP2 = Integer.parseInt(keyboard.nextLine());

        return new Duplex(price, new Person[]{new Person(nameP1, ageP1), new Person(nameP2, ageP2)});
    }

    public Object createObjWithArrayOfPrimitives(boolean isTest)
    {
        // Make a Superstitious object
        if (isTest)
            return new Superstitious("Cody Starzonk", new int[]{3,77,91});

        // Done reflectively. I initially thought I had to do it this way, so I'm leaving it as-is.
        // Should have kept it simple, but I initially thought it had to be done this way. Oh well.
        print("\n\n -----  Create an instance of \"" + PRIMITIVE_ARRAY_CLASS_NAME + "\"  -------------\n");
        
        Class primArryClass = getClassFromName(PRIMITIVE_ARRAY_CLASS_NAME);
        Object obj = instantiateObjectOfClass(primArryClass);

        Field[] fields = primArryClass.getDeclaredFields();
        print(" " + PRIMITIVE_ARRAY_CLASS_NAME + " has " + fields.length + " primitive fields:\n");
        for (Field f : fields) {
            f.setAccessible(true);
            print(" " + InspectorTools.simplifyTypeName(f.getType()) + " " + f.getName() + 
                 (f.getType().isArray() ? "  (length = " + Array.getLength(getFieldValue(f, obj)) + ")" : ""));
        }
        print("\n Set their values:\n");
        for (Field f : fields) {
            if (f.getType().isArray()) {
                Object arry = getFieldValue(f, obj);
                for (int i = 0; i < Array.getLength(arry); i++) {
                    prnt(" " + f.getName() + "[" + i + "] = ");
                    Array.set(arry, i, wrappedPrimitiveTypeFromString(f.getType().getComponentType(), keyboard.nextLine()));
                }
            }
            else {
                prnt(" " + f.getName() + " = ");
                try { f.set(obj, wrappedPrimitiveTypeFromString(f.getType(), keyboard.nextLine()) ); } // * range check?
                    catch (IllegalAccessException e) {System.out.println(e); System.exit(0);}
            }
        }
        print("\nChecking values of array:");
        Superstitious castedObj = (Superstitious)obj;
        for(int i = 0; i < castedObj.luckyNumbers.length; i++)
            print("luckyNumbers[" + i + "] = " + castedObj.luckyNumbers[i]);
            
        return obj;
    }

    public House createObjThatHasOtherObjects(boolean isTest)
    {
        // Make a House object
        if (isTest)
            return new House(500000.00, new Person("Cody", 420));

        print("\n\n -----  Create an instance of \"House\"  -------------\n");
        print(" House has 2 fields:\n");
        print(" double price");
        print(" Person owner");
        print("\n Set their values:\n");
        prnt(" price = ");
        double price = Double.parseDouble(keyboard.nextLine());
        prnt(" owner:\n    name = ");
        String name = keyboard.nextLine();
        prnt("    age = ");
        int age = Integer.parseInt(keyboard.nextLine());

        return new House(price, new Person(name, age));
    }

    Object createSimpleObject(boolean isTest)
    {
        // Make a Person object
        if (isTest)
            return new Person("Charlie", 29);

        // Done reflectively. I initially thought I had to do it this way, so I'm leaving it as-is.
        // But... yeah: this is way more complicated that it had to be, it turns out.
        print("\n\n -----  Create an instance of \"" + SIMPLE_CLASS_NAME + "\"  -------------\n");
        
        Class simpleClass = getClassFromName(SIMPLE_CLASS_NAME);
        Object obj = instantiateObjectOfClass(simpleClass);

        Field[] fields = simpleClass.getDeclaredFields();
        print(" " + SIMPLE_CLASS_NAME + " has " + fields.length + " primitive fields:\n");
        for (Field f : fields) {
            f.setAccessible(true);
            print(" " + InspectorTools.simplifyTypeName(f.getType()) + " " + f.getName());
        }
        print("\n Set their values:\n");
        for (Field f : fields) {
            prnt(" " + f.getName() + " = ");
            try { f.set(obj, wrappedPrimitiveTypeFromString(f.getType(), keyboard.nextLine()) ); } // * range check?
                catch (IllegalAccessException e) {System.out.println(e); System.exit(0);}
        }

        return obj;
    }

    void printMainMenuOptions()
    {
        print(" Select type of object to create:\n");
        print(" 1) Simple object (only primitive type fields)");
        print(" 2) Object with fields that are other objects");
        print(" 3) Object with an array of primitives");
        print(" 4) Object with an array of other objects");
        print(" 5) Object with a collection of other objects\n");
        prnt(" Make your selection (\"done\" to exit):  ");
    }

    Object createSpecificObject(String userSelection)
    {
        if (userSelection.equalsIgnoreCase(MENU_EXIT_OPTION))
            return null;
        
        switch(Integer.parseInt(userSelection)) {
            case SIMPLE_CLASS_TYPE:  return createSimpleObject(false);
            case CLASS_WITH_OBJECTS_TYPE: return createObjThatHasOtherObjects(false);
            case CLASS_WITH_PRIMITIVE_ARRAY_TYPE: return createObjWithArrayOfPrimitives(false);
            case CLASS_WITH_OBJECT_ARRAY_TYPE: return createObjWithArrayOfObjects(false);
            case CLASS_WITH_COLLECTION_TYPE: return createObjWithCollectionOfObjects(false);
            default: return null;
        }
    }

    Object createObject()
    {
        print("\n\n==============================   OBJECT CREATOR   ==============================\n");
        Object obj = null;
        printMainMenuOptions();
        String userSelection = getValidMenuSelection(keyboard.nextLine(), 1, 5);
        obj = createSpecificObject(userSelection);
        print("\n==============================   OBJECT CREATED   ==============================\n\n");
        return obj;
    }




    public static void main(String[] args){}




    // Annnd a few helper methods:
    Object wrappedPrimitiveTypeFromString(Class c, String value) // also does Strings
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
        print("\n----- ERROR: primitive type wasn't parsed correctly from input -----");
        return "(ERROR: primitive type wasn't parsed correctly from input)";
    }
    Class getClassFromName(String className) 
    {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            System.out.println(e); return null;
        }
    }
    Object instantiateObjectOfClass(Class<?> c)
    {
        try {
            return c.getDeclaredConstructor().newInstance();

        } catch (NoSuchMethodException e) {
            System.out.println(e); return null;
        } catch (InvocationTargetException e) {
            System.out.println(e); return null;
        } catch (InstantiationException e) {
            System.out.println(e); return null;
        } catch (IllegalAccessException e) {
            System.out.println(e); return null;
        }
    }
    boolean stringIsInteger(String s) // doesn't work for negative nums
    {
        if (s.length() == 0)
            return false;
        for (int i = 0; i < s.length(); i++)
            if (s.charAt(i) < '0' || s.charAt(i)  > '9')
                return false;
        return true;
    }
    boolean isCollectionClass(Class c)
    {
        return Collection.class.isAssignableFrom(c);
    }
    Object getFieldValue(Field f, Object obj)
    {
        try { return f.get(obj); } 
            catch(IllegalAccessException e){return "IllegalAccessException thrown :(";}
    }
    boolean isValidMenuInput(String userInput, int min, int max)
    {
        if (userInput.equalsIgnoreCase(MENU_EXIT_OPTION))
            return true;
        if ( stringIsInteger(userInput) && 
             Integer.parseInt(userInput) >= min && 
             Integer.parseInt(userInput) <= max    )
             return true;
        return false;
    }
    String getValidMenuSelection(String userInput, int min, int max) // range checks user's menu input
    {
        String input = userInput;
        while( ! isValidMenuInput(input, min, max)) {
            print("   Invalid selection \"" + input + "\"\n");
            prnt(" Re-enter your selection (\"done\" to finish):  ");
            input = keyboard.nextLine();
        }
        return input;
    }
    // to simplify print statements...
    void print(String s){System.out.println(s);}
	void print(){System.out.println();}
	void prnt(String s){System.out.print(s);} // no newline
}