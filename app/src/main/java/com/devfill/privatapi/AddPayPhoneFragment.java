package com.devfill.privatapi;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.widget.SearchView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;

public class AddPayPhoneFragment extends Fragment {

    EditText setNumber;
    String strSpin;
    String strEt;
    Button savePayPhone,setAmt;

    private int routePos;
    private String amt = "1";
    private String name = null;
    private String number;


    DBHelper dbHelper;

    private String LOG_TAG = "privatBankLogs";

    public static final String LOG_TAG_DB = "AddPayPhoneLogs";

    // ArrayList
    ArrayList<ContactsList> selectUsers;
    List<ContactsList> temp;
    // Contact List
    ListView listView;
    // Cursor to load contacts list
    Cursor phones, email;
    // Pop up
    ContentResolver resolver;
    ContactsListAdapter contactsListAdapter;

    Button chooseInCotact,okContact;

    public AddPayPhoneFragment(){};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final MainFragment mainFragment = new MainFragment();
        dbHelper = new DBHelper(getContext());

        View rootView = inflater.inflate(R.layout.add_pay_phone_fragment, container, false);

        savePayPhone = (Button) rootView.findViewById(R.id.savePayPhone);
        chooseInCotact = (Button) rootView.findViewById(R.id.chooseInCotact);
        okContact = (Button) rootView.findViewById(R.id.okContact);
        setAmt = (Button) rootView.findViewById(R.id.setAmt);
        setNumber = (EditText) rootView.findViewById(R.id.setNumber);

        selectUsers = new ArrayList<ContactsList>();
        resolver = getContext().getContentResolver();
        listView = (ListView) rootView.findViewById(R.id.contacts_list);

        listView.setVisibility(View.INVISIBLE);
        okContact.setVisibility(View.INVISIBLE);

        chooseInCotact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.setVisibility(View.VISIBLE);
                okContact.setVisibility(View.VISIBLE);
                savePayPhone.setVisibility(View.INVISIBLE);
            }
        });

        okContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listView.setVisibility(View.INVISIBLE);
                okContact.setVisibility(View.INVISIBLE);
                savePayPhone.setVisibility(View.VISIBLE);

            }
        });

        savePayPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.container, mainFragment);
                ft.commit();

                if(name == null)
                    name = "Без имени";

                savePayPhone(name,number,amt);
            }
        });

        setNumber.setOnKeyListener(new View.OnKeyListener()
                                            {
                                                public boolean onKey(View v, int keyCode, KeyEvent event)
                                                {
                                                    if(event.getAction() == KeyEvent.ACTION_DOWN &&
                                                            (keyCode == KeyEvent.KEYCODE_ENTER))
                                                    {
                                                        number = setNumber.getText().toString();
                                                        return true;
                                                    }
                                                    return false;
                                                }
                                            }
        );

        setAmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NumberPicker picker = new NumberPicker(getContext());
                picker.setMinValue(1);
                picker.setMaxValue(100);

                MaterialNumberPicker.Builder numberPickerBuilder = new MaterialNumberPicker.Builder(getContext());

                numberPickerBuilder.minValue(1);
                numberPickerBuilder.maxValue(100);
                numberPickerBuilder.defaultValue(1);
                numberPickerBuilder.backgroundColor(getContext().getResources().getColor(R.color.drawer_background));
                numberPickerBuilder.separatorColor(getContext().getResources().getColor(R.color.colorAccent));
                numberPickerBuilder.textColor(getContext().getResources().getColor(R.color.colorPrimary));
                numberPickerBuilder.textSize(20);
                numberPickerBuilder.enableFocusability(false);
                numberPickerBuilder.wrapSelectorWheel(true);


                numberPickerBuilder.formatter(new NumberPicker.Formatter() {
                    @Override
                    public String format(int value) {
                        return value + " грн";
                    }
                });
                picker = numberPickerBuilder.build();
                picker.setValue(10);

                final NumberPicker finalPicker = picker;

                new AlertDialog.Builder(getActivity())
                        .setTitle("Укажите сумму")
                        .setView(picker)
                        .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                amt = Integer.toString(finalPicker.getValue()) + " грн";
                                setAmt.setText(amt);
                            }
                        })
                        .show();
            }
        });


        initContactList(rootView);

        return rootView;

    }
    public void savePayPhone(String name,String number,String amt){

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("mytable", null, null, null, null, null, null);
        ContentValues cv = new ContentValues();
        Log.d(LOG_TAG_DB, "--- Insert in mytable: ---");
        cv.put("name", name);
        cv.put("number", number);
        cv.put("amt", amt);

        long rowID = db.insert("mytable", null, cv);
        Log.d(LOG_TAG_DB, "row inserted, ID = " + rowID);


        Log.d(LOG_TAG_DB, "--- Rows in mytable: ---");

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

    private void initContactList(View view) {



        phones = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        LoadContact loadContact = new LoadContact();
        loadContact.execute();

    }

    // Load data on background
    class LoadContact extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Get Contact list from Phone

            if (phones != null) {
                Log.e("count", "" + phones.getCount());
                if (phones.getCount() == 0) {
                    Toast.makeText(getActivity(), "No contacts in your contact list.", Toast.LENGTH_LONG).show();
                }

                while (phones.moveToNext()) {
                    Bitmap bit_thumb = null;
                    String id = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                    String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String EmailAddr = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA2));
                    String image_thumb = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));
                    try {
                        if (image_thumb != null) {
                            bit_thumb = MediaStore.Images.Media.getBitmap(resolver, Uri.parse(image_thumb));
                        } else {
                            Log.e("No Image Thumb", "--------------");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ContactsList selectUser = new ContactsList();
                    selectUser.setThumb(bit_thumb);
                    selectUser.setName(name);
                    selectUser.setPhone(phoneNumber);
                    selectUser.setEmail(id);
                    selectUser.setCheckedBox(false);
                    selectUsers.add(selectUser);
                }
            } else {
                Log.e("Cursor close 1", "----------------");
            }
            //phones.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            contactsListAdapter = new ContactsListAdapter(selectUsers, getContext());
            listView.setAdapter(contactsListAdapter);

            // Select item on listclick
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    ContactsList data = selectUsers.get(i);
                    Log.i("search", "here---------------- listener" + data.getName());

                    setNumber.setText( data.getName() + ", " + data.getPhone());
                    setNumber.setHintTextColor(Color.YELLOW);

                    name = data.getName();
                    number = data.getPhone();

                }
            });

            listView.setFastScrollEnabled(true);
        }
    }

    public void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "AddPayPhoneFragment onResume");
    }
    @Override
    public void onPause() {
        super.onPause();

        Log.i(LOG_TAG, "AddPayPhoneFragment onPause");
    }

}