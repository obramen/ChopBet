package antrix.chopbet.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import antrix.chopbet.Activities.activityAddFunds;
import antrix.chopbet.Models.NewMatch;
import antrix.chopbet.R;
import antrix.chopbet.Activities.activityWithdrawFunds;
import de.hdodenhof.circleimageview.CircleImageView;
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

    TextView bonusTextView, pointsTextView, balance, addFunds, withdraw, cancelText, transactionTypeText;

    RNCryptorNative rnCryptorNative;
    private String diamondKey = null;
    private String goldKey = null;
    double totalDepost = 0;

    RelativeLayout fundsSelector;
    LinearLayout addFundsSelector, withdrawFundsSelector;

    CircleImageView mtn, vodafone, airtel, tigo;



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
        cancelText = (TextView)myView.findViewById(R.id.cancelText);
        transactionTypeText = (TextView)myView.findViewById(R.id.transactionTypeText);


        rnCryptorNative = new RNCryptorNative();

        fundsSelector = (RelativeLayout) myView.findViewById(R.id.fundsSelector);
        addFundsSelector = (LinearLayout) myView.findViewById(R.id.addFundsSelector);
        withdrawFundsSelector = (LinearLayout) myView.findViewById(R.id.withdrawFundsSelector);

        mtn = (CircleImageView)myView.findViewById(R.id.mtn);
        vodafone = (CircleImageView)myView.findViewById(R.id.vodafone);
        tigo = (CircleImageView)myView.findViewById(R.id.tigo);
        airtel = (CircleImageView)myView.findViewById(R.id.airtel);



    }


    private void clickers(){



        addFunds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fundsSelector.setVisibility(View.VISIBLE);
                addFundsSelector.setVisibility(View.VISIBLE);
                //withdrawFundsSelector.setVisibility(View.GONE);
                cancelText.setVisibility(View.VISIBLE);
                transactionTypeText.setVisibility(View.VISIBLE);
                transactionTypeText.setText("ADD FUNDS");
            }
        });


        withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fundsSelector.setVisibility(View.VISIBLE);
                addFundsSelector.setVisibility(View.VISIBLE);
                //withdrawFundsSelector.setVisibility(View.VISIBLE);
                cancelText.setVisibility(View.VISIBLE);
                transactionTypeText.setVisibility(View.VISIBLE);
                transactionTypeText.setText("WITHDRAW");

            }
        });

        cancelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fundsSelector.setVisibility(View.GONE);
                addFundsSelector.setVisibility(View.GONE);
                withdrawFundsSelector.setVisibility(View.GONE);
                cancelText.setVisibility(View.GONE);
                transactionTypeText.setVisibility(View.GONE);
                transactionTypeText.setText("");

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










        mtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(transactionTypeText.getText().toString()){
                    case "ADD FUNDS":
                        depositProceed("mtn-gh");

                        break;
                    case "WITHDRAW":
                        withdrawProceed("mtn-gh");
                        break;
                }
            }
        });



        vodafone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(transactionTypeText.getText().toString()){
                    case "ADD FUNDS":
                        depositProceed("vodafone-gh");

                        break;
                    case "WITHDRAW":
                        withdrawProceed("vodafone-gh");
                        break;
                }
            }
        });



        tigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(transactionTypeText.getText().toString()){
                    case "ADD FUNDS":
                        depositProceed("tigo-gh");

                        break;
                    case "WITHDRAW":
                        withdrawProceed("tigo-gh");
                        break;
                }
            }
        });



        airtel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(transactionTypeText.getText().toString()){
                    case "ADD FUNDS":
                        depositProceed("airtel-gh");

                        break;
                    case "WITHDRAW":
                        withdrawProceed("airtel-gh");
                        break;
                }
            }
        });











    }









    private void depositProceed(String Channel){
        Intent intentA = new Intent(context, activityAddFunds.class);
        intentA.putExtra("string", goldKey);
        intentA.putExtra("channel", Channel);
        startActivity(intentA);
        fundsSelector.setVisibility(View.GONE);
    }


    private void withdrawProceed(String Channel){
        Intent intentB = new Intent(getActivity(), activityWithdrawFunds.class);
        intentB.putExtra("string", goldKey);
        intentB.putExtra("channel", Channel);
        startActivity(intentB);
        fundsSelector.setVisibility(View.GONE);
    }



    // use cloud functions for this

    private void loadDeposits(){

        final ArrayList<Long> depositArray = new ArrayList<Long>();


        dbRef.child("Xperience").child(goldKey).child("Oxygen").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshotB) {

                if(dataSnapshotB.hasChildren()) {

                 /*
                    for (DataSnapshot mData : dataSnapshotB.getChildren()) {

                        //depositArray.add(Long.parseLong(mData.child("amount").getValue().toString()));

                        totalDepost = totalDepost + Double.parseDouble(mData.child("amount").getValue().toString());

                    }
*/
                    balance.setText(String.valueOf("GHS " + dataSnapshotB.child("Bounty").getValue().toString()));


                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Log.d(TAG, "Total Deposits: " + totalDepost);
        //Toast.makeText(context, totalDepost, Toast.LENGTH_SHORT).show();


    }







}
