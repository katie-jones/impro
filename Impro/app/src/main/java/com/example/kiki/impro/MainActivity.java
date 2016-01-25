package com.example.kiki.impro;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

public class MainActivity extends Activity implements MainFragment.MainInterface,
        LiveFragment.LiveFragmentInterface,FileOpenerFragment.LiveFragmentInterface,
        SharedPreferences.OnSharedPreferenceChangeListener {
    private StillFragment mStillFragment;
    private LiveFragment mLiveFragment;
    private ColorbarFragment mColorbarFragment;
    static private String TAG = "MainActivity";
    private static final String TAG_LIVE_FRAGMENT = "LiveFragment";
    private static final String TAG_STILL_FRAGMENT = "StillFragment";
    private static final String TAG_COLORBAR_FRAGMENT = "ColorbarFragment";
    private static final String TAG_FILE_OPENER = "FileOpener";
    private static boolean stillActive;


    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };


    //Checks if the app has permission to write to device storage
    //If the app does not has permission then the user will be prompted to grant permissions
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }

        int permission2 = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);

        if (permission2 != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    1
            );
        }
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };


    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }
    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
    @Override
    public void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, mLoaderCallback);
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreferenceManager.setDefaultValues(this, R.xml.menufragment, false);

        mColorbarFragment = (ColorbarFragment) getFragmentManager().findFragmentById(R.id.colorbarfragment);

        // listener for the preference menu
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);

        // verify that all permissions defined in manifest are actually
        verifyStoragePermissions(this);
    }

    // Change fragment when button is clicked.
    public void onButtonClicked(View v) {
        if (mLiveFragment.isVisible()){
            // change to still fragment
            stillActive=false;
            LiveFragment frag = (LiveFragment) mLiveFragment;
            // take picture in live fragment, when its done, the fragment will change to
            // still fragment via Callback.
            frag.takePicture();
        }
        else  {
            // change to live fragment
            mStillFragment = (StillFragment) getFragmentManager().findFragmentByTag(TAG_STILL_FRAGMENT);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(mStillFragment.getId(), mLiveFragment, TAG_LIVE_FRAGMENT);
            stillActive=false;
            transaction.commit();
        }
    }


    // Interface method from live fragment: initializes both fragments
    public void onFragmentCreated(Bundle savedInstanceState) {
        mLiveFragment = (LiveFragment) getFragmentManager().findFragmentByTag(TAG_LIVE_FRAGMENT);
        mStillFragment = (StillFragment) getFragmentManager().findFragmentByTag(TAG_STILL_FRAGMENT);
        if (mLiveFragment==null)
            mLiveFragment = new LiveFragment();
        if (mStillFragment==null)
            mStillFragment = new StillFragment();

        // show livefragment as first fragment
        if (savedInstanceState==null) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.cameraview, mLiveFragment, TAG_LIVE_FRAGMENT);
            stillActive=false;
            transaction.commit();
        }
    }

    // interface method from live fragment: send bitmap to still fragment
    public void toStillFragment() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(mLiveFragment.getId(), mStillFragment, TAG_STILL_FRAGMENT);
        stillActive=true;
        transaction.commit();
    }

    // Update CommonResources with new preference values
    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {

        if (key.equals(CommonResources.PREF_FILTERTYPE_KEY)) {
            CommonResources.FilterType type = CommonResources.getFilterType(prefs);
            mColorbarFragment.setColorbarType(type);
            return;
        }

        if (key.contains(CommonResources.PREF_FILTERSETTINGS_KEY_ROOT)) {
            CommonResources.FilterType type = CommonResources.getFilterType(prefs);
            final int[] filter_settings = CommonResources.getFilterValues(prefs, type);

            mColorbarFragment.setValues(filter_settings);
            return;
        }

        if (key.equals(CommonResources.PREF_QUALITY_KEY)) {
            int newQuality = prefs.getInt(key, CommonResources.PREF_QUALITY_DEFAULT);
            if (stillActive) {
                mStillFragment.changeQuality(newQuality);
            }
        }
    }
}
