package antrix.chopbet.BetServices;


import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.widget.Toast;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import antrix.chopbet.Activities.activityBetDetails;
import antrix.chopbet.Activities.activityNewBet;
import antrix.chopbet.R;
import br.com.goncalves.pugnotification.notification.PugNotification;

public class NotificationService extends Service {

    DatabaseReference dbRef;
    SharedPreferences sharedPreferences;
    String myUserName;

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        //Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();

        dbRef = FirebaseDatabase.getInstance().getReference();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        myUserName = sharedPreferences.getString("myUserName", null);

        loadNotification();


        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        //Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
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
                            String reference = dataSnapshot.child("reference").getValue().toString();

                            if (Objects.equals(dataSnapshot.child("playerOne").getValue().toString(), myUserName)) {
                                opponenet = dataSnapshot.child("playerTwo").getValue().toString();

                            } else {
                                opponenet = dataSnapshot.child("playerOne").getValue().toString();

                            }


                            Bundle NBundle = new Bundle();

                            switch (type){
                                case "New Match":

                                    PugNotification.with(getApplicationContext())
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
                                    PugNotification.with(getApplicationContext())
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

                                case "Account Credited":
                                    NBundle.putString("matchID", reference);

                                    PugNotification.with(getApplicationContext())
                                            .load()
                                            .title(type)
                                            .message("Your account has been credited for your match against - " + opponenet)
                                            .bigTextStyle("Your account has been credited for your match against - " + opponenet)
                                            .smallIcon(R.mipmap.ic_launcher_round)
                                            .largeIcon(R.mipmap.ic_launcher_round)
                                            .flags(Notification.DEFAULT_ALL)
                                            .click(activityBetDetails.class, NBundle)
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