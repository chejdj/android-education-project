package com.example.samples1contact;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;


public class MainActivity extends ActionBarActivity {

	ListView listView;
	EditText keywordView;
	SimpleCursorAdapter mAdapter;
	
	String[] columns = { ContactsContract.Contacts._ID, Contacts.DISPLAY_NAME};
	String selection = "((" + Contacts.DISPLAY_NAME + " NOT NULL) AND (" + Contacts.DISPLAY_NAME + " != ''))";
	String orderBy = Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView)findViewById(R.id.listView1);
        keywordView = (EditText)findViewById(R.id.editText1);
        
        keywordView.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String keyword = s.toString();
				if (keyword != null && !keyword.equals("")) {
					search(keyword);
				} else {
					searchAll();
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
        String[] from = { Contacts.DISPLAY_NAME };
        int[] to = {android.R.id.text1 };
        mAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null, from, to, 0);
        listView.setAdapter(mAdapter);
        searchAll();
    }
    
    private void search(String keyword) {
    	Uri uri = Uri.withAppendedPath(Contacts.CONTENT_FILTER_URI, Uri.encode(keyword));
        Cursor c = getContentResolver().query(uri, columns, selection, null, orderBy);
        mAdapter.changeCursor(c);
    }
    
    private void searchAll() {
        Cursor c = getContentResolver().query(Contacts.CONTENT_URI, columns, selection, null, orderBy);
        mAdapter.changeCursor(c);
    }
    
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
