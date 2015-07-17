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

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.bind(this);

        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onBeaconServiceConnect() {
        final SharedPreferences sp = this.getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE);

        beaconManager.setMonitorNotifier(new MonitorNotifier() {

            @Override
            public void didEnterRegion(Region region) {
                try {
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
                }            }

            @Override
            public void didDetermineStateForRegion(int i, Region region) {

            }
        });

        beaconManager.setRangeNotifier(new RangeNotifier() {

            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                int distance = sp.getInt("RANGE", 10);
                SharedPreferences.Editor ed = sp.edit();
                for(Beacon b: beacons){
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
                beaconManager.startMonitoringBeaconsInRegion(getBeacon(sp).getRegion());
            }
        } catch (RemoteException e){
            e.printStackTrace();
        }
    }


    private com.olbalabs.beaconconcept.domain.Beacon getBeacon(SharedPreferences sp){
        return new com.olbalabs.beaconconcept.domain.Beacon(sp.getString("BEACON_UUID", ""), sp.getString("BEACON_MAJOR", ""), sp.getString("BEACON_MINOR", ""));
    }
}
