package com.example.eventbookapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.MainThread;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import DataProvider.EventDataProvider;
import Models.EventEntity;

/*
 * Created by visak on 2017-11-12.
 */

/**
 * Class Name: CreateEvent
 *
 * Functionality : To create new events and update already created events
 */

public class CreateEvent extends AppCompatActivity{

    /**
     * Variables Initialisation
     */

    TextView eventNameLabel;
    TextView purposeLabel;
    TextView dateLabel;
    TextView startingTimeLabel;
    TextView endingTimeLabel;
    TextView attendeesLabel;
    TextView locationLabel;
    TextView highlightLabel;
    EditText eventName;
    EditText purpose;
    EditText purpose1;
    EditText dateValue;
    EditText startingTime;
    EditText endingTime;
    EditText attendees;
    EditText location;
    String nameEvent;
    String purposeEvent;
    String dateEvent;
    String startingTimeEvent;
    String endingTimeEvent;
    String atttendeeEvent;
    String locationEvent;
    Button createEvent;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    Boolean isUpdate = false;
    Boolean isFirst = true;
    int startingHour;
    int startingMinute;
    int endingHour;
    int endingMinute;
    int selectedYear;
    int selectedMonth;
    int selectedDay;
    String eventID;
    double latitute;
    double longitude;
    Date date;
    Date endDate;
    EventEntity isEventCreated;
    int position = -1;
    int PLACE_PICKER_REQUEST = 1;
    TextView updateText;
    ArrayList<String> locationList = new ArrayList<>();
    String snackBarText = "Event has been created";

    /**
     *  Method Name: nullCheck
     *
     *  Functionality - To check whether the user has selected all the values. If null, highlight the field
     *
     * @param editTextValue -Value in the editText
     * @param editText - EditText(Field)
     * @return Boolen (whether the value is null or not)
     */

    public Boolean nullCheck(String editTextValue,EditText editText){
        Boolean isNull = false;
        if (editTextValue.isEmpty()){
            Log.d("Null","Value not set");
            editText.setHintTextColor(Color.RED);
            isNull = true;
        }
        return isNull;
    }

    /**
     * Method Name : numberOfAttendeesCheck
     *
     * Functionality: To make sure that numberofAttendees value is greater than zero
     *
     * @return Boolean to represent whether it passed the check. True if it is greater than zero or False
     */

    public Boolean numberOfAttendeesCheck(){
        Boolean isAttendeesZero = false;
        if (!(Integer.parseInt(atttendeeEvent) > 0)) {
            isAttendeesZero = true;
        }
        return isAttendeesZero;
    }

    /**
     * Method Name : dateCheck
     *
     * Functionality: To make sure that the end date is greater than the starting date of the event
     *
     * @return Boolen. True if it is greated and false if it is less than the starting time
     *
     */

    public Boolean dateCheck(){
        Boolean isDateGreater = true;
        Log.d("End Date",""+endDate.getTime());
        Log.d("End Date",""+endDate);
        Log.d("Start Date",""+date.getTime());
        Log.d("Start Date",""+date);
        if ((endDate.getTime() - date.getTime()) > 0){
            Log.d("Time Difference",""+(endDate.getTime() - date.getTime()));
        }
        else {
            isDateGreater = false;
        }
        return isDateGreater;
    }

    /**
     * Method Name: validateValues
     *
     * Functionality : Validation to make sure that all the values are filled.
     *
     * @return True if all the values are filled. False if any one the value is not filled
     *
     */

    public Boolean validateValues(){
        Boolean isValidationSuccess = true;
        if (nullCheck(nameEvent,eventName) && nullCheck(purposeEvent,purpose) && nullCheck(dateEvent,dateValue)
                && nullCheck(startingTimeEvent,startingTime) && nullCheck(endingTimeEvent,endingTime)
                    && nullCheck(atttendeeEvent,attendees) && nullCheck(locationEvent,location)) {
            isValidationSuccess = false;
        }
        else if((nullCheck(nameEvent,eventName) || nullCheck(purposeEvent,purpose) || nullCheck(dateEvent,dateValue)
                || nullCheck(startingTimeEvent,startingTime) || nullCheck(endingTimeEvent,endingTime)
                || nullCheck(atttendeeEvent,attendees) && nullCheck(locationEvent,location))) {
            isValidationSuccess = false;
        }
        return isValidationSuccess;
    }

