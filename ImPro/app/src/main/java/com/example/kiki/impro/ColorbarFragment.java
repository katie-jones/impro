package com.example.kiki.impro;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.florescu.android.rangeseekbar.RangeSeekBar;
import org.opencv.core.Range;

import java.util.Set;


/**
 * Created by kiki on 10.11.15.
 */
public class ColorbarFragment extends Fragment {
    static private String TAG="ColorbarFragment";
    private View mView;
    private RangeSeekBar<Integer> mSeekBar1,mSeekBar2,mSeekBar3;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mView = inflater.inflate(R.layout.colorbarfragment, container, false);

        mSeekBar1 = (RangeSeekBar<Integer>) mView.findViewById(R.id.seekbar1);
        mSeekBar2 = (RangeSeekBar<Integer>) mView.findViewById(R.id.seekbar2);
        mSeekBar3 = (RangeSeekBar<Integer>) mView.findViewById(R.id.seekbar3);



        RangeSeekBar.OnRangeSeekBarChangeListener<Integer> listener = new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            SharedPreferences mPrefs = getActivity().getSharedPreferences("my",Context.MODE_PRIVATE);
            //SharedPreferences mPrefs = getActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor mEditor = mPrefs.edit();
            Integer test = 0;

            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                RangeSeekBar<Integer> mbar = (RangeSeekBar<Integer>) bar;
                if (mbar.getId() == mSeekBar1.getId()) {
                    mEditor.putInt("lower1", minValue);
                    mEditor.putInt("upper1", maxValue);
                }
                if (mbar.getId() == mSeekBar2.getId()) {
                    mEditor.putInt("lower2", minValue);
                    mEditor.putInt("upper2", maxValue);
                }
                if (mbar.getId() == mSeekBar3.getId()) {
                    Log.e(TAG,"minValue3: "+ String.valueOf(minValue));
                    mEditor.putInt("lower3", minValue);
                    mEditor.putInt("upper3", maxValue);
                }
                mEditor.apply();
                SharedPreferences mPrefs2 = getActivity().getSharedPreferences("my",Context.MODE_PRIVATE);
                mPrefs2.getInt("lower3", test);
                Log.e(TAG, "lower3:" + String.valueOf(test));
            }
        };

        mSeekBar1.setOnRangeSeekBarChangeListener(listener);
        mSeekBar2.setOnRangeSeekBarChangeListener(listener);
        mSeekBar3.setOnRangeSeekBarChangeListener(listener);

        return mView;
    }


//    @Override
//    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
//        Log.e(TAG, "Save");
//        super.onSaveInstanceState(savedInstanceState);
//    }
}
