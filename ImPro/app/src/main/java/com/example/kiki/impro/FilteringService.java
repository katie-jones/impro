package com.example.kiki.impro;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by katie on 2015-12-28.
 */
public class FilteringService extends IntentService {

    public FilteringService () {
        super("Image filtering service");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        // Gets data from the incoming Intent
        String dataString = workIntent.getDataString();
        // Do work here, based on the contents of dataString
    }

}
