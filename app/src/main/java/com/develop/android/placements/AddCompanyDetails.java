package com.develop.android.placements;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.google.android.gms.common.internal.safeparcel.SafeParcelable.NULL;

public class AddCompanyDetails extends AppCompatActivity {
    HashMap<String,String> jd= new HashMap<>();
    HashMap<String,String> ec =new HashMap<>();
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mCompanyDatabaseReference,mFilterDatabaseReference;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private String name="",item,filenameimp="",folder="";

    EditText cname;

    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_company_details);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mCompanyDatabaseReference = mFirebaseDatabase.getReference().child("Company");
        mFilterDatabaseReference = mFirebaseDatabase.getReference().child("Filter");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        Button addField=(Button)findViewById(R.id.addField);
        cname=(EditText)findViewById(R.id.CompanyName);
        final LinearLayout JobLayout=(LinearLayout)findViewById(R.id.addFieldJob);
        final LinearLayout ECLayout=(LinearLayout)findViewById(R.id.addFieldECL);
        addField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.field, null);
                // Add the new row before the add field button.
                JobLayout.addView(rowView, JobLayout.getChildCount() - 1);
            }
        });
        Button addFieldEC=(Button)findViewById(R.id.addFieldEC);
        addFieldEC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View rowView = inflater.inflate(R.layout.fieldec, null);
                // Add the new row before the add field button.
                ECLayout.addView(rowView, ECLayout.getChildCount() - 1);
            }
        });
        final List<String> categories = new ArrayList<String>();
        categories.add("Select");
        categories.add("Core");
        categories.add("Software and Service");
        categories.add("Software and Product");
        Button submit=(Button)findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < JobLayout.getChildCount(); i++) {
                    View view = (View) JobLayout.getChildAt(i);
                    EditText editText = (EditText) view.findViewById(R.id.name_field);
                    EditText editTextone = (EditText) view.findViewById(R.id.value_field);
                    jd.put(editText.getText().toString(), editTextone.getText().toString());
                }
                for (int i = 0; i < ECLayout.getChildCount(); i++) {
                    View view = (View) ECLayout.getChildAt(i);
                    EditText editText = (EditText) view.findViewById(R.id.name_field);
                    EditText editTextone = (EditText) view.findViewById(R.id.value_field);
                    ec.put(editText.getText().toString(), editTextone.getText().toString());
                }
                if (item != categories.get(0))
                {
                    CompanyDetails companyDetails=new CompanyDetails(cname.getText().toString().trim().toLowerCase(),item,jd,ec);
                    mCompanyDatabaseReference.child(cname.getText().toString().trim().toLowerCase()).setValue(companyDetails);
                    mFilterDatabaseReference.child(item).child(cname.getText().toString().trim().toLowerCase()).setValue(cname.getText().toString().trim().toLowerCase());
                    Intent i=new Intent(AddCompanyDetails.this,AddCompanyDetails.class);
                    startActivity(i);
                }
                else
                {
                    Toast.makeText(AddCompanyDetails.this,"Select category",Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner

        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
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
        Button uploadiq=(Button)findViewById(R.id.uploadiq);
        uploadiq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cname.getText().toString().length()>0) {
                    name = cname.getText().toString().trim().toLowerCase();
                    folder="questions";
                    filenameimp=name+"_questions.pdf";
                    chooseFile();
                }
                else
                {
                    Toast.makeText(AddCompanyDetails.this,"Enter company name",Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button uploadid=(Button)findViewById(R.id.uploadid);
        uploadid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cname.getText().toString().length()>0) {
                    name = cname.getText().toString().trim().toLowerCase();
                    folder="details";
                    filenameimp= name+"_details.pdf";
                    chooseFile();
                }
                else
                {
                    Toast.makeText(AddCompanyDetails.this,"Enter company name",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void onDelete(View v) {
        final LinearLayout JobLayout=(LinearLayout)findViewById(R.id.addFieldJob);
        JobLayout.removeView((View) v.getParent());
    }
    public void onDeleteEC(View v) {
        final LinearLayout ECLayout=(LinearLayout)findViewById(R.id.addFieldECL);
        ECLayout.removeView((View) v.getParent());
    }
    public void chooseFile() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            uploadFile();
        }
    }
    public void uploadFile() {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            StorageReference ref = storageReference.child(folder+"/"+filenameimp);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(AddCompanyDetails.this, folder+"Uploaded", Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AddCompanyDetails.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }
}
