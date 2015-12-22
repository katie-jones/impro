package com.example.kiki.impro;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
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
    private Bitmap mBitmap;
    private Bitmap rotatedBitmap;
    private ImageView mImageView;
    private final static String TAG = "StillFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.e(TAG,"still on create view");
        mView = inflater.inflate(R.layout.stillfragment, container, false);
        mImageView = (ImageView) mView.findViewById(R.id.stillimageview);
        mBitmap = CommonResources.bitmap;
        if (mBitmap!=null) {
            Log.e(TAG,"Bitmap loaded");
        }
        if ((mBitmap!=null) && (mImageView!=null)) {
            if (savedInstanceState==null){
                imageViewTransform(mImageView.getMaxWidth(), mImageView.getMaxHeight());
              }
          applyFilter(0,200);
            //Log.e(TAG, "Filter applied");
//            mImageView.setImageBitmap(mBitmap);
            mImageView.setImageBitmap(rotatedBitmap);
        }
        return mView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    private void applyFilter(int lower, int upper) {
        int width = rotatedBitmap.getWidth();
        int height = rotatedBitmap.getHeight();
        int depth = 3;

        SharedPreferences mPrefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        int type = Integer.parseInt(mPrefs.getString("p_color_key", "0"));

        Mat mMat = new Mat(height,width,CvType.CV_8UC4,new Scalar(0));
        Mat colorMat = new Mat(height,width,CvType.CV_8UC4,new Scalar(0));
        Utils.bitmapToMat(rotatedBitmap, mMat);

        byte[] pixelvector = new byte[0];
        byte[] zeros = new byte[0];

        switch (type) {
            case 0: // RGB
                depth = 3;
                pixelvector = new byte[depth+1];
                zeros = new byte[depth+1];
                colorMat = mMat;
                break;
            case 1: // HSV
                Imgproc.cvtColor(mMat, colorMat, Imgproc.COLOR_RGB2HSV);
                depth = 3;
                pixelvector = new byte[depth+1];
                zeros = new byte[depth+1];
                break;
            case 2: // CMYK
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

        Utils.bitmapToMat(rotatedBitmap, mMat);
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
                    if ((pixelvector[k] > upper_array[k]) || pixelvector[k] < lower_array[k]) {
                        inrange=false;
                        break;
                    }
                }
                if (!inrange) {
                    mMat.put(i, j, zeros);
                    //Log.e(TAG,"not in range");
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

        Utils.matToBitmap(mMat, rotatedBitmap);
    }


    private void imageViewTransform(int viewWidth, int viewHeight) {
        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
        //Log.e(TAG,String.valueOf(rotation));
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mBitmap.getHeight(), mBitmap.getWidth());
        RectF bufferRect2 = new RectF(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();

        Log.e(TAG, String.valueOf(rotation));

        if (Surface.ROTATION_0 == rotation || Surface.ROTATION_180 == rotation) {
            bufferRect2.offset(centerX - bufferRect2.centerX(),centerY - bufferRect2.centerY());
            matrix.setRectToRect(viewRect, bufferRect2, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / mBitmap.getHeight(),
                    (float) viewWidth / mBitmap.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
        }
        // rotation = 0
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / mBitmap.getHeight(),
                    (float) viewWidth / mBitmap.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            // 90: rotation 1, 270: rotation 3, 180: 2, 0: 0

        }
        matrix.postRotate(90*(1-rotation), centerX, centerY);
//        Matrix matrix2 = new Matrix();
//        matrix2.postRotate(90);
        rotatedBitmap = Bitmap.createBitmap(mBitmap, 0,0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
       // mImageView.setImageMatrix(matrix2);
    }

    public void onRotated() {
        Log.e(TAG,"on rotated");
        //rotatedBitmap=mBitmap;
//        imageViewTransform(mImageView.getMaxWidth(), mImageView.getMaxHeight());
//        mImageView.setImageBitmap(rotatedBitmap);
    }

    private void cvt2CMYK(Mat mMat, Mat newMat) {
        final int depth = 4;
        byte[] pixelvector = new byte[depth];
        float r_prime, g_prime, b_prime, k;
        byte c,m,y;
        final float maxval = 255;

        for (int i = 0; i<mMat.height(); i++) {
            for (int j = 0; j<mMat.width(); j++) {
                mMat.get(i,j,pixelvector);
                r_prime = (float)pixelvector[0]/maxval;
                g_prime = (float)pixelvector[1]/maxval;
                b_prime = (float)pixelvector[2]/maxval;
                k = maxval * (1-Math.max(r_prime,Math.max(g_prime,b_prime)));
                c = (byte) (maxval * (1-r_prime-k)/(1-k));
                m = (byte) (maxval * (1-g_prime-k)/(1-k));
                y = (byte) (maxval * (1-b_prime-k)/(1-k));
                pixelvector[0] = c;
                pixelvector[1] = m;
                pixelvector[2] = y;
                pixelvector[3] = (byte) (maxval*k);
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
