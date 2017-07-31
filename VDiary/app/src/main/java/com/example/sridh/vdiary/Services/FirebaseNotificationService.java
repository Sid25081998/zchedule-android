package com.example.sridh.vdiary.Services;



import com.example.sridh.vdiary.Classes.Notification_Creator;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by sid on 7/6/17.
 */

public class FirebaseNotificationService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Notification_Creator notification_creator = new Notification_Creator(remoteMessage.getNotification().getBody(),"Zchedule","New Notification",this);
            notification_creator.create_notification(1);
        }

        super.onMessageReceived(remoteMessage);
    }
}
