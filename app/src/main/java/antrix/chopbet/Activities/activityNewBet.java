package antrix.chopbet.Activities;

import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Result;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import antrix.chopbet.Models.NewMatch;
import antrix.chopbet.R;
import br.com.goncalves.pugnotification.notification.PugNotification;
import de.hdodenhof.circleimageview.CircleImageView;
import tgio.rncryptor.RNCryptorNative;

import static android.content.ContentValues.TAG;

public class activityNewBet extends AppCompatActivity {


    Context context;
    View view;

    TextView acceptText, playerTwoTextView;
    private Handler mHandler;
    private RelativeLayout coverLayout;
    CircleImageView acceptButton, playerTwoImageView;

    String myPhoneNumber;
    FirebaseAuth mAuth;
    DatabaseReference dbRef;

    String myUID;

    RelativeLayout winChanceLayout, acceptLayout, opponentLayout;
    RadioButton playerOneRadioButton, playerTwoRadioButton;
    Button declineButton;

    ValueEventListener listener, listenerA, listenerB, listenerC;

    SharedPreferences sharedPreferences;

    String currentMatchID;
    String myUserName;
    String playerTwoUserName;

    TextView console, game, amount, internet;

    String p1Accept, p2Accept;

    //private ProgressBar spinner;


    // 15secs progress bar declaration
    private int progress = 0;
    private final int pBarMax = 200;

    ProgressBar acceptProgressBar;

    Button wonMatchButton, lostMatchButton;
    TextView wonText, lostText, winsTextView, lossesTextView;

    RNCryptorNative rnCryptorNative;
    private String diamondKey = null;
    private String goldKey = null;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_bet);

        getSupportActionBar().setElevation(0);

        declarations();
        clickers();
        //loadMatchDetails();

        //listenForPendingMatchChanges();
        //pendingTextColours();


        listenForMatchType();

        loadPrimeKey();
        //loadNotification();

    }




    private void declarations(){

        context = this;
        //mHandler = new Handler();


        dbRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        myPhoneNumber = mAuth.getCurrentUser().getPhoneNumber();

        coverLayout = (RelativeLayout)findViewById(R.id.coverLayout);
        acceptButton = (CircleImageView)findViewById(R.id.acceptButton);
        declineButton = (Button)findViewById(R.id.declineButton);
        playerTwoImageView = (CircleImageView)findViewById(R.id.playerTwoImageView);
        acceptText = (TextView) findViewById(R.id.acceptText);
        winsTextView = (TextView) findViewById(R.id.winsTextView);
        lossesTextView = (TextView) findViewById(R.id.lossesTextView);


        console = (TextView)findViewById(R.id.console);
        game = (TextView)findViewById(R.id.game);
        amount = (TextView)findViewById(R.id.amount);
        internet = (TextView)findViewById(R.id.internet);

        winChanceLayout = (RelativeLayout)findViewById(R.id.winChanceLayout);
        acceptLayout = (RelativeLayout)findViewById(R.id.acceptLayout);
        opponentLayout = (RelativeLayout)findViewById(R.id.opponentLayout);

        playerOneRadioButton = (RadioButton)findViewById(R.id.playerOneRadioButton);
        playerTwoRadioButton = (RadioButton)findViewById(R.id.playerTwoRadioButton);


        wonMatchButton = (Button)findViewById(R.id.matchWonButton);
        lostMatchButton = (Button)findViewById(R.id.matchLostButton);
        wonText = (TextView)findViewById(R.id.wonText);
        lostText = (TextView)findViewById(R.id.lostText);
        playerTwoTextView = (TextView)findViewById(R.id.playerTwo);




        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        currentMatchID = sharedPreferences.getString("currentMatchID", null);

        myUserName = sharedPreferences.getString("myUserName", null);

        p1Accept = "null";
        p2Accept = "null";


        ///currentMatchID = ((activityChopBet)context).currentMatchID;

        rnCryptorNative = new RNCryptorNative();








        //15 minutes progress bar
        acceptProgressBar = (ProgressBar) findViewById(R.id.acceptProgressBar);

        /*
        acceptProgressBar.setMax(pBarMax);
        final Thread pBarThread = new Thread() {
            @Override
            public void run() {
                try {
                    while(progress <= pBarMax) {
                        acceptProgressBar.setProgress(progress);
                        sleep(100);
                        ++progress;
                        if(progress == pBarMax){
                            matchUnaccepted();
                        }
                    }
                }
                catch(InterruptedException e) {
                }
            }
        };
        pBarThread.start();

*/




        //// FIND PLAYER ONE AND TWO







    }


    private void loadDefaults(){

        winChanceLayout.setVisibility(View.GONE);
        acceptLayout.setVisibility(View.GONE);
        opponentLayout.setVisibility(View.GONE);

    }


