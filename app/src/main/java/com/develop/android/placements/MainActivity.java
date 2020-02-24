package com.develop.android.placements;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final int RC_SIGN_IN = 1;
    private FirebaseAuth mFirebaseAuth;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    private StorageReference ref;

    String filename="";
    String anuragh="anuraghyarlagadda@gmail.com";
    String yamini="sweetybandi@gmail.com";
    String admin="ramakrishna_p@vnrvjiet.in";
    String harini="ganeshanharini28@gmail.com";
    String usermail="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if( user != null){
                    //user is signedin
                    usermail=user.getEmail();
                    Toast.makeText(MainActivity.this, "You're now signed in."+usermail, Toast.LENGTH_SHORT).show();
                }
                else{
                    //user is signed out
                    // Choose authentication providers
                    List<AuthUI.IdpConfig> providers = Arrays.asList(
                            new AuthUI.IdpConfig.EmailBuilder().build(),
                            new AuthUI.IdpConfig.GoogleBuilder().build());

                    // Create and launch sign-in intent
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(providers)
                                    .build(),
                            RC_SIGN_IN);
                }

            }
        };
        Button addCompany = (Button) findViewById(R.id.addCompany);
        addCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(usermail.equals(anuragh) || usermail.equals(yamini) || usermail.equals(admin) ||usermail.equals(harini)) {
                    Intent i = new Intent(MainActivity.this, AddCompanyDetails.class);
                    startActivity(i);
                }
                else
                {
                    Toast.makeText(MainActivity.this,"You are not an Intented User",Toast.LENGTH_SHORT).show();
                }
            }
        });
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        Button addAlumni=(Button)findViewById(R.id.AddAlumniExcel);
        addAlumni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(usermail.equals(anuragh) || usermail.equals(yamini) || usermail.equals(admin) ||usermail.equals(harini))
                {
                    filename="AlumniDetails.xlsx";
                    chooseFile(1);
                }
                else
                {
                    Toast.makeText(MainActivity.this,"You are not an Intented User",Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button addlistofcom=(Button)findViewById(R.id.Addlistofcom);
        addlistofcom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(usermail.equals(anuragh) || usermail.equals(yamini) || usermail.equals(admin) ||usermail.equals(harini))
                {
                    filename="listofcompanies.xlsx";
                    chooseFile(1);
                }
                else
                {
                    Toast.makeText(MainActivity.this,"You are not an Intented User",Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button addresume=(Button)findViewById(R.id.addsampleresume);
        addresume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(usermail.equals(anuragh) || usermail.equals(yamini) || usermail.equals(admin) ||usermail.equals(harini))
                {
                    filename="sampleresume.pdf";
                    chooseFile(2);
                }
                else
                {
                    Toast.makeText(MainActivity.this,"You are not an Intented User",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_signout:
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }
    public void chooseFile(int flag) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        if(flag==1)
            intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        else if(flag==2)
            intent.setType("application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
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
            ref= storageReference.child(filename);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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
    public String examine(Uri uri) {
        String extension = "";
        if (uri != null) {
            String path = new File(uri.getPath()).getAbsolutePath();
            if (path != null) {
                String filename;
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                if (cursor == null) { // Source is Dropbox or other similar local file path
                    filename = uri.getPath();
                } else {
                    cursor.moveToFirst();
                    int idx = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME);
                    filename = cursor.getString(idx);
                    cursor.close();
                }
                if (filename != null) {
                    String name = filename.substring(filename.lastIndexOf("."));
                    extension = filename.substring(filename.lastIndexOf(".") + 1);
                }

            }
        }
        return extension;
    }
}
