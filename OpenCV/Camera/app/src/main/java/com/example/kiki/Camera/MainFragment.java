package com.example.kiki.Camera;

import android.app.Fragment;
import android.os.Bundle;
import android.view.View;

/**
 * Created by kiki on 10.11.15.
 */
public class MainFragment extends Fragment {
    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        view.findViewById(R.id.mainfragment);
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
