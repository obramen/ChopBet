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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Objects;

import antrix.chopbet.Activities.activityBetDetails;
import antrix.chopbet.Models.NewMatch;
import antrix.chopbet.R;
import de.hdodenhof.circleimageview.CircleImageView;


public class fragmentHistory extends Fragment {

    String myPhoneNumber;
    String myUID;
    FirebaseAuth mAuth;
    DatabaseReference dbRef;

    FirebaseListAdapter<NewMatch> adapter;
    ListView listView;

    Context context;
    View view;
    View myView;

    Query query;
    Activity activity;

    long previousItemDate = 0;

    SharedPreferences sharedPreferences;

    String myUserName;
    TextView matchID;


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
        myUID = mAuth.getCurrentUser().getUid();

        listView = (ListView)myView.findViewById(R.id.list_History);




        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        //currentMatchID = sharedPreferences.getString("currentMatchID", null);
        myUserName = sharedPreferences.getString("myUserName", null);


        query = dbRef.child("Matches").child(myUserName).orderByChild("index").limitToLast(10);






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
                TextView contactSource = (TextView)v.findViewById(R.id.contactSource);
                TextView betResult = (TextView)v.findViewById(R.id.betResult);
                RelativeLayout topDividor = (RelativeLayout)v.findViewById(R.id.topDividor);
                RelativeLayout bottomDividor = (RelativeLayout)v.findViewById(R.id.bottomDividor);
                CircleImageView profileImage = (CircleImageView)v.findViewById(R.id.profileImage);
                RelativeLayout gameLayout = (RelativeLayout)v.findViewById(R.id.gameLayout);
                matchID = (TextView)v.findViewById(R.id.matchID);





                matchID.setText(model.getMatchID());

                /// DETERMINE OPPONENT
                if(Objects.equals(model.getPlayerOne(), myPhoneNumber)){
                    name.setText(model.getPlayerTwo());
                } else if (Objects.equals(model.getPlayerTwo(), myPhoneNumber)){
                    name.setText(model.getPlayerOne());
                }


                amount.setText(model.getBetAmount());

                betResult.setText(model.getWonOrLost());



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




                ////// SETTING THE DATE
                if (previousItemDate == 0){
                    topDividor.setVisibility(View.GONE);
                    if(DateUtils.isToday(model.getBetDate())){
                        date.setText("Today");
                        previousItemDate = model.getBetDate();
                    } else {
                        date.setText(DateFormat.format(getString(R.string.dateformat), model.getBetDate()));
                        previousItemDate = model.getBetDate();

                    }

                }else if (previousItemDate == model.getBetDate()){
                    topDividor.setVisibility(View.VISIBLE);
                    date.setVisibility(View.GONE);
                } else{
                    bottomDividor.setVisibility(View.VISIBLE);
                    if(DateUtils.isToday(model.getBetDate())){
                        date.setText("Today");
                        previousItemDate = model.getBetDate();
                    } else {
                        date.setText(DateFormat.format(getString(R.string.dateformat), model.getBetDate()));
                        previousItemDate = model.getBetDate();

                    }

                }








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



}
