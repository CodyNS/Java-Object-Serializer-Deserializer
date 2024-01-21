/*
	Produces a visual reprsentation of a Java Object.

	Usage:
		Object o = ...;  // the object you want to visualize
		Visualizer v = new Visualizer();
        v.inspect(o, true);  // true if you want a recursive visualization of the object's object-type fields
        					 // false for a surface-level visualization
	
	I've included this as part of the testing mechanism of this Serializer/Deserializer project. It gets used/called by the Receiver program, which uses the Deserializer program on the XML document it receives (from the Sender program), and then makes use of this Visualizer program to print out a visual representation of the deserialized object, so you can verify the object is the same before and after serialization and deserialization.

	LIMITATIONS/ASSUMPTIONS:  If the input object contains some kind of collection class object, it needs to be an ArrayList type, otherwise I believe this program will not work properly.

	ALSO, PLEASE NOTE: You must be using JDK 16 or earlier for this to work. This program uses reflection and, specifically, a call to the setAccessible(true) method to access private Object fields. JDK 17+ do not allow use of this method. In fact, starting with JDK 9 and anything newer, there seem to be restrictions on the use of this method. If the program does not work for you when you us it on its own, that may be the issue.


	Written by Cody_NS | Fall 2023
*/

import java.util.*;
import java.lang.reflect.*;
import java.lang.IllegalAccessException;


class ClassInspector
{
	InspectorTools t = new InspectorTools(); 

	void printClassInforForObject(Object obj)
	{	
		Class classObj = obj.getClass();
		t.print(" Object's class:  " + classObj.getName());
		// t.print("    Object's immediate superclass:  " + superClassName(classObj));

		// Class[] interfaces = classObj.getInterfaces();
		// String interfacesSt = "";
		// for(int i = 0; i < interfaces.length; i++)
		// 	interfacesSt += interfaces[i].getName() + (i == interfaces.length-1 ? "" : ", ");
		// if (interfacesSt == "") interfacesSt = "(none)";
		// t.print(" Interfaces this class implements:  " + interfacesSt + "\n");
	}
	String superClassName(Class classObj)
	{
		if (classObj.getSuperclass() == null)
			return "";
		return classObj.getSuperclass().getName();
	}
}


class MethodInspector
{
	InspectorTools t = new InspectorTools(); 

	void printMethodsForObject(Object obj, Set<Class> classesInObjHrchy)
	{
		t.print(); //t.print(" ----  METHODS  -------------------------------");
        t.print("  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  METHODS");
		Method[] methods = getAllMethodsForObject(classesInObjHrchy);
		if (methods.length == 0)  t.print(" (none)");
		else for (Method m : methods) printDataForMethod(m);
	}
	Method[] getAllMethodsForObject(Set<Class> classesInObjHrchy)
	{
		Method[] methods = new Method[]{};
		for (Class c : classesInObjHrchy)
			methods = t.combineArrays(methods, c.getDeclaredMethods());
		return methods;
	}
	void printDataForMethod(Method m)
	{
		t.print(
			t.modifiersForItem(m) + 
			t.simplifyTypeName(m.getReturnType()) + " " + 
			m.getName() + "(" + t.parametersForItem(m) + ")" + methodExceptionDetails(m) 
		);
	}
	String methodExceptionDetails(Method m)
	{
		Class[] exceptions = m.getExceptionTypes();
		if (exceptions.length == 0) 
			return "";
		String exceptionSt = " throws ";
		for(int i = 0; i < exceptions.length; i++)
			exceptionSt += (exceptions[i].getName() + (i == exceptions.length-1 ? "" : ", "));
		return exceptionSt;
	}
}


class ConstructorInspector
{
	InspectorTools t = new InspectorTools(); 

	void printConstructorsForObject(Object obj, Set<Class> classesInObjHrchy)
	{
		t.print(); //t.print(" ----  CONSTRUCTORS  --------------------------");
        t.print("  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  CONSTRUCTORS");
		Constructor[] cons = getAllConstructorsForObject(classesInObjHrchy);
		if (cons.length == 0)  t.print(" (none)");
		else for (Constructor c : cons) printConstructorData(c);
	}
	Constructor[] getAllConstructorsForObject(Set<Class> classesInObjHrchy)
	{
		Constructor[] cons = new Constructor[]{};
		for (Class c : classesInObjHrchy)
			cons = t.combineArrays(cons, c.getDeclaredConstructors());
		return cons;
	}
	void printConstructorData(Constructor c)
	{
		t.print(
			t.modifiersForItem(c) +
			t.simplifyTypeName(c.getDeclaringClass()) + "(" + t.parametersForItem(c) + ")"
		);
	}
}


