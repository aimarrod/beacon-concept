package com.olbalabs.beaconconcept.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.RemoteException;

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
        SharedPreferences sp = this.getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE);
        com.olbalabs.beaconconcept.domain.Beacon current = getBeacon(sp);

        beaconManager.setMonitorNotifier(new MonitorNotifier() {

            @Override
            public void didEnterRegion(Region region) {

            }

            @Override
            public void didExitRegion(Region region) {

            }

            @Override
            public void didDetermineStateForRegion(int i, Region region) {

            }
        });

        //try {
            //beaconManager.startRangingBeaconsInRegion(region);
        //} catch (RemoteException e) {

       // }
    }




    private com.olbalabs.beaconconcept.domain.Beacon getBeacon(SharedPreferences sp){
        return new com.olbalabs.beaconconcept.domain.Beacon(sp.getString("BEACON_UUID", ""), sp.getString("BEACON_MAJOR", ""), sp.getString("BEACON_MINOR", ""));
    }
}
