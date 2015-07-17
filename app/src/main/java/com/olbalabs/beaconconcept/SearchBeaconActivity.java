package com.olbalabs.beaconconcept;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.widget.ListView;

import com.olbalabs.beaconconcept.adapter.SearchBeaconAdapter;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;


/**
 * Created by julenzugastibilbao on 17/7/15.
 */
public class SearchBeaconActivity extends Activity implements BeaconConsumer {

    private static final String SERVICE_UUID = "f7826da6-4fa2-4e98-8024-bC5b71e0893D";

    private ListView listView;
    private SearchBeaconAdapter adapter;
    private BeaconManager beaconManager;
    private Region region = new Region("MyRegion", Identifier.parse(SERVICE_UUID), null, null);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_beacon);
        beaconManager = BeaconManager.getInstanceForApplication(this);

        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.bind(this);
        adapter = new SearchBeaconAdapter(this);

        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onResume(){
        super.onResume();
        beaconManager.setBackgroundMode(false);
    }

    @Override
    protected void onPause(){
        super.onPause();
        beaconManager.setBackgroundMode(true);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {

            @Override
            public void didRangeBeaconsInRegion(final Collection<Beacon> beacons, final Region region) {
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {


                        for(Beacon beacon: beacons) {
                            boolean found = false;
                            for(int i = 0; i < adapter.getCount(); i++){
                                com.olbalabs.beaconconcept.domain.Beacon b = adapter.getItem(i);
                                if(b.getMayor() == beacon.getId2().toString() && b.getMinor() == beacon.getId3().toString()){
                                    found = true;
                                    break;
                                };
                            }
                            if(!found) adapter.add(new com.olbalabs.beaconconcept.domain.Beacon(beacon.getId1().toString(), beacon.getId2().toString(), beacon.getId3().toString()));
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {

        }
    }
}




