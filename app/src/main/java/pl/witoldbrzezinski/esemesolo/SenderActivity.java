package pl.witoldbrzezinski.esemesolo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import static pl.witoldbrzezinski.esemesolo.adapters.AllConversationsAdapter.ACTIVITY_INTENT_KEY;

/**
 * Created by Wiciu on 17.06.2017.
 */

public class SenderActivity extends AppCompatActivity {

    public static final String SENDER_INTENT_KEY = "SENDER_INTENT";
    private EditText phoneNumberEditText;
    private EditText messageEditText;
    private FloatingActionButton sendFromMainFloatingButton;
    String phoneNumber= "";
    public static final String SENDER = "SENDER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sender_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.senderToolbar);
        setSupportActionBar(toolbar);
        bindViews();
        setListeners();
    }

    private void bindViews() {
        phoneNumberEditText = (EditText)findViewById(R.id.phoneNumberEditText);
        messageEditText = (EditText)findViewById(R.id.messageEditText);
        sendFromMainFloatingButton = (FloatingActionButton)findViewById(R.id.sendFromMainFloatingButton);
    }

    private void setListeners(){
        sendFromMainFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber = phoneNumberEditText.getText().toString();
                String smsText = messageEditText.getText().toString();
                if (phoneNumber.length()>0&&smsText.length()>0){ //Numer telefonu i wiadomość większe od zera
                    sendSms(phoneNumber,smsText);
                    sendIntent();
                }
                else{
                    Toast.makeText(getBaseContext(),R.string.fillBoth,Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    protected void sendSms(String phoneNumber, String smsText) {
        try{
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber,null,smsText,null,null);}
        catch (Exception e){
            Toast.makeText(getBaseContext(),R.string.smsError, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this,AllConversationsActivity.class);
        startActivity(intent);
    }
    public void sendIntent(){
        Intent intent = new Intent(SenderActivity.this,ConversationActivity.class);
        intent.putExtra(SENDER_INTENT_KEY,phoneNumber);
        intent.putExtra(ACTIVITY_INTENT_KEY,SENDER);
        startActivity(intent);
    }


}
