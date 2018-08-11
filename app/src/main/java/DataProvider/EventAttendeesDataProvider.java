package DataProvider;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import Models.EventAttendees;
import Models.EventUsers;

/**
 * Created by jebes on 11/17/2017.
 */

/**
 *
 * Class Name: EventAttendeesDataProvider
 *
 * Functionality : Data provider for the EventAttendee table
 *
 */

public class EventAttendeesDataProvider {

    MobileServiceClient mobileServiceClient;
    MobileServiceTable<EventAttendees> eventAttendeeTable;

    /**
     * constructor for the class
     *
     */
    public EventAttendeesDataProvider() {
        mobileServiceClient = ServiceDataProvider.getGlobalInstance().getMobileServiceClient();
        eventAttendeeTable = mobileServiceClient.getTable("EventAttendees",EventAttendees.class);
    }

    /**
     *
     * Method Name: EventAttendees
     *
     * Functionality : To create a new event attendee for a event
     *
     * @param attendee - EventAttendee object
     * @return EventAttendee object
     */

    public EventAttendees CreateEventAttendee(EventAttendees attendee)
    {
        EventAttendees out =null;
        try{
            out = eventAttendeeTable.insert(attendee).get();
        }catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return out;
    }

    /**
     *
     * Method Name: DeleteEventAttendeeRecord
     *
     * Functionality: Delete a attendee from a event
     *
     * @param eventAttendee - Event Attendede object which is to be deleted
     */

    public void DeleteEventAttendeeRecord(EventAttendees eventAttendee)
    {
        eventAttendeeTable.delete(eventAttendee.getId());
    }

    /**
     *
     * Method Name: GetEventAttendeeRecordByUser
     *
     * Functionality: To get the list of events the user has attended
     *
     * @param user - EventUsers object
     * @return ArrayList of events attended by user
     */

    public ArrayList<EventAttendees> GetEventAttendeeRecordByUser(EventUsers user)
    {
        MobileServiceList<EventAttendees> queriedEventAttendees=null;
        try {
            if(null!=user)
            queriedEventAttendees =  eventAttendeeTable.where().field("userId").eq(user.getId()).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return queriedEventAttendees;
    }

    /**
     *
     * Method Name: GetEventAttendeeRecordByUserId
     *
     * Functionality : To get the list of events attended by userID
     *
     * @param userId - User Id of the user
     * @return ArrayList of events
     */

    public ArrayList<EventAttendees> GetEventAttendeeRecordByUserId(String userId)
    {
        MobileServiceList<EventAttendees> queriedEventAttendees=null;
        try {
            if(null!=userId)
                queriedEventAttendees =  eventAttendeeTable.where().field("userId").eq(userId).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return queriedEventAttendees;
    }

    /**
     *
     * Method Name: GetEventAttendeeRecordByEventId
     *
     * Functionality: to get the list of attendees of a event by event id
     *
     * @param eventId - String value which contains the eventID
     * @return ArrayList of events
     */

    public ArrayList<EventAttendees> GetEventAttendeeRecordByEventId(String eventId)
    {
        MobileServiceList<EventAttendees> queriedEventAttendees=null;
        try {
            if(null!=eventId)
                queriedEventAttendees =  eventAttendeeTable.where().field("eventId").eq(eventId).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return queriedEventAttendees;
    }

}
