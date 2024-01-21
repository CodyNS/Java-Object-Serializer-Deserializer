/* 
    An Object that contains an array of Objects.
    This is used by the ObjectCreator program as a part of testing the main Serializer/Deserializer functionality of this project.

    Written by Cody_NS | Fall 2023
*/

public class Duplex {

    public double price;
    public Person[] owners = new Person[2];


    public Duplex() {}

    public Duplex(double price, Person[] owners) {
        if (owners.length != this.owners.length) {
            System.out.println("\nERROR: should be two owners of the Duplex, not " + owners.length + "\n");
            owners = null;
            return;
        }
        for (int i = 0; i < this.owners.length; i++)
            this.owners[i] = owners[i];
        this.price = price;
    }

    public boolean equals(Object other) {
        Boolean isEqual = false;
        if (other != null && other instanceof Duplex) {
            Duplex otherDuplex = (Duplex)other;
            isEqual = Math.abs(price - otherDuplex.price) < 0.001;
            if (isEqual)
                for (int i = 0; i < owners.length; i++)
                    if ( ! owners[i].equals(otherDuplex.owners[i]))
                        return false;
        }
        return isEqual;
    }
    public double getPrice() {
        return price;
    }
    public Person[] getOwners() {
        Person[] deepCopy = new Person[owners.length];
        for (int i = 0; i < owners.length; i++)
            deepCopy[i] = owners[i];
        return deepCopy;
    }
}