package com.example.jiashunz.mobilecontacts;

import android.support.v7.widget.RecyclerView;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by zhoujiashun on 1/31/18.
 */

public class ContactListAdapter extends RecyclerView.Adapter {
    private List<Contact> data;

    public ContactListAdapter(@NonNull List<Contact> data) {
        this.data = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_info_item, parent, false);
        return new ContactListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Contact contact = data.get(position);
        ((ContactListViewHolder) holder).phoneNumberTextView.setText(contact.phoneNumber);
        ((ContactListViewHolder) holder).contactNameTextView.setText(contact.contactName);
        if (contact.contactPhoto != null) {
            ((ContactListViewHolder) holder).contactPhotoImageView.setImageBitmap(contact.contactPhoto);
        } else {
            ((ContactListViewHolder) holder).contactPhotoImageView.setImageResource(R.drawable.purple);
        }
        ((ContactListViewHolder) holder).imageTextView.setText(convertName(contact.contactName));
    }

    private String convertName(String name) {
        String[] strings = name.split(" ");
        if (strings.length == 2) {
            return String.valueOf(strings[0].charAt(0)) + String.valueOf(strings[1].charAt(0));
        } else {
            return String.valueOf(strings[0].charAt(0));
        }

    }

    public int getItemCount() {
        return data.size();
    }
}
