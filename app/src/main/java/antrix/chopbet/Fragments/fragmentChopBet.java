package antrix.chopbet.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.dx.dxloadingbutton.lib.LoadingButton;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.RunnableFuture;

import antrix.chopbet.Activities.activityBetDetails;
import antrix.chopbet.Activities.activityUserProfile;
import antrix.chopbet.BetClasses.BetUtilities;
import antrix.chopbet.Models.FindBet;
import antrix.chopbet.Models.NewMatch;
import antrix.chopbet.R;
import de.hdodenhof.circleimageview.CircleImageView;
import io.rmiri.buttonloading.ButtonLoading;
import tgio.rncryptor.RNCryptorNative;

import static android.content.ContentValues.TAG;


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
    FirebaseAuth mAuth;
    DatabaseReference dbRef;
    DatabaseReference poolDbRef;

    RelativeLayout winChanceLayout, acceptLayout, opponentLayout;
    ValueEventListener listener;
    ValueEventListener listenerB;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    String myUserName;

    Activity activity;


    String myUID;
    RNCryptorNative rnCryptorNative;
    private String diamondKey = null;
    private String goldKey = null;
    String currentBalance = null;

    String key;
    String mConsole, mGame, mAmount, mInternet;
    String mSelectedPool;
    String matchStatus;

    Query query;
    FirebaseListAdapter<NewMatch> adapter;
    ListView listView;


    ProgressDialog progressDialog;

    TextView consoleTextView, gameTextView, amountTextView, internetTextView;
    ImageView searchParameters;
    NestedScrollView matchParametersSheet;

    String console, game, amount, internet;

    LoadingButton btnSave, btnCancel;

    RadioButton ps4Button, xboxButton, pcButton, fifaButton, mkButton, a10Button, a20Button, a50Button,
                a100Button, i3GButton, i4GButton, broadbandButton, fibreButton;


    TextView matchID;

    BetUtilities betUtilities;

    CardView historyCard;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chop_bet, container, false);


        declarations();
        loadDefaults();
        clickers();
        loadPrimeKey();
        loadHistory();





        return view;
    }

    private void declarations(){

        activity = getActivity();
        context = getActivity();
        myView = view;
        mHandler = new Handler();


        dbRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        myPhoneNumber = mAuth.getCurrentUser().getPhoneNumber();

        progressDialog = new ProgressDialog(context);

        rnCryptorNative = new RNCryptorNative();



        findBetButton = (ButtonLoading)myView.findViewById(R.id.findBetButton);
        stopSearchButton = (Button)myView.findViewById(R.id.stopSearchButton);
        findingMatchTextView = (TextView)myView.findViewById(R.id.findingMatchTextView);
        //coverLayout = (RelativeLayout)myView.findViewById(R.id.coverLayout);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();

        myUserName = sharedPreferences.getString("myUserName", null);
        mConsole = sharedPreferences.getString("console", null);
        mGame = sharedPreferences.getString("game", null);
        mAmount = sharedPreferences.getString("amount", null);
        mInternet = sharedPreferences.getString("internet", null);

        consoleTextView = (TextView)myView.findViewById(R.id.console);
        gameTextView = (TextView)myView.findViewById(R.id.game);
        amountTextView = (TextView)myView.findViewById(R.id.amount);
        internetTextView = (TextView)myView.findViewById(R.id.internet);
        searchParameters = (ImageView) myView.findViewById(R.id.searchParameters);

        matchParametersSheet = (NestedScrollView)myView.findViewById(R.id.matchParametersSheet);

        btnSave = (LoadingButton)myView.findViewById(R.id.btnSave);
        btnCancel = (LoadingButton)myView.findViewById(R.id.btnCancel);


        ps4Button = (RadioButton)myView.findViewById(R.id.ps4Button);
        xboxButton = (RadioButton)myView.findViewById(R.id.xboxOneButton);
        pcButton = (RadioButton)myView.findViewById(R.id.pcButton);
        fifaButton = (RadioButton)myView.findViewById(R.id.fifaButton);
        mkButton = (RadioButton)myView.findViewById(R.id.mkButton);
        a10Button = (RadioButton)myView.findViewById(R.id.a10Button);
        a20Button = (RadioButton)myView.findViewById(R.id.a20Button);
        a50Button = (RadioButton)myView.findViewById(R.id.a50Button);
        a100Button = (RadioButton)myView.findViewById(R.id.a100Button);
        i3GButton = (RadioButton)myView.findViewById(R.id.i3GButton);
        i4GButton = (RadioButton)myView.findViewById(R.id.i4GButton);
        broadbandButton = (RadioButton)myView.findViewById(R.id.broadbandButton);
        fibreButton = (RadioButton)myView.findViewById(R.id.fibreButton);

        historyCard = (CardView)myView.findViewById(R.id.historyCard);


        query = dbRef.child("Matches").child(myUserName).orderByChild("index").limitToFirst(1);
        listView = (ListView)myView.findViewById(R.id.list_History);


        betUtilities = new BetUtilities();



        if (query == null){
            historyCard.setVisibility(View.INVISIBLE);
        }





        if (mConsole != null){
            consoleTextView.setText(mConsole);
        } else{
            consoleTextView.setText("");
            mConsole = "";
        }


        if (mGame != null){
            gameTextView.setText(mGame);
        } else{
            gameTextView.setText("");
            mGame = "";
        }


        if (mAmount != null){
            amountTextView.setText(mAmount);
        } else{
            amountTextView.setText("");
            mAmount = "";
        }


        if (mInternet != null){
            internetTextView.setText(mInternet);
        } else{
            internetTextView.setText("");
            mInternet = "";
        }


        matchTextColours(consoleTextView, gameTextView, internetTextView, amountTextView);



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


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                matchID = (TextView) view.findViewById(R.id.matchID);
                Intent intent = new Intent(activity, activityBetDetails.class);
                intent.putExtra("matchID", matchID.getText().toString());
                startActivity(intent);
            }
        });





        findBetButton.setOnButtonLoadingListener(new ButtonLoading.OnButtonLoadingListener() {
            @Override
            public void onClick() {








                if(mConsole  == null || Objects.equals(mConsole, "")){
                    Toast.makeText(context, "Select console", Toast.LENGTH_SHORT).show();
                    findBetButton.setProgress(false);
                    findBetButton.setEnabled(true);
                    return;
                }


                if(mGame  == null || Objects.equals(mGame, "")){
                    Toast.makeText(context, "Select game", Toast.LENGTH_SHORT).show();
                    findBetButton.setProgress(false);
                    findBetButton.setEnabled(true);
                    return;
                }


                if(mAmount  == null || Objects.equals(mAmount, "")){
                    Toast.makeText(context, "Select amount", Toast.LENGTH_SHORT).show();
                    findBetButton.setProgress(false);
                    findBetButton.setEnabled(true);
                    return;
                }


                if(mInternet  == null || Objects.equals(mInternet, "")){
                    Toast.makeText(context, "Select internet", Toast.LENGTH_SHORT).show();
                    findBetButton.setProgress(false);
                    findBetButton.setEnabled(true);
                    return;
                }


                if (Double.valueOf(currentBalance) < Double.valueOf(mAmount)){
                    Toast.makeText(context, "You no get money you want play bet, go find money come", Toast.LENGTH_LONG).show();
                    dbRef.child("MatchObserver").child(myUserName).child("searchState").removeValue();
                    findBetButton.setProgress(false);
                    findBetButton.setEnabled(true);
                    return;
                } else {
                    findBetButton.setEnabled(true);
                    dbRef.child("MatchObserver").child(myUserName).child("matchAccepted").removeValue();
                    dbRef.child("PendingMatches").child(myUserName).removeValue();

                    //...
                }




            }

            @Override
            public void onStart() {




                if(mConsole  == null || Objects.equals(mConsole, "")){
                    Toast.makeText(context, "Select console", Toast.LENGTH_SHORT).show();
                    findBetButton.setProgress(false);
                    findBetButton.setEnabled(true);
                    return;
                }


                if(mGame  == null || Objects.equals(mGame, "")){
                    Toast.makeText(context, "Select game", Toast.LENGTH_SHORT).show();
                    findBetButton.setProgress(false);
                    findBetButton.setEnabled(true);
                    return;
                }


                if(mAmount  == null || Objects.equals(mAmount, "")){
                    Toast.makeText(context, "Select amount", Toast.LENGTH_SHORT).show();
                    findBetButton.setProgress(false);
                    findBetButton.setEnabled(true);
                    return;
                }


                if(mInternet  == null || Objects.equals(mInternet, "")){
                    Toast.makeText(context, "Select internet", Toast.LENGTH_SHORT).show();
                    findBetButton.setProgress(false);
                    findBetButton.setEnabled(true);
                    return;
                }








                if (Double.valueOf(currentBalance) < Double.valueOf(mAmount)){
                    Toast.makeText(context, "You no get money you want play bet, go find money come", Toast.LENGTH_LONG).show();
                    dbRef.child("MatchObserver").child(myUserName).child("searchState").removeValue();
                    findBetButton.setProgress(false);
                    findBetButton.setEnabled(true);
                    return;
                } else {

                    dbRef.child("MatchObserver").child(myUserName).child("searchState").setValue("searching");

                    mHandler.postDelayed(new Runnable() {
                        public void run() {


                            stopSearchButton.setVisibility(View.VISIBLE);
                            //findingMatchTextView.setVisibility(View.VISIBLE);

                            matchSearch(mConsole, mGame, mAmount, mInternet);
                            Log.d("Search", "Match search in progress");





                        }
                    }, 3000);


                }









                //...
            }

            @Override
            public void onFinish() {
                //...
            }
        });



        searchParameters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadSearchSheet();

            }
        });

        amountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadSearchSheet();

            }
        });

        internetTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadSearchSheet();

            }
        });

        consoleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadSearchSheet();

            }
        });


        gameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadSearchSheet();

            }
        });


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                matchParametersSheet.setVisibility(View.GONE);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                matchParametersSheet.setVisibility(View.GONE);

                onAmountClicked(a10Button);
                onAmountClicked(a20Button);
                onAmountClicked(a50Button);
                onAmountClicked(a100Button);

                onConsoleClicked(ps4Button);
                onConsoleClicked(xboxButton);
                onConsoleClicked(pcButton);

                onGameClicked(mkButton);
                onGameClicked(fifaButton);

                onInternetClicked(i3GButton);
                onInternetClicked(i4GButton);
                onInternetClicked(broadbandButton);
                onInternetClicked(fibreButton);


                if (mConsole != null){
                    consoleTextView.setText(mConsole);
                    editor.putString("console", mConsole);
                    editor.apply();
                } else{
                    consoleTextView.setText("");
                }


                if (mGame != null){
                    gameTextView.setText(mGame);
                    editor.putString("game", mGame);
                    editor.apply();

                } else{
                    gameTextView.setText("");
                }


                if (mAmount != null){
                    amountTextView.setText("GHS " + mAmount);
                    editor.putString("amount", mAmount);
                    editor.apply();

                } else{
                    amountTextView.setText("");
                }


                if (mInternet != null){
                    internetTextView.setText(mInternet);
                    editor.putString("internet", mInternet);
                    editor.apply();

                } else{
                    internetTextView.setText("");
                }


                matchTextColours(consoleTextView, gameTextView, internetTextView, amountTextView);




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
        //findingMatchTextView.setVisibility(View.GONE);

    }


    public void matchCancel(){

        poolDbRef.child(mSelectedPool).child(key).removeValue();
        dbRef.child("MatchObserver").child(myUserName).child("searchState").removeValue();


    }






    private void matchSearch(final String console, final String game, final String amount, @Nullable String internet){





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

                Map<String, Object> myMatchPath = new HashMap<>();
                myMatchPath.put("console", console);
                myMatchPath.put("game", game);
                myMatchPath.put("amount", amount);
                myMatchPath.put("pool", mSelectedPool);




                dbRef.child("OnlinePresence").child(myPhoneNumber).child("currentMatchPool").setValue(myMatchPath);

/*

                listenerB = poolDbRef.child(mSelectedPool).child(key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshotC) {


                        if (dataSnapshotC.getValue() != null){

                            if(Objects.equals(dataSnapshotC.child("findStatus").getValue().toString(), "true")){
                                ///...... you found a match, do something here

                                stopMatchSearchOptions();
                                poolDbRef.child(mSelectedPool).child(key).removeEventListener(listenerB);




                            } else {
                                ////.... you've not found a match.

                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        ///... wait 3 seconds to be paired

                                        //matchPair();



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
*/


            }
        });




    }






    public void onAmountClicked(View view){

        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        //Check which radio button was clicked
        switch(view.getId()){
            case R.id.a10Button:
                if(checked)
                    // change amount to 10
                mAmount = "10";
                break;

            case R.id.a20Button:
                if(checked)
                    // change amount to 10
                    mAmount = "20";
                break;

            case R.id.a50Button:
                if(checked)
                    // change amount to 10
                    mAmount = "50";
                break;


            case R.id.a100Button:
                if(checked)
                    // change amount to 10
                    mAmount = "100";
                break;

        }
    }


    public void onConsoleClicked(View view){

        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        //Check which radio button was clicked
        switch(view.getId()){
            case R.id.ps4Button:
                if(checked)
                    // change amount to 10
                mConsole = "PS4";
                break;

            case R.id.xboxOneButton:
                if(checked)
                    // change amount to 10
                    mConsole = "XBOX ONE";
                break;

            case R.id.pcButton:
                if(checked)
                    // change amount to 10
                    mConsole = "PC";
                break;
        }
    }


    public void onGameClicked(View view){

        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        //Check which radio button was clicked
        switch(view.getId()){
            case R.id.fifaButton:
                if(checked)
                    // change amount to 10
                    mGame = "FIFA 18";
                break;

            case R.id.mkButton:
                if(checked)
                    // change amount to 10
                    mGame = "MK XL";
                break;
        }
    }



    public void onInternetClicked(View view){

        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        //Check which radio button was clicked
        switch(view.getId()){
            case R.id.i3GButton:
                if(checked)
                    // change amount to 10
                    mInternet = "3G";
                break;

            case R.id.i4GButton:
                if(checked)
                    // change amount to 10
                    mInternet = "4G";
                break;

            case R.id.broadbandButton:
                if(checked)
                    // change amount to 10
                    mInternet = "BROADBAND";
                break;


            case R.id.fibreButton:
                if(checked)
                    // change amount to 10
                    mInternet = "FIBRE";
                break;

        }
    }




    public void onInternetLoad(String string){

        switch(string){
            case "3G":
                    i3GButton.setChecked(true);
                break;

            case "4G":
                    i4GButton.setChecked(true);
                break;

            case "BROADBAND":
                    broadbandButton.setChecked(true);
                break;

            case "FIBRE":
                    fibreButton.setChecked(true);
                break;
            case "":
                break;

        }
    }



    public void onConsoleLoad(String string){

        switch(string){
            case "PS4":
                    ps4Button.setChecked(true);
                break;

            case "XBOX ONE":
                    xboxButton.setChecked(true);
                break;

            case "PC":
                    pcButton.setChecked(true);
                break;
            case "":
                break;

        }
    }


    public void onGameLoad(String string){

        switch(string){
            case "FIFA 18":
                    fifaButton.setChecked(true);
                break;

            case "MK XL":
                    mkButton.setChecked(true);
                break;

            case "":
                break;

        }
    }



    public void onAmountLoad(String string){

        switch(string){
            case "10":
                a10Button.setChecked(true);
                break;

            case "20":
                a20Button.setChecked(true);
                break;

            case "50":
                a50Button.setChecked(true);
                break;

            case "100":
                a100Button.setChecked(true);
                break;
            case "":
                break;

        }
    }





    private void matchTextColours(final TextView console, final TextView game, final TextView internet, final TextView amount){


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


    private void loadSearchSheet(){

        matchParametersSheet.setVisibility(View.VISIBLE);
        onInternetLoad(mInternet);
        onConsoleLoad(mConsole);
        onGameLoad(mGame);
        onAmountLoad(mAmount);

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
                            NewMatch newMatch = new NewMatch(matchKey, myUserName, playerTwoUserName, mAmount, 0, mConsole, mGame, mInternet, "Pending");

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






    private void loadHistory(){





        adapter = new FirebaseListAdapter<NewMatch>(activity, NewMatch.class, R.layout.list_history, query) {
            @Override
            protected void populateView(View v, final NewMatch model, int position) {

                if (adapter.getCount() == 0){
                    historyCard.setVisibility(View.INVISIBLE);
                } else {
                    historyCard.setVisibility(View.VISIBLE);
                }



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
                date.setVisibility(View.GONE);







                matchTextColours(model.getMatchID(), console, game, internet);








                gameLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, activityBetDetails.class);
                        intent.putExtra("matchID", model.getMatchID());
                        intent.putExtra("username", myUserName);
                        startActivity(intent);
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





        };


        if (adapter.getCount() == 0){
            historyCard.setVisibility(View.INVISIBLE);
        } else {
            historyCard.setVisibility(View.VISIBLE);
        }

        listView.setAdapter(adapter);







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

                    loadBalance();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


    }



    private void loadBalance(){

        goldKey = rnCryptorNative.decrypt(diamondKey, myUID);

        Log.d(TAG, "diamondKey: " + diamondKey);


        dbRef.child("Xperience").child(goldKey).child("Oxygen").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshotB) {

                if(dataSnapshotB.hasChildren()) {



                    currentBalance = dataSnapshotB.child("Bounty").getValue().toString();


                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }












}
