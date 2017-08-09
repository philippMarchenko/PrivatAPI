package com.devfill.privatapi;


import com.devfill.privatapi.CardInfoJSON.CardInfo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CardInfoAPI {

    @GET("/query_p24.php")
    Call<CardInfo> getCardInfo(
            @Query(value = "event") String event);
}