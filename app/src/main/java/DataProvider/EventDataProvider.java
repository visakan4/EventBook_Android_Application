package DataProvider;

import android.app.Service;
import android.util.Log;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.query.QueryOrder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import Models.EventEntity;

/**
 * Created by jebes on 11/11/2017.
 */

/**
 * Class EventDataProvider -  DataProvider class for retrieving the Event Records
 */

public class EventDataProvider
{

    MobileServiceTable<EventEntity> eventTable;

    public static ArrayList<EventEntity> eventData;

    /*
    Default Constructor for EventDataProvider
     */
    public EventDataProvider()
    {
        MobileServiceClient mc = ServiceDataProvider.getGlobalInstance().getMobileServiceClient();
        eventTable = mc.getTable("EventEntity",EventEntity.class);
    }

    /**
     * Method Create Events -  Called to Create a event entity on the Events table
     * @param event - Specific event instance to create
     * @return - Create event entity
     */
    public EventEntity CreateEvents(EventEntity event)
    {
        EventEntity out=null;
        try {
            String owner = ServiceDataProvider.getGlobalInstance().getUserId();
            event.setOwner(owner);
            event.setEventCreatedOnDate(new Date());
            out =  eventTable.insert(event).get();
        } catch (InterruptedException e) {

            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return out;
    }

    /**
     * Method UpdateEvents - Called to Update a Specific event entity on the Event table
     * @param event  -  Event entity that needs to be updated
     * @return updated event entity
     */
    public EventEntity UpdateEvents(EventEntity event)
    {
        EventEntity out=null;
        try {
            out =  eventTable.update(event).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return out;
    }

    /**
     * Method DeleteEvents -Called to delete the Event instance from Events table
     * @param event - Event object to delete
     */
    public void DeleteEvents(EventEntity event)
    {
        eventTable.delete(event);
    }

    /**
     * Method DeleteEvents -Called to delete the Event instance from Events table by Id
     * @param eventId - Event id to delete
     */
    public void DeleteEventsById(String eventId)
    {
        eventTable.delete(eventId);
    }

    /**
     * Method GetEventById - Retrieve single event by Id
     * @param id - Id of the event to retrieve
     * @return
     */
    public EventEntity GetEventById(String id)
    {
        EventEntity out=null;
        try {
            out =  eventTable.lookUp(id).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out;
    }

    /**
     * Method GetAllFutureEvets - Retireve a list of all events whose start date is later than today
     * @return - List of Event Entity
     */
    public ArrayList<EventEntity> GetAllFutureEvents()
    {
        MobileServiceList<EventEntity> events = null;
        try{
            events =  eventTable.where().field("startDate").gt((new Date()).getTime()).orderBy("startDate", QueryOrder.Ascending).execute().get();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return events;
    }

}