package com.cardreaderapp.api;

import com.cardreaderapp.models.Card;

import java.util.concurrent.ExecutionException;

public interface ICard {
    Card GetCardData(String imageName, String base64EncodedData) throws ExecutionException, InterruptedException;
}
