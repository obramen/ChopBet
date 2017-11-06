package antrix.chopbet.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import antrix.chopbet.BetClasses.BaseActivity;
import antrix.chopbet.R;
import de.hdodenhof.circleimageview.CircleImageView;


public class activityEditUsername extends BaseActivity {

    EditText userNameTextView;
    Button acceptButton;
    CircleImageView profileImage;

    Context context;

    String myPhoneNumber, myUID;
    FirebaseAuth mAuth;
    DatabaseReference dbRef;

    public String username;

    TextInputLayout textInputLayout;
    TextView hintTextView;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_username);

        declarations();
        clickers();
    }


    private void declarations(){

        context = this;

        dbRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        myPhoneNumber = mAuth.getCurrentUser().getPhoneNumber();
        myUID = mAuth.getCurrentUser().getUid();

        userNameTextView = (EditText)findViewById(R.id.userNameTextView);
        profileImage = (CircleImageView)findViewById(R.id.profileImage);
        acceptButton = (Button)findViewById(R.id.acceptButton);
        textInputLayout = (TextInputLayout)findViewById(R.id.textInputLayout);
        hintTextView = (TextView)findViewById(R.id.hintTextView);

        username = "";

        progressDialog = new ProgressDialog(context);




    }

    private void clickers(){

        acceptButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                hintTextView.setText("");

                if (TextUtils.isEmpty(userNameTextView.getText().toString())) {
                    textInputLayout.setHint("Enter username");
                    return;
                }

                progressDialog.setMessage("Verifying username...");

                username = userNameTextView.getText().toString();


                dbRef.child("usernames").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.child(username).exists()){

                            hintTextView.setText(username + " is not available");
                            progressDialog.dismiss();

                        } else {
                            dbRef.child("usernames").child(username).setValue(username);
                            dbRef.child("usernamesByPhone").child(myPhoneNumber).setValue(username);
                            progressDialog.dismiss();

                            Intent intent = new Intent(context, activityChopBet.class);
                            startActivity(intent);
                            finish();
                            Toast.makeText(context, "Username created successfully", Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });


    }

}
