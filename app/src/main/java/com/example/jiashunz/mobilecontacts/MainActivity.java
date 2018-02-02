package com.example.jiashunz.mobilecontacts;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.os.Bundle;
import android.database.Cursor;
import android.util.Log;
import android.Manifest;
import android.widget.ImageView;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    final int PERMISSIONS_REQUEST_CONTACTS = 1;
    final String PERMISSIONS_READ_CONTACTS = "android.permission.READ_CONTACTS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupUI();
    }

    private void setupUI() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.contact_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Contact> contacts = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_CONTACTS);
        } else {
           contacts = getData();
        }

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


    @NonNull
    private List<Contact> getData() {

        List<Contact> contacts = new ArrayList<>();

        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

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

            /*try {
                InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(),
                        ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(id)));

                if (inputStream != null) {
                    photo = BitmapFactory.decodeStream(inputStream);
                }

                //assert inputStream != null;
                if (inputStream != null) inputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }*/

            if (photo != null) photo = BitmapFactory.decodeStream(openPhoto(new Long(id)));

            contacts.add(new Contact(number, name, photo));

        }
        return contacts;
    }

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
