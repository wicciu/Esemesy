package pl.witoldbrzezinski.esemesolo.realm;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import pl.witoldbrzezinski.esemesolo.model.ContactModel;
import pl.witoldbrzezinski.esemesolo.model.SmsModel;

/**
 * Created by Wiciu on 17.06.2017.
 */

public class DatabaseManager {

    private Realm mRealm;
    private static DatabaseManager instance;

    public static DatabaseManager getInstance(){
        if (instance == null){
            instance = new DatabaseManager();
        }
        return instance;
    }


    private DatabaseManager(){
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        mRealm = Realm.getInstance(realmConfiguration);
    }

    public void insertSmses(List<SmsModel> smsRealmList){
        mRealm.beginTransaction();
        mRealm.deleteAll();
        mRealm.insertOrUpdate(smsRealmList);
        mRealm.commitTransaction();
    }

    public List<SmsModel> getAllSmses(){
        RealmResults<SmsModel> realmSmModels = mRealm.where(SmsModel.class).
                findAllSorted("smsDate", Sort.DESCENDING);
        return realmSmModels;
    }

    public List<SmsModel> getSmsesByPhoneNumber(){
        RealmQuery<SmsModel> realmQuery = mRealm.where(SmsModel.class);
        RealmResults realmResults = realmQuery.findAllSorted("smsDate",Sort.DESCENDING).distinct("phoneNumber");
        return realmResults;
    }

    public List<SmsModel> getOneNumberSmses(String phoneNumber) {
        RealmResults<SmsModel> realmSmModels = mRealm.where(SmsModel.class).
                equalTo("phoneNumber", phoneNumber).findAllSorted("smsDate",Sort.ASCENDING);
        return realmSmModels;
    }

    public void insertContacts(List<ContactModel> contactRealmList ){
        mRealm.beginTransaction();
        mRealm.deleteAll();
        mRealm.insertOrUpdate(contactRealmList);
        mRealm.commitTransaction();
    }

    public List<ContactModel> getAllContacts(){
        RealmResults<ContactModel> contactModels = mRealm.where(ContactModel.class).findAll();
        return contactModels;
    }

    public void deleteContactItem(final ContactModel contact){
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                contact.deleteFromRealm();
            }
        });
    }

}
