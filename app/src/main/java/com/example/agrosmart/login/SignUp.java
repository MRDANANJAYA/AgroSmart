package com.example.agrosmart.login;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.agrosmart.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.HashMap;
import java.util.Objects;


public class SignUp extends AppCompatActivity {


    final static int REQUEST_CODE = 215;
    private static final String TAG = "";
    private static final int PICK_FROM_GALLERY = 103;
    StorageReference storageRef;
    FirebaseStorage storage;
    StorageTask uploadTask;
    private ImageView profilePic;
    private Uri imageUri;
    private TextView addText;
    private EditText userName, emailAdd, passwordSn, cmfPassword;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private String myUri = "";
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        profilePic = findViewById(R.id.ProPic);
        Button signUp = findViewById(R.id.SignUpBt);
        addText = findViewById(R.id.addImageText);
        TextView loginHere = findViewById(R.id.loginHereBt);
        userName = findViewById(R.id.UserNameSn);
        emailAdd = findViewById(R.id.UserEmailSn);
        passwordSn = findViewById(R.id.UserPasswordSn);
        cmfPassword = findViewById(R.id.ConfirmPassword);

        //set progressDialog
        loadingBar = new ProgressDialog(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize Firebase Database
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://agrismartwatering-default-rtdb.firebaseio.com/");

        storage = FirebaseStorage.getInstance();

        dbRef = db.getReference("User");
        // Create a storage reference from the app
        storageRef = storage.getReference();

        profilePic.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(SignUp.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE);

            } else {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PICK_FROM_GALLERY);
            }

        });


        signUp.setOnClickListener(v -> {

            // validate values
            String email = emailAdd.getText().toString().trim();
            String password = passwordSn.getText().toString();
            String ConfirmPassword = cmfPassword.getText().toString();
            String Username = userName.getText().toString();
            String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"; //email pattern validation


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

            if(password.length() < 6){
                passwordSn.setError("Password should be at least 6 characters");
                passwordSn.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(ConfirmPassword)) {
                cmfPassword.setError("Confirm Password Cannot Be Empty");
                cmfPassword.requestFocus();
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


                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener((Task<AuthResult> task) -> {


                    if (task.isSuccessful()) {

                        //set data into User.class
                        loadingBar.setTitle("Registering");
                        loadingBar.show();
                        loadingBar.setCancelable(false);

                        User user = new User();
                        user.setEmail(emailAdd.getText().toString());
                        user.setUsername(userName.getText().toString());
                        user.setPassword(passwordSn.getText().toString());


                        dbRef.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(user)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task1) {


                                        final StorageReference fileRef = storageRef
                                                .child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid() + ".jpg");

                                        uploadTask = fileRef.putFile(imageUri)
                                                .addOnSuccessListener(taskSnapshot -> {
                                                    loadingBar.dismiss();
                                                    Toast.makeText(SignUp.this, "User Registration has been Successful!", Toast.LENGTH_SHORT).show();
                                                    Intent log = new Intent(SignUp.this, LoginActivity.class);
                                                    SignUp.this.startActivity(log);


                                                }).addOnFailureListener(e -> {

                                                    Toast.makeText(SignUp.this, "Upload image Unsuccessful!" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();
                                                }).addOnProgressListener(snapshot -> {

                                                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                                        myUri = uri.toString();

                                                        HashMap<String, Object> userMap = new HashMap<>();
                                                        userMap.put("image", myUri);


                                                        dbRef.child(mAuth.getCurrentUser().getUid()).updateChildren(userMap);
                                                    });

                                                    double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                                                    loadingBar.setMessage("Please wait. while we are registering... " + (int) progressPercent + "%");

                                                });


                                    }
                                }).addOnFailureListener((Exception e) -> {
                            Toast.makeText(SignUp.this, "Registration Unsuccessful!" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });

                    } else {
                        Log.w(TAG, "signInWithCustomToken:failure", task.getException());
                        Toast.makeText(SignUp.this, "Something Wrong!", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }


                });


            }

        });
        loginHere.setOnClickListener(v -> {
            Intent logback = new Intent(SignUp.this, LoginActivity.class);
            startActivity(logback);

        });


    }

    // called when the user permition accepts
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PICK_FROM_GALLERY) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE);
            } else {
                //Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {

            imageUri = data.getData();
            Glide.with(this).load(imageUri).circleCrop().into(profilePic);
            addText.setVisibility(View.INVISIBLE);


        }
    }


}