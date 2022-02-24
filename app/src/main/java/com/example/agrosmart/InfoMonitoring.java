package com.example.agrosmart;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import com.airbnb.lottie.LottieAnimationView;
import com.example.agrosmart.login.LoginActivity;
import com.example.agrosmart.login.SignUp;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class InfoMonitoring extends AppCompatActivity {

    private DatabaseReference mDatabase;
    BottomNavigationView bottomNavigationView;
    FloatingActionButton floatingActionButton;
    private TextView mHumid;
    private TextView mTemp;
    private TextView mLight;
    private TextView mMoisture;
    private TextView mMoisture2;
    private TextView lStatus;
    private TextView mStatus;
    private LottieAnimationView lottieAnimationLoading;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_monitoring);



        mHumid = findViewById(R.id.humidity_value);
        mTemp = findViewById(R.id.temperature_value);
        mMoisture= findViewById(R.id.moisture_value);
        mMoisture2 = findViewById(R.id.moisture_value2);
        lStatus = findViewById(R.id.Light_Status);
        mStatus = findViewById(R.id.Motor_Status);
        mLight = findViewById(R.id.light_value);
        lottieAnimationLoading = findViewById(R.id.lottieMonitor);

        //Define bottom Navigation bar
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        floatingActionButton = findViewById(R.id.Fab);
        bottomNavigationView.setBackground(null);
        bottomNavigationView.getMenu().getItem(2).setEnabled(false);


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent main= new Intent(InfoMonitoring.this, MainActivity.class);
                startActivity(main);
                finish();

            }
        });

        lottieAnimationLoading.setVisibility(View.VISIBLE);



        //set default select
        bottomNavigationView.setSelectedItemId(R.id.Sensor);

        //select Menu items
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                switch (id) {

                    case R.id.reminder:
                        startActivity(new Intent(getApplicationContext(),
                                Reminder.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.Sensor:
                        startActivity(new Intent(getApplicationContext(),
                                InfoMonitoring.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.Settings:
                        startActivity(new Intent(getApplicationContext(),
                                Settings.class));
                        overridePendingTransition(0, 0);
                        return true;


                    case R.id.ImagePro:
                        startActivity(new Intent(getApplicationContext(),
                                ImageProcessing.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });






        sensorData();

    }



     public void sensorData() {

         mDatabase = FirebaseDatabase.getInstance("https://agrismartwatering-default-rtdb.firebaseio.com/").getReference();
         mDatabase.child("TempData").orderByKey().limitToLast(1).addChildEventListener(new ChildEventListener() {
             @Override
             public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                 Double Humid = snapshot.child("humidity").getValue(Double.class);
                 mHumid.setText(String.format("%.2f", Humid));


                 Double Temp = snapshot.child("temperature").getValue(Double.class);
                 mTemp.setText(String.format("%.2f", Temp));

                 String Moisture = snapshot.child("moisture").getValue(String.class);
                 mMoisture.setText(Moisture);

                 String Moisture2 = snapshot.child("moisture2").getValue(String.class);
                 mMoisture2.setText(Moisture2);


                 Integer Light = snapshot.child("light").getValue(Integer.class);
                 mLight.setText(Integer.toString(Light));

                 lottieAnimationLoading.setVisibility(View.GONE);

             }



             @Override
             public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

             }

             @Override
             public void onChildRemoved(@NonNull DataSnapshot snapshot) {

             }

             @Override
             public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }

         });


         mDatabase = FirebaseDatabase.getInstance("https://agrismartwatering-default-rtdb.firebaseio.com/").getReference("led");
         mDatabase.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {

                 Integer LightS = snapshot.child("activation").getValue(Integer.class);
                 if(LightS == 1){
                     String text = "ON";
                     lStatus.setTextColor(Color.parseColor("#2DBB54"));
                     lStatus.setText(text);
                 }else {
                     String text2 = "OFF";
                     lStatus.setTextColor(Color.parseColor("#EF5350"));
                     lStatus.setText(text2);
                 }


             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });
         mDatabase = FirebaseDatabase.getInstance("https://agrismartwatering-default-rtdb.firebaseio.com/").getReference("pump");
         mDatabase.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 Integer MotorS = snapshot.child("activation").getValue(Integer.class);
                 if(MotorS == 1){
                     String text = "ON";
                     mStatus.setTextColor(Color.parseColor("#2DBB54"));
                     mStatus.setText(text);
                 }else {
                     String text2 = "OFF";
                     mStatus.setTextColor(Color.parseColor("#EF5350"));
                     mStatus.setText(text2);

                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });



             }




}