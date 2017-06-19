package pl.witoldbrzezinski.esemesolo.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import pl.witoldbrzezinski.esemesolo.ConversationActivity;
import pl.witoldbrzezinski.esemesolo.R;
import pl.witoldbrzezinski.esemesolo.model.SmsModel;

/**
 * Created by Wiciu on 17.06.2017.
 */

public class AllConversationsAdapter extends RecyclerView.Adapter<AllConversationsAdapter.AllConversationsHolder> {

    private List<SmsModel> mSmsModelList;
    public static final String ALL_CONVERSATIONS_INTENT_KEY = "ALL_CONVERSATIONS_INTENT";
    public static final String ACTIVITY_INTENT_KEY = "ACTIVITY_NAME";
    public static final String ALL_CONVERSATIONS = "ALL_CONVERSATIONS";
    Context mContext;

    public AllConversationsAdapter(List<SmsModel> realmSmModels){
        mSmsModelList = realmSmModels;
    }

    @Override
    public AllConversationsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_conversations_item,parent,false);
        AllConversationsHolder allConversationsHolder = new AllConversationsHolder(view);
        return allConversationsHolder;
    }

    @Override
    public void onBindViewHolder(AllConversationsHolder holder, int position) {
        mContext = holder.itemView.getContext();
        SmsModel smsModel = mSmsModelList.get(position);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        holder.number = smsModel.getPhoneNumber();
        holder.phoneNumber.setText(getContactNameFromNumber(smsModel.getPhoneNumber()));
        holder.type = mSmsModelList.get(position).getType();
        holder.message.setText(mSmsModelList.get(position).getMessageText());
        holder.date.setText(simpleDateFormat.format(mSmsModelList.get(position).getSmsDate()));
    }

    private String getContactNameFromNumber(String number) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String name = number;
        Cursor cursor = mContext.getContentResolver().query(uri,
                new String[] { ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);
        if (cursor.moveToFirst()) {
            name = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }
        return name;
    }

    @Override
    public int getItemCount() {
        return mSmsModelList.size();
    }

    public class AllConversationsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        Context mContext;
        TextView phoneNumber;
        TextView message;
        TextView date;
        ImageView personImage;
        String number = "";
        String type = "";

        public AllConversationsHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            phoneNumber = (TextView) itemView.findViewById(R.id.conversation_name);
            message = (TextView)itemView.findViewById(R.id.conversation_lastmsg);
            personImage = (ImageView)itemView.findViewById(R.id.conversation_image);
            date = (TextView)itemView.findViewById(R.id.conversation_lastupdate);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(mContext,ConversationActivity.class);
            intent.putExtra(ALL_CONVERSATIONS_INTENT_KEY,number);
            intent.putExtra(ACTIVITY_INTENT_KEY,ALL_CONVERSATIONS);
            mContext.startActivity(intent);
        }
    }
}
