package com.example.kiki.impro;

import android.app.DialogFragment;
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
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.core.Mat;

/**
 * Created by kiki on 10.11.15.
 */
public class StillFragment extends Fragment {
    private View mView; // View corresponding to fragment -- inflated xml file
    private Bitmap mBitmap; // original, unmodified image
    private Bitmap filteredBitmap; // filtered image
    private Bitmap reducedBitmap; // scaled image
    private ImageView mImageView;
    private Button mButtonFilt;
    private Button mButtonOrig;

    private final static String TAG = "StillFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.stillfragment, container, false);
        mImageView = (ImageView) mView.findViewById(R.id.stillimageview);
        mBitmap = CommonResources.bitmap;

        // Set up button actions for saving images
        mButtonFilt = (Button) mView.findViewById(R.id.button_savefilt);
        mButtonFilt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG,"saving filtered");
                save_image("filtered");
            }
        });
        mButtonOrig = (Button) mView.findViewById(R.id.button_saveorig);
        mButtonOrig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG,"saving original");
                save_image("original");
            }
        });


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
        else {
            mImageView.setImageBitmap(filteredBitmap);
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

    public void save_image(String type) {
        Log.e(TAG, "save_image:" + type);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        FilenamePickerFragment prev = (FilenamePickerFragment) getFragmentManager().findFragmentByTag("FilenameFragment");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = FilenamePickerFragment.newInstance(type);
        newFragment.show(ft, "FilenameFragment");
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
}
