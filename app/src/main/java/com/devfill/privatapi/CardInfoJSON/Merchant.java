package com.devfill.privatapi.CardInfoJSON;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Merchant {

    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("signature")
    @Expose
    public String signature;

}