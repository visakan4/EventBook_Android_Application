package com.example.eventbookapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import DataProvider.EventDataProvider;
import DataProvider.ServiceDataProvider;
import Models.EventEntity;
import cs.dal.eventbookcalendar.CustomCalendarView;
import cs.dal.eventbookcalendar.DayDecorator;

import static DataProvider.EventDataProvider.eventData;

/**
 *
 * Class Name: DashboardActivity
 *
 * Functionality: Class which is related to EventBook Dashboard
 *
 * User can view the list of events in calendar view and a create event floating button is available
 *
 */

public class DashboardActivity extends AppCompatActivity {

    /**
     * Initialising the variables
     */

    CustomCalendarView dashBoardCalendar;
    Calendar systemCalendar;
//    SearchView searchView;
    public static ListView eventList;
    FloatingActionButton addEvent;
    Button showEvents;
    int currentYear;
    int currentMonth;
    int currentDay;
    public static ArrayList<EventEntity> listDisplay = new ArrayList<>();
    public static ListViewAdapter listViewAdapter;

    /**
     *
     * Method Name: setListViewData
     *
     * Functionality: To display the events on the selected dates. It is also used to set the data to the listViewAdapter
     *
     * @param listData - ArrayList which contains all the events
     * @param selectedData - Selected Date
     * @param selectedMonth - Selected Month
     * @param selectedYear - Selected Year
     * @param isFirst - Boolean value to differentiate whether it is the first time or not
     */

    public static void setListViewData(ArrayList<EventEntity> listData,int selectedData,int selectedMonth,int selectedYear,Boolean isFirst){
        if (!(isFirst)){
            listDisplay.clear();
        }
        for (EventEntity eventEntity:listData){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(eventEntity.getstartDate());
            int eventDay = calendar.get(Calendar.DAY_OF_MONTH);
            int eventMonth = calendar.get(Calendar.MONTH);
            int eventYear = calendar.get(Calendar.YEAR);
            if ((selectedData == eventDay) && (selectedMonth == eventMonth) && (selectedYear == eventYear)){
                listDisplay.add(eventEntity);
            }
        }
        if (isFirst){
            eventList.setAdapter(listViewAdapter);
        }
        else {
            listViewAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Method Name: getPosition
     *
     * Functionality: To get the position of the event in the main Event Data list
     *
     * @param currentList - Current list which is being used
     * @param listPosition - Position in the current List
     * @return Integer value - position in the current list
     *
     */

    public static int getPosition(ArrayList<EventEntity> currentList,int listPosition){
        int eventListPosition = 0;
        EventEntity selectedEvent = currentList.get(listPosition);
        String selectedId = selectedEvent.getId();
        for (EventEntity event: eventData){
            if (event.getId().equals(selectedId)){
                break;
            }
            eventListPosition+=1;
        }
        return eventListPosition;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard2);
        addEvent = (FloatingActionButton) findViewById(R.id.addEvent);
        showEvents = (Button)findViewById(R.id.showEvents);
        eventList =(ListView)findViewById(R.id.eventList);

        /*
            To check whether the user has admin rights
         */

        if (ServiceDataProvider.getGlobalInstance().hasAdminRights()){

            addEvent.setVisibility(View.VISIBLE);
        }else{

            addEvent.setVisibility(View.GONE);
        }

        /*
         *
         * Set the current date from the calendar instance
         *
         */

        systemCalendar = Calendar.getInstance(Locale.getDefault());
        currentDay = systemCalendar.get(Calendar.DAY_OF_MONTH);
        currentMonth = systemCalendar.get(Calendar.MONTH);
        currentYear = systemCalendar.get(Calendar.YEAR);


//        searchView = (SearchView) findViewById(R.id.searchbox);

        /*
            Setting up the calendar view
         */

        dashBoardCalendar = (CustomCalendarView) findViewById(R.id.dashboardcalendar_view);
        dashBoardCalendar.setFirstDayOfWeek(systemCalendar.MONDAY);
        dashBoardCalendar.refreshCalendar(systemCalendar);

        /*
            Getting all the future events
         */

        RefreshEventsFromService();

        /*
            OnClick listener for show events
            On click of the button, start new activity which shows all the events
         */

        showEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this,EventList.class);
                startActivity(intent);
            }
        });

        /*
            OnClick listener for show events
            Onclick of the add event floating bar, show a new activity where the user can fill the form and create events
         */

        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this,CreateEvent.class);
                intent.putExtra("position",-1);
                startActivity(intent);
            }
        });

        /*
            OnClick listener for List Adapter
            Onclick of a item in the event
         */

        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(DashboardActivity.this, EventDetails.class);
                intent.putExtra("position",getPosition(listDisplay,i));
                startActivity(intent);
            }
        });
    }


    /**
     *
     * Method Name: RefreshEventsFromService
     *
     * Functionalities: Get all future events, from the Database and update the memory allocated
     *
     */

    private void RefreshEventsFromService() {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    EventDataProvider provider = new EventDataProvider();
                    eventData = provider.GetAllFutureEvents();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(null!=eventData){
                                listViewAdapter = new ListViewAdapter(DashboardActivity.this,R.layout.list_dashboard,listDisplay);
                                setListViewData(eventData,currentDay,currentMonth,currentYear,true);
                                ArrayList<DayDecorator> dec = new ArrayList<DayDecorator>();
                                for(EventEntity eventEntity: eventData){
                                    dec.add((DayDecorator)eventEntity);
                                }
                                dashBoardCalendar.setDecorators(dec);
                                dashBoardCalendar.refreshCalendar(systemCalendar);
                            }
                        }
                    });
                } catch (final Exception e){

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

}
