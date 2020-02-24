package com.develop.android.placements;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

public class ShowCompany extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mCompanyDatabaseReference,mFilterDatabaseReference;
    private FirebaseStorage storage;
    private ChildEventListener mChildEventListener;
    private StorageReference storageReference;
    ArrayList<String> list = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    String item;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_company);
        Intent i=getIntent();
        item=i.getStringExtra("Filter");
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mCompanyDatabaseReference = mFirebaseDatabase.getReference().child("Company");
        mFilterDatabaseReference = mFirebaseDatabase.getReference().child("Filter").child(item);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        ListView lv=(ListView)findViewById(R.id.company_list);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = ((TextView)view).getText().toString();
                Toast.makeText(getBaseContext(), item, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ShowCompany.this, ShowCompanyDetails.class);
                intent.putExtra("companyName",item);
                ShowCompany.this.startActivity(intent);
            }
        });

        mChildEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String cd = dataSnapshot.getValue(String.class);
                adapter.add(cd);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mFilterDatabaseReference.addChildEventListener(mChildEventListener);
    }
}
