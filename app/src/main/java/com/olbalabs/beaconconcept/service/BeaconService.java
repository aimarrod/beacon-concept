package com.olbalabs.beaconconcept.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.olbalabs.beaconconcept.R;

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

    private BeaconManager beaconManager;
    private Region region;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Log.d("BeaconService", "OnStartCommand");

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.bind(this);

        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setMonitorNotifier(new MonitorNotifier() {

            @Override
            public void didEnterRegion(Region region) {
                try {
                    Log.d("BeaconService", "Entered region");
                    beaconManager.startRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void didExitRegion(Region region) {
                try {
                    beaconManager.stopRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void didDetermineStateForRegion(int i, Region region) {

            }
        });
        final SharedPreferences sp = this.getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE);

        beaconManager.setRangeNotifier(new RangeNotifier() {

            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                Log.d("BeaconService", "It did range");
                int distance = sp.getInt("RANGE", 10);
                SharedPreferences.Editor ed = sp.edit();
                for(Beacon b: beacons){
                    Log.d("BeaconService", "Distance " + b.getDistance());

                    if(b.getDistance() > distance){
                        if(sp.getBoolean("ALARM_ON", false)) {
                            Log.d("BeaconService", "Distance breached");
                            ed.putBoolean("ALARM_ON", true);
                        }
                    } else {
                        if(sp.getBoolean("ALARM_ON", true)){
                            Log.d("BeaconService", "Re entered");
                            ed.putBoolean("ALARM_ON", false);
                        }
                    }
                }
                ed.commit();
            }
        });

        sp.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener(){

            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                Log.d("BeaconService", "Key " + key + " modified");
                if (key == "BEACON_UUID" || key == "BEACON_MAJOR" || key == "BEACON_MINOR") {
                    restart(sharedPreferences);
                }
            }
        });
        restart(sp);
    }


    private void restart(SharedPreferences sp){
        try {
            if (region != null) {
                beaconManager.stopMonitoringBeaconsInRegion(region);
                beaconManager.stopMonitoringBeaconsInRegion(region);
            }

            if(sp.contains("BEACON_UUID") && sp.contains("BEACON_MAJOR") && sp.contains("BEACON_MINOR")){
                Log.d("BeaconService", "Starting monitor");
                beaconManager.startMonitoringBeaconsInRegion(getBeacon(sp).getRegion());
                beaconManager.setForegroundBetweenScanPeriod(1000);
                beaconManager.setBackgroundMode(false);
                beaconManager.setBackgroundBetweenScanPeriod(1000);
            }
        } catch (RemoteException e){
            e.printStackTrace();
        }
    }


    private com.olbalabs.beaconconcept.domain.Beacon getBeacon(SharedPreferences sp){
        return new com.olbalabs.beaconconcept.domain.Beacon(sp.getString("BEACON_UUID", ""), sp.getString("BEACON_MAJOR", ""), sp.getString("BEACON_MINOR", ""));
    }
}
