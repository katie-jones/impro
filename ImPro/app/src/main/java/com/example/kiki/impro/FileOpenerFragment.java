package com.example.kiki.impro;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import xdroid.toaster.Toaster;

/**
 * Created by kiki on 2016-01-15.
 */
public class FileOpenerFragment extends DialogFragment {
    Button mButton_Ok;
    Button mButton_Cancel;
    View mView;
    static private String TAG="FileOpenerFragment";
    private static final String TAG_FILE_FRAGMENT="FileFragment";

    public interface LiveFragmentInterface {
        public void toStillFragment();
    }
    LiveFragmentInterface mInterface;

    static FileOpenerFragment newInstance() {
        return new FileOpenerFragment();
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        myOnAttach(activity);

    }


    public void myOnAttach(Activity activity) {
        // Make sure the interface ClickCallback is defined in MainActivity
        try {
            mInterface = (LiveFragmentInterface) activity;
        }
        catch (Exception e) {
            throw new ClassCastException(activity.toString()
                    + " must implement LiveFragmentInterface");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.filenameopener_dialog, null);

        FileOpenerList fragment = new FileOpenerList();
        fragment.setTargetFragment(this, 0);
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(R.id.fileopenerlist, fragment, TAG_FILE_FRAGMENT);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        //ft.addToBackStack(null);
        ft.commit();


        mButton_Ok = (Button) mView.findViewById(R.id.button_ok);
        mButton_Cancel = (Button) mView.findViewById(R.id.button_cancel);

        mButton_Ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialogPositive();
            }
        });

        mButton_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO: change to still fragment
                getDialog().dismiss();
            }
        });

        return mView;
    }

    // Function to call from FileOpenerList to dismiss the dialog
    public void dismissDialogPositive()
    {
        // return the chosen filename
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(CommonResources.file_to_be_opened, options);

        // Save in common resources
        CommonResources.bitmap = bitmap;

        //TODO: change to still fragment
        mInterface.toStillFragment();

        getDialog().dismiss();
    }
}