/*

    private void loadMatchDetails(){
        dbRef.child("MatchObserver").child(myUserName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentMatchID = dataSnapshot.child("currentMatchID").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

*/


    private void listenForMatchType(){

        dbRef.child("MatchObserver").child(myUserName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if(dataSnapshot.child("matchAccepted").getValue() == null){

                    listenForPendingMatchChanges();
                    loadActionbar("New Match");


                    wonMatchButton.setVisibility(View.GONE);
                    lostMatchButton.setVisibility(View.GONE);
                    wonText.setVisibility(View.GONE);
                    lostText.setVisibility(View.GONE);

                    acceptButton.setVisibility(View.VISIBLE);
                    playerOneRadioButton.setVisibility(View.VISIBLE);
                    playerTwoRadioButton.setVisibility(View.VISIBLE);
                    declineButton.setVisibility(View.VISIBLE);
                    acceptProgressBar.setVisibility(View.VISIBLE);
                    acceptText.setVisibility(View.VISIBLE);

                    pendingTextColours(currentMatchID, console, game, amount, internet);


                } else {


                    winsAndLosses();
                    loadActionbar("Match in Progress");

                    wonMatchButton.setVisibility(View.VISIBLE);
                    lostMatchButton.setVisibility(View.VISIBLE);
                    wonText.setVisibility(View.VISIBLE);
                    lostText.setVisibility(View.VISIBLE);

                    acceptButton.setVisibility(View.GONE);
                    playerOneRadioButton.setVisibility(View.GONE);
                    playerTwoRadioButton.setVisibility(View.GONE);
                    declineButton.setVisibility(View.GONE);
                    acceptProgressBar.setVisibility(View.GONE);
                    acceptText.setVisibility(View.GONE);


                    //dbRef.child("PendingMatches").child(myUserName).removeValue();

                    matchTextColours(currentMatchID, console, game, amount, internet);

                }

                if (Objects.equals(dataSnapshot.child("matchStatus").getValue().toString(), "Open")){


                    Intent intent = new Intent(activityNewBet.this, activityChopBet.class);
                    //code status is used to tell the ChopBet activity where the extras is coming from.
                    // the number "0" is used to signal that the intent is coming from splash screen activity.
                    // Hence it comes with no country code attached to it
                    intent.putExtra("countryCodeStatus", "0");
                    intent.putExtra("countryCode", "0");


                    startActivity(intent);
                    finish();


                }




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



    private void listenForPendingMatchChanges(){




            listener = dbRef.child("PendingMatches").child(myUserName).child(currentMatchID).addValueEventListener(new ValueEventListener() {


                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {







                    if (dataSnapshot.child("playerOne").getValue() != null) {




                            int acceptColor = getResources().getColor(R.color.green);
                            int declineColor = getResources().getColor(R.color.colorPrimary);



                            if (Objects.equals(dataSnapshot.child("playerOne").getValue().toString(), myUserName)){
                                playerTwoUserName = dataSnapshot.child("playerTwo").getValue().toString();
                                playerTwoTextView.setText(playerTwoUserName);

                            } else{
                                playerTwoUserName = dataSnapshot.child("playerOne").getValue().toString();
                                playerTwoTextView.setText(playerTwoUserName);

                            }


                        pendingTextColours(currentMatchID, console, game, amount, internet);


                        dbRef.child("Matches").child(playerTwoUserName).orderByChild("wonOrLost").equalTo("WON").addListenerForSingleValueEvent(new ValueEventListener() {
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

                        dbRef.child("Matches").child(playerTwoUserName).orderByChild("wonOrLost").equalTo("LOST").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshotY) {
                                if (dataSnapshotY.hasChildren()){
                                    long numberOfLosses = dataSnapshotY.getChildrenCount();
                                    lossesTextView.setText(String.valueOf(numberOfLosses));
                                }




                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });






                        if (dataSnapshot.child("betStatus").getValue() != null) {
                                String playerOneBetStatus = dataSnapshot.child("betStatus").getValue().toString();
                                if (Objects.equals(playerOneBetStatus, "true")) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        playerOneRadioButton.setButtonTintList(ColorStateList.valueOf(acceptColor));
                                    }
                                    playerOneRadioButton.setChecked(true);
                                    p1Accept = "true";
                                } else if (Objects.equals(playerOneBetStatus, "false")) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        playerOneRadioButton.setButtonTintList(ColorStateList.valueOf(declineColor));
                                    }
                                    playerOneRadioButton.setChecked(true);
                                    p1Accept = "false";


                                }
                            }


                            listenerB = dbRef.child("PendingMatches").child(playerTwoUserName).child(currentMatchID).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshotB) {



                                        if (dataSnapshotB.child("betStatus").getValue() != null) {


                                            int acceptColor = getResources().getColor(R.color.green);
                                            int declineColor = getResources().getColor(R.color.colorPrimary);


                                            String playerOneBetStatus = dataSnapshotB.child("betStatus").getValue().toString();
                                            if (Objects.equals(playerOneBetStatus, "true")) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                    playerTwoRadioButton.setButtonTintList(ColorStateList.valueOf(acceptColor));
                                                }
                                                playerTwoRadioButton.setChecked(true);
                                                p2Accept = "true";
                                            } else if (Objects.equals(playerOneBetStatus, "false")) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                    playerTwoRadioButton.setButtonTintList(ColorStateList.valueOf(declineColor));
                                                }
                                                playerTwoRadioButton.setChecked(true);
                                                p2Accept = "false";

                                            }
                                        }



                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


