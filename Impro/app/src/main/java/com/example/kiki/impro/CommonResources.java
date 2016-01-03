package com.example.kiki.impro;

import android.graphics.Bitmap;
import android.os.Bundle;

/**
 * Class containing resources shared across the app.
 * Contains: bitmap image sent from LiveFragment to StillFragment
 * Created by katie on 2015-11-25.
 */
public class CommonResources {
    // original, unmodified bitmap
    public static Bitmap bitmap;

    // filtered bitmap
    public static Bitmap filteredBitmap;

    // string defining broadcast action for filtering service
    public static final String BROADCAST_ACTION = "com.example.kiki.impro.BROADCAST";

    // key for return status of filtering service
    public static final String EXTENDED_DATA_STATUS = "com.example.kiki.impro.STATUS";

}
