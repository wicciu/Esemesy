package pl.witoldbrzezinski.esemesolo;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import pl.witoldbrzezinski.esemesolo.adapters.ContactsAdapter;
import pl.witoldbrzezinski.esemesolo.model.ContactModel;
import pl.witoldbrzezinski.esemesolo.realm.DatabaseManager;

/**
 * Created by Wiciu on 17.06.2017.
 */

public class ContactsActivity extends AppCompatActivity {

    private RecyclerView contactRecyclerView;
    private ContactsAdapter contactsAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DatabaseManager databaseManager = DatabaseManager.getInstance();

    String id = "";
    String name = "";
    String contactNumber = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_layout);
        Toolbar contactToolbar = (Toolbar) findViewById(R.id.contact_toolbar);
        setSupportActionBar(contactToolbar);
        bindViews();
        getContactsFromCursor();
        getContactsAndSaveToDatabase();
        showContacts();
        getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, contactObserver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contacts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.addContact) {
           showAddContactDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshContacts();
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(contactObserver!=null){
            getContentResolver().unregisterContentObserver(contactObserver);
            contactObserver=null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(contactObserver!=null){
            getContentResolver().unregisterContentObserver(contactObserver);
        }
    }

    private void bindViews() {
        contactRecyclerView = (RecyclerView) findViewById(R.id.contactList);
    }

    public void setRecyclerViewData(List<ContactModel> data) {
        mLayoutManager = new LinearLayoutManager(this);
        contactRecyclerView.setLayoutManager(mLayoutManager);
        contactsAdapter = new ContactsAdapter(databaseManager.getAllContacts());
        contactRecyclerView.setAdapter(contactsAdapter);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        contactRecyclerView.addItemDecoration(itemDecoration);
    }

    private void showContacts() {
        List<ContactModel> contactModels = databaseManager.getAllContacts();
        setRecyclerViewData(contactModels);
    }

    public void getContactsAndSaveToDatabase() {
        databaseManager.insertContacts(getContactsFromCursor());
    }

    public List<ContactModel> getContactsFromCursor() {
        List<ContactModel> contactList = new ArrayList<ContactModel>();
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        contactNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));//niezbyt udana koncepcja
                        ContactModel contactModel = new ContactModel(contactNumber, name);
                        contactList.add(contactModel);
                        break;
                    }
                    pCur.close();
                }
            } while (cursor.moveToNext());
            Collections.sort(contactList, new Comparator<ContactModel>() {
                @Override
                public int compare(ContactModel o1, ContactModel o2) {
                    return o1.getContactName().compareTo(o2.getContactName());
                }
            });
        }
        return contactList;
    }

    private void showAddContactDialog() {
        final AddContactDialogFragment dialog = new AddContactDialogFragment();
        dialog.show(getSupportFragmentManager(), dialog.getClass().getName());
        dialog.setListener(new AddContactDialogFragment.OnAddContactClickListener() {
            @Override
            public void addContact(Context context, ContactModel contactModel) {
            }
        });
    }

    private ContentObserver contactObserver = new ContentObserver(new Handler()) {

        @Override
        public void onChange(boolean selfChange) {
            this.onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            refreshContacts();
        }
    };

    public void refreshContacts(){
        databaseManager.insertContacts(Collections.EMPTY_LIST);
        contactsAdapter.notifyDataSetChanged();
        getContactsAndSaveToDatabase();
    }

}
