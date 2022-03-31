package com.example.agrosmart.ml;

import android.annotation.SuppressLint;
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
    private final static int IMAGE_CODE_DRY = 222;
    private final ArrayList<Uri> ImageList = new ArrayList<Uri>();
    private final ArrayList<String> ImageNameList = new ArrayList<String>();
    private final ArrayList<Uri> ImageListDry = new ArrayList<Uri>();
    private final ArrayList<String> ImageNameListDry = new ArrayList<String>();
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    RecyclerView recyclerView, recyclerViewDry;
    List<ModelClassWatered> modelClassWateredList;
    List<ModelClassDry> modelClassDryList;
    CustomAdapterWatered customAdapterWatered;
    CustomAdapterDry customAdapterDry;
    Uri imageUri, imageUriDry;

    Button backBt, WateredImage, firebaseUploadBt, DryImage;
    String imageName, imageNameDry, user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_model);


        backBt = findViewById(R.id.backBt);
        WateredImage = findViewById(R.id.btnUpload);
        firebaseUploadBt = findViewById(R.id.uploadBt);
        DryImage = findViewById(R.id.btnUpload_Dry);

        recyclerView = findViewById(R.id.recycler_upload);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));


        recyclerViewDry = findViewById(R.id.recycler_upload2);
        recyclerViewDry.setHasFixedSize(true);
        recyclerViewDry.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));


        // Create Array object to get ModelClassWatered Arraylist
        modelClassWateredList = new ArrayList<>();
        UploadImageWatered();

        // Create Array object to get ModelClassWatered Arraylist
        modelClassDryList = new ArrayList<>();
        //Create progressDialog object
        loadingBar = new ProgressDialog(this);
        //Refer Storage
        mStorageRef = FirebaseStorage.getInstance().getReference();
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //currently signed in user
        user = mAuth.getCurrentUser().getUid();


        DryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent UploadIntentDry = new Intent();
                UploadIntentDry.setType("image/*");
                UploadIntentDry.setAction(Intent.ACTION_GET_CONTENT);
                UploadIntentDry.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(UploadIntentDry, IMAGE_CODE_DRY);
            }
        });


        WateredImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent uploadIntent = new Intent();
                uploadIntent.setType("image/*");
                uploadIntent.setAction(Intent.ACTION_GET_CONTENT);
                uploadIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(uploadIntent, IMAGE_CODE);
            }
        });


        backBt.setOnClickListener(new View.OnClickListener() {
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


        if (requestCode == IMAGE_CODE_DRY && resultCode == RESULT_OK) {

            if (data.getClipData() != null) {

                int TotalItemDry = data.getClipData().getItemCount();
                for (int k = 0; k < TotalItemDry; k++) {
                    imageUriDry = data.getClipData().getItemAt(k).getUri();
                    imageNameDry = getFileNameDry(imageUriDry);

                    ImageListDry.add(imageUriDry);
                    ImageNameListDry.add(imageNameDry);

                    ModelClassDry modelClassDry = new ModelClassDry(imageNameDry, imageUriDry);
                    modelClassDryList.add(modelClassDry);

                    customAdapterDry = new CustomAdapterDry(CreateModel.this, modelClassDryList);
                    recyclerViewDry.setAdapter(customAdapterDry);


                }

            } else if (data.getData() != null) {
                Toast.makeText(this, "Add More Images", Toast.LENGTH_SHORT).show();
            }


        } else if (requestCode == IMAGE_CODE && resultCode == RESULT_OK) {

            if (data.getClipData() != null) {

                int TotalItem = data.getClipData().getItemCount();


                for (int i = 0; i < TotalItem; i++) {

                    imageUri = data.getClipData().getItemAt(i).getUri();
                    imageName = getFileNameWatered(imageUri);
                    ImageList.add(imageUri);
                    ImageNameList.add(imageName);

                    ModelClassWatered modelClassWatered = new ModelClassWatered(imageName, imageUri);
                    modelClassWateredList.add(modelClassWatered);

                    customAdapterWatered = new CustomAdapterWatered(CreateModel.this, modelClassWateredList);
                    recyclerView.setAdapter(customAdapterWatered);


                }


            } else if (data.getData() != null) {
                Toast.makeText(this, "Add More Images", Toast.LENGTH_SHORT).show();
            }

        }


    }




    private void UploadImageWatered() {


        firebaseUploadBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    loadingBar.setTitle("Uploading");

                if (ImageList.size() != 0 && ImageListDry.size() != 0) {


                    loadingBar.setCancelable(false);
                    loadingBar.show();


                    for (int uploadsDry = 0; uploadsDry < ImageList.size(); uploadsDry++) {

                        StorageReference mRefDry = mStorageRef.child("imageDry").child(user).child(ImageNameListDry.get(uploadsDry));

                        mRefDry.putFile(ImageList.get(uploadsDry)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                Toast.makeText(CreateModel.this, "We will Notify you once Your model is ready", Toast.LENGTH_LONG).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CreateModel.this, "Upload Fail" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                loadingBar.setTitle("Uploading Dry Plant Images");
                                double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                                loadingBar.setMessage("Please wait.While we are Uploading... " + (int) progressPercent + "%");

                            }
                        });


                    }


                    for (int uploads = 0; uploads < ImageList.size(); uploads++) {
                        // Uri Image = ImageList.get(uploads);


                        StorageReference mRef = mStorageRef.child("imageWatered").child(user).child(ImageNameList.get(uploads));

                        mRef.putFile(ImageList.get(uploads)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                loadingBar.dismiss();
                                Toast.makeText(CreateModel.this, "Upload has been Completed", Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CreateModel.this, "Upload Fail" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                loadingBar.setTitle("Uploading Watered Plant Images");
                                double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                                loadingBar.setMessage("Please wait. While we are Uploading... " + (int) progressPercent + "%");

                            }
                        });
                    }

                } else if (ImageList.size() == 0 || ImageListDry.size() == 0) {
                    Toast.makeText(CreateModel.this, "There is no images to upload", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }


    @SuppressLint("Range")
    public String getFileNameWatered(Uri uri) {
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


    @SuppressLint("Range")
    public String getFileNameDry(Uri uri) {
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