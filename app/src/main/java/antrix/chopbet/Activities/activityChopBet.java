package antrix.chopbet.Activities;

import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
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
import br.com.goncalves.pugnotification.notification.PugNotification;
import tgio.rncryptor.RNCryptorNative;

import static android.content.ContentValues.TAG;

public class activityChopBet extends BaseActivity {

    private static final String TAG_CHOP_BET = "0";
    private static final String TAG_FRIENDS = "1";
    private static final String TAG_HISTORY = "2";
    private static final String TAG_TRANSACTIONS = "3";
    private static final String TAG_WALLET = "4";


    FirebaseAuth.AuthStateListener mAuthListener;
    String myPhoneNumber;
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

    public String deviceOnlinekey = null;

    ChopBetViewPager chopBetViewPager;


    RelativeLayout buttonLayout;

    String myUID;
    RNCryptorNative rnCryptorNative;
    private String diamondKey = null;
    private String goldKey = null;



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.nav_home:
                    transaction.replace(R.id.content, new fragmentChopBet()).commit();
                    getSupportActionBar().setTitle("Chop Bet");
                    loadActionbar("Chop Bet");
                    return true;
                case R.id.nav_friends:
                    transaction.replace(R.id.content, new fragmentFriends()).commit();
                    getSupportActionBar().setTitle("Friends");
                    loadActionbar("Friends");
                    return true;
                case R.id.nav_history:
                    transaction.replace(R.id.content, new fragmentHistory()).commit();
                    getSupportActionBar().setTitle("Match History");
                    loadActionbar("History");
                    return true;
                case R.id.nav_transactions:
                    transaction.replace(R.id.content, new fragmentTransactions()).commit();
                    getSupportActionBar().setTitle("Transactions");
                    loadActionbar("Transactions");
                    return true;
                case R.id.nav_wallet:

