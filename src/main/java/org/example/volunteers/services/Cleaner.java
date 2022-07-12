package org.example.volunteers.services;

import org.example.volunteers.models.Volunteer;
import org.example.volunteers.models.VolunteerEmailError;
import org.example.volunteers.models.VolunteerNameError;
import org.example.volunteers.models.VolunteerPhoneNumberError;

import java.util.*;
import java.util.stream.Collectors;
import java.util.ArrayList;


public class Cleaner {

    private Validations validators;
    public VolunteerEmailError emailValidator;
    public VolunteerNameError nameValidator;
    public VolunteerPhoneNumberError phoneNumberValidator;
    public List<Volunteer> allVolunteers;

    public Cleaner(List<Volunteer> volunteers){
        this.validators = new Validations();
        this.allVolunteers = volunteers;
    }

    public List<Volunteer> cleanUp() {
        // This function should contain your dark magic.
        // For now, it simply returns a copy of the initial list.
        Set<Volunteer> volunteersToRemove = new HashSet<>();
        this.checkEmails();
        this.checkPhoneNumbers();
        this.checkNames();
        volunteersToRemove.addAll(this.emailValidator.noEmail);
        volunteersToRemove.addAll(this.phoneNumberValidator.noPhoneNumber);
        volunteersToRemove.addAll(this.emailValidator.badFormatEmail);
        volunteersToRemove.addAll(this.phoneNumberValidator.badFormatPhoneNumber);
        volunteersToRemove.addAll(this.nameValidator.malformedNames);
        HashMap<Boolean,Set<Volunteer>> duplicateVolunteers = this.cleanDuplicate();
        volunteersToRemove.addAll(duplicateVolunteers.get(false));

        List<Volunteer> allVolunteersCorrect = this.allVolunteers;
        allVolunteersCorrect.removeAll(volunteersToRemove);
        allVolunteersCorrect.addAll(duplicateVolunteers.get(true));
        return allVolunteersCorrect;
    }

    public HashMap<Boolean,Set<Volunteer>> cleanDuplicate(){
        HashMap<Boolean,Set<Volunteer>> mapResult = new HashMap<>();
        HashSet<Volunteer> badVolunteers = new HashSet<>();

        HashMap<Boolean,List<Volunteer>> mapEmail = this.cleanDuplicate(this.emailValidator.duplicateEmail , new ArrayList<>());
        badVolunteers.addAll(mapEmail.get(false));
        HashMap<Boolean,List<Volunteer>> mapPhone = this.cleanDuplicate(this.phoneNumberValidator.duplicatePhoneNumber, mapEmail.get(true));
        badVolunteers.addAll(mapPhone.get(false));
        HashMap<Boolean,List<Volunteer>> mapName = this.cleanDuplicate(this.nameValidator.duplicateName, mapPhone.get(true));
        badVolunteers.addAll(mapName.get(false));

        mapResult.put(false,badVolunteers);
        mapResult.put(true,mapName.get(true).stream().collect(Collectors.toSet()));
        return mapResult;
    }

    private HashMap<Boolean,List<Volunteer>> cleanDuplicate(HashMap<String,List<Volunteer>> mapToRemoveEquals ,List<Volunteer> cleanVolunteer){
        HashMap<Boolean,List<Volunteer>> mapToRemove = new HashMap<>();
        List<Volunteer> badVolunteer = new ArrayList<>();

        for ( String condition : mapToRemoveEquals.keySet()){
            List<Volunteer> volunteers = mapToRemoveEquals.get(condition);
            for(Volunteer volunteer : volunteers){
                if(!cleanVolunteer.stream().anyMatch(x-> x.equals(volunteer))){
                    List<Volunteer> sameVolunteers =  volunteers.stream().filter(x-> x.equals(volunteer)).collect(Collectors.toList());
                    if(sameVolunteers.size() > 1){
                        cleanVolunteer.add(volunteer);
                    }
                }
                badVolunteer.add(volunteer);
            }
        }
        mapToRemove.put(false,badVolunteer);
        mapToRemove.put(true,cleanVolunteer);
        return mapToRemove;
    }


