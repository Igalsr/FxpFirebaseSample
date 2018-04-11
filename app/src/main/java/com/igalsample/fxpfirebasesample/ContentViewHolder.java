package com.igalsample.fxpfirebasesample;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ContentViewHolder extends RecyclerView.ViewHolder {

    View mView;
    FirebaseAuth mAuth;
    DatabaseReference mDatabaseReference;

    private TextView mContentTxt;

    public ContentViewHolder(View itemView) {
        super(itemView);


        mView = itemView;
        mAuth = FirebaseAuth.getInstance();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("TextHolder");

        mContentTxt = mView.findViewById(R.id.recycler_txt);

    }

    public void setContent(String content) {

        mContentTxt.setText(content);
    }
}

