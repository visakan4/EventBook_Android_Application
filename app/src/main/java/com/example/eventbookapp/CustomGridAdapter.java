package com.example.eventbookapp;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import Models.EventEntity;

/**
 * Created by visak on 2017-11-13.
 */

/**
 * Class Name: CustomGridAdapter
 *
 * Functionaliyt: CustomGridAdapter is used to modify the values in gridAdpater
 *
 */

public class CustomGridAdapter extends BaseAdapter {

    private Context context;
    public ArrayList<EventEntity> listOfEvents;
    LayoutInflater inflater;

    public CustomGridAdapter(Context context,ArrayList<EventEntity> listOfEvents){
        this.context = context;
        this.listOfEvents = listOfEvents;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        Log.d("Size",""+listOfEvents.size());
        return listOfEvents.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    /**
     * Method Name: getView
     *
     * Functionality: To display the values in the gridView
     *
     * @param i - Position of the grid
     * @param view - View
     * @param viewGroup - Parent
     * @return View
     */

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null){
            view = inflater.inflate(R.layout.event_value,null);
        }
        TextView eventNameDisplay = (TextView)view.findViewById(R.id.eventNameDisplay);
        TextView dateDisplay = (TextView)view.findViewById(R.id.dateDisplay);
        TextView numberofAttendeesDisplay = (TextView)view.findViewById(R.id.numberofAttendeesDisplay);
        eventNameDisplay.setText(listOfEvents.get(i).getEventName());
        eventNameDisplay.setTextColor(Color.BLUE);
        dateDisplay.setText(listOfEvents.get(i).getstartDate().toString());
        numberofAttendeesDisplay.setText(listOfEvents.get(i).getNoOfAttendees());
        return view;
    }
}
