package com.sarahehabm.eventreminder.view;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import com.sarahehabm.eventreminder.R;

/**
 * Created by Sarah E. Mostafa on 25-Mar-16.
 */
public class EventsItemTouchHelperCallback extends ItemTouchHelper.SimpleCallback {
    private static final String TAG = EventsItemTouchHelperCallback.class.getSimpleName();

    /**
     * Creates a Callback for the given drag and swipe allowance. These values serve as
     * defaults
     * and if you want to customize behavior per ViewHolder, you can override
     * {@link #getSwipeDirs(RecyclerView, ViewHolder)}
     * and / or {@link #getDragDirs(RecyclerView, ViewHolder)}.
     *
     * @param dragDirs  Binary OR of direction flags in which the Views can be dragged. Must be
     *                  composed of {@link #LEFT}, {@link #RIGHT}, {@link #START}, {@link
     *                  #END},
     *                  {@link #UP} and {@link #DOWN}.
     * @param swipeDirs Binary OR of direction flags in which the Views can be swiped. Must be
     *                  composed of {@link #LEFT}, {@link #RIGHT}, {@link #START}, {@link
     *                  #END},
     *                  {@link #UP} and {@link #DOWN}.
     */
    public EventsItemTouchHelperCallback() {
        super(0, ItemTouchHelper.LEFT);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        EventsAdapter.ViewHolder holder = (EventsAdapter.ViewHolder) viewHolder;

        String id = (String) holder.itemView.getTag(R.string.tag_event_id);
        int position = (int) holder.itemView.getTag(R.string.tag_item_position);

        Log.v(TAG, "Item at position " + position + " with id " + id + " has been swiped.");

        //TODO handle swipe
    }
}
