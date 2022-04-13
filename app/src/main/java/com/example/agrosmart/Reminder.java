package com.example.agrosmart;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

import com.example.agrosmart.NumberPickers.GreenNumberPicker;
import com.example.agrosmart.dialogs.AlarmReceiver;
import com.example.agrosmart.dialogs.NotificationShow;
import com.example.agrosmart.ml.ImageProcessing;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Locale;

public class Reminder extends AppCompatActivity {

    private static final String TAG = "";
    int hour;
    int min;
    String alarmName;
    private TextView time, water_time;
    private String format = "";
    private DatabaseReference mDatabaseref;
    private EditText remindName;
    private String alarmTime;
    BottomNavigationView bottomNavigationView;
    FloatingActionButton floatingActionButton;
    RelativeLayout rel2, wateringLayout;
    Button btOk, btClear;
    private NotificationShow settings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        //Initialize hooks
        bottomNavigationView = findViewById(R.id.bottomNavigation); //Initialize bottom Navigation bar
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

        /**   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
         NotificationChannel channel =
         new NotificationChannel("My channel", "My Notification", NotificationManager.IMPORTANCE_DEFAULT);
         NotificationManager manager = getSystemService(NotificationManager.class);
         manager.createNotificationChannel(channel);

         }*/


        formatNotifTimingTextView();

        rel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TimePickerDialog TimeDialog = new TimePickerDialog(Reminder.this, R.style.Timepicker, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        //Initialize hours and minutes
                        hour = hourOfDay;
                        min = minute;

                        settings.setAlarmHour(hour);
                        settings.setAlarmMinute(min);
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
                    Toast.makeText(Reminder.this, "Reminder name can not be empty", Toast.LENGTH_LONG).show();

                } else if (alarmTime == null) {
                    Toast.makeText(Reminder.this, "Reminding Time can not be empty", Toast.LENGTH_LONG).show();
                } else {

                    mDatabaseref.child("Reminder").child(alarmName).child("Time").child("Hour").setValue((hour));
                    mDatabaseref.child("Reminder").child(alarmName).child("Time").child("Minutes").setValue((min));
                    mDatabaseref.child("Reminder").child(alarmName).child("Time").child("Format").setValue((format));
                    //Toast.makeText(Reminder.this, "Reminder Set!", Toast.LENGTH_SHORT).show();

                    startAlert(); // set alarm time

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

            @SuppressLint("NonConstantResourceId")
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

    //set the Notification time
    private void startAlert() {

        // Create time.
        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, hour);
        startTime.set(Calendar.MINUTE, min);
        startTime.set(Calendar.SECOND,0);
        long alarmStartTime = startTime.getTimeInMillis();
        String s = String.valueOf(Calendar.SECOND);

        if (settings.getNotifEnabled()) {
           String side =  String.valueOf(alarmStartTime + (settings.getNotifRepetInterval() * 60 * 60 * 1000L));
            // getBroadcast(context, requestCode, intent, flags)
            Intent intent = new Intent(this, AlarmReceiver.class);
            intent.putExtra("NAME_ID", alarmName);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am = (AlarmManager) this.getSystemService(ALARM_SERVICE);
            if (settings.getNotifRepetInterval() > 0) {
                //set Trigger alarm
                am.set(AlarmManager.RTC_WAKEUP, alarmStartTime + (settings.getNotifRepetInterval() * 60 * 60 * 1000L), pendingIntent);
                Toast.makeText(this, "Reminder set for: " + side, Toast.LENGTH_SHORT).show();
            } else {
                //set Trigger alarm
                am.set(AlarmManager.RTC_WAKEUP, alarmStartTime, pendingIntent);
                Toast.makeText(this, "Reminder set for: " + hour + "h " + min + "m " + s +"s", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Notification is disabled! Turn on first before set the alarm", Toast.LENGTH_SHORT).show();
        }
    }


    private void formatNotifTimingTextView() {

        int resultHour = settings.getAlarmHour();
        int resultMini = settings.getAlarmMinute();

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