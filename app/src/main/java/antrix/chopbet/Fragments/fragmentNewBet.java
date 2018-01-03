package antrix.chopbet.Fragments;


import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import antrix.chopbet.Activities.activityChopBet;
import antrix.chopbet.Activities.activityUserProfile;
import antrix.chopbet.Models.NewMatch;
import antrix.chopbet.R;
import de.hdodenhof.circleimageview.CircleImageView;
import io.rmiri.buttonloading.ButtonLoading;

public class fragmentNewBet extends Fragment {

    Context context;
    View view;
    View myView;

    TextView acceptText;
    private Handler mHandler;
    private RelativeLayout coverLayout;
    CircleImageView acceptButton, playerTwoImageView;

    String myPhoneNumber;
    FirebaseAuth mAuth;
    DatabaseReference dbRef;

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






    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_new_bet, container, false);



        declarations();
        clickers();
        //loadMatchDetails();

        //listenForPendingMatchChanges();
        //pendingTextColours();


        listenForMatchType();



        return view;

    }


    private void declarations(){

        context = getActivity();
        myView = view;
        mHandler = new Handler();


        dbRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        myPhoneNumber = mAuth.getCurrentUser().getPhoneNumber();

        coverLayout = (RelativeLayout)myView.findViewById(R.id.coverLayout);
        acceptButton = (CircleImageView)myView.findViewById(R.id.acceptButton);
        declineButton = (Button)myView.findViewById(R.id.declineButton);
        playerTwoImageView = (CircleImageView)myView.findViewById(R.id.playerTwoImageView);
        acceptText = (TextView) myView.findViewById(R.id.acceptText);
        winsTextView = (TextView) myView.findViewById(R.id.winsTextView);
        lossesTextView = (TextView) myView.findViewById(R.id.lossesTextView);


        console = (TextView)myView.findViewById(R.id.console);
        game = (TextView)myView.findViewById(R.id.game);
        amount = (TextView)myView.findViewById(R.id.amount);
        internet = (TextView)myView.findViewById(R.id.internet);

        winChanceLayout = (RelativeLayout)myView.findViewById(R.id.winChanceLayout);
        acceptLayout = (RelativeLayout)myView.findViewById(R.id.acceptLayout);
        opponentLayout = (RelativeLayout)myView.findViewById(R.id.opponentLayout);

        playerOneRadioButton = (RadioButton)myView.findViewById(R.id.playerOneRadioButton);
        playerTwoRadioButton = (RadioButton)myView.findViewById(R.id.playerTwoRadioButton);


        wonMatchButton = (Button)myView.findViewById(R.id.matchWonButton);
        lostMatchButton = (Button)myView.findViewById(R.id.matchLostButton);
        wonText = (TextView)myView.findViewById(R.id.wonText);
        lostText = (TextView)myView.findViewById(R.id.lostText);




        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        currentMatchID = sharedPreferences.getString("currentMatchID", null);
        myUserName = sharedPreferences.getString("myUserName", null);

        p1Accept = "null";
        p2Accept = "null";

        ///currentMatchID = ((activityChopBet)context).currentMatchID;







        //15 minutes progress bar
        acceptProgressBar = (ProgressBar) myView.findViewById(R.id.acceptProgressBar);

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
                    //pendingTextColours();

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

                } else {


                    matchTextColours();
                    winsAndLosses();
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

                }




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



    private void listenForPendingMatchChanges(){

        if(isAdded()) {


            listener = dbRef.child("PendingMatches").child(myUserName).child(currentMatchID).addValueEventListener(new ValueEventListener() {


                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {


                    if (isAdded()) {


                        if (dataSnapshot.child("playerOne").getValue() != null) {

                            int acceptColor = getResources().getColor(R.color.green);
                            int declineColor = getResources().getColor(R.color.colorPrimary);

                            final String firstPlayer = dataSnapshot.child("playerOne").getValue().toString();
                            String secondPlayer = dataSnapshot.child("playerTwo").getValue().toString();

                            if (Objects.equals(myUserName, firstPlayer)) {
                                playerTwoUserName = secondPlayer;
                            } else if (Objects.equals(myUserName, secondPlayer)) {
                                playerTwoUserName = firstPlayer;
                            }


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

                                    if(isAdded()) {



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
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });




                            if ((Objects.equals(p1Accept, "true")) && (Objects.equals(p2Accept, "false"))) {

                                dbRef.child("PendingMatches").child(myUserName).child(currentMatchID).removeEventListener(listener);
                                dbRef.child("PendingMatches").child(playerTwoUserName).child(currentMatchID).removeEventListener(listenerB);
                                //dbRef.child("PendingMatches").child(myUserName).removeValue();
                                dbRef.child("MatchObserver").child(myUserName).child("matchStatus").setValue("Open");
                                dbRef.child("MatchObserver").child(playerTwoUserName).child("matchStatus").setValue("Open");

                            } else if ((Objects.equals(p1Accept, "false")) && (Objects.equals(p2Accept, "true"))) {

                                dbRef.child("PendingMatches").child(myUserName).child(currentMatchID).removeEventListener(listener);
                                dbRef.child("PendingMatches").child(playerTwoUserName).child(currentMatchID).removeEventListener(listenerB);

                                //dbRef.child("PendingMatches").child(myUserName).removeValue();
                                dbRef.child("MatchObserver").child(myUserName).child("matchStatus").setValue("Open");
                                dbRef.child("MatchObserver").child(playerTwoUserName).child("matchStatus").setValue("Open");

                            } else if ((Objects.equals(p1Accept, "false")) && (Objects.equals(p2Accept, "false"))) {

                                dbRef.child("PendingMatches").child(myUserName).child(currentMatchID).removeEventListener(listener);
                                dbRef.child("PendingMatches").child(playerTwoUserName).child(currentMatchID).removeEventListener(listenerB);

                                //dbRef.child("PendingMatches").child(myUserName).removeValue();
                                dbRef.child("MatchObserver").child(myUserName).child("matchStatus").setValue("Open");
                                dbRef.child("MatchObserver").child(playerTwoUserName).child("matchStatus").setValue("Open");

                            }else if ((Objects.equals(p1Accept, "true")) && (Objects.equals(p2Accept, "true"))) {


                                if (Objects.equals(myUserName, dataSnapshot.child("playerOne").getValue().toString())) {



                                    dbRef.child("MatchObserver").child(playerTwoUserName).child("matchAccepted").setValue("true");
                                    dbRef.child("MatchObserver").child(myUserName).child("matchAccepted").setValue("true");

                                    String xAmount = dataSnapshot.child("betAmount").getValue().toString();
                                    String xConsole = dataSnapshot.child("betConsole").getValue().toString();
                                    String xGame = dataSnapshot.child("betGame").getValue().toString();
                                    String xInternet = dataSnapshot.child("betInternet").getValue().toString();


                                    //String matchKey = dbRef.child("Matches").child(myUserName).push().getKey();
                                    NewMatch newMatch = new NewMatch(currentMatchID, myUserName, playerTwoUserName, xAmount, 0, xConsole, xGame, xInternet, "Pending");




                                    dbRef.child("Matches").child(myUserName).child(currentMatchID).setValue(newMatch);
                                    dbRef.child("Matches").child(playerTwoUserName).child(currentMatchID).setValue(newMatch);



                                    //dbRef.child("PendingMatches").child(myUserName).child(currentMatchID).removeEventListener(listener);



                                }


                            }





                            winsAndLosses();
                            pendingTextColours();





                        }


                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


    }













    private void clickers(){


        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                acceptButton.setEnabled(false);

                Toast.makeText(context, "Match Accepted", Toast.LENGTH_SHORT).show();

                dbRef.child("PendingMatches").child(myUserName).child(currentMatchID).child("betStatus").setValue("true");



            }
        });


        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                declineButton.setEnabled(false);



                dbRef.child("PendingMatches").child(myUserName).child(currentMatchID).child("betStatus").setValue("false")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
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

                dbRef.child("MatchObserver").child(myUserName).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshotF) {
                        final String mCurrentMatchID = dataSnapshotF.child("currentMatchID").getValue().toString();







                        dbRef.child("Matches").child(myUserName).child(currentMatchID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshotE) {

                                String firstPlayer = dataSnapshotE.child("playerOne").getValue().toString();
                                String secondPlayer = dataSnapshotE.child("playerTwo").getValue().toString();



                                if (Objects.equals(myUserName, firstPlayer)) {
                                    playerTwoUserName = secondPlayer;
                                } else if (Objects.equals(myUserName, secondPlayer)) {
                                    playerTwoUserName = firstPlayer;
                                }


                                dbRef.child("Matches").child(myUserName).child(currentMatchID).child("wonOrLost").setValue("WON");
                                dbRef.child("Matches").child(playerTwoUserName).child(currentMatchID).child("wonOrLost").setValue("LOST");

                                dbRef.child("MatchObserver").child(myUserName).child("matchStatus").setValue("Open");
                                dbRef.child("MatchObserver").child(playerTwoUserName).child("matchStatus").setValue("Open");

                                dbRef.child("MatchObserver").child(playerTwoUserName).child("matchAccepted").removeValue();
                                dbRef.child("MatchObserver").child(myUserName).child("matchAccepted").removeValue();



                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });








                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });





            }
        });


        lostMatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isAdded()) {


                    dbRef.child("MatchObserver").child(myUserName).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshotF) {
                            final String mCurrentMatchID = dataSnapshotF.child("currentMatchID").getValue().toString();


                            dbRef.child("Matches").child(myUserName).child(currentMatchID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    String firstPlayer = dataSnapshot.child("playerOne").getValue().toString();
                                    String secondPlayer = dataSnapshot.child("playerTwo").getValue().toString();

                                    if (Objects.equals(myUserName, firstPlayer)) {
                                        playerTwoUserName = secondPlayer;
                                    } else if (Objects.equals(myUserName, secondPlayer)) {
                                        playerTwoUserName = firstPlayer;
                                    }


                                    dbRef.child("Matches").child(playerTwoUserName).child(currentMatchID).child("wonOrLost").setValue("WON");
                                    dbRef.child("Matches").child(myUserName).child(currentMatchID).child("wonOrLost").setValue("LOST");

                                    dbRef.child("MatchObserver").child(myUserName).child("matchStatus").setValue("Open");
                                    dbRef.child("MatchObserver").child(playerTwoUserName).child("matchStatus").setValue("Open");

                                    dbRef.child("MatchObserver").child(playerTwoUserName).child("matchAccepted").removeValue();
                                    dbRef.child("MatchObserver").child(myUserName).child("matchAccepted").removeValue();


                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }




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




    private void pendingTextColours(){

        if(isAdded()) {



            dbRef.child("Matches").child(myUserName).child(currentMatchID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshotE) {


                    listenerA = dbRef.child("Matches").child(myUserName).child(currentMatchID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshotC) {

                            if (isAdded()) {

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

                        }

                        @Override
                        public void onCancelled (DatabaseError databaseError){

                        }

                    });




                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });






        }

    }

    private void matchTextColours(){

        if(isAdded()) {


            dbRef.child("Matches").child(myUserName).child(currentMatchID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshotE) {



                    listenerA = dbRef.child("PendingMatches").child(myUserName).child(currentMatchID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshotC) {

                            if (isAdded()) {

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

                        }

                        @Override
                        public void onCancelled (DatabaseError databaseError){

                        }

                    });



                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });









        }
    }

    private void winsAndLosses(){

        if(isAdded()){
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
    public void onDestroyView() {
        super.onDestroyView();

        if(!isDetached()){
            //dbRef.child("PendingMatches").child(myUserName).child(currentMatchID).removeEventListener(listener);
            //dbRef.child("Matches").child(playerTwoUserName).child(currentMatchID).removeEventListener(listenerB);
            //dbRef.child("Matches").child(myUserName).child(currentMatchID).removeEventListener(listenerC);
        }


    }




}
