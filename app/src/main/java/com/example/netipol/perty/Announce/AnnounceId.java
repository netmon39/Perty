package com.example.netipol.perty.Announce;

import android.support.annotation.NonNull;

/**
 * Created by netipol on 15/4/2018 AD.
 */

public class AnnounceId {
    public String announceId;

    public <T extends AnnounceId> T withId(@NonNull final String id){
        this.announceId = id;
        return (T) this;
    }
}
