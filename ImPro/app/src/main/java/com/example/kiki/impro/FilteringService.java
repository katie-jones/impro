package com.example.kiki.impro;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * Created by katie on 2015-12-28.
 */

public class FilteringService extends IntentService {
    private final int HMaxValue = 180;
    private final int RGBMaxValue = 255;
    private Bitmap filteredBitmap;
    private final String TAG = "filtering_service";


    public FilteringService () {
        super("Image filtering service");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        Log.e(TAG,"Start filtering service");

        // Start filtering from original bitmap
//        filteredBitmap = CommonResources.bitmap;
        //filteredBitmap = CommonResources.filteredBitmap;
        applyFilter();
        //CommonResources.filteredBitmap = filteredBitmap;
        Log.e(TAG,"Finish applying filter");
        int status = 0; // status to broadcast - 0 is no error

        Intent localIntent =
                new Intent(CommonResources.BROADCAST_ACTION)
                        // Puts the status into the Intent
                        .putExtra(CommonResources.EXTENDED_DATA_STATUS, status);
        // Broadcasts the Intent to receivers in this app.
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);

    }

    private void applyFilter() {
        int width = CommonResources.reducedBitmap.getWidth();
        int height = CommonResources.reducedBitmap.getHeight();
        int depth = 3;

        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        int type = Integer.parseInt(mPrefs.getString("p_color_key", "0"));

        Mat mMat = new Mat(height,width, CvType.CV_8UC4,new Scalar(0));
        Mat mOrigMat = new Mat(height,width, CvType.CV_8UC4,new Scalar(0));
        Mat colorMat = new Mat(height,width,CvType.CV_8UC4,new Scalar(0));
        //Utils.bitmapToMat(filteredBitmap, mMat);
        //TEST:
        Utils.bitmapToMat(CommonResources.reducedBitmap,mOrigMat);
        mOrigMat.copyTo(mMat);

        byte[] pixelvector = new byte[0];
        byte[] zeros = new byte[0];

        switch (type) {
            case 0: // RGB
                Log.e(TAG, "RGB");
                depth = 3;
                pixelvector = new byte[depth+1];
                zeros = new byte[depth+1];
                colorMat = mMat;
                break;
            case 1: // HSV
                Log.e(TAG, "HSV");
                Imgproc.cvtColor(mMat, colorMat, Imgproc.COLOR_RGB2HSV);
                depth = 3;
                pixelvector = new byte[depth];
                zeros = new byte[depth+1];
                break;
            case 2: // CMYK
                Log.e(TAG,"CMYK");
                cvt2CMYK(mMat, colorMat);
                depth = 4;
                pixelvector = new byte[depth];
                zeros = new byte[depth];
        }

        // Read min and max filter values and check if filter has to be applied.
        int[] lower_array = new int[depth];
        int[] upper_array = new int[depth];
        boolean do_filtering = false;
        for (int k=0; k<depth;k++) {
            lower_array[k] = mPrefs.getInt("lower"+String.valueOf(k+1), 0);
            if (type == 1 && k==0) {
                upper_array[k] = mPrefs.getInt("upper"+String.valueOf(k+1), HMaxValue);
                if (lower_array[k]>0 || upper_array[k]<HMaxValue) {
                    do_filtering = true;
                }
            }
            else {
                upper_array[k] = mPrefs.getInt("upper"+String.valueOf(k+1), RGBMaxValue);
                if (lower_array[k]>0 || upper_array[k]<RGBMaxValue) {
                    do_filtering = true;
                }
            }
//            Log.e(TAG,"lower"+String.valueOf(lower_array[k]));
//            Log.e(TAG,"upper"+String.valueOf(upper_array[k]));


        }

        // Filter out colors of interest
        if (do_filtering) {
            Log.e(TAG,"filtering");

            boolean inrange;
            int this_pix;


            for (int i = 0; i<mMat.height(); i++) {
                for (int j = 0; j<mMat.width(); j++) {
                    inrange=true;
                    colorMat.get(i, j, pixelvector);
                    for (int k = 0; k<depth; k++) {
                        // convert signed byte into unsigned integer
                        this_pix = ((int) pixelvector[k]) & 0xFF;
//                    if ((j==0) && (i==0)) {
//                        Log.e(TAG, "pixel " + String.valueOf(pixelvector[k]));
//                        Log.e(TAG, "pixel int " + String.valueOf(this_pix));
//                    }
                        if ((this_pix > upper_array[k]) || this_pix < lower_array[k]) {
                            inrange=false;
                            break;
                        }
                    }
                    if (!inrange) {
                        mMat.put(i, j, zeros);
                    }
                }
            }
        }

        Utils.matToBitmap(mMat, CommonResources.filteredBitmap);
    }



    // Convert RGB Mat mMat to CMYK Mat newMat
    private void cvt2CMYK(Mat mMat, Mat newMat) {
        final int depth = 4;
        byte[] pixelvector = new byte[depth];
        float r_prime, g_prime, b_prime, k;
        int c,m,y,kint;
        final float maxval = 255;

        for (int i = 0; i<mMat.height(); i++) {
            for (int j = 0; j<mMat.width(); j++) {
                mMat.get(i,j,pixelvector);
                r_prime = (float)pixelvector[0]/maxval;
                g_prime = (float)pixelvector[1]/maxval;
                b_prime = (float)pixelvector[2]/maxval;
                k = (1-Math.max(r_prime,Math.max(g_prime,b_prime)));
                c = (int) (maxval * (1-r_prime-k)/(1-k));
                m = (int) (maxval * (1-g_prime-k)/(1-k));
                y = (int) (maxval * (1-b_prime-k)/(1-k));
                kint = (int) (maxval*k);
                pixelvector[0] = (byte) c;
                pixelvector[1] = (byte) m;
                pixelvector[2] = (byte) y;
                pixelvector[3] = (byte) kint;
                newMat.put(i,j,pixelvector);
            }
        }
    }

}
