package org.example.volunteers.services;

import org.example.volunteers.models.Volunteer;

import java.util.ArrayList;
import java.util.List;

public class Cleaner {
    public static List<Volunteer> cleanUp(List<Volunteer> volunteers) {
        // This function should contain your dark magic.
        // For now, it simply returns a copy of the initial list.
        return new ArrayList<>(volunteers);
    }

    public static List<Volunteer> removeDuplicateByEmail() throws Exception{
        throw new Exception("not implemented");
    }

    public static List<Volunteer> removeDuplicateByFullName(List<Volunteer> volunteers){
        Volunteer volunteerTest = volunteers.get(0);
        int index = 0;
        for(Volunteer volunteer : volunteers){
            if(volunteer.firstName.equals(volunteerTest.firstName) && volunteer.lastName.equals(volunteerTest.lastName)){
                volunteers.remove(index);
            }
            index++;
        }

        return volunteers;
    }

    public  static List<Volunteer> removeDuplicateByPhoneNumber() throws Exception{
        throw new Exception("not implemented");
    }
}
