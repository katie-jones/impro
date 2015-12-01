package com.example.kiki.impro;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by kiki on 10.11.15.
 */
public class MenuFragment extends PreferenceFragment {
    //private View mView; // View corresponding to fragment -- inflated xml file


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.menufragment);
    }



}
