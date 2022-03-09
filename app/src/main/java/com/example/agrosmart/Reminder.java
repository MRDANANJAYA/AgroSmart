package com.example.agrosmart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.EventListener;
import java.util.Locale;

public class Reminder extends AppCompatActivity {

    private static final String TAG = "";
    TextView time;
    BottomNavigationView bottomNavigationView;
    FloatingActionButton floatingActionButton;
    private String format = "";
    RelativeLayout rel2;
    DatabaseReference mDatabaseref;
    EditText remindName;
    Button btOk,btClear;
    int hour;
    int min;
    String alarmName;
    String alarmTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        //Define bottom Navigation bar
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        floatingActionButton = findViewById(R.id.Fab);
        bottomNavigationView.setBackground(null);
        bottomNavigationView.getMenu().getItem(2).setEnabled(false);
        rel2 = (RelativeLayout) findViewById(R.id.r2);
        remindName = (EditText) findViewById(R.id.RemindName);
        btOk = findViewById(R.id.TimePickOk);
        btClear = findViewById(R.id.TimePickClear);
        time = findViewById(R.id.textTimePick);


        // Initialize Firebase Database
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://agrismartwatering-default-rtdb.firebaseio.com/");

        //Refer database
        mDatabaseref = db.getReference();


        rel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                TimePickerDialog TimeDialog = new TimePickerDialog(Reminder.this,R.style.Timepicker, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view,  int hourOfDay, int minute) {

                        //Initialize hours and minutes
                         hour = hourOfDay;
                         min = minute;

                        if (hour == 0) {
                            hour += 12;
                            format = "am";
                        } else if (hour == 12) {
                            format = "pm";
                        } else if (hour > 12) {
                            hour -= 12;
                            format = "pm";
                        } else {
                            format = "am";
                        }

                      alarmTime = String.format(Locale.getDefault(),"%02d : %02d ", hour,min)+format;
                        time.setText(alarmTime);

                    }
                },12,0,false
                );


                //display previous selected time
                TimeDialog.updateTime(hour,min);

                //show dialog
                TimeDialog.show();
                  //theme overlay

            }
        });


        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alarmName = remindName.getText().toString();

                if (alarmName.isEmpty()){
                    Toast.makeText(Reminder.this,"can not be empty", Toast.LENGTH_LONG).show();

                }if(alarmTime == null){
                    Toast.makeText(Reminder.this,"Time can not be empty", Toast.LENGTH_LONG).show();
                }else {

                    mDatabaseref.child("Reminder").child(alarmName).child("Time").child("Hour").setValue((hour));
                    mDatabaseref.child("Reminder").child(alarmName).child("Time").child("Minutes").setValue((min));
                    mDatabaseref.child("Reminder").child(alarmName).child("Time").child("Format").setValue((format));

                    Toast.makeText(Reminder.this,"Successfully Completed", Toast.LENGTH_LONG).show();
                }

            }
        });

        btClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remindName.setText("");
                time.setText("");
            }
        });



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




}