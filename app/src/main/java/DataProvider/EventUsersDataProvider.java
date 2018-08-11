package DataProvider;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import Models.EventAttendees;
import Models.EventEntity;
import Models.EventUsers;

/**
 * Created by jebes on 11/17/2017.
 */

/**
 * Class Event User Data provider class for Event User table access
 */
public class EventUsersDataProvider {

    MobileServiceClient mobileServiceClient;
    MobileServiceTable<EventUsers> eventUsersTable;

    /**
     * Method EventUserDataProvider- Default Constructor of the EventUserDataProvider Class
     */
    public EventUsersDataProvider() {
        mobileServiceClient = ServiceDataProvider.getGlobalInstance().getMobileServiceClient();
        eventUsersTable = mobileServiceClient.getTable("EventUsers",EventUsers.class);
    }

    /**
     * Method CreateEventUsers for creating a EventUser record Eventuser table in the database
     * @param user  -Event User instance to create in the table
     * @return
     */
    public EventUsers CreateEventUsers(EventUsers user)
    {
        EventUsers out =null;
        try{
            out = eventUsersTable.insert(user).get();
        }catch (InterruptedException e) {
        e.printStackTrace();
        } catch (ExecutionException e) {
        e.printStackTrace();
        }
        return out;
    }

    /**
     * Method UpdateEventUsers - Update the Event User table record with the supplied Eventuser object matched by Id
     * @param user - EventUser entity instane that requires update
     * @return
     */
    public EventUsers UpdateEventUsers(EventUsers user)
    {
        EventUsers out =null;
        try{
            out = eventUsersTable.update(user).get();
        }catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return out;
    }

    /**
     * Method DeleteEventUsers -  Delete a specific event user from the EventUser table by user Instance
     * @param user - Event user instance to delete on the table
     */
    public void DeleteEventUsers(EventUsers user)
    {
        eventUsersTable.delete(user);
    }

    /**
     * Method DeleteEventUsers -  Delete a specific event user from the EventUser table by user Id supplied
     * @param userId - Event user instance to delete on the table
     */
    public void DeleteEventUsersById(String userId)
    {
        eventUsersTable.delete(userId);
    }

    /**
     * Method DeleteEventUsers -  Get a specific event user from the EventUser table by user Id
     * @param userId - Event user instance to retrieve from the table
     */
    public EventUsers GetEventUsersById(String userId)
    {
        EventUsers out=null;
        try {
            out =  eventUsersTable.lookUp(userId).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out;
    }

    /**
     * Method GetEventUserByName -  Get a list of event user from the EventUser table by user Name
     * @param userName - Event user name to retrieve from the table
     */
    public ArrayList<EventUsers> GetEventUserbyUserName(String userName)
    {
        MobileServiceList<EventUsers> queryUsers=null;
        try {
            queryUsers =  eventUsersTable.where().field("userName").eq(userName).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return queryUsers;
    }

    /**
     * Method GetEventuserByEmail -  Get a list of event user from the EventUser table by user email address
     * @param emailAddress - Event user email that will be queried against to retrieve from the table
     */
    public ArrayList<EventUsers> GetEventUserbyEmail(String emailAddress)
    {
        MobileServiceList<EventUsers> queryEmail=null;
        try {
            queryEmail =  eventUsersTable.where().field("emailAddress").eq(emailAddress).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return queryEmail;
    }


}
