package com.example.eventbookapp;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import Models.EventAttendees;

/**
 * Created by Anitt on 11/29/2017.
 */

/**
 * Class Name: CustomListView
 *
 * Functionality: To Customize the list view
 *
 */

public class CustomListView extends BaseAdapter{

    private Context context;
    public ArrayList<EventAttendees> gridDataattendee;
    LayoutInflater inflater;

    public CustomListView(Context context,ArrayList<EventAttendees>gridDataattendee){

        this.context = context;
        this.gridDataattendee = gridDataattendee;
        inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }



    @Override
    public int getCount() {
        return gridDataattendee.size();
    }

    @Override
    public Object getItem(int i) {
        return gridDataattendee.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (view == null){
            view = inflater.inflate(R.layout.itemcell,null);
        }

        TextView attendeesname = (TextView)view.findViewById(R.id.attendesslist);

        // Setting values

        attendeesname.setText(gridDataattendee.get(i).getUserName());

        return view;
    }
}
