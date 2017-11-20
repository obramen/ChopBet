package antrix.chopbet.Activities;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import antrix.chopbet.BetClasses.BaseActivity;
import antrix.chopbet.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class activityUserProfile extends BaseActivity{


    TextView name, psnTextView, originTextView, xboxLiveTextView, editProfileTextView, psnHeader, xboxLiveHeader, originHeader;
    RelativeLayout psnLayout, xboxLiveLayout, originLayout;
    CircleImageView profileImage;

    View view;
    Context context;

    String myPhoneNumber, myUID, userName;
    String phoneNumber, sourceActivity;
    FirebaseAuth mAuth;

    DatabaseReference dbRef;
    DatabaseReference profileDbRef;

    Bundle bundle;

    SharedPreferences sharedPreferences;
    String myUserName;

    ProgressDialog progressDialog;






    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        loadActionbar("Profile");


        declarations();
        clickers();
        loadProfile();


        if (Objects.equals(userName, myUserName)){
            editProfileTextView.setVisibility(View.VISIBLE);
        }





    }


    private void declarations(){

        context = this;


        dbRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        myPhoneNumber = mAuth.getCurrentUser().getPhoneNumber();
        myUID = mAuth.getCurrentUser().getUid();


        name = (TextView)findViewById(R.id.name);
        psnTextView = (TextView)findViewById(R.id.psnTextView);
        originTextView = (TextView)findViewById(R.id.originTextView);
        xboxLiveTextView = (TextView)findViewById(R.id.xboxLiveTextView);
        profileImage = (CircleImageView)findViewById(R.id.profileImage);
        editProfileTextView = (TextView) findViewById(R.id.editProfileTextView);
        psnLayout = (RelativeLayout)findViewById(R.id.psnLayout);
        xboxLiveLayout = (RelativeLayout)findViewById(R.id.xboxLiveLayout);
        originLayout = (RelativeLayout)findViewById(R.id.originLayout);

        bundle = getIntent().getExtras();
        userName = bundle.getString("userName");

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        myUserName = sharedPreferences.getString("myUserName", null);

        profileDbRef = dbRef.child("UserNames").child(userName);


        name = (TextView)findViewById(R.id.name);
        psnTextView = (TextView)findViewById(R.id.psnTextView);
        xboxLiveTextView = (TextView)findViewById(R.id.xboxLiveTextView);
        originTextView = (TextView)findViewById(R.id.originTextView);
        psnHeader = (TextView)findViewById(R.id.psnHeader);
        originHeader = (TextView)findViewById(R.id.originHeader);
        xboxLiveHeader = (TextView)findViewById(R.id.xboxLiveHeader);


        progressDialog = new ProgressDialog(context);

    }

    private void loadProfile(){

        progressDialog.setMessage("Loading profile...");
        progressDialog.show();


        profileDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {



                if (dataSnapshot.child("userName").getValue()!=null){
                    name.setText(dataSnapshot.child("userName").getValue().toString());
                }

                if (dataSnapshot.child("psn").getValue()!=null){
                    psnTextView.setText(dataSnapshot.child("psn").getValue().toString());
                    psnLayout.setVisibility(View.VISIBLE);
                } else {
                    psnLayout.setVisibility(View.GONE);
                }

                if (dataSnapshot.child("xboxLive").getValue()!=null){
                    xboxLiveTextView.setText(dataSnapshot.child("xboxLive").getValue().toString());
                    xboxLiveLayout.setVisibility(View.VISIBLE);
                } else {
                    xboxLiveLayout.setVisibility(View.GONE);
                }

                if (dataSnapshot.child("origin").getValue()!=null){
                    originTextView.setText(dataSnapshot.child("origin").getValue().toString());
                    originLayout.setVisibility(View.VISIBLE);
                } else {
                    originLayout.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        progressDialog.dismiss();


    }






    private void clickers(){

        editProfileTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /// 2 - for normal profile edit

                Intent intent = new Intent(context, activityEditUserProfile.class);
                intent.putExtra("userName", userName);
                intent.putExtra("sourceActivity", 2);
                startActivity(intent);
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

    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }





}
