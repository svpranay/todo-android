package com.sowdaboina.todo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class TodoActivity extends ActionBarActivity {

    ArrayAdapter itemsAdapter;
    ListView lvItems;
    ArrayList<String> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);
        lvItems = (ListView) findViewById(R.id.lvItems);
        items = new ArrayList<>();
        readItems();
        itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        lvItems.setAdapter(itemsAdapter);
        setUpItemListViewListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_todo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onAddItem(View v) {
        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
        String itemText = etNewItem.getText().toString();
        itemsAdapter.add(itemText);
        writeItems();
        etNewItem.setText("");
    }

    int REQUEST_CODE = 20;
    public void setUpItemListViewListener() {
        lvItems.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {

                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        items.remove(position);
                        itemsAdapter.notifyDataSetChanged();
                        writeItems();
                        return true;
                    }
                }
        );

        lvItems.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent i = new Intent(TodoActivity.this, EditItemActivity.class);
                        i.putExtra("value", items.get(position));
                        i.putExtra("position", position);
                        startActivityForResult(i, REQUEST_CODE);
                    }
                }
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            // Extract name value from result extras
            String value = data.getExtras().getString("value");
            int code = data.getExtras().getInt("code", 0);
            int position = data.getExtras().getInt("position", 0);
            itemsAdapter.remove(itemsAdapter.getItem(position));
            itemsAdapter.insert(value, position);
            itemsAdapter.notifyDataSetChanged();
            writeItems();
            Toast.makeText(this, "New value : " + value + "@" + position, Toast.LENGTH_SHORT).show();
        }
    }

    public void readItems() {
        File filesDir = getFilesDir();
        File todofile = new File(filesDir, "todo.txt");
        try {
            items = new ArrayList<String>(FileUtils.readLines(todofile));
        } catch (IOException e) {
            items = new ArrayList<String>();
        }
    }

    public void writeItems() {
        File filesDir = getFilesDir();
        File todofile = new File(filesDir, "todo.txt");
        try {
            FileUtils.writeLines(todofile, items);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
