package com.example.kiki.impro;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by kiki on 10.11.15.
 */

public class MenuFragment extends PreferenceFragment {
    static private String TAG="MenuFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.menufragment);

////        SharedPreferences prefs = PreferenceManager
//                .getDefaultSharedPreferences(getActivity());
//        prefs.registerOnSharedPreferenceChangeListener(getActivity());


    }


//    @Override
//    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
//        Log.e(TAG, "Save");
//        super.onSaveInstanceState(savedInstanceState);
//    }
}
