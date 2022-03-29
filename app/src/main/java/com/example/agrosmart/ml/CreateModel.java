package com.example.agrosmart.ml;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agrosmart.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;


public class CreateModel extends AppCompatActivity {

    private final static int IMAGE_CODE = 120;
    Button backbt, multiImage, firebaseUploadBt;
    RecyclerView recyclerView;
    List<ModelClass> modelClassList;
    CustomAdapter customAdapter;
    Uri imageUri;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;
    String imagename, user;
    private StorageReference mStorageRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_model);


        backbt = findViewById(R.id.backBt);
        multiImage = findViewById(R.id.btnUpload);
        recyclerView = findViewById(R.id.recycler_upload);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        firebaseUploadBt = findViewById(R.id.uploadBt);

        //set progressDialog
        loadingBar = new ProgressDialog(this);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //currently signed in user
        user = mAuth.getCurrentUser().getUid();
        Toast.makeText(this, user, Toast.LENGTH_SHORT).show();

        modelClassList = new ArrayList<>();

        multiImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent uploadIntent = new Intent();
                uploadIntent.setType("image/*");
                uploadIntent.setAction(Intent.ACTION_GET_CONTENT);
                uploadIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(uploadIntent, IMAGE_CODE);
            }
        });


        backbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentBack = new Intent(CreateModel.this, ImageProcessing.class);
                startActivity(intentBack);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == IMAGE_CODE && resultCode == RESULT_OK) {

            if (data.getClipData() != null) {

                int totalitem = data.getClipData().getItemCount();

                for (int i = 0; i < totalitem; i++) {

                    imageUri = data.getClipData().getItemAt(i).getUri();
                    imagename = getFileName(imageUri);

                    ModelClass modelClass = new ModelClass(imagename,imageUri);
                    modelClassList.add(modelClass);

                    customAdapter = new CustomAdapter(CreateModel.this, modelClassList);
                    recyclerView.setAdapter(customAdapter);

                    UploadImage();

                }


            } else if (data.getData() != null) {
                Toast.makeText(this, "single", Toast.LENGTH_SHORT).show();
            }

        }

    }

    private void UploadImage() {

        firebaseUploadBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadingBar.setTitle("Registering");
                loadingBar.show();

                StorageReference mRef = mStorageRef.child("image").child(imagename);

                mRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        loadingBar.dismiss();
                        Toast.makeText(CreateModel.this, "Upload has been Done", Toast.LENGTH_LONG).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateModel.this, "Fail" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                        double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        loadingBar.setMessage("Please wait. while we are Uploading... " + (int) progressPercent + "%");

                    }
                });
            }
        });
    }


    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}