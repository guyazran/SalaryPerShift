package com.guyazran.SalaryTracker.HumanResources;

/**
 * Created by guyazran on 11/14/15.
 */
public class Employee {
    private String firstName;
    private String lastName;

    public Employee(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Employee(String name){
        setName(name);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setName(String name){

        if (name == null || name.equals("")){
            setFirstName("");
            setLastName("");
            return;
        }

        String nameFromFirstLetter = name;
        while (name.charAt(0) == ' '){
            if (nameFromFirstLetter.length() == 1){
                setFirstName("");
                setLastName("");
                return;
            }
            nameFromFirstLetter = nameFromFirstLetter.substring(1);
        }

        int indexOfNextSpace = nameFromFirstLetter.indexOf(' ');
        if (indexOfNextSpace == -1){
            setFirstName(nameFromFirstLetter);
            setLastName("");
            return;
        }

        setFirstName(nameFromFirstLetter.substring(0, indexOfNextSpace));
        setLastName(nameFromFirstLetter.substring(indexOfNextSpace + 1));
    }
}