/*

                            if ((Objects.equals(p1Accept, "true")) && (Objects.equals(p2Accept, "false"))) {

                                //dbRef.child("PendingMatches").child(myUserName).child(currentMatchID).removeEventListener(listener);
                                //dbRef.child("PendingMatches").child(playerTwoUserName).child(currentMatchID).removeEventListener(listenerB);
                                //dbRef.child("PendingMatches").child(myUserName).removeValue();
                                dbRef.child("MatchObserver").child(myUserName).child("matchStatus").setValue("Open");
                                dbRef.child("MatchObserver").child(playerTwoUserName).child("matchStatus").setValue("Open");

                            } else if ((Objects.equals(p1Accept, "false")) && (Objects.equals(p2Accept, "true"))) {

                                //dbRef.child("PendingMatches").child(myUserName).child(currentMatchID).removeEventListener(listener);
                                //dbRef.child("PendingMatches").child(playerTwoUserName).child(currentMatchID).removeEventListener(listenerB);

                                //dbRef.child("PendingMatches").child(myUserName).removeValue();
                                dbRef.child("MatchObserver").child(myUserName).child("matchStatus").setValue("Open");
                                dbRef.child("MatchObserver").child(playerTwoUserName).child("matchStatus").setValue("Open");

                            } else if ((Objects.equals(p1Accept, "false")) && (Objects.equals(p2Accept, "false"))) {

                                //dbRef.child("PendingMatches").child(myUserName).child(currentMatchID).removeEventListener(listener);
                                //dbRef.child("PendingMatches").child(playerTwoUserName).child(currentMatchID).removeEventListener(listenerB);

                                //dbRef.child("PendingMatches").child(myUserName).removeValue();
                                dbRef.child("MatchObserver").child(myUserName).child("matchStatus").setValue("Open");
                                dbRef.child("MatchObserver").child(playerTwoUserName).child("matchStatus").setValue("Open");

                            }else if ((Objects.equals(p1Accept, "false")) && (Objects.equals(p2Accept, "null"))) {

                                //dbRef.child("PendingMatches").child(myUserName).child(currentMatchID).removeEventListener(listener);
                                //dbRef.child("PendingMatches").child(playerTwoUserName).child(currentMatchID).removeEventListener(listenerB);

                                //dbRef.child("PendingMatches").child(myUserName).removeValue();
                                dbRef.child("MatchObserver").child(myUserName).child("matchStatus").setValue("Open");
                                dbRef.child("MatchObserver").child(playerTwoUserName).child("matchStatus").setValue("Open");

                            }else if ((Objects.equals(p1Accept, "null")) && (Objects.equals(p2Accept, "false"))) {

                                //dbRef.child("PendingMatches").child(myUserName).child(currentMatchID).removeEventListener(listener);
                                //dbRef.child("PendingMatches").child(playerTwoUserName).child(currentMatchID).removeEventListener(listenerB);

                                //dbRef.child("PendingMatches").child(myUserName).removeValue();
                                dbRef.child("MatchObserver").child(myUserName).child("matchStatus").setValue("Open");
                                dbRef.child("MatchObserver").child(playerTwoUserName).child("matchStatus").setValue("Open");

                            }else if ((Objects.equals(p1Accept, "true")) && (Objects.equals(p2Accept, "true"))) {


                                //if (Objects.equals(myUserName, dataSnapshot.child("playerOne").getValue().toString())) {



                                    dbRef.child("MatchObserver").child(playerTwoUserName).child("matchAccepted").setValue("true");
                                    dbRef.child("MatchObserver").child(myUserName).child("matchAccepted").setValue("true");

                                    String xAmount = dataSnapshot.child("betAmount").getValue().toString();
                                    String xConsole = dataSnapshot.child("betConsole").getValue().toString();
                                    String xGame = dataSnapshot.child("betGame").getValue().toString();
                                    String xInternet = dataSnapshot.child("betInternet").getValue().toString();


                                    //String matchKey = dbRef.child("Matches").child(myUserName).push().getKey();
                                    NewMatch newMatch = new NewMatch(currentMatchID, myUserName, playerTwoUserName, xAmount, "Fee", xConsole, xGame, xInternet, "Pending");




                                    dbRef.child("Matches").child(myUserName).child(currentMatchID).setValue(newMatch);
                                    dbRef.child("Matches").child(playerTwoUserName).child(currentMatchID).setValue(newMatch);



                                    dbRef.child("PendingMatches").child(myUserName).child(currentMatchID).removeEventListener(listener);





                                //}


                            }

*/






                        }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



    }













    private void clickers(){


        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                dbRef.child("PendingMatches").child(myUserName).child(currentMatchID).child("betStatus").setValue("true").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        acceptButton.setEnabled(false);

                        dbRef.child("MatchObserver").child(myUserName).child("keyString").setValue(goldKey);
                        Toast.makeText(context, "Match Accepted", Toast.LENGTH_SHORT).show();
                    }
                });



            }
        });


        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                dbRef.child("PendingMatches").child(myUserName).child(currentMatchID).child("betStatus").setValue("false")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                declineButton.setEnabled(false);

                                Toast.makeText(context, "Match Declined", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });




        playerTwoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, activityUserProfile.class);
                intent.putExtra("userName", playerTwoUserName);
                context.startActivity(intent);
            }
        });



        wonMatchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Setting match result");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();



                dbRef.child("Matches").child(myUserName).child(currentMatchID).child("wonOrLost").setValue("WON");

                progressDialog.dismiss();

