package com.example.kiki.Camera;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
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

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }



    public void onButtonClicked(View v) {

        // Exchange current fragment with the other one.
        if (mLiveFragment.isVisible()){
            LiveFragment frag = (LiveFragment) mLiveFragment;
            // take picture in live fragment
            frag.takePicture();
        }
        else {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(mStillFragment.getId(), mLiveFragment);
            transaction.commit();
        }


    }

    // interface method from live fragment: initializes both fragments
    public void onFragmentCreated() {
        mLiveFragment = new LiveFragment();
        mStillFragment = new StillFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.cameraview, mStillFragment);
        transaction.commit();
    }

    // interface method from live fragment: send bitmap to still fragment
    public void toStillFragment() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(mLiveFragment.getId(), mStillFragment);
        transaction.commit();
    }
}
