/* 
    An Object that contains Objects.
    This is used by the ObjectCreator program as a part of testing the main Serializer/Deserializer functionality of this project.

    Written by Cody_NS | Fall 2023
*/

public class House {

    public double price;
    public Person owner;
    public House circularRef = this;  // used for testing the Serializer/Deserializer to ensure the can handle circular references properly (ie: they don't break)


    public House() {
        price = 0.0;
        owner = null;
    }
    public House(double price, Person owner) {
        this.price = price;
        this.owner = owner;
    }

    public boolean equals(Object other) {
        Boolean isEqual = false;
        if (other != null && other instanceof House) {
            House otherHouse = (House)other;
            isEqual = owner.equals(otherHouse.owner) && 
                      Math.abs(price - otherHouse.price) < 0.001;
        }
        return isEqual;
    }
    public double getPrice() {
        return price;
    }
    public Person getOwner() {
        return owner;
    }
}