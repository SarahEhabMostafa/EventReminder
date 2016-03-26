package com.sarahehabm.eventreminder.view;

import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sarahehabm.eventreminder.R;
import com.sarahehabm.eventreminder.controller.database.EventsContract.EventEntry;
import com.sarahehabm.eventreminder.controller.sync.SyncUtility;

/**
 * A placeholder fragment containing a simple view.
 */
public class EventsActivityFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener {

    private static final int LOADER = 0;
    private static final String TAG = EventsActivityFragment.class.getSimpleName();

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView emptyTextView;
    private FloatingActionButton floatingActionButton;

    private EventsAdapter adapter;

    public EventsActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_events, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int  itemId = item.getItemId();

        switch (itemId) {
            case R.id.action_settings:
                //TODO open settings activity
                return true;

            case R.id.action_refresh:
                Log.v(TAG, "Triggering refresh..");
                swipeRefreshLayout.setRefreshing(true);
                SyncUtility.requestSync();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_events, container, false);

        setHasOptionsMenu(true);

        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        toolbar.showOverflowMenu();
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources
                (R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorAccent);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView_events);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        emptyTextView = (TextView) rootView.findViewById(R.id.textView_empty);
        recyclerView.setHasFixedSize(true);
        adapter = new EventsAdapter(getActivity());
        recyclerView.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new EventsItemTouchHelperCallback());
        itemTouchHelper.attachToRecyclerView(recyclerView);

        floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                Toast.makeText(getActivity(), "FAB clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), EventEntry.CONTENT_URI,
                null, null, null,
                EventEntry.COLUMN_EVENT_DATE + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
        swipeRefreshLayout.setRefreshing(false);
        updateEmptyTextView();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    private void updateEmptyTextView() {
        if(adapter.getItemCount() == 0) {
            emptyTextView.setVisibility(View.VISIBLE);
            emptyTextView.setText(getString(R.string.empty));
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyTextView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRefresh() {
        Log.v(TAG, "Refreshing..");
        SyncUtility.requestSync();
    }
}
