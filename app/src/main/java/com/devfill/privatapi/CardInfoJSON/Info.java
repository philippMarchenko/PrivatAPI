package com.devfill.privatapi.CardInfoJSON;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Info {

    @SerializedName("cardbalance")
    @Expose
    public Cardbalance cardbalance;

}