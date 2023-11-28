package com.example.firebasecourse

import com.stfalcon.chatkit.commons.models.IMessage
import com.stfalcon.chatkit.commons.models.IUser
import java.util.Date

class Messages(var mid:String, var mText:String,var mUser:IUser,var mDate:Date):IMessage {
    override fun getId(): String {
        return mid;
    }

    override fun getText(): String {
        return mText;
    }

    override fun getUser(): IUser {
        return mUser;
    }

    override fun getCreatedAt(): Date {
        return mDate;
    }
}