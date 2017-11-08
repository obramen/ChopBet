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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import antrix.chopbet.BetClasses.BaseActivity;
import antrix.chopbet.Models.BetBuddy;
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
    FirebaseFirestore fireDbRef;

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
        fireDbRef = FirebaseFirestore.getInstance();
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

                userNameTextView.setEnabled(false);
                acceptButton.setEnabled(false);

                progressDialog.setMessage("Verifying username...");
                progressDialog.show();

                username = userNameTextView.getText().toString();


                fireDbRef.document("Users/UserNames").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        if (documentSnapshot.exists()){
                            if (documentSnapshot.contains(username)){
                                hintTextView.setText(username + " is not available");
                                progressDialog.dismiss();

                                userNameTextView.setEnabled(true);
                                acceptButton.setEnabled(true);
                            } else {
                                saveUserName();
                                progressDialog.dismiss();

                            }

                        }

                        else{

                            saveUserName();
                            progressDialog.dismiss();


                        }



                    }
                });



            }
        });


    }

    public void saveUserName(){
        Map<String, Object> mUserName = new HashMap<>();
        Map<String, Object> xUserName = new HashMap<>();
        mUserName.put(username, myPhoneNumber);
        xUserName.put("userName", username);

        BetBuddy betBuddy = new BetBuddy(username, "Add Friend");

        fireDbRef.document("Users/UserNames").set(mUserName, SetOptions.merge());
        fireDbRef.collection("UserNames").add(betBuddy);
        fireDbRef.document("Users/UserInfo/PhoneNumber/"+myPhoneNumber).set(xUserName, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Intent intent = new Intent(context, activityChopBet.class);
                intent.putExtra("countryCodeStatus", "0");
                intent.putExtra("countryCode", "0");
                startActivity(intent);
                finish();
                Toast.makeText(context, "Username created successfully", Toast.LENGTH_LONG).show();
            }
        });
    }

}
