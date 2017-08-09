package com.devfill.privatapi.CardInfoJSON;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("oper")
    @Expose
    public String oper;
    @SerializedName("info")
    @Expose
    public Info info;

}