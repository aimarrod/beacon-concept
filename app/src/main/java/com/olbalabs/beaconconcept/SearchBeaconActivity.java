package com.olbalabs.beaconconcept;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.olbalabs.beaconconcept.adapter.SearchBeaconAdapter;


/**
 * Created by julenzugastibilbao on 17/7/15.
 */
public class SearchBeaconActivity extends Activity{

    ListView listView;
    SearchBeaconAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_beacon);

        listView = (ListView) findViewById(R.id.list_view);

        //CHECK BEACON
        //WHEN SEARCHED
        //adapter = new SearchBeaconAdapter(this, LIST_BEACONS);
        //adapter.notifyDataSetChanged();

    }
}
