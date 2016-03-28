package com.sarahehabm.eventreminder.view;

/**
 * Created by Sarah E. Mostafa on 28-Mar-16.
 */
public interface SendEmailInterface {
    public void onSendEmail(long suggestedTimeSlot, final String eventName, final String creatorEmail);
}
