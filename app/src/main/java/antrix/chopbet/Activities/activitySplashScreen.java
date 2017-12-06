package antrix.chopbet.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import antrix.chopbet.R;


public class activitySplashScreen extends Activity {

    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    /** Called when the activity is first created. */


    //Persistence persistence;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_splash_screen);

        //persistence = new Persistence();


        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {


                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    // User is signed in

                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activitySplashScreen.this);
                    if (sharedPreferences.getString("myUserName", null) != null){
                        String myUserName = sharedPreferences.getString("myUserName", null);

                        FirebaseDatabase.getInstance().getReference().child("MatchObserver").child(myUserName).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (dataSnapshot.child("matchStatus").getValue() != null){
                                    if (Objects.equals(dataSnapshot.child("matchStatus").getValue().toString(), "Closed")){
                                        String currentMatchID = dataSnapshot.child("currentMatchID").getValue().toString();
                                        Intent betIntent = new Intent(activitySplashScreen.this, activityNewBet.class);
                                        betIntent.putExtra("currentMatchID", currentMatchID);
                                        startActivity(betIntent);
                                        finish();
                                    } else {
                                        Intent intent = new Intent(activitySplashScreen.this, activityChopBet.class);
                                        //code status is used to tell the ChopBet activity where the extras is coming from.
                                        // the number "0" is used to signal that the intent is coming from splash screen activity.
                                        // Hence it comes with no country code attached to it
                                        intent.putExtra("countryCodeStatus", "0");
                                        intent.putExtra("countryCode", "0");
                                        startActivity(intent);
                                        finish();
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    } else {

                        Intent intent = new Intent(activitySplashScreen.this, activityChopBet.class);
                        //code status is used to tell the ChopBet activity where the extras is coming from.
                        // the number "0" is used to signal that the intent is coming from splash screen activity.
                        // Hence it comes with no country code attached to it
                        intent.putExtra("countryCodeStatus", "0");
                        intent.putExtra("countryCode", "0");

                        startActivity(intent);
                        finish();
                    }






                } else {
                    // No user is signed in
                    Intent openRegisterLoginIntent = new Intent(activitySplashScreen.this, activityRegisterLogin.class);
                    startActivity(openRegisterLoginIntent);
                    FirebaseAuth.getInstance().signOut();
                    finish();
                }











            }





        }, SPLASH_DISPLAY_LENGTH);
    }


}



