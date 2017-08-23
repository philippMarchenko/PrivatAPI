package com.devfill.privatapi;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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

    Privat24API privat24API;


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
        //  setUpAnimationDecoratorHelper();

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

        privat24API = retrofit.create(Privat24API.class);

        updateCardInfo(privat24API);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCardInfo(privat24API);
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

        return rootview;
    }

    public void updateCardInfo(Privat24API privat24API) {
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

    public void sendPayPhoneRequest(Privat24API privat24API, String amt) {
        try {


            privat24API.sendPayPhone("pay_phone_balance", amt).enqueue(new Callback<PayPhoneResponse>() {
                @Override
                public void onResponse(Call<PayPhoneResponse> call, Response<PayPhoneResponse> response) {

                    Toast.makeText(getContext(), "Успешное проведение платежа! Комиссия " + response.body().getData().getPayment().getComis(), Toast.LENGTH_LONG).show();


                    // Log.i(LOG_TAG,"onResponse getAccName " + response.body(). + "\n");
                    // Log.i(LOG_TAG,"onResponse getAccType " + response.body().getCardNumber() + "\n");
                    // Log.i(LOG_TAG,"onResponse getBalDate " + response.body().getBalDate() + "\n");
                    // Log.i(LOG_TAG,"onResponse getCurrency " + response.body().getCurrency() + "\n");

                }

                @Override
                public void onFailure(Call<PayPhoneResponse> call, Throwable t) {

                    Toast.makeText(getContext(), "Неудалось совершить платеж! ", Toast.LENGTH_LONG).show();

                    Log.i(LOG_TAG, "onFailure. Ошибка REST запроса getBalance " + t.getMessage());


                }
            });
        } catch (Exception e) {

            Log.i(LOG_TAG, "Ошибка REST запроса к серверу  getBalance " + e.getMessage());
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
                        sendPayPhoneRequest(privat24API, payPhoneList.get(pos).getAmt());
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

            // we want to cache these and not allocate anything repeatedly in the onChildDraw method
            Drawable background;
            Drawable xMark;
            int xMarkMargin;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(Color.RED);
                xMark = ContextCompat.getDrawable(getContext(), R.drawable.ic_clear_24dp);
                xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                xMarkMargin = (int) getContext().getResources().getDimension(R.dimen.ic_clear_margin);
                initiated = true;
            }

            // not important, we don't want drag & drop
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

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;

                // not sure why, but this method get's called for viewholder that are already swiped away
                if (viewHolder.getAdapterPosition() == -1) {
                    // not interested in those
                    return;
                }

                if (!initiated) {
                    init();
                }

                // draw red background
                background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                background.draw(c);

                // draw x mark
                int itemHeight = itemView.getBottom() - itemView.getTop();
                int intrinsicWidth = xMark.getIntrinsicWidth();
                int intrinsicHeight = xMark.getIntrinsicWidth();

                int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
                int xMarkRight = itemView.getRight() - xMarkMargin;
                int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
                int xMarkBottom = xMarkTop + intrinsicHeight;
                xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);

                xMark.draw(c);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        };
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void setUpAnimationDecoratorHelper() {
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {

            // we want to cache this and not allocate anything repeatedly in the onDraw method
            Drawable background;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(Color.RED);
                initiated = true;
            }

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

                if (!initiated) {
                    init();
                }

                // only if animation is in progress
                if (parent.getItemAnimator().isRunning()) {

                    // some items might be animating down and some items might be animating up to close the gap left by the removed item
                    // this is not exclusive, both movement can be happening at the same time
                    // to reproduce this leave just enough items so the first one and the last one would be just a little off screen
                    // then remove one from the middle

                    // find first child with translationY > 0
                    // and last one with translationY < 0
                    // we're after a rect that is not covered in recycler-view views at this point in time
                    View lastViewComingDown = null;
                    View firstViewComingUp = null;

                    // this is fixed
                    int left = 0;
                    int right = parent.getWidth();

                    // this we need to find out
                    int top = 0;
                    int bottom = 0;

                    // find relevant translating views
                    int childCount = parent.getLayoutManager().getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View child = parent.getLayoutManager().getChildAt(i);
                        if (child.getTranslationY() < 0) {
                            // view is coming down
                            lastViewComingDown = child;
                        } else if (child.getTranslationY() > 0) {
                            // view is coming up
                            if (firstViewComingUp == null) {
                                firstViewComingUp = child;
                            }
                        }
                    }

                    if (lastViewComingDown != null && firstViewComingUp != null) {
                        // views are coming down AND going up to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    } else if (lastViewComingDown != null) {
                        // views are going down to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = lastViewComingDown.getBottom();
                    } else if (firstViewComingUp != null) {
                        // views are coming up to fill the void
                        top = firstViewComingUp.getTop();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    }

                    background.setBounds(left, top, right, bottom);
                    background.draw(c);

                }
                super.onDraw(c, parent, state);
            }

        });
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
