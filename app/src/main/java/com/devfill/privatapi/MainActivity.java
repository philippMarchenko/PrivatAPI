package com.devfill.privatapi;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.devfill.privatapi.CardInfoJSON.CardInfoRequest;
import com.devfill.privatapi.CardInfoXML.CardInfoXML;
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



    FragmentTransaction ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainFragment mainFragment = new MainFragment();

        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, mainFragment);
        ft.commit();






        Log.i(LOG_TAG,"onCreate  ");


    }




/*
    private void task1() {

        final Handler uiHandler = new Handler();
        myTimer.schedule(new TimerTask() { // Определяем задачу
            @Override
            public void run() {
                if(runTimer){
                    Log.i(LOG_TAG,"myTimer run ");



                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {

                    }
                });

            };

        }, 0L, 6L * 1000); // интервал - 60000 миллисекунд, 0 миллисекунд до первого запуска.
    }*/

    @Override
    public void onDestroy() {
        runTimer = false;
        Log.i(LOG_TAG,"onDestroy  ");

        super.onDestroy();
    }
}
