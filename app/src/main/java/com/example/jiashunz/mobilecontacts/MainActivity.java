package com.example.jiashunz.mobilecontacts;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.os.Bundle;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextWatcher;
import android.Manifest;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    final int PERMISSIONS_REQUEST_CONTACTS = 1;
    final String PERMISSIONS_READ_CONTACTS = "android.permission.READ_CONTACTS";
    SwipeRefreshLayout refreshLayout = null;
    EditText searchEditText = null;
    Button cancelButton = null;
    List<Contact> contacts = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupEditListener();
        setupRefreshListener();
        setupUI();
    }

    /**
     * This method is used to set up listener on edit text view.
     */
    private void setupEditListener() {
        searchEditText = (EditText) findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Listen to text change and search the list
                searchList(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        searchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    cancelButton.setVisibility(Button.VISIBLE);
                    searchEditText.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                } else {
                    cancelButton.setVisibility(Button.GONE);
                    searchEditText.setGravity(Gravity.CENTER);
                    //Hide soft keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
                }
            }
        });
        cancelButton = (Button) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.main_layout).requestFocus();
                searchEditText.setText("");
            }
        });
    }

    /**
     * This method is used to search contact list based on keyword s and update the view.
     * @param s keyword
     */
    public void searchList(CharSequence s) {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.contact_recycler_view);
        List<Contact> resultList = new ArrayList<>();
        for (int i = 0; i < contacts.size(); i++) {
            Contact contact = contacts.get(i);
            //Case insensitive search applied here
            if (contact.contactName.toLowerCase().indexOf(s.toString().toLowerCase()) != -1) {
                resultList.add(contact);
            }
        }
        //Update list on view
        recyclerView.setAdapter(new ContactListAdapter(resultList));
    }

    /**
     * This method is used to set up Refresh Listener on swipeRefreshLayout
     */
    private void setupRefreshListener() {
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //When refresh, check if is in search list mode
                if (!searchEditText.isFocused()) {
                    // Refresh items
                    refreshList();
                } else {
                    // Simply finish refresh
                    onRefreshComplete();
                }
            }
        });

    }

    /**
     * This method is used to get contact list agian and update view
     */
    private void refreshList() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.contact_recycler_view);

        getData();
        //Update List on view
        recyclerView.setAdapter(new ContactListAdapter(contacts));

        onRefreshComplete();
    }

    /**
     * This method is used to clear refresh animation
     */
    private void onRefreshComplete() {
        refreshLayout.setRefreshing(false);
    }

    /**
     * This method is used to bind data to view
     */
    private void setupUI() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.contact_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //Check user permission, request permission if doesn't have
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_CONTACTS);
        } else {
           getData();
        }
        //Update list on view
        recyclerView.setAdapter(new ContactListAdapter(contacts));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        for (int index = 0; index < permissions.length; index++) {
            if (permissions[index].equals(PERMISSIONS_READ_CONTACTS) && grantResults[index] == 0) {
                setupUI();
                break;
            }
        }
    }

    /**
     * This method is used to get contact list from cell phone.
     */
    @NonNull
    private void getData() {
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        contacts = new ArrayList<>();

        int idIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);
        int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);

        while (cursor.moveToNext()) {
            String id = cursor.getString(idIndex);
            String name = cursor.getString(nameIndex);
            String number = null;
            Bitmap photo = null;
            Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
            if (phoneCursor.moveToFirst()) {
                int tmp = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                number = phoneCursor.getString(tmp);
            }

            photo = BitmapFactory.decodeStream(openPhoto(new Long(id)));

            contacts.add(new Contact(number, name, photo));

        }
        Collections.sort(contacts, new Comparator<Contact>() {
            @Override
            public int compare(Contact a, Contact b) {
                return a.contactName.compareTo(b.contactName);
            }
        });
    }

    /**
     * This method is used to open a photo based on contactId
     * @param contactId unique id in contact list
     * @return ByteArrayInputStream if photo is not null
     */
    public InputStream openPhoto(long contactId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = getContentResolver().query(photoUri,
                new String[] {ContactsContract.Contacts.Photo.PHOTO}, null, null, null);
        if (cursor == null) {
            return null;
        }
        try {
            if (cursor.moveToFirst()) {
                byte[] data = cursor.getBlob(0);
                if (data != null) {
                    return new ByteArrayInputStream(data);
                }
            }
        } finally {
            cursor.close();
        }
        return null;
    }
}
