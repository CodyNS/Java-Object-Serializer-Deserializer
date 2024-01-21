/* 
    Some helper methods for simplifying the visualization of a Java Object (used in conjunction with Visualizer.java).

    Written by Cody_NS | Fall 2023
*/

import java.util.*;
import java.lang.reflect.*;
import java.lang.IllegalAccessException;


public class InspectorTools
{
    String parametersForItem(Member m)
    {
        Class[] params = {};
        if (m instanceof Constructor) params = ((Constructor)m).getParameterTypes();
        else if (m instanceof Method) params = ((Method)m).getParameterTypes();
        else {
            print("\n\nERROR: invalid parameter type passed to parametersForItem(<Member>) method\n\n");
            System.exit(0);
        }
        String paramSt = "";
        for(int i = 0; i < params.length; i++)
            paramSt += (simplifyTypeName(params[i]) + (i == params.length-1 ? "" : ", "));
        return paramSt;
    }
    String modifiersForItem(Member m)
    {
        String modifierSt = Modifier.toString(m.getModifiers());
        return " " + modifierSt + (modifierSt == "" ? "" : " ");
    }
    static String simplifyTypeName(Class dataType)
    {
        if (dataType.isPrimitive())
            return dataType.getName();
        if (dataType.isArray())
            return simplifyArrayTypeName(dataType);
        return simplifiedClassName(dataType);
    }
    static String simplifiedClassName(Class dataType) // strips off the fully-qualified (leading) part of class name and returns String version of that
    {
        String s = dataType.getName().substring(dataType.getName().lastIndexOf(".")+1);
        if (s.contains("[L"))
            s = s.substring(s.lastIndexOf("[L")+2);
        if (s.charAt(s.length()-1) == ';') // if array type
            return s.substring(0, s.length()-1);
        return s;
    }
    static String simplifyArrayTypeName(Class dataType)
    {
        String typeSt = dataType.getName();
        int numDimensions = 0;
        for (int i = 0; i < typeSt.length(); i++){
            if (typeSt.charAt(i) == '[')  numDimensions++;
        }
        String arrayTypeSt = "" + (dataType.getComponentType().isPrimitive() ? dataType.getComponentType() : simplifiedClassName(dataType));
        arrayTypeSt += "[]".repeat(numDimensions);
        return arrayTypeSt;
    }
    void getAllClassesInHierarchyForClass(Class c, Set<Class> classesInObjHrchy)
    {
        if (c == null || classHasAlreadyBeenTraversed(c, classesInObjHrchy))
            return;
        // traverse up the inheritence hierarchy one level and repeat
        getAllClassesInHierarchyForClass(c.getSuperclass(), classesInObjHrchy);
        // get the interfaces:
        for (Class i : c.getInterfaces())
            getAllClassesInHierarchyForClass(i, classesInObjHrchy);
    }
    boolean classHasAlreadyBeenTraversed(Class c, Set<Class> classesInObjHrchy)
    {
        return ! classesInObjHrchy.add(c); // hashset .add() returns false if item is already present
    }
    boolean objectIsNull(Object obj)
    {
        if (obj == null) { 
            print(" (null object: nothing to inspect)\n");
            return true;
        }
        return false;
    }
    boolean isCollectionClass(Class c)
    {
        return Collection.class.isAssignableFrom(c);
    }
    boolean isPrimitiveArray(Field f){
        return f.getType().isArray() && f.getType().getComponentType().isPrimitive();
    }

    // a few basic helpers ---------------
    void print(String s){System.out.println(Visualizer.INDENT.repeat(Visualizer.indentMultiple) + s);}
    void print(){System.out.println();}
    void prnt(String s){System.out.print(s);} // no newline at end
    <T> T[] combineArrays(T[] first, T[] second)
    {
        T[] combined = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, combined, first.length, second.length);
        return combined;
    }
    String numberExtensionFor(int i)
    {
        String numStr = "" + i;
        char digitOfInterest = numStr.charAt(numStr.length()-1);
        switch (digitOfInterest){
            case '1': return "st";
            case '2': return "nd";
            case '3': return "rd";
            default: return "th";
        }
    }
}