    /**
     * Method Name: runAsyncTask
     *
     * Functionality : To run Async tasks
     *
     * @param task
     * @return Execute task
     */
    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }

    /**
     * Method Name: getPlace()
     *
     * Functionality: To get the location of the event using Google Maps(PlacePicker)
     *
     */

    public void getPlace(){
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(CreateEvent.this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method Name: OnActivityResult
     *
     * Funtionality: Store the value received from intent and display in the UI
     *
     * @param requestCode - request code to place picker
     * @param resultCode - result code from the intent
     * @param data - Data from the intent
     */

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data,this);
                String toastMsg = String.format("%s", place.getName());
                latitute = place.getLatLng().latitude;
                longitude = place.getLatLng().longitude;
                locationList.add(String.valueOf(latitute));
                locationList.add(String.valueOf(longitude));
                locationList.add(toastMsg);
                location.setText(toastMsg);
            }
        }
    }


    /**
     * Method Name: addEvent
     *
     * Functionality : Async task to add the events in the Table. Creates a Event Entity object and sets all the necessary values needed to create an event
     * Once the event has been added, move back to the UI.
     *
     */

    private void addEvent(){
        AsyncTask<Void,Void,Void> newTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                EventDataProvider eventDataProvider = new EventDataProvider();
                EventEntity eventEntity = new EventEntity();
                eventEntity.setEventName(nameEvent);
                eventEntity.setEventDescription(purposeEvent);
                eventEntity.setstartDate(date);
                eventEntity.setEndDate(endDate);
                eventEntity.setNoOfAttendees(atttendeeEvent);
                eventEntity.setEventLocation(locationEvent);
                eventEntity.setColorTheme("#ffffff");
                Log.d("Start Date",date.toString());
                Log.d("End Date",endDate.toString());
                if(isUpdate){
                    eventEntity.setId(eventID);
                    isEventCreated = eventDataProvider.UpdateEvents(eventEntity);
                }else {
                    isEventCreated = eventDataProvider.CreateEvents(eventEntity);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isEventCreated!=null){
                            Log.d("Event Created?",isEventCreated.toString());
                            Intent intent1 = new Intent(CreateEvent.this,DashboardActivity.class);
                            startActivity(intent1);
                        }
                        else {
                            Log.d("Event Created?",""+isEventCreated);
                        }
                    }
                });
                return null;
            }
        };
        runAsyncTask(newTask);
    }

    /**
     * Method Name :getLocationValue
     *
     * Functionality : Modifying(Adding a delimiter) and storing the location values.
     *
     * @return string - Modified location value
     */


    public String getLocationValue(){
        if (isUpdate){
            return EventDataProvider.eventData.get(position).getEventLocation();
        }
        else {
            String locationValue = "";
            for (String s:locationList){
                locationValue+=s+"#";
            }
            return locationValue.substring(0,locationValue.length()-1);
        }
    }

    /**
     * Method Name: getLocationText
     *
     * Functionality: Split the value from the location. Return only the place name
     *
     * @return Place Name to be displayed in the Form
     */

    public String getLocationText(){
        String[] values = EventDataProvider.eventData.get(position).getEventLocation().split("#");
        return values[2];
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);

        /*
          * Get Intent and the extra values
          * Initialize all the Form values
          * Initialize all the needed variables
         */

        Intent intent = getIntent();
        position = intent.getExtras().getInt("position");
        eventNameLabel = (TextView)findViewById(R.id.eventNameLabel);
        purposeLabel = (TextView)findViewById(R.id.purposeLabel);
        dateLabel = (TextView)findViewById(R.id.dateLabel);
        startingTimeLabel = (TextView)findViewById(R.id.startingTimeLabel);
        endingTimeLabel=(TextView)findViewById(R.id.endingTimeLabel);
        attendeesLabel = (TextView)findViewById(R.id.attendeesLabel);
        locationLabel = (TextView)findViewById(R.id.locationLabel);
        highlightLabel = (TextView)findViewById(R.id.validationFailure);
        eventName = (EditText)findViewById(R.id.eventName);
        purpose=(EditText)findViewById(R.id.purpose);
        purpose1=(EditText)findViewById(R.id.purpose1);
        dateValue=(EditText) findViewById(R.id.date);
        startingTime = (EditText)findViewById(R.id.startingTime);
        endingTime = (EditText)findViewById(R.id.endingTime);
        attendees = (EditText)findViewById(R.id.attendees);
        location = (EditText)findViewById(R.id.location);
        createEvent = (Button)findViewById(R.id.createEvent);
        updateText = (TextView)findViewById(R.id.createEventText);
        highlightLabel.setVisibility(View.INVISIBLE);
        highlightLabel.setText(R.string.empty);
        Calendar calendar = Calendar.getInstance();
        final int currentYear = calendar.get(Calendar.YEAR);
        final int currentMonth = calendar.get(Calendar.MONTH);
        final int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);
        startingHour = hour;
        startingMinute = minute;
        endingHour = hour+1;
        endingMinute = minute;

        /*
         * Below code helps in distinguishing between whether the user needs to update/create a new event
         * If a event is to be updated. All the necessaary values are fetched and displayed in the form
         */

        if(position!=-1){
            isUpdate = true;
            eventID = EventDataProvider.eventData.get(position).getId();
            eventName.setText(EventDataProvider.eventData.get(position).getEventName());
            purpose.setText(EventDataProvider.eventData.get(position).getEventDescription());
            attendees.setText(EventDataProvider.eventData.get(position).getNoOfAttendees());
            location.setText(getLocationText());
            Calendar cal = Calendar.getInstance();
            cal.setTime(EventDataProvider.eventData.get(position).getstartDate());
            selectedYear = cal.get(Calendar.YEAR);
            selectedMonth = cal.get(Calendar.MONTH);
            selectedDay = cal.get(Calendar.DAY_OF_MONTH);
            dateValue.setText(String.valueOf(selectedYear)+"/"+String.valueOf(selectedMonth+1)+"/"+String.valueOf(selectedDay));
            startingHour = cal.get(Calendar.HOUR_OF_DAY);
            startingMinute = cal.get(Calendar.MINUTE);
            startingTime.setText(String.format(Locale.getDefault(),"%02d",cal.get(Calendar.HOUR_OF_DAY))+":"+String.format(Locale.CANADA,"%02d",cal.get(Calendar.MINUTE)));
            date = cal.getTime();
            cal.setTime(EventDataProvider.eventData.get(position).getEndDate());
            endingHour = cal.get(Calendar.HOUR_OF_DAY);
            endingMinute = cal.get(Calendar.MINUTE);
            endingTime.setText(String.format(Locale.getDefault(),"%02d",cal.get(Calendar.HOUR_OF_DAY))+":"+String.format(Locale.CANADA,"%02d",cal.get(Calendar.MINUTE)));
            endDate = cal.getTime();
            createEvent.setText(R.string.updateEvent);
            updateText.setText(R.string.updateText);
            snackBarText = "Event has been Updated";
        }

        /*
            * Focus Change Listener on the Event Date set field
            * On focus, DatePicker Dialog would appear
            * Date can be picked on the UI
            * Once the values are picked, it would be shown in the UI
            *
         */

        dateValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    datePickerDialog = new DatePickerDialog(CreateEvent.this,new DatePickerDialog.OnDateSetListener(){
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int dayofMonth) {
                            dateValue.setText(year+"/"+(month+1)+"/"+(dayofMonth));
                            selectedYear = year;
                            selectedMonth = month;
                            selectedDay = dayofMonth;
                        }
                    },currentYear,currentMonth,currentDay);
                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 100);
                    datePickerDialog.show();
                }
            }
        });

        /*
          * Focus change Listener on the Event Time field
          *
          * On Focus, TimePicker Dialog would appear.
          * Time can be selected on the UI
          * Once the time is selected, it is displayed in proper format in the frontend
          *
         */

        startingTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    timePickerDialog = new TimePickerDialog(CreateEvent.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int hourSelectedStarting, int minuteSelectedStarting) {
                            startingHour = hourSelectedStarting;
                            startingMinute = minuteSelectedStarting;
                            Calendar calendar1 = Calendar.getInstance();
                            calendar1.set(selectedYear,selectedMonth,selectedDay,startingHour,startingMinute);
                            date = calendar1.getTime();
                            startingTime.setText(String.format(Locale.getDefault(),"%02d",calendar1.get(Calendar.HOUR_OF_DAY))+":"+String.format(Locale.CANADA,"%02d",calendar1.get(Calendar.MINUTE)));
                        }
                    },hour,minute,true);
                    timePickerDialog.show();
                }
            }
        });

        /*
         * On Focus Change Listener for End Time Text field
         * On focus, time picker dialog would appear and the values can be chosen on the UI
         * Value are set and displayed in the UI
         */

        endingTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    timePickerDialog = new TimePickerDialog(CreateEvent.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int hourSelected, int minuteSelected) {
                            endingHour = hourSelected;
                            endingMinute = minuteSelected;
                            Calendar calendar1 = Calendar.getInstance();
                            calendar1.set(selectedYear,selectedMonth,selectedDay,endingHour,endingMinute);
                            endDate = calendar1.getTime();
                            endingTime.setText(String.format(Locale.getDefault(),"%02d",calendar1.get(Calendar.HOUR_OF_DAY))+":"+String.format(Locale.CANADA,"%02d",calendar1.get(Calendar.MINUTE)));
                        }
                    },startingHour+1,startingMinute,true);
                    timePickerDialog.show();
                }
            }
        });


        /*
         * Location - On Focus Change Listerner:
          *
          * To make sure that location(place picker dialog) would appear only the user clicks on the text field
          *
         */

        location.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    if (isFirst){
                        isFirst = false;
                    }else {
                        getPlace();
                    }
                }
            }
        });

        /*
          * CreateEvent - Button click Listener:
          * All the values from the text field are got
          * Validation checks are done
          * If all the validation are passed, Events are created/updated in DB.
          * If validation fails, proper error messages are shown in the UI
          *
         */

        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nameEvent = eventName.getText().toString();
                purposeEvent = purpose.getText().toString() + purpose1.getText().toString().trim();
                dateEvent = dateValue.getText().toString();
                startingTimeEvent = startingTime.getText().toString();
                endingTimeEvent = endingTime.getText().toString();
                atttendeeEvent = attendees.getText().toString();
                locationEvent = getLocationValue();
                if (isUpdate){
                    Calendar calendarGet = Calendar.getInstance();
                    calendarGet.set(selectedYear,selectedMonth,selectedDay,startingHour,startingMinute);
                    date = calendarGet.getTime();
                    calendarGet.set(selectedYear,selectedMonth,selectedDay,endingHour,endingMinute);
                    endDate = calendarGet.getTime();
                }
                if (validateValues()){
                    if (dateCheck()){
                        if (!numberOfAttendeesCheck()){
                            highlightLabel.setVisibility(View.INVISIBLE);
                            createEvent.setEnabled(false);
                            addEvent();
                        }
                        else {
                            highlightLabel.setVisibility(View.VISIBLE);
                            attendees.setTextColor(Color.RED);
                            highlightLabel.setText(R.string.attendeesCheck);
                        }
                    }
                    else {
                        highlightLabel.setVisibility(View.VISIBLE);
                        endingTime.setTextColor(Color.RED);
                        highlightLabel.setText(R.string.dateGreater);
                    }
                }else {
                    Log.d("ValueCheck","One or Some of the values are null");
                    highlightLabel.setVisibility(View.VISIBLE);
                    highlightLabel.setText(R.string.highlightedFields);
                }
            }
        });
    }
}
