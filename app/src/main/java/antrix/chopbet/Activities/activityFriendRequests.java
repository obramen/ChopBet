package antrix.chopbet.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import antrix.chopbet.BetClasses.BaseActivity;
import antrix.chopbet.Models.BetBuddy;
import antrix.chopbet.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class activityFriendRequests extends BaseActivity{


    String myPhoneNumber;
    FirebaseAuth mAuth;
    FirebaseFirestore fireDbRef;
    DatabaseReference dbRef;

    FirebaseListAdapter<BetBuddy> adapterFriends;
    ListView listView;

    Context context;
    View view;
    View myView;

    String myUserName;
    SharedPreferences sharedPreferences;


    Activity activity;

    TextInputEditText searchText;

    ValueEventListener listener;
    DatabaseReference friendsDbRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_requests);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadActionbar("Friend Requests");
        getSupportActionBar().setElevation(0);



        declarations();
        searchFriends();


    }


    private void declarations(){


        activity = activityFriendRequests.this;
        context = this;

        dbRef = FirebaseDatabase.getInstance().getReference();

        fireDbRef = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        myPhoneNumber = mAuth.getCurrentUser().getPhoneNumber();


        listView = (ListView)findViewById(R.id.list_FriendRequests);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        myUserName = sharedPreferences.getString("myUserName", null);


    }




    private void searchFriends(){


        //final Query userDbRef = dbRef.child("UserNames").orderByKey().equalTo(username);
        Query userDBRef = dbRef.child("Friends").child(myPhoneNumber).orderByChild("status").equalTo("Pending");
        friendsDbRef = dbRef.child("Friends").child(myPhoneNumber);


        adapterFriends = new FirebaseListAdapter<BetBuddy>(activity, BetBuddy.class, R.layout.list_friends, userDBRef) {
            @Override
            protected void populateView(View v, final BetBuddy model, int position) {


                TextView name = (TextView) v.findViewById(R.id.name);
                CircleImageView profileImage = (CircleImageView) v.findViewById(R.id.profileImage);
                final TextView addFriend = (TextView) v.findViewById(R.id.addFriend);

                String phoneNumber = model.getPhoneNumber();
                name.setText(model.getUserName());

                addFriend.setText(model.getStatus());

                addFriend.setEnabled(true);
                addFriend.setTypeface(null, Typeface.NORMAL);
                addFriend.setTextColor(getResources().getColor(R.color.colorPrimary));
                addFriend.setText("Accept");
                addFriend.setVisibility(View.VISIBLE);

/*


                listener = friendsDbRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(model.getUserName()).exists()) {
                            String status = dataSnapshot.child(model.getUserName()).child("status").getValue().toString();


                            switch (status) {
                                case "Add Friend":
                                    addFriend.setEnabled(true);
                                    addFriend.setTypeface(null, Typeface.NORMAL);
                                    addFriend.setTextColor(getResources().getColor(R.color.colorPrimary));
                                    addFriend.setText("Add Friend");
                                    addFriend.setVisibility(View.VISIBLE);

                                    break;
                                case "Pending":
                                    addFriend.setEnabled(true);
                                    addFriend.setTypeface(null, Typeface.NORMAL);
                                    addFriend.setTextColor(getResources().getColor(R.color.colorPrimary));
                                    addFriend.setText("Accept");
                                    addFriend.setVisibility(View.VISIBLE);

                                    break;
                                case "Sent":
                                    addFriend.setEnabled(false);
                                    addFriend.setTypeface(null, Typeface.ITALIC);
                                    addFriend.setTextColor(getResources().getColor(R.color.selector));
                                    addFriend.setText("Sent");
                                    addFriend.setVisibility(View.VISIBLE);

                                    break;
                                case "Friend":
                                    addFriend.setEnabled(false);
                                    addFriend.setTypeface(null, Typeface.ITALIC);
                                    addFriend.setTextColor(getResources().getColor(R.color.selector));
                                    addFriend.setText("Friend");
                                    addFriend.setVisibility(View.VISIBLE);

                                    break;

                            }


                        } else {
                            addFriend.setVisibility(View.VISIBLE);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

*/





                addFriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final BetBuddy myBetBuddy = new BetBuddy(model.getUserName(), model.getPhoneNumber(), "Sent", "false");
                        BetBuddy hisBetBuddy = new BetBuddy(myUserName, myPhoneNumber, "Pending", "false");

                        switch (addFriend.getText().toString()) {
                            case "Add Friend":
                                dbRef.child("Friends").child(myPhoneNumber).child(model.getUserName()).setValue(myBetBuddy);
                                dbRef.child("Friends").child(model.getPhoneNumber()).child(myUserName).setValue(hisBetBuddy);

                                break;
                            case "Accept":
                                dbRef.child("Friends").child(myPhoneNumber).child(model.getUserName()).child("status").setValue("Friend");
                                dbRef.child("Friends").child(model.getPhoneNumber()).child(myUserName).child("status").setValue("Friend");

                                break;
                            case "Sent":

                                break;

                        }
                    }
                });


            }

        };

        listView.setAdapter(adapterFriends);










    }








    public void loadActionbar(String title){

        final ActionBar abar = getSupportActionBar();
        //abar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_background));//line under the action bar
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




    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }


}
