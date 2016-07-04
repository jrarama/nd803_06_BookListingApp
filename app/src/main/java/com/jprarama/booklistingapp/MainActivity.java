package com.jprarama.booklistingapp;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.jprarama.booklistingapp.adapter.BookInfoAdapter;
import com.jprarama.booklistingapp.model.BookInfo;
import com.jprarama.booklistingapp.task.BookFetcher;
import com.jprarama.booklistingapp.util.JsonConsumer;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements JsonConsumer<BookInfo> {

    private static final String TAG = MainActivity.class.getName();
    private static final String DEFAULT_QUERY = "android";
    private static final String BOOK_INFO_KEY = "book_infos";
    private static final String QUERY_KEY = "query";
    private String query;

    private TextView tvQuery;
    private TextView tvNoResults;
    private ListView listView;
    private BookInfoAdapter bookInfoAdapter;
    private ArrayList<BookInfo> bookInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);
        tvQuery = (TextView) findViewById(R.id.tvQuery);
        tvNoResults = (TextView) findViewById(R.id.tvNoResults);

        if (savedInstanceState != null) {
            bookInfos = savedInstanceState.getParcelableArrayList(BOOK_INFO_KEY);
            query = savedInstanceState.getString(QUERY_KEY);

            listItems();
            Log.w(TAG, "Query: " + query);
        } else {
            Intent defaultIntent = new Intent(Intent.ACTION_SEARCH);
            defaultIntent.putExtra(SearchManager.QUERY, DEFAULT_QUERY);
            setIntent(defaultIntent);
        }

        tvQuery.setText(query);

        Intent intent = getIntent();
        if (intent != null) {
            handleIntent(intent);
            return;
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        System.out.println("Saving outState");
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(BOOK_INFO_KEY, bookInfos);
        outState.putString(QUERY_KEY, query);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "Handling new intent");
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            Log.d(TAG, "Handling new query");
            String q = intent.getStringExtra(SearchManager.QUERY);
            tvQuery.setText(q);

            if (!String.valueOf(q).equals(query)) {
                query = q;
                new BookFetcher(this).execute(q);
            }
        }
        intent.setAction(null);
        setIntent(null);
    }

    private void listItems() {
        if (bookInfos == null) {
            Log.w(TAG, "Book Infos is null");
            return;
        }

        if (bookInfos.isEmpty()) {
            tvNoResults.setText(getString(R.string.no_results));
            tvNoResults.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            return;
        }

        tvNoResults.setVisibility(View.GONE);

        if (bookInfoAdapter == null) {
            bookInfoAdapter = new BookInfoAdapter(this, R.layout.book_info_item, bookInfos);
            bookInfoAdapter.setNotifyOnChange(false);
            listView.setAdapter(bookInfoAdapter);
        } else {
            bookInfoAdapter.clear();
            bookInfoAdapter.addAll(bookInfos);
        }

        bookInfoAdapter.notifyDataSetChanged();
        listView.setVisibility(View.VISIBLE);
    }

    @Override
    public void consume(ArrayList<BookInfo> items) {
        this.bookInfos = items;
        listItems();
    }

    @Override
    public void consumeException(Exception ex) {
        listView.setVisibility(View.GONE);
        String message;
        if (ex instanceof JSONException) {
            message = getString(R.string.error_parsing_result);
        } else {
            message = getString(R.string.error_fetching_data);
        }

        tvNoResults.setVisibility(View.VISIBLE);
        tvNoResults.setText(message);
        Log.e(TAG, ex.getMessage());
    }
}
