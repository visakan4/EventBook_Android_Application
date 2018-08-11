package DataProvider;

import android.app.Activity;
import android.app.Service;
import android.content.Context;

import com.example.eventbookapp.*;
import com.example.eventbookapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.drive.Drive;
import com.microsoft.windowsazure.mobileservices.*;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.notifications.NotificationsHandler;
import com.microsoft.windowsazure.notifications.NotificationsManager;

import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutionException;

import Models.EventEntity;
import Models.EventUsers;
import PushNotification.NotificationHandler;

/**
 * Created by jebes on 11/10/2017.
 */

/**
 * class: Service Data Provider act as a global instance of Event Book App
 */
public class ServiceDataProvider {

    /*
    mContext provides the android activity context of the launcher activity
     */
    private Context mContext;
    private MobileServiceClient mobileServiceClient;
    private static ServiceDataProvider globalInstance = null;
    public static SimpleDateFormat dateFormat;
    private static final String SENDER_ID = "555698293404";
    private String serviceUrl;
    private GoogleSignInClient googleSignInClient;

    /*
    Set Current user Setter property to make the glaobl instace aware of the current user
     */
    public void setCurrentuser(EventUsers currentuser) {
        this.currentuser = currentuser;
    }

    private EventUsers currentuser;

    /*
    Indicates whether the user made a normal login or OAuth login
     */
    public void setNormalLogin(boolean normalLogin) {
        this.normalLogin = normalLogin;
    }

    private boolean normalLogin =false;

    /*
    Get the current userId string of the EventBook
     */
    public String getUserId() {
        String id ="";
        if(normalLogin)
        {
            id=  currentuser.getId();
        }else if(!normalLogin)
        {
            id=  mobileServiceClient.getCurrentUser().getUserId();

        }
        return id;
    }
    /*
    Get the String Username of the Event user of the Event Book App
     */
    public String getUserName() {
        String name ="";
        if(normalLogin)
        {
            name=  currentuser.getUserName();
        }else if(!normalLogin)
        {
            name= "Anonymous";

        }
        return name;
    }
    /*
    Boolean property indicates whether the current Event User has Admoinistrative rights
     */
    public boolean hasAdminRights()
    {
        boolean isAdmin=false;
        if(normalLogin)
        {
            isAdmin = currentuser.isHasAdminRights();
        }else
        {
            isAdmin =false;
        }
        return isAdmin;
    }
    /*
    Getter Property for retrieving the application context
     */
    public Context getmContext() {
        return mContext.getApplicationContext();
    }

    /*
    Getter property which retrieve the Mobile Service Client
     */
    public MobileServiceClient getMobileServiceClient() {
        return mobileServiceClient;
    }

    /*
    Getter property which retrieve the current class singleton instance
     */
    public static ServiceDataProvider getGlobalInstance() {
        return globalInstance;
    }

    /**
     * Overloaded private constructor with context argument
     * @param context - Launcher activity context
     */
    private ServiceDataProvider(Context context)
    {
        this.mContext = context;
        serviceUrl =  context.getResources().getString(R.string.service_url);
        try {
            mobileServiceClient = new MobileServiceClient(serviceUrl, mContext);
            dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            NotificationsManager.handleNotifications(mContext, SENDER_ID, NotificationHandler.class);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method - Instantiate the singleton instance of teh service data provider class
     * @param contex -  Android Activity context
     * @return Service Provider class instance
     */
    public static ServiceDataProvider Instantiate(Context contex)
    {
        if(globalInstance==null)
        globalInstance = new ServiceDataProvider(contex);
        return globalInstance;
    }

    /**
     * Method GetGoogleSignInClient to retrieve the Google API Login
     * @param callingActivity
     * @return
     */
    public GoogleSignInClient getGoogleSignInClient(Activity callingActivity) {

        if(googleSignInClient ==null)
        {
            googleSignInClient = CreateGoogleSignInClient(callingActivity);
        }
        return googleSignInClient;
    }

    /**
     *Method verifies whetther user has already logged in through Google API
     * @return
     */
    public boolean requiresGoogleAuthorization()
    {
        return null==googleSignInClient;

    }

    /**
     * Method setGoogleSignInClient sets the current Google SignIn Client
     * @param googleSignInClient - Google SignIn Client Instance
     */
    public void setGoogleSignInClient(GoogleSignInClient googleSignInClient) {
        this.googleSignInClient = googleSignInClient;
    }

    /**
     * Method CreateGoogleSignClient creates the google SignIn Client
     */
    private GoogleSignInClient CreateGoogleSignInClient(Activity callingActivity)
    {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(Drive.SCOPE_FILE)
                        .build();
        return GoogleSignIn.getClient(callingActivity, signInOptions);
    }

}


