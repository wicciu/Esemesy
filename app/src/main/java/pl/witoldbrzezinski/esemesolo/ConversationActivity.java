package pl.witoldbrzezinski.esemesolo;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import pl.witoldbrzezinski.esemesolo.adapters.AllConversationsAdapter;
import pl.witoldbrzezinski.esemesolo.adapters.ContactsAdapter;
import pl.witoldbrzezinski.esemesolo.adapters.ConversationAdapter;
import pl.witoldbrzezinski.esemesolo.model.SmsModel;
import pl.witoldbrzezinski.esemesolo.realm.DatabaseManager;

import static pl.witoldbrzezinski.esemesolo.SenderActivity.SENDER_INTENT_KEY;
import static pl.witoldbrzezinski.esemesolo.adapters.AllConversationsAdapter.ACTIVITY_INTENT_KEY;

/**
 * Created by Wiciu on 17.06.2017.
 */

public class ConversationActivity extends AppCompatActivity {

    private Button sendButton;
    private EditText messageEditText;
    private TextView meTextView;
    private TextView friendTextView;
    private RecyclerView conversationRecyclerView;
    private RecyclerView.Adapter conversationAdapter;
    private LinearLayoutManager conversationLayoutManager;
    private DatabaseManager databaseManager = DatabaseManager.getInstance();
    String number = "";
    private IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation_main);
        Toolbar conversationToolbar = (Toolbar) findViewById(R.id.conversation_toolbar);
        setSupportActionBar(conversationToolbar);
        registerReceiver(conversationReceiver,filter);
        collectParams();
        bindViews();
        setListeners();
        showSmses();
    }

    private void bindViews() {
        sendButton = (Button)findViewById(R.id.button_send);
        conversationRecyclerView = (RecyclerView) findViewById(R.id.messages_recyclerview);
        messageEditText = (EditText)findViewById(R.id.message_edittext_new);
        meTextView = (TextView)findViewById(R.id.me_name);
        meTextView.setText(R.string.me);
        friendTextView = (TextView)findViewById(R.id.friend_name);
        friendTextView.setText(getContactNameFromNumber(getNumber()));
    }

    private void setListeners() {
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = number;
                String smsText = messageEditText.getText().toString();
                if (phoneNumber.length()>0&&smsText.length()>0) //Numer telefonu i wiadomość większe od zera
                    sendSmsInConversation(phoneNumber,smsText);
                else{
                    Toast.makeText(getBaseContext(),R.string.fillBoth,Toast.LENGTH_LONG).show();}
                refresh();
            }
        });
    }

    public void setRecyclerViewData(List<SmsModel> data){
        conversationLayoutManager = new LinearLayoutManager(this);
        conversationLayoutManager.setStackFromEnd(true);
        conversationRecyclerView.setLayoutManager(conversationLayoutManager);
        List<SmsModel> list = databaseManager.getOneNumberSmses(number);
        conversationAdapter = new ConversationAdapter(list);
        conversationRecyclerView.setAdapter(conversationAdapter);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this,LinearLayoutManager.VERTICAL);
        conversationRecyclerView.addItemDecoration(itemDecoration);
    }

    private BroadcastReceiver conversationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refresh();
        }
    };


    @Override
    protected void onPause(){
        super.onPause();
        if (conversationReceiver!=null){
            getApplicationContext().unregisterReceiver(conversationReceiver);
            conversationReceiver = null;
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        getApplicationContext().registerReceiver(conversationReceiver,filter);
        refresh();
    }
    @Override
    protected void onStop() {
        if (conversationReceiver!=null){
            unregisterReceiver(conversationReceiver);
            conversationReceiver=null;
        }
        super.onStop();
    }

    private void showSmses(){
        List<SmsModel> smsModels = databaseManager.getAllSmses();
        setRecyclerViewData(smsModels);
    }

    public void refresh(){
        databaseManager.insertSmses(Collections.EMPTY_LIST);
        conversationAdapter.notifyDataSetChanged();
        databaseManager.insertSmses(getOneNumberSmesesFromCursor());
        conversationRecyclerView.scrollToPosition(conversationAdapter.getItemCount()-1);
    }

    public List<SmsModel> getOneNumberSmesesFromCursor() {
        List<SmsModel> realmList = new ArrayList<SmsModel>();
        ContentResolver contentResolver = getContentResolver();
        Uri uri = Uri.parse("content://sms");
        Cursor smsCursor = contentResolver.query(uri, null,null, null, null);
        int indexBodyInbox = smsCursor.getColumnIndex("body");
        int indexAddressInbox = smsCursor.getColumnIndex("address");
        int indexDate = smsCursor.getColumnIndex("date");
        int indexTypeSms = smsCursor.getColumnIndex("type");
        if (indexBodyInbox < 0 || !smsCursor.moveToFirst())
            return realmList;
        do {
            long timeMillis = smsCursor.getLong(indexDate);
            SmsModel smsModel = new SmsModel(smsCursor.getString(indexAddressInbox).replace("+48",""),// niezbyt udana koncepcja
                    smsCursor.getString(indexBodyInbox),
                    new Date(timeMillis), smsCursor.getString(indexTypeSms));
            realmList.add(smsModel);
        } while (smsCursor.moveToNext());
        smsCursor.close();
        return realmList;
    }

    private void collectParams(){
        Intent intent = getIntent();
        if (intent==null) {
            return ;
        }
        Bundle bundle = intent.getExtras();
        if (bundle==null){
            return;
        }
        String intentName = bundle.getString(ACTIVITY_INTENT_KEY);
        switch (intentName){
            case AllConversationsAdapter.ALL_CONVERSATIONS:
                number = bundle.getString(AllConversationsAdapter.ALL_CONVERSATIONS_INTENT_KEY);
                break;
            case ContactsAdapter.CONTACTS:
                number = bundle.getString(ContactsAdapter.CONTACTS_INTENT_KEY);
               break;
            case SenderActivity.SENDER:
                number = bundle.getString(SENDER_INTENT_KEY);
                break;
        }
    }

    protected void sendSmsInConversation(String phoneNumber, String smsText) {
        try{
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber,null,smsText,null,null);}
        catch (Exception e){
            Toast.makeText(getBaseContext(),R.string.smsError, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private String getContactNameFromNumber(String number) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String name = number;
        Cursor cursor = getContentResolver().query(uri,
                new String[] { ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);
        if (cursor.moveToFirst()) {
            name = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }
        return name;
    }

    public String getNumber() {
        return number;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
