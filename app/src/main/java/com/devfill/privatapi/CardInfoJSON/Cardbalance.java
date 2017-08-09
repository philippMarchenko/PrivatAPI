package com.devfill.privatapi.CardInfoJSON;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Cardbalance {

    @SerializedName("card")
    @Expose
    public Card card;
    @SerializedName("av_balance")
    @Expose
    public String avBalance;
    @SerializedName("bal_date")
    @Expose
    public String balDate;
    @SerializedName("bal_dyn")
    @Expose
    public String balDyn;
    @SerializedName("balance")
    @Expose
    public String balance;
    @SerializedName("fin_limit")
    @Expose
    public String finLimit;
    @SerializedName("trade_limit")
    @Expose
    public String tradeLimit;

}