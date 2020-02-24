package com.develop.android.placements;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ShowCategories extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mCompanyDatabaseReference;
    private FirebaseStorage storage;
    private ChildEventListener mChildEventListener;
    private StorageReference storageReference;
    ArrayList<String> list = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    Button go;
    String item;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_categories);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mCompanyDatabaseReference = mFirebaseDatabase.getReference().child("Company");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        final List<String> categories = new ArrayList<String>();
        categories.add("Select");
        categories.add("Core");
        categories.add("Software and Service");
        categories.add("Software and Product");

        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
        spinner.setSelection(0);
        // Spinner click listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                item = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        go=(Button)findViewById(R.id.go);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(item!=categories.get(0))
                {
                    Intent i=new Intent(ShowCategories.this,ShowCompany.class);
                    i.putExtra("Filter",item);
                    startActivity(i);
                    Toast.makeText(ShowCategories.this, "Selected Category: " + item, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
