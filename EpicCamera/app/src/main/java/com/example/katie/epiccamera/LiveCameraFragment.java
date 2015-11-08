package com.example.katie.epiccamera;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by katie on 2015-11-08.
 */
public class LiveCameraFragment extends Fragment implements View.OnClickListener {


    private View mView;
    private TextView mText;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.live_camera, container, false);
        return mView;
    }

    @Override
    public void onClick(View view)
    {
        /*
        On click: replace current (LiveCameraFragment) fragment with a PhotoFragment
         */

        // Create new fragment and transaction
//        Fragment newFragment = new PhotoFragment();
//        FragmentTransaction transaction = getFragmentManager().beginTransaction();
//
//        // Replace whatever is in the livecamera view with this fragment,
//        // and add the transaction to the back stack
//        transaction.replace(R.id.livecamera, newFragment);
////        transaction.remove(R.id.livecamera);
//        transaction.addToBackStack(null);
//
//        // Commit the transaction
//        transaction.commit();

        mText=(TextView) mView.findViewById(R.id.textview1);
        mText.setText("@string/frag2");
        mText.setBackgroundColor(0);


    }


}
