package in.motorindiaonline.motorindia.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

import in.motorindiaonline.motorindia.R;
import in.motorindiaonline.motorindia.Utilities.MotorIndiaPreferences;

public class MySettings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = "Settings";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(MotorIndiaPreferences.KEY_PREF_NOTIFICATION)){
            Log.i(TAG,"Notification settings have been changed to => "+sharedPreferences.getBoolean(key,Boolean.TRUE));
            SharedPreferences.Editor editor = getSharedPreferences(MotorIndiaPreferences.GENERAL_DATA, MODE_PRIVATE).edit();
            editor.putBoolean(MotorIndiaPreferences.SEND_NOTIFICATIONS, sharedPreferences.getBoolean(key,Boolean.TRUE));
            editor.apply();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
