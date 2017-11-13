package antrix.chopbet.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import antrix.chopbet.BetClasses.BaseActivity;
import antrix.chopbet.Fragments.fragmentChopBet;
import antrix.chopbet.Fragments.fragmentFriends;
import antrix.chopbet.Fragments.fragmentHistory;
import antrix.chopbet.Fragments.fragmentNewBet;
import antrix.chopbet.Fragments.fragmentTransactions;
import antrix.chopbet.Fragments.fragmentWallet;
import antrix.chopbet.R;

public class activityChopBet extends BaseActivity {

    FirebaseAuth.AuthStateListener mAuthListener;
    String myPhoneNumber;
    String myUID;
    Context context;
    FirebaseAuth mAuth;

    long loginTime;
    Bundle bundle;
    String countryCode;
    String countryCodeStatus;

    DatabaseReference dbRef;
    FirebaseFirestore fireDbRef;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    int x = 0;

    String matchStatus;
    public String currentMatchID;
    String myUserName;
    ValueEventListener matchListener;

    Handler mHandler;

    ProgressDialog progressDialog;



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.nav_home:
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();
                    if (Objects.equals(matchStatus, "Open")){
                        transaction.replace(R.id.content, new fragmentChopBet()).commitAllowingStateLoss();
                    }else{
                        transaction.replace(R.id.content, new fragmentNewBet()).commitAllowingStateLoss();
                    }
                    getSupportActionBar().setTitle("Chop Bet");
                    loadActionbar("Chop Bet");
                    progressDialog.dismiss();

                    return true;
                case R.id.nav_friends:
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();
                    transaction.replace(R.id.content, new fragmentFriends()).commit();
                    getSupportActionBar().setTitle("Friends");
                    loadActionbar("Friends");
                    progressDialog.dismiss();
                    return true;
                case R.id.nav_history:
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();
                    transaction.replace(R.id.content, new fragmentHistory()).commit();
                    getSupportActionBar().setTitle("History");
                    loadActionbar("History");
                    progressDialog.dismiss();
                    return true;
                case R.id.nav_transactions:
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();
                    transaction.replace(R.id.content, new fragmentTransactions()).commit();
                    getSupportActionBar().setTitle("Transactions");
                    loadActionbar("Transactions");
                    progressDialog.dismiss();
                    return true;
                case R.id.nav_wallet:
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();
                    transaction.replace(R.id.content, new fragmentWallet()).commit();
                    getSupportActionBar().setTitle("Wallet");
                    loadActionbar("Wallet");
                    progressDialog.dismiss();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chop_bet);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        declarations();
        loadDefaults();
        initialSetup();
        clickers();














    }

    private void declarations(){

        context = this;
        dbRef = FirebaseDatabase.getInstance().getReference();
        fireDbRef = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();

        myPhoneNumber = mAuth.getCurrentUser().getPhoneNumber();
        myUID = mAuth.getCurrentUser().getUid();

        loginTime = new Date().getTime();

        // get bundled country code from registerLogin activity

        bundle = getIntent().getExtras();
        if (bundle != null){
            countryCode = bundle.getString("countryCode");
            countryCodeStatus = bundle.getString("countryCodeStatus");
        }


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        loadActionbar("Chop Bet");

        matchStatus = sharedPreferences.getString("matchStatus", null);
        currentMatchID = sharedPreferences.getString("currentMatchID", null);


        if (Objects.equals(matchStatus, "null")) {
            matchStatus = "Open";
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(R.id.content, new fragmentChopBet()).commit();

        mHandler = new Handler();
        progressDialog = new ProgressDialog(context);


    }


    private void watchForNewMatch(){


        matchListener = dbRef.child("MatchObserver").child(myUserName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                progressDialog.setMessage("Loading...");
                progressDialog.show();

                matchStatus = dataSnapshot.child("matchStatus").getValue().toString();
                currentMatchID = dataSnapshot.child("currentMatchID").getValue().toString();
                editor.putString("matchStatus", matchStatus);
                editor.putString("currentMatchID", currentMatchID);



                FragmentManager fragmentManager = getSupportFragmentManager();
                final FragmentTransaction transaction = fragmentManager.beginTransaction();

                if (Objects.equals(matchStatus, "Open")){


                    transaction.replace(R.id.content, new fragmentChopBet()).commitAllowingStateLoss();



                }else{
                    transaction.replace(R.id.content, new fragmentNewBet()).commitAllowingStateLoss();
                }



                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }



    private void loadActionbar(String title){

        final ActionBar abar = getSupportActionBar();
        // abar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_background));//line under the action bar
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


    private void loadDefaults(){

        /// ENTRY OF INFO FOR USER IN DATA BASE TO BE USED. SUCH AS PHONE NUMBER AND UID

        // record user login time

        //Map<String, Object> mLoginTime = new HashMap<>();
        //mLoginTime.put("loginTime", loginTime);


        //dbRef.child("phoneNumbers").child(myPhoneNumber).child("logins").push().setValue(loginTime);
        //fireDbRef.collection("Users/UserLogins/"+myPhoneNumber).add(mLoginTime);

        int x = Integer.parseInt(countryCodeStatus);
        if (x == 1) {
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("phoneNumber", myPhoneNumber);
            userInfo.put("uid", myUID);
            userInfo.put("countryCode", countryCode);
            //fireDbRef.document("Users/UserInfo/PhoneNumber/"+myPhoneNumber).set(userInfo, SetOptions.merge());
            dbRef.child("UserInfo").child(myPhoneNumber).setValue(userInfo);

            //dbRef.child("phoneNumbers").child(myPhoneNumber).child("uid").setValue(myUID);
            //dbRef.child("users").child(myPhoneNumber).setValue(myPhoneNumber);
            //dbRef.child("uid").child(myUID).child("phoneNumber").setValue(myPhoneNumber);
            //dbRef.child("uid").child(myUID).child("countryCode").setValue("+" + countryCode);

            matchStatus = "Open";
            currentMatchID = "null";
            editor.putString("countryCode", "+" + countryCode);
            editor.putString("matchStatus", "+" + matchStatus);
            editor.putString("currentMatchID", "+" + currentMatchID);
            editor.apply();


        } else {

        }



    }



    private void clickers(){


    }


    public void initialSetup(){


        dbRef.child("UserInfo").child(myPhoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChildren()){
                    if (dataSnapshot.child("userName").getValue() != null){

                        myUserName = dataSnapshot.child("userName").getValue().toString();
                        editor.putString("myUserName", myUserName);
                        editor.apply();

                        watchForNewMatch();

                        //Toast.makeText(context, "Username is " +userName, Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent (context, activityEditUsername.class);
                        startActivity(intent);
                        finish();
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }




    //minimize leap when back is pressed
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            this.moveTaskToBack(true);
            return true;
        } else {
            super.onBackPressed();
        }


        return super.onKeyDown(keyCode, event);


    }


    @Override
    protected void onResume() {

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // Sign in logic here.
                }
                else {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(activityChopBet.this, activityRegisterLogin.class);
                    startActivity(intent);

                }
            }
        };
        super.onResume();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(!isFinishing()){
            dbRef.child("MatchObserver").child(myPhoneNumber).removeEventListener(matchListener);
        }

    }







}
