package Models;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.eventbookapp.DashboardActivity;
import com.example.eventbookapp.EventList;
import com.example.eventbookapp.R;
import com.microsoft.windowsazure.mobileservices.table.DateTimeOffset;

import java.util.Calendar;
import java.util.Date;

import DataProvider.EventDataProvider;
import DataProvider.ServiceDataProvider;
import cs.dal.eventbookcalendar.DayDecorator;
import cs.dal.eventbookcalendar.DayView;

/**
 * Created by jebes on 11/11/2017.
 */

/**
 * Class: EventEntity represent a single instance of the specific Event in the Event Book App
 */

public class EventEntity implements cs.dal.eventbookcalendar.DayDecorator {
        /*
        Default Constructor which get called for every instance of the event
         */
        public EventEntity(){

        }
        /*
        Unique identifier of the event entity record stored in the event table
         */
        @com.google.gson.annotations.SerializedName("id")
        private String mId;
        public String getId() { return mId; }
        public final void setId(String id) { mId = id; }
        /*
        String field desribes total number of user atending the particular event
         */
        @com.google.gson.annotations.SerializedName("noOfAttendees")
        private String noOfAttendees;
        public String getNoOfAttendees() { return noOfAttendees; }
        public final void setNoOfAttendees(String noOfAtte) { noOfAttendees = noOfAtte; }
        /*
        Property represent the Datetime at which the event is created
         */
        @com.google.gson.annotations.SerializedName("eventCreatedOn")
        private long eventCreatedOn;
        public Date getEventCreatedOnDate() { return new Date(eventCreatedOn); }
        public void setEventCreatedOnDate(Date eventCreatedOnDate) { if(null!=eventCreatedOnDate){eventCreatedOn = eventCreatedOnDate.getTime();}; }

        /*
        Property represent the Datetime at which current event will start
         */
        @com.google.gson.annotations.SerializedName("startDate")
        private long startDate;
        public Date getstartDate() { return new Date(startDate); }
        public void setstartDate(Date start) { if(null!=start){startDate = start.getTime();}; }
        /*
        Propery represent the Datetime at which teh current event will finish
         */
        @com.google.gson.annotations.SerializedName("endDate")
        private long endDate;
        public Date getEndDate() { return new Date(endDate); }
        public void setEndDate(Date end) { if(null!=end){endDate = end.getTime();}}
        /*
        Property provides description of the event provided by the event organizer
         */
        @com.google.gson.annotations.SerializedName("eventDescription")
        private String eventDescription;
        public String getEventDescription() { return eventDescription; }
        public String setEventDescription(String desc) { eventDescription = desc; return desc; }
        /*
        Owner fields that holds the reference to the Event User record unique id
         */
        @com.google.gson.annotations.SerializedName("owner")
        private String owner;
        public String getOwner() { return owner; }
        public String setOwner(String own) { owner = own; return owner; }

        /*
        String property that represent the color theme for the event entity record
         */
        @com.google.gson.annotations.SerializedName("colorTheme")
        private String colorTheme;
        public String getColorTheme() { return colorTheme; }
        public String setColorTheme(String color) { colorTheme = color; return colorTheme; }

        /*
        String property that provide name of the Event
         */
        @com.google.gson.annotations.SerializedName("eventName")
        private String eventName;
        public String getEventName() { return eventName; }
        public String setEventName(String name) { eventName = name; return name; }

        /*
        String property that holds the latitude and longitide of the event location
         */
        @com.google.gson.annotations.SerializedName("location")
        private String location;
        public String getEventLocation(){ return location; }
        public String setEventLocation(String locationValue){
            location = locationValue;
            return location;
        }

    /**
     * Method Decorate called for each and ever date cell of the Custom calendar View
     * @param cell -  Dayview Cell instance of the Custom calendar view
     */
    @Override
        public void decorate(final DayView cell) {
            if(CompareDate(cell.getDate(),getstartDate()))
            {
                cell.setBackgroundResource(R.drawable.roundbutton);

                cell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v){
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(cell.getDate());
                        DashboardActivity.setListViewData(EventDataProvider.eventData,cal.get(Calendar.DAY_OF_MONTH),cal.get(Calendar.MONTH),cal.get(Calendar.YEAR),false);
                    }
                });
            }
        }

    /**
     * Method Compare Date used to compare two DateTime field only using the date property
     * @param dateOne - Date One object to Compare
     * @param dateTwo - Date two object to compare
     * @return Boolean - Indicating whether the input dates are equal
     */
        public boolean CompareDate(Date dateOne, Date dateTwo)
        {
            if(dateOne.getDate()==dateTwo.getDate() && dateOne.getMonth()==dateTwo.getMonth() && dateOne.getYear()==dateTwo.getYear())
            {
                return true;
            }else
                return false;
        }
}