    public void checkEmails(){
        ArrayList<Volunteer> checkVolunteersWithNoEmail = new ArrayList();
        HashMap<String , List<Volunteer>> mapEmailVolunteers = new HashMap<>();
        ArrayList<Volunteer> volunteersWithBadEmails =new ArrayList<>();

        for(Volunteer volunteer : this.allVolunteers){
            if(volunteer.getEmail() == null || volunteer.getEmail().isEmpty()){
                checkVolunteersWithNoEmail.add(volunteer);
                continue;
            }
            if(!this.validators.validateEmailAddress(volunteer.getEmail())){
                volunteersWithBadEmails.add(volunteer);
                continue;
            }
            if(mapEmailVolunteers.containsKey(volunteer.getEmail())){
                List<Volunteer> emailVolunteers = mapEmailVolunteers.get(volunteer.getEmail());
                emailVolunteers.add(volunteer);
                mapEmailVolunteers.put(volunteer.getEmail() , emailVolunteers);
            }else{
                mapEmailVolunteers.put(volunteer.getEmail() , new ArrayList<>(Arrays.asList(volunteer)));
            }
        }
        HashMap<String , List<Volunteer>> mapDuplicateEmailVolunteers = mapEmailVolunteers
                .entrySet()
                .stream()
                .filter(x-> x.getValue().stream().count() > 1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (prev, next) -> next, HashMap::new));
        this.emailValidator= new VolunteerEmailError(checkVolunteersWithNoEmail , mapDuplicateEmailVolunteers , volunteersWithBadEmails);
    }

    public void checkNames() {

        HashMap<String, List<Volunteer>> mapVolunteerNames = new HashMap<>();
        ArrayList<Volunteer> volunteersWithMalformedNames = new ArrayList<>();

        for (Volunteer volunteer : this.allVolunteers) {

            if (volunteer.getFirstName() == null || volunteer.getFirstName().isEmpty()) {
                volunteersWithMalformedNames.add(volunteer);
                continue;
            }

            if (volunteer.getLastName() == null || volunteer.getLastName().isEmpty()) {
                volunteersWithMalformedNames.add(volunteer);
            }

            if (this.validators.validateFirstName(volunteer.getFirstName()) || this.validators.validateLastName(volunteer.getLastName())) {
                volunteersWithMalformedNames.add(volunteer);
            }

            if (mapVolunteerNames.containsKey(volunteer.getFirstName() + "." + volunteer.getLastName())) {
                List<Volunteer> storedDuplicateVolunteers = mapVolunteerNames.get(volunteer.getFirstName() + "." + volunteer.getLastName());
                int originalSize = storedDuplicateVolunteers.size();

                if (storedDuplicateVolunteers.size() > 0) {
                    boolean isDuplicated = false;
                    for (Volunteer storedVolunteer : storedDuplicateVolunteers) {
                        if (storedVolunteer.getEmail().equals(volunteer.getEmail())) {
                            isDuplicated = true;

                            break;
                        }
                    }
                    if (isDuplicated) {
                        storedDuplicateVolunteers.add(volunteer);
                    }
                } else {
                    storedDuplicateVolunteers.add(volunteer);
                }

                if (originalSize != storedDuplicateVolunteers.size()) {
                    mapVolunteerNames.put(volunteer.getFirstName() + "." + volunteer.getLastName(), storedDuplicateVolunteers);
                }

            } else {
                mapVolunteerNames.put(volunteer.getFirstName() + "." + volunteer.getLastName(), new ArrayList<>(Arrays.asList(volunteer)));
            }
        }

        HashMap<String, List<Volunteer>> mapDuplicateNamesVolunteers = mapVolunteerNames
                .entrySet()
                .stream()
                .filter(x-> x.getValue().stream().count() > 1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (prev, next) -> next, HashMap::new));
        this.nameValidator = new VolunteerNameError(mapDuplicateNamesVolunteers, volunteersWithMalformedNames);
    }

    public void checkPhoneNumbers(){
        ArrayList<Volunteer> volunteersWithoutPhoneNumber = new ArrayList();
        HashMap<String,List<Volunteer>> volunteersMappedByPhoneNumber = new HashMap<>();
        ArrayList<Volunteer> volunteersWithBadPhoneNumber = new ArrayList<>();

        for(Volunteer volunteer : this.allVolunteers){
            String volunteerPhone = volunteer.getPhone();
            if(volunteerPhone == null || volunteerPhone.isEmpty()){
                volunteersWithoutPhoneNumber.add(volunteer);
                continue;
            }
            if(!this.validators.validatePhoneNumber(volunteerPhone)){
                volunteersWithBadPhoneNumber.add(volunteer);
                continue;
            }

            String formattedVolunteerPhone = formatPhoneNumber(volunteerPhone);
            if(volunteersMappedByPhoneNumber.containsKey(formattedVolunteerPhone)){
                List<Volunteer> phoneNumberVolunteers = volunteersMappedByPhoneNumber.get(formattedVolunteerPhone);
                phoneNumberVolunteers.add(volunteer);
                volunteersMappedByPhoneNumber.put(formattedVolunteerPhone, phoneNumberVolunteers);
            } else {
                volunteersMappedByPhoneNumber.put(formattedVolunteerPhone, new ArrayList<>(Arrays.asList(volunteer)));
            }
        }
        HashMap<String , List<Volunteer>> mapDuplicatePhoneNumberVolunteers = volunteersMappedByPhoneNumber
                .entrySet()
                .stream()
                .filter(x-> x.getValue().stream().count() > 1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (prev, next) -> next, HashMap::new));
        this.phoneNumberValidator = new VolunteerPhoneNumberError(volunteersWithoutPhoneNumber, mapDuplicatePhoneNumberVolunteers, volunteersWithBadPhoneNumber);
    }

    public  static String formatPhoneNumber(String phoneNumber){
        return phoneNumber.replaceFirst("\\+\\d{2}","0")
                .replaceFirst("\\(0\\)", "")
                .replace(".", "")
                .replace("-", "")
                .replace(" ", "");
    }

}
