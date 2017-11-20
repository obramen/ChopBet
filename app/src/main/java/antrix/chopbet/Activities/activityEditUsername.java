package antrix.chopbet.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dx.dxloadingbutton.lib.LoadingButton;
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

import java.util.HashMap;
import java.util.Map;

import antrix.chopbet.BetClasses.BaseActivity;
import antrix.chopbet.Models.BetBuddy;
import antrix.chopbet.R;
import de.hdodenhof.circleimageview.CircleImageView;


public class activityEditUsername extends BaseActivity {

    EditText userNameTextView;
    Button acceptButton;
    CircleImageView profileImage;

    Context context;

    String myPhoneNumber, myUID;
    FirebaseAuth mAuth;
    DatabaseReference dbRef;
    FirebaseFirestore fireDbRef;

    public String username;

    TextInputLayout textInputLayout;
    TextView hintTextView;

    ProgressDialog progressDialog;

    LoadingButton btnSave;

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
        myUID = mAuth.getCurrentUser().getUid();


        userNameTextView = (EditText)findViewById(R.id.userNameTextView);
        profileImage = (CircleImageView)findViewById(R.id.profileImage);
        acceptButton = (Button)findViewById(R.id.acceptButton);
        textInputLayout = (TextInputLayout)findViewById(R.id.textInputLayout);
        hintTextView = (TextView)findViewById(R.id.hintTextView);

        username = "";

        progressDialog = new ProgressDialog(context);

        btnSave = (LoadingButton)findViewById(R.id.btnSave);




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

                username = userNameTextView.getText().toString();

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
                    return;
                }

                btnSave.startLoading();

                userNameTextView.setEnabled(false);
                acceptButton.setEnabled(false);

                progressDialog.setMessage("Verifying username...");
                //progressDialog.show();

                username = userNameTextView.getText().toString();

                final DatabaseReference userDbRef = dbRef.child("UserNames");

                userDbRef.child("UserNames").addValueEventListener(new ValueEventListener() {
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


    }

    public void saveUserName(){
        Map<String, Object> mUserName = new HashMap<>();
        Map<String, Object> xUserName = new HashMap<>();
        mUserName.put(username, myPhoneNumber);
        xUserName.put("userName", username);
        xUserName.put("phoneNumber", myPhoneNumber);

        BetBuddy betBuddy = new BetBuddy(username);


        dbRef.child("UserNames").child(username).setValue(xUserName);
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


/*
        fireDbRef.document("Users/UserNames").set(mUserName, SetOptions.merge());
        fireDbRef.collection("UserNames").add(betBuddy);
        fireDbRef.document("Users/UserInfo/PhoneNumber/"+myPhoneNumber).set(xUserName, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Intent intent = new Intent(context, activityChopBet.class);
                intent.putExtra("countryCodeStatus", "0");
                intent.putExtra("countryCode", "0");
                startActivity(intent);
                finish();
                Toast.makeText(context, "Username created successfully", Toast.LENGTH_LONG).show();
            }
        });


        */
    }

}
