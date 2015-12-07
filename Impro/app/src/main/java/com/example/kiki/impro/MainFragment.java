package com.example.kiki.impro;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by kiki on 10.11.15.
 */
public class MainFragment extends Fragment implements View.OnClickListener {
    private final static String TAG = "MainFrag";

    private View mView; // View corresponding to fragment -- inflated xml file
    private Button mButton;
    private Fragment mStillFragment;
    private Fragment mLiveFragment;

    private MainInterface mMainInterface;

    public interface MainInterface {
        public void onButtonClicked(View v);
        public void onFragmentCreated(Bundle savedInstanceState);
    }

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
        mMainInterface.onFragmentCreated(savedInstanceState);


    }


    @Override
    public void onClick(View v) {
        mMainInterface.onButtonClicked(v);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        myOnAttach(getActivity());

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        myOnAttach(activity);

    }


    public void myOnAttach(Activity activity) {
        // Make sure the interface ClickCallback is defined in MainActivity
        try {
            mMainInterface = (MainInterface) activity;
        }
        catch (Exception e) {
            throw new ClassCastException(activity.toString()
                    + " must implement MainInterface");
        }
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
