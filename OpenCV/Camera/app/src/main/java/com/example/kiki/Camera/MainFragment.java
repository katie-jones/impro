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
public class MainFragment extends Fragment {
    private View mView; // View corresponding to fragment -- inflated xml file


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.mainfragment, container, false);

        return mView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fragment stillFragment = new StillFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.cameraview, stillFragment);
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
