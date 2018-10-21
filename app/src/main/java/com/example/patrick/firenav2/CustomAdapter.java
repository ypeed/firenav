package com.example.patrick.firenav2;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {

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
        return 3;
    }

    @Override
    public Object getItem(int position) {
        return naslovi.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        itemView = (itemView == null) ? inflater.inflate(R.layout.list_item, null): itemView;
        TextView naslov = itemView.findViewById(R.id.customText);
        naslov.setText(naslovi.get(position).toString());
        return  itemView;
    }
}
