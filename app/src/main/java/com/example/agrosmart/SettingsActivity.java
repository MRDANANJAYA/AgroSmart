

package com.example.agrosmart;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.agrosmart.NumberPickers.GreenNumberPicker;
import com.example.agrosmart.dialogs.NotificationShow;
import com.example.agrosmart.ml.ImageProcessing;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    FloatingActionButton floatingActionButton;
    int hour;
    int min;
    String alarmTime, userId;
    private SwitchCompat notifEnablerSwitch;
    private NotificationShow settings;
    private ConstraintLayout notifTimingBox, licenseDialog, aboutDialog, deleteDialog;
    private TextView notifTimingTextView;
    private String format = "";
    private ConstraintLayout notifPostponeBox;
    private TextView notifPostponeNumberTextView;
    private TextView notifPostponeHourTextView;
    private FirebaseAuth mAuth;
    private DatabaseReference databasereference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // create a notification object
        settings = new NotificationShow(this);
        //Prepare Notification Timing UI
        notifTimingBox = findViewById(R.id.settings_notif_timing_box);
        notifTimingTextView = findViewById(R.id.settings_notif_timing_hour);
        //get last assigned time
        formatNotifTimingTextView();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        //get current user
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            userId = user.getUid();
            if (userId.equals(getString(R.string.admin))) {
                findViewById(R.id.settings_delete_box).setVisibility(View.VISIBLE);
            }
        }

        // Initialize Firebase Database
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://agrismartwatering-default-rtdb.firebaseio.com/");
        //Refer database
        databasereference = db.getReference();



        //Prepare Notification Postpone UI
        notifPostponeBox = findViewById(R.id.settings_notif_remind_box);
        notifPostponeNumberTextView = findViewById(R.id.settings_notif_remind_num_hours);
        notifPostponeHourTextView = findViewById(R.id.settings_notif_remind_text_hours);
        notifPostponeNumberTextView.setText(String.valueOf(settings.getPostponedTime()));
        notifPostponeHourTextView.setText(getResources().getQuantityString(R.plurals.hours, settings.getPostponedTime()));

        //Define bottom Navigation bar
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        floatingActionButton = findViewById(R.id.Fab);
        bottomNavigationView.setBackground(null);
        bottomNavigationView.getMenu().getItem(2).setEnabled(false);
        licenseDialog = findViewById(R.id.settings_license_box);
        aboutDialog = findViewById(R.id.settings_about_box);
        deleteDialog = findViewById(R.id.settings_delete_box);


        licenseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this, R.style.AlertDialogTheme);
                View view = LayoutInflater.from(SettingsActivity.this).inflate(
                        R.layout.settings_licenses_dialog,
                        findViewById(R.id.layout_dialog_container)
                );

                //Make the links clickable
                TextView textView = view.findViewById(R.id.settings_license_text);
                textView.setMovementMethod(LinkMovementMethod.getInstance());

                builder.setView(view);
                final AlertDialog alertDialog = builder.create();

                view.findViewById(R.id.license_close_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                if (alertDialog.getWindow() != null) {
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                }

                alertDialog.show();


            }
        });

        aboutDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this, R.style.AlertDialogTheme);
                View view = LayoutInflater.from(SettingsActivity.this).inflate(
                        R.layout.settings_about_dialog,
                        findViewById(R.id.layout_dialog_container)
                );

                //Make the links clickable
                TextView textView = view.findViewById(R.id.settings_about_text);
                textView.setMovementMethod(LinkMovementMethod.getInstance());

                builder.setView(view);
                final AlertDialog alertDialog = builder.create();

                view.findViewById(R.id.about_close_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                if (alertDialog.getWindow() != null) {
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                }

                alertDialog.show();

            }
        });


        deleteDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this, R.style.AlertDialogTheme);
                View view = LayoutInflater.from(SettingsActivity.this).inflate(
                        R.layout.settings_delete_dialog,
                        findViewById(R.id.layout_dialog_container)
                );
                builder.setView(view);
                final AlertDialog alertDialog = builder.create();

                view.findViewById(R.id.delete_okay_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        alertDialog.dismiss();
                        databasereference.child("TempData").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful()){
                                    Toast.makeText(SettingsActivity.this, "Data has been Successfully Deleted!", Toast.LENGTH_SHORT).show();
                                }


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(SettingsActivity.this, "Deleting Unsuccessful!" + e.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });


                    }
                });

                view.findViewById(R.id.delete_close_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                if (alertDialog.getWindow() != null) {
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                }

                alertDialog.show();



            }
        });

        notifTimingBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog TimeDialog = new TimePickerDialog(SettingsActivity.this, R.style.Timepicker, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        //Initialize hours and minutes
                        hour = hourOfDay;
                        min = minute;

                        settings.setNotifHour(hour);
                        settings.setNotifMinute(min);

                        // alarmTime = String.format(Locale.getDefault(),"%02d : %02d ", hour,min)+format;
                        //notifTimingTextView.setText(alarmTime);
                        formatNotifTimingTextView();

                    }
                }, settings.getNotifHour(), settings.getNotifMinute(), false);


                //display previous selected time
                TimeDialog.updateTime(hour, min);

                //show dialog
                TimeDialog.show();
                //theme overlay
            }
        });

        notifPostponeBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this, R.style.AlertDialogTheme);

                View view = LayoutInflater.from(SettingsActivity.this).inflate(
                        R.layout.settings_postpone_dialog,
                        findViewById(R.id.layout_dialog_container)
                );

                builder.setView(view);
                final AlertDialog alertDialog = builder.create();

                final GreenNumberPicker numberPicker = view.findViewById(R.id.settings_postpone_numberpicker);
                numberPicker.setMinValue(0);
                numberPicker.setMaxValue(23);
                numberPicker.setValue(settings.getPostponedTime());
                numberPicker.setWrapSelectorWheel(false);

                view.findViewById(R.id.postpone_accept_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Store new value
                        settings.setPostponedTime(numberPicker.getValue());
                        // Update visual text:
                        notifPostponeNumberTextView.setText(String.valueOf(settings.getPostponedTime()));
                        notifPostponeHourTextView.setText(getResources().getQuantityString(R.plurals.hours, settings.getPostponedTime()));
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
                Intent main = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(main);


            }
        });

        //set default select
        bottomNavigationView.setSelectedItemId(R.id.Settings);

        //select Menu items
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                switch (id) {

                    case R.id.reminder:
                        startActivity(new Intent(getApplicationContext(),
                                ReminderActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.Sensor:
                        startActivity(new Intent(getApplicationContext(),
                                InfoMonitoringActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.Settings:
                        startActivity(new Intent(getApplicationContext(),
                                SettingsActivity.class));
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

        // Prepare Notification Enabler UI    (after everything because it can modify the upper ones)
        notifEnablerSwitch = findViewById(R.id.settings_notif_enabler_switch);
        notifEnablerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    onNotifEnablerSwitchChangedOn();
                } else {
                    onNotifEnablerSwitchChangedOff();
                }
            }
        });
        notifEnablerSwitch.setChecked(settings.getNotifEnabled());      // Triggers onCheckedChanged call bc after it


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

        alarmTime = String.format(Locale.getDefault(),"%02d : %02d ", resultHour,resultMini)+format;
        notifTimingTextView.setText(alarmTime);

    }

    public void onNotifEnablerSwitchChangedOn() {
        settings.setNotifEnabled(true);

        int orange = getResources().getColor(R.color.white);

        notifTimingTextView.setTextColor(orange);
        notifTimingBox.setClickable(true);

        notifPostponeBox.setClickable(true);
        notifPostponeNumberTextView.setTextColor(orange);
        notifPostponeHourTextView.setTextColor(orange);
    }

    public void onNotifEnablerSwitchChangedOff() {
        settings.setNotifEnabled(false);
        int grey = getResources().getColor(R.color.Grey);
        notifTimingBox.setClickable(false);
        notifTimingTextView.setTextColor(grey);
        notifPostponeBox.setClickable(false);
        notifPostponeNumberTextView.setTextColor(grey);
        notifPostponeHourTextView.setTextColor(grey);
    }


}