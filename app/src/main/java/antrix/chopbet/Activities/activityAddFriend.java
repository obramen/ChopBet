package antrix.chopbet.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
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

public class activityAddFriend extends BaseActivity{

    BetUtilities betUtilities;


    String myPhoneNumber;
    String myUID;
    FirebaseAuth mAuth;
    FirebaseFirestore fireDbRef;

    FirebaseListAdapter<BetBuddy> adapterFriends;
    ListView listView;

    Context context;
    View view;
    View myView;

    String myUserName;
    SharedPreferences sharedPreferences;


    Query queryFriends;
    Activity activity;

    ActionBar actionBar;
    Map<String, ArrayList<String>> retrievedFriends;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadActionbar("Add Friend");



        declarations();
        listFriends();

    }

    private void declarations(){


        activity = activityAddFriend.this;
        context = this;

        fireDbRef = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        myPhoneNumber = mAuth.getCurrentUser().getPhoneNumber();
        myUID = mAuth.getCurrentUser().getUid();


        listView = (ListView)findViewById(R.id.list_Friends);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        myUserName = sharedPreferences.getString("myUserName", null);


    }




    private void listFriends(){

        queryFriends = fireDbRef.collection("UserNames").orderBy("userName", Query.Direction.ASCENDING);
        queryFriends.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(final QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if(documentSnapshots.isEmpty()){
                    //....

                }else {
                    final ArrayList<BetBuddy> arrayList = new ArrayList<BetBuddy>();


                    //BetBuddy betBuddy = dSnap.toObject(BetBuddy.class);

                    for(int x = 0; x<documentSnapshots.size(); x++){

                        DocumentSnapshot dSnap = documentSnapshots.getDocuments().get(x);
                        final BetBuddy betBuddy = dSnap.toObject(BetBuddy.class);


                        fireDbRef.collection("Friends/"+myUserName+"/All/").addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(QuerySnapshot documentSnapshotsB, FirebaseFirestoreException e) {


                                if (documentSnapshotsB.isEmpty()
                                        ){

                                    if (Objects.equals(betBuddy.getUserName(), myUserName)) {
                                        //....
                                    } else {
                                        arrayList.add(betBuddy);

                                    }

                                } else {


                                    if (Objects.equals(betBuddy.getUserName(), myUserName)) {
                                        //....
                                    } else {

                                        for (int y = 0; y < documentSnapshotsB.size(); y++){


                                            BetBuddy xbuddy = documentSnapshotsB.getDocuments().get(y).toObject(BetBuddy.class);
                                            if (Objects.equals(betBuddy.getUserName(), xbuddy.getUserName())){
                                                Toast.makeText(activityAddFriend.this, "TOASTED", Toast.LENGTH_SHORT).show();


                                                String mmUserName = xbuddy.getUserName();

                                                fireDbRef.document("Friends/" + myUserName + "/All/" + mmUserName)
                                                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                                                        arrayList.add(documentSnapshot.toObject(BetBuddy.class));

                                                    }
                                                });



                                            }


                                        }

                                    }




                                }






                            }
                        });

                        //listenerA.remove();




                    }


                    ///get list of current friends




                    //arrayList.add(betBuddy);
                    AdapterBetBuddy adapterFriends = new AdapterBetBuddy(arrayList, context){
                        @Override
                        public void onClick(View v) {
                            super.onClick(v);

                            int position=(Integer) v.getTag();
                            Object object = getItem(position);
                            BetBuddy mBetBuddy = (BetBuddy) object;

                            switch (v.getId())
                            {
                                case R.id.addFriend:
                                    //Snackbar.make(v, "UserName is " + betBuddy.getUserName(), Snackbar.LENGTH_LONG)
                                    //      .setAction("No action", null).show();
                                    Toast.makeText(context, "Request sent", Toast.LENGTH_SHORT).show();

                                    BetBuddy xBetBuddy = new BetBuddy(mBetBuddy.getUserName(), "pending", "false");
                                    BetBuddy yBetBuddy = new BetBuddy(myUserName, "received", "false");

                                    TextView view = (TextView)v.findViewById(R.id.addFriend);
                                    view.setEnabled(false);
                                    view.setText("Sent");
                                    view.setTextColor(getResources().getColor(R.color.selector));


                                    fireDbRef.document("Friends/"+myUserName+"/All/"+mBetBuddy.getUserName()).set(xBetBuddy, SetOptions.merge());
                                    fireDbRef.document("Friends/"+mBetBuddy.getUserName()+"/All/"+myUserName).set(yBetBuddy, SetOptions.merge());
                                    break;
                            }
                        }

                    };

                    listView.setAdapter(adapterFriends);

                }

            }
        });





        /*

        fireDbRef.document("Users/Collections/UserNames").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if(documentSnapshot.exists()) {

                    ArrayList<BetBuddy> arrayList = new ArrayList<BetBuddy>();


                    //retrievedFriends.put(arrayList, documentSnapshots.getQuery());
                    BetBuddy betBuddy = documentSnapshot.toObject(BetBuddy.class);


                    arrayList.add(betBuddy);



                    AdapterBetBuddy adapterFriends = new AdapterBetBuddy(arrayList, context);

                    listView.setAdapter(adapterFriends);

                }




            }
        });


*/


        /*

        adapterFriends = new FirebaseListAdapter<BetBuddy>(activity, BetBuddy.class, R.layout.list_friends, (com.google.firebase.database.Query) retrievedFriends) {
            @Override
            protected void populateView(View v, BetBuddy model, int position) {

                TextView name = (TextView)v.findViewById(R.id.name);
                TextView addFriend = (TextView)v.findViewById(R.id.addFriend);

                CircleImageView profileImage = (CircleImageView)v.findViewById(R.id.profileImage);

                name.setText(model.getUserName());





            }
        };

        listView.setAdapter(adapterFriends);

        */



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
