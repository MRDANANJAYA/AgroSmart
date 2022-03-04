package com.example.agrosmart.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.agrosmart.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;


public class SignUp extends AppCompatActivity {


    final static int REQUEST_CODE = 215;
    StorageReference storageRef;
    FirebaseStorage storage;
    private ImageView profilePic;
    private Button signUp;
    private Uri imageUri;
    private TextView addText, loginHere;
    private EditText userName, emailAdd, passwordSn, cmfPassword;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private String myUri = "";
    private StorageTask uploadTask;
    private FirebaseDatabase db;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        profilePic = findViewById(R.id.ProPic);
        signUp = findViewById(R.id.SignUpBt);
        addText = findViewById(R.id.addImageText);
        loginHere = findViewById(R.id.loginHereBt);
        userName = findViewById(R.id.UserNameSn);
        emailAdd = findViewById(R.id.UserEmailSn);
        passwordSn = findViewById(R.id.UserPasswordSn);
        cmfPassword = findViewById(R.id.ConfirmPassword);

        //set progressDialog
        loadingBar = new ProgressDialog(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize Firebase Database
        db = FirebaseDatabase.getInstance("https://agrismartwatering-default-rtdb.firebaseio.com/");

        storage = FirebaseStorage.getInstance();

        dbRef = db.getReference("User");
        // Create a storage reference from our app
        storageRef = storage.getReference();

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE);


            }
        });


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String email = emailAdd.getText().toString().trim();
                String password = passwordSn.getText().toString();
                String ConfirmPassword = cmfPassword.getText().toString();
                String Username = userName.getText().toString();
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";


                if (TextUtils.isEmpty(Username)) {

                    userName.setError("Username Cannot Be Empty");
                    userName.requestFocus();
                    return;

                }

                if (TextUtils.isEmpty(email)) {
                    emailAdd.setError("Email Cannot Be Empty");
                    emailAdd.requestFocus();
                    return;
                }

                if (!email.matches(emailPattern)) {
                    emailAdd.setError("Invalid email address");
                    emailAdd.requestFocus();
                    return;
                }


                if (TextUtils.isEmpty(password)) {
                    passwordSn.setError("Password Cannot Be Empty");
                    passwordSn.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(ConfirmPassword)) {
                    cmfPassword.setError("Confirm Password Cannot Be Empty");
                    cmfPassword.requestFocus();
                    return;
                }

                if (!password.equals(ConfirmPassword)) {
                    cmfPassword.setError("Password Didn't Match");
                    cmfPassword.requestFocus();
                    return;
                }
                if (imageUri == null) {

                    Toast.makeText(SignUp.this, "Image Not Selected", Toast.LENGTH_LONG).show();
                } else {


                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {


                            loadingBar.setTitle("Registering");
                            loadingBar.show();

                            if (task.isSuccessful()) {

                                //set data into User.class

                                User user = new User();
                                user.setEmail(emailAdd.getText().toString());
                                user.setUsername(userName.getText().toString());
                                user.setPassword(passwordSn.getText().toString());


                                dbRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {


                                                final StorageReference fileRef = storageRef
                                                        .child(mAuth.getCurrentUser().getUid() + "jpg");

                                                uploadTask = fileRef.putFile(imageUri)
                                                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                            @Override
                                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                loadingBar.dismiss();
                                                                Toast.makeText(SignUp.this, "User Registration has been Successful!", Toast.LENGTH_SHORT).show();
                                                                Intent log = new Intent(SignUp.this, LoginActivity.class);
                                                                startActivity(log);


                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {

                                                                Toast.makeText(SignUp.this, "Upload image Unsuccessful!" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                loadingBar.dismiss();
                                                            }


                                                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                                            @Override
                                                            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                                                                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                    @Override
                                                                    public void onSuccess(Uri uri) {
                                                                        final String downloadUrl = uri.toString();
                                                                        myUri = downloadUrl;

                                                                        HashMap<String, Object> userMap = new HashMap<>();
                                                                        userMap.put("image", myUri);


                                                                        dbRef.child(mAuth.getCurrentUser().getUid()).updateChildren(userMap);
                                                                    }
                                                                });

                                                                double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                                                                loadingBar.setMessage("Please wait. while we are registering... " + (int) progressPercent + "%");

                                                            }


                                                        });


                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        Toast.makeText(SignUp.this, "Registration Unsuccessful!" + e.getMessage(), Toast.LENGTH_SHORT).show();


                                    }
                                });

                            } else {

                                Toast.makeText(SignUp.this, "Something Wrong!", Toast.LENGTH_SHORT).show();
                            }


                        }
                    });


                }

            }
        });
        loginHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logback = new Intent(SignUp.this, LoginActivity.class);
                startActivity(logback);

            }
        });


    }


    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {

            imageUri = data.getData();
           // profilePic.setImageURI(imageUri);
           // profilePic.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Glide.with(this).load(imageUri).circleCrop().into(profilePic);
            addText.setVisibility(View.INVISIBLE);


        }
    }


}