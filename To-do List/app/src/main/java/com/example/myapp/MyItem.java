package com.example.myapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class MyItem extends BaseAdapter {
    private String file_allItem, file_default, file_personal, file_work, file_favorite, file_shopping, file_completed;
    Context context;

    ArrayList<String> arrayList;
    ArrayList<String> a1;
    ArrayList<String> a2;
    ArrayList<String> a3;
    ArrayList<String> a4;
    ArrayList<String> a5;

    public ArrayList<String> completed = new ArrayList<>();

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
    public View getView(final int position, final View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        View view = convertView;
        view = layoutInflater.inflate(R.layout.item, null);

        TextView textView = (TextView) view.findViewById(R.id.item_name);
        final ImageButton rmvBtn = view.findViewById(R.id.rmvButton);
        CheckBox cmp = (CheckBox) view.findViewById(R.id.completed);

        textView.setText(arrayList.get(position));
        rmvBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rmv = arrayList.get(position);
                a1.remove(rmv);
                a2.remove(rmv);
                a3.remove(rmv);
                a4.remove(rmv);
                a5.remove(rmv);
                arrayList.remove(rmv);
                try {
                    removeDataInFile(file_default, context, rmv);
                    removeDataInFile(file_personal, context, rmv);
                    removeDataInFile(file_work, context, rmv);
                    removeDataInFile(file_favorite, context, rmv);
                    removeDataInFile(file_shopping, context, rmv);
                    removeDataInFile(file_allItem, context, rmv);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                notifyDataSetChanged();
            }
        });
        cmp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cl = arrayList.get(position);
                if(a1.contains(cl)) a1.remove(cl);
                if(a2.contains(cl)) a2.remove(cl);
                if(a3.contains(cl)) a3.remove(cl);
                if(a4.contains(cl)) a4.remove(cl);
                a5.remove(cl);
                arrayList.remove(cl);
                try {
                    removeDataInFile(file_default, context, cl);
                    removeDataInFile(file_personal, context, cl);
                    removeDataInFile(file_work, context, cl);
                    removeDataInFile(file_favorite, context, cl);
                    removeDataInFile(file_shopping, context, cl);
                    removeDataInFile(file_allItem, context, cl);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                completed.add(cl);
                saveData(file_completed, context, completed);
                notifyDataSetChanged();
            }
        });

        return view;
    }

    public MyItem(Activity activity, Context context, ArrayList<String> arrayList, ArrayList<String> a1, ArrayList<String> a2, ArrayList<String> a3,  ArrayList<String> a4,  ArrayList<String> a5, ArrayList<String> completed){
        this.arrayList=arrayList;
        this.context=context;
        this.a1=a1;
        this.a2=a2;
        this.a3=a3;
        this.a4=a4;
        this.a5=a5;
        this.completed=completed;
        this.activity=activity;
    }

    public void setFilename(String f1, String f2, String f3, String f4, String f5, String f6, String f7){
        this.file_allItem=f1;
        this.file_default=f2;
        this.file_personal=f3;
        this.file_work=f4;
        this.file_favorite=f5;
        this.file_shopping=f6;
        this.file_completed=f7;
    }

    public void add(String addItem){
        arrayList.add(addItem);
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<String> arrayList){
        this.arrayList.addAll(arrayList);
        notifyDataSetChanged();
    }

    public ArrayList<String> getCompleted() {
        return this.completed;
    }

    public void remove(ArrayList<String> a){
        this.arrayList.removeAll(a);
        this.a1.removeAll(a);
        this.a2.removeAll(a);
        this.a3.removeAll(a);
        this.a4.removeAll(a);
        this.a5.removeAll(a);
        notifyDataSetChanged();
    }
    public void addCompleted(ArrayList<String> completed){
        this.completed.addAll(completed);
        notifyDataSetChanged();
    }
    public void removeCompleted(ArrayList<String> remove_completed){
        this.completed.removeAll(remove_completed);
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
