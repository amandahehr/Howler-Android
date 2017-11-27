package com.howlersafe.howler;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.howlersafe.howler.R;
import com.howlersafe.howler.database.Contact;
import com.howlersafe.howler.database.DatabaseHelper;
import java.util.List;

public class ViewContact extends AppCompatActivity implements View.OnClickListener {

    //Widget declarations
    public TextView contactName;
    public TextView contactNumber;
    public FloatingActionButton editContactButton;
    //Variable declarations
    public String currentContactName;
    public DatabaseHelper db;
    public Contact thisContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contact);

        //Get widget references
        contactName = findViewById(R.id.contactName);
        contactNumber = findViewById(R.id.contactNumber);
        editContactButton = findViewById(R.id.editContactButton);

        //Listeners
        editContactButton.setOnClickListener(this);

        //Get passed contact
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentContactName = extras.getString("CONTACT_SELECTED");
        }

        //Database -- get the ID of the passed contact
        db = new DatabaseHelper(this);
        List<Contact> contacts = db.getAllContacts();
        for (Contact cn : contacts) {
            if (cn.getName().equals(currentContactName)){
                thisContact = cn;
            }
        }

        contactName.setText(thisContact.getName());
        String subString1 = thisContact.getPhoneNumber().substring(2, 5);
        String subString2 = thisContact.getPhoneNumber().substring(5, 8);
        String subString3 = thisContact.getPhoneNumber().substring(8);
        String phone = "+1 (" + subString1 + ") " + subString2 + "-" + subString3;
        contactNumber.setText(phone);


    }

    //OnClick functions
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editContactButton:
                Intent i = new Intent(getApplicationContext(), EditContact.class);
                i.putExtra("CONTACT_NAME", currentContactName);
                startActivity(i);
                break;
        }
    }
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), ContactList.class);
        startActivity(i);
    }
}
