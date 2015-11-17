package com.example.kiki.Camera;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by kiki on 10.11.15.
 */
public class MainFragment extends Fragment implements View.OnClickListener {
    private View mView; // View corresponding to fragment -- inflated xml file
    private Fragment mStillFragment;
    private Fragment mLiveFragment;
    private Button mButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.mainfragment, container, false);

        mButton = (Button) mView.findViewById(R.id.button);
        mButton.setOnClickListener(this);
        return mView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLiveFragment = new LiveFragment();
        mStillFragment = new StillFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.cameraview, mStillFragment);
        transaction.commit();


    }

    @Override
    public void onClick(View v){
//        LiveFragment test = (LiveFragment)
//                getChildFragmentManager().findFragmentByTag("livefragment");
        // Exchange current fragment with the other one.
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        if (mLiveFragment.isVisible()){
            mStillFragment = new StillFragment();
            transaction.replace(mLiveFragment.getId(), mStillFragment);
        }
        else {
            mLiveFragment = new LiveFragment();
            transaction.replace(mStillFragment.getId(), mLiveFragment);
        }

//        if (test == null) {
//            mLiveFragment = new LiveFragment();
//            transaction.replace(mStillFragment.getId(), mLiveFragment);
//        }
//        else {
//            mStillFragment = new StillFragment();
//            transaction.replace(mLiveFragment.getId(), mStillFragment);
//        }
        transaction.commit();


    }

//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
        //super.onActivityCreated(savedInstanceState);

        // Check to see if we have a frame in which to embed the details
        // fragment directly in the containing UI.
//        View mainFrame = getActivity().findViewById(R.id.activity_main);
//        mDualPane = mainFrame != null && mainFrame.getVisibility() == View.VISIBLE;
//
//        if (mDualPane) {
//            // In dual-pane mode, the list view highlights the selected item.
//            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//            // Make sure our UI is in the correct state.
//            showDetails(mCurCheckPosition);
//        }
//    }

}
