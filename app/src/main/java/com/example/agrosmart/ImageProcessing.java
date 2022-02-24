package com.example.agrosmart;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agrosmart.ml.ModelUnquant;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ImageProcessing extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    FloatingActionButton floatingActionButton;
    TextView result, confidenceX;
    ImageView imageView;
    Button picture;
    Uri imageUri;
    int imageSize = 224;



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




        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(ImageProcessing.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                  // Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    Intent intent=new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent,"Select Picture"),1);



                } else {
                    //Request storage permission if  don't have it.
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 103);
                }
            }
        });






        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent main= new Intent(ImageProcessing.this, MainActivity.class);
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


    public void classifyImage(Bitmap image){

        try {
            ModelUnquant model = ModelUnquant.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

           int [] intValues =  new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(),0 ,0, image.getWidth(), image.getHeight());
            int pixel = 0;
            for(int  i = 0; i < imageSize; i++ ) {
                for(int j = 0; j < imageSize; j++){
                    int val = intValues[pixel++]; //RGB
                    byteBuffer.putFloat(((val >> 16)& 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat(((val >> 8)& 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat(((val >> 0 )& 0xFF) * (1.f / 255.f));

                }

            }

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            ModelUnquant.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidence = outputFeature0.getFloatArray();
            int maxPos =0;
            float maxConfidence = 0;
            for(int i = 0; i < confidence.length; i++){
                if(confidence[i] > maxConfidence){
                    maxConfidence = confidence[i];
                    maxPos = i;
                }
            }
            String[] classes = {"No need to be watered","Need to be watered thoroughly","Need to be watered Lighty","Not recognized"};
            result.setText(classes[maxPos]);
            String s = "";
            for(int i = 0; i <classes.length; i++){
                s += String.format("%s: %.1f%%\n", classes[i], confidence[i] *100);

            }

            confidenceX.setText(s);

            if(maxPos == 0){
                Toast.makeText(ImageProcessing.this, "No need to be watered", Toast.LENGTH_LONG).show();
            }else if(maxPos == 1){
                Toast.makeText(ImageProcessing.this, "Need to be watered thoroughly", Toast.LENGTH_LONG).show();
            }else  if (maxPos == 2){
                Toast.makeText(ImageProcessing.this, "Need to be watered Lighty", Toast.LENGTH_LONG).show();
            }else if (maxPos == 3){
                Toast.makeText(ImageProcessing.this, "Not recognized", Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(ImageProcessing.this, "Not vaild", Toast.LENGTH_LONG).show();
            }



            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


   @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            imageUri = data.getData();

            try {
                Bitmap image = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                int dimensions = Math.min(image.getWidth(), image.getHeight());
                image = ThumbnailUtils.extractThumbnail(image, dimensions, dimensions);
                imageView.setImageBitmap(image);


                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                classifyImage(image);

            } catch (IOException e) {
                e.printStackTrace();
            }

           // Bitmap image = (Bitmap) data.getExtras().get("data");


        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}