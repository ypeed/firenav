package com.example.patrick.firenav20;

import android.app.Activity;
import android.content.Context;
import android.graphics.ColorSpace;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Patrick on 26. 12. 2017.
 */

public class CustomAdapter extends BaseAdapter{

    Activity context;
    ArrayList<String> naslovi;
    private static LayoutInflater inflater = null;

    public CustomAdapter(Activity context, ArrayList<String> naslovi) {
        this.context = context;
        this.naslovi = naslovi;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Object getItem(int i) {
        return naslovi.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View itemView = view;
        itemView = (itemView == null) ? inflater.inflate(R.layout.list_white_item, null): itemView;
        TextView naslov = itemView.findViewById(R.id.customText);
        naslov.setText(naslovi.get(i).toString());
        return  itemView;
    }

}
