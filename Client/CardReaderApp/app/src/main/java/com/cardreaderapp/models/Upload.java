package com.cardreaderapp.models;

public class Upload {

    public String mName;
    public String mPhone;
    public String mCompany;
    public String mAddress;
    public String mEmail;
    public String mWebsite;
    public String mImageUri;

    public Upload(){
        // Empty ctor - do not delete!
    }

    public Upload(String name, String phone, String company, String address, String email, String website , String imageUri){
        mName = name;
        mPhone = phone;
        mCompany = company;
        mAddress = address;
        mEmail = email;
        mWebsite = website;
        mImageUri = imageUri;
    }
}
