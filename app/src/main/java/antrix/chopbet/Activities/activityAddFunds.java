package antrix.chopbet.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dx.dxloadingbutton.lib.LoadingButton;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import antrix.chopbet.BetClasses.BaseActivity;
import antrix.chopbet.Models.BetBuddy;
import antrix.chopbet.Models.NewTransaction;
import antrix.chopbet.R;

import com.hubtel.payments.Class.Environment;
import com.hubtel.payments.Class.PaymentItem;
import com.hubtel.payments.Exception.HubtelPaymentException;
import com.hubtel.payments.Interfaces.OnPaymentResponse;
import com.hubtel.payments.HubtelCheckout;
import com.hubtel.payments.SessionConfiguration;

public class activityAddFunds extends BaseActivity {



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
        setContentView(R.layout.activity_add_funds);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadActionbar("Add Funds");
        getSupportActionBar().setElevation(0);

        declarations();
        clickers();


        Selection.setSelection(amountEditText.getText(), amountEditText.getText().length());
        usernameTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().startsWith("GHS")){
                    amountEditText.setText("GHS");
                    Selection.setSelection(amountEditText.getText(), amountEditText.getText().length());

                }
            }
        });








    }

    private void declarations(){


        activity = activityAddFunds.this;
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

                addMoney();

            }
        });
    }


    private void addMoney(){

        final String goldKey = bundle.getString("string");
        //Toast.makeText(context, goldKey, Toast.LENGTH_SHORT).show();




        final String transactionAmount = amountEditText.getText().toString().trim();
        //final String transactionAmount = "0.2";

        PaymentItem paymentItem = new PaymentItem();
        paymentItem.description = "Wallet top-up";
        paymentItem.total_p = transactionAmount;
        paymentItem.name = "BET Cash";
        paymentItem.unit_p = transactionAmount;
        paymentItem.qty = "1";
        //paymentItem.notify();

        try {
            SessionConfiguration sessionConfiguration = new SessionConfiguration()
                    .Builder().setClientId("yvxesfkc")
                    .setSecretKey("bhvfxklk")
                    .setEnvironment(Environment.LIVE_MODE)
                    .build();
            HubtelCheckout hubtelPayments = new HubtelCheckout(sessionConfiguration);
            hubtelPayments.addPaymentItem(paymentItem);
            hubtelPayments.setPaymentDetails(0.2,
                    "Account Top-up");
            hubtelPayments.Pay(activity);
            hubtelPayments.setOnPaymentCallback(new OnPaymentResponse() {
                @Override
                public void onFailed(String token, String reason) {
                    String key = dbRef.child("Xperience").child(goldKey).child("Injection").push().getKey();
                    dbRef.child("Xperience").child(goldKey).child("Injection").child(key)
                            .setValue(new NewTransaction(key, transactionAmount, "0.00", "Hubtel", token, "Deposit", "FAILED"));
                    Toast.makeText(context, "Deposit Failed: " + reason, Toast.LENGTH_LONG).show();
                    finish();
                }

                @Override
                public void onCancelled() {
                    Toast.makeText(context, "Transaction cancelled", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSuccessful(String token) {
                    String key = dbRef.child("Xperience").child(goldKey).child("Injection").push().getKey();
                    dbRef.child("Xperience").child(goldKey).child("Injection").child(key)
                            .setValue(new NewTransaction(key, transactionAmount, "0.00",  "Hubtel", token, "Deposit", "SUCCESS"));
                    Toast.makeText(context, "Deposit Successful", Toast.LENGTH_LONG).show();
                    finish();
                }
            });
        }
        catch (HubtelPaymentException e) {
            e.printStackTrace();
        }


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
