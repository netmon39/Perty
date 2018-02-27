package com.example.netipol.perty.Util;

import android.support.annotation.NonNull;


/**
 * Created by netipol on 26/2/2018 AD.
 */

public class FriendReqID {
    public String friendreqId;

    public <T extends FriendReqID> T withId(@NonNull final String id){
        this.friendreqId = id;
        return (T) this;
    }
}
