package com.howlersafe.howler;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.howlersafe.howler.R;
import com.howlersafe.howler.database.Contact;
import com.howlersafe.howler.database.DatabaseHelper;

public class AddContact extends AppCompatActivity implements View.OnClickListener{

    //Widget declarations
    public EditText firstName;
    public EditText lastName;
    public EditText mobile1;
    public FloatingActionButton saveContact;
    //Variable declarations
    public DatabaseHelper db;
    public String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);


        //Get widget references
        saveContact =  findViewById(R.id.saveContactButton);
        firstName = findViewById(R.id.firstNameEditText);
        lastName = findViewById(R.id.lastNameEditText);
        mobile1 = findViewById(R.id.mobileEditText);

        //Listeners
        saveContact.setOnClickListener(this);

        //Database
        db = new DatabaseHelper(this);

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveContactButton:
                if (mobile1.getText().length() != 10){
                    Toast.makeText(this, "Invalid phone number.", Toast.LENGTH_LONG).show();
                    break;

                }
                else if (firstName.getText().length() == 0){
                    Toast.makeText(this, "Enter a contact name.", Toast.LENGTH_LONG).show();
                    break;
                }
                else {
                    phoneNumber = "+1" + mobile1.getText().toString();
                    saveContact();
                    break;
                }

        }
    }

    public void saveContact(){
        db.addContact(new Contact(firstName.getText().toString()+ " " + lastName.getText().toString(), phoneNumber));
        Intent i = new Intent(getApplicationContext(), ContactList.class);
        startActivity(i);
    }
}
