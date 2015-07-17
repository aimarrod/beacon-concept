package com.olbalabs.beaconconcept.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.olbalabs.beaconconcept.R;
import com.olbalabs.beaconconcept.domain.SharedPreferenceWrapper;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;

/**
 * Created by obzzidian on 17/07/15.
 */
public class BeaconService extends Service implements BeaconConsumer{

    private static BeaconService instance;

    private BeaconManager beaconManager;
    private Region region;
    private SharedPreferences sp;
    private SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener(){

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Log.d("BeaconService", "Key " + key + " modified");
        }
    };


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Log.d("BeaconService", "OnStartCommand");


        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.unbind(this);
        beaconManager.bind(this);

        sp = SharedPreferenceWrapper.getInstance();

        instance = this;

        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onBeaconServiceConnect() {
        try {
            beaconManager.setRangeNotifier(rn);
            beaconManager.startRangingBeaconsInRegion(getBeacon(sp).getRegion());
            beaconManager.setForegroundBetweenScanPeriod(1000);
            beaconManager.setBackgroundMode(false);
        } catch(RemoteException e){
            e.printStackTrace();
        }
    }

    private RangeNotifier rn = new RangeNotifier() {

        @Override
        public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
            Log.d("BeaconService", "It did range");
            int distance = sp.getInt("RANGE", 10);
            Log.d("BeaconService", "Range " + distance);
            SharedPreferences.Editor ed = sp.edit();
            for(Beacon b: beacons){
                Log.d("BeaconService", "Distance " + b.getDistance());
                ed.putInt("LATEST_DISTANCE", (int) b.getDistance());

                if(b.getDistance() > distance){
                    if(!sp.getBoolean("ALARM_ON", false)) {
                        Log.d("BeaconService", "Distance breached");
                        ed.putBoolean("ALARM_ON", true);
                    }
                } else {
                    if(sp.getBoolean("ALARM_ON", true)){
                        Log.d("BeaconService", "Re entered");
                        ed.putBoolean("ALARM_ON", false);
                    }
                }
                ed.commit();
            }
        }
    };

    public static void start(){
        instance.beaconManager.bind(instance);
    }

    public static void stop(){
        instance.beaconManager.unbind(instance);
    }

    private com.olbalabs.beaconconcept.domain.Beacon getBeacon(SharedPreferences sp){
        return new com.olbalabs.beaconconcept.domain.Beacon(sp.getString("BEACON_UUID", ""), sp.getString("BEACON_MAJOR", ""), sp.getString("BEACON_MINOR", ""));
    }
}
