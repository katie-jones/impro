package com.example.kiki.impro;

import android.os.Parcel;
import android.os.Parcelable;
import android.preference.Preference;

/**
 * Created by kiki on 08.12.15.
 */
public class PreferenceSavedState extends Preference.BaseSavedState{
    int quality;
    public PreferenceSavedState(Parcelable superState) {
        super(superState);
    }

    public PreferenceSavedState(Parcel source){
        super(source);
        quality=source.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeInt(quality);
    }

    public static final Parcelable.Creator<PreferenceSavedState> CREATOR =
        new Parcelable.Creator<PreferenceSavedState>() {
            public PreferenceSavedState createFromParcel(Parcel in) {
                return new PreferenceSavedState(in);
            }
            public PreferenceSavedState[] newArray(int size) {
                return new PreferenceSavedState[size];
            }
        };
}