                    transaction.replace(R.id.content, new fragmentWallet()).commit();
                    getSupportActionBar().setTitle("Wallet");
                    loadActionbar("Wallet");
                    return true;
            }
            return false;




        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chop_bet);
        getSupportActionBar().setElevation(0);



        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        declarations();
        loadDefaults();
        initialSetup();
        clickers();
        onlinePresence();


        loadPrimeKey();

        //loadNotification();







    }


    private void declarations(){

        context = this;
        dbRef = FirebaseDatabase.getInstance().getReference();
        fireDbRef = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();

        myPhoneNumber = mAuth.getCurrentUser().getPhoneNumber();

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
        myUserName = sharedPreferences.getString("myUserName", null);


        if (Objects.equals(matchStatus, "null")) {
            matchStatus = "Open";
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(R.id.content, new fragmentChopBet()).commit();

        mHandler = new Handler();
        progressDialog = new ProgressDialog(context);

        rnCryptorNative = new RNCryptorNative();

        buttonLayout = (RelativeLayout)findViewById(R.id.buttonsLayout);




    }


    private void watchForNewMatch(){


        dbRef.child("MatchObserver").child(myUserName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {




                matchStatus = dataSnapshot.child("matchStatus").getValue().toString();
                currentMatchID = dataSnapshot.child("currentMatchID").getValue().toString();
                editor.putString("matchStatus", matchStatus);
                editor.putString("currentMatchID", currentMatchID);
                editor.apply();



                FragmentManager fragmentManager = getSupportFragmentManager();
                final FragmentTransaction transaction = fragmentManager.beginTransaction();

                if (Objects.equals(matchStatus, "Open")){


                    //transaction.replace(R.id.content, new fragmentChopBet()).commit();



                }else if (Objects.equals(matchStatus, "Closed")){
                    //transaction.replace(R.id.content, new fragmentNewBet()).commit();


                    dbRef.child("MatchObserver").child(myUserName).child("searchState").removeValue();
                    Intent betIntent = new Intent(activityChopBet.this, activityNewBet.class);
                    betIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    betIntent.putExtra("currentMatchID", currentMatchID);
                    startActivity(betIntent);
                    finish();
                }

                if (dataSnapshot.child("searchState").getValue() == null){

                    BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
                    navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
                    navigation.setVisibility(View.VISIBLE);

                } else {

                    BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
                    navigation.setOnNavigationItemSelectedListener(null);
                    navigation.setVisibility(View.INVISIBLE);


                }



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
            userInfo.put("countryCode", countryCode);
            //fireDbRef.document("Users/UserInfo/PhoneNumber/"+myPhoneNumber).set(userInfo, SetOptions.merge());
            dbRef.child("UserInfo").child(myPhoneNumber).updateChildren(userInfo);

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
                    if (dataSnapshot.child("userName").getValue() == null){
                        Intent intent = new Intent (context, activityEditUsername.class);
                        startActivity(intent);
                        finish();

                    } else {

                        myUserName = dataSnapshot.child("userName").getValue().toString();
                        editor.putString("myUserName", myUserName);
                        editor.apply();

                        watchForNewMatch();

                        //Toast.makeText(context, "Username is " +userName, Toast.LENGTH_SHORT).show();
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
            //dbRef.child("MatchObserver").child(myPhoneNumber).removeEventListener(matchListener);
        }

    }



    private void onlinePresence(){





            final DatabaseReference myConnectionsRef = dbRef.child("OnlinePresence").child(myPhoneNumber);

            // stores the timestamp of my last disconnect (the last time I was seen online)
            final DatabaseReference lastOnlineRef = dbRef.child("OnlinePresence").child(myPhoneNumber).child("lastOnline");

            final DatabaseReference connectedRef = dbRef.child(".info").child("connected");
            connectedRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    boolean connected = snapshot.getValue(Boolean.class);
                    if (connected) {


                        DatabaseReference con = myConnectionsRef.child("onlineNow");
                        //deviceOnlinekey = con.getKey();

                        // when this device disconnects, remove it
                        con.onDisconnect().setValue(Boolean.FALSE);

                        // when I disconnect, update the last time I was seen online
                        lastOnlineRef.onDisconnect().setValue(ServerValue.TIMESTAMP);

                        // add this device to my connections list
                        // this value could contain info about the device or a timestamp too
                        con.setValue(Boolean.TRUE);


                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    System.err.println("Listener was cancelled at .info/connected");
                }
            });




    }






    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chop_bet, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        if(item != null && id == R.id.profileIcon) {

            Intent intent = new Intent(context, activityUserProfile.class);
            intent.putExtra("userName", myUserName);
            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);




    }
















    //.....................MONEY MATTERS........................


    private void loadPrimeKey(){

        String xUserName = sharedPreferences.getString("myUserName", null);
        if(xUserName == null){

        } else{
            myUID = mAuth.getUid();
            String rUserName = new StringBuffer(xUserName).reverse().toString();

            dbRef.child("Xhaust").child(rUserName).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    diamondKey = dataSnapshot.child("liquidNitrogen").getValue().toString();

                    loadBalance();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


    }



    private void loadBalance(){

        goldKey = rnCryptorNative.decrypt(diamondKey, myUID);

        Log.d(TAG, "diamondKey: " + diamondKey);


        dbRef.child("Xperience").child(goldKey).child("Oxygen").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshotB) {

                if(dataSnapshotB.hasChildren()) {


                    editor.putString("balance", dataSnapshotB.child("Bounty").getValue().toString());
                    editor.apply();


                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }



    private void loadNotification(){

        if (myUserName != null) {

            dbRef.child("Notifications").child(myUserName).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChildren()) {
                        if (Objects.equals(dataSnapshot.child("status").getValue().toString(), "New")) {
                            String type = dataSnapshot.child("type").getValue().toString();
                            String opponenet = "";

                            if (Objects.equals(dataSnapshot.child("playerOne").getValue().toString(), myUserName)) {
                                opponenet = dataSnapshot.child("playerTwo").getValue().toString();

                            } else {
                                opponenet = dataSnapshot.child("playerOne").getValue().toString();

                            }


                            Bundle NBundle = new Bundle();
                            NBundle.putString("Notification", null);


                            switch (type){
                                case "New Match":
                                    PugNotification.with(context)
                                            .load()
                                            .title(type)
                                            .message("Your match with - " + opponenet + " - has begun. Report after play. If match reported is not disputed within 7 minutes, reward goes to opponent")
                                            .bigTextStyle("Your match with - " + opponenet + " - has begun. Report after play. If match reported is not disputed within 7 minutes, reward goes to opponent")
                                            .smallIcon(R.mipmap.ic_launcher_round)
                                            .largeIcon(R.mipmap.ic_launcher_round)
                                            .flags(Notification.DEFAULT_ALL)
                                            //.click(activityNewBet.class, NBundle)
                                            .autoCancel(true)
                                            .simple()
                                            .build();
                                    break;

                                case "Match Found":
                                    PugNotification.with(context)
                                            .load()
                                            .title(type)
                                            .message("New match against - " + opponenet + ". Accept match within 20 secs to avoid bans")
                                            .bigTextStyle("New match against - " + opponenet + ". Accept match within 20 secs to avoid bans")
                                            .smallIcon(R.mipmap.ic_launcher_round)
                                            .largeIcon(R.mipmap.ic_launcher_round)
                                            .flags(Notification.DEFAULT_ALL)
                                            //.click(activityNewBet.class, NBundle)
                                            .autoCancel(true)
                                            .simple()
                                            .build();

                                    break;

                                case "Account Credited":
                                    PugNotification.with(context)
                                            .load()
                                            .title(type)
                                            .message("Your account has been credited for your match against - " + opponenet)
                                            .bigTextStyle("Your account has been credited for your match against - " + opponenet)
                                            .smallIcon(R.mipmap.ic_launcher_round)
                                            .largeIcon(R.mipmap.ic_launcher_round)
                                            .flags(Notification.DEFAULT_ALL)
                                            //.click(activityNewBet.class, NBundle)
                                            .autoCancel(true)
                                            .simple()
                                            .build();

                                    break;
                            }



                            dbRef.child("Notifications").child(myUserName).child("status").setValue("Old");


                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }


    }












}
