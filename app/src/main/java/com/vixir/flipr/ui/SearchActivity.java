package com.vixir.flipr.ui;

import android.app.SearchManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.vixir.flipr.R;
import com.vixir.flipr.data.PhotoShot;
import com.vixir.flipr.data.SearchDataManager;
import com.vixir.flipr.ui.RecyclerView.EndlessRecyclerViewScrollListener;

import java.util.List;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity {
    @BindView(R.id.searchback)
    ImageButton searchBack;
    @BindView(R.id.searchback_container)
    ViewGroup searchBackContainer;
    @BindView(R.id.search_view)
    SearchView searchView;
    @BindView(R.id.search_background)
    View searchBackground;
    @BindView(android.R.id.empty)
    ProgressBar progress;
    @BindView(R.id.container)
    ViewGroup container;
    @BindView(R.id.search_toolbar)
    ViewGroup searchToolbar;
    @BindView(R.id.results_container)
    ViewGroup resultsContainer;
    @BindView(R.id.scrim)
    View scrim;
    @BindView(R.id.results_scrim)
    View resultsScrim;
    @BindView(R.id.search_results)
    RecyclerView grid;
    @BindInt(R.integer.num_columns) // can later specify in layout as per screen width
            int columns;
    private EndlessRecyclerViewScrollListener mScrollListener;
    GridLayoutManager layoutManager;
    SearchDataManager dataManager;
    FeedAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        setupSearchView();


        dataManager = new SearchDataManager(getApplicationContext()) {
            @Override
            public void onDataLoaded(List<? extends PhotoShot> data) {
                adapter.addAndResort(data);
            }
        };
        adapter = new FeedAdapter(this, dataManager);
        layoutManager = new GridLayoutManager(this, columns);
        grid.setLayoutManager(layoutManager);
        grid.addItemDecoration(new SimpleDividerItemDecoration(8, 8));
        grid.setHasFixedSize(true);
        grid.setAdapter(adapter);


        grid.setHasFixedSize(true);
        onNewIntent(getIntent());
    }

    private void setupSearchView() {
        searchBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        searchView.setImeOptions(searchView.getImeOptions() | EditorInfo.IME_ACTION_SEARCH | EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_FLAG_NO_FULLSCREEN);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                dataManager.loadDataSource(0, query);

                mScrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
                    @Override
                    public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                        // Triggered only when new data needs to be appended to the list
                        // Add whatever code is needed to append new items to the bottom of the list
                        dataManager.loadDataSource(page, query);
                    }
                };
                grid.addOnScrollListener(mScrollListener);
                searchFor(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (TextUtils.isEmpty(query)) {
                    clearResults();
                }
                return true;
            }
        });

    }

    void searchFor(String query) {
        clearResults();
//        progress.setVisibility(View.VISIBLE);
//        ImeUtils.hideIme(searchView);
        searchView.clearFocus();
        grid.setVisibility(View.VISIBLE);
//        dataManager.searchFor(query);
    }

    void clearResults() {
//        adapter.clear();
//        dataManager.clear();
        grid.setVisibility(View.GONE);
        progress.setVisibility(View.GONE);
        resultsScrim.setVisibility(View.GONE);
    }
}
