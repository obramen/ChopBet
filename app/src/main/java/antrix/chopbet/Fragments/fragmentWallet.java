package antrix.chopbet.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import antrix.chopbet.Activities.activityAddFunds;
import antrix.chopbet.Models.NewMatch;
import antrix.chopbet.R;
import tgio.rncryptor.RNCryptorNative;

import static android.content.ContentValues.TAG;

public class fragmentWallet extends Fragment {

    String myPhoneNumber;
    String myUID;
    FirebaseAuth mAuth;
    DatabaseReference dbRef;

    FirebaseListAdapter<NewMatch> adapter;
    ListView listView;

    Context context;
    View view;
    View myView;

    Query query;
    Activity activity;


    SharedPreferences sharedPreferences;

    String myUserName;

    TextView bonusTextView, pointsTextView, balance, addFunds, withdraw;

    RNCryptorNative rnCryptorNative;
    private String diamondKey = null;
    private String goldKey = null;
    int totalDepost = 0;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_wallet, container, false);



        declarations();
        clickers();
        //loadBalance();
        loadPrimeKey();








        return view;
    }




    private void declarations(){

        activity = getActivity();
        context = getActivity();
        myView = view;

        dbRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        myPhoneNumber = mAuth.getCurrentUser().getPhoneNumber();





        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        //currentMatchID = sharedPreferences.getString("currentMatchID", null);
        myUserName = sharedPreferences.getString("myUserName", null);

        bonusTextView = (TextView)myView.findViewById(R.id.bonusTextView);
        pointsTextView = (TextView)myView.findViewById(R.id.pointsTextView);
        balance = (TextView)myView.findViewById(R.id.balance);
        addFunds = (TextView)myView.findViewById(R.id.addFunds);
        withdraw = (TextView)myView.findViewById(R.id.withdraw);


        rnCryptorNative = new RNCryptorNative();



    }


    private void clickers(){
/*

        addFunds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, activityAddFunds.class);
                startActivity(intent);
            }
        });

*/
        withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                KeyGenerator keyGen = null;
                try {
                    keyGen = KeyGenerator.getInstance("AES");
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                keyGen.init(256); // for example
                SecretKey secretKey = keyGen.generateKey();


                String mKey = new SecureRandom(secretKey.getEncoded()).toString();


                Toast.makeText(context, "Key: " + mKey, Toast.LENGTH_SHORT).show();



                String encrypted = new String(rnCryptorNative.encrypt("ABCD", mKey));

                Toast.makeText(context, "encrypted is " + encrypted, Toast.LENGTH_LONG).show();

                String decrypted = rnCryptorNative.decrypt(encrypted, mKey);

                Toast.makeText(context, "decrypted is " + decrypted, Toast.LENGTH_SHORT).show();


            }
        });

    }



    private void loadPrimeKey(){

        myUID = mAuth.getUid();
        String rUserName = new StringBuffer(myUserName).reverse().toString();

        dbRef.child("Xhaust").child(rUserName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                diamondKey = dataSnapshot.child("liquidNitrogen").getValue().toString();

                loadBalance();

                loadDeposits();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



    private void loadBalance(){

        //loadPrimeKey();

        goldKey = rnCryptorNative.decrypt(diamondKey, myUID);

        Log.d(TAG, "diamondKey: " + diamondKey);

        dbRef.child("Xperience").child(goldKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChildren()){
                    String mGold = dataSnapshot.child("Oxygen").getValue().toString();
                    //balance.setText(mGold);


                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        addFunds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, activityAddFunds.class);
                intent.putExtra("string", goldKey);
                startActivity(intent);
            }
        });














    }


    private void loadDeposits(){

        final ArrayList<Integer> depositArray = new ArrayList<Integer>();

        dbRef.child("Xperience").child(goldKey).child("Injection").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshotB) {

                if(dataSnapshotB.hasChildren()) {

                    for (DataSnapshot mData : dataSnapshotB.getChildren()) {

                        depositArray.add(Integer.parseInt(mData.child("amount").getValue().toString()));

                        totalDepost = totalDepost + Integer.parseInt(mData.child("amount").getValue().toString());

                    }

                    balance.setText(String.valueOf("GHS " + totalDepost));


                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Log.d(TAG, "Total Deposits: " + totalDepost);
        //Toast.makeText(context, totalDepost, Toast.LENGTH_SHORT).show();


    }







}
