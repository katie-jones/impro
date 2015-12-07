package com.example.kiki.impro;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;

/**
 * Created by katie on 2015-12-07.
 */
public class NumberPickerPreference extends DialogPreference {
    int mNewValue;
    NumberPicker np;
    static private String TAG="NumberPicker";

    public NumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDialogLayoutResource(R.layout.numberpicker_dialog);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);

        setDialogIcon(null);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        np = (NumberPicker) view.findViewById(R.id.numberPicker1);

        np.setMinValue(10);
        np.setMaxValue(100);
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mNewValue=newVal;
                Log.e(TAG, String.valueOf(oldVal));
                Log.e(TAG, String.valueOf(newVal));

            }
        });
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {

        // When the user selects "OK", persist the new value
        if (positiveResult) {
            // save the new value
            persistInt(mNewValue);
        }
    }
}
