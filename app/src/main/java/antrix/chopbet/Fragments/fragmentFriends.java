package antrix.chopbet.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import antrix.chopbet.Activities.activityAddFriend;
import antrix.chopbet.Activities.activityFriendRequests;
import antrix.chopbet.Activities.activityUserProfile;
import antrix.chopbet.BetClasses.BetUtilities;
import antrix.chopbet.Models.BetBuddy;
import antrix.chopbet.Models.NewMatch;
import antrix.chopbet.R;
import de.hdodenhof.circleimageview.CircleImageView;


public class fragmentFriends extends Fragment {

    String myPhoneNumber;
    String myUID;
    FirebaseAuth mAuth;
    DatabaseReference dbRef;
    FirebaseFirestore fireDbRef;

    FirebaseListAdapter<BetBuddy> adapterFriends;
    FirebaseRecyclerAdapter adapterFavourites;
    ListView listView;
    RecyclerView recyclerView;

    Context context;
    View view;
    View myView;

    Query queryFriends;
    Query queryFavourites;
    Activity activity;

    LinearLayout favouritesLayout;

    TextView viewAll, addFriend, friendReqeust;

    ValueEventListener listener;
    DatabaseReference friendsDbRef;
    BetUtilities betUtilities;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_friends, container, false);




        declarations();
        listFriends();
        listFavouriteFirends();
        clickers();
        notificationIcon();






        return view;
    }



    private void notificationIcon(){




        Query userDBRef = dbRef.child("Friends").child(myPhoneNumber).orderByChild("status").equalTo("Pending");


        listener = userDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                long count = dataSnapshot.getChildrenCount();
                if (count >= 1){
                    friendReqeust.setText("[" + count + "]" + " Requests");
                    friendReqeust.setTextColor(getResources().getColor(R.color.colorPrimary));
                } else {
                    friendReqeust.setText("Requests");
                    friendReqeust.setTextColor(getResources().getColor(R.color.black));
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }




    private void clickers() {

        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, activityAddFriend.class);
                startActivity(intent);
            }
        });

        friendReqeust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, activityFriendRequests.class);
                startActivity(intent);
            }
        });



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView name = (TextView)view.findViewById(R.id.name);
                Intent intent = new Intent(context, activityUserProfile.class);
                intent.putExtra("userName", name.getText().toString());
                context.startActivity(intent);
            }

        });
    }


    private void listFriends(){
        adapterFriends = new FirebaseListAdapter<BetBuddy>(activity, BetBuddy.class, R.layout.list_friends, queryFriends) {
            @Override
            protected void populateView(View v, BetBuddy model, int position) {

                TextView name = (TextView)v.findViewById(R.id.name);
                CircleImageView profileImage = (CircleImageView)v.findViewById(R.id.profileImage);

                name.setText(model.getUserName());





            }
        };

        listView.setAdapter(adapterFriends);
    }



    private void listFavouriteFirends(){
        adapterFavourites = new FirebaseRecyclerAdapter<BetBuddy, favouritesViewHolder>(BetBuddy.class, R.layout.list_favourite_friends,
                favouritesViewHolder.class, queryFavourites) {
            @Override
            protected void populateViewHolder(favouritesViewHolder viewHolder, BetBuddy model, int position) {

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);

                recyclerView = (RecyclerView)view.findViewById(R.id.list_FavouriteFriends);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(linearLayoutManager);
                linearLayoutManager.setOrientation(linearLayoutManager.HORIZONTAL);



                viewHolder.setFavouriteName(model.getUserName());



            }


        };

        recyclerView.setAdapter(adapterFavourites);


    }




    public static class favouritesViewHolder extends RecyclerView.ViewHolder {


        CircleImageView profileImage;
        TextView name;


        public favouritesViewHolder(View itemView) {
            super(itemView);

            name = (TextView)itemView.findViewById(R.id.name);

        }

        public void setFavouriteName(String favouriteName) {
            this.name.setText(favouriteName);
        }
    }





    private void declarations(){

        activity = getActivity();
        context = getActivity();
        myView = view;

        dbRef = FirebaseDatabase.getInstance().getReference();
        fireDbRef = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        myPhoneNumber = mAuth.getCurrentUser().getPhoneNumber();
        myUID = mAuth.getCurrentUser().getUid();

        viewAll = (TextView)myView.findViewById(R.id.viewAll);
        addFriend = (TextView)myView.findViewById(R.id.addFriend);
        friendReqeust = (TextView)myView.findViewById(R.id.friendRequest);

        listView = (ListView)myView.findViewById(R.id.list_Friends);
        recyclerView = (RecyclerView) myView.findViewById(R.id.list_FavouriteFriends);

        favouritesLayout = (LinearLayout)myView.findViewById(R.id.favouritesLayout);
        betUtilities = new BetUtilities();



        if (dbRef.child("friends").child(myPhoneNumber) != null){
            queryFriends = dbRef.child("Friends").child(myPhoneNumber).orderByChild("status").equalTo("Friend");
            queryFavourites = dbRef.child("Friends").child(myPhoneNumber).orderByChild("favourite").equalTo("true").limitToFirst(5);
            if (queryFavourites == null){
                favouritesLayout.setVisibility(View.VISIBLE);
            }

        }




    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if(!isDetached()){
            dbRef.child("Friends").child(myPhoneNumber).orderByChild("status").equalTo("Pending").removeEventListener(listener);
        }


    }




}
