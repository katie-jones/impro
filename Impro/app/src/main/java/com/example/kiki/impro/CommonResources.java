package com.example.kiki.impro;

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

    // default quality value in % for filtered image
    public static final int DEFAULT_QUALITY = 50;

    // toast showing that filtering is happening
    public static Toast filtering_toast;

    // filename for filtered image
    public static String filteredName;

    // filename for original image
    public static String bitmapName;

    public static String directory = "ImPro";

}
