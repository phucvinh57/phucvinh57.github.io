package com.example.myapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class CompletedActivity extends AppCompatActivity {
    private ArrayList<String> itemName;
    private ListView listItem;
    private CompletedItem completedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed);

        Intent intent = getIntent();
        Bundle receiveData = intent.getExtras();

        String title = receiveData.getString("title");
        Toast.makeText(getApplicationContext(), title, Toast.LENGTH_SHORT).show();
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        listItem = findViewById(R.id.list_item_completed);
        itemName = new ArrayList<>();
        completedItem = new CompletedItem(this, itemName);
        completedItem.add(receiveData.getStringArrayList("itemCompleted"));
        listItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),"Item...", Toast.LENGTH_SHORT).show();
            }
        });

        listItem.setAdapter(completedItem);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                String title = getIntent().getExtras().getString("title");

                Intent intent = new Intent();
                Bundle returnResult = new Bundle();
                returnResult.putStringArrayList("remove_completed", completedItem.getRemove());
                returnResult.putString("title", title);
                intent.putExtras(returnResult);
                setResult(RESULT_OK, intent);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}