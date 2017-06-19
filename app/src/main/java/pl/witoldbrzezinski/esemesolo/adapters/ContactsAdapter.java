package pl.witoldbrzezinski.esemesolo.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import pl.witoldbrzezinski.esemesolo.ConversationActivity;
import pl.witoldbrzezinski.esemesolo.R;
import pl.witoldbrzezinski.esemesolo.model.ContactModel;
import pl.witoldbrzezinski.esemesolo.realm.DatabaseManager;

import static pl.witoldbrzezinski.esemesolo.adapters.AllConversationsAdapter.ACTIVITY_INTENT_KEY;

/**
 * Created by Wiciu on 17.06.2017.
 */

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    List<ContactModel> mContactModel;
    Context mContext;
    DatabaseManager databaseManager = DatabaseManager.getInstance();

    public static final String CONTACTS_INTENT_KEY = "CONTACTS_INTENT";
    public static final String CONTACTS = "CONTACTS";

    public ContactsAdapter(List<ContactModel> mContactModel) {
        this.mContactModel = mContactModel;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_item,parent,false);
        ContactsAdapter.ViewHolder contactsHolder = new ContactsAdapter.ViewHolder(view);
        return contactsHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ContactModel contactModel = mContactModel.get(position);
        mContext = holder.itemView.getContext();
        holder.phoneNumber.setText(contactModel.getPhoneNumber());
        holder.name.setText(getContactNameFromNumber(contactModel.getPhoneNumber()));
        holder.contactNumber = contactModel.getPhoneNumber();
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(R.string.deleteContactConfirm);
                builder.setCancelable(true);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteContact(mContext,contactModel.getPhoneNumber(),contactModel.getContactName());
                        databaseManager.deleteContactItem(contactModel);
                        notifyDataSetChanged();

                    }
                });
                builder.setNegativeButton(R.string.no,null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;
            }
        });
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
        return mContactModel.size();
    }
    public static boolean deleteContact(Context context, String phone, String name) {
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));
        Cursor cursor = context.getContentResolver().query(contactUri, null, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    if (cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)).equalsIgnoreCase(name)) {
                        String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
                        context.getContentResolver().delete(uri,null,null);
                        return true;
                    }

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }finally {
            cursor.close();
        }
        return false;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView phoneNumber;
        TextView name;
        Context mContext;
        ImageView contactImage;
        String contactNumber = "";
        View itemView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            mContext = itemView.getContext();
            contactImage = itemView.findViewById(R.id.contact_image);
            phoneNumber = itemView.findViewById(R.id.contact_number);
            name = itemView.findViewById(R.id.contact_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, ConversationActivity.class);
            intent.putExtra(CONTACTS_INTENT_KEY, contactNumber);
            intent.putExtra(ACTIVITY_INTENT_KEY,CONTACTS);
            mContext.startActivity(intent);
        }
    }
}
