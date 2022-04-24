package com.example.chirp.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chirp.Common.NodeNames;
import com.example.chirp.R;
import com.example.chirp.SplashActivity;
import com.example.chirp.login.LoginActivity;
import com.example.chirp.password.ChangePasswordActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

/**
 * This class allows the user to perform the necessary
 * profile processes.
 * Such as, updating email, name and profile image.
 */
public class ProfileActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etName;
    private String email, name;

    private ImageView ivProfile;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;

    private StorageReference fileStorage;
    private Uri localFileUri, serverFileUri;
    private FirebaseAuth firebaseAuth;
    private View progressBar;
    private Boolean errorUpdatingEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        etEmail = findViewById(R.id.etEmail);
        etName = findViewById(R.id.etName);
        ivProfile = findViewById(R.id.ivProfile);

        fileStorage = FirebaseStorage.getInstance().getReference();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        progressBar = findViewById(R.id.progressBar);

        if(firebaseUser!=null)
        {
            etName.setText(firebaseUser.getDisplayName());
            etEmail.setText(firebaseUser.getEmail());
            serverFileUri= firebaseUser.getPhotoUrl();

            if(serverFileUri!=null)
            {
                Glide.with(this)
                        .load(serverFileUri)
                        .placeholder(R.drawable.default_profile)
                        .error(R.drawable.default_profile)
                        .into(ivProfile);
            }
        }

    }

    /**
     * The below code executes when the user clicks the logout button.
     * @param view
     */
    public void btnLogoutClick(View view)
    {
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference databaseReference = rootRef.child(NodeNames.TOKENS).child(currentUser.getUid());

        databaseReference.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    firebaseAuth.signOut();

                    //startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                    //finish();
                    finishAffinity();
                }
                else
                {
                    Toast.makeText(ProfileActivity.this, getString(R.string.something_went_wrong, task.getException())
                            , Toast.LENGTH_SHORT).show();
                }
            }
        });



    }


    /**
     * The below code executes when the user clicks the save button.
     * @param view
     */
    public void btnSaveClick(View view)
    {
        if(etName.getText().toString().trim().equals(""))
        {
            etName.setError(getString(R.string.empty_name_field));
        }else if(etEmail.getText().toString().trim().equals(""))
        {
            etEmail.setError(getString(R.string.empty_email_field));
        }
        else
        {
            if(localFileUri!=null)
                updateNameEmailAndPhoto();
            else
                updateNameAndEmail();

        }

    }

    /**
     * The below code executes when the user clicks on their profile image.
     * @param view
     */
    public void changeImage(View view)
    {
        if(serverFileUri==null)
        {
            pickImage();
        }
        else
        {
            PopupMenu popupMenu  = new PopupMenu(this, view);
            popupMenu.getMenuInflater().inflate(R.menu.menu_picture,popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    int id = menuItem.getItemId();

                    if(id==R.id.mnuChangePic)
                    {
                        pickImage();
                    }
                    else if (id==R.id.mnuRemovePic)
                    {
                        removePhoto();
                    }
                    return false;
                }
            });
            popupMenu.show();
        }

    }

    //Allows the user to choose another profile image
    private void pickImage() {

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 101);
        }
        else
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},102);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {

                localFileUri = data.getData();
                ivProfile.setImageURI(localFileUri);
            }

        }
    }

    /**
     * The below code checks to see if permission has been granted to access the
     * users gallery.
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==102)
        {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 101);
            }
            else
            {
                Toast.makeText(this, R.string.permission_required, Toast.LENGTH_SHORT).show();
            }

        }
    }

    /**
     * The below code removes the users profile image.
     */
    private  void removePhoto()
    {
        progressBar.setVisibility(View.VISIBLE);
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(etName.getText().toString().trim())
                .setPhotoUri(Uri.parse("https://firebasestorage.googleapis.com/v0/b/cs4084project-162cb.appspot.com/o/assets%2Fdefault_profile.png?alt=media&token=c4c0d2e8-a938-403f-b97a-ad0b17537514"))
                .build();

        firebaseUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    ivProfile.setImageResource(R.drawable.default_profile);
                    String userID = firebaseUser.getUid();
                    databaseReference = FirebaseDatabase.getInstance().getReference().child(NodeNames.USERS);

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put(NodeNames.PHOTO, "/v0/b/cs4084project-162cb.appspot.com/o/assets/default_profile.png");

                    databaseReference.child(userID).setValue(hashMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    Toast.makeText(ProfileActivity.this, R.string.photo_removed_successfully, Toast.LENGTH_SHORT).show();

                                }
                            });


                } else {
                    Toast.makeText(ProfileActivity.this,
                            getString(R.string.failed_to_update_profile, task.getException()), Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


    /**
     * The below code updates the users name, email and profile image
     * within the firebase database.
     */
    private void updateNameEmailAndPhoto()
    {
        errorUpdatingEmail = false;

        String strFileName= firebaseUser.getUid() + ".jpg";

        final  StorageReference fileRef = fileStorage.child("images/"+ strFileName);
        progressBar.setVisibility(View.VISIBLE);
        fileRef.putFile(localFileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                progressBar.setVisibility(View.GONE);
                if(task.isSuccessful())
                {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            firebaseUser.updateEmail(etEmail.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (!task.isSuccessful()) {
                                        errorUpdatingEmail = true;
                                        Toast.makeText(ProfileActivity.this, getString(R.string.failed_to_update_profile, task.getException()), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            if (errorUpdatingEmail){
                                return;
                            }

                            serverFileUri = uri;
                            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(etName.getText().toString().trim())
                                    .setPhotoUri(serverFileUri)
                                    .build();

                            firebaseUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        String userID = firebaseUser.getUid();
                                        databaseReference = FirebaseDatabase.getInstance().getReference().child(NodeNames.USERS);

                                        HashMap<String, String> hashMap = new HashMap<>();

                                        hashMap.put(NodeNames.NAME, etName.getText().toString().trim());
                                        hashMap.put(NodeNames.PHOTO, serverFileUri.getPath());
                                        hashMap.put(NodeNames.EMAIL, etEmail.getText().toString().trim());

                                        databaseReference.child(userID).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(ProfileActivity.this, getString(R.string.successful_update), Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        });
                                    } else {
                                        Toast.makeText(ProfileActivity.this, getString(R.string.failed_to_update_profile, task.getException()), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }else{
                    Toast.makeText(ProfileActivity.this, getString(R.string.failed_to_update_profile, task.getException()), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /**
     * The below code only updates the users name and email within the firebase database.
     */
    private void updateNameAndEmail() {
        errorUpdatingEmail = false;

        firebaseUser.updateEmail(etEmail.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    errorUpdatingEmail = true;
                    Toast.makeText(ProfileActivity.this, getString(R.string.failed_to_update_profile), Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (errorUpdatingEmail){
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(etName.getText().toString().trim())
                .build();

        firebaseUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    String userID = firebaseUser.getUid();
                    databaseReference = FirebaseDatabase.getInstance().getReference().child(NodeNames.USERS);

                    HashMap<String, String> hashMap = new HashMap<>();

                    hashMap.put(NodeNames.NAME, etName.getText().toString().trim());
                    hashMap.put(NodeNames.EMAIL, etEmail.getText().toString().trim());

                    databaseReference.child(userID).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            finish();
                        }
                    });
                } else {
                    Toast.makeText(ProfileActivity.this, getString(R.string.failed_to_update_profile, task.getException()), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    /**
     * The below code executes on clicking the change password
     * button.
     * @param view
     */
    public  void btnChangePasswordClick(View view)
    {
        startActivity(new Intent(ProfileActivity.this, ChangePasswordActivity.class));
    }
}