package com.example.eventbookapp;

/*
 * Created by visak on 2017-11-13.
*/

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import DataProvider.EventAttendeesDataProvider;
import DataProvider.EventDataProvider;
import DataProvider.EventUsersDataProvider;
import DataProvider.ServiceDataProvider;
import Models.EventAttendees;
import Models.EventEntity;
import cs.dal.eventbookcalendar.DayDecorator;

import static DataProvider.EventDataProvider.eventData;

/**
 * Class Name: EventList
 *
 * Functionality: To show the list of events. Three tabs would be displayed.
 *
 * Tabs:
 *
 * 1) Future
 * 2) Attending
 * 3) Attended
 *
 */


public class EventList extends AppCompatActivity{

    /**
     * Initialising the necessary variables
     */

    GridView gridView;
    Button attended;
    Button future;
    Button attending;
    String namefilterText;
    int hourFilter;
    int minuteFilter;
    int filterDate;
    int filterMonth;
    int filterYear;
    int toFilterDate;
    int toFilterMonth;
    int toFilterYear;
    Date toDate;
    Date fromDate;
    FloatingActionButton addEvent;
    TimePickerDialog getFilterTime;
    DatePickerDialog getFilterDate;
    DatePickerDialog getToFilterDate;
    ArrayList<EventEntity> eventAttendedList = new ArrayList<EventEntity>();
    ArrayList<EventEntity> gridData = new ArrayList<EventEntity>();

    /**
     * Method Name: updateGridData()
     *
     * Functionality: Clear the memory allocated for gridView and adds new elements in the memory
     *
     *
     */

    public void updateGridData(){
        gridData.clear();
        for (EventEntity event:eventData){
            gridData.add(event);
        }
    }

