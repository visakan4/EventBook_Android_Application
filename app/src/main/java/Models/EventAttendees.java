package Models;

import java.util.Date;

/**
 * Created by jebes on 11/17/2017.
 */

/**
 * Class: Event Attendee represent a records of Event Attendee table indicating a user attending the event
 */
public class EventAttendees {
    /*
    Default Constructor of the Event Attendee instance
     */
    public EventAttendees() {

    }

    /**
     * Overloaded constructor of the Event Attendee instance
     * @param linkUserId - Unique user of the user attending the even
     * @param linkEventId - Event id of the event
     * @param eventDate - Start Date of the Event
     */
    public EventAttendees(String linkUserId, String linkEventId,Date eventDate) {
    this.setUserId(linkUserId);
    this.setEventId(linkEventId);
        this.setEventDate(eventDate);
    }

    /*
    Unnique identifier of the Event Attendee record
     */
    @com.google.gson.annotations.SerializedName("id")
    private String mId;
    public String getId() { return mId; }
    public final void setId(String id) { mId = id; }

    /*
    String unique Id of the Event Entity record
     */
    @com.google.gson.annotations.SerializedName("eventId")
    private String eventId;
    public String getEventId() { return eventId; }
    public final void setEventId(String id) { eventId = id; }
    /*
    String username of the Event User attending the event
     */
    @com.google.gson.annotations.SerializedName("userName")
    private String userName;
    public String getUserName() { return userName; }
    public final void setUserName(String user) { userName = user; }
    /*
    String user id of the Event User
     */
    @com.google.gson.annotations.SerializedName("userId")
    private String userId;
    public String getUserId() { return userId; }
    public final void setUserId(String id) { userId = id; }

    /*
    Date Time field represent the start date of the event
     */
    @com.google.gson.annotations.SerializedName("eventDate")
    private long eventDate;
    public Date getEventDate() { return new Date(eventDate); }
    public void setEventDate(Date logTime) { if(null!=logTime){eventDate = logTime.getTime();}; }

}
