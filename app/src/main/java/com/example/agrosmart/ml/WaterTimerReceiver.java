package com.example.agrosmart.ml;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class WaterTimerReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        DatabaseReference mDatabaseref;
        // Initialize Firebase Database
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://agrismartwatering-default-rtdb.firebaseio.com/");
        //Refer database
        mDatabaseref = db.getReference();


       mDatabaseref.child("pump").child("status").setValue(0);
        Toast.makeText(context, "Worked", Toast.LENGTH_LONG).show();

    }
}
