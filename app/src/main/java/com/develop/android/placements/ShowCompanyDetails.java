package com.develop.android.placements;

import android.Manifest;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.util.JsonToken.NULL;
import static java.lang.Thread.sleep;


public class ShowCompanyDetails extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mCompanyDatabaseReference,mFilterDatabaseReference;
    private FirebaseStorage storage;
    private ChildEventListener mChildEventListener;
    private StorageReference storageReference;
    TextView cname,jd,ec;
    Button showid,showiq,downid,downiq;
    HashMap<String, String> jdhm = new HashMap<String, String>();
    HashMap<String, String> echm = new HashMap<String, String>();
    public static final String PROGRESS_UPDATE = "progress_update";
    private static final int PERMISSION_REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_company_details);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mCompanyDatabaseReference = mFirebaseDatabase.getReference().child("Company");
        mFilterDatabaseReference = mFirebaseDatabase.getReference().child("Filter");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        requestAppPermissions();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        cname=(TextView)findViewById(R.id.CompanyName);

        final TextView jdhead=(TextView)findViewById(R.id.JobDescription);
        final TextView echead=(TextView)findViewById(R.id.EligibityCriteria);

        jd=(TextView)findViewById(R.id.JobDescriptionDetails);
        ec=(TextView)findViewById(R.id.EligibityCriteriaDetails);
        downid=(Button)findViewById(R.id.downloadinterviewdetails);
        downiq=(Button)findViewById(R.id.downloadinterviewquestions);
        showid=(Button)findViewById(R.id.showinterviewdetails);
        showiq=(Button)findViewById(R.id.showinterviewquestions);
        Intent intent = getIntent();
        final String companyName = intent.getStringExtra("companyName");
        Query query=mCompanyDatabaseReference.child(companyName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CompanyDetails cd=dataSnapshot.getValue(CompanyDetails.class);
                cname.setText(cd.companyName);
                jdhm=cd.jd;
                echm=cd.ec;
                if(jdhm!=null){
                    if(!jdhm.isEmpty()) {
                        for (Map.Entry mapElement : jdhm.entrySet()) {
                            String key = (String) mapElement.getKey();
                            String value = (String) mapElement.getValue();
                            jd.setText(jd.getText() + key + " : " + value + "\n");
                        }
                    }
                }
                else
                {
                    jdhead.setVisibility(View.GONE);
                }
                if(echm!=null) {
                    if(!echm.isEmpty()) {
                        for (Map.Entry mapElement : echm.entrySet()) {
                            String key = (String) mapElement.getKey();
                            String value = (String) mapElement.getValue();
                            ec.setText(ec.getText() + key + " : " + value + "\n");
                        }
                    }
                }
                else
                {
                    echead.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        downiq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission();
                if (!(hasReadPermissions() && hasWritePermissions())) {
                    Toast.makeText(ShowCompanyDetails.this,"Grant Access to external storage",Toast.LENGTH_SHORT).show();
                }
                StorageReference ref = storageReference.child(companyName+"Interview_questions.pdf");
                String directory_path = Environment.getExternalStorageDirectory().getPath() + "/Placements/";
                File file = new File(directory_path);
                if (!file.exists()) {
                    file.mkdirs();
                }
                File fileNameOnDevice = new File(directory_path+"/"+companyName+"Interview_questions.pdf");
                ref.getFile(fileNameOnDevice).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(ShowCompanyDetails.this,"PDF File Saved",Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        downid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestAppPermissions();
                if (!(hasReadPermissions() && hasWritePermissions())) {
                    Toast.makeText(ShowCompanyDetails.this,"Grant Access to external storage",Toast.LENGTH_SHORT).show();
                }
                StorageReference ref = storageReference.child(companyName).child("Files/"+companyName+"Interview_details.pdf");
                String directory_path = Environment.getExternalStorageDirectory().getPath() + "/Placements/";
                File file = new File(directory_path);
                if (!file.exists()) {
                    file.mkdirs();
                }
                File fileNameOnDevice = new File(directory_path+"/"+companyName+"Interview_details.pdf");
                ref.getFile(fileNameOnDevice).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(ShowCompanyDetails.this,"PDF File Saved",Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        showiq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String directory_path = Environment.getExternalStorageDirectory().getPath() + "/Placements/";
                File pdfFile = new File(directory_path+"/"+companyName+"Interview_questions.pdf");
                if (pdfFile.exists()) {
                    Uri excelPath;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                        excelPath = FileProvider.getUriForFile(ShowCompanyDetails.this, "com.develop.android.placements", pdfFile);
                    } else {
                        excelPath = Uri.fromFile(pdfFile);
                    }
                    Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                    pdfIntent.setDataAndType(excelPath, "application/pdf");
                    pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    pdfIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    try {
                        startActivity(pdfIntent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(ShowCompanyDetails.this, "No Application available to view PDF", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ShowCompanyDetails.this, "File not found!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        showid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String directory_path = Environment.getExternalStorageDirectory().getPath() + "/Placements/";
                File pdfFile = new File(directory_path+"/"+companyName+"Interview_details.pdf");
                if (pdfFile.exists()) {
                    Uri excelPath;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                        excelPath = FileProvider.getUriForFile(ShowCompanyDetails.this, "com.develop.android.placements", pdfFile);
                    } else {
                        excelPath = Uri.fromFile(pdfFile);
                    }
                    Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                    pdfIntent.setDataAndType(excelPath, "application/pdf");
                    pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    pdfIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    try {
                        startActivity(pdfIntent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(ShowCompanyDetails.this, "No Application available to view PDF", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ShowCompanyDetails.this, "File not found!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void requestAppPermissions() {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        if (hasReadPermissions() && hasWritePermissions()) {
            return;
        }

        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, 112); // your request code
    }

    private boolean hasReadPermissions() {
        return (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    private boolean hasWritePermissions() {
        return (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {

                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();

                }
                break;
        }
    }
}
