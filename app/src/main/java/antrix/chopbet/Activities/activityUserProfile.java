package antrix.chopbet.Activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import antrix.chopbet.BetClasses.BaseActivity;
import antrix.chopbet.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class activityUserProfile extends BaseActivity{


    TextView name, psnTextView, originTextView, xboxLiveTextView, editProfileTextView;
    CircleImageView profileImage;

    View view;
    Context context;

    String myPhoneNumber, myUID;
    String phoneNumber, sourceActivity;
    FirebaseAuth mAuth;

    DatabaseReference dbRef;

    Bundle bundle;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);


        declarations();
        clickers();


        if (Objects.equals(phoneNumber, myPhoneNumber)){
            editProfileTextView.setVisibility(View.VISIBLE);
        }







    }


    private void declarations(){

        context = this;


        dbRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        myPhoneNumber = mAuth.getCurrentUser().getPhoneNumber();
        myUID = mAuth.getCurrentUser().getUid();


        name = (TextView)findViewById(R.id.name);
        psnTextView = (TextView)findViewById(R.id.psnTextView);
        originTextView = (TextView)findViewById(R.id.originTextView);
        xboxLiveTextView = (TextView)findViewById(R.id.xboxLiveTextView);
        profileImage = (CircleImageView)findViewById(R.id.profileImage);
        editProfileTextView = (TextView) findViewById(R.id.editProfileTextView);

        bundle = getIntent().getExtras();
        phoneNumber = bundle.getString("phoneNumber");


    }

    private void clickers(){

        editProfileTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /// 2 - for normal profile edit

                Intent intent = new Intent(context, activityEditUserProfile.class);
                intent.putExtra("phoneNumber", myPhoneNumber);
                intent.putExtra("sourceActivity", 2);
                startActivity(intent);
            }
        });




    }



}
