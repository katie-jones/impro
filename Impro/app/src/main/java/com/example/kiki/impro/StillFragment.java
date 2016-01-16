package com.example.kiki.impro;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.LayerDrawable;
import android.media.Image;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kiki on 10.11.15.
 */
public class StillFragment extends Fragment {
    private View mView; // View corresponding to fragment -- inflated xml file
    private Bitmap mBitmap; // original, unmodified image
    private Bitmap filteredBitmap; // filtered image
    private Bitmap reducedBitmap; // scaled image
    private ImageView mImageView;
    private final static String TAG = "StillFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.stillfragment, container, false);
        mImageView = (ImageView) mView.findViewById(R.id.stillimageview);
        mBitmap = CommonResources.bitmap;

        // use global layout listener to find when view sizes have been assigned so we can
        // determine size of image view and apply appropriate transformation
        ViewTreeObserver vto = mImageView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
//                    Log.e(TAG, "Image view size: " + String.valueOf(mImageView.getWidth()) + " x " + String.valueOf(mImageView.getHeight()));
                ViewTreeObserver obs = mImageView.getViewTreeObserver();
                obs.removeOnGlobalLayoutListener(this);

                imageViewTransform(mImageView.getWidth(), mImageView.getHeight());

            }

        });

        // Apply filter only if a new image has been taken
        if (savedInstanceState == null) {
            // initialize filtered image and then apply filter
            SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            int mQuality = mPrefs.getInt("p_key_quality", CommonResources.DEFAULT_QUALITY);

            reducedBitmap = Bitmap.createScaledBitmap(mBitmap, (mQuality * mBitmap.getWidth()) / 100, (mQuality * mBitmap.getHeight()) / 100, true);
            filteredBitmap = Bitmap.createBitmap(reducedBitmap, 0, 0, reducedBitmap.getWidth(), reducedBitmap.getHeight(), new Matrix(), true);
            CommonResources.reducedBitmap = reducedBitmap;
            CommonResources.filteredBitmap = filteredBitmap;

            // Create new intent to filter image
            CommonResources.filtering_toast = Toast.makeText(getActivity(), "filtering...", Toast.LENGTH_LONG);
            CommonResources.filtering_toast.show();
            Intent mServiceIntent = new Intent(getActivity(), FilteringService.class);
            getActivity().startService(mServiceIntent);
        }


        return mView;
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        // The filter's action is BROADCAST_ACTION
        IntentFilter mStatusIntentFilter = new IntentFilter(
                CommonResources.BROADCAST_ACTION);

        // Adds a data filter for the HTTP scheme
//        mStatusIntentFilter.addDataScheme("http");


        // Registers the FilteringBroadcastReceiver and its intent filters
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                mReceiver,
                mStatusIntentFilter);


    }


    @Override
    public void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
        super.onDestroy();
    }


    // Instantiates a new FilteringBroadcastReceiver
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        // Called when the BroadcastReceiver gets an Intent it's registered to receive
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG,"Broadcast receiver called");
            // Update filtered bitmap and set as content of image view
            filteredBitmap = CommonResources.filteredBitmap;
            CommonResources.filtering_toast.cancel();
            mImageView.setImageBitmap(filteredBitmap);


        }
    };




    private void imageViewTransform(int viewWidth, int viewHeight) {
        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();

        Matrix matrix = new Matrix(); // transformation matrix
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight); // view rectangle
        RectF bufferRect; // image rectangle

        bufferRect = new RectF(0, 0, filteredBitmap.getWidth(), filteredBitmap.getHeight());

        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();


//        Log.e(TAG, "Bitmap size " + String.valueOf(mBitmap.getHeight()) + " x " + String.valueOf(mBitmap.getWidth()));
//        Log.e(TAG, "view size " + String.valueOf(viewHeight + " x " + viewWidth));

        float scale;
        if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) {
            scale = Math.min(
                    (float) viewHeight / bufferRect.width(),
                    (float) viewWidth / bufferRect.height());
        }

        else {
            scale = Math.min(
                    (float) viewHeight / bufferRect.height(),
                    (float) viewWidth / bufferRect.width());
        }

        matrix.setScale(scale, scale, centerX, centerY);
        matrix.preTranslate((centerX - bufferRect.centerX()), (centerY - bufferRect.centerY()));
        matrix.postRotate(90 * (1 - rotation), centerX, centerY);

        mImageView.setImageMatrix(matrix);
    }




    private void cvt2RGB(Mat mMat) {
        final int depth = 3;
        byte[] pixelvector = new byte[depth+1];
        byte r,g,b;
        float c,m,y,k;
        final float maxval = 255;

        for (int i = 0; i<mMat.height(); i++) {
            for (int j = 0; j<mMat.width(); j++) {
                mMat.get(i,j,pixelvector);
                c = (float)pixelvector[0]/maxval;
                m = (float)pixelvector[1]/maxval;
                y = (float)pixelvector[2]/maxval;
                k = (float)pixelvector[3]/maxval;
                r = (byte) (maxval*(1-c)*(1-k));
                g = (byte) (maxval*(1-m)*(1-k));
                b = (byte) (maxval*(1-y)*(1-k));
                pixelvector[0] = r;
                pixelvector[1] = g;
                pixelvector[2] = b;
                pixelvector[3] = (byte) maxval;
                mMat.put(i,j,pixelvector);
            }
        }
    }



//
//        // Checks if external storage is available for read and write
//        public boolean isExternalStorageWritable() {
//            String state = Environment.getExternalStorageState();
//            if (Environment.MEDIA_MOUNTED.equals(state)) {
//                return true;
//            }
//            return false;
//        }
//
//        // Checks if external storage is available to at least read
//        public boolean isExternalStorageReadable() {
//            String state = Environment.getExternalStorageState();
//            if (Environment.MEDIA_MOUNTED.equals(state) ||
//                    Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
//                return true;
//            }
//            return false;
//        }
//
//        // Get file where picture can be stored
//        public File getAlbumStorageDir(String pictureName) {
//            // Get the directory for the user's public pictures directory.
//            File file = new File(Environment.DIRECTORY_PICTURES,pictureName);
////                    Environment.getExternalStorageDirectory()+"/myfolder",pictureName);
////                    Environment.getExternalStoragePublicDirectory(
////                    Environment.DIRECTORY_PICTURES), pictureName);
//
//            if (!file.mkdirs()) {
//                Log.e(TAG, "Directory not created");
//            }
//            return file;
//        }
//
//
//    }




}
