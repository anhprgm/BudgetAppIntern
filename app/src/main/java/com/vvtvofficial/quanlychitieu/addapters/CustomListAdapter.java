package com.vvtvofficial.quanlychitieu.addapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vvtvofficial.quanlychitieu.DataBase.Model;
import com.vvtvofficial.quanlychitieu.R;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;

public class CustomListAdapter extends BaseAdapter {
    Context context;
    ArrayList<Model> modelList;
    public CustomListAdapter(Context context, ArrayList<Model> modelList) {
        this.context = context;
        this.modelList = modelList;
    }
    @Override
    public int getCount() {
        return modelList.size();
    }

    @Override
    public Object getItem(int position) {
        return modelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=(LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.Itemname);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);

        Model m = modelList.get(position);
        txtTitle.setText(m.getName());
        if(m != null && m.getImage() !=null) imageView.setImageBitmap(m.getImage());

        return rowView;

    };
}
