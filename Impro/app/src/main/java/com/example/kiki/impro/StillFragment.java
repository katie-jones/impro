package com.example.kiki.impro;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kiki on 10.11.15.
 */
public class StillFragment extends Fragment {
    private View mView; // View corresponding to fragment -- inflated xml file
    private Bitmap mBitmap; // original, unmodified image
    private Bitmap filteredBitmap; // filtered image
    private ImageView mImageView;
    private final static String TAG = "StillFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.stillfragment, container, false);
        mImageView = (ImageView) mView.findViewById(R.id.stillimageview);
        mBitmap = CommonResources.bitmap;

        // use global layout listener to find when view sizes have been assigned so we can
        // determine size of image view and apply appropriate transformation
        ViewTreeObserver vto = mImageView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
//                    Log.e(TAG, "Image view size: " + String.valueOf(mImageView.getWidth()) + " x " + String.valueOf(mImageView.getHeight()));
                ViewTreeObserver obs = mImageView.getViewTreeObserver();
                obs.removeOnGlobalLayoutListener(this);

                imageViewTransform(mImageView.getWidth(), mImageView.getHeight());

            }

        });

        // Apply filter only if a new image has been taken
        if (savedInstanceState == null) {
            applyFilter();
        }

        mImageView.setImageBitmap(filteredBitmap);


        return mView;
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    private void applyFilter() {
        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();
        int depth = 3;

        filteredBitmap = Bitmap.createBitmap(mBitmap, 0,0, width, height, new Matrix(), true);

        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int type = Integer.parseInt(mPrefs.getString("p_color_key", "0"));

        Mat mMat = new Mat(height,width,CvType.CV_8UC4,new Scalar(0));
        Mat colorMat = new Mat(height,width,CvType.CV_8UC4,new Scalar(0));
        Utils.bitmapToMat(filteredBitmap, mMat);

        byte[] pixelvector = new byte[0];
        byte[] zeros = new byte[0];

        switch (type) {
            case 0: // RGB
                Log.e(TAG,"RGB");
                depth = 3;
                pixelvector = new byte[depth+1];
                zeros = new byte[depth+1];
                colorMat = mMat;
                break;
            case 1: // HSV
                Log.e(TAG,"HSV");
                Imgproc.cvtColor(mMat, colorMat, Imgproc.COLOR_RGB2HSV);
                depth = 3;
                pixelvector = new byte[depth];
                zeros = new byte[depth+1];
                break;
            case 2: // CMYK
                Log.e(TAG,"CMYK");
                cvt2CMYK(mMat, colorMat);
                depth = 4;
                pixelvector = new byte[depth];
                zeros = new byte[depth];
        }

        int[] lower_array = new int[depth];
        int[] upper_array = new int[depth];
        for (int k=0; k<depth;k++) {
            lower_array[k] = mPrefs.getInt("lower"+String.valueOf(k+1), 0);
            Log.e(TAG,"lower"+String.valueOf(lower_array[k]));
            upper_array[k] = mPrefs.getInt("upper"+String.valueOf(k+1), 255);
            Log.e(TAG,"upper"+String.valueOf(upper_array[k]));
        }

        boolean inrange;
        int this_pix;

        Utils.bitmapToMat(filteredBitmap, mMat);
        //List<Mat> planes = new ArrayList<Mat>();
        //Core.split(mMat, planes);
        Log.e(TAG, "height" + String.valueOf(mMat.height()));
        //Log.e(TAG, "pixel" + String.valueOf(mMat.get(0, 0, pixelvector)));
        Log.e(TAG,"depth"+String.valueOf(mMat.type()));
        for (int i = 0; i<mMat.height(); i++) {
            for (int j = 0; j<mMat.width(); j++) {
                inrange=true;
                colorMat.get(i, j, pixelvector);
                for (int k = 0; k<depth; k++) {
                    // convert signed byte in unsigned integer
                    this_pix = ((int) pixelvector[k]) & 0xFF;
                    if ((j==0) && (i==0)) {
                        Log.e(TAG, "pixel " + String.valueOf(pixelvector[k]));
                        Log.e(TAG, "pixel int " + String.valueOf(this_pix));
                    }
                    if ((this_pix > upper_array[k]) || this_pix < lower_array[k]) {
                        inrange=false;
                        break;
                    }
                }
                if (!inrange) {
                    mMat.put(i, j, zeros);
                }
            }
        }
        //for (int k = 0; k < planes.size(); k++) {

            // all pixels above "upper" set to 0, other pixels untouched.
          //  Imgproc.threshold(planes.get(k), planes.get(k), upper, 0, Imgproc.THRESH_TOZERO_INV);
            // all pixels below "lower" set to 0, other pixels set to 1.
            // Imgproc.threshold(mMat, mMat, lower, 255, Imgproc.THRESH_BINARY);
          //  Imgproc.threshold(planes.get(k), planes.get(k), lower, 0, Imgproc.THRESH_TOZERO);
        //}

        //Core.merge(planes, mMat);

        Utils.matToBitmap(mMat, filteredBitmap);
    }


    private void imageViewTransform(int viewWidth, int viewHeight) {
        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();

        Matrix matrix = new Matrix(); // transformation matrix
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight); // view rectangle
        RectF bufferRect; // image rectangle

        bufferRect = new RectF(0, 0, mBitmap.getWidth(), mBitmap.getHeight());

        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();


