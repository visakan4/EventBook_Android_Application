package com.example.eventbookapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import DataProvider.EventAttendeesDataProvider;
import DataProvider.EventDataProvider;
import DataProvider.ServiceDataProvider;
import Models.EventAttendees;
import Models.EventEntity;
import cs.dal.eventbookapp.AttachNotes;
import cs.dal.eventbookcalendar.DayDecorator;

import static DataProvider.EventDataProvider.eventData;

/**
 * Created by yuvaraj on 2017-11-13.
 */

/**
 *
 * Class Name: EventDetails
 *
 * Functionalites:
 *
 * 1) To show the event details to the user
 * 2) To show the comments and rating section
 * 3) To re-direct the user to the location
 * 4) To allow admin users to the edit the events
 *
 */

public class EventDetails extends AppCompatActivity {

    /**
     * Initialise the variables
     */

    TextView eventNameDisplay;
    TextView eventDescDisplay;
    TextView datetextDisplay;
    TextView timetextDisplay;
    TextView locationtextDisplay;
    TextView attendeesCountDisplay;
    CheckBox attendeeCheckbox;
    Button btnAttachNotes;
    Button directions;
    ImageButton sharefacebook;
    Button attendeeslist;
    EventAttendeesDataProvider dataProvider = null;

    FloatingActionButton editEvent;
    int position;
    Models.EventEntity currentEvent;
    Models.EventAttendees currentAttendeeRecord = null;
    boolean attendingEvent =false;

    /**
     * Method Name: getLocationText
     *
     * Functionality:  To get the location name from the location values
     *
     * @return String - which contains the name of the local
     */

