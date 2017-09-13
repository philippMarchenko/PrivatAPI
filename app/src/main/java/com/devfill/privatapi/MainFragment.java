package com.devfill.privatapi;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.devfill.privatapi.CardInfoXML.CardInfoXML;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;


public class MainFragment extends Fragment implements PayPhoneAdapter.IPayPhoneListener {

    TextView myBlance;
    ImageView updateButton;
    ProgressBar progressUpdate;

    DBHelper dbHelper;

    private String LOG_TAG = "privatBankLogs";
    public static final String LOG_TAG_DB = "AddPayPhoneLogs";

    private FragmentTransaction ft;
    private PayPhoneAdapter payPhoneAdapter;
    private RecyclerView recyclerView;
    private List<PayPhone> payPhoneList = new ArrayList<>();

    Privat24API privat24Api;

    private static final int READ_CONTACTS_PERMISSION_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private boolean sentToSettings = false;
    private SharedPreferences permissionStatus;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.main_fragment, container, false);

        final AddPayPhoneFragment addPayPhoneFragment = new AddPayPhoneFragment();

        dbHelper = new DBHelper(getContext());

        recyclerView = (RecyclerView) rootview.findViewById(R.id.recycler_view_settings);
        payPhoneAdapter = new PayPhoneAdapter(getContext(), getActivity(), payPhoneList, this);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(payPhoneAdapter);

        setUpItemTouchHelper();
       // setUpAnimationDecoratorHelper();

        myBlance = (TextView) rootview.findViewById(R.id.myBalance);
        updateButton = (ImageView) rootview.findViewById(R.id.updateButton);
        progressUpdate = (ProgressBar) rootview.findViewById(R.id.progressUpdate);


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




        privat24Api = retrofit.create(Privat24API.class);


        updateCardInfo(privat24Api);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCardInfo(privat24Api);
                progressUpdate.setVisibility(View.VISIBLE);
                updateButton.setVisibility(View.INVISIBLE);

            }
        });

        FloatingActionButton fab = (FloatingActionButton) rootview.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.container, addPayPhoneFragment);
                ft.commit();

            }
        });

        initReminderList();

        showQueryPremission();



        return rootview;
    }

    public void showQueryPremission(){

        permissionStatus = getContext().getSharedPreferences("permissionStatus",getActivity().MODE_PRIVATE);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_CONTACTS)) {
                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Необходимо разрешение!");
                builder.setMessage("Этому приложению необходимо разрешениена просмотр контактов");
                builder.setPositiveButton("Запрсить!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS_PERMISSION_CONSTANT);
                    }
                });
                builder.setNegativeButton("Отмена!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
            else {
                //just request the permission
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS_PERMISSION_CONSTANT);
            }
            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(Manifest.permission.READ_CONTACTS,true);
            editor.commit();
        }
        else {
            //You already have the permission, just go ahead.
            proceedAfterPermission();
        }


      /*  @Override
        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode == EXTERNAL_STORAGE_PERMISSION_CONSTANT) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //The External Storage Write Permission is granted to you... Continue your left job...
                    proceedAfterPermission();
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        //Show Information about why you need the permission
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Need Storage Permission");
                        builder.setMessage("This app needs storage permission");
                        builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();


                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);


                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.show();
                    } else {
                        Toast.makeText(getBaseContext(),"Unable to get Permission",Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
*/
    }
    private void proceedAfterPermission() {
        //We've got the permission, now we can proceed further
       // Toast.makeText(getContext(), "У нас есть разрешение!", Toast.LENGTH_LONG).show();
    }
    public void updateCardInfo(Privat24API privat24API) {
        String netType = getNetworkType(getContext());
        if(netType == null)
            Toast.makeText(getActivity(), "Подключение к сети отсутствует!", Toast.LENGTH_LONG).show();
        else {
            try {


                privat24API.getCardInfo("balance_card").enqueue(new Callback<CardInfoXML>() {
                    @Override
                    public void onResponse(Call<CardInfoXML> call, Response<CardInfoXML> response) {

                        String balance = response.body().getData().getInfo().getCardbalance().getBalance();

                        myBlance.setText("₴ " + balance);
                        progressUpdate.setVisibility(View.INVISIBLE);
                        updateButton.setVisibility(View.VISIBLE);
                        //  Log.i(LOG_TAG,"onResponse getAccName " + response.body().getAccName() + "\n");
                        // Log.i(LOG_TAG,"onResponse getAccType " + response.body().getCardNumber() + "\n");
                        // Log.i(LOG_TAG,"onResponse getBalDate " + response.body().getBalDate() + "\n");
                        // Log.i(LOG_TAG,"onResponse getCurrency " + response.body().getCurrency() + "\n");

                    }

                    @Override
                    public void onFailure(Call<CardInfoXML> call, Throwable t) {
                        Log.i(LOG_TAG, "onResponse getBalance " + call.toString() + "\n");
                        Log.i(LOG_TAG, "onFailure. Ошибка REST запроса getBalance " + t.getMessage());

                        try {
                            Toast.makeText(getContext(), "Произошла ошибка запроса! " + t.getMessage(), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Log.i(LOG_TAG, "Не удалось вывести на экран сообщение! " + e.getMessage());
                        }
                        progressUpdate.setVisibility(View.INVISIBLE);
                        updateButton.setVisibility(View.VISIBLE);
                    }
                });
            } catch (Exception e) {

                Log.i(LOG_TAG, "Ошибка REST запроса к серверу  getBalance " + e.getMessage());
            }
        }
    }
    public String getNetworkType(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            return activeNetwork.getTypeName();
        }
        return null;
    }
    public void sendPayPhoneRequest(Privat24API privat24API, String phone,String amt) {
        String netType = getNetworkType(getContext());

        String amtNumber = amt.replaceAll("[^0-9]+", "");   //обрежем само число в сумме

        if(netType == null)
            Toast.makeText(getActivity(), "Подключение к сети отсутствует!", Toast.LENGTH_LONG).show();
        else {

            try {

                privat24API.sendPayPhoneToMyServer("pay_phone_balance",phone, amtNumber).enqueue(new Callback<PayPhoneResponse>() {
                    @Override
                    public void onResponse(Call<PayPhoneResponse> call, Response<PayPhoneResponse> response) {

                        PayPhoneResponse payPhoneResponse = response.body();


                        if (payPhoneResponse.getData().getPayment().getState().equals("1")) {
                            Toast.makeText(getContext(), "Успешное проведение платежа! Комиссия " + response.body().getData().getPayment().getComis(), Toast.LENGTH_LONG).show();
                        } else {
                            String message = payPhoneResponse.getData().getPayment().getMessage();
                            Toast.makeText(getContext(), "Ошибка проведения платежа: " + message, Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<PayPhoneResponse> call, Throwable t) {

                        Toast.makeText(getContext(), "Неудалось совершить платеж! ", Toast.LENGTH_LONG).show();

                        Log.i(LOG_TAG, "onFailure. Ошибка REST запроса sendPayPhoneToMyServer " + t.getMessage());


                    }
                });

            } catch (Exception e) {

                Log.i(LOG_TAG, "Ошибка REST запроса к серверу  sendPayPhoneToMyServer " + e.getMessage());
            }
        }
    }
    public void initReminderList() {

        PayPhone payPhone;
        payPhoneList.clear();

        // подключаемся к БД
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // делаем запрос всех данных из таблицы mytable, получаем Cursor
        Cursor c = db.query("mytable", null, null, null, null, null, null);

        if (c.moveToFirst()) {
            int nameColIndex = c.getColumnIndex("name");
            int numberColIndex = c.getColumnIndex("number");
            int amtColIndex = c.getColumnIndex("amt");


            do {
                //Log.d(LOG_TAG_DB, "name = " + c.getString(nameColIndex) + ", amt = " + c.getString(amtColIndex));
                payPhone = new PayPhone(c.getString(nameColIndex),c.getString(numberColIndex),c.getString(amtColIndex));
                payPhoneList.add(payPhone);

            } while (c.moveToNext());
        } else
            Log.d(LOG_TAG_DB, "0 rows");
        c.close();
        dbHelper.close();

        Log.d(LOG_TAG_DB, "Init list.Count payPhoneList " + payPhoneList.size());

        payPhoneAdapter.notifyDataSetChanged();

    }
    public void onClickPay(int position) {

        final int pos = position;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.MyDialogTheme);
        builder.setTitle("Внимание!");
        builder.setMessage("Вы действительно хотите совершить платеж на номер " + payPhoneList.get(position).getName() + ", " +
               payPhoneList.get(position).getNumber() + " на сумму " + payPhoneList.get(position).getAmt() + " ?");

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            sendPayPhoneRequest(privat24Api, payPhoneList.get(pos).getNumber(),payPhoneList.get(pos).getAmt());
                    }
                });

        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // negative button logic
                    }
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();



    }
    public void deleteItemFromDB(int position) {

        Log.d(LOG_TAG_DB, "deleteItemFromDB  position " + position);

        Log.d(LOG_TAG_DB, "payPhoneList  zise " + payPhoneList.size());

        Log.d(LOG_TAG_DB, " payPhoneList.get(position).getName() " + payPhoneList.get(position).getName());
        Log.d(LOG_TAG_DB, " payPhoneList.get(position).getAmt() " + payPhoneList.get(position).getAmt());

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // делаем запрос всех данных из таблицы mytable, получаем Cursor
        Cursor c = db.query("mytable", null, null, null, null, null, null);
        int x = db.delete("mytable", "name = ? AND amt = ?",    //по наименованию номера и сумме
                new String[]{payPhoneList.get(position).getName(), payPhoneList.get(position).getAmt()});


        Log.d(LOG_TAG_DB, "Удалено " + x + " елементов");


        if (c.moveToFirst()) {

            int nameColIndex = c.getColumnIndex("name");
            int amtColIndex = c.getColumnIndex("amt");

            do {
                // получаем значения по номерам столбцов и пишем все в лог
                Log.d(LOG_TAG_DB,
                        "name = " + c.getString(nameColIndex) +
                                ", amt = " + c.getString(amtColIndex));
                // переход на следующую строку
                // а если следующей нет (текущая - последняя), то false - выходим из цикла
            } while (c.moveToNext());
        } else
            Log.d(LOG_TAG_DB, "0 rows");

        c.close();
        dbHelper.close();
    }
    private void setUpItemTouchHelper() {

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();
                PayPhoneAdapter payPhoneAdapter = (PayPhoneAdapter) recyclerView.getAdapter();
                if (payPhoneAdapter.isUndoOn() && payPhoneAdapter.isPendingRemoval(position)) {
                    return 0;
                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int swipedPosition = viewHolder.getAdapterPosition();
                PayPhoneAdapter payPhoneAdapter = (PayPhoneAdapter) recyclerView.getAdapter();
                boolean undoOn = payPhoneAdapter.isUndoOn();
                if (undoOn) {
                    payPhoneAdapter.pendingRemoval(swipedPosition);
                } else {
                    deleteItemFromDB(swipedPosition);           //сначала удаляем с базы елемент
                    payPhoneAdapter.remove(swipedPosition);     //а потом из списка

                }
            }
        };
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.i(LOG_TAG, "MainFragment onResume");
    }
    @Override
    public void onPause() {
        super.onPause();

        Log.i(LOG_TAG, "MainFragment onPause");
    }

}
