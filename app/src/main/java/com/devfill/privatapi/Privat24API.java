package com.devfill.privatapi;


import com.devfill.privatapi.CardInfoJSON.CardInfoRequest;
import com.devfill.privatapi.CardInfoXML.CardInfoXML;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Privat24API {

    @GET("/query_p24.php")
    Call<CardInfoXML> getCardInfo(
            @Query(value = "event") String event);

    @GET("/query_p24.php")
    Call<PayPhoneResponse> sendPayPhone(
            @Query(value = "event") String event,
            @Query(value = "amt") String amt);
}