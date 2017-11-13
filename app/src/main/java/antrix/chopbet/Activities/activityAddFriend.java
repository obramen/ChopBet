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
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import antrix.chopbet.Adapters.AdapterBetBuddy;
import antrix.chopbet.BetClasses.BaseActivity;
import antrix.chopbet.BetClasses.BetUtilities;
import antrix.chopbet.Models.BetBuddy;
import antrix.chopbet.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class activityAddFriend extends BaseActivity{



    String myPhoneNumber;
    String myUID;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadActionbar("Add Friend");



        declarations();

        //listFriends();

    }

    private void declarations(){


        activity = activityAddFriend.this;
        context = this;

        dbRef = FirebaseDatabase.getInstance().getReference();

        fireDbRef = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        myPhoneNumber = mAuth.getCurrentUser().getPhoneNumber();
        myUID = mAuth.getCurrentUser().getUid();


        listView = (ListView)findViewById(R.id.list_Friends);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        myUserName = sharedPreferences.getString("myUserName", null);

        searchText = (TextInputEditText)findViewById(R.id.searchEditText);


    }


    private void searchFriends(String username){


        final Query userDbRef = dbRef.child("UserNames").orderByKey().equalTo(username);
        friendsDbRef = dbRef.child("Friends").child(myPhoneNumber);


        adapterFriends = new FirebaseListAdapter<BetBuddy>(activity, BetBuddy.class, R.layout.list_friends, userDbRef) {
            @Override
            protected void populateView(View v, final BetBuddy model, int position) {


                    TextView name = (TextView) v.findViewById(R.id.name);
                    CircleImageView profileImage = (CircleImageView) v.findViewById(R.id.profileImage);
                    final TextView addFriend = (TextView) v.findViewById(R.id.addFriend);

                    String phoneNumber = model.getPhoneNumber();
                    name.setText(model.getUserName());


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








/*


        userDbRef.child("UserNames").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChildren()){

                    for(DataSnapshot buddySnapShot: dataSnapshot.getChildren()){
                        BetBuddy betBuddy = dataSnapshot.getValue(BetBuddy.class);

                        searchResult.add(betBuddy);

                    }


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        */





    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(!isFinishing()){
            friendsDbRef.removeEventListener(listener);
        }

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            if (Objects.equals(searchText.getText().toString().trim(), "")){

            } else if(Objects.equals(searchText.getText().toString().trim(), myUserName)){

            }
            else {
                searchFriends(searchText.getText().toString().trim());
            }

            return true;
        }
        return super.dispatchKeyEvent(e);
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
