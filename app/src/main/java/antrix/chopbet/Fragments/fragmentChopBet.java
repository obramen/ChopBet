package antrix.chopbet.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import antrix.chopbet.Activities.activityUserProfile;
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
    CircleImageView acceptButton, playerTwoImageView;

    String myPhoneNumber;
    String myUID;
    FirebaseAuth mAuth;
    DatabaseReference dbRef;

    RelativeLayout winChanceLayout, acceptLayout, opponentLayout;





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


        //dbRef = FirebaseDatabase.getInstance().getReference();
        //mAuth = FirebaseAuth.getInstance();
        //myPhoneNumber = mAuth.getCurrentUser().getPhoneNumber();
        //myUID = mAuth.getCurrentUser().getUid();


        findBetButton = (ButtonLoading)myView.findViewById(R.id.findBetButton);
        stopSearchButton = (Button)myView.findViewById(R.id.stopSearchButton);
        findingMatchTextView = (TextView)myView.findViewById(R.id.findingMatchTextView);
        coverLayout = (RelativeLayout)myView.findViewById(R.id.coverLayout);
        acceptButton = (CircleImageView)myView.findViewById(R.id.acceptButton);
        playerTwoImageView = (CircleImageView)myView.findViewById(R.id.playerTwoImageView);



        winChanceLayout = (RelativeLayout)myView.findViewById(R.id.winChanceLayout);
        acceptLayout = (RelativeLayout)myView.findViewById(R.id.acceptLayout);
        opponentLayout = (RelativeLayout)myView.findViewById(R.id.opponentLayout);



    }


    private void loadDefaults(){

        winChanceLayout.setVisibility(View.GONE);
        acceptLayout.setVisibility(View.GONE);
        opponentLayout.setVisibility(View.GONE);
    }






    private void clickers(){

        findBetButton.setOnButtonLoadingListener(new ButtonLoading.OnButtonLoadingListener() {
            @Override
            public void onClick() {

                findBetButton.setEnabled(true);

                //...
            }

            @Override
            public void onStart() {


                mHandler.postDelayed(new Runnable() {
                    public void run() {


                        stopSearchButton.setVisibility(View.VISIBLE);
                        findingMatchTextView.setVisibility(View.VISIBLE);

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

                findBetButton.setEnabled(true);
                findBetButton.setProgress(false);
                stopSearchButton.setVisibility(View.GONE);
                findingMatchTextView.setVisibility(View.GONE);


            }
        });



        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });




        playerTwoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, activityUserProfile.class);
                context.startActivity(intent);
            }
        });


    }






}
