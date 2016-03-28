package com.sarahehabm.eventreminder.view;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sarahehabm.eventreminder.R;
import com.sarahehabm.eventreminder.controller.database.EventsContract.EventEntry;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Sarah E. Mostafa on 19-Mar-16.
 */
public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {
    private static final String NULL = "null";
    private static final String TAG = EventsAdapter.class.getSimpleName();
    private Context context;
    private Cursor cursor;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewDate, textViewStatus, textViewLocation;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewTitle = (TextView) itemView.findViewById(R.id.textView_title);
            textViewDate = (TextView) itemView.findViewById(R.id.textView_date);
            textViewStatus = (TextView) itemView.findViewById(R.id.textView_status);
            textViewLocation = (TextView) itemView.findViewById(R.id.textView_location);
        }
    }

    public EventsAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_event_item, parent, false);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = (String) v.getTag(R.string.tag_event_id);
                int position = (int) v.getTag(R.string.tag_item_position);
//                Toast.makeText(v.getContext(), "Item #" + position + " with id: " + id + " clicked.", Toast.LENGTH_SHORT).show();
                Toast.makeText(v.getContext(), "Event already accepted/confirmed", Toast.LENGTH_SHORT).show();
                Log.v(TAG, "Item #" + position + " with id: " + id + " clicked.");

            }
        });

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        cursor.moveToPosition(position);

        String eventId = cursor.getString(cursor.getColumnIndex(EventEntry.COLUMN_EVENT_ID));
        String eventTitle = cursor.getString(cursor.getColumnIndex(EventEntry.COLUMN_EVENT_TITLE));
        long eventStartDateMillis = cursor.getLong(cursor.getColumnIndex(EventEntry.COLUMN_EVENT_START_DATE));
        long eventEndDateMillis = cursor.getLong(cursor.getColumnIndex(EventEntry.COLUMN_EVENT_END_DATE));
        String eventStatus = cursor.getString(cursor.getColumnIndex(EventEntry.COLUMN_EVENT_STATUS));
        String eventLocation = cursor.getString(cursor.getColumnIndex(EventEntry.COLUMN_EVENT_LOCATION));
        String eventCreator = cursor.getString(cursor.getColumnIndex(EventEntry.COLUMN_EVENT_CREATOR));

        if (eventTitle != null && !eventTitle.trim().isEmpty() && !eventTitle.equalsIgnoreCase(NULL)) {
            holder.textViewTitle.setText(eventTitle);
            holder.textViewTitle.setVisibility(View.VISIBLE);
        } else {
            holder.textViewTitle.setVisibility(View.GONE);
        }

        if (eventStartDateMillis > 0) {
            String eventDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date(eventStartDateMillis));
            holder.textViewDate.setText(eventDate);
            holder.textViewDate.setVisibility(View.VISIBLE);
        } else {
            holder.textViewDate.setVisibility(View.GONE);
        }

        if (eventStatus != null && !eventStatus.trim().isEmpty() && !eventStatus.equalsIgnoreCase(NULL)) {
            holder.textViewStatus.setText(eventStatus);
            holder.textViewStatus.setVisibility(View.VISIBLE);
        } else {
            holder.textViewStatus.setVisibility(View.GONE);
        }

        if (eventLocation != null && !eventLocation.trim().isEmpty() && !eventLocation.equalsIgnoreCase(NULL)) {
            holder.textViewLocation.setText(eventLocation);
            holder.textViewLocation.setVisibility(View.VISIBLE);
        } else {
            holder.textViewLocation.setVisibility(View.GONE);
        }

        holder.itemView.setTag(R.string.tag_event_id, eventId);
        holder.itemView.setTag(R.string.tag_item_position, position);
        holder.itemView.setTag(R.string.tag_item_start_date, eventStartDateMillis);
        holder.itemView.setTag(R.string.tag_item_end_date, eventEndDateMillis);
        holder.itemView.setTag(R.string.tag_event_creator, eventCreator);
    }

    @Override
    public int getItemCount() {
        if(cursor == null)
            return 0;

        return cursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        cursor = newCursor;
        notifyDataSetChanged();
    }

    public Cursor getCursor() {
        return cursor;
    }
}