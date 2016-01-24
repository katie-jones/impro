package com.example.kiki.impro;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.florescu.android.rangeseekbar.RangeSeekBar;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Range;
import org.opencv.core.Scalar;

import java.util.Set;

import xdroid.toaster.Toaster;


/**
 * Created by kiki on 10.11.15.
 */
public class ColorbarFragment extends Fragment {
    static private String TAG="ColorbarFragment";
    private View mView;
    private RangeSeekBar<Integer> mSeekBar1,mSeekBar2,mSeekBar3,mSeekBar4;
    private TextView mText1,mText2,mText3,mText4;
    public static final int HMaxValue = 180;
    public static final int RGBMaxValue = 255;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mView = inflater.inflate(R.layout.colorbarfragment, container, false);

        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        CommonResources.FilterType filter_type = CommonResources.getFilterType(mPrefs);

        mSeekBar1 = (RangeSeekBar<Integer>) mView.findViewById(R.id.seekbar1);
        mSeekBar2 = (RangeSeekBar<Integer>) mView.findViewById(R.id.seekbar2);
        mSeekBar3 = (RangeSeekBar<Integer>) mView.findViewById(R.id.seekbar3);
        mSeekBar4 = (RangeSeekBar<Integer>) mView.findViewById(R.id.seekbar4);

        mText1 = (TextView) mView.findViewById(R.id.colortext1);
        mText2 = (TextView) mView.findViewById(R.id.colortext2);
        mText3 = (TextView) mView.findViewById(R.id.colortext3);
        mText4 = (TextView) mView.findViewById(R.id.colortext4);

        int[] filter_settings = CommonResources.getFilterValues(mPrefs, filter_type);

        setValues(filter_settings);

        setColorbarType(filter_type);


        RangeSeekBar.OnRangeSeekBarChangeListener<Integer> listener = new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor mEditor = mPrefs.edit();

            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                RangeSeekBar<Integer> mbar = (RangeSeekBar<Integer>) bar;
                if (mbar.getId() == mSeekBar1.getId()) {
                    mEditor.putInt(CommonResources.PREF_FILTERSETTINGS_KEY_ROOT + "0", minValue);
                    mEditor.putInt(CommonResources.PREF_FILTERSETTINGS_KEY_ROOT + "4", maxValue);
                }
                if (mbar.getId() == mSeekBar2.getId()) {
                    mEditor.putInt(CommonResources.PREF_FILTERSETTINGS_KEY_ROOT + "1", minValue);
                    mEditor.putInt(CommonResources.PREF_FILTERSETTINGS_KEY_ROOT + "5", maxValue);
                }
                if (mbar.getId() == mSeekBar3.getId()) {
                    mEditor.putInt(CommonResources.PREF_FILTERSETTINGS_KEY_ROOT + "2", minValue);
                    mEditor.putInt(CommonResources.PREF_FILTERSETTINGS_KEY_ROOT + "6", maxValue);
                }
                if (mbar.getId() == mSeekBar4.getId()) {
                    mEditor.putInt(CommonResources.PREF_FILTERSETTINGS_KEY_ROOT + "3", minValue);
                    mEditor.putInt(CommonResources.PREF_FILTERSETTINGS_KEY_ROOT + "7", maxValue);
                }
                mEditor.apply();

                // Dynamically apply new filter to image if in Still mode if any type is set.;
                StillFragment mFragment = (StillFragment)getFragmentManager().findFragmentByTag("StillFragment");
                if (mFragment != null && mFragment.isVisible()) {
                    CommonResources.filtering_toast = Toast.makeText(getActivity(), "filtering...", Toast.LENGTH_LONG);
                    CommonResources.filtering_toast.show();
                    Intent mServiceIntent = new Intent(getActivity(), FilteringService.class);
                    getActivity().startService(mServiceIntent);
                }
            }
        };



        mSeekBar1.setOnRangeSeekBarChangeListener(listener);
        mSeekBar2.setOnRangeSeekBarChangeListener(listener);
        mSeekBar3.setOnRangeSeekBarChangeListener(listener);
        mSeekBar4.setOnRangeSeekBarChangeListener(listener);

        return mView;
    }

    // Change colorbars based on chosen color scheme
    public void setColorbarType (CommonResources.FilterType type) {
        // use RGB limits and no 4th colorbar by default
        mSeekBar1.setRangeValues(0, RGBMaxValue);
        mSeekBar4.setVisibility(View.INVISIBLE);

        switch (type) {
            case RGB:
                mText1.setText("R");
                mText2.setText("G");
                mText3.setText("B");
                mText4.setText("");
                break;
            case HSV:
                mText1.setText("H");
                mText2.setText("S");
                mText3.setText("V");
                mText4.setText("");

                // change upper limit for seekbar 1 if HSV is chosen (H goes to 180 only)
                mSeekBar1.setRangeValues(0, HMaxValue);
                if (mSeekBar1.getSelectedMaxValue() > HMaxValue)
                    mSeekBar1.setSelectedMaxValue(HMaxValue);
                break;
            case CMYK:
                mText1.setText("C");
                mText2.setText("M");
                mText3.setText("Y");
                mText4.setText("K");

                // Set 4th colorbar to enabled if CMYK type is chosen
                mSeekBar4.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }

    }


    public void setValues(int[] settings)
    {
        mSeekBar1.setSelectedMinValue(settings[0]);
        mSeekBar2.setSelectedMinValue(settings[1]);
        mSeekBar3.setSelectedMinValue(settings[2]);
        mSeekBar4.setSelectedMinValue(settings[3]);

        mSeekBar1.setSelectedMaxValue(settings[4]);
        mSeekBar2.setSelectedMaxValue(settings[5]);
        mSeekBar3.setSelectedMaxValue(settings[6]);
        mSeekBar4.setSelectedMaxValue(settings[7]);
    }

}
