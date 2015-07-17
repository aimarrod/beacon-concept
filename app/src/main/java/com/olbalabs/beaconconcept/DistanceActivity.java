package com.olbalabs.beaconconcept;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.olbalabs.beaconconcept.domain.SharedPreferenceWrapper;
import com.olbalabs.beaconconcept.service.BeaconService;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;


/**
 * Created by julenzugastibilbao on 17/7/15.
 */
public class DistanceActivity extends Activity {

    private DiscreteSeekBar dsb;
    private SharedPreferences sp;
    private LinearLayout ll;
    private TextView tv;
    private TextView pulsera;
    private TextView curretn_uuid;

    MediaPlayer mp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = SharedPreferenceWrapper.getInstance();
        setContentView(R.layout.activity_distance);

        BeaconService.start();

        ll = (LinearLayout) findViewById(R.id.alarm_layout);
        tv = (TextView) findViewById(R.id.current_distance);

        pulsera = (TextView) findViewById(R.id.pulsera);
        curretn_uuid = (TextView) findViewById(R.id.identificator);
        mp = MediaPlayer.create(DistanceActivity.this, R.raw.alarm);

        dsb = (DiscreteSeekBar) findViewById(R.id.bar);
        dsb.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar discreteSeekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar discreteSeekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar discreteSeekBar) {
                SharedPreferences.Editor ed = sp.edit();
                ed.putInt("RANGE", discreteSeekBar.getProgress());
                ed.commit();

            }
        });

        sp.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals("ALARM_ON")) {
                    Log.d("Alarm", sharedPreferences.getBoolean("ALARM_ON", false) + "");
                    if (sharedPreferences.getBoolean("ALARM_ON", false)) {
                        ll.setBackgroundColor(getResources().getColor(R.color.alert));
                        tv.setTextColor(getResources().getColor(android.R.color.white));
                        pulsera.setTextColor(getResources().getColor(android.R.color.white));
                        curretn_uuid.setTextColor(getResources().getColor(android.R.color.white));
                        mp.setLooping(true);
                        mp.start();
                    } else {
                        ll.setBackgroundColor(getResources().getColor(R.color.normal));
                        tv.setTextColor(getResources().getColor(android.R.color.black));
                        pulsera.setTextColor(getResources().getColor(android.R.color.black));
                        curretn_uuid.setTextColor(getResources().getColor(android.R.color.black));
                        mp.stop();
                    }
                } else if(key.equals("LATEST_DISTANCE")) {
                    tv.setText(sharedPreferences.getInt("LATEST_DISTANCE", 1)+ " m");
                }
            }
        });

        if(sp.contains("RANGE")){
            dsb.setProgress(sp.getInt("RANGE", 2));
        } else {
            SharedPreferences.Editor ed = sp.edit();
            ed.putInt("RANGE", 2);
            ed.commit();
        }

        curretn_uuid.setText(getString(R.string.distance_current_uuid) + " " + sp.getString("BEACON_MAJOR", "") + "-" + sp.getString("BEACON_MINOR", ""));

    }
}
