package com.cardreaderapp.models;

public class Card {
    private String personName;
    private String phoneNumber;
    private String company;
    private String address;
    private String email;
    private String website;

    public String GetPersonName(){return this.personName;}
    public String GetPhoneNumber(){return this.phoneNumber;}
    public String GetCompany(){return this.company;}
    public String GetAddress(){return this.address;}
    public String GetEmail(){return this.email;}
    public String GetWebsite(){return this.website;}

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Card(){

    }
    public Card(String personName, String phoneNumber, String company, String address, String email, String website){
        this.personName = personName;
        this.phoneNumber = phoneNumber;
        this.company = company;
        this.address = address;
        this.email = email;
        this.website = website;
    }
}
