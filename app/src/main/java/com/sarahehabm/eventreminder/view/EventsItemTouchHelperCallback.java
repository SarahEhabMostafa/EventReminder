package com.sarahehabm.eventreminder.view;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.widget.Toast;

import com.sarahehabm.eventreminder.R;
import com.sarahehabm.eventreminder.controller.database.EventsContract.EventEntry;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Sarah E. Mostafa on 25-Mar-16.
 */
public class EventsItemTouchHelperCallback extends ItemTouchHelper.SimpleCallback {
    private static final String TAG = EventsItemTouchHelperCallback.class.getSimpleName();

    private Context context;
    private SendEmailInterface sendEmailInterface;

    public EventsItemTouchHelperCallback(Context context, SendEmailInterface sendEmailInterface) {
        super(0, 0);
        this.context = context;
        this.sendEmailInterface = sendEmailInterface;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        EventsAdapter.ViewHolder holder = (EventsAdapter.ViewHolder) viewHolder;
        String status = holder.textViewStatus.getText().toString();

        if(status.equalsIgnoreCase("needsAction") || status.equalsIgnoreCase("tentative")
                || status.equalsIgnoreCase("unsure"))
            return ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;

        return super.getSwipeDirs(recyclerView, viewHolder);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        EventsAdapter.ViewHolder holder = (EventsAdapter.ViewHolder) viewHolder;

        String id = (String) holder.itemView.getTag(R.string.tag_event_id);
        int position = (int) holder.itemView.getTag(R.string.tag_item_position);
        String oldStatus = holder.textViewStatus.getText().toString();
        String creator = (String) holder.itemView.getTag(R.string.tag_event_creator);
        String title = holder.textViewTitle.getText().toString();

        Log.v(TAG, "Item at position " + position + " with id " + id + " has been swiped " +
                "to the " + direction + ".");

        long startTimeCurrent = (long) holder.itemView.getTag(R.string.tag_item_start_date);
        long endTimeCurrent = (long) holder.itemView.getTag(R.string.tag_item_end_date);
        Log.v(TAG, "current start time= " + startTimeCurrent);
        Log.v(TAG, "current end time= " + endTimeCurrent);

        String newStatus = null;
        switch (direction) {
            case ItemTouchHelper.LEFT:
                Log.v(TAG, "Should DECLINE");
                newStatus = "declined";
                break;

            case ItemTouchHelper.RIGHT:
                Log.v(TAG, "Should ACCEPT");
                String selection = EventEntry.COLUMN_EVENT_START_DATE + " = ? AND "
                        + EventEntry.COLUMN_EVENT_ID + " != ?";
                String[] selectionArgs = new String[]{String.valueOf(startTimeCurrent), id};
                Cursor cursor = context.getContentResolver().query(EventEntry.CONTENT_URI,
                        null, selection, selectionArgs, null);
                Log.e(TAG, cursor.getCount() +"");

                if(cursor.getCount()>0) {
                    boolean emptySlotFound = false;
                    long nextEmptySlot = startTimeCurrent + 86400000;
                    do {
                        selectionArgs = new String[]{String.valueOf(nextEmptySlot), id};
                        cursor = context.getContentResolver().query(EventEntry.CONTENT_URI,
                                null, selection, selectionArgs, null);

                        if(cursor.getCount()==0) {
                            emptySlotFound = true;
                        } else {
                            nextEmptySlot += 86400000;
                        }
                    } while (!emptySlotFound);

                    try {
                        JSONObject creatorJsonObject = new JSONObject(creator);
                        String creatorEmail = creatorJsonObject.getString("email");
                        sendEmailInterface.onSendEmail(nextEmptySlot, title, creatorEmail);
                        newStatus = oldStatus;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    newStatus = "accepted";
                }
                break;
        }

        if(newStatus!=null) {
            ContentValues values = new ContentValues();
            values.put(EventEntry.COLUMN_EVENT_STATUS, newStatus);
            int numUpdated = context.getContentResolver().update(EventEntry.CONTENT_URI, values,
                    EventEntry.COLUMN_EVENT_ID + " = ? ",
                    new String[]{id});

            Toast.makeText(context,"Event has been updated.", Toast.LENGTH_SHORT).show();
        }
    }
}
