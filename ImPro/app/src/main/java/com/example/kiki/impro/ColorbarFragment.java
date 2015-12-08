package com.example.kiki.impro;

import android.app.Fragment;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by kiki on 10.11.15.
 */
public class ColorbarFragment extends Fragment {
    static private String TAG="ColorbarFragment";
    private View mView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mView = inflater.inflate(R.layout.colorbarfragment, container, false);
        return mView;
    }


//    @Override
//    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
//        Log.e(TAG, "Save");
//        super.onSaveInstanceState(savedInstanceState);
//    }
}