//        Log.e(TAG, "Bitmap size " + String.valueOf(mBitmap.getHeight()) + " x " + String.valueOf(mBitmap.getWidth()));
//        Log.e(TAG, "view size " + String.valueOf(viewHeight + " x " + viewWidth));

        float scale;
        if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) {
            scale = Math.min(
                    (float) viewHeight / bufferRect.width(),
                    (float) viewWidth / bufferRect.height());
        }

        else {
            scale = Math.min(
                    (float) viewHeight / bufferRect.height(),
                    (float) viewWidth / bufferRect.width());
        }

        matrix.setScale(scale, scale, centerX, centerY);
        matrix.preTranslate((centerX - bufferRect.centerX()), (centerY - bufferRect.centerY()));
        matrix.postRotate(90 * (1 - rotation), centerX, centerY);

        mImageView.setImageMatrix(matrix);
    }


    private void cvt2CMYK(Mat mMat, Mat newMat) {
        final int depth = 4;
        byte[] pixelvector = new byte[depth];
        float r_prime, g_prime, b_prime, k;
        int c,m,y,kint;
        final float maxval = 255;

        for (int i = 0; i<mMat.height(); i++) {
            for (int j = 0; j<mMat.width(); j++) {
                mMat.get(i,j,pixelvector);
                r_prime = (float)pixelvector[0]/maxval;
                g_prime = (float)pixelvector[1]/maxval;
                b_prime = (float)pixelvector[2]/maxval;
                k = (1-Math.max(r_prime,Math.max(g_prime,b_prime)));
                c = (int) (maxval * (1-r_prime-k)/(1-k));
                m = (int) (maxval * (1-g_prime-k)/(1-k));
                y = (int) (maxval * (1-b_prime-k)/(1-k));
                kint = (int) (maxval*k);
                pixelvector[0] = (byte) c;
                pixelvector[1] = (byte) m;
                pixelvector[2] = (byte) y;
                pixelvector[3] = (byte) kint;
                newMat.put(i,j,pixelvector);
            }
        }
    }

    private void cvt2RGB(Mat mMat) {
        final int depth = 3;
        byte[] pixelvector = new byte[depth+1];
        byte r,g,b;
        float c,m,y,k;
        final float maxval = 255;

        for (int i = 0; i<mMat.height(); i++) {
            for (int j = 0; j<mMat.width(); j++) {
                mMat.get(i,j,pixelvector);
                c = (float)pixelvector[0]/maxval;
                m = (float)pixelvector[1]/maxval;
                y = (float)pixelvector[2]/maxval;
                k = (float)pixelvector[3]/maxval;
                r = (byte) (maxval*(1-c)*(1-k));
                g = (byte) (maxval*(1-m)*(1-k));
                b = (byte) (maxval*(1-y)*(1-k));
                pixelvector[0] = r;
                pixelvector[1] = g;
                pixelvector[2] = b;
                pixelvector[3] = (byte) maxval;
                mMat.put(i,j,pixelvector);
            }
        }
    }

}
