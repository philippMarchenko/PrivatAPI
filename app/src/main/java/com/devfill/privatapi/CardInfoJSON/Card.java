package com.devfill.privatapi.CardInfoJSON;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Card {

    @SerializedName("account")
    @Expose
    public String account;
    @SerializedName("card_number")
    @Expose
    public String cardNumber;
    @SerializedName("acc_name")
    @Expose
    public String accName;
    @SerializedName("acc_type")
    @Expose
    public String accType;
    @SerializedName("currency")
    @Expose
    public String currency;
    @SerializedName("card_type")
    @Expose
    public String cardType;
    @SerializedName("main_card_number")
    @Expose
    public String mainCardNumber;
    @SerializedName("card_stat")
    @Expose
    public String cardStat;
    @SerializedName("src")
    @Expose
    public String src;

}