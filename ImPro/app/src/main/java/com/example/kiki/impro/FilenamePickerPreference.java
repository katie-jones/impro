package com.example.kiki.impro;

import android.app.DialogFragment;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.media.Image;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.preference.DialogPreference;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

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
            //File file = new File(getContext().getFilesDir(),mFilename+".png");

            //mFilename = getContext().getFilesDir()+"/"+mFilename+".png";

            // make a new image saver and run it
            //ImageSaverInternal mImageSaver = new ImageSaverInternal(CommonResources.filteredBitmap,file);

            ImageSaverExternal mImageSaver = new ImageSaverExternal(CommonResources.filteredBitmap,mFilename+".png",getContext());
            Thread mThread = new Thread(mImageSaver,"ImageSavingBadassMofoThread");
            mThread.start();

            // set file world readable: Doesn't work for internal storage!
            // file.setReadable(true, false);

            // alert media scanner of new image file
            MediaScannerConnection.scanFile(getContext(), new String[]{mFilename+".png"}, null, new MediaScannerConnection.OnScanCompletedListener() {
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

    public final static String APP_PATH_SD_CARD = "/DesiredSubfolderName/";
    public final static String APP_THUMBNAIL_PATH_SD_CARD = "thumbnails";



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
            String fullPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
            try {
                File dir = new File(fullPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                OutputStream fOut = null;
                File file = new File(fullPath,mFilename);
                file.createNewFile();
                fOut = new FileOutputStream(file);

                mImage.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                fOut.flush();
                fOut.close();

                MediaStore.Images.Media.insertImage(mContext.getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());

                Log.e(TAG, "Image saved! filename:" + file.getAbsolutePath());
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }
}
