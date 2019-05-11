package com.cardreaderapp.models;

public class Card {
    private String personName;
    private String phoneNumber;
    private String company;
    private String address;
    private String email;
    private String website;
    private String imageUri;

    public String getPersonName(){return this.personName;}
    public String getPhoneNumber(){return this.phoneNumber;}
    public String getCompany(){return this.company;}
    public String getAddress(){return this.address;}
    public String getEmail(){return this.email;}
    public String getWebsite(){return this.website;}
    public String getImageUri(){return this.imageUri;}

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
    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
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

    public Card(String personName, String phoneNumber, String company, String address, String email, String website, String imageUri){
        this(personName, phoneNumber, company, address, email,website);
        this.imageUri = imageUri;
    }
}
