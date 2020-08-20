package com.example.myapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class CompletedItem extends BaseAdapter {
    private ArrayList<String> arrayList;
    private ArrayList<String> remove = new ArrayList<>();
    private Activity activity;
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
        view = layoutInflater.inflate(R.layout.item_completed, null);

        TextView textView = view.findViewById(R.id.item_name_completed);
        ImageButton rmvBtnCompleted = view.findViewById(R.id.rmvButton_completed);

        textView.setText(arrayList.get(position));
        rmvBtnCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String a = arrayList.get(position);
                remove.add(a);
                arrayList.remove(a);
                notifyDataSetChanged();
            }
        });
        return view;
    }

    public CompletedItem(Activity activity, ArrayList<String> arrayList){
        this.arrayList=arrayList;
        this.activity=activity;
    }

    public void add(ArrayList<String> a){
        this.arrayList.addAll(a);
        notifyDataSetChanged();
    }
    public ArrayList<String> getRemove(){
        return this.remove;
    }
}
