package com.example.kiki.Camera;

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
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

/**
 * Created by kiki on 10.11.15.
 */
public class StillFragment extends Fragment {
    private View mView; // View corresponding to fragment -- inflated xml file
    private Bitmap mBitmap;
    private ImageView mImageView;
    private final static String TAG = "livefragment";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.e(TAG,"still on create view");
        mView = inflater.inflate(R.layout.stillfragment, container, false);
        mImageView = (ImageView) mView.findViewById(R.id.stillimageview);
        mBitmap = CommonResources.bitmap;
        applyFilter();
        if ((mBitmap!=null) && (mImageView!=null)) {
            //imageViewTransform(mImageView.getMaxWidth(),mImageView.getMaxHeight());

            mImageView.setImageBitmap(mBitmap);

        }
        return mView;
    }

    private void applyFilter(int component,int lower,int upper) {
        Mat mMat =new Mat();
        Utils.bitmapToMat(mBitmap,mMat);
        double width = mMat.size().width;
        double height =  mMat.size().height;
        // get good component

        // apply filter
        Mat newMat = new Mat();
        for (int i=0; i<height; i++){
            for (int j=0; j<width; j++) {
                if ((mMat.get(i,j)[0]>=lower) && (mMat.get(i,j)[0]<=upper)){
                    newMat.put(i, j,0.);
                }
                else {
                    newMat.put(i,j,1.);
                }

            }
        }
        // convert to bitmap.
    }

    private void imageViewTransform(int viewWidth, int viewHeight) {
        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
        Log.e(TAG,String.valueOf(rotation));
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mBitmap.getHeight(), mBitmap.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / mBitmap.getHeight(),
                    (float) viewWidth / mBitmap.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }
        mImageView.setImageMatrix(matrix);
    }


//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
        //super.onActivityCreated(savedInstanceState);

        // Check to see if we have a frame in which to embed the details
        // fragment directly in the containing UI.
//        View mainFrame = getActivity().findViewById(R.id.activity_main);
//        mDualPane = mainFrame != null && mainFrame.getVisibility() == View.VISIBLE;
//
//        if (mDualPane) {
//            // In dual-pane mode, the list view highlights the selected item.
//            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//            // Make sure our UI is in the correct state.
//            showDetails(mCurCheckPosition);
//        }
//    }

}
