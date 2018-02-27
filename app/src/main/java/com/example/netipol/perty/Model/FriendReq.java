package com.example.netipol.perty.Model;

import com.example.netipol.perty.Util.EventId;
import com.example.netipol.perty.Util.FriendReqID;

/**
 * Created by netipol on 26/2/2018 AD.
 */

public class FriendReq extends FriendReqID {

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
