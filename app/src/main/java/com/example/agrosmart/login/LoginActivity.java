package com.example.agrosmart.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.agrosmart.MainActivity;
import com.example.agrosmart.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import io.github.muddz.styleabletoast.StyleableToast;

public class LoginActivity extends AppCompatActivity {


    private final static int RC_SIGN_IN = 123; //Signing request code
    private final String TAG = "";
    protected Button login, googleBut;
    protected TextView signup, forgotPass;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private EditText lemail;
    private EditText lpassword;
    private LottieAnimationView lottieLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = findViewById(R.id.Login);
        signup = findViewById(R.id.createAcc);
        googleBut = findViewById(R.id.Google);
        lemail = findViewById(R.id.UserEmail);
        lpassword = findViewById(R.id.UserPassword);
        lottieLoading = findViewById(R.id.lottie);
        forgotPass = findViewById(R.id.forgetPassword);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //currently signed in user
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

        }

        createRequest();

        //reset Password
        forgotPass.setOnClickListener(v -> {

            final EditText forgotPass1 = new EditText(LoginActivity.this);
            final AlertDialog.Builder PasswordResetDialog = new AlertDialog.Builder(v.getContext());
            PasswordResetDialog.setTitle("Did you forget password..?");
            PasswordResetDialog.setMessage("Enter Tour Email To Received Reset Link");
            PasswordResetDialog.setView(forgotPass1);
            forgotPass1.setHint("Email");
            forgotPass1.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

            PasswordResetDialog.setPositiveButton("Yes", (dialog, which) -> {

                String email = forgotPass1.getText().toString().trim();
                mAuth.sendPasswordResetEmail(email).addOnSuccessListener(aVoid -> Toast.makeText(LoginActivity.this, "Reset Link Sent To Your Email", Toast.LENGTH_LONG).show()).addOnFailureListener(e -> Toast.makeText(LoginActivity.this, "Reset Link Can Not be Send !" + e.getMessage(), Toast.LENGTH_LONG).show());
            });
            PasswordResetDialog.setNegativeButton("No", (dialog, which) -> {

            });

            PasswordResetDialog.create().show();

        });


        googleBut.setOnClickListener(view -> signIn());


        signup.setOnClickListener(v -> {
            Intent sign = new Intent(LoginActivity.this, SignUp.class);
            startActivity(sign);
        });


        login.setOnClickListener(v -> {


            String email = lemail.getText().toString();
            String password = lpassword.getText().toString();

            if (TextUtils.isEmpty(email)) {
                lemail.setError("Email Cannot Be Empty");
                lemail.requestFocus();

                return;
            }

            if (TextUtils.isEmpty((password))) {
                lpassword.setError("Password Cannot Be Empty");
                lpassword.requestFocus();
            } else {

                lottieLoading.setVisibility(View.VISIBLE);
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {

                            if (task.isSuccessful()) {
                                FirebaseDatabase.getInstance("https://agrismartwatering-default-rtdb.firebaseio.com/").getReference("User")
                                        .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                GlobalUser.currentUser = snapshot.getValue(User.class);
                                                lottieLoading.setVisibility(View.GONE);

                                                StyleableToast.makeText(LoginActivity.this, "Login Successful", R.style.mytoast).show();
                                                Intent log = new Intent(LoginActivity.this, MainActivity.class);
                                                startActivity(log);
                                                finish();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                            }


                        }).addOnFailureListener(e -> {

                    Toast.makeText(LoginActivity.this, "Login Failed! " + e.getMessage(), Toast.LENGTH_LONG).show();
                    lottieLoading.setVisibility(View.GONE);
                });


            }

        });

    }


    private void createRequest() {


        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        lottieLoading.setVisibility(View.VISIBLE);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                        //FirebaseUser user = mAuth.getCurrentUser();
                        Intent log = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(log);


                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());

                    }
                    lottieLoading.setVisibility(View.GONE);
                });
    }

}