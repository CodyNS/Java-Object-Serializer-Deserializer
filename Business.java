/* 
    An Object that contains a collection (ArrayList) of Objects.
    This is used by the ObjectCreator program as a part of testing the main Serializer/Deserializer functionality of this project.

    Written by Cody_NS | Fall 2023
*/

import java.util.*;


public class Business {

    public String name;
    public String address;
    public ArrayList<Object> customers;


    public Business() {
        name = null;
        address = null;
        customers = new ArrayList<Object>();
    }
    public Business(String name, String address, ArrayList<Object> customers) {
        this.name = name;
        this.address = address;
        this.customers = new ArrayList<Object>(customers);
    }

    public String getName() {return name;}
    public String getAddress() {return address;}
    public ArrayList<Object> getCustomers() {return new ArrayList<Object>(customers);}

    public boolean equals(Object other) {
        Boolean isEqual = false;
        if (other != null && other instanceof Business) {
            Business otherBiz = (Business)other;
            isEqual = name.equals(otherBiz.name) && address.equals(otherBiz.address) && customers.equals(otherBiz.customers);
        }
        return isEqual;
    }
}