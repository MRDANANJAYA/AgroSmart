package com.example.agrosmart.dialogs;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.example.agrosmart.MainActivity;
import com.example.agrosmart.R;
import com.example.agrosmart.ml.ImageProcessing;
import com.example.agrosmart.ml.WaterTimerReceiver;

import java.util.Calendar;


public class AlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        Calendar startTime = Calendar.getInstance();
        long alarmStartTime = startTime.getTimeInMillis();


        // create a notification object
        NotificationShow Watering_time = new NotificationShow(context);

        String message = intent.getStringExtra("NAME_ID");
        int water_minutes = Watering_time.getWateringMinute();




        //setContentIntent
        Intent mainIntent = new Intent(context, MainActivity.class);
        mainIntent.putExtra("TestOne", true);
        PendingIntent contentIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            contentIntent = PendingIntent.getActivity(context, 1, mainIntent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        }


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "My channel")
                .setSmallIcon(R.drawable.set_icon_notif_bell) //set icon for notification
                .setContentTitle("It's Time! " + message) //set title of notification
                .setContentText("Your Alarm has been rung. watering time : " + water_minutes + "Min" + ". do you want to water your plant Now?")//this is notification message
                .setAutoCancel(true) // makes auto cancel of notification
                .addAction(R.drawable.ic_baseline_access_alarm_24, "NOW",contentIntent)// When notification is tapped, call MainActivity.
                .setPriority(NotificationCompat.PRIORITY_DEFAULT); //set priority of notification


        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.notify(1, builder.build());




    }




}
