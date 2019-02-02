package com.cardreaderapp.api;

import com.cardreaderapp.models.Card;

public interface ICard {
    Card GetCardData(String base64EncodedData);
}
