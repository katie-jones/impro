package com.example.kiki.impro;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Class containing resources shared across the app.
 * Contains: bitmap image sent from LiveFragment to StillFragment
 * Created by katie on 2015-11-25.
 */
public class CommonResources {
    // original, unmodified bitmap
    public static Bitmap bitmap;

    // reduced bitmap
    public static Bitmap reducedBitmap;

    // filtered bitmap
    public static Bitmap filteredBitmap;

    // string defining broadcast action for filtering service
    public static final String BROADCAST_ACTION = "com.example.kiki.impro.BROADCAST";

    // key for return status of filtering service
    public static final String EXTENDED_DATA_STATUS = "com.example.kiki.impro.STATUS";

    // toast showing that filtering is happening
    public static Toast filtering_toast;

    // filename for filtered image
    public static String filteredName;

    // filename for original image
    public static String bitmapName;

    public static String directory = "ImPro";

    public static String file_to_be_opened = "file";

    // image types to be saved
    public enum ImageType{PNG, JPG, WEBP};

    // filter types used
    public enum FilterType{RGB, HSV, CMYK};

    // Preferences shit
    public static String PREF_QUALITY_KEY = "p_quality_key";
    public static int PREF_QUALITY_DEFAULT = 50;
    public static String PREF_FILTERTYPE_KEY = "p_color_key";
    public static String PREF_FILTERTYPE_DEFAULT = "0";


    // Method to return the filter values from prefs as an integer array
    // The array is as follows:
    //      entries 0-3: lower values in order (entry 3 is 0 for RGB, HSV)
    //      entries 4-7: upper values in order (entry 7 is 0 for RGB, HSV)
    public static int[] getFilterValues(SharedPreferences prefs, FilterType type)
    {
        int depth = 4;

        int HMaxValue = 180;
        int RGBMaxValue = 255;

        int[] values = new int[2*depth];

        for (int k=0; k<depth;k++) {
            values[k] = prefs.getInt("lower" + String.valueOf(k + 1), 0);
            if (type == FilterType.HSV && k == 0) {
                values[k+depth] = prefs.getInt("upper" + String.valueOf(k + 1), HMaxValue);
            } else {
                values[k+depth] = prefs.getInt("upper" + String.valueOf(k + 1), RGBMaxValue);
            }
        }
        return values;
    }
}
