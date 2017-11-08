package antrix.chopbet.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

                    Intent openLeapIntent = new Intent(activitySplashScreen.this, activityChopBet.class);
                    //code status is used to tell the Leap activity where the extras is coming from.
                    // the number "0" is used to signal that the intent is coming from splash screen activity.
                    // Hence it comes with no country code attached to it
                    openLeapIntent.putExtra("countryCodeStatus", "0");
                    openLeapIntent.putExtra("countryCode", "0");

                    startActivity(openLeapIntent);
                    finish();


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



