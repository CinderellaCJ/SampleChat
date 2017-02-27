package com.cj.samplechat.bean;

import com.hyphenate.chat.EMContact;

/**
 * Created by Administrator on 217/2/10.
 */

public class User extends EMContact {

    //昵称首字母,方便排序//
    protected String initialLetter;
    private  String avatar;


    public User(String username){
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getInitialLetter() {
        return initialLetter;
    }

    public void setInitialLetter(String initialLetter) {
        this.initialLetter = initialLetter;
    }
}
