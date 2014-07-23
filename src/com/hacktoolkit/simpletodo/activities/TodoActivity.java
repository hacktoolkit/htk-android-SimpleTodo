package com.hacktoolkit.simpletodo.activities;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.hacktoolkit.android.utils.FileUtils;
import com.hacktoolkit.android.utils.HTKUtils;
import com.hacktoolkit.simpletodo.R;
import com.hacktoolkit.simpletodo.constants.AppConstants;

public class TodoActivity extends Activity {
	ArrayList<String> items;
	ArrayAdapter<String> itemsAdapter;
	ListView lvItems;
	EditText etNewItem;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo);
		lvItems = (ListView) findViewById(R.id.lvItems);

		readItemsFromFile();
		itemsAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, items);
		lvItems.setAdapter(itemsAdapter);
		
		// enter key submits item
		etNewItem = (EditText) findViewById(R.id.etNewItem);
		etNewItem.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_NULL) {
					addTodoItem(view);
				}
				return true;
			}

		});

		setupListViewListeners();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.todo_activity, menu);
		return true;
	}

	public void addTodoItem(View view) {
		etNewItem = (EditText) findViewById(R.id.etNewItem);
		String value = etNewItem.getText().toString();
		if (!value.equals(AppConstants.EMPTY_STRING)) {
			itemsAdapter.add(value);
			itemsAdapter.notifyDataSetChanged();
			etNewItem.setText(AppConstants.EMPTY_STRING);
			lvItems.setSelection(itemsAdapter.getCount() - 1);
			storeItemsToFile();
			HTKUtils.hideSoftKeyboard(this, view);
			Toast.makeText(getBaseContext(), "Task Added!",
					Toast.LENGTH_SHORT).show();
		}
	}
	
	private void setupListViewListeners() {
		lvItems.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> aView,
						View item, int position, long rowId) {
				items.remove(position);
				itemsAdapter.notifyDataSetChanged();
				storeItemsToFile();
				return true;
			}
		});

		lvItems.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(	AdapterView<?> aView,
					View view, int position, long rowId) {
				String clickedItem = items.get(position);
				Toast.makeText(
						getApplicationContext(),
						"Clicked ListItem Number " + position + ", " + clickedItem,
						Toast.LENGTH_LONG
						).show();
				}
		});
	}
	
	private void readItemsFromFile() {
		try {
			items = FileUtils.readLines(this, AppConstants.SAVE_FILENAME);
		} catch (IOException e) {
			items = new ArrayList<String>();
			e.printStackTrace();
		}
	}
	
	private void storeItemsToFile() {
		try {
			int linesWritten = FileUtils.writeLines(this, AppConstants.SAVE_FILENAME, items);
			if (linesWritten != items.size()) {
				throw new IOException("Not all items stored");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
