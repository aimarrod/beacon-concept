package com.olbalabs.beaconconcept;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;

import com.olbalabs.beaconconcept.service.BeaconService;

/**
 * Created by julenzugastibilbao on 17/7/15.
 */
public class DistanceActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance);

        startService(new Intent(this, BeaconService.class));
    }
}
