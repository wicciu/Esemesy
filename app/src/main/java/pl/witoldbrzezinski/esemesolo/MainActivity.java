package pl.witoldbrzezinski.esemesolo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setAllConversationsActivity();
    }

    public void setAllConversationsActivity(){
        Intent allConversationsIntent = new Intent(this,AllConversationsActivity.class);
        startActivity(allConversationsIntent);
    }
}
