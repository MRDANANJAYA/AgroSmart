package com.example.agrosmart;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.agrosmart.login.LoginActivity;
import com.example.agrosmart.login.SignUp;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kwabenaberko.openweathermaplib.constant.Languages;
import com.kwabenaberko.openweathermaplib.constant.Units;
import com.kwabenaberko.openweathermaplib.implementation.OpenWeatherMapHelper;
import com.kwabenaberko.openweathermaplib.implementation.callback.CurrentWeatherCallback;
import com.kwabenaberko.openweathermaplib.model.currentweather.CurrentWeather;


public class InfoMonitoring extends AppCompatActivity {

    private DatabaseReference mDatabase;
    BottomNavigationView bottomNavigationView;
    FloatingActionButton floatingActionButton;
    private TextView mHumid;
    private TextView mTemp;
    private TextView mLight;
    private TextView mMoisture;
    private TextView mMoisture2;
    private TextView lStatus;
    private TextView mStatus;
    private TextView mRain;
    private ImageView WIcon;
    private LottieAnimationView lottieAnimationLoading;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_monitoring);



        mHumid = findViewById(R.id.humidity_value);
        mTemp = findViewById(R.id.temperature_value);
        mMoisture= findViewById(R.id.moisture_value);
        mMoisture2 = findViewById(R.id.moisture_value2);
        lStatus = findViewById(R.id.Light_Status);
        mStatus = findViewById(R.id.Motor_Status);
        mLight = findViewById(R.id.light_value);
        mRain = findViewById(R.id.wind_value);
        WIcon = findViewById(R.id.WeatherIcon);
        lottieAnimationLoading = findViewById(R.id.lottieMonitor);

        //Define bottom Navigation bar
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        floatingActionButton = findViewById(R.id.Fab);
        bottomNavigationView.setBackground(null);
        bottomNavigationView.getMenu().getItem(2).setEnabled(false);
        weatherData();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent main= new Intent(InfoMonitoring.this, MainActivity.class);
                startActivity(main);
                finish();

            }
        });

        lottieAnimationLoading.setVisibility(View.VISIBLE);



        //set default select
        bottomNavigationView.setSelectedItemId(R.id.Sensor);

        //select Menu items
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






        sensorData();

    }




    public void weatherData(){
        OpenWeatherMapHelper helper = new OpenWeatherMapHelper(getString(R.string.weather_api_key));
        helper.setUnits(Units.METRIC);
        helper.setLanguage(Languages.ENGLISH);

        helper.getCurrentWeatherByCityName("Gampaha,LK", new CurrentWeatherCallback() {
            @Override
            public void onSuccess(CurrentWeather currentWeather) {

                //Double wind = currentWeather.getWind().getSpeed();
                String weatherDis =  currentWeather.getWeather().get(0).getDescription();
                //mRain.setText(String.format("%.2f", wind));
                mRain.setText(weatherDis);

               /** if(weatherDis.equals("broken clouds")){

                  //  WIcon.setImageURI(R.drawable.clear_sky);
                    Glide.with(InfoMonitoring.this).load(R.drawable.clear_sky).centerCrop().into(WIcon);
                }else if(weatherDis.equals("clear sky")){

                    //  WIcon.setImageURI(R.drawable.clear_sky);
                    Glide.with(InfoMonitoring.this).load(R.drawable.clear_sky).centerCrop().into(WIcon);
                }else if(weatherDis.equals("few clouds")) {

                    //  WIcon.setImageURI(R.drawable.clear_sky);
                    Glide.with(InfoMonitoring.this).load(R.drawable.clear_sky).centerCrop().into(WIcon);
                }else if(weatherDis.equals("moderate rain") ||weatherDis.equals("rain")) {

                    //  WIcon.setImageURI(R.drawable.clear_sky);
                    Glide.with(InfoMonitoring.this).load(R.drawable.clear_sky).centerCrop().into(WIcon);
                }else if(weatherDis.equals("shower rain")) {

                    //  WIcon.setImageURI(R.drawable.clear_sky);
                    Glide.with(InfoMonitoring.this).load(R.drawable.clear_sky).centerCrop().into(WIcon);
                }else if(weatherDis.equals("thunderstorm")) {

                    //  WIcon.setImageURI(R.drawable.clear_sky);
                    Glide.with(InfoMonitoring.this).load(R.drawable.clear_sky).centerCrop().into(WIcon);
                }**/




                switch (weatherDis) {
                    case "broken clouds":
                        Glide.with(InfoMonitoring.this).load(R.drawable.clear_sky).into(WIcon);
                        break;
                    case "few clouds":
                        Glide.with(InfoMonitoring.this).load(R.drawable.few_clouds).into(WIcon);
                        break;
                    case "moderate rain":
                    case "light rain":
                    case "heavy intensity rain":
                        Glide.with(InfoMonitoring.this).load(R.drawable.rain).into(WIcon);
                        break;
                    case "shower rain":
                        Glide.with(InfoMonitoring.this).load(R.drawable.shower_rain).into(WIcon);
                        break;
                    case "thunderstorm":
                        Glide.with(InfoMonitoring.this).load(R.drawable.thunderstorm).into(WIcon);
                        break;
                    case "mist":
                    case "fog":
                        Glide.with(InfoMonitoring.this).load(R.drawable.mist).into(WIcon);
                        break;
                    case "scattered clouds":
                        Glide.with(InfoMonitoring.this).load(R.drawable.scattered_clouds).into(WIcon);
                        break;

                }




                    //Double pressure = currentWeather.getMain().getPressure();
                //mPressure.setText(String.format("%.2f", pressure));
                Log.e(null, "Coordinates: " + currentWeather.getCoord().getLat() + ", "+currentWeather.getCoord().getLon() +"\n"
                        +"Weather Description: " + currentWeather.getWeather().get(0).getDescription() + "\n"
                        +"Temperature: " + currentWeather.getMain().getTempMax()+"\n"
                        +"Wind Speed: " + currentWeather.getWind().getSpeed() + "\n"
                        +"City, Country: " + currentWeather.getName() + ", " + currentWeather.getSys().getCountry()+"\n"



                );
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.v(null, throwable.getMessage());
            }
        });
    }
            public void sensorData() {

         mDatabase = FirebaseDatabase.getInstance("https://agrismartwatering-default-rtdb.firebaseio.com/").getReference();
         mDatabase.child("TempData").orderByKey().limitToLast(1).addChildEventListener(new ChildEventListener() {
             @Override
             public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                 Double Humid = snapshot.child("humidity").getValue(Double.class);
                 mHumid.setText(String.format("%.2f", Humid));


                 Double Temp = snapshot.child("temperature").getValue(Double.class);
                 mTemp.setText(String.format("%.2f", Temp));

                 String Moisture = snapshot.child("moisture").getValue(String.class);
                 mMoisture.setText(Moisture);

                 String Moisture2 = snapshot.child("moisture2").getValue(String.class);
                 mMoisture2.setText(Moisture2);


                 Integer Light = snapshot.child("light").getValue(Integer.class);
                 mLight.setText(Integer.toString(Light));

                 lottieAnimationLoading.setVisibility(View.GONE);

             }



             @Override
             public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

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


         mDatabase = FirebaseDatabase.getInstance("https://agrismartwatering-default-rtdb.firebaseio.com/").getReference("led");
         mDatabase.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {

                 Integer LightS = snapshot.child("activation").getValue(Integer.class);
                 if(LightS == 1){
                     String text = "ON";
                     lStatus.setTextColor(Color.parseColor("#2DBB54"));
                     lStatus.setText(text);
                 }else {
                     String text2 = "OFF";
                     lStatus.setTextColor(Color.parseColor("#EF5350"));
                     lStatus.setText(text2);
                 }


             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });
         mDatabase = FirebaseDatabase.getInstance("https://agrismartwatering-default-rtdb.firebaseio.com/").getReference("pump");
         mDatabase.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 Integer MotorS = snapshot.child("activation").getValue(Integer.class);
                 if(MotorS == 1){
                     String text = "ON";
                     mStatus.setTextColor(Color.parseColor("#2DBB54"));
                     mStatus.setText(text);
                 }else {
                     String text2 = "OFF";
                     mStatus.setTextColor(Color.parseColor("#EF5350"));
                     mStatus.setText(text2);

                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });



             }





}