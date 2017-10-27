package com.example.amanda.howler;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.amanda.howler.database.Contact;
import com.example.amanda.howler.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class ContactList extends AppCompatActivity implements View.OnClickListener{

    public FloatingActionButton addNewContact;
    public ListView contactList;
    public DatabaseHelper db;
    Intent i;

    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> listItems=new ArrayList<String>();
    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        //Get widget references
        addNewContact = (FloatingActionButton) findViewById(R.id.addContactsButton);
        contactList = (ListView) findViewById(R.id.contactListView);

        //Listeners
        addNewContact.setOnClickListener(this);
        contactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String s = contactList.getItemAtPosition(i).toString();

                Intent j = new Intent(getApplicationContext(), ViewContact.class);
                j.putExtra("CONTACT_SELECTED", s);
                startActivity(j);


//                Toast.makeText(activity.getApplicationContext(), s, Toast.LENGTH_LONG).show();
//                adapter.dismiss(); // If you want to close the adapter
            }
        });

        //Database
        db = new DatabaseHelper(this);

        //List adapter
        adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
        contactList.setAdapter(adapter);

        List<Contact> contacts = db.getAllContacts();
        for (Contact cn : contacts) {
            adapter.add(cn.getName());
        }
    }

    //OnClick Functions
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addContactsButton:
                i = new Intent(getApplicationContext(), AddContact.class);
                startActivity(i);
                break;
        }
    }
}