    /**
     * Method Name: runAsyncTask
     *
     * Functionality : Run the async task
     *
     * @param task - Async task that needs to be run
     * @return Execute the task
     *
     */

    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }

    /**
     * Method Name: removeFutureEvents()
     *
     * Functionality: To remove the events that are going to happen
     *
     * Any event date which is greater than the current date would be removed from the list
     *
     * @return tempList - Arraylist which would contain only the past events
     */

    public ArrayList<EventEntity> removeFutureEvents(){
        ArrayList<EventEntity> tempList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        for (EventEntity event:eventAttendedList){
            if (event.getstartDate().before(calendar.getTime())){
                tempList.add(event);
            }
        }
        return tempList;
    }

    /**
     * Method Name: getUserAttendedEvents
     *
     * Functionality: Async task to get the list of events that has been attended by the current user from the DB.
     *
     * Steps Involved:
     *
     * 1) get current user ID
     * 2) Network call to DB to get the data
     * 3) Clear and update the list in the memory allocated for grid
     *
     * @param gridAdapter - Instance of the grid Adapter
     * @param isPast - Boolean value to determine whether only the past events are needed
     */

    private void getUserAttendedEvents(final CustomGridAdapter gridAdapter, final Boolean isPast) {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    ArrayList<EventAttendees> eventAttendees = new ArrayList<EventAttendees>();
                    ArrayList<EventEntity> updatedEventList = new ArrayList<EventEntity>();
                    EventAttendeesDataProvider eventAttendeesDataProvider = new EventAttendeesDataProvider();
                    EventDataProvider eventDataProvider = new EventDataProvider();
                    eventAttendees = eventAttendeesDataProvider.GetEventAttendeeRecordByUserId(ServiceDataProvider.getGlobalInstance().getUserId());
                    eventAttendedList.clear();
                    for (EventAttendees eventRecord: eventAttendees){
                        eventAttendedList.add(eventDataProvider.GetEventById(eventRecord.getEventId()));
                    }
                    if (isPast){
                        updatedEventList = removeFutureEvents();
                        eventAttendedList.clear();
                        for (EventEntity event:updatedEventList){
                            eventAttendedList.add(event);
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            gridData.clear();
                            for (EventEntity event: eventAttendedList){
                                gridData.add(event);
                            }
                            gridAdapter.notifyDataSetChanged();
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

    /**
     * Method Name: setGridData
     *
     * Functionality: Update the memory with eventData
     *
     */

    public void setGridData(){
        for (EventEntity event:eventData){
            gridData.add(event);
        }
    }

    /**
     * Method Name: filter
     *
     * Functionality: Functionality to search events in the list.
     *
     * Search would happen based on
     * 1) Location
     * 2) Name
     * 3) Date
     *
     * Data would be filtered and the grid values would be updated
     *
     * @param filter - String value(Search Key)
     *
     */

    public void filter(String filter){
        try{
            Log.d("Filter","Inside Filter");
            ArrayList<EventEntity> tempList = new ArrayList<>();
            for (EventEntity eventEntity:gridData){
                tempList.add(eventEntity);
            }
            gridData.clear();
            for (EventEntity eventEntity:tempList){
                if (filter.equals("name")){
                    if (eventEntity.getEventName().toLowerCase().contains(namefilterText.toLowerCase())|| (eventEntity.getEventLocation().toLowerCase().contains(namefilterText.toLowerCase()))){
                        gridData.add(eventEntity);
                    }
                }
                else if (filter.equals("date")){
                    Date eventDate = eventEntity.getstartDate();
                    if (eventDate.after(fromDate) && eventDate.before(toDate)){
                        gridData.add(eventEntity);
                    }
                }
                else {
                    gridData.add(eventEntity);
                }
            }
        }
        catch (Exception e){
            Log.d("Exception","Exception!!!");
            Log.d("StackTrace",""+e);
        }
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_list);
        Intent intent = getIntent();

        /*
            Initalising the values
         */

        gridView = (GridView) findViewById(R.id.grid);
        addEvent = (FloatingActionButton) findViewById(R.id.addEvent);
        attended = (Button)findViewById(R.id.attended);
        future = (Button)findViewById(R.id.future);
        attending =(Button)findViewById(R.id.FutureAttending);

        /*
            Admin Check on whether to show the create event button or not
         */

        if (ServiceDataProvider.getGlobalInstance().hasAdminRights()){
            addEvent.setVisibility(View.VISIBLE);
        }else{
            addEvent.setVisibility(View.GONE);
        }

        /*
         *  Set values to the grid
         *
         */

        setGridData();
        final CustomGridAdapter gridAdapter = new CustomGridAdapter(EventList.this,gridData);
        gridView.setAdapter(gridAdapter);

        /*
            OnClick Listener for the attended tab
            1) On click get the list of events attended by the user and display
         */

        attended.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUserAttendedEvents(gridAdapter,true);
            }
        });

        /*
            OnClick Listener for the future tab
            1) On click get the list of future events and display
         */
        future.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateGridData();
                gridAdapter.notifyDataSetChanged();
            }
        });

        /*
            OnClick Listener for the Attending tab
            1) On click get the list of future events which would be attended by the current user
         */
        attending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUserAttendedEvents(gridAdapter,false);
            }
        });

        /*
         * Add Event button click listener
         *
         * On click of the event, another new activity would be created
         *
         */

        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventList.this,CreateEvent.class);
                intent.putExtra("position",-1);
                startActivity(intent);
            }
        });

        /*
            Grid Item on click Listener
            On click on any of the items in the list, user would be re-directed to the event details page
         */

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(EventList.this,EventDetails.class);
                intent.putExtra("position",DashboardActivity.getPosition(gridData,i));
                startActivity(intent);
            }
        });
    }

    /**
     * Method Name: OnCreateOptionsMenu
     *
     * Functionality: To inflate the menu list an show the items in the menu
     *
     * @param menu -Menu Items
     * @return Boolean value to see if the menu has been created
     */

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_list, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                namefilterText = query;
                filter("name");
                ((BaseAdapter)gridView.getAdapter()).notifyDataSetChanged();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

        });
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Method Name: onOptionsItemSelected
     *
     * Functionality: To determine the actions which would happen on click of the menu items
     *
     * @param menuItem - List of menu items
     * @return Boolean
     *
     */

    public boolean onOptionsItemSelected(MenuItem menuItem){
        int id = menuItem.getItemId();
        if (id==R.id.action_search){
            Log.d("Search","Inside");
            return true;
        }
        else if(id==R.id.date){
            getFilterDate = new DatePickerDialog(EventList.this, new DatePickerDialog.OnDateSetListener(){
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    filterYear = year;
                    filterMonth = month;
                    filterDate = day;
                    Calendar calendar1 = Calendar.getInstance();
                    calendar1.set(filterYear,filterMonth,filterDate,0,0);
                    toDate = calendar1.getTime();
                    Log.d("To Date",""+toDate);
                    if ((fromDate!=null) && (toDate!=null)){
                        filter("date");
                        ((BaseAdapter)gridView.getAdapter()).notifyDataSetChanged();
                    }
                }
            },Calendar.getInstance().get(Calendar.YEAR),Calendar.getInstance().get(Calendar.MONTH),Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
            getFilterDate.show();

            getToFilterDate = new DatePickerDialog(EventList.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    toFilterDate = day;
                    toFilterMonth = month;
                    toFilterYear = year;
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(toFilterYear,toFilterMonth,toFilterDate,0,0);
                    fromDate = calendar.getTime();
                    Log.d("FromDate",""+fromDate);
                }
            },Calendar.getInstance().get(Calendar.YEAR),Calendar.getInstance().get(Calendar.MONTH),Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
            getToFilterDate.show();

            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
