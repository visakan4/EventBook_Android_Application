package com.example.eventbookapp;

import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.ListView;

import java.sql.Array;
import java.util.ArrayList;

import DataProvider.EventAttendeesDataProvider;
import DataProvider.ServiceDataProvider;
import Models.EventAttendees;
import Models.EventEntity;
import com.example.eventbookapp.R;

/**
 * Class Name: AttendeesList
 *
 * Functionality: To show the list of attendees to a specific event
 *
 */

public class AttendeesList extends AppCompatActivity {

    /*
     *  Initialisation of the necessary variables
     */

    ListView listView;
    ArrayList<EventAttendees> gridDataattendee = new ArrayList<EventAttendees>();
    String currentEventId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendees_list);

        /*
            getEventId of the event whose event list is to be displayed
            List adapter instance is set up and the values are updated
         */

        currentEventId=getIntent().getExtras().getString("eventId");
        listView = (ListView) findViewById(R.id.list);
        final CustomListView listadapter = new CustomListView(AttendeesList.this, gridDataattendee);
        listView.setAdapter(listadapter);
        GetAttendeeList(listadapter);
    }

    /**
     * Method Name: GetAttendeeList
     *
     * Functionality: To get the list of attendees of a particular event
     *
     * @param listadapter - Instance of listadaper whose list is to be updated
     */

    private void GetAttendeeList(final CustomListView listadapter) {
        AsyncTask<Void, Void, Void> taskattendees = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    EventAttendeesDataProvider eventAttendeesDataProvider = new EventAttendeesDataProvider();
                    ArrayList<EventAttendees> eventAttendees = new ArrayList<EventAttendees>();
                    eventAttendees = eventAttendeesDataProvider.GetEventAttendeeRecordByEventId(currentEventId);
                    final ArrayList<EventAttendees> finalEventAttendees = eventAttendees;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (EventAttendees event : finalEventAttendees) {
                                gridDataattendee.add(event);
                            }
                            listadapter.notifyDataSetChanged();
                        }
                    });
                } catch (final Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        runAsyncTask(taskattendees);
    }

    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }
}


