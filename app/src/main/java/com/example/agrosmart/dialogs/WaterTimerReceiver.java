package com.example.agrosmart.dialogs;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.agrosmart.R;
import com.example.agrosmart.dialogs.NotificationShow;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class WaterTimerReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        DatabaseReference mDatabaseref;

        // create a pump Switch object
        NotificationShow switch_enabler = new NotificationShow(context);
        // Initialize Firebase Database
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://agrismartwatering-default-rtdb.firebaseio.com/");
        //Refer database
        mDatabaseref = db.getReference();

        //set value
        mDatabaseref.child("pump").child("status").setValue(0);


        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, "New channel")
                        .setSmallIcon(R.drawable.set_icon_notif_bell) //set icon for notification
                        .setContentTitle("Time Up!") //set title of notification
                        .setContentText("Watering Has been Completed")//this is notification message
                        .setAutoCancel(true) // makes auto cancel of notification
                        .setPriority(Notification.PRIORITY_DEFAULT); //set priority of notification

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // === Removed some obsoletes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "New channel";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "New channel",
                    NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }

        manager.notify(1, builder.build());

        Toast.makeText(context, "Watering Has been Completed", Toast.LENGTH_LONG).show();


    }
}
