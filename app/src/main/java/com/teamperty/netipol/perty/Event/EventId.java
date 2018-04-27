package com.teamperty.netipol.perty.Event;

import android.support.annotation.NonNull;

/**
 * Created by Administrator on 8/2/2561.
 */

public class EventId {

    public String eventId;

    public <T extends EventId> T withId(@NonNull final String id){
        this.eventId = id;
        return (T) this;
    }
}
