package com.cardreaderapp.models;

import java.util.Vector;

public class User {
    private String mName;
    private String mEmail;
    private Boolean mIsPro;
    private String mToken;
    private Vector<Card> mCards;
    private Vector<Share> mShares;

    public String getName(){  return this.mName;   }
    public String getEmail(){ return this.mEmail; }
    public Boolean isPro() { return this.mIsPro; }
    public String getToken() { return this.mToken; }
    public Vector<Card> getCards(){  return this.mCards;   }
    public Vector<Share> getShares() { return this.mShares; }

    public User(String name, String email, Boolean isPro, String token){
        mName = name;
        mEmail = email;
        mIsPro = isPro;
        mToken = token;
    }

    public User(String name, String email, Boolean isPro, String token, Vector<Card> cards){
        this(name, email, isPro, token);
        mCards = cards;
    }

    public User(String name, String email, Boolean isPro, String token, Vector<Card> cards, Vector<Share> shares){
        this(name, email, isPro, token);
        mCards = cards;
        mShares = shares;
    }
}
