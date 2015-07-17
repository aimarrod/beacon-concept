package com.olbalabs.beaconconcept;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.olbalabs.beaconconcept.adapter.SearchBeaconAdapter;
import com.olbalabs.beaconconcept.domain.SharedPreferenceWrapper;
import com.olbalabs.beaconconcept.service.BeaconService;

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

        beaconManager.bind(this);
        adapter = new SearchBeaconAdapter(this);

        SharedPreferences sp = SharedPreferenceWrapper.getInstance();

        if(sp.contains("BEACON_UUID") && sp.contains("BEACON_MAJOR") && sp.contains("BEACON_MINOR")){
            adapter.add(new com.olbalabs.beaconconcept.domain.Beacon(sp.getString("BEACON_UUID",""), sp.getString("BEACON_MAJOR",""), sp.getString("BEACON_MINOR",""), true));
            adapter.notifyDataSetChanged();
        }

        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    beaconManager.stopRangingBeaconsInRegion(region);

                    com.olbalabs.beaconconcept.domain.Beacon b = adapter.getItem(i);

                    SharedPreferences.Editor spe = SharedPreferenceWrapper.getInstance().edit();
                    spe.putString("BEACON_UUID", b.getUuid());
                    spe.putString("BEACON_MAJOR", b.getMayor());
                    spe.putString("BEACON_MINOR", b.getMinor());
                    spe.commit();

                    Intent intent = new Intent(SearchBeaconActivity.this, DistanceActivity.class);
                    startActivity(intent);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        beaconManager.bind(this);
        BeaconService.stop();
    }

    @Override
    protected void onPause(){
        super.onPause();
        beaconManager.unbind(this);
        BeaconService.start();
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
                                if(b.getMayor().equals(beacon.getId2().toString()) && b.getMinor().equals(beacon.getId3().toString())){
                                    found = true;
                                    break;
                                }
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




