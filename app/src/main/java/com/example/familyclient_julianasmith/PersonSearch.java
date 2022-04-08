package com.example.familyclient_julianasmith;

public class PersonSearch {
    private final String name;
    private final String gender;

    public PersonSearch(String name, String gender){
        this.name = name;
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public String getGender() {return gender; }
}
