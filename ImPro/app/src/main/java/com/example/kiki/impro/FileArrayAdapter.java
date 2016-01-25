package com.example.kiki.impro;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by kiki on 21.01.16.
 *
 * from
 * http://www.dreamincode.net/forums/topic/190013-creating-simple-file-chooser/
 *
 * Adapter for creating list of files in Picture folder
 */
public class FileArrayAdapter extends ArrayAdapter<Option> {
    static private String TAG = "FileArrayAdapter";
    private Context c;
    private int id;
    private List<Option> items;

    public FileArrayAdapter(Context context, int textViewResourceId,
                            List<Option> objects) {
        super(context, textViewResourceId, objects);

        Log.e(TAG, "constructing");
        c = context;
        id = textViewResourceId;
        items = objects;
    }

    public Option getItem(int i_new)
    {
        return items.get(i_new);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(id, null);
        }
        final Option o = items.get(position);
        if (o != null) {

            TextView t1 = (TextView) v.findViewById(R.id.TextView01);
            TextView t2 = (TextView) v.findViewById(R.id.TextView02);

            if(t1!=null)
                t1.setText(o.getName());

            if(t2!=null)
                t2.setText(o.getData());

        }
        return v;
    }




}


