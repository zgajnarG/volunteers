package org.example.volunteers;

import org.example.volunteers.models.Volunteer;
import org.example.volunteers.services.Cleaner;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NameTest {
    @BeforeAll
    public static void globalSetUp() {
        //System.out.println("Ce code est exécuté une seule fois avant l'ensemble des tests");

    }

    @BeforeEach
    public void setUp() {
        //System.out.println("Ce code est exécuté avant chaque test");
    }

//    @Test
//    public void shouldPass() {
//        // Arrange
//        List<Volunteer> volunteersWithCorrectName = new ArrayList<>();
//
//
//        // Act
//
//
//        // Assert
//    }

    @Test
    public void shouldRemoveWithDuplicateFullName() throws Exception {

        List<Volunteer> volunteers = new ArrayList<>();
        Volunteer duplicateVolunteerFName = new Volunteer("Marine", "Dupont", "MDP", "marine.dupont@test.fr", "+33670000000");

        volunteers.add(new Volunteer("Marine", "Dupont", "MD", "marine.dupont@test.fr", "+33600000000"));
        volunteers.add(duplicateVolunteerFName);

        List<Volunteer> results = Cleaner.removeDuplicateByFullName(volunteers);

        Assertions.assertEquals(results.size(), 1);

    }

    @Test
    public void shouldThrowExceptionForMalformedNames() throws Exception {

        List<Volunteer> volunteers = new ArrayList<>();
        Volunteer malformedVolunteerFName = new Volunteer("Marine", "Dupont?", "MD", "marine.dupont@test.fr", "+33600000000");

        volunteers.add(new Volunteer("Marine", "Dupont", "MDP", "marine.dupont@test.fr", "+33670000000"));
        volunteers.add(malformedVolunteerFName);

        Assertions.assertThrows(Exception.class, () -> Cleaner.removeDuplicateByFullName(volunteers), "Malformed name for user "+malformedVolunteerFName.geteMail());

    }

//    @AfterEach
//    public void tearDown() {
//        System.out.println("Ce code est exécuté après chaque test");
//    }
//
//    @AfterAll
//    public static void globalTearDown() {
//        System.out.println("Ce code est exécuté une seule fois après l'ensemble des tests");
//    }
}
