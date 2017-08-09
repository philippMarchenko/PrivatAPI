package com.devfill.privatapi;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.devfill.privatapi.CardInfoJSON.CardInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class MainActivity extends AppCompatActivity {

    Timer myTimer = new Timer(); // Создаем таймер
    String LOG_TAG = "privatBankLogs";
    boolean runTimer = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(LOG_TAG,"onCreate  ");

        task1();

    }
    private void task1() {

        final Handler uiHandler = new Handler();
        myTimer.schedule(new TimerTask() { // Определяем задачу
            @Override
            public void run() {
                if(runTimer){
                    Log.i(LOG_TAG,"myTimer run ");
                    try {
                        Gson gson = new GsonBuilder()
                                .setLenient()
                                .create();

                        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

                        Retrofit retrofit = new Retrofit.Builder()
                                .client(client)
                                .baseUrl("http://api.mkdeveloper.ru")
                                //.addConverterFactory(GsonConverterFactory.create(gson))
                                .addConverterFactory(SimpleXmlConverterFactory.create())
                                .build();

                        CardInfoAPI cardInfoAPI = retrofit.create(CardInfoAPI.class);

                        cardInfoAPI.getCardInfo("balance_card").enqueue(new Callback<CardInfo>() {
                            @Override
                            public void onResponse(Call<CardInfo> call, Response<CardInfo> response) {

                                Log.i(LOG_TAG,"onResponse getBalance " + response.body().getBalance()+ "\n");
                                Log.i(LOG_TAG,"onResponse getAccName " + response.body().getAccName() + "\n");
                                Log.i(LOG_TAG,"onResponse getAccType " + response.body().getCardNumber() + "\n");
                                Log.i(LOG_TAG,"onResponse getBalDate " + response.body().getBalDate() + "\n");
                                Log.i(LOG_TAG,"onResponse getCurrency " + response.body().getCurrency() + "\n");

                            }

                            @Override
                            public void onFailure(Call<CardInfo> call, Throwable t) {
                                Log.i(LOG_TAG,"onResponse getBalance "  + call.toString() + "\n" );

                                Log.i(LOG_TAG,"onFailure. Ошибка REST запроса getBalance " + t.getMessage());
                            }
                        });
                    }
                    catch(Exception e){

                        Log.i(LOG_TAG,"Ошибка REST запроса к серверу  getBalance " + e.getMessage());
                    }
                }


                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {

                    }
                });

            };

        }, 0L, 6L * 1000); // интервал - 60000 миллисекунд, 0 миллисекунд до первого запуска.
    }

    @Override
    public void onDestroy() {
        runTimer = false;
        Log.i(LOG_TAG,"onDestroy  ");

        super.onDestroy();
    }
}
