package com.example.tictactoe;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class UserAdapter extends ArrayAdapter {

    public UserAdapter(Context context, ArrayList<User> users){
        super(context,0,users);
    }


    @Override
    public View getView(int position,View convertView,ViewGroup parent) {
        User user =(User)getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_list,parent,false);
        }
        TextView textUser = (TextView) convertView.findViewById(R.id.textId);
        TextView textName = (TextView) convertView.findViewById(R.id.textName);
        TextView textEmail = (TextView) convertView.findViewById(R.id.textEmail);

        textUser.setText(user.myId);
        textEmail.setText(user.email);
        textName.setText(user.name);

        return convertView;
    }
}
