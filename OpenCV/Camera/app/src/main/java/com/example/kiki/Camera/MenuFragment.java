package com.example.kiki.Camera;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by kiki on 10.11.15.
 */
public class MenuFragment extends PreferenceFragment {
    private View mView; // View corresponding to fragment -- inflated xml file


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.menufragment, container, false);

        return mView;
    }



}
