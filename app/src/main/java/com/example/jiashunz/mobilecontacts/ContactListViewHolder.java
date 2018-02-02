package com.example.jiashunz.mobilecontacts;

/**
 * Created by zhoujiashun on 1/31/18.
 */

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ContactListViewHolder extends RecyclerView.ViewHolder{
    TextView phoneNumberTextView;
    TextView contactNameTextView;
    ImageView contactPhotoImageView;

    public ContactListViewHolder(@NonNull View itemView) {
        super(itemView);

        phoneNumberTextView = (TextView) itemView.findViewById(R.id.phone_number);
        contactNameTextView = (TextView) itemView.findViewById(R.id.contact_name);
        contactPhotoImageView = (ImageView) itemView.findViewById(R.id.contact_photo);
    }

}
