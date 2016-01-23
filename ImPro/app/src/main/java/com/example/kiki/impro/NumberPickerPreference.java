package com.example.kiki.impro;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;

import java.util.prefs.Preferences;

/**
 * Created by katie on 2015-12-07.
 */
public class NumberPickerPreference extends DialogPreference {
    int mQualityValue;
    NumberPicker np;
    static private String TAG="NumberPicker";
    static int DEFAULT_VALUE=CommonResources.PREF_QUALITY_DEFAULT;
    int minValue = 5;
    int maxValue = 100;
    int step = 5;


    public NumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.numberpicker_dialog);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);
        setDialogIcon(null);
    }

    @Override
    protected void onBindDialogView(View view) {
        // Create NumberPicker
        super.onBindDialogView(view);
        np = (NumberPicker) view.findViewById(R.id.numberPicker1);
        np.setSaveEnabled(true);

        // set values incrementing by "step"
        int length=(maxValue-minValue)/step+1;
        String[] valueSet = new String[length];
        for (int i=minValue;i<=maxValue;i+=step) {
            valueSet[(i-minValue)/step]=String.valueOf(i);
        }
        np.setMinValue(0);
        np.setMaxValue(length - 1);
        np.setDisplayedValues(valueSet);
        // don't wrap values
        np.setWrapSelectorWheel(false);
        // set initial value (mQualityValue initialized by onSetInitialValue)
        np.setValue((mQualityValue-minValue)/step);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            // Restore existing state
            mQualityValue = this.getPersistedInt(DEFAULT_VALUE);
            Log.e(TAG,String.valueOf(mQualityValue));
        } else {
            // Set default state from the XML attribute
            mQualityValue = (Integer) defaultValue;
            persistInt(mQualityValue);
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        // get local DEFAULT_VALUE if defaultValue is not defined in XML file.
        return a.getInteger(index,DEFAULT_VALUE);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {

        // When the user selects "OK", persist the new value
        if (positiveResult) {
            // save the new value
            mQualityValue=np.getValue()*step+minValue;
            persistInt(mQualityValue);
        }
    }
}
