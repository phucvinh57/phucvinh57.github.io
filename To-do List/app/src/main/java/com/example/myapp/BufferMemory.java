package com.example.myapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class BufferMemory extends BaseAdapter {
    ArrayList<String> arrayList;
    ArrayList<String> del = new ArrayList<>();
    ArrayList<String> completed = new ArrayList<>();
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
        view = layoutInflater.inflate(R.layout.item_buffer_memory, null);

        TextView textView = (TextView) view.findViewById(R.id.item_name_buffer);
        final ImageButton rmvBtn = view.findViewById(R.id.rmvButton_buffer);
        CheckBox cmp = (CheckBox) view.findViewById(R.id.completed_buffer);

        textView.setText(arrayList.get(position));
        rmvBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rmv = arrayList.get(position);
                arrayList.remove(rmv);
                del.add(rmv);
                notifyDataSetChanged();
            }
        });
        cmp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cl = arrayList.get(position);
                arrayList.remove(cl);
                del.add(cl);
                completed.add(cl);
                notifyDataSetChanged();
            }
        });
        return view;
    }

    public BufferMemory(Activity activity, ArrayList<String>arrayList){
        this.arrayList=arrayList;
        this.activity=activity;
    }

    public void add(String a){
        arrayList.add(a);
        notifyDataSetChanged();
    }

    public ArrayList<String> getDel(){
        return this.del;
    }

    public ArrayList<String> getCompleted() {
        return completed;
    }
}
