package com.howlersafe.howler;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.howlersafe.howler.R;
import com.howlersafe.howler.database.Contact;
import com.howlersafe.howler.database.DatabaseHelper;
import java.util.ArrayList;
import java.util.List;

public class ContactList extends AppCompatActivity implements View.OnClickListener{

    //Widget declarations
    public FloatingActionButton addNewContact;
    public ListView contactList;
    //Variable declarations
    public DatabaseHelper db;
    Intent i;
    //for adapter
    ArrayList<String> listItems=new ArrayList<String>();
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
            }
        });

        //Database
        db = new DatabaseHelper(this);

        //List adapter
        adapter=new ArrayAdapter<String>(this, R.layout.image_row_layout, R.id.Itemname, listItems);
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

    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
    }

}
