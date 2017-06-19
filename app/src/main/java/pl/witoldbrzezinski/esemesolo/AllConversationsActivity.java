package pl.witoldbrzezinski.esemesolo;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import pl.witoldbrzezinski.esemesolo.adapters.AllConversationsAdapter;
import pl.witoldbrzezinski.esemesolo.model.SmsModel;
import pl.witoldbrzezinski.esemesolo.realm.DatabaseManager;

/**
 * Created by Wiciu on 17.06.2017.
 */

public class AllConversationsActivity extends AppCompatActivity {

    private RecyclerView allConversationRecyclerView;
    private RecyclerView.Adapter allConversationAdapter;
    private LinearLayoutManager mLayoutManager;
    private DatabaseManager databaseManager = DatabaseManager.getInstance();
    private FloatingActionButton smsFloatingButton;

    private IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");

    int PERMISSION_ALL = 1;

    String[] PERMISSIONS = new String[]{Manifest.permission.RECEIVE_SMS,Manifest.permission.READ_SMS,
            Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.SEND_SMS,Manifest.permission.VIBRATE,Manifest.permission.READ_PHONE_STATE};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_conversations_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.all_conversations_toolbar);
        setSupportActionBar(toolbar);
        requestPermissionsIfNeeded();
        registerReceiver(allConversationsReceiver,filter);
        bindViews();
        setListeners();
        getSmsesAndSaveToDatabase();
        showSmses();
    }

    private void bindViews() {
        allConversationRecyclerView = (RecyclerView) findViewById(R.id.all_conversations_recyclerview);
        smsFloatingButton = (FloatingActionButton)findViewById(R.id.sendFloatingButton);
    }

    private void setListeners() {
        smsFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(),SenderActivity.class);
                startActivity(intent);
            }
        });
    }

    public void setRecyclerViewData(List<SmsModel> data) {
        mLayoutManager = new LinearLayoutManager(this);
        allConversationRecyclerView.setLayoutManager(mLayoutManager);
        allConversationAdapter = new AllConversationsAdapter(databaseManager.getSmsesByPhoneNumber());
        allConversationRecyclerView.setAdapter(allConversationAdapter);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        allConversationRecyclerView.addItemDecoration(itemDecoration);
    }


    @Override
    protected void onResume(){
        super.onResume();
        getApplicationContext().registerReceiver(allConversationsReceiver,filter);
        refresh();
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(allConversationsReceiver!=null){
            getApplicationContext().unregisterReceiver(allConversationsReceiver);
            allConversationsReceiver=null;
        }
    }

    private BroadcastReceiver allConversationsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refresh();
        }
    };

    private void showSmses() {
        List<SmsModel> smsModels = databaseManager.getAllSmses();
        setRecyclerViewData(smsModels);
    }

    public void getSmsesAndSaveToDatabase() {
        if(hasPermissions(PERMISSIONS)){
            databaseManager.insertSmses(getRealmSmsFromCursor());
        }
    }

    public List<SmsModel> getRealmSmsFromCursor() {
        List<SmsModel> realmList = new ArrayList<SmsModel>();
        ContentResolver contentResolver = getContentResolver();
        Uri uri = Uri.parse("content://sms");
        Cursor smsCursor = contentResolver.query(uri, null, null, null, null);
        int indexBodyInbox = smsCursor.getColumnIndex("body");
        int indexAddressInbox = smsCursor.getColumnIndex("address");
        int indexDate = smsCursor.getColumnIndex("date");
        int indexTypeSms = smsCursor.getColumnIndex("type");
        if (indexBodyInbox < 0 || !smsCursor.moveToFirst())
            return realmList;
        do {
            long timeMillis = smsCursor.getLong(indexDate);
            SmsModel smsModel = new SmsModel(smsCursor.getString(indexAddressInbox).replace("+48",""),
                    smsCursor.getString(indexBodyInbox),
                    new Date(timeMillis), smsCursor.getString(indexTypeSms));
            realmList.add(smsModel);
        } while (smsCursor.moveToNext());
        smsCursor.close();
        return realmList;
    }

    public void refresh(){
        databaseManager.insertSmses(Collections.EMPTY_LIST);
        allConversationAdapter.notifyDataSetChanged();
        getSmsesAndSaveToDatabase();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.bookMenu){
            Intent intent = new Intent(this, ContactsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void requestPermissionsIfNeeded(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermissions(PERMISSIONS)){
                requestPermissions(PERMISSIONS,PERMISSION_ALL);}
        }
    }

    public boolean hasPermissions(String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null) {
            for (String permission : permissions) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
