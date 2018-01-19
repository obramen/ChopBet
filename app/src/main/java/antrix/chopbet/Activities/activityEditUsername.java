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
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Base64;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import antrix.chopbet.BetClasses.BaseActivity;
import antrix.chopbet.BetClasses.BetUtilities;
import antrix.chopbet.BetClasses.ImageUtils;
import antrix.chopbet.Models.BetBuddy;
import antrix.chopbet.R;
import de.hdodenhof.circleimageview.CircleImageView;
import tgio.rncryptor.RNCryptorNative;


public class activityEditUsername extends BaseActivity implements ImageUtils.ImageAttachmentListener{

    EditText userNameTextView;
    Button acceptButton;
    CircleImageView profileImage;

    Context context;

    String myPhoneNumber;
    FirebaseAuth mAuth;
    DatabaseReference dbRef;
    FirebaseFirestore fireDbRef;
    StorageReference storageReference;
    StorageReference profileStorageRef;

    public String username;

    TextInputLayout textInputLayout;
    TextView hintTextView;

    ProgressDialog progressDialog;

    LoadingButton btnSave;

    ImageUtils imageUtils;
    private Bitmap bitmap;
    private String file_name;
    byte[] data = null;
    TextView changeProfilePicture;
    BetUtilities betUtilities;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_username);
        loadActionbar("Set Username");
        getSupportActionBar().setElevation(0);

        declarations();
        clickers();
    }


    private void declarations(){

        context = this;

        dbRef = FirebaseDatabase.getInstance().getReference();
        fireDbRef = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        myPhoneNumber = mAuth.getCurrentUser().getPhoneNumber();


        userNameTextView = (EditText)findViewById(R.id.userNameTextView);
        profileImage = (CircleImageView)findViewById(R.id.profileImage);
        acceptButton = (Button)findViewById(R.id.acceptButton);
        textInputLayout = (TextInputLayout)findViewById(R.id.textInputLayout);
        hintTextView = (TextView)findViewById(R.id.hintTextView);
        changeProfilePicture = (TextView)findViewById(R.id.changeProfilePicture);


        username = "";

        progressDialog = new ProgressDialog(context);

        btnSave = (LoadingButton)findViewById(R.id.btnSave);

        imageUtils = new ImageUtils(this);

        storageReference = FirebaseStorage.getInstance().getReference();






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


    private void clickers(){

        acceptButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                hintTextView.setText("");

                if (TextUtils.isEmpty(userNameTextView.getText().toString())) {
                    textInputLayout.setHint("Enter username");
                    return;
                }

                userNameTextView.setEnabled(false);
                acceptButton.setEnabled(false);

                progressDialog.setMessage("Verifying username...");
                progressDialog.show();

                username = userNameTextView.getText().toString().trim().toLowerCase();

                final DatabaseReference userDbRef = dbRef.child("UserNames");

                userDbRef.child("UserNames").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChildren()){

                            if (dataSnapshot.child(username).exists()){
                                hintTextView.setText(username + " is not available");
                                progressDialog.dismiss();

                                userNameTextView.setEnabled(true);
                                acceptButton.setEnabled(true);


                            }else {
                                saveUserName();
                                userDbRef.removeEventListener(this);
                                progressDialog.dismiss();
                            }
                        }

                        else {
                            saveUserName();
                            userDbRef.removeEventListener(this);
                            progressDialog.dismiss();



                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });






            }
        });



        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                hintTextView.setText("");

                if (TextUtils.isEmpty(userNameTextView.getText().toString())) {
                    textInputLayout.setHint("Enter username");
                    btnSave.cancelLoading();
                    btnSave.reset();
                    return;

                }

                btnSave.startLoading();

                userNameTextView.setEnabled(false);
                acceptButton.setEnabled(false);

                progressDialog.setMessage("Verifying username...");
                //progressDialog.show();

                username = userNameTextView.getText().toString().trim().toLowerCase();

                final DatabaseReference userDbRef = dbRef.child("UserNames");

                dbRef.child("UserNames").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChildren()){

                            if (dataSnapshot.child(username).exists()){
                                hintTextView.setText(username + " is not available");
                                progressDialog.dismiss();
                                btnSave.cancelLoading();
                                btnSave.reset();

                                userNameTextView.setEnabled(true);
                                acceptButton.setEnabled(true);


                            }else {
                                saveUserName();
                                userDbRef.removeEventListener(this);
                                progressDialog.dismiss();
                                btnSave.cancelLoading();
                                btnSave.reset();
                            }
                        }

                        else {
                            saveUserName();
                            userDbRef.removeEventListener(this);
                            progressDialog.dismiss();
                            btnSave.cancelLoading();
                            btnSave.reset();



                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });








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

    public void saveUserName(){

        long joinDate = new Date().getTime();


        Map<String, Object> mUserName = new HashMap<>();
        Map<String, Object> xUserName = new HashMap<>();
        mUserName.put(username, myPhoneNumber);
        xUserName.put("userName", username);
        xUserName.put("phoneNumber", myPhoneNumber);
        xUserName.put("joinDate", joinDate);
        BetBuddy betBuddy = new BetBuddy(username);




        String myUID = mAuth.getUid();
        String rUserName = new StringBuffer(username).reverse().toString();

        String goldKey = dbRef.child("Xperience").push().getKey();
        dbRef.child("Xperience").child(goldKey).child("Oxygen").child("Bounty").setValue("0.00");


        RNCryptorNative rnCryptorNative = new RNCryptorNative();
        String diamondKey = new String(rnCryptorNative.encrypt(goldKey, myUID));



        dbRef.child("UserNames").child(username).setValue(xUserName);
        dbRef.child("Xhaust").child(rUserName).child("liquidNitrogen").setValue(diamondKey);
        dbRef.child("UserInfo").child(myPhoneNumber).updateChildren(xUserName).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Intent intent = new Intent(context, activityChopBet.class);
                intent.putExtra("countryCodeStatus", "0");
                intent.putExtra("countryCode", "0");
                startActivity(intent);
                finish();
                dbRef.child("MatchObserver").child(username).child("matchStatus").setValue("Open");
                dbRef.child("MatchObserver").child(username).child("currentMatchID").setValue("null");
                Toast.makeText(context, "Username created successfully", Toast.LENGTH_LONG).show();



            }
        });








        if (data != null){




            progressDialog.setMessage("Image uploading...");
            progressDialog.show();


            profileStorageRef = storageReference.child("ProfileImages").child(username).child(username);


            UploadTask uploadTask = profileStorageRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {


                    progressDialog.dismiss();
                    Toast.makeText(activityEditUsername.this, "error encountered, retry", Toast.LENGTH_SHORT).show();

                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    /// add new timestamp for image for caching purposes
                    FirebaseDatabase.getInstance().getReference().child("profileImageTimestamp")
                            .child(username).child(username).setValue(new Date().getTime());

                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    //Toast.makeText(activityEditUsername.this, "Profile picture changed", Toast.LENGTH_SHORT).show();
                    /*Glide.with(activityEditUsername.this).using(new FirebaseImageLoader()).load(profileStorageRef)
                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                            .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                            .error(R.drawable.ic_profile).centerCrop().into(profileImage);*/
                    progressDialog.dismiss();


                }
            });



        }





    }



    public void image_attachment(int from, String filename, Bitmap file, Uri uri) {



        this.bitmap=file;
        this.file_name=filename;
        profileImage.setImageBitmap(file);

        String path =  Environment.getExternalStorageDirectory() + File.separator + "ChopBet" + File.separator;
        imageUtils.createImage(file,filename,path,false);


        // Get the data from an ImageView as bytes
        profileImage.setDrawingCacheEnabled(true);
        profileImage.buildDrawingCache();
        Bitmap bitmap = profileImage.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        data = baos.toByteArray();











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


}
