package com.example.kiki.impro;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.Image;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
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
public class FilenamePickerFragment extends DialogFragment {
    String mFilename;
    EditText mTextBox;
    TextView mTitle;
    ImageSaverExternal mImageSaver;
    Button mButton_Ok;
    Button mButton_Cancel;
    static private String TAG="FilenameFragment";
    static String DEFAULT_VALUE = "image";

    private static final String PREF_IMAGETYPE_DEFAULT = "0";
    private static final String PREF_IMAGETYPE_KEY = "p_imagetype_key";

    ImproDbAdapter mDbAdapter;




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

        mDbAdapter = new ImproDbAdapter(getActivity());

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

            // get image type from preferences
            SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            CommonResources.ImageType imageFormat = CommonResources.getImageType(mPrefs);
//            CommonResources.ImageType imageFormat = CommonResources.ImageType.values()[Integer.parseInt(mPrefs.getString(PREF_IMAGETYPE_KEY, PREF_IMAGETYPE_DEFAULT))];
            Bitmap.CompressFormat compFormat;
            switch (imageFormat)
            {
                case JPEG:
                    mFilename = mFilename + ".jpeg";
                    compFormat = Bitmap.CompressFormat.JPEG;
                    break;
                case WEBP:
                    mFilename = mFilename + ".webp";
                    compFormat = Bitmap.CompressFormat.WEBP;
                    break;
                default:
                    mFilename = mFilename + ".png";
                    compFormat = Bitmap.CompressFormat.PNG;
                    break;
            }


            // save the new value
            switch(getArguments().getString("type")){
                case "filtered":
                    CommonResources.filteredName = mFilename;
                    mImageSaver = new ImageSaverExternal(CommonResources.filteredBitmap,mFilename,getActivity(), compFormat);
                    break;
                case "original":
                    CommonResources.bitmapName = mFilename;
                    mImageSaver = new ImageSaverExternal(CommonResources.bitmap,mFilename,getActivity(), compFormat);

                    // If saving original image, add database entry with relevant info
                    mDbAdapter.open();

                    int quality = mPrefs.getInt(CommonResources.PREF_QUALITY_KEY, CommonResources.PREF_QUALITY_DEFAULT);
//                    String filterType = mPrefs.getString(CommonResources.PREF_FILTERTYPE_KEY, CommonResources.PREF_FILTERTYPE_DEFAULT);
//                    int[] values = CommonResources.getFilterValues(mPrefs, CommonResources.FilterType.values()[Integer.parseInt(filterType)]);
                    CommonResources.FilterType filterType = CommonResources.getFilterType(mPrefs);
                    int[] values = CommonResources.getFilterValues(mPrefs, filterType);

                    long rowId = mDbAdapter.createFilter(mFilename, quality, filterType.toString(), values);
                    Log.e(TAG, "New DB entry (row ID: " + String.valueOf(rowId) + ")");


                    mDbAdapter.close();

                    break;
                default:
                    break;
            }
            Toast.makeText(getActivity(),"Saving...",Toast.LENGTH_SHORT).show();

            Thread mThread = new Thread(mImageSaver,"ImageSavingBadassMofoThread");
            mThread.start();



        }
    }

    private static class ImageSaverExternal implements Runnable {
        private final Bitmap mImage;
        private final String mFilename;
        private final Context mContext;
        private final Bitmap.CompressFormat mFormat;

        public ImageSaverExternal(Bitmap image, String filename, Context context, Bitmap.CompressFormat imageFormat) {
            mFormat = imageFormat;
            mFilename = filename;
            mImage = image;
            mContext = context;
        }

        @Override
        public void run() {
            String fullPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()+"/"+CommonResources.directory;
            try {
                boolean result = true;
                File dir = new File(fullPath);
                if (!dir.exists()) {
                    result = dir.mkdirs();
                }
                if (result) {

                    OutputStream fOut;
                    File file = new File(fullPath, mFilename);
                    boolean existant = file.createNewFile();

                    fOut = new FileOutputStream(file);

                    mImage.compress(mFormat, 100, fOut);
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

            // alert media scanner of new image file
            MediaScannerConnection.scanFile(mContext, new String[]{mFilename}, null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                    Log.i(TAG, "Scanned " + path + ":");
                    Log.i(TAG, "uri=" + uri);
                }
            });
        }
    }
}
