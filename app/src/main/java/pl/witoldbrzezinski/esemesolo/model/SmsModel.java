package pl.witoldbrzezinski.esemesolo.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Index;

/**
 * Created by Wiciu on 17.06.2017.
 */

public class SmsModel extends RealmObject {

    @Index
    String phoneNumber;
    String messageText;
    @Index
    Date smsDate;
    String type;


    public SmsModel(String phoneNumber, String messageText, Date smsDate, String type) {
        this.phoneNumber = phoneNumber;
        this.messageText = messageText;
        this.smsDate = smsDate;
        this.type = type;
    }

    public SmsModel(){

    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getMessageText() {
        return messageText;
    }

    public Date getSmsDate() {
        return smsDate;
    }

    public String getType() {
        return type;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public void setSmsDate(Date smsDate) {
        this.smsDate = smsDate;
    }

    public void setType(String type) {
        this.type = type;
    }
}
