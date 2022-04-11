package com.example.agrosmart;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.agrosmart.NumberPickers.GreenNumberPicker;
import com.example.agrosmart.dialogs.NotificationShow;
import com.example.agrosmart.ml.ImageProcessing;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class Reminder extends AppCompatActivity {

    private static final String TAG = "";
    TextView time, water_time;
    BottomNavigationView bottomNavigationView;
    FloatingActionButton floatingActionButton;
    RelativeLayout rel2, wateringLayout;
    private String format = "";
    Button btOk, btClear;
    DatabaseReference mDatabaseref;
    EditText remindName;
    int hour;
    int min;
    String alarmName;
    String alarmTime;
    private NotificationShow settings;
    private final int notificationId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);


        //Define bottom Navigation bar
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        floatingActionButton = findViewById(R.id.Fab);
        bottomNavigationView.setBackground(null);
        bottomNavigationView.getMenu().getItem(2).setEnabled(false);
        rel2 = findViewById(R.id.r2);
        wateringLayout = findViewById(R.id.watering_Relative_layout);
        remindName = findViewById(R.id.RemindName);
        btOk = findViewById(R.id.TimePickOk);
        btClear = findViewById(R.id.TimePickClear);
        time = findViewById(R.id.textTimePick);
        water_time = findViewById(R.id.interval_text);

        // create a notification object
        settings = new NotificationShow(this);


        // Initialize Firebase Database
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://agrismartwatering-default-rtdb.firebaseio.com/");

        //Refer database
        mDatabaseref = db.getReference();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel("My channel", "My Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

        }


        rel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TimePickerDialog TimeDialog = new TimePickerDialog(Reminder.this, R.style.Timepicker, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        //Initialize hours and minutes
                        hour = hourOfDay;
                        min = minute;

                        //settings.setNotifHour(hour);
                        // settings.setNotifMinute(min);
                        formatNotifTimingTextView();

                    }
                }, settings.getNotifHour(), settings.getNotifMinute(), false);

                //display previous selected time
                TimeDialog.updateTime(hour, min);

                //show dialog
                TimeDialog.show();

            }
        });

        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alarmName = remindName.getText().toString();

                if (alarmName.isEmpty()) {
                    Toast.makeText(Reminder.this, "can not be empty", Toast.LENGTH_LONG).show();

                }
                if (alarmTime == null) {
                    Toast.makeText(Reminder.this, "Time can not be empty", Toast.LENGTH_LONG).show();
                } else {

                    addNotification();
                    mDatabaseref.child("Reminder").child(alarmName).child("Time").child("Hour").setValue((hour));
                    mDatabaseref.child("Reminder").child(alarmName).child("Time").child("Minutes").setValue((min));
                    mDatabaseref.child("Reminder").child(alarmName).child("Time").child("Format").setValue((format));

                    Toast.makeText(Reminder.this, "Reminder Set!", Toast.LENGTH_LONG).show();
                }

            }
        });

        btClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remindName.clearFocus();
                remindName.setText("");
                time.setText("");
            }
        });


        wateringLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Reminder.this, R.style.AlertDialogTheme);

                View view = LayoutInflater.from(Reminder.this).inflate(
                        R.layout.reminder_watering_time_dialog,
                        findViewById(R.id.layout_dialog_container)
                );

                builder.setView(view);
                final AlertDialog alertDialog = builder.create();

                final GreenNumberPicker numberPicker = view.findViewById(R.id.reminder_watering_numberpicker);
                numberPicker.setMinValue(0);
                numberPicker.setMaxValue(60);
                numberPicker.setWrapSelectorWheel(false);

                view.findViewById(R.id.postpone_accept_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int minutes = numberPicker.getValue();
                        String plural = getResources().getQuantityString(R.plurals.minutes, minutes);
                        water_time.setText(String.valueOf(minutes)+" "+plural);
                        alertDialog.dismiss();
                    }
                });

                if (alertDialog.getWindow() != null) {
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                }
                alertDialog.show();

            }
        });


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

    private void addNotification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, "My channel")
                        .setSmallIcon(R.drawable.set_icon_notif_bell) //set icon for notification
                        .setContentTitle(alarmName) //set title of notification
                        .setContentText("This is a notification message")//this is notification message
                        .setAutoCancel(true) // makes auto cancel of notification
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT); //set priority of notification


        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.notify(1, builder.build());
    }

    private void formatNotifTimingTextView() {

        int resultHour = settings.getNotifHour();
        int resultMini = settings.getNotifMinute();

        if (resultHour == 0) {
            resultHour += 12;
            format = "am";
        } else if (resultHour == 12) {
            format = "pm";
        } else if (resultHour > 12) {
            resultHour -= 12;
            format = "pm";
        } else {
            format = "am";
        }

        alarmTime = String.format(Locale.getDefault(), "%02d : %02d ", resultHour, resultMini) + format;
        time.setText(alarmTime);

    }


}