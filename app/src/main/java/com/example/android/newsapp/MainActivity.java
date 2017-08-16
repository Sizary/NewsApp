package com.example.android.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    //URL guardianapis
    public static final String NEWS_REQUEST_URL =
            "http://content.guardianapis.com/search?q=world+games&api-key=test";
    private static final int NEWS_LOADER_ID = 1;
    private NewsAdapter mNewsAdapter;
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNewsAdapter = new NewsAdapter(this, new ArrayList<News>());
        ListView newsListView = (ListView) findViewById(R.id.list);
        newsListView.setAdapter(mNewsAdapter);

        // Reference to the empty text view in a layout and set empty view
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);

        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                News currentNews = mNewsAdapter.getItem(position);
                Uri newsUrl = Uri.parse(currentNews.getUrl());

                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUrl);

                startActivity(websiteIntent);
            }
        });

        // Reference to the ConnectivityManager to check the state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();

            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            mEmptyStateTextView.setText(R.string.state_no_internet);
        }
    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the given URL
        return new NewsLoader(this, NEWS_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {
        // Set empty state text to display "No news has been found."
        mEmptyStateTextView.setText(R.string.no_news);

        mNewsAdapter.clear();

        if (news != null && !news.isEmpty()) {
            mNewsAdapter.addAll(news);
            mEmptyStateTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        mNewsAdapter.clear();

    }
}