package com.sarahehabm.eventreminder.controller.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.Events;
import com.sarahehabm.eventreminder.controller.PreferencesUtility;
import com.sarahehabm.eventreminder.controller.database.EventsContract.EventEntry;
import com.sarahehabm.eventreminder.model.UserCredential;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 * Created by Sarah E. Mostafa on 17-Mar-16.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String TAG = SyncAdapter.class.getSimpleName();

    private com.google.api.services.calendar.Calendar mService = null;


    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        Log.v(TAG, "OnPerformSync");
        Log.e(TAG, "SYNCINGG!");
        System.out.println("Hi there, this is me syncing");


        //Syncing Google events
        if(PreferencesUtility.getGoogleAccount(getContext()) != null) {
            Log.e(TAG, "About to sync; google account is not null");
            GoogleAccountCredential credential =
                    UserCredential.getInstance(getContext()).getCredential();

            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Calendar API Android Quickstart")
                    .build();


            List<Event> retrievedEvents;
            try {
                DateTime now = new DateTime(System.currentTimeMillis());
//                DateTime now = new DateTime(1451606400*1000);
                Events events = mService.events().list("primary")
                        .setTimeMin(now)
                        .setOrderBy("startTime")
                        .setSingleEvents(true)
                        .execute();
                retrievedEvents = events.getItems();

                if (retrievedEvents != null && retrievedEvents.size() > 0) {
                    Log.v(TAG, "Retrieved " + retrievedEvents.size() + " events.");
                    int numInserted = getContext().getContentResolver().bulkInsert(EventEntry.CONTENT_URI,
                            listToContentValuesArray(retrievedEvents));
                    Log.v(TAG, Calendar.getInstance().getTime().toString() + ": Inserted " + numInserted + " rows.");
                }
            } catch (UserRecoverableAuthIOException e) {
                //TODO handle authorization exception
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "Did not sync; google account is null");
        }


        //Syncing Facebook events
        String userId = PreferencesUtility.getFacebookUserId(getContext());
        Log.e(TAG, "" + userId);

        if(userId != null) {
            Log.e(TAG, "About to sync; facebook user id is not null");

            Bundle parameters = new Bundle();
            parameters.putString("fields", "name, start_time.order(chronological), end_time," +
                    " rsvp_status, id, type={attending, created, declined, maybe, not_replied}, " +
                    "owner, place, updated_time");
            parameters.putInt("limit", 100);
            Calendar calendar = Calendar.getInstance();
            parameters.putLong("since", calendar.getTimeInMillis()/1000);
//            parameters.putLong("since", 1451606400);
            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/" + userId + "/events",
                    parameters,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                            Log.v(TAG, "onCompleted");
                            Log.v(TAG, response.toString());

                            try {
                                if(response!=null) {
                                    if(response.getJSONObject()!=null) {
                                        JSONArray eventsArray = response.getJSONObject().getJSONArray("data");
                                        Log.v(TAG, "eventsArray size = " + eventsArray.length());

                                        int numInserted = 0;
                                        if(jsonArrayToContentValuesArray(eventsArray)!= null) {
                                             numInserted = getContext().getContentResolver()
                                                     .bulkInsert(EventEntry.CONTENT_URI
                                                             , jsonArrayToContentValuesArray(eventsArray));
                                        }
                                        Log.v(TAG, Calendar.getInstance().getTime().toString()
                                                + ": FACEBOOK Inserted " + numInserted + " rows.");
                                    }

                                    GraphRequest nextRequest = response.getRequestForPagedResults(
                                            GraphResponse.PagingDirection.NEXT);
                                    if (nextRequest != null) {
                                        nextRequest.setCallback(this);
                                        nextRequest.executeAndWait();
                                    }
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
            ).executeAndWait();
        } else {
            Log.e(TAG, "Did not sync; facebook user id is null");
        }

        Log.e(TAG, "End of onPerformSync");
    }

    private ContentValues[] listToContentValuesArray(List<Event> events) {
        if(events == null || events.size()<=0)
            return null;

        Vector<ContentValues> contentValuesVector = new Vector<>(events.size());

        for (int i=0; i<events.size(); i++) {
            Event event = events.get(i);

            if(!isAvailableInDatabase(event.getId())) {
                ContentValues values = new ContentValues();
                values.put(EventEntry.COLUMN_EVENT_ID, event.getId());
                values.put(EventEntry.COLUMN_EVENT_TITLE, event.getSummary());

                String status = event.getStatus();
                List<EventAttendee> attendees = event.getAttendees();
                if(attendees!=null) {
                    for (EventAttendee attendee : attendees) {
                        if (attendee.isSelf()) {
                            status = attendee.getResponseStatus();
                            break;
                        }
                    }
                }
                values.put(EventEntry.COLUMN_EVENT_STATUS, status);

                DateTime startDateObj = event.getStart().getDate();
                if (startDateObj != null)
                    values.put(EventEntry.COLUMN_EVENT_START_DATE, startDateObj.getValue());
                else {
                    startDateObj = event.getStart().getDateTime();
                    if(startDateObj!=null)
                        values.put(EventEntry.COLUMN_EVENT_START_DATE, startDateObj.getValue());
                }

                DateTime endDateObject = event.getEnd().getDate();
                if(endDateObject!=null) {
                    values.put(EventEntry.COLUMN_EVENT_END_DATE, endDateObject.getValue());
                } else {
                    endDateObject = event.getEnd().getDateTime();
                    if (endDateObject!=null)
                        values.put(EventEntry.COLUMN_EVENT_END_DATE, endDateObject.getValue());
                }

                values.put(EventEntry.COLUMN_EVENT_COLOR, String.valueOf(event.getColorId()));
                values.put(EventEntry.COLUMN_EVENT_CREATED_DATE, String.valueOf(event.getCreated()));
                values.put(EventEntry.COLUMN_EVENT_CREATOR, String.valueOf(event.getCreator()));
                values.put(EventEntry.COLUMN_EVENT_LOCATION, String.valueOf(event.getLocation()));
                values.put(EventEntry.COLUMN_EVENT_ORGANIZER, String.valueOf(event.getOrganizer()));
                values.put(EventEntry.COLUMN_EVENT_UPDATED, String.valueOf(event.getUpdated()));

                contentValuesVector.add(values);
            }
        }

        Log.v(TAG, "contentValuesVector.size()= " + contentValuesVector.size());
        ContentValues[] contentValuesArray = new ContentValues[contentValuesVector.size()];
        return contentValuesVector.toArray(contentValuesArray);
    }

    private ContentValues[] jsonArrayToContentValuesArray(JSONArray jsonArray) throws JSONException, ParseException {
        if (jsonArray==null || jsonArray.length() <= 0)
            return null;

        Vector<ContentValues> contentValuesVector = new Vector<>(jsonArray.length());

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject event = (JSONObject) jsonArray.get(i);

            String id = event.getString("id");
            if (!isAvailableInDatabase(id)) {
                ContentValues values = new ContentValues();
                values.put(EventEntry.COLUMN_EVENT_ID, id);
                values.put(EventEntry.COLUMN_EVENT_TITLE, event.getString("name"));
                values.put(EventEntry.COLUMN_EVENT_STATUS, event.getString("rsvp_status"));

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ");
                Date startDateObject = dateFormat.parse(event.getString("start_time"));
                values.put(EventEntry.COLUMN_EVENT_START_DATE, startDateObject.getTime());
                if(event.has("end_time")) {
                    Date endDateObject = dateFormat.parse(event.getString("end_time"));
                    values.put(EventEntry.COLUMN_EVENT_END_DATE, endDateObject.getTime());
                }
//                                values.put(EventEntry.COLUMN_EVENT_COLOR, );
//                                values.put(EventEntry.COLUMN_EVENT_CREATED_DATE, );
                if (event.has("owner")) {
                    JSONObject owner = event.getJSONObject("owner");
                    String ownerName = owner.getString("name");
                    values.put(EventEntry.COLUMN_EVENT_CREATOR, ownerName);
                    values.put(EventEntry.COLUMN_EVENT_ORGANIZER, ownerName);
                }
                if (event.has("place")) {
                    JSONObject place = event.getJSONObject("place");
                    String placeName;
                    if(place.has("name"))
                        placeName = place == null ? "" : place.getString("name");
                    else
                        placeName = "";

                    JSONObject placeLocationObject =
                            (place == null || !place.has("location")) ? null :
                                    place.getJSONObject("location");

                    String locationName;
                    if (placeLocationObject == null)
                        locationName = placeName;
                    else {
                        String street = placeLocationObject.has("street") ?
                                placeLocationObject.getString("street") + ", "
                                : "";
                        String city = placeLocationObject.has("city") ?
                                placeLocationObject.getString("city") + ", "
                                : "";
                        String country = placeLocationObject.has("country") ?
                                placeLocationObject.getString("country")
                                : "";

                        locationName =
                                placeName + ", " + street + city + country;
                    }

                    if (locationName.lastIndexOf(", ") == locationName.length() - 1)
                        locationName.substring(0, locationName.lastIndexOf(", ") - 1);

                    values.put(EventEntry.COLUMN_EVENT_LOCATION, locationName);
                }
                values.put(EventEntry.COLUMN_EVENT_UPDATED, event.getString("updated_time"));

                contentValuesVector.add(values);
            }
        }

        Log.v(TAG, "contentValuesVector(facebook).size()= " + contentValuesVector.size());
        ContentValues[] contentValuesArray = new ContentValues[contentValuesVector.size()];
        return contentValuesVector.toArray(contentValuesArray);
    }

    private boolean isAvailableInDatabase(String id) {
        Cursor result = getContext().getContentResolver().query(EventEntry.CONTENT_URI, null,
                EventEntry.COLUMN_EVENT_ID + " = ? ",
                new String[]{id}, null);

        boolean isAvailable = (result != null && result.getCount()>0) ? true : false;
        result.close();
        return isAvailable;
    }
}
