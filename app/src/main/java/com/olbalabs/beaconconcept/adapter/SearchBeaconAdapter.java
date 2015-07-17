package com.olbalabs.beaconconcept.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gc.materialdesign.views.CheckBox;
import com.olbalabs.beaconconcept.R;
import com.olbalabs.beaconconcept.domain.Beacon;

import java.util.ArrayList;

/**
 * Created by julenzugastibilbao on 17/7/15.
 */
public class SearchBeaconAdapter extends ArrayAdapter<Beacon> {

    ArrayList<Beacon> array;
    LayoutInflater inflater;

    public SearchBeaconAdapter(Context context, ArrayList<Beacon> array) {
        super(context, R.layout.beacon_item);
        this.array = array;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return array.size();
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        final Beacon beacon;

        if (view == null) {
            view = inflater.inflate(R.layout.beacon_item, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();

            beacon = array.get(position);

            //DRAW BEACON
            holder.uuid.setText(beacon.getUuid());

            //CHANGE COLOR IF CONNECTED
        }
        return view;
    }


    static class ViewHolder {

        TextView uuid;
        CheckBox checkBox;

        ViewHolder(View view) {
            uuid = (TextView) view.findViewById(R.id.uuid);
            checkBox = (CheckBox) view.findViewById(R.id.checkbox);

        }

    }

}
