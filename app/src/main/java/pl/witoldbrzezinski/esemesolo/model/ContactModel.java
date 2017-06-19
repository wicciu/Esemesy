package pl.witoldbrzezinski.esemesolo.model;

import io.realm.RealmObject;

/**
 * Created by Wiciu on 17.06.2017.
 */

public class ContactModel extends RealmObject {

    String phoneNumber;
    String contactName;

    public ContactModel(String phoneNumber, String contactName) {
        this.phoneNumber = phoneNumber;
        this.contactName = contactName;
    }

    public ContactModel(){

    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
