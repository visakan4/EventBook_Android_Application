package Models;

import java.util.Date;

import DataProvider.EventUsersDataProvider;

/**
 * Created by jebes on 11/17/2017.
 */


/**
 * Class :Event User Model for representing EventBook Users
 */
public class EventUsers {

    /*
    Constructor of the Event users class - Placed for applying any extension
     */
    public EventUsers() {
    }

    /*
    Unique String Identifier of the Event User
     */

    @com.google.gson.annotations.SerializedName("id")
    private String mId;
    public String getId() { return mId; }
    public final void setId(String id) { mId = id; }

    /*
    String User Name of the Event Book User
     */
    @com.google.gson.annotations.SerializedName("userName")
    private String userName;
    public String getUserName() { return userName; }
    public final void setUserName(String user) { userName = user; }

    /*
    String Email Address of the current Event User
     */
    @com.google.gson.annotations.SerializedName("emailAddress")
    private String email;
    public String getEmailAddress() { return email; }
    public final void setEmailAddress(String emailAddress) { email = emailAddress; }

    /*
    Long Phone Number of the Current Event user
     */
    @com.google.gson.annotations.SerializedName("phoneNumber")
    private long phoneNumber;
    public long getstartDate() { return phoneNumber;}
    public void setstartDate(long phone) { phoneNumber = phone; }

    /*
    Password string credentials of the Event User record
     */
    @com.google.gson.annotations.SerializedName("password")
    private String pass;
    public String getPassword() { return pass; }
    public final void setPassword(String password) { pass = password; }

    /*
    Bool property describes whether the user logged in using OAuth
     */
    @com.google.gson.annotations.SerializedName("isOAuthEnabled")
    private boolean isOAuthEnabled;
    public boolean getIsOAuthEnabled() { return isOAuthEnabled; }
    public final void setIsOAuthEnabled(boolean IsOAuth) { isOAuthEnabled = IsOAuth; }
    /*
    Date Time represent the Last Logged In time for the current user
     */
    @com.google.gson.annotations.SerializedName("lastLoggedIn")
    private long lastLoggedIn;
    public Date getLastLoggedIn() { return new Date(lastLoggedIn); }
    public void setLastLoggedIn(Date logTime) { if(null!=logTime){lastLoggedIn = logTime.getTime();}; }

    /*
    Boolean property describes whether the user has Administrative rights
     */
    @com.google.gson.annotations.SerializedName("hasAdminRights")
    private boolean hasAdminRights;
    public boolean isHasAdminRights() { return hasAdminRights; }
    public final void setHasAdminRights(boolean isAdmin) { hasAdminRights = isAdmin; }

}
