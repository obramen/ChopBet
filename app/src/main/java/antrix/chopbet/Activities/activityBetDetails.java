package antrix.chopbet.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import antrix.chopbet.BetClasses.BaseActivity;
import antrix.chopbet.Models.BetBuddy;
import antrix.chopbet.Models.NewMatch;
import antrix.chopbet.R;
import de.hdodenhof.circleimageview.CircleImageView;


public class activityBetDetails extends BaseActivity{




    String myPhoneNumber;
    String myUID;
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



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bet_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadActionbar("Bet Details");

        declarations();
        loadHistory();




    }


    private void declarations(){


        activity = activityBetDetails.this;
        context = this;

        dbRef = FirebaseDatabase.getInstance().getReference();

        fireDbRef = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        myPhoneNumber = mAuth.getCurrentUser().getPhoneNumber();
        myUID = mAuth.getCurrentUser().getUid();


        listView = (ListView)findViewById(R.id.list_BetDetails);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        myUserName = sharedPreferences.getString("myUserName", null);

        bundle = getIntent().getExtras();
        currentMatchID = bundle.getString("matchID");

        query = dbRef.child("Matches").child(myUserName).orderByChild("matchID").equalTo(currentMatchID);




    }


    private void loadHistory(){





        adapter = new FirebaseListAdapter<NewMatch>(activity, NewMatch.class, R.layout.list_bet_details, query) {
            @Override
            protected void populateView(View v, final NewMatch model, int position) {

                TextView date = (TextView)v.findViewById(R.id.date);
                TextView name = (TextView)v.findViewById(R.id.name);
                TextView contactSource = (TextView)v.findViewById(R.id.contactSource);
                TextView wonOrLost = (TextView)v.findViewById(R.id.wonOrLost);
                TextView matchID = (TextView)v.findViewById(R.id.matchID);
                TextView bottomAmount = (TextView)v.findViewById(R.id.bottomAmount);
                TextView topAmount = (TextView)v.findViewById(R.id.topAmount);
                TextView fee = (TextView)v.findViewById(R.id.fee);
                TextView report = (TextView)v.findViewById(R.id.report);
                TextView dispute = (TextView)v.findViewById(R.id.dispute);




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

                if (Objects.equals(model.getWonOrLost(), "WON")){
                    fee.setText(String.valueOf((Integer.parseInt(model.getBetAmount()))*(0.05)));
                } else {
                    fee.setText("0.00");
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

                } else if (Objects.equals(myUserName, model.getPlayerTwo())) {
                    name.setText(model.getPlayerOne());
                }


                if(DateUtils.isToday(model.getBetDate())){
                    date.setText("Today");
                } else {
                    date.setText(DateFormat.format(getString(R.string.dateformat), model.getBetDate()));
                }








            }


        };

        listView.setAdapter(adapter);










    }

    private void clickers(){




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
