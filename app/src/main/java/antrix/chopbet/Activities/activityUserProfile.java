package antrix.chopbet.Activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import antrix.chopbet.BetClasses.BaseActivity;
import antrix.chopbet.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class activityUserProfile extends BaseActivity{


    TextView name, psnTextView, originTextView, xboxLiveTextView, editProfileTextView;
    CircleImageView profileImage;

    View view;
    Context context;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);


        declarations();
        clickers();






    }


    private void declarations(){

        context = this;

        name = (TextView)findViewById(R.id.name);
        psnTextView = (TextView)findViewById(R.id.psnTextView);
        originTextView = (TextView)findViewById(R.id.originTextView);
        xboxLiveTextView = (TextView)findViewById(R.id.xboxLiveTextView);
        profileImage = (CircleImageView)findViewById(R.id.profileImage);
        editProfileTextView = (TextView) findViewById(R.id.editProfileTextView);


    }

    private void clickers(){

        editProfileTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, activityEditUserProfile.class);
                startActivity(intent);
            }
        });




    }



}
