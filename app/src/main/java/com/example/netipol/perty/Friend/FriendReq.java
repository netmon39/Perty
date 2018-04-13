package com.example.netipol.perty.Friend;

/**
 * Created by netipol on 26/2/2018 AD.
 */

public class FriendReq extends FriendReqId {

        private String uid;

        public FriendReq(){

        }

        public FriendReq(String uid) {
            this.uid = uid;

        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }
}
