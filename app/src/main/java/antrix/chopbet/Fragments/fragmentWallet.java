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
import android.widget.AdapterView;
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

import java.util.Locale;

import antrix.chopbet.Activities.activityAddFunds;
import antrix.chopbet.BetClasses.ConfirmDialog;
import antrix.chopbet.Models.NewCard;
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

    FirebaseListAdapter<NewCard> adapter;
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

    ConfirmDialog confirmDialog;





    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_wallet, container, false);



        declarations();
        clickers();
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

        listView = (ListView)myView.findViewById(R.id.list_cards);

        confirmDialog = new ConfirmDialog();




    }


    private void clickers(){



        addFunds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fundsSelector.setVisibility(View.VISIBLE);
                fundsSelector.setBackgroundColor(getResources().getColor(R.color.divider));
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
                fundsSelector.setBackgroundColor(getResources().getColor(R.color.selector));
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

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



    private void loadBalance(){


        goldKey = rnCryptorNative.decrypt(diamondKey, myUID);

        Log.d(TAG, "diamondKey: " + diamondKey);


        dbRef.child("Xperience").child(goldKey).child("Oxygen").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshotB) {

                if(dataSnapshotB.hasChildren()) {


                    balance.setText(String.valueOf("GHS " + String.format(Locale.UK, "%1.2f", Double.valueOf(dataSnapshotB.child("Bounty").getValue().toString()))));


                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        loadCards(goldKey);













        mtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(transactionTypeText.getText().toString()){
                    case "ADD FUNDS":
                        depositProceed("mtn-gh", "", "New Card");

                        break;
                    case "WITHDRAW":
                        withdrawProceed("mtn-gh", "", "New Card");
                        break;
                }
            }
        });



        vodafone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(transactionTypeText.getText().toString()){
                    case "ADD FUNDS":
                        depositProceed("vodafone-gh", "", "New Card");

                        break;
                    case "WITHDRAW":
                        withdrawProceed("vodafone-gh", "", "New Card");
                        break;
                }
            }
        });



        tigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(transactionTypeText.getText().toString()){
                    case "ADD FUNDS":
                        depositProceed("tigo-gh", "", "New Card");

                        break;
                    case "WITHDRAW":
                        withdrawProceed("tigo-gh", "", "New Card");
                        break;
                }
            }
        });



        airtel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(transactionTypeText.getText().toString()){
                    case "ADD FUNDS":
                        depositProceed("airtel-gh", "", "New Card");

                        break;
                    case "WITHDRAW":
                        withdrawProceed("airtel-gh", "", "New Card");
                        break;
                }
            }
        });











    }

    private void loadCards(final String goldKey){

        query = dbRef.child("Xperience").child(goldKey).child("Cards").orderByChild("index");

        adapter = new FirebaseListAdapter<NewCard>(activity, NewCard.class, R.layout.list_cards, query) {
            @Override
            protected void populateView(View v, final NewCard model, int position) {

                TextView phoneNumber = (TextView)v.findViewById(R.id.phoneNumber);
                CircleImageView profileImage = (CircleImageView)v.findViewById(R.id.profileImage);
                TextView deleteCard = (TextView)v.findViewById(R.id.deleteCard);

                TextView addFundsWithCard = (TextView)v.findViewById(R.id.addFundsWithCard);
                TextView withdraFundsWithCard = (TextView)v.findViewById(R.id.withdrawWithCard);


                phoneNumber.setText(model.getPhoneNumber());

                RelativeLayout detailsLayout = (RelativeLayout)v.findViewById(R.id.detailsLayout);






                switch (model.getChannel()){

                    case "mtn-gh":
                        profileImage.setImageResource(R.drawable.mtn);
                        detailsLayout.setBackgroundColor(getResources().getColor(R.color.mtn));
                        break;

                    case "vodafone-gh":
                        profileImage.setImageResource(R.drawable.vodafone);
                        detailsLayout.setBackgroundColor(getResources().getColor(R.color.vodafone));
                        break;

                    case "tigo-gh":
                        profileImage.setImageResource(R.drawable.tigo);
                        detailsLayout.setBackgroundColor(getResources().getColor(R.color.tigo));

                        break;

                    case "airtel-gh":
                        profileImage.setImageResource(R.drawable.airtel);
                        detailsLayout.setBackgroundColor(getResources().getColor(R.color.airtel));

                        break;

                }



                deleteCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        confirmDialog.NewConfirmDialog(context, "Confirm Action", "Delete saved card", "Delete", "Cancel");
                        confirmDialog.confirmAccept.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dbRef.child("Xperience").child(goldKey).child("Cards").child(model.getCardID()).removeValue();
                                confirmDialog.dialog.dismiss();
                            }
                        });




                    }
                });


                addFundsWithCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        depositProceed(model.getChannel(), model.getPhoneNumber(), "Saved Card");
                    }
                });

                withdraFundsWithCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        withdrawProceed(model.getChannel(), model.getPhoneNumber(), "Saved Card");
                    }
                });




            }
        };

        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RelativeLayout transactionLayout = (RelativeLayout)view.findViewById(R.id.transactionLayout);

                if(transactionLayout.getVisibility() == View.VISIBLE){
                    transactionLayout.setVisibility(View.GONE);
                } else{
                    transactionLayout.setVisibility(View.VISIBLE);
                }



            }
        });








    }









    private void depositProceed(String Channel, String PhoneNumber, String cardStatus){
        Intent intentA = new Intent(context, activityAddFunds.class);
        intentA.putExtra("string", goldKey);
        intentA.putExtra("channel", Channel);
        intentA.putExtra("cardStatus", cardStatus);
        intentA.putExtra("phoneNumber", PhoneNumber);
        startActivity(intentA);
        fundsSelector.setVisibility(View.GONE);
    }


    private void withdrawProceed(String Channel, String PhoneNumber, String cardStatus){
        Intent intentB = new Intent(getActivity(), activityWithdrawFunds.class);
        intentB.putExtra("string", goldKey);
        intentB.putExtra("channel", Channel);
        intentB.putExtra("cardStatus", cardStatus);
        intentB.putExtra("phoneNumber", PhoneNumber);
        startActivity(intentB);
        fundsSelector.setVisibility(View.GONE);
    }








}
