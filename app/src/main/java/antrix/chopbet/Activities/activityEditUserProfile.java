package antrix.chopbet.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dx.dxloadingbutton.lib.LoadingButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import antrix.chopbet.BetClasses.BaseActivity;
import antrix.chopbet.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class activityEditUserProfile extends BaseActivity {

    EditText nameTextView, psnTextView, originTextView, xboxLiveTextView;
    CircleImageView profileImage;
    Button cancelButton, acceptButton;

    Context context;

    String myPhoneNumber, myUID;
    String phoneNumber;
    FirebaseAuth mAuth;
    DatabaseReference dbRef;
    DatabaseReference profileDbRef;

    Bundle bundle;
    String userName;

    ProgressDialog progressDialog;

    LoadingButton btnSave;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_user_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        loadActionbar("Edit Profile");


        declarations();
        clickers();






    }


    private void declarations(){

        context = this;

        dbRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        myPhoneNumber = mAuth.getCurrentUser().getPhoneNumber();
        myUID = mAuth.getCurrentUser().getUid();

        nameTextView = (EditText)findViewById(R.id.nameTextView);
        psnTextView = (EditText)findViewById(R.id.psnTextView);
        originTextView = (EditText)findViewById(R.id.originTextView);
        xboxLiveTextView = (EditText)findViewById(R.id.xboxLiveTextView);
        profileImage = (CircleImageView)findViewById(R.id.profileImage);
        cancelButton = (Button)findViewById(R.id.cancelButton);
        acceptButton = (Button)findViewById(R.id.acceptButton);



        // get bundled country code from registerLogin activity
        bundle = getIntent().getExtras();
        userName = bundle.getString("userName");

        profileDbRef = dbRef.child("UserNames").child(userName);

        progressDialog = new ProgressDialog(context);

        btnSave = (LoadingButton)findViewById(R.id.btnSave);


    }

    private void clickers(){

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Saving profile...");
                progressDialog.show();
                profileDbRef.child("psn").setValue(psnTextView.getText().toString());
                profileDbRef.child("origin").setValue(originTextView.getText().toString());
                profileDbRef.child("xboxLive").setValue(xboxLiveTextView.getText().toString());
                progressDialog.dismiss();
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSave.startLoading();
                progressDialog.setMessage("Saving profile...");
                progressDialog.show();
                profileDbRef.child("psn").setValue(psnTextView.getText().toString());
                profileDbRef.child("origin").setValue(originTextView.getText().toString());
                profileDbRef.child("xboxLive").setValue(xboxLiveTextView.getText().toString());
                progressDialog.dismiss();
                finish();
                btnSave.cancelLoading();
                btnSave.reset();
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




}

