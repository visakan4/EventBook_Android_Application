package com.example.eventbookapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import Models.EventEntity;

import static com.example.eventbookapp.R.id.parent;

/**
 * Created by visak on 2017-11-26.
 */

/**
 *
 * Class Name: ListView Adapter
 *
 * Functionalites: To customise the values passed to the listView adapter
 *
 */

public class ListViewAdapter extends ArrayAdapter<EventEntity> {

    private Context context;
    public ArrayList<EventEntity> listEvent;
    LayoutInflater inflater;
    int layoutResourceId;


    public ListViewAdapter(Context context,int layoutResourceId,ArrayList<EventEntity> listEvent){
        super(context,layoutResourceId,listEvent);
        this.context = context;
        this.layoutResourceId =layoutResourceId;
        this.listEvent = listEvent;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Method Name : getView
     *
     * Functionalities : Set Text values to be displayed
     *
     * @param i - Position of the grid
     * @param view - View
     * @param viewGroup - View
     * @return View
     */

    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null){
            view = inflater.inflate(layoutResourceId, null);
        }
        TextView dashboard_event_name = (TextView) view.findViewById(R.id.dashboard_event_name);
        TextView dashboard_event_date = (TextView) view.findViewById(R.id.dashboard_event_date);
        TextView dashboard_event_no_of_attendees = (TextView) view.findViewById(R.id.dashboard_event_no_ofAttendees);
        dashboard_event_name.setText(listEvent.get(i).getEventName());
        dashboard_event_name.setTextColor(Color.BLUE);
        dashboard_event_date.setText(listEvent.get(i).getstartDate().toString());
        dashboard_event_no_of_attendees.setText(listEvent.get(i).getNoOfAttendees());
        return view;
    }
}
