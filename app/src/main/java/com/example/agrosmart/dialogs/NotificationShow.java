package com.example.agrosmart.dialogs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class NotificationShow {

    private final SharedPreferences prefs;

    private final String notifEnabledKey = "notif_enabled";
    private final boolean notifEnabledDefault = true;

    private final String notifHourKey = "notif_hour";
    private final int notifHourDefault = 18;

    private final String notifMinuteKey = "notif_minute";
    private final int notifMinuteDefault = 00;

    private final String notifRepetIntervalKey = "notif_repetition";
    private final int notifRepetIntervalDefault = 1;

    private final String alarmHourKey = "Alarm_hour";
    private final int alarmHourDefault = 00;

    private final String alarmMinuteKey = "Alarm_minute";
    private final int alarmMinuteDefault = 00;

    private final String waterMinuteKey = "Alarm_minute";
    private final int waterMinuteDefault = 00;


    public int getAlarmHour() {
        return prefs.getInt(alarmHourKey, alarmHourDefault);
    }

    public void setAlarmHour(int newValue) {
        if (newValue >= 0 && newValue <= 23){
            prefs.edit().putInt(alarmHourKey, newValue).apply();
        }
    }

    public int getAlarmMinute() {
        return prefs.getInt(alarmMinuteKey, alarmMinuteDefault);
    }

    public void setAlarmMinute(int newValue){
        if (newValue >= 0 && newValue <= 59){
            prefs.edit().putInt(alarmMinuteKey, newValue).apply();
        }
    }


    public NotificationShow(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    public int getNotifHour() {

        return prefs.getInt(notifHourKey, notifHourDefault);
    }

    public void setNotifHour(int newValue) {
        if (newValue >= 0 && newValue <= 23){
            prefs.edit().putInt(notifHourKey, newValue).apply();
        }
    }

    public int getNotifMinute(){
        return prefs.getInt(notifMinuteKey, notifMinuteDefault);
    }

    public void setNotifMinute(int newValue){
        if (newValue >= 0 && newValue <= 59){
            prefs.edit().putInt(notifMinuteKey, newValue).apply();
        }
    }

    public int getWateringMinute(){
        return prefs.getInt(waterMinuteKey, waterMinuteDefault);
    }

    public void setWateringMinute(int newValue){
        if (newValue >= 0 && newValue <= 59){
            prefs.edit().putInt(waterMinuteKey, newValue).apply();
        }
    }


    public int getNotifRepetInterval(){
        return prefs.getInt(notifRepetIntervalKey, notifRepetIntervalDefault);
    }

    public void setNotifRepetInterval(int newValue){
        if (newValue >= 0 && newValue <= 23){
            prefs.edit().putInt(notifRepetIntervalKey, newValue).apply();
        }
    }

    public boolean getNotifEnabled(){
        return prefs.getBoolean(notifEnabledKey, notifEnabledDefault);
    }

    public void setNotifEnabled(boolean value){
        prefs.edit().putBoolean(notifEnabledKey, value).apply();
    }
}
