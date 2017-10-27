package com.example.amanda.howler;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.amanda.howler.database.Contact;
import com.example.amanda.howler.database.DatabaseHelper;

public class AddContact extends AppCompatActivity implements View.OnClickListener{

    public EditText firstName;
    public EditText lastName;
    public EditText phoneNumber;
    public Button saveContact;
    public DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        //Get widget references
        saveContact = (Button) findViewById(R.id.saveContactButton);
        firstName = (EditText) findViewById(R.id.firstNameEditText);
        lastName = (EditText) findViewById(R.id.lastNameEditText);
        phoneNumber = (EditText) findViewById(R.id.phoneEditText);

        //Listeners
        saveContact.setOnClickListener(this);

        //Database
        db = new DatabaseHelper(this);

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveContactButton:
                saveContact();
                break;
        }
    }

    public void saveContact(){
        db.addContact(new Contact(firstName.getText().toString(), phoneNumber.getText().toString()));
        Intent i = new Intent(getApplicationContext(), ContactList.class);
        startActivity(i);
    }
}
