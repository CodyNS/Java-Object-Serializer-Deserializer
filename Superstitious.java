/* 
    An Object that contains an array of primitive values.
    This is used by the ObjectCreator program as a part of testing the main Serializer/Deserializer functionality of this project.

    Written by Cody_NS | Fall 2023
*/

public class Superstitious
{
    public String name;
    public int[] luckyNumbers = new int[3];


    public Superstitious() {
        name = "not set";
    }
    public Superstitious(String name, int[] luckyNumbers) {
        this.name = name;
        for (int i = 0; i < luckyNumbers.length; i++)
            this.luckyNumbers[i] = luckyNumbers[i];
    }

    public boolean equals(Object other) {
        Boolean isEqual = false;
        if (other != null && other instanceof Superstitious) {
            Superstitious otherSuper = (Superstitious)other;
            isEqual = name.equals(otherSuper.name);
            if (isEqual)
                for (int i = 0; i < luckyNumbers.length; i++)
                    if (luckyNumbers[i] != otherSuper.luckyNumbers[i])
                        return false;
        }
        return isEqual;
    }
}