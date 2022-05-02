package com.example.agrosmart.ml;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.agrosmart.InfoMonitoringActivity;
import com.example.agrosmart.MainActivity;
import com.example.agrosmart.NumberPickers.GreenNumberPicker;
import com.example.agrosmart.R;
import com.example.agrosmart.ReminderActivity;
import com.example.agrosmart.SettingsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.checkerframework.checker.units.qual.C;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Calendar;

@SuppressWarnings("ALL")
public class ImageProcessing extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    FloatingActionButton floatingActionButton;
    TextView result, confidenceX, createml, dialog_result, compare_water_time, sensor2, sensor1, compare_dialog_text, water_time_placeholder;
    ImageView imageView;
    Button picture, Okay, Cancel, Compare, start, compare_cancel;
    RelativeLayout watering_layout;
    Uri imageUri;
    int imageSize = 224;
    String Moisture, Moisture2;
    DatabaseReference mDatabaseref;
    int maxPos;
    int WateringMinute = 0;
    String Plurals;
    private Dialog dialog, compDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_processing);

        //Define bottom Navigation bar
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        floatingActionButton = findViewById(R.id.Fab);
        bottomNavigationView.setBackground(null);
        bottomNavigationView.getMenu().getItem(2).setEnabled(false);
        result = findViewById(R.id.result);
        confidenceX = findViewById(R.id.confidence);
        imageView = findViewById(R.id.imageView);
        picture = findViewById(R.id.button);
        createml = findViewById(R.id.addml);


        //Create the Mian Dialog
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_layout);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.round_rectangle));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false); //Optional
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog
        dialog_result = dialog.findViewById(R.id.Dialog_recognized_textView);
        Okay = dialog.findViewById(R.id.btn_okay);
        Cancel = dialog.findViewById(R.id.btn_cancel);
        Compare = dialog.findViewById(R.id.btn_compare);

        //Create the Compare Dialog
        compDialog = new Dialog(this);
        compDialog.setContentView(R.layout.compare_dialog);
        compDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.round_rectangle));
        compDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        compDialog.setCancelable(false);
        compDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog
        sensor2 = compDialog.findViewById(R.id.sensor_value_text_2);
        sensor1 = compDialog.findViewById(R.id.sensor_value_text_1);
        start = compDialog.findViewById(R.id.compare_btn_start);
        compare_cancel = compDialog.findViewById(R.id.compare_btn_cancel);
        watering_layout = compDialog.findViewById(R.id.watering_layout);
        compare_water_time = compDialog.findViewById(R.id.watering_timer);
        compare_dialog_text = compDialog.findViewById(R.id.compare_text);
        water_time_placeholder = compDialog.findViewById(R.id.compare_dialog_watering_time);


        // Initialize Firebase Database
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://agrismartwatering-default-rtdb.firebaseio.com/");

        //Refer database
        mDatabaseref = db.getReference();

        mDatabaseref.child("TempData").orderByKey().limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Moisture = snapshot.child("moisture").getValue(String.class);
                Moisture2 = snapshot.child("moisture2").getValue(String.class);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Moisture = snapshot.child("moisture").getValue(String.class);
                Moisture2 = snapshot.child("moisture2").getValue(String.class);
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


        createml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createml.setText(R.string.create_a_model_long_pressed);
                Intent intent = new Intent(ImageProcessing.this, CreateModel.class);
                startActivity(intent);
            }
        });


        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(ImageProcessing.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 103);
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);


                } else {
                    //Request storage permission if  don't have it.
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 103);

                }
            }
        });


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent main = new Intent(ImageProcessing.this, MainActivity.class);
                startActivity(main);


            }
        });


        //set default select
        bottomNavigationView.setSelectedItemId(R.id.ImagePro);

        //select Menu items with listener
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
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


    }


    @SuppressLint("DefaultLocale")
    public void classifyImage(Bitmap image) {

        try {
            com.example.agrosmart.ml.ModelUnquant model = com.example.agrosmart.ml.ModelUnquant.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
            int pixel = 0;
            for (int i = 0; i < imageSize; i++) {
                for (int j = 0; j < imageSize; j++) {
                    int val = intValues[pixel++]; //RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 255.f));

                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            com.example.agrosmart.ml.ModelUnquant.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidence = outputFeature0.getFloatArray();
            maxPos = 0;
            float maxConfidence = 0;
            for (int i = 0; i < confidence.length; i++) {
                if (confidence[i] > maxConfidence) {
                    maxConfidence = confidence[i];
                    maxPos = i;
                }
            }
            String[] classes = {"No need to be watered", "Need to be watered thoroughly", "Need to be watered Lighty", "Not recognized"};
            result.setText(classes[maxPos]);
            String s = "";
            for (int i = 0; i < classes.length; i++) {
                s += String.format("%s: %.1f%%\n", classes[i], confidence[i] * 100);

            }

            confidenceX.setText(s);//set confidence values

            if (maxPos == 3) {
                Toast.makeText(ImageProcessing.this, "Not recognized", Toast.LENGTH_LONG).show();
            } else {
                displayDialog();
            }

            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void displayDialog() {
        //set result acording to maxPos
        switch (maxPos) {
            case 0:
                dialog_result.setText("No need to be watered");
                break;
            case 1:
                dialog_result.setText("Need to be watered thoroughly");
                break;
            case 2:
                dialog_result.setText("Need to be watered Lighty");
                break;
        }

        dialog.show(); // Showing the dialog here


        Okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pump_state 1 : Pump turn on
                //pump_state 0 : Pump turn off
                //pump_state 2 : default settings[automatic]
                mDatabaseref.child("pump").child("status").setValue(2);// default settings

                dialog.dismiss();


            }
        });
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(ImageProcessing.this, "Cancel", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        Compare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

                //set sensor values
                sensor1.setText(Moisture);
                sensor2.setText(Moisture2);

                compDialog.show();
                Plurals = (getResources().getQuantityString(R.plurals.minutes, WateringMinute));
                compare_water_time.setText(String.valueOf(WateringMinute) + " " + Plurals);


                compare_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        compDialog.dismiss();
                        dialog.show();

                    }
                });

                if (Moisture.equals("Wet") && Moisture2.equals("Wet")) {

                    int grey = getResources().getColor(R.color.Grey);
                    watering_layout.setClickable(false);
                    compare_water_time.setTextColor(grey);
                    start.setTextColor(grey);
                    water_time_placeholder.setTextColor(grey);
                    compDialog.findViewById(R.id.compare_dialog_watering_time);
                    compare_dialog_text.setText(R.string.dialog_compare_text_wet);
                    return;

                } else if (Moisture.equals("Dry") && Moisture2.equals("Wet") || Moisture.equals("Wet") && Moisture2.equals("Dry")) {

                    watering_layout.setClickable(true);
                    compare_water_time.setTextColor(0xFF669900);
                    start.setTextColor(getColor(R.color.Green));
                    water_time_placeholder.setTextColor(0xFF669900);
                    compare_dialog_text.setText(R.string.dialog_compare_text_oneDry);


                } else if (Moisture.equals("Dry") && Moisture2.equals("Dry")) {

                    watering_layout.setClickable(true);
                    compare_water_time.setTextColor(0xFF669900);
                    water_time_placeholder.setTextColor(0xFF669900);
                    compare_dialog_text.setText(R.string.dialog_compare_text_allDry);

                }

                watering_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //call Timepicker Dialog
                        timePickDialog();

                    }
                });

                start.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar startTime = Calendar.getInstance();
                        long alarmStartTime = startTime.getTimeInMillis();

                        long S = WateringMinute * 60 * 1000;
                        mDatabaseref.child("pump").child("status").setValue(1);
                        Toast.makeText(ImageProcessing.this, "Time set " + (WateringMinute) + " min", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ImageProcessing.this, WaterTimerReceiver.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(ImageProcessing.this, 1, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
                        AlarmManager am = (AlarmManager) ImageProcessing.this.getSystemService(ALARM_SERVICE);
                        am.set(AlarmManager.RTC_WAKEUP, alarmStartTime + S, pendingIntent); //trigger at specified time

                        compDialog.dismiss();

                    }
                });
            }
        });

    }

    private void timePickDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ImageProcessing.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(ImageProcessing.this).inflate(
                R.layout.reminder_watering_time_dialog,
                compDialog.findViewById(R.id.layout_compare_dialog_container), false);

        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        final GreenNumberPicker numberPicker = view.findViewById(R.id.reminder_watering_numberpicker);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(60);
        numberPicker.setValue(WateringMinute);
        numberPicker.setWrapSelectorWheel(false);


        view.findViewById(R.id.Watering_accept_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WateringMinute = numberPicker.getValue();

                // Update visual text:
                compare_water_time.setText(String.valueOf(WateringMinute) + " " + Plurals);
                alertDialog.dismiss();
            }
        });
        if (alertDialog.getWindow() != null) {

            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        alertDialog.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            imageUri = data.getData();

            try {
                Bitmap image = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                int dimensions = Math.min(image.getWidth(), image.getHeight());
                image = ThumbnailUtils.extractThumbnail(image, dimensions, dimensions);
                //imageView.setImageBitmap(image);

                Glide.with(this).load(imageUri).centerCrop().into(imageView);
                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                classifyImage(image);

            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }


}