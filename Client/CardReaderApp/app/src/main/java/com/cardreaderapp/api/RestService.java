package com.cardreaderapp.api;

import com.cardreaderapp.models.Card;
import com.google.gson.Gson;

public class RestService implements ICard {

    private Gson gson;
    private static final RestService service = new RestService();

    private RestService(){
        gson = new Gson();
    }

    public static RestService GetRestService(){
        return service;
    }

    @Override
    public Card GetCardData(String base64EncodedData) {
        //TODO: Send http request with base 64 encoded data
        
        String cardData = "{" +
                "\"personName\": \"Moti Levi\"," +
                "\"phoneNumber\": \"08-678943212\"," +
                "\"company\": \"Apple\"," +
                "\"address\": \"Hamasger 4, Tel Aviv\"," +
                "\"website\": \"www.apple.com\"," +
                "\"email\": \"Moti.Levi22@gmail.com\"" +
                "}";

        return gson.fromJson(cardData, Card.class); // deserializes json into card
    }
}
