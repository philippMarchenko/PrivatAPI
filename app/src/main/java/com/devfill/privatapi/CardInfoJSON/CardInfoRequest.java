package com.devfill.privatapi.CardInfoJSON;

public class CardInfoRequest {

    public Data data;


    public String getBalance (){

        return data.info.cardbalance.balance;
    }

    public String getBalDate (){

        return data.info.cardbalance.balDate;
    }

    public String getAccName (){

        return data.info.cardbalance.card.accName;
    }

    public String getCardNumber (){

        return data.info.cardbalance.card.cardNumber;
    }

    public String getCurrency (){

        return data.info.cardbalance.card.currency;
    }


}