    public String getLocationText(){
        if (currentEvent.getEventLocation().split("#").length == 3){
            return currentEvent.getEventLocation().split("#")[2];
        }
        else {
            return currentEvent.getEventLocation().split("#")[0];
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_details);
        Intent intent = getIntent();

        /*
            Initialise the variables
         */

        eventNameDisplay = (TextView) findViewById(R.id.eventName);
        eventDescDisplay = (TextView) findViewById(R.id.eventDesc);
        datetextDisplay = (TextView) findViewById(R.id.datetext);
        timetextDisplay = (TextView) findViewById(R.id.timetext);
        locationtextDisplay = (TextView) findViewById(R.id.locationtext);
        attendeesCountDisplay = (TextView) findViewById(R.id.attendeesCount);
        attendeeCheckbox  =(CheckBox) findViewById(R.id.checkBox);
        directions = (Button) findViewById(R.id.directionIcon);
        dataProvider = new EventAttendeesDataProvider();
        attendeeslist = (Button)findViewById(R.id.viewAttendees);
        sharefacebook = (ImageButton) findViewById(R.id.shareIcon);

        /*
         * OnClickListener for Atttendees List
         *
         * On click of the attendees list button, start a new activity.
         *
         * Pass the event id to the activity
         *
         */

        attendeeslist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent1 = new Intent(EventDetails.this,AttendeesList.class);
                intent1.putExtra("eventId",currentEvent.getId());
                startActivity(intent1);
            }
        });


        position = intent.getExtras().getInt("position");
        if(EventDataProvider.eventData!=null && EventDataProvider.eventData.size() >0)
        {
            currentEvent = EventDataProvider.eventData.get(position);
        }else{
            return;
        }

        /*
         * Set default text values
         */

        eventNameDisplay.setText(currentEvent.getEventName());
        eventDescDisplay.setText(currentEvent.getEventDescription());
        datetextDisplay.setText(GetStringFromDate(currentEvent.getstartDate()));
        timetextDisplay.setText(GettimeFromDate(currentEvent.getstartDate()));
        locationtextDisplay.setText(getLocationText());
        attendeesCountDisplay.setText(currentEvent.getNoOfAttendees());

        /*
            OnClick Listener for Share Facebook button
            On click start a new activity which will help the user to share the event via various social networking
         */

        sharefacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sharecontent = "check out "+""+currentEvent.getEventName()+" on"+currentEvent.getstartDate()+" at"+currentEvent.getEventLocation()+"  and the event is about "+currentEvent.getEventDescription();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,sharecontent);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });

        /*
            Directions - OnClickListener
            On click of the directions button, show directions to the user from his current location to the event location
         */

        directions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String latitude = currentEvent.getEventLocation().split("#")[0];
                String longitude = currentEvent.getEventLocation().split("#")[1];
                String url = "http://maps.google.com/maps?daddr="+latitude+","+longitude;
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });

        editEvent = (FloatingActionButton) findViewById(R.id.editEvent);

        /*
            Check if the current use has admin access
         */
        if (ServiceDataProvider.getGlobalInstance().hasAdminRights()){

            editEvent.setVisibility(View.VISIBLE);
        }else{
            editEvent.setVisibility(View.GONE);
        }

        /*
            On click for Edit Event
            On click show the editevent activity
         */

        editEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(EventDetails.this,CreateEvent.class);
                intent1.putExtra("position",position);
                startActivity(intent1);
            }
        });



        attendeeCheckbox.setEnabled(false);

        /*
         * OnChecked change listener for checkbox
         * On checking the check box - add the user to event attending list
         * On unchecking the checkbox - remove the user fromt he event attending list
         *
         */

        attendeeCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                attendeeCheckbox.setEnabled(false);
                if(b){
                    if(currentAttendeeRecord!=null) {
                    attendeeCheckbox.setEnabled(true);
                        return;
                    }
                    attendingEvent=true;
                    CreateEventAttendee();
                }else{
                    if(currentAttendeeRecord!=null)
                    {
                        DeleteEventAttendee();
                        attendingEvent =false;
                    }
                }

            }
        });

        /*
         * Attach Notes -Button OnClick listener
                   * On click start a new activity where the user can attach notes
                   * Send the event id to the next activity
         */

        btnAttachNotes = (Button) findViewById(R.id.takeNotes);
        btnAttachNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent attachNotesIntent = new Intent(EventDetails.this, AttachNotes.class);
                if(currentEvent!=null)
                {
                    attachNotesIntent.putExtra("contextEvent",currentEvent.getId());
                }
                startActivity(attachNotesIntent);

            }
        });
        GetEventAttendee();
    }

    /**
     * Method Name: GetStringFromDate
     *
     * Funtionality : get the date is specified format
     *
     * @param d - Date object
     * @return Formatted Date
     *
     */

    public String GetStringFromDate(Date d)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy");
        return dateFormat.format(d);
    }


    /**
     * Method Name : GettimeFromDate
     *
     * Functionality: get formatted time from the date
     *
     * @param d - Date Object
     * @return Fromatted Time from the date
     *
     */

    public String GettimeFromDate(Date d)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("K:mm, z");
        return dateFormat.format(d);
    }

    /**
     * Method Name: GetEventAttendee
     *
     * Functionalites: Get the list of attendees for the event and determine whether the user is attending the event.
     *
     * If attending, check the attending checkbox
     *
     */

    private void GetEventAttendee() {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try
                {
                    ArrayList<EventAttendees> eventAttendeeList=  dataProvider.GetEventAttendeeRecordByUserId(ServiceDataProvider.getGlobalInstance().getUserId());
                    for(int counter=0;counter<eventAttendeeList.size();counter++)
                    {
                       if(currentEvent.getId().equalsIgnoreCase(eventAttendeeList.get(counter).getEventId()))
                       {
                           currentAttendeeRecord = eventAttendeeList.get(counter);
                           attendingEvent=true;
                           break;
                       }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            attendeeCheckbox.setEnabled(true);
                            if(attendingEvent)
                            {
                                attendeeCheckbox.setChecked(true);
                            }else
                            {
                                attendeeCheckbox.setChecked(false);
                            }
                        }
                    });
                } catch (final Exception e){
                    e.printStackTrace();
                }
                return null;
            }
        };
        runAsyncTask(task);
    }

    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }


    /**
     *
     * Method Name: CreateEventAttendee
     *
     * Functionalites: Create a new event attendee record for the event and update the table
     *
     */

    private void CreateEventAttendee(){
        AsyncTask<Void,Void,Void> createTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                EventAttendees userAttendingEvent = new EventAttendees();
                userAttendingEvent.setEventId(currentEvent.getId());
                userAttendingEvent.setEventDate(currentEvent.getstartDate());
                userAttendingEvent.setUserId(ServiceDataProvider.getGlobalInstance().getUserId());
                userAttendingEvent.setUserName(ServiceDataProvider.getGlobalInstance().getUserName());
                currentAttendeeRecord = dataProvider.CreateEventAttendee(userAttendingEvent);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        attendeeCheckbox.setEnabled(true);
                    }
                });
                return null;
            }
        };
        runAsyncTask(createTask);
    }


    /**
     * Method Name: DeleteEventAttendee
     *
     * Functionality: Delete the event attendee record for the current user
     *
     */

    private void DeleteEventAttendee(){
        AsyncTask<Void,Void,Void> deleteTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                dataProvider.DeleteEventAttendeeRecord(currentAttendeeRecord);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        attendeeCheckbox.setEnabled(true);
                        currentAttendeeRecord =null;
                    }
                });
                return null;
            }
        };
        runAsyncTask(deleteTask);
    }

}
