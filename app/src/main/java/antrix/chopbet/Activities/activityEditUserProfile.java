package antrix.chopbet.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.dx.dxloadingbutton.lib.LoadingButton;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Date;
import java.util.Objects;

import antrix.chopbet.BetClasses.BaseActivity;
import antrix.chopbet.BetClasses.BetUtilities;
import antrix.chopbet.BetClasses.ImageUtils;
import antrix.chopbet.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class activityEditUserProfile extends BaseActivity implements ImageUtils.ImageAttachmentListener {

    EditText nameTextView, psnTextView, originTextView, xboxLiveTextView;
    CircleImageView profileImage;
    Button cancelButton, acceptButton;

    Context context;

    String myPhoneNumber;
    String phoneNumber;
    FirebaseAuth mAuth;
    DatabaseReference dbRef;
    DatabaseReference profileDbRef;
    StorageReference storageReference;
    StorageReference profileStorageRef;

    Bundle bundle;
    String userName;

    ProgressDialog progressDialog;

    LoadingButton btnSave;

    ImageUtils imageUtils;
    private Bitmap bitmap;
    private String file_name;
    TextView changeProfilePicture;
    BetUtilities betUtilities;

    int nameCheck, psnCheck, xboxLiveCheck, originCheck = 0;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_user_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        loadActionbar("Edit Profile");


        declarations();
        clickers();

        loadProfileImage();
        loadOldValued();






    }


    private void declarations(){

        context = this;

        dbRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        myPhoneNumber = mAuth.getCurrentUser().getPhoneNumber();

        imageUtils = new ImageUtils(this);


        nameTextView = (EditText)findViewById(R.id.nameTextView);
        psnTextView = (EditText)findViewById(R.id.psnTextView);
        originTextView = (EditText)findViewById(R.id.originTextView);
        xboxLiveTextView = (EditText)findViewById(R.id.xboxLiveTextView);
        profileImage = (CircleImageView)findViewById(R.id.profileImage);
        cancelButton = (Button)findViewById(R.id.cancelButton);
        acceptButton = (Button)findViewById(R.id.acceptButton);
        changeProfilePicture = (TextView)findViewById(R.id.changeProfilePicture);



        // get bundled country code from registerLogin activity
        bundle = getIntent().getExtras();
        userName = bundle.getString("userName");

        profileDbRef = dbRef.child("UserNames").child(userName);

        progressDialog = new ProgressDialog(context);

        btnSave = (LoadingButton)findViewById(R.id.btnSave);

        storageReference = FirebaseStorage.getInstance().getReference();
        profileStorageRef = storageReference.child("ProfileImages").child(userName).child(userName);


        betUtilities = new BetUtilities();


    }


    private void loadProfileImage(){



        dbRef.child("profileImageTimestamp").child(userName)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChildren()){

                            String timestamp = dataSnapshot.child(userName).getValue().toString();
                            StorageReference profileStorageRef = FirebaseStorage.getInstance().getReference()
                                    .child("ProfileImages").child(userName).child(userName);


                            betUtilities.CircleImageFromFirebase(activityEditUserProfile.this, profileStorageRef, profileImage, timestamp);

                        }



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



    }

    private void loadOldValued(){

        profileDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()){
                    if (dataSnapshot.child("psn").getValue() != null){
                        psnTextView.setText(dataSnapshot.child("psn").getValue().toString());
                        psnCheck = 1;

                    }

                    if (dataSnapshot.child("xboxLive").getValue() != null){
                        xboxLiveTextView.setText(dataSnapshot.child("xboxLive").getValue().toString());
                        xboxLiveCheck = 1;
                    }


                    if (dataSnapshot.child("origin").getValue() != null){
                        originTextView.setText(dataSnapshot.child("origin").getValue().toString());
                        originCheck = 1;
                    }

                    if (dataSnapshot.child("name").getValue() != null){
                        nameTextView.setText(dataSnapshot.child("name").getValue().toString());
                        nameCheck = 1;
                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }




    private void clickers(){

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSave.startLoading();
                if (!Objects.equals(nameTextView.getText().toString().trim(), "")){
                    profileDbRef.child("name").setValue(nameTextView.getText().toString().trim());
                }else if (Objects.equals(nameTextView.getText().toString().trim(), "")){
                    if (nameCheck == 1){
                        nameTextView.setHint("Enter Name");
                        nameTextView.setHintTextColor(getResources().getColor(R.color.colorPrimary));
                        btnSave.cancelLoading();
                        btnSave.reset();
                        return;
                    }
                }

                if(!Objects.equals(psnTextView.getText().toString().trim(), "")){
                    profileDbRef.child("psn").setValue(psnTextView.getText().toString().trim());
                }else if(Objects.equals(psnTextView.getText().toString().trim(), "")){
                   if (psnCheck == 1){
                       profileDbRef.child("psn").removeValue();
                   }
                }


                if(!Objects.equals(originTextView.getText().toString().trim(), "")){
                    profileDbRef.child("origin").setValue(originTextView.getText().toString().trim());
                }else if(Objects.equals(originTextView.getText().toString().trim(), "")){
                    if (originCheck == 1){
                        profileDbRef.child("origin").removeValue();
                    }
                }


                if(!Objects.equals(xboxLiveTextView.getText().toString().trim(), "")){
                    profileDbRef.child("xboxLive").setValue(xboxLiveTextView.getText().toString().trim());
                }else if(Objects.equals(xboxLiveTextView.getText().toString().trim(), "")){
                    if (xboxLiveCheck == 1){
                        profileDbRef.child("xboxLive").removeValue();
                    }
                }



                finish();
                btnSave.cancelLoading();
                btnSave.reset();
            }
        });



        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUtils.isDeviceSupportCamera())
                    imageUtils.imagepicker(1);
                else imageUtils.imagepicker(0);
            }
        });

        changeProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUtils.isDeviceSupportCamera())
                    imageUtils.imagepicker(1);
                else imageUtils.imagepicker(0);
            }
        });


    }


    public void loadActionbar(String title){

        final ActionBar abar = getSupportActionBar();
        //abar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_background));//line under the action bar
        View viewActionBar = getLayoutInflater().inflate(R.layout.actionbar_titletext_layout, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textviewTitle = (TextView) viewActionBar.findViewById(R.id.actionbar_textview);
        textviewTitle.setText(title);
        abar.setCustomView(viewActionBar, params);
        abar.setDisplayShowCustomEnabled(true);
        abar.setDisplayShowTitleEnabled(false);
        //abar.setDisplayHomeAsUpEnabled(true);
        abar.setIcon(R.color.transparent);
        abar.setHomeButtonEnabled(true);

    }



    public void image_attachment(int from, String filename, Bitmap file, Uri uri) {



            this.bitmap=file;
            this.file_name=filename;
            profileImage.setImageBitmap(file);

            String path =  Environment.getExternalStorageDirectory() + File.separator + "ImageAttach" + File.separator;
            imageUtils.createImage(file,filename,path,false);


            // Get the data from an ImageView as bytes
            profileImage.setDrawingCacheEnabled(true);
            profileImage.buildDrawingCache();
            Bitmap bitmap = profileImage.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            progressDialog.setMessage("Image uploading...");
            progressDialog.show();



            UploadTask uploadTask = profileStorageRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {


                    progressDialog.dismiss();
                    Toast.makeText(activityEditUserProfile.this, "error encountered, retry", Toast.LENGTH_SHORT).show();

                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    /// add new timestamp for image for caching purposes
                    FirebaseDatabase.getInstance().getReference().child("profileImageTimestamp")
                            .child(userName).child(userName).setValue(new Date().getTime());

                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    Toast.makeText(activityEditUserProfile.this, "Profile picture changed", Toast.LENGTH_SHORT).show();
                    Glide.with(activityEditUserProfile.this).using(new FirebaseImageLoader()).load(profileStorageRef)
                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                            .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                            .error(R.drawable.ic_profile).centerCrop().into(profileImage);
                    progressDialog.dismiss();


                }
            });











    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);
        imageUtils.onActivityResult(requestCode, resultCode, data);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        imageUtils.request_permission_result(requestCode, permissions, grantResults);
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}

