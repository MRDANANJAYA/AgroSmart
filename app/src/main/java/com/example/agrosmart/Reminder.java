package com.example.agrosmart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.agrosmart.login.SignUp;
import com.example.agrosmart.ml.ImageProcessing;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.EventListener;
import java.util.Locale;

public class Reminder extends AppCompatActivity {

    private static final String TAG = "";
    TimePicker timePicker1;
    TextView time;
    BottomNavigationView bottomNavigationView;
    FloatingActionButton floatingActionButton;
    private Calendar calendar;
    private String format = "", name ="";
    RelativeLayout rel2, RemRel;
    Button timeOk, timeCancel;
    DatabaseReference mDatabaseref;
    EditText remindName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        //Define bottom Navigation bar
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        floatingActionButton = findViewById(R.id.Fab);
        bottomNavigationView.setBackground(null);
        bottomNavigationView.getMenu().getItem(2).setEnabled(false);
        timePicker1 = (TimePicker) findViewById(R.id.timePicker1);
        time = (TextView) findViewById(R.id.textTimePick);
        rel2 = (RelativeLayout) findViewById(R.id.r2);
        RemRel = (RelativeLayout) findViewById(R.id.reminderCardRel);
        timeOk = (Button) findViewById(R.id.TimePickOk);
        timeCancel = (Button) findViewById(R.id.TimePickCancel);
        remindName = (EditText) findViewById(R.id.RemindName);

        // Initialize Firebase Database
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://agrismartwatering-default-rtdb.firebaseio.com/");

        //Refer database
        mDatabaseref = db.getReference();


        rel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RemRel.setVisibility(View.VISIBLE);


                timeOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int hour = timePicker1.getCurrentHour();
                        int min = timePicker1.getCurrentMinute();
                        name = remindName.getText().toString();

                        showTime(hour, min);
                        RemRel.setVisibility(View.GONE);
                    }
                });

                timeCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RemRel.setVisibility(View.GONE);
                        time.setText("");
                    }
                });


            }
        });





        String name = "dana";
        // code under construction
       /** Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:a");
        String strDate =  mdFormat.format(calendar.getTime());
        Toast.makeText(Reminder.this, ""+ (strDate), Toast.LENGTH_LONG).show();**/
      //  mDatabaseref.child("Reminder"+ (name)).child("Time").setValue(ServerValue.TIMESTAMP);









        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent main= new Intent(Reminder.this, MainActivity.class);
                startActivity(main);


            }
        });

        //set default select
        bottomNavigationView.setSelectedItemId(R.id.reminder);

        //select Menu items with listener
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







    }



    private void showTime(int hour, int min) {

        if (hour == 0) {
            hour += 12;
            format = "AM";
        } else if (hour == 12) {
            format = "PM";
        } else if (hour > 12) {
            hour -= 12;
            format = "PM";
        } else {
            format = "AM";
        }

        time.setText(new StringBuilder().append(hour).append(" : ").append(min)
                .append(" ").append(format));



        mDatabaseref.child("Reminder").child(name).child("Time").child("Hour").setValue((hour));
        mDatabaseref.child("Reminder").child(name).child("Time").child("Min").setValue((min));
        mDatabaseref.child("Reminder").child(name).child("Time").child("Format").setValue((format));
    }
}