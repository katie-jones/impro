package com.example.kiki.impro;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.media.Image;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by katie on 2015-12-07.
 */
public class FilenamePickerPreference extends DialogPreference {
    String mFilename;
    EditText mTextBox;

    static private String TAG="FilenamePicker";
    static String DEFAULT_VALUE = "image";


    public FilenamePickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.filenamepicker_dialog);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);
        setDialogIcon(null);
    }

    @Override
    protected void onBindDialogView(View view) {
        // Create NumberPicker
        super.onBindDialogView(view);
        mTextBox = (EditText) view.findViewById(R.id.filenamePickerTextBox);
        mTextBox.setText(mFilename);

    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            // Restore existing state
            mFilename = this.getPersistedString(DEFAULT_VALUE);
            Log.e(TAG,mFilename);
        } else {
            // Set default state from the XML attribute
            mFilename = (String) defaultValue;
            persistString(mFilename);
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        // get local DEFAULT_VALUE if defaultValue is not defined in XML file.
        return a.getString(index);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {

        // When the user selects "OK", persist the new value
        if (positiveResult) {
            if (mTextBox.getText().toString().isEmpty()) {
                Toast.makeText(getContext(),"Filename empty. Not saving...",Toast.LENGTH_SHORT).show();
                return;
            }
            mFilename = mTextBox.getText().toString();
            // save the new value
            persistString(mFilename);

            // start new thread to save picture
            // Make new file in internal storage
            File file = new File(getContext().getFilesDir(),mFilename+".png");

            // make a new image saver and run it
            ImageSaver mImageSaver = new ImageSaver(CommonResources.filteredBitmap,file);
//            mImageSaver.run();
            Thread mThread = new Thread(mImageSaver,"ImageSavingBadassMofoThread");
            mThread.start();

            // set file world readable
            file.setReadable(true, false);

            // alert media scanner of new image file
            MediaScannerConnection.scanFile(getContext(), new String[]{file.toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
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

    private static class ImageSaver implements Runnable {
        //The JPEG image
        private final Bitmap mImage;
        //The file we save the image into
        private final File mFile;

        public ImageSaver(Bitmap image, File file) {
            mImage = image;
            mFile = file;
        }

        @Override
        public void run() {

//                ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
//                byte[] bytes = new byte[buffer.remaining()];
//                buffer.get(bytes);
            FileOutputStream output = null;
            try {
//                    File mFile = getAlbumStorageDir(mFilename);
                if (mFile.exists()) mFile.delete();
                mFile.createNewFile();
                Log.e(TAG, "Make file output stream");
                output = new FileOutputStream(mFile);
                Log.e(TAG, "Start saving file");
                mImage.compress(Bitmap.CompressFormat.PNG, 100, output);
                output.flush();
                Log.e(TAG, "Image saved! filename:" + mFile.getAbsolutePath());
//                    output.write(bytes);
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
}
