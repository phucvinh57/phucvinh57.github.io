package com.example.myapp;

import android.app.Activity;
import android.app.Dialog;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public final static String FILE_ALL_ITEM = "allItem.txt";
    public final static String FILE_DEFAULT = "_default.txt";
    public final static String FILE_PERSONAL = "personal.txt";
    public final static String FILE_WORK = "work.txt";
    public final static String FILE_FAVORITE = "favorite.txt";
    public final static String FILE_SHOPPING = "shopping.txt";
    public final static String FILE_COMPLETED = "completed.txt";

    public final static String FILE_MENU_NAME = "menu_name.txt";

    private ArrayList<String> allItemName = new ArrayList<>();
    private ArrayList<String> _default = new ArrayList<>();
    private ArrayList<String> personal = new ArrayList<>();
    private ArrayList<String> work = new ArrayList<>();
    private ArrayList<String> favorite = new ArrayList<>();
    private ArrayList<String> shopping = new ArrayList<>();
    private ArrayList<String> completed = new ArrayList<>();
    ArrayList<ArrayList<String>> addMenu = new ArrayList<>();
    private MyItem myAllItem;

    private ListView listItem;
    private Button addBtn;

    public ArrayList<String> menuTitles;
    private MyMenuAdapter menuAdapter;
    private ListView menuList;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    private Bundle sendData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            allItemName.addAll(readData(FILE_ALL_ITEM));
            _default.addAll(readData(FILE_DEFAULT));
            personal.addAll(readData(FILE_PERSONAL));
            work.addAll(readData(FILE_WORK));
            favorite.addAll(readData(FILE_FAVORITE));
            shopping.addAll(readData(FILE_SHOPPING));
            completed.addAll(readData(FILE_COMPLETED));
        } catch (IOException e) {
            e.printStackTrace();
        }

        menuTitles = new ArrayList<>();
        menuTitles.add(getResources().getString(R.string._default));
        menuTitles.add(getResources().getString(R.string.personal));
        menuTitles.add(getResources().getString(R.string.work));
        menuTitles.add(getResources().getString(R.string.favorite));
        menuTitles.add(getResources().getString(R.string.shopping));
        menuTitles.add(getResources().getString(R.string.completed));
        try {
            menuTitles.addAll(readData(FILE_MENU_NAME));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Toast.makeText(getApplicationContext(), String.valueOf(menuTitles.size()),Toast.LENGTH_LONG).show();

        for(int i=0;i<menuTitles.size()-6;i++){
            try {
                ArrayList<String> a= new ArrayList<>();
                a.addAll(readData(menuTitles.get(i+6)+ ".txt"));
                addMenu.add(a);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //Toast.makeText(getApplicationContext(), String.valueOf(addMenu.size()),Toast.LENGTH_LONG).show();

        /********************************* MAIN SCREEN ********************************************************/
        listItem = findViewById(R.id.list_item);

        myAllItem = new MyItem(this, this, allItemName, _default, personal, work, favorite, shopping, completed, addMenu);
        myAllItem.setFilename(FILE_ALL_ITEM, FILE_DEFAULT, FILE_PERSONAL, FILE_WORK, FILE_FAVORITE, FILE_SHOPPING, FILE_COMPLETED, menuTitles);
        addBtn = (Button) findViewById(R.id.addBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input = findViewById(R.id.input);
                String addItem = input.getText().toString();

                if(addItem.length()>0){
                    myAllItem.add(addItem);
                    saveData(FILE_ALL_ITEM, allItemName);
                    input.setText("");
                }
                else {
                    Toast.makeText(getApplicationContext(), "Please enter ...", Toast.LENGTH_LONG).show();
                }
            }
        });

        listItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),"Item...", Toast.LENGTH_SHORT).show();
            }
        });

        listItem.setAdapter(myAllItem);

        /*************************************** APP BAR **************************************************/

        drawerLayout = findViewById(R.id.activity_main_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        menuList = (ListView) findViewById(R.id.list_menu);

        menuAdapter = new MyMenuAdapter(FILE_MENU_NAME,this,menuTitles, this, addMenu, myAllItem);
        menuList.setAdapter(menuAdapter);

        final Activity current = this;
        menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String title = new String();
                sendData = new Bundle();
                if(position==5){
                    Intent intent = new Intent(current, CompletedActivity.class);
                    title = getResources().getString(R.string.completed);
                    sendData.putStringArrayList("itemCompleted", myAllItem.getCompleted());
                    sendData.putString("title", title);
                    intent.putExtras(sendData);
                    startActivityForResult(intent, 1);
                }
                else{
                    Intent intent = new Intent(current, ListActivity.class);
                    switch (position) {
                        case 0:
                            title = getResources().getString(R.string._default);
                            sendData.putStringArrayList("itemName", _default);
                            break;
                        case 1:
                            title = getResources().getString(R.string.personal);
                            sendData.putStringArrayList("itemName", personal);
                            break;
                        case 2:
                            title = getResources().getString(R.string.work);
                            sendData.putStringArrayList("itemName", work);
                            break;
                        case 3:
                            title = getResources().getString(R.string.favorite);
                            sendData.putStringArrayList("itemName", favorite);
                            break;
                        case 4:
                            title = getResources().getString(R.string.shopping);
                            sendData.putStringArrayList("itemName", shopping);
                            break;
                        default:
                            title = menuAdapter.arrayList.get(position);
                            //Toast.makeText(getApplicationContext(), String.valueOf(addMenu.size()),Toast.LENGTH_LONG).show();
                            sendData.putStringArrayList("itemName",addMenu.get(position-6));
                            break;
                    }
                    sendData.putString("title", title);
                    intent.putExtras(sendData);
                    startActivityForResult(intent, 1);
                }

            }
        });
    }



    /*For Navigation View*/
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();

    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }
    /*For Navigation View*/

    /*For ActionBar*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.addMenuBtn:
                dialogAddMenu();
                return true;
            case R.id.about:
                Intent about = new Intent(this, AboutActivity.class);
                startActivity(about);
                Toast.makeText(this, "About", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.help:
                Intent help = new Intent(this, HelpActivity.class);
                startActivity(help);
                Toast.makeText(this, "Help", Toast.LENGTH_SHORT).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void dialogAddMenu() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_menu);

        Button confirm = dialog.findViewById(R.id.confirmAddMenu);
        Button cancel = dialog.findViewById(R.id.cancel_add_menu);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText menuName = dialog.findViewById(R.id.inputNameMenu);
                String input = menuName.getText().toString();

                if(input.length()>0){
                    menuAdapter.add(input);
                    addMenu.add(new ArrayList<String>());
                    saveData(FILE_MENU_NAME, menuTitles);
                    for(int i=6;i<menuTitles.size();i++){
                        saveData( menuTitles.get(i)+".txt", addMenu.get(i-6));
                    }
                    menuName.setText("");
                }
                else {
                    Toast.makeText(getApplicationContext(), "Please enter a name ...", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    /*For ActionBar*/

    /*Get data from ListActivity*/
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String title = data.getStringExtra("title");

            ArrayList<String> add =new ArrayList<>();
            ArrayList<String> rmv = new ArrayList<>();
            ArrayList<String> c = new ArrayList<>();
            ArrayList<String> rc = new ArrayList<>();
            if(data.getStringArrayListExtra("insert")!=null)
                add = data.getStringArrayListExtra("insert");
            if(data.getStringArrayListExtra("del")!=null)
                rmv = data.getStringArrayListExtra("del");
            if(data.getStringArrayListExtra("completed")!=null)
                c = data.getStringArrayListExtra("completed");
            if(data.getStringArrayListExtra("remove_completed")!=null)
                rc = data.getStringArrayListExtra("remove_completed");

            if(title.equals(getResources().getString(R.string._default))){
                _default.addAll(add);
            }
            else if(title.equals(getResources().getString(R.string.personal))) {
                personal.addAll(add);
            }
            else if(title.equals(getResources().getString(R.string.work))){
                work.addAll(add);
            }
            else if(title.equals(getResources().getString(R.string.favorite))){
                favorite.addAll(add);
            }
            else if(title.equals(getResources().getString(R.string.shopping))){
                shopping.addAll(add);
            }
            else {
                for(int i=0;i<addMenu.size();i++){
                    if(title.equals(menuAdapter.arrayList.get(i+6))){
                        addMenu.get(i).addAll(add);
                        break;
                    }
                }
            }
            myAllItem.addCompleted(c);
            myAllItem.removeCompleted(rc);

            myAllItem.addAll(add);
            myAllItem.remove(rmv);
            saveData(FILE_COMPLETED, completed);
            saveData(FILE_DEFAULT, _default);
            saveData(FILE_PERSONAL, personal);
            saveData(FILE_WORK, work);
            saveData(FILE_FAVORITE, favorite);
            saveData(FILE_SHOPPING, shopping);
            saveData(FILE_ALL_ITEM, allItemName);

            saveData(FILE_MENU_NAME, menuTitles);
            for(int i=6;i<menuTitles.size();i++){
                saveData( menuTitles.get(i)+".txt", addMenu.get(i-6));
            }

        }
    }

    public void saveData(String filename, ArrayList<String> data) {
        FileOutputStream output = null;
        try {
            output = openFileOutput(filename, MODE_PRIVATE);
            if(filename.equals(FILE_MENU_NAME)){
                for(int i=6;i<data.size();i++){
                    output.write((data.get(i)+'\n').getBytes());
                }
            }
            else {
                for(int i=0;i<data.size();i++){
                    output.write((data.get(i)+'\n').getBytes());
                }
            }
            output.close();

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public ArrayList<String> readData(String filename) throws IOException {
        ArrayList<String> data = new ArrayList<>();
        FileInputStream in = openFileInput(filename);
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
}