class FieldInspector
{
	InspectorTools t = new InspectorTools(); 

	void printFieldsForObject(Object obj, Set<Class> classesInObjHrchy, List<Field> objectFields, boolean showMethodsAndConstructors, HashSet<Integer> inspectedObjects)
	{
		t.print(); //t.print(" ----  FIELDS  --------------------------------");
        if (showMethodsAndConstructors) t.print("  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  FIELDS");
		Field[] fields = getAllFieldsForObject(classesInObjHrchy);
		if (fields.length == 0)  t.print(" (none)");
		else for (Field f : fields) printDataForField(f, obj, objectFields, inspectedObjects);
	}
	Field[] getAllFieldsForObject(Set<Class> classesInObjHrchy)
	{
		Field[] fields = new Field[]{};
		for (Class c : classesInObjHrchy) {
            if (c == java.util.ArrayList.class){
                try{
                    fields = new Field[]{c.getDeclaredField("elementData")};
                } catch (Exception e) {e.printStackTrace();}
            }
            else
			    fields = t.combineArrays(fields, c.getDeclaredFields());
        }
		return fields;
	}
	void printDataForField(Field f, Object obj, List<Field> objectFields, HashSet<Integer> inspectedObjects)
	{
		f.setAccessible(true);
		boolean objectAlreadyInspected = ! inspectedObjects.add(getFieldValue(f, obj).hashCode());
		t.prnt(Visualizer.INDENT.repeat(Visualizer.indentMultiple) + t.modifiersForItem(f) + t.simplifyTypeName(f.getType()) + " " + f.getName());
		if ( ! f.getType().isPrimitive()  &&  f.getType() != String.class  &&  getFieldValue(f, obj) != null  &&  ! objectAlreadyInspected ){

			if (! t.isPrimitiveArray(f) )
				objectFields.add(f);  // inspect this later

			if (f.getType().isArray()) {
				t.prnt(arrayFieldValues(f, obj, objectFields) + "\n");
				return; 
			}
			else if (t.isCollectionClass(f.getType())) {
				t.prnt(collectionFieldValues(f, obj) + "\n");
				return;
			}
		}
		t.prnt(" = " + simpleFieldValue(f, obj)); // for primitives and non-array/collection reference values
		if ( objectAlreadyInspected  &&  ! f.getType().isPrimitive() )
			t.prnt("  <- object already inspected");
		t.print();
	}
	Object getFieldValue(Field f, Object obj)
	{
		try {return f.get(obj); } 
			catch(IllegalAccessException e){e.printStackTrace(); return "IllegalAccessException thrown :(";}
	}
	String simpleFieldValue(Field f, Object obj)
	{
		return f.getType() == String.class ? "\"" + getFieldValue(f, obj) + "\"" : 
											 "" + getFieldValue(f, obj);
	}
	String arrayFieldValues(Field f, Object obj, List<Field> objectFields)
	{
		Object o = getFieldValue(f, obj);
		int length = Array.getLength(o);
		String arraySt = " (length: " + length + ")  = {";
		String quoteForWhenItsAStr = o.getClass().getComponentType() == String.class ? "\"" : "";
		
		for (int i = 0; i < length; i++)
			arraySt += quoteForWhenItsAStr + Array.get(o, i) + quoteForWhenItsAStr + (i < length-1 ? ", " : "");
		return arraySt + "}";
	}
	@SuppressWarnings("unchecked") // for the collection class, casting it to a List
	String collectionFieldValues(Field f, Object obj)
	{
		ArrayList<Object> collection = (ArrayList)getFieldValue(f, obj);
		String s = " (size: " + collection.size() + ") = " + simpleFieldValue(f, obj);
		return s;
	}
}


