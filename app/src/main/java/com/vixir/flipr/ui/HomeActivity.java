package com.vixir.flipr.ui;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toolbar;

import com.vixir.flipr.R;
import com.vixir.flipr.data.DataManager;
import com.vixir.flipr.data.PhotoShot;
import com.vixir.flipr.ui.RecyclerView.EndlessRecyclerViewScrollListener;

import java.util.List;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends Activity {
    private static final int RC_SEARCH = 1;
    @BindView(R.id.drawer)
    DrawerLayout drawer;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.grid)
    RecyclerView grid;
    @Nullable

    @BindView(R.id.no_connection)
    ImageView noConnection;

    @BindView(android.R.id.empty)
    ProgressBar loading;

    GridLayoutManager layoutManager;
    @BindInt(R.integer.num_columns) // can later specify in layout as per screen width
            int columns;
    boolean connected = true;
    private boolean monitoringConnectivity = false;
    FeedAdapter adapter;
    DataManager dataManager;
    private MyFlipAnimator mChangeAnimator = new MyFlipAnimator();
    private EndlessRecyclerViewScrollListener mScrollListener;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                View searchMenuView = toolbar.findViewById(R.id.menu_search);
                startActivityForResult(new Intent(this, SearchActivity.class), RC_SEARCH);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        drawer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        setActionBar(toolbar);
        dataManager = new DataManager(this) {
            @Override
            public void onDataLoaded(List<? extends PhotoShot> data) {
                adapter.addAndResort(data);
                checkEmptyState();
            }
        };
        adapter = new FeedAdapter(this, dataManager);
        layoutManager = new GridLayoutManager(this, columns);
        grid.setLayoutManager(layoutManager);
        grid.setItemAnimator(mChangeAnimator);
        grid.addItemDecoration(new SimpleDividerItemDecoration(8, 8));
        grid.setHasFixedSize(true);
        grid.setAdapter(adapter);

        mScrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                dataManager.loadDataSource(page);
            }
        };

        grid.addOnScrollListener(mScrollListener);
        // drawer layout treats fitsSystemWindows specially so we have to handle insets ourselves
        drawer.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                // inset the toolbar down by the status bar height
                ViewGroup.MarginLayoutParams lpToolbar = (ViewGroup.MarginLayoutParams) toolbar
                        .getLayoutParams();
                lpToolbar.topMargin += insets.getSystemWindowInsetTop();
                lpToolbar.leftMargin += insets.getSystemWindowInsetLeft();
                lpToolbar.rightMargin += insets.getSystemWindowInsetRight();
                toolbar.setLayoutParams(lpToolbar);

                grid.setPadding(
                        0,
                        insets.getSystemWindowInsetTop()
                                + ViewUtils.getActionBarSize(HomeActivity.this),
                        0,
                        grid.getPaddingBottom() + insets.getSystemWindowInsetBottom());

                View statusBarBackground = findViewById(R.id.status_bar_background);
                FrameLayout.LayoutParams lpStatus = (FrameLayout.LayoutParams)
                        statusBarBackground.getLayoutParams();
                lpStatus.height = insets.getSystemWindowInsetTop();
                statusBarBackground.setLayoutParams(lpStatus);
                drawer.setOnApplyWindowInsetsListener(null);
                return insets.consumeSystemWindowInsets();
            }
        });
        dataManager.loadDataSource(0);
        checkEmptyState();

    }

    @Override
    protected void onPause() {
        if (monitoringConnectivity) {
            final ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            connectivityManager.unregisterNetworkCallback(connectivityCallback);
            monitoringConnectivity = false;
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        checkConnectivity();
        super.onResume();
    }

    private void checkEmptyState() {
        if (adapter.getDataItemCount() == 0) {
            loading.setVisibility(View.VISIBLE);
            grid.setVisibility(View.INVISIBLE);
        } else {
            grid.setVisibility(View.VISIBLE);
            loading.setVisibility(View.GONE);
        }
    }

    private void checkConnectivity() {
        final ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        connected = activeNetworkInfo != null && activeNetworkInfo.isConnected();
        if (!connected) {
            loading.setVisibility(View.GONE);
            if (noConnection == null) {
                final ViewStub stub = (ViewStub) findViewById(R.id.stub_no_connection);
                noConnection = (ImageView) stub.inflate();
            }
            final AnimatedVectorDrawable avd =
                    (AnimatedVectorDrawable) getDrawable(R.drawable.avd_no_connection);
            if (noConnection != null && avd != null) {
                noConnection.setImageDrawable(avd);
                avd.start();
            }

            connectivityManager.registerNetworkCallback(
                    new NetworkRequest.Builder()
                            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build(),
                    connectivityCallback);
            monitoringConnectivity = true;
        }
    }

    private ConnectivityManager.NetworkCallback connectivityCallback
            = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(Network network) {
            connected = true;
            if (adapter.getDataItemCount() != 0) return;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TransitionManager.beginDelayedTransition(drawer);
                    noConnection.setVisibility(View.GONE);
                    loading.setVisibility(View.VISIBLE);
                    dataManager.loadDataSource(0);
                }
            });
        }

        @Override
        public void onLost(Network network) {
            connected = false;
        }
    };

}
