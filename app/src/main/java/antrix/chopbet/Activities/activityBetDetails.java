package antrix.chopbet.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Locale;
import java.util.Objects;

import antrix.chopbet.BetClasses.BaseActivity;
import antrix.chopbet.BetClasses.BetUtilities;
import antrix.chopbet.Models.BetBuddy;
import antrix.chopbet.Models.NewMatch;
import antrix.chopbet.R;
import de.hdodenhof.circleimageview.CircleImageView;


public class activityBetDetails extends BaseActivity{




    String myPhoneNumber;
    FirebaseAuth mAuth;
    FirebaseFirestore fireDbRef;
    DatabaseReference dbRef;

    ListView listView;

    Context context;
    View view;
    View myView;

    String myUserName;
    SharedPreferences sharedPreferences;

    FirebaseListAdapter<NewMatch> adapter;


    Activity activity;

    Bundle bundle;


    ValueEventListener listener;
    DatabaseReference matchDetailsDbRef;

    String currentMatchID;

    Query query;

    BetUtilities betUtilities;
    DatabaseReference friendsDbRef;
    ProgressDialog progressDialog;





    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bet_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadActionbar("Bet Details");
        getSupportActionBar().setElevation(0);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        declarations();
        loadHistory();
        progressDialog.dismiss();




    }


    private void declarations(){


        activity = activityBetDetails.this;
        context = this;

        dbRef = FirebaseDatabase.getInstance().getReference();

        fireDbRef = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        myPhoneNumber = mAuth.getCurrentUser().getPhoneNumber();


        listView = (ListView)findViewById(R.id.list_BetDetails);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        //myUserName = sharedPreferences.getString("myUserName", null);

        bundle = getIntent().getExtras();

        currentMatchID = bundle.getString("matchID");
        currentMatchID = bundle.getString("username");

        query = dbRef.child("Matches").child(myUserName).orderByChild("matchID").equalTo(currentMatchID);


        friendsDbRef = dbRef.child("Friends").child(myPhoneNumber);


        betUtilities = new BetUtilities();


    }


    private void loadHistory(){





        adapter = new FirebaseListAdapter<NewMatch>(activity, NewMatch.class, R.layout.list_bet_details, query) {
            @Override
            protected void populateView(View v, final NewMatch model, int position) {

                TextView date = (TextView)v.findViewById(R.id.date);
                final TextView name = (TextView)v.findViewById(R.id.name);
                TextView contactSource = (TextView)v.findViewById(R.id.contactSource);
                TextView wonOrLost = (TextView)v.findViewById(R.id.wonOrLost);
                TextView matchID = (TextView)v.findViewById(R.id.matchID);
                TextView bottomAmount = (TextView)v.findViewById(R.id.bottomAmount);
                TextView topAmount = (TextView)v.findViewById(R.id.topAmount);
                TextView fee = (TextView)v.findViewById(R.id.fee);
                final TextView report = (TextView)v.findViewById(R.id.report);
                final TextView dispute = (TextView)v.findViewById(R.id.dispute);

                CircleImageView profileImage = (CircleImageView)v.findViewById(R.id.profileImage);

                final TextView addFriend = (TextView)v.findViewById(R.id.addFriend);



                final TextView internet = (TextView)v.findViewById(R.id.internet);
                final TextView console = (TextView)v.findViewById(R.id.console);
                final TextView game = (TextView)v.findViewById(R.id.game);



                matchID.setText(model.getMatchID());

                /// DETERMINE OPPONENT
                if(Objects.equals(model.getPlayerOne(), myPhoneNumber)){
                    name.setText(model.getPlayerTwo());
                } else if (Objects.equals(model.getPlayerTwo(), myPhoneNumber)){
                    name.setText(model.getPlayerOne());
                }


                bottomAmount.setText("GHS " + model.getBetAmount());
                topAmount.setText("GHS " + model.getBetAmount());


                wonOrLost.setText(model.getWonOrLost());

                fee.setText(String.valueOf(String.format(Locale.UK, "%1.2f", model.getBetFee())));

                if (Objects.equals(model.getWonOrLost(), "WON")){
                    wonOrLost.setTextColor(getResources().getColor(R.color.green));
                }else if (Objects.equals(model.getWonOrLost(), "LOST")){
                    wonOrLost.setTextColor(getResources().getColor(R.color.colorPrimary));
                } else if (Objects.equals(model.getWonOrLost(), "Pending")){
                    wonOrLost.setTextColor(getResources().getColor(R.color.colorGameFIFA));
                }

                /*

                if(Objects.equals(model.getBetStatus(), "Pending")){
                    betResult.setText(R.string.pending);
                } else if (Objects.equals(model.getBetStatus(), myPhoneNumber)){
                    betResult.setText("WON");
                } else {
                    betResult.setText("LOST");
                }


*/

                if (Objects.equals(myUserName, model.getPlayerOne())) {
                    name.setText(model.getPlayerTwo());
                    addToFriends(model.getPlayerTwo(), addFriend);
                    loadProfilePic(model.getPlayerTwo(), profileImage);

                } else if (Objects.equals(myUserName, model.getPlayerTwo())) {
                    name.setText(model.getPlayerOne());
                    addToFriends(model.getPlayerOne(), addFriend);
                    loadProfilePic(model.getPlayerOne(), profileImage);

                }


                if(DateUtils.isToday(model.getBetDate())){
                    date.setText("Today");
                } else {
                    date.setText(DateFormat.format(getString(R.string.dateformat), model.getBetDate()));
                }




                matchTextColours(model.getMatchID(), console, game, internet);




                profileImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, activityUserProfile.class);
                        intent.putExtra("userName", name.getText().toString());
                        context.startActivity(intent);
                    }
                });

                name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, activityUserProfile.class);
                        intent.putExtra("userName", name.getText().toString());
                        context.startActivity(intent);
                    }
                });



                dbRef.child("Matches").child(model.getPlayerOne()).child(model.getMatchID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        /*if(dataSnapshot.child("credited").exists()){
                            dispute.setVisibility(View.INVISIBLE);

                        } else */if (dataSnapshot.child("resolved").exists()){
                            //dispute.setVisibility(View.INVISIBLE);
                            dispute.setText("Resolved");
                            dispute.setTypeface(null, Typeface.ITALIC);
                            dispute.setEnabled(false);

                        } else if (dataSnapshot.child("disputed").exists() && dataSnapshot.child("resolved").getValue() == null){
                            //dispute.setVisibility(View.INVISIBLE);
                            dispute.setText("Dipute pending");
                            dispute.setTypeface(null, Typeface.ITALIC);
                            dispute.setEnabled(false);

                        } else {

                            //dispute.setVisibility(View.VISIBLE);
                            dispute.setText("DISPUTE");
                            dispute.setTypeface(null, Typeface.NORMAL);
                            dispute.setEnabled(true);
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



                dispute.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dbRef.child("Matches").child(model.getPlayerOne()).child(model.getMatchID()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                /*if(dataSnapshot.child("credited").exists()){

                                    Toast.makeText(activityBetDetails.this, "Match has already been credited, you can no longer dispute it", Toast.LENGTH_SHORT).show();

                                } else */if (dataSnapshot.child("resolved").exists()){
                                    Toast.makeText(activityBetDetails.this, "Match has been resolved", Toast.LENGTH_SHORT).show();
                                } else if (dataSnapshot.child("disputed").exists() && dataSnapshot.child("resolved").getValue() == null){
                                    //dispute.setVisibility(View.INVISIBLE);
                                    Toast.makeText(activityBetDetails.this, "Dispute pending", Toast.LENGTH_SHORT).show();
                                } else {
                                    dbRef.child("Matches").child(model.getPlayerOne()).child(model.getMatchID()).child("disputed").setValue(true);
                                    dbRef.child("Matches").child(model.getPlayerTwo()).child(model.getMatchID()).child("disputed").setValue(true);
                                    Toast.makeText(activityBetDetails.this, "Match disputed successfully", Toast.LENGTH_SHORT).show();
                                    //dispute.setVisibility(View.INVISIBLE);
                                }


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });

                report.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dbRef.child("UserReports").child(name.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (dataSnapshot.getValue() == null){
                                    dbRef.child("UserReports").child(name.getText().toString()).child("reports").setValue(1);

                                    report.setVisibility(View.INVISIBLE);
                                    Toast.makeText(activityBetDetails.this, "Thank you for your submission.", Toast.LENGTH_SHORT).show();

                                }else {

                                    Integer reports = Integer.parseInt(dataSnapshot.child("reports").getValue().toString());
                                    reports = reports + 1;
                                    dbRef.child("UserReports").child(name.getText().toString()).child("reports").setValue(reports);

                                    report.setVisibility(View.GONE);

                                    Toast.makeText(activityBetDetails.this, "Thank you for your submission.", Toast.LENGTH_SHORT).show();

                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });



            }






        };

        listView.setAdapter(adapter);










    }

    private void clickers(){




    }

    private void loadProfilePic(final String string, final CircleImageView circleImageView){


        dbRef.child("profileImageTimestamp").child(string)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChildren()){

                            String timestamp = dataSnapshot.child(string).getValue().toString();
                            StorageReference profileStorageRef = FirebaseStorage.getInstance().getReference()
                                    .child("ProfileImages").child(string).child(string);


                            betUtilities.CircleImageFromFirebase(context, profileStorageRef, circleImageView, timestamp);

                        }



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });




    }


    private void addToFriends(final String string, final TextView textView){

        friendsDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(string).exists()) {
                    String status = dataSnapshot.child(string).child("status").getValue().toString();


                    switch (status) {
                        case "Add Friend":
                            textView.setEnabled(true);
                            textView.setTypeface(null, Typeface.NORMAL);
                            textView.setTextColor(getResources().getColor(R.color.colorPrimary));
                            textView.setText("Add Friend");
                            textView.setVisibility(View.VISIBLE);

                            break;
                        case "Pending":
                            textView.setEnabled(true);
                            textView.setTypeface(null, Typeface.NORMAL);
                            textView.setTextColor(getResources().getColor(R.color.colorPrimary));
                            textView.setText("Accept");
                            textView.setVisibility(View.VISIBLE);

                            break;
                        case "Sent":
                            textView.setEnabled(false);
                            textView.setTypeface(null, Typeface.ITALIC);
                            textView.setTextColor(getResources().getColor(R.color.selector));
                            textView.setText("Sent");
                            textView.setVisibility(View.VISIBLE);

                            break;
                        case "Friend":
                            textView.setEnabled(false);
                            textView.setTypeface(null, Typeface.ITALIC);
                            textView.setTextColor(getResources().getColor(R.color.selector));
                            textView.setText("Friend");
                            textView.setVisibility(View.GONE);

                            break;

                    }


                } else {
                    textView.setText("Add Friend");
                    textView.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dbRef.child("UserNames").child(string).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String phoneNumber = dataSnapshot.child("phoneNumber").getValue().toString();

                        final BetBuddy myBetBuddy = new BetBuddy(string, phoneNumber, "Sent", "false");
                        BetBuddy hisBetBuddy = new BetBuddy(myUserName, myPhoneNumber, "Pending", "false");


                        switch (textView.getText().toString()) {
                            case "Add Friend":
                                dbRef.child("Friends").child(myPhoneNumber).child(string).setValue(myBetBuddy);
                                dbRef.child("Friends").child(phoneNumber).child(myUserName).setValue(hisBetBuddy);


                                break;
                            case "Accept":
                                dbRef.child("Friends").child(myPhoneNumber).child(string).child("status").setValue("Friend");
                                dbRef.child("Friends").child(phoneNumber).child(myUserName).child("status").setValue("Friend");

                                break;
                            case "Sent":

                                break;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        });



    }


    private void matchTextColours(final String currentMatchID, final TextView console, final TextView game, final TextView internet){



        dbRef.child("Matches").child(myUserName).child(currentMatchID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshotC) {


                if (dataSnapshotC.hasChildren()) {


                    String mConsole = dataSnapshotC.child("betConsole").getValue().toString();
                    String mGame = dataSnapshotC.child("betGame").getValue().toString();
                    String mAmount = dataSnapshotC.child("betAmount").getValue().toString();
                    String mInternet = dataSnapshotC.child("betInternet").getValue().toString();

                    switch (mConsole) {
                        case "PS4":
                            console.setText("PS4");
                            console.setTextColor(getResources().getColor(R.color.colorConsolePS4));
                            console.setVisibility(View.VISIBLE);
                            break;

                        case "XBOX ONE":
                            console.setText("XBOX ONE");
                            console.setTextColor(getResources().getColor(R.color.colorConsoleXBOXONE));
                            console.setVisibility(View.VISIBLE);
                            break;

                        case "PC":
                            console.setText("PC");
                            console.setTextColor(getResources().getColor(R.color.colorConsolePC));
                            console.setVisibility(View.VISIBLE);

                            break;

                    }


                    switch (mGame) {
                        case "FIFA 18":
                            game.setText("FIFA 18");
                            game.setTextColor(getResources().getColor(R.color.colorGameFIFA));
                            game.setVisibility(View.VISIBLE);
                            break;

                        case "MK XL":
                            game.setText("MK XL");
                            game.setTextColor(getResources().getColor(R.color.colorGameMKXL));
                            game.setVisibility(View.VISIBLE);
                            break;


                    }

/*

                    switch (mAmount) {
                        case "10":
                            amount.setText("GHS 10");
                            amount.setTextColor(getResources().getColor(R.color.colorAmount10));
                            amount.setVisibility(View.VISIBLE);
                            break;

                        case "20":
                            amount.setText("GHS 20");
                            amount.setTextColor(getResources().getColor(R.color.colorAmount20));
                            amount.setVisibility(View.VISIBLE);
                            break;


                        case "50":
                            amount.setText("GHS 50");
                            amount.setTextColor(getResources().getColor(R.color.colorAmount50));
                            amount.setVisibility(View.VISIBLE);
                            break;


                        case "100":
                            amount.setText("GHS 100");
                            amount.setTextColor(getResources().getColor(R.color.colorAmount100));
                            amount.setVisibility(View.VISIBLE);
                            break;


                        case "200":
                            amount.setText("GHS 200");
                            amount.setTextColor(getResources().getColor(R.color.colorAmount200));
                            amount.setVisibility(View.VISIBLE);
                            break;


                    }

*/


                    switch (mInternet) {
                        case "3G":
                            internet.setText("3G");
                            internet.setTextColor(getResources().getColor(R.color.colorInternet3g));
                            internet.setVisibility(View.VISIBLE);
                            break;

                        case "4G":
                            internet.setText("4G");
                            internet.setTextColor(getResources().getColor(R.color.colorInternet4G));
                            internet.setVisibility(View.VISIBLE);
                            break;


                        case "BROADBAND":
                            internet.setText("BROADBAND");
                            internet.setTextColor(getResources().getColor(R.color.colorInternetBroadband));
                            internet.setVisibility(View.VISIBLE);
                            break;


                        case "FIBRE":
                            internet.setText("FIBRE");
                            internet.setTextColor(getResources().getColor(R.color.colorInternetFibre));
                            internet.setVisibility(View.VISIBLE);
                            break;


                    }


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

    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }








}
