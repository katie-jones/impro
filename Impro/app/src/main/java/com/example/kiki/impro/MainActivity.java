package com.example.kiki.impro;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.util.prefs.PreferenceChangeListener;

public class MainActivity extends Activity implements MainFragment.MainInterface, LiveFragment.LiveFragmentInterface, SharedPreferences.OnSharedPreferenceChangeListener {
    private Fragment mStillFragment;
    private Fragment mLiveFragment;
    private ColorbarFragment mColorbarFragment;
    static private String TAG = "MainActivity";
    private static final String TAG_LIVE_FRAGMENT = "live_fragment";
    private static final String TAG_STILL_FRAGMENT = "still_fragment";
    private static final String TAG_COLORBAR_FRAGMENT = "colorbar_fragment";
    private static boolean stillActive;


    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
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
        Log.e(TAG,"Save");
        super.onSaveInstanceState(savedInstanceState);
    }
    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        Log.e(TAG, "Restore");
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

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);

        verifyStoragePermissions(this);
    }

    public void onButtonClicked(View v) {
        Log.e(TAG, "button clicked");
        // Exchange current fragment with the other one.
        if (mLiveFragment.isVisible()){
            LiveFragment frag = (LiveFragment) mLiveFragment;
            // take picture in live fragment, when its done, the fragment will change to
            // still fragment via Callback.
            frag.takePicture();
        }
        else {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(mStillFragment.getId(), mLiveFragment, TAG_LIVE_FRAGMENT);
            stillActive=false;
            transaction.commit();
        }


    }

    // interface method from live fragment: initializes both fragments
    public void onFragmentCreated(Bundle savedInstanceState) {
        Log.e(TAG,"fragment created");
        mLiveFragment = getFragmentManager().findFragmentByTag(TAG_LIVE_FRAGMENT);
        mStillFragment = getFragmentManager().findFragmentByTag(TAG_STILL_FRAGMENT);
        if (mLiveFragment==null)
            mLiveFragment = new LiveFragment();
        if (mStillFragment==null)
            mStillFragment = new StillFragment();

        if (savedInstanceState==null) {

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            //transaction.replace(R.id.cameraview, mStillFragment);
            transaction.replace(R.id.cameraview, mLiveFragment, TAG_LIVE_FRAGMENT);
            stillActive=false;
            transaction.commit();
        }
        else {
            if (stillActive) {
                StillFragment frag = (StillFragment) mStillFragment;
//                frag.onRotated();
            }
        }
    }

    // interface method from live fragment: send bitmap to still fragment
    public void toStillFragment() {
        Log.e(TAG,"to still fragment");
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(mLiveFragment.getId(), mStillFragment, TAG_STILL_FRAGMENT);
        stillActive=true;
        transaction.commit();
    }

    // method for menu fragment
    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        Log.e(TAG, "onSharedPreferenceChange");

        if (key.equals("p_color_key")) {
            int type = Integer.parseInt(prefs.getString("p_color_key", "0"));
            mColorbarFragment.setColorbarType(type);
        }
    }
}
