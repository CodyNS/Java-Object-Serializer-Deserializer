/*
    A super simple ID mapper class that maps objects' identityHashCode to a simple integer value, starting at 0 for the first one, 1 for the second one, and so on....

    Use: .put(System.identityHashCode(<your object>)) to add your object to the ID map and give it a unique, simpler (smaller) integer value ID that starts at 0 for the first item you add, 1 for the second one, and so on....

    You can then get that object's simple ID from the map by using: .get(System.identityHashCode(<your object>))

    ----- Why use this instead of Java's IdentityHashMap data type? -------------
    I made this because I tried using Java's IdentityHashMap data type/data structure, but it does not seem to work with Integer values > 127 (ie: greater than 8 bits, signed?). I believe it has something to do with how the Integer class caches values... I'm not sure exactly. I even tried using Strings for the keys, but again: it's not identifying the keys I'm using: large integers represented in String form: it's just not working for me, and I've wasted enough time trying to figure it out.

    ex:
        int idCounter = 0;
        IdentityHashMap<String, Integer> idHashMap = new IdentityHashMap<>();
        Person p = new Person("Cody_NS", 69);
        String key = "" + System.identityHashCode(simp);
        idHashMap.put(key, idCounter++);
        // ... doesn't work with keys > 127, which is basically all of them when using the hash code of Objects as the keys


    I'm not messing with ^ this IdentityHashMap stuff any more. It's going to be faster for me to just write a simple ID map class for this purpose.

    Hence, this class.
    ----------------------------------------


    Written by Cody_NS | Fall 2023
*/


import java.util.*;

public class CodysSimpleIDmap {

    private int size = 0;
    private int maxSize = 1000;  // works for up to this many items
    public static final int NULLVALUE = -1;
    private int[] keys = new int[maxSize];
    private int[] values = new int[maxSize];
    

    public CodysSimpleIDmap() {
        // for (int i = 0; i < maxSize; i++) {
        //     keys[i] = NULLVALUE;
        //     values[i] = NULLVALUE;
        // }
        // // nah. Not needed.
    }

    public int get(int key) {
        for (int i = 0; i < size; i++) {
            if (keys[i] == key)
                return values[i];
        }
        return NULLVALUE; // key not found
    }
    
    public boolean put(int key) {
        if (size >= maxSize || get(key) != NULLVALUE)
            return false;  // map is full. Resize it if needed (and add that functionality... I don't need it right now)
                           // or key is already in the map. Can't add it again: needs to be unique.
        keys[size] = key;
        values[size] = size; // used to be:  = value;
        size++;
        return true;  // key-value pair successfully added
    }

    public boolean containsKey(int key) {
        return get(key) == NULLVALUE ? false : true;
    } 

    public int size() {
        return size;
    }
}