package com.example.kiki.impro;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.media.Image;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.preference.DialogPreference;
import android.provider.MediaStore;
import android.util.AttributeSet;
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
 * Created by kiki on 2015-12-07.
 */
public class FilenamePickerFragment extends DialogFragment {
    String mFilename;
    EditText mTextBox;
    TextView mTitle;
    ImageSaverExternal mImageSaver;
    Button mButton_Ok;
    Button mButton_Cancel;
    static private String TAG="FilenameFragment";
    static String DEFAULT_VALUE = "image";


    static FilenamePickerFragment newInstance(String type) {
        FilenamePickerFragment f = new FilenamePickerFragment();
        Bundle args = new Bundle();
        args.putString("type", type);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(this.getActivity());
        View v = inflater.inflate(R.layout.filenamepicker_fragment, container, false);

        mTitle = (TextView) v.findViewById(R.id.filenamePickerTitle);
        switch(getArguments().getString("type")){
            case "filtered":
                Log.e(TAG, "create: filtered");
                mTitle.setText("Save filtered image");
                break;
            case "original":
                Log.e(TAG, "create: original");
                mTitle.setText("Save original image");
                break;
            default:
                Log.e(TAG,"default");
                break;
        }

        mTextBox = (EditText) v.findViewById(R.id.filenamePickerTextBox);
        mTextBox.setText(mFilename);

        mButton_Ok = (Button) v.findViewById(R.id.button_ok);
        mButton_Cancel = (Button) v.findViewById(R.id.button_cancel);

        mButton_Ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // return the chosen filename
                saveFilename(true);
                getDialog().dismiss();
            }
        });
        mButton_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFilename(false);
                getDialog().dismiss();
            }
        });

        return v;
    }

    protected void saveFilename(boolean positiveResult) {
        Log.e(TAG,"saveFilename: saving "+getArguments().getString("type"));

        // When the user selects "OK", persist the new value
        if (positiveResult) {
            if (mTextBox.getText().toString().isEmpty()) {
                Toast.makeText(getActivity(),"Filename empty. Not saving...",Toast.LENGTH_SHORT).show();
                return;
            }
            mFilename = mTextBox.getText().toString();
            // save the new value
            switch(getArguments().getString("type")){
                case "filtered":
                    CommonResources.filteredName = mFilename;
                    mImageSaver = new ImageSaverExternal(CommonResources.filteredBitmap,mFilename+".png",getActivity());
                    break;
                case "original":
                    CommonResources.bitmapName = mFilename;
                    mImageSaver = new ImageSaverExternal(CommonResources.bitmap,mFilename+".png",getActivity());
                    break;
                default:
                    break;
            }
            Toast.makeText(getActivity(),"Saving...",Toast.LENGTH_SHORT).show();

            Thread mThread = new Thread(mImageSaver,"ImageSavingBadassMofoThread");
            mThread.start();

            // alert media scanner of new image file
            MediaScannerConnection.scanFile(getActivity(), new String[]{mFilename + ".png"}, null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                    Log.i(TAG, "Scanned " + path + ":");
                    Log.i(TAG, "uri=" + uri);
                }
            });


        }
    }



    /**
     * Saves a JPEG {@link Image} into the specified {@link File}.
     */

    private static class ImageSaverInternal implements Runnable {
        private final Bitmap mImage;
        private final File mFile;

        public ImageSaverInternal(Bitmap image, File file) {
            mImage = image;
            mFile = file;
            //mContext = context;
        }

        @Override
        public void run() {

            FileOutputStream output = null;
            try {
                if (mFile.exists()) mFile.delete();
                //mFile.createNewFile();
                Log.e(TAG, "Make file output stream");
                output = new FileOutputStream(mFile);
                Log.e(TAG, "Start saving file");
                mImage.compress(Bitmap.CompressFormat.PNG, 100, output);
                output.flush(); // makes sure all data in buffer is written
                Log.e(TAG, "Image saved! filename:" + mFile.getName());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (null != output) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static class ImageSaverExternal implements Runnable {
        //The JPEG image
        private final Bitmap mImage;
        private final String mFilename;
        private final Context mContext;

        public ImageSaverExternal(Bitmap image, String file, Context context) {
            mImage = image;
            mFilename = file;
            mContext = context;
        }

        @Override
        public void run() {
            String fullPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath()+"/"+CommonResources.directory;
            try {
                boolean result = true;
                File dir = new File(fullPath);
                if (!dir.exists()) {
                    result = dir.mkdirs();
                }
                if (result) {

                    OutputStream fOut = null;
                    File file = new File(fullPath, mFilename);
                    boolean existant = file.createNewFile();

                    fOut = new FileOutputStream(file);

                    mImage.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    fOut.flush();
                    fOut.close();

                    MediaStore.Images.Media.insertImage(mContext.getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());

                    if (!existant) Toaster.toast("Overwritten: "+file.getAbsolutePath());
                    else Toaster.toast("Image saved under " + file.getAbsolutePath());
                    Log.e(TAG, "Image saved under " + file.getAbsolutePath());

                } else {
                    Toaster.toast("Could not create directory at " + fullPath);
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }
}
