package pl.witoldbrzezinski.esemesolo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import pl.witoldbrzezinski.esemesolo.model.ContactModel;

/**
 * Created by Wiciu on 17.06.2017.
 */

public class AddContactDialogFragment extends android.support.v4.app.DialogFragment implements View.OnClickListener {

    private EditText addContactPhoneEditText;
    private EditText addContactNameEditText;
    private Button addContactButton;

    private OnAddContactClickListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_contact_dialog, container);
        bindViews(view);
        return view;
    }

    private void bindViews(View view){
        addContactPhoneEditText = view.findViewById(R.id.addContactPhoneEditText);
        addContactNameEditText = view.findViewById(R.id.addContactNameEditText);
        addContactButton = view.findViewById(R.id.addContactButton);
        addContactNameEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        addContactButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        String contactName = addContactNameEditText.getText().toString();
        String contactNumber = addContactPhoneEditText.getText().toString();
        if(contactName.isEmpty()||contactNumber.isEmpty()) {
            Toast.makeText(getContext(),R.string.fillBoth,Toast.LENGTH_LONG).show();
        }
        else {
            ContactModel contactModel = new ContactModel(contactNumber,contactName);
            addContact(getContext(),contactModel);
            dismiss();
        }
    }

    public void setListener(OnAddContactClickListener listener) {
        this.listener  = listener;
    }

    public interface OnAddContactClickListener{
        void addContact(Context context, ContactModel contactModel);
    }

    public static void addContact(Context context, ContactModel contact) {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        intent.putExtra(ContactsContract.Intents.Insert.NAME, contact.getContactName());
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, contact.getPhoneNumber());
        context.startActivity(intent);
    }
}