/*




                dbRef.child("Matches").child(playerTwoUserName).child(currentMatchID).child("wonOrLost").setValue("LOST");

                dbRef.child("MatchObserver").child(myUserName).child("matchStatus").setValue("Open");
                dbRef.child("MatchObserver").child(playerTwoUserName).child("matchStatus").setValue("Open");

                dbRef.child("MatchObserver").child(playerTwoUserName).child("matchAccepted").removeValue();
                dbRef.child("MatchObserver").child(myUserName).child("matchAccepted").removeValue();

*/





            }
        });


        lostMatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                dbRef.child("Matches").child(playerTwoUserName).child(currentMatchID).child("wonOrLost").setValue("WON");

  /*
                dbRef.child("Matches").child(myUserName).child(currentMatchID).child("wonOrLost").setValue("LOST");

                dbRef.child("MatchObserver").child(myUserName).child("matchStatus").setValue("Open");
                dbRef.child("MatchObserver").child(playerTwoUserName).child("matchStatus").setValue("Open");

                dbRef.child("MatchObserver").child(playerTwoUserName).child("matchAccepted").removeValue();
                dbRef.child("MatchObserver").child(myUserName).child("matchAccepted").removeValue();

*/


            }
        });


    }




    private void matchUnaccepted(){




        if ((Objects.equals(p1Accept, "null")) && (Objects.equals(p2Accept, "null"))) {



            //dbRef.child("PendingMatches").child(myUserName).child(currentMatchID).removeEventListener(listener);
            //dbRef.child("PendingMatches").child(playerTwoUserName).child(currentMatchID).removeEventListener(listenerB);

            //dbRef.child("PendingMatches").child(myUserName).removeValue();
            dbRef.child("MatchObserver").child(myUserName).child("matchStatus").setValue("Open");
            //dbRef.child("MatchObserver").child(playerTwoUserName).child("matchStatus").setValue("Open");


        } else if ((Objects.equals(p1Accept, "null")) && (Objects.equals(p2Accept, "true"))) {

            //dbRef.child("PendingMatches").child(myUserName).child(currentMatchID).removeEventListener(listener);
            //dbRef.child("PendingMatches").child(playerTwoUserName).child(currentMatchID).removeEventListener(listenerB);

            //dbRef.child("PendingMatches").child(myUserName).removeValue();
            dbRef.child("MatchObserver").child(myUserName).child("matchStatus").setValue("Open");
            dbRef.child("MatchObserver").child(playerTwoUserName).child("matchStatus").setValue("Open");


        } else if ((Objects.equals(p1Accept, "true")) && (Objects.equals(p2Accept, "null"))) {

            //dbRef.child("PendingMatches").child(myUserName).child(currentMatchID).removeEventListener(listener);
            //dbRef.child("PendingMatches").child(playerTwoUserName).child(currentMatchID).removeEventListener(listenerB);

            //dbRef.child("PendingMatches").child(myUserName).removeValue();
            dbRef.child("MatchObserver").child(myUserName).child("matchStatus").setValue("Open");
            dbRef.child("MatchObserver").child(playerTwoUserName).child("matchStatus").setValue("Open");


        } else if ((Objects.equals(p1Accept, "false")) && (Objects.equals(p2Accept, "null"))) {

            //dbRef.child("PendingMatches").child(myUserName).child(currentMatchID).removeEventListener(listener);
            //dbRef.child("PendingMatches").child(playerTwoUserName).child(currentMatchID).removeEventListener(listenerB);

            //dbRef.child("PendingMatches").child(myUserName).removeValue();
            dbRef.child("MatchObserver").child(myUserName).child("matchStatus").setValue("Open");
            //dbRef.child("MatchObserver").child(playerTwoUserName).child("matchStatus").setValue("Open");


        } else if ((Objects.equals(p1Accept, "null")) && (Objects.equals(p2Accept, "false"))) {

            //dbRef.child("PendingMatches").child(myUserName).child(currentMatchID).removeEventListener(listener);
            //dbRef.child("PendingMatches").child(playerTwoUserName).child(currentMatchID).removeEventListener(listenerB);

            //dbRef.child("PendingMatches").child(myUserName).removeValue();
            dbRef.child("MatchObserver").child(myUserName).child("matchStatus").setValue("Open");
            dbRef.child("MatchObserver").child(playerTwoUserName).child("matchStatus").setValue("Open");


        }
    }




    private void pendingTextColours(final String currentMatchID, final TextView console, final TextView game, final TextView amount, final TextView internet){



                    dbRef.child("PendingMatches").child(myUserName).child(currentMatchID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshotC) {


                                if (dataSnapshotC.child("betAmount").getValue() != null) {



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
                        public void onCancelled (DatabaseError databaseError){

                        }

                    });





    }




    private void winsAndLosses(){




        dbRef.child("Matches").child(myUserName).child(currentMatchID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChildren()){


                    if (Objects.equals(dataSnapshot.child("playerOne").getValue().toString(), myUserName)){
                        playerTwoUserName = dataSnapshot.child("playerTwo").getValue().toString();
                        playerTwoTextView.setText(playerTwoUserName);

                    } else{
                        playerTwoUserName = dataSnapshot.child("playerOne").getValue().toString();
                        playerTwoTextView.setText(playerTwoUserName);

                    }





                    dbRef.child("Matches").child(playerTwoUserName).orderByChild("wonOrLost").equalTo("WON").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            long numberOfWins = dataSnapshot.getChildrenCount();
                            winsTextView.setText(String.valueOf(numberOfWins));


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    dbRef.child("Matches").child(playerTwoUserName).orderByChild("wonOrLost").equalTo("LOST").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            long numberOfLosses = dataSnapshot.getChildrenCount();
                            lossesTextView.setText(String.valueOf(numberOfLosses));


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });





                }




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }



    private void matchTextColours(final String currentMatchID, final TextView console, final TextView game, final TextView amount, final TextView internet){



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
    protected void onDestroy() {
        super.onDestroy();
        dbRef.child("PendingMatches").child(myUserName).child(currentMatchID).removeEventListener(listener);
        dbRef.child("PendingMatches").child(playerTwoUserName).child(currentMatchID).removeEventListener(listenerB);
    }




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

                    goldKey = rnCryptorNative.decrypt(diamondKey, myUID);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


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
