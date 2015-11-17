package com.example.kiki.Camera;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by kiki on 10.11.15.
 */
public class LiveFragment extends Fragment {
    private View mView; // View corresponding to fragment -- inflated xml file
    private final static String TAG = "livefragment";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.livefragment, container, false);

        return mView;
    }

    

}
