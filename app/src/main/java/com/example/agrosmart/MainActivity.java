package com.example.agrosmart;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.agrosmart.dialogs.NotificationShow;
import com.example.agrosmart.dialogs.OnBoarding;
import com.example.agrosmart.login.GlobalUser;
import com.example.agrosmart.login.LoginActivity;
import com.example.agrosmart.login.User;
import com.example.agrosmart.ml.ImageProcessing;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView Name, light_switch_text, pump_switch_text;
    private ImageView profilePic, pumpImage, lightImage;
    private DatabaseReference dbRef;
    private SwitchCompat default_settings;
    private SwitchCompat pump_switch;
    private SwitchCompat light_switch;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        FloatingActionButton floatingActionButton = findViewById(R.id.Fab);
        bottomNavigationView.setBackground(null);
        bottomNavigationView.getMenu().getItem(2).setEnabled(false);
        Name = findViewById(R.id.NameUser);
        profilePic = findViewById(R.id.Photo);
        pump_switch = findViewById(R.id.switch2);
        light_switch = findViewById(R.id.switch1);
        light_switch_text = findViewById(R.id.Light_on_off);
        pump_switch_text = findViewById(R.id.Pump_on_off);
        pumpImage = findViewById(R.id.image_pump);
        lightImage = findViewById(R.id.image_light);
        default_settings = findViewById(R.id.default_on_off);

        // create a pump Switch object
        NotificationShow swicht_enabler = new NotificationShow(this);
        //FirebaseStorage storage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://agrismartwatering-default-rtdb.firebaseio.com/");

        dbRef = db.getReference();

        dbRef.child("led").child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer light_value = (int) snapshot.getValue(Integer.class);
                if(light_value == 0){
                    light_switch.setChecked(false);
                    swicht_enabler.setLightEnabled(false);

                }else if(light_value == 1){
                    light_switch.setChecked(true);
                    swicht_enabler.setLightEnabled(true);

                }else if(light_value == 2){

                    default_settings.setChecked(true);



                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        dbRef.child("pump").child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer pump_value = (int) snapshot.getValue(Integer.class);
                if(pump_value == 0){
                    pump_switch.setChecked(false);
                    swicht_enabler.setPumpEnabled(false);

                }else if(pump_value == 1){
                    pump_switch.setChecked(true);
                    swicht_enabler.setPumpEnabled(true);

                }else if(pump_value == 2) {

                    default_settings.setChecked(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        GoogleSignInOptions gso = new GoogleSignInOptions.
                Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                build();


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);
                googleSignInClient.signOut();
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(MainActivity.this, "User has been sign out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();

            }
        });


        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (googleSignInAccount != null) {

            Name.setText(googleSignInAccount.getDisplayName());
            Uri photo = googleSignInAccount.getPhotoUrl();
            Picasso.with(this).load(photo).fit().centerCrop().placeholder(R.mipmap.ic_launcher_round).into(profilePic);
        } else {
            getUserinfo();
            //Fitch User name
           db.getReference("User")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            GlobalUser.currentUser = snapshot.getValue(User.class);

                            Name.setText(GlobalUser.currentUser.getUsername());

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }


        pump_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dbRef.child("pump").child("status").setValue(1);
                    swicht_enabler.setPumpEnabled(true);
                } else {
                    dbRef.child("pump").child("status").setValue(0);
                    swicht_enabler.setPumpEnabled(false);
                }
            }
        });

        light_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    dbRef.child("led").child("status").setValue(1);
                    swicht_enabler.setLightEnabled(true);
                } else {
                    dbRef.child("led").child("status").setValue(0);
                    swicht_enabler.setLightEnabled(false);
                }

            }
        });

        //pump_state 1 : Pump turn on
        //pump_state 0 : Pump turn off
        //pump_state 2 : default settings[automatic]

        default_settings.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SwitchChangedOff();
                    dbRef.child("led").child("status").setValue(2);
                    dbRef.child("pump").child("status").setValue(2);
                } else {
                    pump_switch.setChecked(swicht_enabler.getPumpEnabled());
                    if(swicht_enabler.getPumpEnabled() == true){
                        dbRef.child("led").child("status").setValue(1);
                        dbRef.child("pump").child("status").setValue(1);
                    }else {
                        dbRef.child("led").child("status").setValue(0);
                        dbRef.child("pump").child("status").setValue(0);
                    }
                    SwitchChangedOn();

                }
            }
        });



        //set default select
        bottomNavigationView.setSelectedItemId(R.id.PlaceHolder);

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



    }

    public void SwitchChangedOn() {

        int white = getResources().getColor(R.color.white);
        pump_switch.setClickable(true);
        light_switch.setClickable(true);
        light_switch_text.setTextColor(white);
        pump_switch_text.setTextColor(white);
    }

    public void SwitchChangedOff() {

        int grey = getResources().getColor(R.color.Grey);
        pump_switch.setClickable(false);
        light_switch.setClickable(false);
        light_switch_text.setTextColor(grey);
        pump_switch_text.setTextColor(grey);
    }




    private void getUserinfo() {


        dbRef.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {

                    if (snapshot.hasChild("image")) {

                        String image = snapshot.child("image").getValue().toString();
                        Picasso.with(MainActivity.this).load(image).fit().placeholder(R.mipmap.ic_launcher_round).into(profilePic);

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}