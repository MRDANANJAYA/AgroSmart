package com.example.agrosmart.dialogs;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.example.agrosmart.MainActivity;
import com.example.agrosmart.R;




public class AlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        String message = intent.getStringExtra("NAME_ID");

        //setContentIntent
        Intent mainIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 1, mainIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        // Toast.makeText(context, "ado!", Toast.LENGTH_LONG).show();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "My channel")
                .setSmallIcon(R.drawable.set_icon_notif_bell) //set icon for notification
                .setContentTitle("It's Time! "+ message) //set title of notification
                .setContentText("This is a notification message")//this is notification message
                .setAutoCancel(true) // makes auto cancel of notification
                .setWhen(System.currentTimeMillis())
                .setContentIntent(contentIntent)// When notification is tapped, call MainActivity.
                .setPriority(NotificationCompat.PRIORITY_DEFAULT); //set priority of notification


        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.notify(1, builder.build());

    }

}
