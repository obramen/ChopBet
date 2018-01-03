package antrix.chopbet.Activities;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

import antrix.chopbet.BetClasses.BaseActivity;
import antrix.chopbet.BetClasses.BetUtilities;
import antrix.chopbet.Models.NewMatch;
import antrix.chopbet.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class activityUserProfile extends BaseActivity{


    TextView name, psnTextView, originTextView, xboxLiveTextView, editProfileTextView, psnHeader, xboxLiveHeader, originHeader,
            winsTextView, lossTextView;
    RelativeLayout psnLayout, xboxLiveLayout, originLayout;
    CircleImageView profileImage;

    View view;
    Context context;

    String myPhoneNumber, userName;
    String phoneNumber, sourceActivity;
    FirebaseAuth mAuth;

    DatabaseReference dbRef;
    DatabaseReference profileDbRef;

    Bundle bundle;

    SharedPreferences sharedPreferences;
    String myUserName;

    ProgressDialog progressDialog;

    BetUtilities betUtilities;
    TextView sendBet;

    ListView listView;
    FirebaseListAdapter<NewMatch> adapter;

    Query query;
    TextView matchID;
    Activity activity;

    CharSequence previousItemDate = null;









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
            sendBet.setVisibility(View.GONE);
        }


        loadProfileImage();
        loadHistory();



    }


    private void declarations(){

        context = this;
        activity = activityUserProfile.this;



        dbRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        myPhoneNumber = mAuth.getCurrentUser().getPhoneNumber();


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

        sendBet = (TextView)findViewById(R.id.sendBet);


        profileDbRef = dbRef.child("UserNames").child(userName);


        name = (TextView)findViewById(R.id.name);
        psnTextView = (TextView)findViewById(R.id.psnTextView);
        xboxLiveTextView = (TextView)findViewById(R.id.xboxLiveTextView);
        originTextView = (TextView)findViewById(R.id.originTextView);
        psnHeader = (TextView)findViewById(R.id.psnHeader);
        originHeader = (TextView)findViewById(R.id.originHeader);
        xboxLiveHeader = (TextView)findViewById(R.id.xboxLiveHeader);
        winsTextView = (TextView)findViewById(R.id.winsTextView);
        lossTextView = (TextView)findViewById(R.id.lossesTextView);


        progressDialog = new ProgressDialog(context);
        betUtilities = new BetUtilities();

        loadWinLoss(userName);


        listView = (ListView)findViewById(R.id.list_History);

        query = dbRef.child("Matches").child(userName).orderByChild("index").limitToFirst(5);



    }

    private void loadProfile(){

        progressDialog.setMessage("Loading profile...");
        progressDialog.setCanceledOnTouchOutside(false);
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

    private void loadProfileImage(){



        dbRef.child("profileImageTimestamp").child(userName)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChildren()){

                            String timestamp = dataSnapshot.child(userName).getValue().toString();
                            StorageReference profileStorageRef = FirebaseStorage.getInstance().getReference()
                                    .child("ProfileImages").child(userName).child(userName);


                            betUtilities.CircleImageFromFirebase(activityUserProfile.this, profileStorageRef, profileImage, timestamp);

                        }



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



    }


    private void loadWinLoss(String string){


        dbRef.child("Matches").child(string).orderByChild("wonOrLost").equalTo("WON").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshotX) {

                if (dataSnapshotX.hasChildren()){
                    long numberOfWins = dataSnapshotX.getChildrenCount();
                    winsTextView.setText(String.valueOf(numberOfWins));
                }




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        dbRef.child("Matches").child(string).orderByChild("wonOrLost").equalTo("LOST").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshotY) {
                if (dataSnapshotY.hasChildren()){
                    long numberOfLosses = dataSnapshotY.getChildrenCount();
                    lossTextView.setText(String.valueOf(numberOfLosses));
                }




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




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

        sendBet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });




        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                matchID = (TextView) view.findViewById(R.id.matchID);
                Intent intent = new Intent(activity, activityBetDetails.class);
                intent.putExtra("matchID", matchID.getText().toString());
                startActivity(intent);
            }
        });



    }






    private void loadHistory(){





        adapter = new FirebaseListAdapter<NewMatch>(activity, NewMatch.class, R.layout.list_history, query) {
            @Override
            protected void populateView(View v, final NewMatch model, int position) {

                TextView date = (TextView)v.findViewById(R.id.date);
                TextView name = (TextView)v.findViewById(R.id.name);
                TextView amount = (TextView)v.findViewById(R.id.amount);
                //TextView contactSource = (TextView)v.findViewById(R.id.contactSource);
                TextView betResult = (TextView)v.findViewById(R.id.betResult);
                RelativeLayout topDividor = (RelativeLayout)v.findViewById(R.id.topDividor);
                RelativeLayout bottomDividor = (RelativeLayout)v.findViewById(R.id.bottomDividor);
                final CircleImageView profileImage = (CircleImageView)v.findViewById(R.id.profileImage);
                RelativeLayout gameLayout = (RelativeLayout)v.findViewById(R.id.gameLayout);
                matchID = (TextView)v.findViewById(R.id.matchID);


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


                amount.setText("GHS " + model.getBetAmount());


                betResult.setText(model.getWonOrLost());
                if (Objects.equals(betResult.getText().toString(), "WON")){
                    betResult.setTextColor(getResources().getColor(R.color.green));
                }else if (Objects.equals(betResult.getText().toString(), "LOST")){
                    betResult.setTextColor(getResources().getColor(R.color.colorPrimary));
                } else if (Objects.equals(betResult.getText().toString(), "Pending")){
                    betResult.setTextColor(getResources().getColor(R.color.colorGameFIFA));
                }




                if (Objects.equals(userName, model.getPlayerOne())) {

                    name.setText(model.getPlayerTwo());


                    //matchTextColours(model.getMatchID());


                    dbRef.child("profileImageTimestamp").child(model.getPlayerTwo())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.hasChildren()){

                                        String timestamp = dataSnapshot.child(model.getPlayerTwo()).getValue().toString();
                                        StorageReference profileStorageRef = FirebaseStorage.getInstance().getReference()
                                                .child("ProfileImages").child(model.getPlayerTwo()).child(model.getPlayerTwo());


                                        betUtilities.CircleImageFromFirebase(context, profileStorageRef, profileImage, timestamp);

                                    }



                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });




                } else if (Objects.equals(userName, model.getPlayerTwo())) {
                    name.setText(model.getPlayerOne());

                    //matchTextColours(model.getMatchID());


                    dbRef.child("profileImageTimestamp").child(model.getPlayerOne())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.hasChildren()){

                                        String timestamp = dataSnapshot.child(model.getPlayerOne()).getValue().toString();
                                        StorageReference profileStorageRef = FirebaseStorage.getInstance().getReference()
                                                .child("ProfileImages").child(model.getPlayerOne()).child(model.getPlayerOne());


                                        betUtilities.CircleImageFromFirebase(context, profileStorageRef, profileImage, timestamp);

                                    }



                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });



                }



                CharSequence compareDate = DateFormat.format(getString(R.string.dateformat), model.getBetDate());


                ////// SETTING THE DATE
                if (previousItemDate == null){
                    topDividor.setVisibility(View.GONE);
                    if(DateUtils.isToday(model.getBetDate())){
                        date.setText("Today");
                        previousItemDate = compareDate;
                    } else {
                        date.setText(DateFormat.format(getString(R.string.dateformat), model.getBetDate()));
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
                    if(DateUtils.isToday(model.getBetDate())){
                        date.setText("Today");
                        previousItemDate = compareDate;
                    } else {
                        date.setText(DateFormat.format(getString(R.string.dateformat), model.getBetDate()));
                        previousItemDate = compareDate;
                    }

                }







                matchTextColours(model.getMatchID(), console, game, internet);








                gameLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, activityBetDetails.class);
                        intent.putExtra("matchID", model.getMatchID());
                        intent.putExtra("username", userName);
                        startActivity(intent);
                    }
                });






            }


        };

        listView.setAdapter(adapter);






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
