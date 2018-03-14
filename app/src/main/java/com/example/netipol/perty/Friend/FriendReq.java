package com.example.netipol.perty.Friend;

/**
 * Created by netipol on 26/2/2018 AD.
 */

public class FriendReq extends FriendReqId {

        private String name;

        public FriendReq(){

        }

        public FriendReq(String name) {
            this.name = name;

        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
}
