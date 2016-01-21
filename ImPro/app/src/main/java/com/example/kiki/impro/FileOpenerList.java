package com.example.kiki.impro;

import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by kiki on 2016-01-15.
 *
 * from
 * http://www.dreamincode.net/forums/topic/190013-creating-simple-file-chooser/
 */
public class FileOpenerList extends ListFragment {
    static private String TAG="FileOpener";
    private File currentDir;
    static private FileArrayAdapter adapter;
    private View mView;
    private ListView mListView;
    private static final String TAG_STILL_FRAGMENT="StillFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.e(TAG,"onCreateView");

        mView = inflater.inflate(R.layout.fileopenerlist, container, false);
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
        // TODO: show that file has been clicked
        CommonResources.file_to_be_opened = o.getPath();

        // Load bitmap


        // Show picture in Stillfragment.
//        StillFragment mStillFragment = (StillFragment) getFragmentManager().findFragmentByTag(TAG_STILL_FRAGMENT);
//        if (mStillFragment==null)
//            mStillFragment = new StillFragment();
//        FragmentTransaction transaction = getFragmentManager().beginTransaction();
//        transaction.replace(this.getId(), mStillFragment, TAG_STILL_FRAGMENT);
//        transaction.commit();
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

        adapter = new FileArrayAdapter(FileOpenerList.this.getActivity(),R.layout.fileopenerlist,dir);
        Log.e(TAG, "adapter created");
        this.setListAdapter(adapter);
        Log.e(TAG, "adapter set");
    }
}
