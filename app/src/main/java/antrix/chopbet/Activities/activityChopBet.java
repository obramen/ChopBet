package antrix.chopbet.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.MenuItem;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import antrix.chopbet.BetClasses.BaseActivity;
import antrix.chopbet.Fragments.fragmentChopBet;
import antrix.chopbet.Fragments.fragmentFriends;
import antrix.chopbet.Fragments.fragmentHistory;
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
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;



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
                    return true;
                case R.id.nav_friends:
                    transaction.replace(R.id.content, new fragmentFriends()).commit();
                    getSupportActionBar().setTitle("Friends");
                    return true;
                case R.id.nav_history:
                    transaction.replace(R.id.content, new fragmentHistory()).commit();
                    getSupportActionBar().setTitle("History");
                    return true;
                case R.id.nav_wallet:
                    transaction.replace(R.id.content, new fragmentWallet()).commit();
                    getSupportActionBar().setTitle("Wallet");
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

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(R.id.content, new fragmentChopBet()).commit();
        getSupportActionBar().setTitle("Chop Bet");


        declarations();
        loadDefaults();
        initialSetup();
        clickers();





    }

    private void declarations(){

        context = this;
        dbRef = FirebaseDatabase.getInstance().getReference();

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



    }


    private void loadDefaults(){

        /// ENTRY OF INFO FOR USER IN DATA BASE TO BE USED. SUCH AS PHONE NUMBER AND UID

        // record user login time
        dbRef.child("phoneNumbers").child(myPhoneNumber).child("logins").push().setValue(loginTime);

        int x = Integer.parseInt(countryCodeStatus);
        if (x == 1) {
            dbRef.child("phoneNumbers").child(myPhoneNumber).child("uid").setValue(myUID);
            dbRef.child("users").child(myPhoneNumber).setValue(myPhoneNumber);
            dbRef.child("uid").child(myUID).child("phoneNumber").setValue(myPhoneNumber);
            dbRef.child("uid").child(myUID).child("countryCode").setValue("+" + countryCode);

            editor.putString("countryCode", "+" + countryCode);
            editor.apply();


        } else {

        }



    }



    private void clickers(){


    }


    public void initialSetup(){

        dbRef.child("usernamesByPhone").child(myPhoneNumber).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChildren()){

                }else{
                    Intent intent = new Intent (context, activityEditUsername.class);
                    startActivity(intent);
                    finish();
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
}
