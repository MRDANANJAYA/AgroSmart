package com.example.agrosmart.dialogs;


import static android.os.Build.*;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.agrosmart.MainActivity;
import com.example.agrosmart.R;



public class AlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {


        Toast.makeText(context, "Called" , Toast.LENGTH_LONG).show();

        // create a notification object
        NotificationShow Watering_time = new NotificationShow(context);

        String message = intent.getStringExtra("NAME_ID");
        int water_minutes = Watering_time.getWateringMinute();




        //setContentIntent
        Intent mainIntent = new Intent(context, MainActivity.class);
        mainIntent.putExtra("TestOne", true);
        PendingIntent contentIntent = null;
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            contentIntent = PendingIntent.getActivity(context, 1, mainIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        }


        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, "My channel")
                .setSmallIcon(R.drawable.set_icon_notif_bell) //set icon for notification
                .setContentTitle("It's Time! " + message) //set title of notification
                .setContentText("Your Alarm has been rung. watering time : " + water_minutes + "Min" + ". do you want to water your plant Now?")//this is notification message
                .setAutoCancel(true) // makes auto cancel of notification
                .addAction(R.drawable.ic_baseline_access_alarm_24, "NOW",contentIntent)// When notification is tapped, call MainActivity.
                .setPriority(Notification.PRIORITY_DEFAULT); //set priority of notification


        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        // === Removed some obsoletes
        if (VERSION.SDK_INT >= VERSION_CODES.O)
        {
            String channelId = "My channel";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }

        manager.notify(1, builder.build());




    }




}
