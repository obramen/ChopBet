package antrix.chopbet.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import antrix.chopbet.BetClasses.BaseActivity;
import antrix.chopbet.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class activityEditUserProfile extends BaseActivity {

    EditText nameTextView, psnTextView, originTextView, xboxLiveTextView;
    CircleImageView profileImage;

    Context context;

    String myPhoneNumber, myUID;
    String phoneNumber;
    FirebaseAuth mAuth;
    DatabaseReference dbRef;

    Bundle bundle;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_user_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        declarations();
        clickers();






    }


    private void declarations(){

        context = this;

        dbRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        myPhoneNumber = mAuth.getCurrentUser().getPhoneNumber();
        myUID = mAuth.getCurrentUser().getUid();

        nameTextView = (EditText)findViewById(R.id.nameTextView);
        psnTextView = (EditText)findViewById(R.id.psnTextView);
        originTextView = (EditText)findViewById(R.id.originTextView);
        xboxLiveTextView = (EditText)findViewById(R.id.xboxLiveTextView);
        profileImage = (CircleImageView)findViewById(R.id.profileImage);


        // get bundled country code from registerLogin activity
        bundle = getIntent().getExtras();
        phoneNumber = bundle.getString("phoneNumber");


    }

    private void clickers(){


    }



}

