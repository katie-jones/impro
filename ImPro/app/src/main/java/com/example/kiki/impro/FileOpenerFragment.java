package com.example.kiki.impro;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
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
    static private String TAG="FileOpenerFragment";
    public interface LiveFragmentInterface {
        public void toStillFragment();
    }
    LiveFragmentInterface mInterface;

    static FileOpenerFragment newInstance() {
        return new FileOpenerFragment();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.filenameopener_dialog, null);

        mButton_Ok = (Button) v.findViewById(R.id.button_ok);
        mButton_Cancel = (Button) v.findViewById(R.id.button_cancel);

        mButton_Ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });

        mButton_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO: change to still fragment
                getDialog().dismiss();
            }
        });

        return v;
    }
}
