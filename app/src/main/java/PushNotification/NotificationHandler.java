package PushNotification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.example.eventbookapp.HomeActivity;
import com.example.eventbookapp.R;
import com.google.firebase.messaging.RemoteMessage;
import com.microsoft.windowsazure.mobileservices.notifications.MobileServicePush;
import com.microsoft.windowsazure.notifications.NotificationsHandler;

import DataProvider.ServiceDataProvider;

/**
 * Created by jebes on 11/18/2017.
 */


/**
 * Class Name: Notification Handler class that listens to push Notification
 *
 * Functionality : OnReceive Method will be handle the incoming push notification from server
 */
public class NotificationHandler extends NotificationsHandler {

    public static final int NOTIFICATION_ID = 1;
    public static final int Accept_Action = 100;
    public static final int Dismiss_Action = 101;

    /**
     * Constructor that accepts activity context and Google Cloud Messaging Registration Id for instantiating the Notification handler class
     * @param context -  Android Acivity context
     * @param gcmRegistrationId - Google Cloud Messaging unique app Registered Id
     */
    @Override
    public void onRegistered(Context context, String gcmRegistrationId) {
        super.onRegistered(context, gcmRegistrationId);
        RegisterMeWithNotificatinHub(gcmRegistrationId);
    }

    /**
     * Overriden OnReceive Implementation of the Default Notification Handler Class
     * @param context - Acivity android Context
     * @param bundle - Instance Bundle
     */

    @Override
    public void onReceive(Context context, Bundle bundle) {
        super.onReceive(context, bundle);

        Intent actionIntent = new Intent(context,HomeActivity.class);


        PendingIntent notificationIntent = PendingIntent.getActivity(context,0,new Intent(context, HomeActivity.class),0);
        String receivedMessage = bundle.getString("eventName");

        Notification.Action AcceptAction =new  Notification.Action.Builder(Icon.createWithResource(context,R.drawable.ic_action_checkmark),
                "Accept",notificationIntent).build();
        Notification.Action DismissAction =new  Notification.Action.Builder(Icon.createWithResource(context,R.drawable.ic_action_cross),
                "Dismiss",notificationIntent).build();
        /**
         * Code which notify the user on receive of a push Notification
         */
        Uri notifySound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification pushNotification = new  Notification.Builder(context).setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("EventBook")
                .setContentText(receivedMessage + "just created.")
                .setContentIntent(notificationIntent).setAutoCancel(true).setSound(notifySound)
                .addAction(AcceptAction)
                .addAction(DismissAction)
                .build();
        /*
         * Call the Notification service to build the Notification build instance
         */
        NotificationManager eventNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        eventNotificationManager.notify(1,pushNotification);
    }

    /**
     * Register the current device asynchronously with Azure notification hub
     * @param gsmRegId -  Current Device unique registration Id
     */
    private void RegisterMeWithNotificatinHub(final String gsmRegId)
    {

        AsyncTask<Void,Void,Void> registerTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                MobileServicePush servicePush= ServiceDataProvider.getGlobalInstance().getMobileServiceClient().getPush();
                servicePush.register(gsmRegId);
                return null;
            }
        };

        runAsyncTask(registerTask);
    }


    /**
     * Async Method helper to run async task synchrronously in the background thread
     * @param task - Async task Instance
     * @return Task handler of the input task
     */
    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }
}
