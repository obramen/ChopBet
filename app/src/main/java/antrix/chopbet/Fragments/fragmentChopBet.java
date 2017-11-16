package antrix.chopbet.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.RunnableFuture;

import antrix.chopbet.Activities.activityUserProfile;
import antrix.chopbet.Models.FindBet;
import antrix.chopbet.Models.NewMatch;
import antrix.chopbet.R;
import de.hdodenhof.circleimageview.CircleImageView;
import io.rmiri.buttonloading.ButtonLoading;


public class fragmentChopBet extends Fragment {

    Context context;
    View view;
    View myView;

    ButtonLoading findBetButton;
    Button stopSearchButton;
    TextView findingMatchTextView;
    private Handler mHandler;
    private RelativeLayout coverLayout;

    String myPhoneNumber;
    String myUID;
    FirebaseAuth mAuth;
    DatabaseReference dbRef;
    DatabaseReference poolDbRef;

    RelativeLayout winChanceLayout, acceptLayout, opponentLayout;
    ValueEventListener listener;
    ValueEventListener listenerB;
    SharedPreferences sharedPreferences;
    String myUserName;



    String key;
    String mConsole, mGame, mAmount, mInternet;
    String mSelectedPool;
    String matchStatus;


    ProgressDialog progressDialog;







    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_chop_bet, container, false);


        declarations();
        loadDefaults();
        clickers();


        return view;
    }

    private void declarations(){

        context = getActivity();
        myView = view;
        mHandler = new Handler();


        dbRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        myPhoneNumber = mAuth.getCurrentUser().getPhoneNumber();
        myUID = mAuth.getCurrentUser().getUid();

        progressDialog = new ProgressDialog(context);


        findBetButton = (ButtonLoading)myView.findViewById(R.id.findBetButton);
        stopSearchButton = (Button)myView.findViewById(R.id.stopSearchButton);
        findingMatchTextView = (TextView)myView.findViewById(R.id.findingMatchTextView);
        //coverLayout = (RelativeLayout)myView.findViewById(R.id.coverLayout);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        myUserName = sharedPreferences.getString("myUserName", null);


    }


    private void loadDefaults(){

        //winChanceLayout.setVisibility(View.GONE);
        //acceptLayout.setVisibility(View.GONE);
        //opponentLayout.setVisibility(View.GONE);


        findBetButton.setVisibility(View.VISIBLE);
    }






    private void listenForMatchType(){

        dbRef.child("MatchObserver").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if(dataSnapshot.child("matchAccepted").getValue() == null){


                } else {

                    findBetButton.setVisibility(View.VISIBLE);

                }




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }





    private void clickers(){



        findBetButton.setOnButtonLoadingListener(new ButtonLoading.OnButtonLoadingListener() {
            @Override
            public void onClick() {


                findBetButton.setEnabled(true);
                dbRef.child("MatchObserver").child(myUserName).child("matchAccepted").removeValue();
                dbRef.child("PendingMatches").child(myUserName).removeValue();

                //...
            }

            @Override
            public void onStart() {


                mHandler.postDelayed(new Runnable() {
                    public void run() {


                        stopSearchButton.setVisibility(View.VISIBLE);
                        findingMatchTextView.setVisibility(View.VISIBLE);



                        mConsole = "PS4";
                        mGame = "FIFA 18";
                        mAmount = "50";
                        mInternet = "FIBRE";
                        matchSearch(mConsole, mGame, mAmount, mInternet);
                        Log.d("Search", "Match search in progress");




                    }
                }, 3000);


                //...
            }

            @Override
            public void onFinish() {
                //...
            }
        });







        stopSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                stopMatchSearchOptions();
                matchCancel();


            }
        });


    }


    public void stopMatchSearchOptions(){
        findBetButton.setEnabled(true);
        findBetButton.setProgress(false);
        stopSearchButton.setVisibility(View.GONE);
        findingMatchTextView.setVisibility(View.GONE);
    }


    public void matchCancel(){

        poolDbRef.child(mSelectedPool).child(key).removeValue();

    }






    private void matchSearch(String console, String game, String amount, @Nullable String internet){





        final Random poolNumber = new Random();
        int selectedPool = poolNumber.nextInt(2 - 1) + 1;
        mSelectedPool = String.valueOf(selectedPool);


        poolDbRef = dbRef.child("MatchPool").child(console).child(game).child(amount);
        //key = poolDbRef.child(mSelectedPool).push().getKey();
        key = myUserName;
        FindBet findBet = new FindBet(key, myUserName, amount, "Open", internet);
        poolDbRef.child(mSelectedPool).child(key).setValue(findBet).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {



                listenerB = poolDbRef.child(mSelectedPool).child(key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshotC) {


                        if (dataSnapshotC.getValue() != null){

                            if(Objects.equals(dataSnapshotC.child("findStatus").getValue().toString(), "Closed")){
                                ///...... you found a match, do something here

                                stopMatchSearchOptions();
                                poolDbRef.child(mSelectedPool).child(key).removeEventListener(listenerB);




                            } else {
                                ////.... you've not found a match.

                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        ///... wait 3 seconds to be paired

                                        matchPair();



                                    }
                                }, 5000);
                            }

                        } else {

                            stopMatchSearchOptions();
                            poolDbRef.child(mSelectedPool).child(key).removeEventListener(listenerB);
                        }




                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        });




    }






    private void matchPair(){




        listener = poolDbRef.child(mSelectedPool).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshotB) {


                if (dataSnapshotB.hasChildren()){

                    List<String> bets = new ArrayList<String>();
                    SparseArray<Object> mBets = new SparseArray<Object>();

                    long numberOfMatches = dataSnapshotB.getChildrenCount();

                    if (numberOfMatches > 1){
                        int y = 0;
                        for(DataSnapshot mData: dataSnapshotB.getChildren()){

                            if (Objects.equals(mData.getKey(), key)) {
                            }else{
                                bets.add(mData.getKey());
                                mBets.put(y, mData.getKey());
                                y++;

                            }

                        }



                        Random pooledMatches = new Random();
                        int pairedMatch = pooledMatches.nextInt(mBets.size());
                        final Object pairedOpponent = mBets.get(pairedMatch);

                        String playerTwoUserName = pairedOpponent.toString();

                        progressDialog.setMessage("Setting up your match...");
                        progressDialog.show();

                        //Toast.makeText(context, "You paired with " + pairedOpponent.toString(), Toast.LENGTH_SHORT).show();
                        poolDbRef.child(mSelectedPool).child(playerTwoUserName).child("findStatus").setValue("Closed");
                        poolDbRef.child(mSelectedPool).child(key).child("findStatus").setValue("Closed");

                        poolDbRef.child(mSelectedPool).removeEventListener(listener);
                        poolDbRef.child(mSelectedPool).child(key).removeEventListener(listenerB);




                        if (myUserName.substring(1).compareTo(playerTwoUserName.substring(1)) > 0){

                            String matchKey = dbRef.child("PendingMatches").child(myUserName).push().getKey();
                            NewMatch newMatch = new NewMatch(matchKey, myUserName, playerTwoUserName, mAmount, "Fee", mConsole, mGame, mInternet, "Pending");

                            dbRef.child("PendingMatches").child(myUserName).child(matchKey).setValue(newMatch);
                            dbRef.child("PendingMatches").child(playerTwoUserName).child(matchKey).setValue(newMatch);


                            poolDbRef.child(mSelectedPool).child(key).removeValue();
                            poolDbRef.child(mSelectedPool).child(pairedOpponent.toString()).removeValue();

                            dbRef.child("MatchObserver").child(myUserName).child("matchStatus").setValue("Closed");
                            dbRef.child("MatchObserver").child(myUserName).child("currentMatchID").setValue(matchKey);

                            dbRef.child("MatchObserver").child(playerTwoUserName).child("matchStatus").setValue("Closed");
                            dbRef.child("MatchObserver").child(playerTwoUserName).child("currentMatchID").setValue(matchKey);


                        }



                        findBetButton.setVisibility(View.GONE);

                        stopMatchSearchOptions();

                        progressDialog.dismiss();

                        mBets.clear();


                        //poolDbRef.child(mSelectedPool).removeEventListener(listener);


                    }


                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }













}
