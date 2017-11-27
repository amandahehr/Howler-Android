package com.howlersafe.howler;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.howlersafe.howler.R;
import com.howlersafe.howler.database.Contact;
import com.howlersafe.howler.database.DatabaseHelper;
import java.util.List;

public class EditContact extends AppCompatActivity implements View.OnClickListener{

    //Widget declarations
    public EditText firstNameEditText;
    public EditText lastNameEditText;
    public EditText phoneNumberEditText;
    public FloatingActionButton saveContactButton;
    public Button deleteContactButton;
    //Variable declarations
    public String currentContactName;
    public static DatabaseHelper db;
    public static Contact thisContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);

        //Get widget references
        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        phoneNumberEditText = findViewById(R.id.mobileEditText);
        saveContactButton = findViewById(R.id.saveContactButton);
        deleteContactButton = findViewById(R.id.deleteContactButton);

        //Listeners
        saveContactButton.setOnClickListener(this);
        deleteContactButton.setOnClickListener(this);

        //Get passed contact
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentContactName = extras.getString("CONTACT_NAME");
        }

        //Database -- get the ID of the passed contact
        db = new DatabaseHelper(this);
        List<Contact> contacts = db.getAllContacts();
        for (Contact cn : contacts) {
            if (cn.getName().equals(currentContactName)){
                thisContact = cn;
            }
        }
        String[] name = currentContactName.split(" ");
        firstNameEditText.setText(name[0]);
        if (name.length < 2) {
            lastNameEditText.setText("");
        } else {
            lastNameEditText.setText(name[1]);
        }
        phoneNumberEditText.setText(thisContact.getPhoneNumber().substring(2));


    }

    //OnClick functions
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveContactButton:
                Contact updatedContact = new Contact(firstNameEditText.getText().toString() + " " + lastNameEditText.getText().toString(), "+1" + phoneNumberEditText.getText().toString());
                db.deleteContact(thisContact);
                db.addContact(updatedContact);
                Intent j = new Intent(getApplicationContext(), ViewContact.class);
                j.putExtra("CONTACT_SELECTED", currentContactName);
                startActivity(j);
                break;
            case R.id.deleteContactButton:
                DeleteContactAlert();
                break;
        }
    }

    public void DeleteContactAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Contact?");
        builder.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                db.deleteContact(thisContact);
                Intent i = new Intent(getApplicationContext(), ContactList.class);
                startActivity(i);
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
    @Override
    public void onBackPressed() {
        Intent j = new Intent(getApplicationContext(), ViewContact.class);
        j.putExtra("CONTACT_SELECTED", currentContactName);
        startActivity(j);
    }
}
