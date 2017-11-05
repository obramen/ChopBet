package antrix.chopbet.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import antrix.chopbet.BetClasses.BaseActivity;
import antrix.chopbet.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class activityEditUserProfile extends BaseActivity {

    EditText nameTextView, psnTextView, originTextView, xboxLiveTextView;
    CircleImageView profileImage;

    Context context;


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

        nameTextView = (EditText)findViewById(R.id.nameTextView);
        psnTextView = (EditText)findViewById(R.id.psnTextView);
        originTextView = (EditText)findViewById(R.id.originTextView);
        xboxLiveTextView = (EditText)findViewById(R.id.xboxLiveTextView);
        profileImage = (CircleImageView)findViewById(R.id.profileImage);


    }

    private void clickers(){


    }



}

