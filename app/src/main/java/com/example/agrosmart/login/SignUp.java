package com.example.agrosmart.login;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.agrosmart.R;
import com.google.firebase.storage.StorageReference;

public class SignUp extends AppCompatActivity {


    private ImageView profilePic;
    private Button signUp;
    private Uri imageUri;
    private TextView addText, loginHere;
    final static int REQUEST_CODE = 215;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

      profilePic = findViewById(R.id.ProPic);
      signUp = findViewById(R.id.SignUpBt);
      addText = findViewById(R.id.addImageText);
      loginHere = findViewById(R.id.loginHereBt);

      // Create a storage reference from our app
        //StorageReference storageRef = storage.getReference();

      loginHere.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent logback = new Intent( SignUp.this, LoginActivity.class);
              startActivity(logback);

          }
      });

      profilePic.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {

              Intent intent = new Intent();
              intent.setType("image/*");
              intent.setAction(Intent.ACTION_GET_CONTENT);
              startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE);



          }
      });



    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode ==REQUEST_CODE && resultCode == RESULT_OK && data != null){

            imageUri = data.getData();
            profilePic.setImageURI(imageUri);
            profilePic.setScaleType(ImageView.ScaleType.CENTER_CROP);
            addText.setVisibility(View.INVISIBLE);


        }
    }
}