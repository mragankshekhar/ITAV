package com.mssinfotech.itav;

/**
 * Created by mssinfotech on 8/26/2016.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static com.mssinfotech.itav.CommonUtilities.EXTRA_MESSAGE;
import static com.mssinfotech.itav.CommonUtilities.SENDER_ID;
import static com.mssinfotech.itav.CommonUtilities.displayMessage;

public class GCMIntentService extends GCMBaseIntentService {

    private static final String TAG = "GCMIntentService";

    public GCMIntentService() {
        super(SENDER_ID);
    }

    /**
     * Method called on device registered
     **/
    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "Device registered: regId = " + registrationId);
        //displayMessage(context, "Your device registred with GCM");
        Log.d("NAME", HomeActivity.name);
        ServerUtilities.register(context, HomeActivity.name, HomeActivity.email, registrationId);
    }

    /**
     * Method called on device un registred
     * */
    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
        //displayMessage(context, getString(R.string.gcm_unregistered));
        ServerUtilities.unregister(context, registrationId);
    }

    /**
     * Method called on Receiving a new message
     * */
    @Override
    protected void onMessage(Context context, Intent intent) {

        String message = intent.getExtras().getString(EXTRA_MESSAGE);
        String ImageUrl = intent.getExtras().getString("image");
        String Url = intent.getExtras().getString("url");
        Log.i(TAG, "Received message mssin"+intent.getExtras().toString());
        displayMessage(context, message);
        // notifies user
        if(!ImageUrl.isEmpty() && ImageUrl != null && !ImageUrl.equals("null"))
            generateImageNotification(context, message,ImageUrl,Url);
        else
            generateNotification(context, message,Url);

    }

    /**
     * Method called on receiving a deleted message
     * */
    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        displayMessage(context, message);
        // notifies user
        //generateNotification(context, message);
    }

    /**
     * Method called on Error
     * */
    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "Received error: " + errorId);
        displayMessage(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        displayMessage(context, getString(R.string.gcm_recoverable_error,
                errorId));
        return super.onRecoverableError(context, errorId);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, String message, String urls) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.putExtra("url", urls);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int requestCode = 0;
        String title = context.getString(R.string.app_name);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(context)
                .setSound(sound)
                .setSmallIcon(R.drawable.logo)
                .setColor(context.getResources().getColor(R.color.notification))
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentText(message)
                //.setContentInfo(message)
                //.setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, noBuilder.build()); //0 = ID of notification
    }
    private static void generateImageNotification(Context context, String message,String ImageUrl, String urls) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.putExtra("url", urls);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int requestCode = 0;
        Bitmap remote_picture=null;
        String title = context.getString(R.string.app_name);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
        try {
            remote_picture = BitmapFactory.decodeStream((InputStream) new URL(ImageUrl).getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(context)
                .setSound(sound)
                .setSmallIcon(R.drawable.logo)
                .setColor(context.getResources().getColor(R.color.notification))
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(remote_picture));

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, noBuilder.build()); //0 = ID of notification
    }

}