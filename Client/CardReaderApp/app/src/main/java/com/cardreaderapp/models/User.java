package com.cardreaderapp.models;

import java.util.List;

public class User {
    private String m_id;
    private List<Card> m_cards;

    public String GetId(){  return this.m_id;   }
    public List<Card> GetCards(){  return this.m_cards;   }
}
