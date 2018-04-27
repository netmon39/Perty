package com.teamperty.netipol.perty.Friend;

import android.support.annotation.NonNull;


/**
 * Created by netipol on 26/2/2018 AD.
 */

public class FriendReqId {
    public String friendreqId;

    public <T extends FriendReqId> T withId(@NonNull final String id){
        this.friendreqId = id;
        return (T) this;
    }
}