public class Visualizer
{
	InspectorTools t = new InspectorTools();
	public static final String INDENT = "        ";
	static int indentMultiple = 0;
    private boolean showMethodsAndConstructors = true;
    private Scanner keyboard = new Scanner(System.in);

	ClassInspector cI = new ClassInspector();
	MethodInspector mI = new MethodInspector();
	ConstructorInspector consI = new ConstructorInspector();
	FieldInspector fI = new FieldInspector();

	public Visualizer(){
        t.prnt("\n Object received. Show methods and constructors for this visualization? (y/n): ");
        showMethodsAndConstructors = keyboard.nextLine().equalsIgnoreCase("y");
		printBanner();
	}

	public void printBanner()
	{
		t.print("\n=============================  OBJECT VISUALIZER  =============================\n");
	}

	void inspect(Object obj, boolean recursive)
	{
		if (t.objectIsNull(obj))
			return;

		Set<Class> classesInObjHrchy = new HashSet<Class>();
		//t.getAllClassesInHierarchyForClass(obj.getClass(), classesInObjHrchy);
        classesInObjHrchy.add(obj.getClass());

		List<Field> objectFields = new ArrayList<Field>();
		HashSet<Integer> inspectedObjects = new HashSet<Integer>();
		inspectedObjects.add(obj.hashCode());

		t.print();
		cI.printClassInforForObject(obj);
		fI.printFieldsForObject(obj, classesInObjHrchy, objectFields, showMethodsAndConstructors, inspectedObjects);
		if (showMethodsAndConstructors){
            consI.printConstructorsForObject(obj, classesInObjHrchy);
            mI.printMethodsForObject(obj, classesInObjHrchy);
        } 
		t.print("\n");

		if (recursive)
			inspectObjectFields(obj, objectFields, recursive);
	}

	@SuppressWarnings("unchecked") // for the collection class, casting it to a List
	void inspectObjectFields(Object obj, List<Field> objectFields, boolean recursive)
	{
		if (objectFields.size() > 0){
			t.print(" >>>>>>> INSPECTING ^ THIS OBJECT'S CLASS-TYPE FIELDS:\n");
			
			indentMultiple++;
		}
		for (Field f : objectFields) {
			t.print(" Inspecting Field:  " + f.getName());
			//t.print(" Reference value =  " + fI.getFieldValue(f, obj) + "\n");
			if (f.getType().isArray()) {
				t.print("   Object's class:  " + t.simplifyTypeName(f.getType()) +"\n");
				Object[] array = (Object[])fI.getFieldValue(f, obj);
				for(int i = 0; i < array.length; i++){
                    // if (array[i] == null)
                    //     continue;
					t.print(" " + f.getName() + "[" + i + "]:");
					indentMultiple++;
					inspect(array[i], recursive);
                    indentMultiple--;
				}
			}
			else if (t.isCollectionClass(f.getType())) {
				t.print("   Object's class:  " + t.simplifyTypeName(f.getType()) +"\n");
				List<Object> collection = (ArrayList<Object>)fI.getFieldValue(f, obj);
				for(int i = 0; i < collection.size(); i++){
                    // if (collection.get(i) == null)
                    //     continue;
					t.print(" " + f.getName() + "[" + i + "]:");
					indentMultiple++;
					inspect(collection.get(i), recursive);
                    indentMultiple--;
				}
			}
			else
				inspect(fI.getFieldValue(f, obj), recursive);
			t.print(" - - - - - - - - - - - - - -\n");
		}
		if (objectFields.size() > 0){
			indentMultiple--;
			t.print(" <<<<<<<<\n");
		}
	}



	public static void main (String[] arg)  
	{ 
		// // just for testing purposes
		// Serializer ser = new Serializer();
		// Deserializer des = new Deserializer();
		// ObjectCreator oc = new ObjectCreator();
		// Visualizer v = new Visualizer();

		// // Object obj = oc.createSimpleObject(true);
		// // Object obj = oc.createObjThatHasOtherObjects(true);
		// // Object obj = oc.createObjWithArrayOfPrimitives(true);
		// // Object obj = oc.createObjWithArrayOfObjects(true);
		// Object obj = oc.createObjWithCollectionOfObjects(true);

		// obj = des.deserialize(ser.serialize(obj));

		// v.inspect(obj, true);
	}
}