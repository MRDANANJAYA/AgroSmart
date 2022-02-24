#include <ESP8266WiFi.h>
#include "FirebaseESP8266.h"
#include <DHT.h>


#define FIREBASE_HOST "agrismartwatering-default-rtdb.firebaseio.com" //Without http:// or https:// schemes                        
#define FIREBASE_AUTH "iKc6c5wLcfX3VsXniPamAMi0imiNSGSSBk566TT9"

#define WIFI_SSID "Mrd-wifi"
#define WIFI_PASSWORD "mrd1234dana"

#define DHTPIN D2
#define DHTTYPE DHT11
DHT dht(DHTPIN, DHTTYPE);

int digitalPin = D8;
int digitalPin2 = D7;
const int motorPin = D3;
int LedPin = D5;
int lightSensorPin = A0;
int analogValue = 0;


//Define FirebaseESP8266 data object
FirebaseData firebaseData;
FirebaseJson json;

int pump_status = 0;
int pump_activation = 0;
int led_status = 0;
int led_activation = 0;
int lightStat = 0;
int PumpStat = 0;
unsigned long _time;

void setup() {
  Serial.begin(9600);
  delay(1000);
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to ");
  Serial.print(WIFI_SSID);
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(500);
  }
  Serial.println();
  Serial.print("Connected to ");
  Serial.println(WIFI_SSID);
  Serial.print("IP Address is : ");
  Serial.println(WiFi.localIP());
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  dht.begin();    //Start reading dht sensor

  Serial.begin(9600);
  pinMode(digitalPin, INPUT);
  pinMode(digitalPin2, INPUT);
  pinMode(LedPin, OUTPUT);
  pinMode(motorPin, OUTPUT);

  _time = millis();
}

void loop() {
  if ( (unsigned long) (millis() - _time) > 9800)
  {
    float h = dht.readHumidity();
    float t = dht.readTemperature();
    float m = (digitalRead(digitalPin));
    float mm = (digitalRead(digitalPin2));
    if (isnan(h) || isnan(t)) {
      Serial.println(F("Failed to read from DHT sensor!"));
      return;
    }
    analogValue = analogRead(lightSensorPin);
    //LED ON OFF According to temp

    if (led_status == 0) {
      if (analogValue > 250 && t < 28.8) {
        digitalWrite(LedPin, HIGH);
        Serial.println("Light ON");

      } else {
        digitalWrite(LedPin, LOW);
        Serial.println("Light OFF");
      }
    }
    else {
      digitalWrite(LedPin, led_activation);

    }




    /*

      High == Dry
      Low == Wet
    */
    String firemoist = "";
    String firemoist2 = "";
    String fireStatus = "";
    if (digitalRead(digitalPin) == HIGH) {
      Serial.print("Moisture: ");  Serial.print(m);  firemoist = String("Dry");

    }
    else {
      Serial.print("Moisture: ");  Serial.print(m); firemoist = String("Wet");
    }

    if (digitalRead(digitalPin2) == HIGH) {
      Serial.print("Moisture: ");  Serial.print(mm);  firemoist2 = String("Dry");
    }
    else {
      Serial.print("Moisture: ");  Serial.print(mm); firemoist2 = String("Wet");
    }




    

    Serial.print("  LightIntensity: ");  Serial.print(analogValue);
    String fireLight = String(analogValue) + String("%");
    Serial.print("  Humidity: ");  Serial.print(h);
    String fireHumid = String(h) + String("%");
    Serial.print("%  Temperature: ");  Serial.print(t);  Serial.println("°C ");
    String fireTemp = String(t) + String("°C");






    json.set("temperature", t);
    json.set("humidity", h);
    json.set("moisture", firemoist);
    json.set("moisture2", firemoist2);
    json.set("light", analogValue);
    json.set("timestamp", ServerValue.TIMESTAMP);

    if (Firebase.pushJSON(firebaseData, "/TempData", json)) {
     // Serial.println(firebaseData.dataPath());
      //Serial.println(firebaseData.pushName());
      Serial.println(firebaseData.dataPath() + "/" + firebaseData.pushName());
    }
    else {
      Serial.println(firebaseData.errorReason());
    }

    _time = millis();
  }


  //LED Status send to database

  if (digitalRead(LedPin) == HIGH) {
    lightStat = 1;

    //Serial.print(lightStat);
  } else {
    lightStat = 0;


  }
  Firebase.setInt(firebaseData, "/led/activation", lightStat);


//Pump Status send to database

  if (digitalRead(motorPin) == HIGH) {
    PumpStat = 1;

    //Serial.print(lightStat);
  } else {
    PumpStat = 0;


  }
  Firebase.setInt(firebaseData, "/pump/activation", PumpStat);





  if (Firebase.getInt(firebaseData, "/pump/status")) {
    if (firebaseData.dataType() == "int") {
      pump_status = firebaseData.intData();
    }
  } else {
    Serial.println(firebaseData.errorReason());
  }

  if (Firebase.getInt(firebaseData, "/pump/activation")) {
    if (firebaseData.dataType() == "int") {
      pump_activation = firebaseData.intData();
    }
  } else {
    Serial.println(firebaseData.errorReason());
  }



  if (Firebase.getInt(firebaseData, "/led/status")) {
    if (firebaseData.dataType() == "int") {
      led_status = firebaseData.intData();
    }
  } else {
    Serial.println(firebaseData.errorReason());
  }

  if (Firebase.getInt(firebaseData, "/led/activation")) {
    if (firebaseData.dataType() == "int") {
      led_activation = firebaseData.intData();
    }
  } else {
    Serial.println(firebaseData.errorReason());
  }

  if (pump_status == 0) {
    if (digitalRead(digitalPin) == HIGH || digitalRead(digitalPin2) == HIGH ) {
      digitalWrite(motorPin, HIGH);         // tun on motor
    }
    else if (digitalRead(digitalPin) == LOW && digitalRead(digitalPin2) == LOW) {
      digitalWrite(motorPin, LOW);          // turn off mottor
    }
  }
  else {
    digitalWrite(motorPin, pump_activation);
  }

  /**
    if (led_status == 0) {
      if (analogValue < 50) {
        digitalWrite(LedPin, HIGH);


      } else
        digitalWrite(LedPin, LOW);

    }
    else {
      digitalWrite(LedPin, led_activation);

    }*/


  delay(200);
}
