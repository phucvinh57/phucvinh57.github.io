package com.example.myapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {
    private ArrayList<String> itemName;
    private ListView listItem;
    private BufferMemory bufferMemory;
    private Button addBtn;

    private ArrayList<String> insert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Intent intent = getIntent();
        Bundle receiveData = intent.getExtras();

        String title = receiveData.getString("title");
        getSupportActionBar().setTitle(title);
        Toast.makeText(getApplicationContext(), title, Toast.LENGTH_SHORT).show();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        itemName = new ArrayList<>(receiveData.getStringArrayList("itemName"));
        bufferMemory = new BufferMemory(this, itemName);

        insert = new ArrayList<>();

        addBtn = (Button) findViewById(R.id.addBtn_2);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input = findViewById(R.id.input_2);
                String addItem = input.getText().toString();

                if(addItem.length()>0){
                    insert.add(addItem);
                    bufferMemory.add(addItem);
                    input.setText("");
                }
                else {
                    Toast.makeText(getApplicationContext(), "Please enter ...", Toast.LENGTH_LONG).show();
                }
            }
        });

        listItem = findViewById(R.id.list_item_2);
        listItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),"Item...", Toast.LENGTH_SHORT).show();
            }
        });

        listItem.setAdapter(bufferMemory);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                String title = getIntent().getExtras().getString("title");

                Intent intent = new Intent();
                Bundle returnResult = new Bundle();

                returnResult.putStringArrayList("insert", insert);
                returnResult.putString("title", title);
                returnResult.putStringArrayList("del", bufferMemory.getDel());
                returnResult.putStringArrayList("completed", bufferMemory.getCompleted());

                intent.putExtras(returnResult);
                setResult(RESULT_OK, intent);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}