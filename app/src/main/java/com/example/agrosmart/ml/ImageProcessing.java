package com.example.agrosmart.ml;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.agrosmart.InfoMonitoring;
import com.example.agrosmart.MainActivity;
import com.example.agrosmart.R;
import com.example.agrosmart.Reminder;
import com.example.agrosmart.Settings;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@SuppressWarnings("ALL")
public class ImageProcessing extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    FloatingActionButton floatingActionButton;
    TextView result, confidenceX, createml;
    ImageView imageView;
    Button picture, Okay, Cancel;
    Uri imageUri;
    int imageSize = 224;
    String Moisture, Moisture2;
    DatabaseReference mDatabaseref;
    private Dialog dialog;

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


        //Create the Dialog
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_layout);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.round_rectangle));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false); //Optional
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog
        Okay = dialog.findViewById(R.id.btn_okay);
        Cancel = dialog.findViewById(R.id.btn_cancel);


        // Initialize Firebase Database
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://agrismartwatering-default-rtdb.firebaseio.com/");

        //Refer database
        mDatabaseref = db.getReference();

        mDatabaseref.child("TempData").orderByKey().limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Moisture = snapshot.child("moisture").getValue(String.class);
                Moisture2 = snapshot.child("moisture2").getValue(String.class);
                Toast.makeText(ImageProcessing.this, Moisture+" "+Moisture2, Toast.LENGTH_SHORT).show();
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
            int maxPos = 0;
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

            confidenceX.setText(s);

            if (maxPos == 0) {
                Toast.makeText(ImageProcessing.this, "No need to be watered", Toast.LENGTH_LONG).show();
                mDatabaseref.child("pump").child("level").setValue(1);
                mDatabaseref.child("pump").child("status").setValue(3);
            } else if (maxPos == 1) {
                //Toast.makeText(ImageProcessing.this, "Need to be watered thoroughly", Toast.LENGTH_LONG).show();
                displayDialog();
            } else if (maxPos == 2) {
                Toast.makeText(ImageProcessing.this, "Need to be watered Lighty", Toast.LENGTH_LONG).show();
                mDatabaseref.child("pump").child("level").setValue(2);
            } else if (maxPos == 3) {
                Toast.makeText(ImageProcessing.this, "Not recognized", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(ImageProcessing.this, "invalid", Toast.LENGTH_LONG).show();
            }


            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displayDialog() {
        dialog.show(); // Showing the dialog here
        Okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (Moisture.equals("Dry") && Moisture2.equals("Wet") || Moisture.equals("Wet") && Moisture2.equals("Dry")) {

                    Toast.makeText(ImageProcessing.this, "Its dry wet", Toast.LENGTH_SHORT).show();


                }
                if (Moisture.equals("Wet") && Moisture2.equals("Wet")) {

                    Toast.makeText(ImageProcessing.this, "Its wet wet", Toast.LENGTH_SHORT).show();


                }
                if (Moisture.equals("Dry") && Moisture2.equals("Dry")) {

                    Toast.makeText(ImageProcessing.this, "Its dry dry", Toast.LENGTH_SHORT).show();


                }


            }
        });
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabaseref.child("pump").child("level").setValue(3);
                mDatabaseref.child("pump").child("status").setValue(1);
                Toast.makeText(ImageProcessing.this, "Cancel", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

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