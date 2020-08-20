package com.example.myapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MyMenuAdapter extends BaseAdapter {
    ArrayList<String> arrayList;
    Activity activity;

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        View view = convertView;
        view = layoutInflater.inflate(R.layout.item_menu, null);

        TextView textView = (TextView) view.findViewById(R.id.menu_name);
        ImageButton rmvMenuBtn = (ImageButton) view.findViewById(R.id.rmvMenuBtn);

        textView.setText(arrayList.get(position));
        rmvMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position<=4)
                    Toast.makeText(v.getContext(), "Cannot delete a default menu", Toast.LENGTH_SHORT).show();
                else {
                    arrayList.remove(position);
                    notifyDataSetChanged();
                }
            }
        });

        return view;
    }

    public MyMenuAdapter(Activity activity, ArrayList<String> arrayList){
        this.arrayList=arrayList;
        this.activity=activity;
    }

    public void add(String addItem){
        arrayList.add(addItem);
        notifyDataSetChanged();
    }
}
