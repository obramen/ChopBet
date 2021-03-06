package antrix.chopbet.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.dx.dxloadingbutton.lib.LoadingButton;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import antrix.chopbet.BetClasses.BaseActivity;
import antrix.chopbet.BetClasses.BetUtilities;
import antrix.chopbet.BetClasses.ConfirmDialog;
import antrix.chopbet.Models.BetBuddy;
import antrix.chopbet.Models.NewCard;
import antrix.chopbet.Models.NewTransaction;
import antrix.chopbet.R;
import de.hdodenhof.circleimageview.CircleImageView;

import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.hubtel.payments.Class.Environment;
import com.hubtel.payments.Class.PaymentItem;
import com.hubtel.payments.Exception.HubtelPaymentException;
import com.hubtel.payments.Interfaces.OnPaymentResponse;
import com.hubtel.payments.HubtelCheckout;
import com.hubtel.payments.SessionConfiguration;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.Objects;

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

    RelativeLayout newCardLayout;
    RelativeLayout oldCardLayout;
    CheckBox saveCard;

    Bundle bundle;


    private static final String TAG = "TAG";

    Handler mHandler;


    ConfirmDialog confirmDialog;

    String regenRune, invisRune, bountyRune;

    TextInputEditText phoneNumberEditText;
    TextView phoneNumberTextView;

    CircleImageView profileImage;
    CircleImageView profileImage1;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_funds);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadActionbar("Add Funds");
        getSupportActionBar().setElevation(0);

        declarations();
        clickers();









    }

    private void declarations(){


        activity = activityAddFunds.this;
        context = this;

        dbRef = FirebaseDatabase.getInstance().getReference();

        fireDbRef = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        myPhoneNumber = mAuth.getCurrentUser().getPhoneNumber();
        myUID = mAuth.getCurrentUser().getUid();

        mHandler = new Handler();


        listView = (ListView)findViewById(R.id.list_Friends);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        usernameTextView = (TextView)findViewById(R.id.userNameTextView);
        amountEditText = (EditText)findViewById(R.id.amountEditText);

        newCardLayout = (RelativeLayout)findViewById(R.id.newCardLayout);
        oldCardLayout = (RelativeLayout)findViewById(R.id.oldCardLayout);
        saveCard = (CheckBox)findViewById(R.id.saveCard);
        btnNext = (LoadingButton)findViewById(R.id.btnNext);
        phoneNumberEditText = (TextInputEditText)findViewById(R.id.phoneNumberEditText);
        phoneNumberTextView = (TextView) findViewById(R.id.phoneNumberTextView);

        profileImage = (CircleImageView)findViewById(R.id.profileImage);
        profileImage1 = (CircleImageView)findViewById(R.id.profileImage1);


        myUserName = sharedPreferences.getString("myUserName", null);
        usernameTextView.setText(myUserName);
        bundle = getIntent().getExtras();

        confirmDialog = new ConfirmDialog();

        dbRef.child("Xplosion").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                regenRune = dataSnapshot.child("regenRune").getValue().toString();
                invisRune = dataSnapshot.child("invisRune").getValue().toString();
                bountyRune = dataSnapshot.child("bountyRune").getValue().toString();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        String Channel = bundle.getString("channel");

        switch (Channel){

            case "mtn-gh":
                profileImage.setImageResource(R.drawable.mtn);
                break;

            case "vodafone-gh":
                profileImage.setImageResource(R.drawable.vodafone);
                break;

            case "tigo-gh":
                profileImage.setImageResource(R.drawable.tigo);
                break;

            case "airtel-gh":
                profileImage.setImageResource(R.drawable.airtel);
                break;

        }

         switch (Channel){

                    case "mtn-gh":
                        profileImage1.setImageResource(R.drawable.mtn);
                        break;

                    case "vodafone-gh":
                        profileImage1.setImageResource(R.drawable.vodafone);
                        break;

                    case "tigo-gh":
                        profileImage1.setImageResource(R.drawable.tigo);
                        break;

                    case "airtel-gh":
                        profileImage1.setImageResource(R.drawable.airtel);
                        break;

                }



        switch (bundle.getString("cardStatus")){
            case "New Card":
                newCardLayout.setVisibility(View.VISIBLE);
                oldCardLayout.setVisibility(View.GONE);

                break;
            case "Saved Card":
                newCardLayout.setVisibility(View.GONE);
                oldCardLayout.setVisibility(View.VISIBLE);
                phoneNumberTextView.setText(bundle.getString("phoneNumber"));

                break;
        }







    }

    private void clickers(){

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Objects.equals(amountEditText.getText().toString().trim(), "")){
                    Toast.makeText(context, "Enter amount", Toast.LENGTH_LONG).show();
                    return;
                }



                if (Integer.parseInt(amountEditText.getText().toString().trim()) > 500){
                    Toast.makeText(context, "Maximum allowed amount: GHS 500", Toast.LENGTH_LONG).show();
                    return;
                }



                //addMoney();
                amountEditText.setEnabled(false);
                btnNext.setEnabled(false);

                hubtelConnect();

            }
        });
    }






    private void hubtelConnect(){

        if(TextUtils.isEmpty(amountEditText.getText().toString().trim())){
            Toast.makeText(context, "Enter amount", Toast.LENGTH_SHORT).show();
        }

        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Processing payment...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        final String goldKey = bundle.getString("string");
        final String Channel = bundle.getString("channel");
        String PhoneNumber = "";

        switch (bundle.getString(getString(R.string.cardstatus))){
            case "New Card":
                PhoneNumber = phoneNumberEditText.getText().toString();
                if(TextUtils.isEmpty(phoneNumberEditText.getText().toString().trim())  || phoneNumberEditText.getText().toString().trim().length() < 9){
                    Toast.makeText(context, "Enter phone number", Toast.LENGTH_SHORT).show();
                    return;
                }

                break;
            case "Saved Card":
                PhoneNumber = bundle.getString("phoneNumber");
                break;
        }

        if(!Objects.equals(PhoneNumber.substring(0, 1), "0")){
            PhoneNumber = "0" + PhoneNumber;
        }

        final String key = dbRef.child("Xperience").child(goldKey).child("Injection").push().getKey();

        //http://requestb.in/1minotz1
        //myPhoneNumber.substring(4)

        final String amount = amountEditText.getText().toString();
        final String merchant = "Hubtel";

        final JsonObject json = new JsonObject();
        json.addProperty("CustomerName", myUserName);
        json.addProperty("CustomerMsisdn", PhoneNumber);
        json.addProperty("CustomerEmail", "");
        json.addProperty("Channel", Channel);
        json.addProperty("Amount", amount);
        json.addProperty("PrimaryCallbackUrl", "http://antrixgaming.com");
        json.addProperty("SecondaryCallbackUrl", "http://antrixgaming.com");
        json.addProperty("Description", "Chop-Bet Wallet Topup");
        json.addProperty("ClientReference", "Chop-Bet");
        json.addProperty("Token", "string");
        json.addProperty("FeesOnCustomer", true);

        final String finalPhoneNumber = PhoneNumber;
        Ion.with(context)
                .load(bountyRune)
                .setHeader("Authorization",regenRune)
                //.setHeader("Host", "api.hubtel.com")
                //.setHeader("Accept", "application/json")
                .setHeader("Content-Type", "application/json")
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {

                        if (result == null || result.get("ResponseCode").getAsInt() == 4101){
                            progressDialog.dismiss();
                            //Snackbar.make(v, "Payment failed, check your connection...", Snackbar.LENGTH_LONG).show();
                            Toast.makeText(context, "Payment failed, check your connection...", Toast.LENGTH_SHORT).show();
                            amountEditText.setEnabled(true);
                            btnNext.setEnabled(true);
                        } else {
                            try {

                                Toast.makeText(context, result.toString(), Toast.LENGTH_SHORT).show();

                                JsonObject newTResult = result;    // Converts the string "result" to a JSONObject
                                String responseCode = newTResult.get("ResponseCode").getAsString();


                                JsonObject newTData = newTResult.get("Data").getAsJsonObject();

                                final String transactionId = newTData.get("TransactionId").getAsString();

                                String charges = newTData.get("Charges").getAsString();

                                dbRef.child("Xperience").child(goldKey).child("Injection").child(key)
                                        .setValue(new NewTransaction(key, "DEPOSIT", amount, charges, merchant, transactionId, finalPhoneNumber, "New Transaction", Channel));


                                if (saveCard.isChecked()){


                                    dbRef.child("Xperience").child(goldKey).child("Cards").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChildren()){
                                                if (dataSnapshot.child(finalPhoneNumber).exists()){

                                                } else{
                                                    //String cardID = dbRef.child("Xperience").child(goldKey).child("Cards").push().getKey();
                                                    dbRef.child("Xperience").child(goldKey).child("Cards").child(finalPhoneNumber).setValue(new NewCard(finalPhoneNumber, Channel, finalPhoneNumber));

                                                }

                                            } else{
                                                dbRef.child("Xperience").child(goldKey).child("Cards").child(finalPhoneNumber).setValue(new NewCard(finalPhoneNumber, Channel, finalPhoneNumber));
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });


                                }

                                if (Objects.equals(responseCode, "0001")){

                                    mHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {

                                            //String TransactionId = result.get("TransactionId").getAsString();
                                            Ion.with(getApplicationContext())
                                                    .load(invisRune + transactionId)
                                                    .setHeader("Authorization",regenRune)
                                                    //.setHeader("Host", "api.hubtel.com")
                                                    //.setHeader("Accept", "application/json")
                                                    .setHeader("Content-Type", "application/json")
                                                    .asJsonObject()
                                                    .setCallback(new FutureCallback<JsonObject>() {
                                                        @Override
                                                        public void onCompleted(Exception e, JsonObject result2) {
                                                            String finalResponseCode = result2.get("ResponseCode").getAsString();

                                                            if (Objects.equals(finalResponseCode, "0000")){

                                                                JsonArray data = result2.get("Data").getAsJsonArray();



                                                                JsonObject info = data.get(0).getAsJsonObject();
                                                                String status = info.get("TransactionStatus").getAsString();
                                                                // do stuff with the result or error

                                                                String fees = info.get("Fee").getAsString();

                                                                dbRef.child("Xperience").child(goldKey).child("Injection").child(key)
                                                                        .setValue(new NewTransaction(key, "DEPOSIT", amount, fees, merchant, transactionId, finalPhoneNumber, status, Channel));



                                                                Toast.makeText(context, "Payment "+ status, Toast.LENGTH_LONG).show();
                                                                progressDialog.dismiss();

                                                                finish();

                                                            }

                                                        }
                                                    });



                                        }
                                    },500);


                                }

                                else{
                                    Toast.makeText(context, "Error occurred, please try again", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }


                            } catch (JsonIOException f){
                                // This method will run if something goes wrong with the json, like a typo to the json-key or a broken JSON.
                                progressDialog.dismiss();
                                Log.e(TAG, f.getMessage());
                                Toast.makeText(getApplicationContext(), "Please check your internet connection.", Toast.LENGTH_LONG).show();
                                amountEditText.setEnabled(true);
                                btnNext.setEnabled(true);
                            }
                        }




                    }

                });


        /*
        confirmDialog.NewConfirmDialog(context, "Confirm Deposit", "GHS " + amount +"\n"+Channel + "\n0" + myPhoneNumber.substring(4) , "Next", "Cancel");
        confirmDialog.confirmAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {







            }
        });
        //progressDialog.dismiss();


        //Ion.getDefault(getApplicationContext()).cancelAll(json1);
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
