package com.example.kiki.impro;

import android.app.Activity;
import android.app.Dialog;
import android.app.ListFragment;
import android.content.Context;
import android.database.Cursor;
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
    private ListView itemView;
    private int currentPosition = 0;
    private static final String TAG_STILL_FRAGMENT="StillFragment";

    private ImproDbAdapter mDbAdapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.e(TAG,"onCreateView");

        mView = inflater.inflate(R.layout.filenameopener_list, container, false);
        String fullPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()+"/"+CommonResources.directory;
        currentDir = new File(fullPath+"/");
        fill(currentDir);

        mDbAdapter = new ImproDbAdapter(getActivity());

        return mView;
    }

    /** Called when the fragment is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        //setRetainInstance(true);

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
            Toast.makeText(this.getActivity(), "File Clicked: "+o.getPath(), Toast.LENGTH_SHORT).show();
            // TODO: show that file has been clicked
            CommonResources.file_to_be_opened = o.getPath();

            // Dismiss parent dialog fragment
            ((FileOpenerFragment) getTargetFragment()).dismissDialogPositive();

            mDbAdapter.open();

            Cursor settings = mDbAdapter.fetchFilter(o.getName());
            int quality = settings.getInt(settings.getColumnIndexOrThrow(mDbAdapter.KEY_QUALITY));
            String filterSettings = settings.getString(settings.getColumnIndexOrThrow(mDbAdapter.KEY_FILTERSETTINGS));
            Log.e(TAG, "Filter settings: " + filterSettings + " quality: " + String.valueOf(quality));

            mDbAdapter.close();
        }



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

        adapter = new FileArrayAdapter(FileOpenerList.this.getActivity(),R.layout.filenameopener_list,dir);
        Log.e(TAG, "adapter created");
        this.setListAdapter(adapter);
        Log.e(TAG, "adapter set");
    }
}
