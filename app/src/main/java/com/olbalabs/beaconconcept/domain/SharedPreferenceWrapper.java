package com.olbalabs.beaconconcept.domain;

import android.content.Context;
import android.content.SharedPreferences;

import com.olbalabs.beaconconcept.R;

/**
 * Created by obzzidian on 17/07/15.
 */
public class SharedPreferenceWrapper {

    private static SharedPreferences instance;

    public static SharedPreferences getInstance(){
        return instance;
    }

    public static void initialize(Context ctx){
        instance = ctx.getSharedPreferences(ctx.getString(R.string.preferences_file), Context.MODE_MULTI_PROCESS);
    }
}
