package com.cardreaderapp.models;

import java.util.List;
import java.util.Vector;

public class Share {
    private List<Card> mCards;
    private String mSender;

    public String getSender(){  return this.mSender;   }
    public List<Card> getCards(){  return this.mCards;   }

    public void setSender(String sender){  this.mSender = sender;   }
    public void setCards(Vector<Card> cards){  this.mCards = cards;   }

    public Share(String sender, Vector<Card> cards)
    {
        mSender = sender;
        mCards = cards;
    }
}
