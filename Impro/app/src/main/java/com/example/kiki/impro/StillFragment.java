package com.example.kiki.impro;

import android.app.Fragment;
import android.app.FragmentTransaction;
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
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

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
//          applyFilter(0,0,200);
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

    private void applyFilter(int component, int lower, int upper) {
        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();
        Mat mMat = new Mat(height,width,CvType.CV_8UC4,new Scalar(0));
        Utils.bitmapToMat(mBitmap,mMat);
        // all pixels above "upper" set to 0, other pixels untouched.
        Imgproc.threshold(mMat,mMat,upper,0,Imgproc.THRESH_TOZERO_INV);
        // all pixels below "lower" set to 0, other pixels set to 1.
        Imgproc.threshold(mMat, mMat, lower, 255, Imgproc.THRESH_BINARY);
        Utils.matToBitmap(mMat, mBitmap);
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

}
