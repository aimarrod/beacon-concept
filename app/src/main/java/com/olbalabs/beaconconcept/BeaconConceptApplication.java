package com.olbalabs.beaconconcept;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.olbalabs.beaconconcept.domain.SharedPreferenceWrapper;
import com.olbalabs.beaconconcept.service.BeaconService;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;

/**
 * Created by julenzugastibilbao on 17/7/15.
 */
public class BeaconConceptApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.setForegroundBetweenScanPeriod(1000);

        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));

        SharedPreferenceWrapper.initialize(this);
        startService(new Intent(this, BeaconService.class));
    }
}
