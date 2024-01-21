/* 
    An Object that contains only primitive fields (I'm considering Strings primitive, even though that's not really true).
    This is used by the ObjectCreator program as a part of testing the main Serializer/Deserializer functionality of this project.

    Written by Cody_NS | Fall 2023
*/

public class Person
{
	public String name;
	public int age;


	public Person() {
		name = null;
		age = 0;
	}
	public Person(String name, int age) {
		this.name = name;
		this.age = age;
	}

	public boolean equals(Object other) {
        Boolean isEqual = false;
        if (other != null && other instanceof Person) {
            Person otherP = (Person)other;
            isEqual = name.equals(otherP.name) && age == otherP.age;
        }
        return isEqual;
    }
	public String getName() {
		return name;
	}
	public int getAge() {
		return age; 
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setAge(int age) {
		this.age = age; 
	}
}