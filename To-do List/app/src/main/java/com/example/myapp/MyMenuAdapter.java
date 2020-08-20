package com.example.myapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class MyMenuAdapter extends BaseAdapter {
    ArrayList<String> arrayList;
    Activity activity;
    Context context;
    String filename;
    ArrayList<ArrayList<String>> addMenu;
    MyItem myItem;

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
                if(position<=5)
                    Toast.makeText(v.getContext(), "Cannot delete a default menu", Toast.LENGTH_SHORT).show();
                else {
                    String rmv = arrayList.get(position);
                    ArrayList<String> temp = new ArrayList<>();
                    try {
                        temp.addAll(readData(context, rmv+".txt"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    myItem.arrayList.removeAll(temp);
                    myItem.notifyDataSetChanged();

                    try {
                        File file = new File(rmv+".txt");
                        file.delete();
                        removeDataInFile(filename, context, rmv);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    arrayList.remove(rmv);
                    addMenu.remove(position - 6);
                    notifyDataSetChanged();
                }
            }
        });

        return view;
    }

    public MyMenuAdapter(String filename, Activity activity, ArrayList<String> arrayList, Context context, ArrayList<ArrayList<String>> addMenu, MyItem myItem){
        this.arrayList=arrayList;
        this.context=context;
        this.filename=filename;
        this.activity=activity;
        this.addMenu=addMenu;
        this.myItem=myItem;
    }

    public void add(String addItem){
        arrayList.add(addItem);
        notifyDataSetChanged();
    }

    public void saveData(String filename, Context context, ArrayList<String> data) {
        FileOutputStream output = null;
        try {
            output = context.openFileOutput(filename, MODE_PRIVATE);
            for(int i=0;i<data.size();i++){
                output.write((data.get(i)+'\n').getBytes());
            }
            output.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public ArrayList<String> readData(Context context, String filename) throws IOException {
        ArrayList<String> data = new ArrayList<>();
        FileInputStream in = context.openFileInput(filename);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line = br.readLine();
        while (line!=null){
            data.add(line);
            line = br.readLine();
        }
        br.close();
        in.close();
        return data;
    }

    public void removeDataInFile(String filename, Context context, String lineToRemove) throws IOException {
        ArrayList<String> dataOfFile = new ArrayList<>(readData(context, filename));
        dataOfFile.remove(lineToRemove);
        saveData(filename, context, dataOfFile);
    }
}
