package com.example.kiki.impro;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

public class MainActivity extends Activity implements MainFragment.MainInterface, LiveFragment.LiveFragmentInterface {
    private Fragment mStillFragment;
    private Fragment mLiveFragment;
    static private String TAG = "MainActivity";
    private static final String TAG_LIVE_FRAGMENT = "live_fragment";
    private static final String TAG_STILL_FRAGMENT = "still_fragment";
    private static boolean stillActive;

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
        Log.e(TAG,"Restore");
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
    }

    public void onButtonClicked(View v) {
        Log.e(TAG,"button clicked");
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
                frag.onRotated();
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
}
