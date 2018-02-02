package com.example.jiashunz.mobilecontacts;

import android.support.v7.widget.RecyclerView;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        ((ContactListViewHolder) holder).contactPhotoImageView.setImageBitmap(contact.contactPhoto);
    }

    public int getItemCount() {
        return data.size();
    }
}
