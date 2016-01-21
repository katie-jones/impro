package com.example.kiki.impro;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.app.ListFragment;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import xdroid.toaster.Toaster;

/**
 * Created by kiki on 2016-01-15.
 *
 * from
 * http://www.dreamincode.net/forums/topic/190013-creating-simple-file-chooser/
 */
public class FileOpenerFragment extends ListFragment {
    static private String TAG="FileOpener";
    private File currentDir;
    static private FileArrayAdapter adapter;
    private View mView;
    private static final String TAG_STILL_FRAGMENT="StillFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.e(TAG,"onCreateView");

        mView = inflater.inflate(R.layout.fileopenerfragment, container, false);

        String fullPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath()+"/"+CommonResources.directory;
        currentDir = new File(fullPath+"/");
        fill(currentDir);

        return mView;
    }

    /** Called when the fragment is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e(TAG,"onCreate");
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);
        Option o = adapter.getItem(position);
        if(o.getData().equalsIgnoreCase("folder")||o.getData().equalsIgnoreCase("parent directory")){
            currentDir = new File(o.getPath());
            fill(currentDir);
        }
        else
        {
            onFileClick(o);
        }
    }

    private void onFileClick(Option o)
    {

        Toast.makeText(this.getActivity(), "File Clicked: "+o.getPath(), Toast.LENGTH_SHORT).show();

        // Load bitmap
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(o.getPath(), options);

        // Save in common resources
        CommonResources.bitmap = bitmap;

        // Show picture in Stillfragment.
        StillFragment mStillFragment = (StillFragment) getFragmentManager().findFragmentByTag(TAG_STILL_FRAGMENT);
        if (mStillFragment==null)
            mStillFragment = new StillFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(this.getId(), mStillFragment, TAG_STILL_FRAGMENT);
        transaction.commit();
    }

    private void fill(File f)
    {
        Log.e(TAG,"fill");
        File[]dirs = f.listFiles();
        List<Option> dir = new ArrayList<Option>();
        List<Option>fls = new ArrayList<Option>();
        try{
            for(File ff: dirs)
            {
                if(ff.isDirectory())
                    dir.add(new Option(ff.getName(),"Folder",ff.getAbsolutePath()));
                else
                {
                    fls.add(new Option(ff.getName(),"File Size: "+ff.length(),ff.getAbsolutePath()));
                }
            }
        }catch(Exception e)
        {
            Log.e(TAG, e.getMessage());
        }
        Log.e(TAG, "sort and show");
        Collections.sort(dir);
        Collections.sort(fls);
        dir.addAll(fls);
        if(!f.getName().equalsIgnoreCase(CommonResources.directory))
            dir.add(0,new Option("..", "Parent Directory",f.getParent()));

        adapter = new FileArrayAdapter(FileOpenerFragment.this.getActivity(),R.layout.fileopenerfragment,dir);
        Log.e(TAG, "adapter created");
        this.setListAdapter(adapter);
        Log.e(TAG, "adapter set");
    }
}
