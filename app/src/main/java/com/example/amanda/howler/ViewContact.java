package com.example.amanda.howler;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.amanda.howler.R;
import com.example.amanda.howler.database.Contact;
import com.example.amanda.howler.database.DatabaseHelper;

import org.w3c.dom.Text;

import java.util.List;

public class ViewContact extends AppCompatActivity implements View.OnClickListener {

    public String currentContactName;
    public TextView contactName;
    public TextView contactNumber;
    public ImageButton deleteContactButton;
    public ImageButton editContactButton;
    public DatabaseHelper db;
    public Contact thisContact;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contact);

        //Get widget references
        contactName = (TextView) findViewById(R.id.contactName);
        contactNumber = (TextView) findViewById(R.id.contactNumber);
        deleteContactButton = (ImageButton) findViewById(R.id.deleteContactButton);
        editContactButton = (ImageButton) findViewById(R.id.editContactButton);

        //Listeners
        deleteContactButton.setOnClickListener(this);
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
        contactNumber.setText(thisContact.getPhoneNumber());


    }

    //OnClick functions
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.deleteContactButton:
                db.deleteContact(thisContact);
                Intent i = new Intent(getApplicationContext(), ContactList.class);
                startActivity(i);
                break;
            case R.id.editContactButton:
                break;
        }
    }
}
