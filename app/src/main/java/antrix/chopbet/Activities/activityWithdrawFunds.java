package antrix.chopbet.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.dx.dxloadingbutton.lib.LoadingButton;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import antrix.chopbet.Models.BetBuddy;
import antrix.chopbet.R;

public class activityWithdrawFunds extends AppCompatActivity {




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

    TextView usernameTextView;
    LoadingButton btnNext;
    EditText amountEditText;

    Bundle bundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_funds);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadActionbar("Withdraw Funds");
        getSupportActionBar().setElevation(0);

        declarations();




    }
    private void declarations(){


        activity = activityWithdrawFunds.this;
        context = this;

        dbRef = FirebaseDatabase.getInstance().getReference();

        fireDbRef = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        myPhoneNumber = mAuth.getCurrentUser().getPhoneNumber();
        myUID = mAuth.getCurrentUser().getUid();


        listView = (ListView)findViewById(R.id.list_Friends);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        usernameTextView = (TextView)findViewById(R.id.userNameTextView);
        amountEditText = (EditText)findViewById(R.id.amountEditText);

        btnNext = (LoadingButton)findViewById(R.id.btnNext);


        myUserName = sharedPreferences.getString("myUserName", null);
        usernameTextView.setText(myUserName);
        bundle = getIntent().getExtras();





    }


    private void clickers(){

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                withdrawMoney();

            }
        });
    }




    private void withdrawMoney(){






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
