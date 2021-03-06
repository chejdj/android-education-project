package com.example.osnavermovie;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.begentgroup.xmlparser.XMLParser;
import com.example.osnavermovie.NetworkManager.OnResultListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;


public class MainActivity extends ActionBarActivity {
	PullToRefreshListView refreshView;
	ListView listView;
	MyAdapter mAdapter;
	EditText keywordView;
	Handler mHandler = new Handler(Looper.getMainLooper());
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        refreshView = (PullToRefreshListView)findViewById(R.id.listView1);
        refreshView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> rv) {
				mHandler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						refreshView.onRefreshComplete();
					}
				}, 2000);
			}
		});
        
//        refreshView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
//
//			@Override
//			public void onLastItemVisible() {
//				String keyword = mAdapter.getKeyword();
//				if (!TextUtils.isEmpty(keyword)) {
//					int start = mAdapter.getStart();
//					if (start != -1) {
//						NetworkManager.getInstance().getNaverMovies(MainActivity.this, keyword, start, 20, new OnResultListener<Movies>() {
//
//							@Override
//							public void onSuccess(Movies result) {
//								mAdapter.addAll(result.items);
//							}
//
//							@Override
//							public void onFail(int code) {
//								// TODO Auto-generated method stub
//								
//							}
//							
//						});
//					}
//				}
//			}
//		});
        
        listView = refreshView.getRefreshableView();
        mAdapter = new MyAdapter();
        listView.setAdapter(mAdapter);
        keywordView = (EditText)findViewById(R.id.edit_keyword);
        Button btn = (Button)findViewById(R.id.btn_search);
        btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final String keyword = keywordView.getText().toString();
				if (!TextUtils.isEmpty(keyword)) {
//					new MovieTask().execute(keyword);
					NetworkManager.getInstance().getNaverMovies(MainActivity.this, keyword, 1, 20, new OnResultListener<Movies>() {
						
						@Override
						public void onSuccess(Movies result) {
							mAdapter.clear();
							mAdapter.addAll(result.items);
							mAdapter.setKeyword(keyword);
							mAdapter.setTotal(result.total);
						}
						
						@Override
						public void onFail(int code) {
							Toast.makeText(MainActivity.this, "fail", Toast.LENGTH_SHORT).show();
						}
					});
				}
			}
		});
    }

    class MovieTask extends AsyncTask<String, Integer, Movies> {
    	@Override
    	protected Movies doInBackground(String... params) {
    		String keyword = params[0];
    		try {
				String urlString = String.format("http://openapi.naver.com/search?key=55f1e342c5bce1cac340ebb6032c7d9a&query=%s&display=10&start=1&target=movie", URLEncoder.encode(keyword, "utf-8"));
				URL url = new URL(urlString);
				HttpURLConnection conn = (HttpURLConnection)url.openConnection();
				int code = conn.getResponseCode();
				if (code == HttpURLConnection.HTTP_OK) {
					XMLParser parser = new XMLParser();
					Movies movies = parser.fromXml(conn.getInputStream(), "channel", Movies.class);
					return movies;
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		return null;
    	}
    	
    	@Override
    	protected void onPostExecute(Movies result) {
    		super.onPostExecute(result);
    		if (result != null) {
    			mAdapter.addAll(result.items);
    		} else {
    			Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
    		}
    	}
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
