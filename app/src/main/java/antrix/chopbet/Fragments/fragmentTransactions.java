package antrix.chopbet.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.Objects;

import antrix.chopbet.Activities.activityBetDetails;
import antrix.chopbet.Activities.activityTransactionDetails;
import antrix.chopbet.Models.NewMatch;
import antrix.chopbet.Models.NewTransaction;
import antrix.chopbet.R;
import de.hdodenhof.circleimageview.CircleImageView;
import tgio.rncryptor.RNCryptorNative;

public class fragmentTransactions extends Fragment {

    String myPhoneNumber;
    FirebaseAuth mAuth;
    DatabaseReference dbRef;

    FirebaseListAdapter<NewTransaction> adapter;
    ListView listView;

    Context context;
    View myView;

    Query query;
    Activity activity;

    CharSequence previousItemDate = null;

    SharedPreferences sharedPreferences;

    String myUserName;
    //TextView transactionID;

    View view;

    RNCryptorNative rnCryptorNative;
    private String myUID;
    private String diamondKey = null;
    private String goldKey = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_transactions, container, false);

        declarations();
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

        listView = (ListView)myView.findViewById(R.id.list_Transactions);




        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        //currentMatchID = sharedPreferences.getString("currentMatchID", null);
        myUserName = sharedPreferences.getString("myUserName", null);



        rnCryptorNative = new RNCryptorNative();



    }



    private void loadPrimeKey(){

        myUID = mAuth.getUid();
        String rUserName = new StringBuffer(myUserName).reverse().toString();

        dbRef.child("Xhaust").child(rUserName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                diamondKey = dataSnapshot.child("liquidNitrogen").getValue().toString();

                goldKey = rnCryptorNative.decrypt(diamondKey, myUID);

                loadHistory();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void loadHistory(){


        query = dbRef.child("Xperience").child(goldKey).child("Injection").orderByChild("index").limitToLast(10);

        adapter = new FirebaseListAdapter<NewTransaction>(activity, NewTransaction.class, R.layout.list_transactions, query) {
            @Override
            protected void populateView(View v, final NewTransaction model, int position) {

                TextView date = (TextView)v.findViewById(R.id.date);
                TextView transactionType = (TextView)v.findViewById(R.id.transactionType);
                TextView amount = (TextView)v.findViewById(R.id.amount);
                TextView merchant = (TextView)v.findViewById(R.id.merchant);
                TextView transactionResult = (TextView)v.findViewById(R.id.transactionResult);
                RelativeLayout topDividor = (RelativeLayout)v.findViewById(R.id.topDividor);
                RelativeLayout bottomDividor = (RelativeLayout)v.findViewById(R.id.bottomDividor);
                CircleImageView profileImage = (CircleImageView)v.findViewById(R.id.profileImage);
                RelativeLayout gameLayout = (RelativeLayout)v.findViewById(R.id.gameLayout);
                TextView transactionID = (TextView)v.findViewById(R.id.transactionID);





                transactionID.setText(model.getTransactionID());

                transactionType.setText(model.getTransactionType());
                amount.setText("GHS " + model.getAmount());
                transactionResult.setText(model.getResult());
                merchant.setText(model.getMerchant());



                CharSequence compareDate = DateFormat.format(getString(R.string.dateformat), model.getDate());


                ////// SETTING THE DATE
                if (previousItemDate == null){
                    topDividor.setVisibility(View.GONE);
                    if(DateUtils.isToday(model.getDate())){
                        date.setText("Today");
                        previousItemDate = compareDate;
                    } else {
                        date.setText(DateFormat.format(getString(R.string.dateformat), model.getDate()));
                        previousItemDate = compareDate;

                    }

                }
                else if (Objects.equals(previousItemDate, compareDate)){
                    topDividor.setVisibility(View.VISIBLE);
                    date.setVisibility(View.GONE);
                    previousItemDate = compareDate;
                }
                else{
                    bottomDividor.setVisibility(View.VISIBLE);
                    if(DateUtils.isToday(model.getDate())){
                        date.setText("Today");
                        previousItemDate = compareDate;
                    } else {
                        date.setText(DateFormat.format(getString(R.string.dateformat), model.getDate()));
                        previousItemDate = compareDate;

                    }

                }








                gameLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, activityTransactionDetails.class);
                        intent.putExtra("string", goldKey);
                        intent.putExtra("transactionID", model.getTransactionID());
                        startActivity(intent);
                    }
                });






            }


        };

        listView.setAdapter(adapter);





    }



}
