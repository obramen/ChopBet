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
import android.widget.AdapterView;
import android.widget.Button;
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

import antrix.chopbet.Activities.activityBetDetails;
import antrix.chopbet.BetClasses.BetUtilities;
import antrix.chopbet.Models.NewMatch;
import antrix.chopbet.R;
import de.hdodenhof.circleimageview.CircleImageView;

import static antrix.chopbet.R.id.amount;
import static antrix.chopbet.R.id.console;


public class fragmentHistory extends Fragment {

    String myPhoneNumber;
    FirebaseAuth mAuth;
    DatabaseReference dbRef;

    FirebaseListAdapter<NewMatch> adapter;
    ListView listView;

    Context context;
    View view;
    View myView;

    Query query;
    Activity activity;

    CharSequence previousItemDate = null;

    SharedPreferences sharedPreferences;

    String myUserName;
    TextView matchID;

    BetUtilities betUtilities;

    //TextView internet, console, game;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_history, container, false);



        declarations();
        loadHistory();
        clickers();






        return view;
    }




    private void declarations(){

        activity = getActivity();
        context = getActivity();
        myView = view;

        dbRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        myPhoneNumber = mAuth.getCurrentUser().getPhoneNumber();

        listView = (ListView)myView.findViewById(R.id.list_History);




        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        //currentMatchID = sharedPreferences.getString("currentMatchID", null);
        myUserName = sharedPreferences.getString("myUserName", null);


        query = dbRef.child("Matches").child(myUserName).orderByChild("index").limitToLast(10);



        betUtilities = new BetUtilities();






    }

    private void clickers(){


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




                if (Objects.equals(myUserName, model.getPlayerOne())) {

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




                } else if (Objects.equals(myUserName, model.getPlayerTwo())) {
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







}
