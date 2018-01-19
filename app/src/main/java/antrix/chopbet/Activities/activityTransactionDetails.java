package antrix.chopbet.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import antrix.chopbet.BetClasses.BaseActivity;
import antrix.chopbet.Models.NewMatch;
import antrix.chopbet.Models.NewTransaction;
import antrix.chopbet.R;

public class activityTransactionDetails extends BaseActivity {



    String myPhoneNumber;
    FirebaseAuth mAuth;
    FirebaseFirestore fireDbRef;
    DatabaseReference dbRef;

    ListView listView;

    Context context;
    View view;
    View myView;

    String myUserName;
    SharedPreferences sharedPreferences;

    FirebaseListAdapter<NewTransaction> adapter;


    Activity activity;

    Bundle bundle;


    ValueEventListener listener;
    DatabaseReference matchDetailsDbRef;

    String transactionID;

    Query query;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadActionbar("Transaction Details");
        getSupportActionBar().setElevation(0);

        declarations();

        loadHistory();


    }


    private void declarations(){


        activity = activityTransactionDetails.this;
        context = this;

        dbRef = FirebaseDatabase.getInstance().getReference();

        fireDbRef = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        myPhoneNumber = mAuth.getCurrentUser().getPhoneNumber();


        listView = (ListView)findViewById(R.id.list_TransactionDetails);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        myUserName = sharedPreferences.getString("myUserName", null);

        bundle = getIntent().getExtras();
        transactionID = bundle.getString("transactionID");
        String goldKey = bundle.getString("string");

        query = dbRef.child("Xperience").child(goldKey).child("Injection").orderByChild("transactionID").equalTo(transactionID);


    }


    private void loadHistory(){





        adapter = new FirebaseListAdapter<NewTransaction>(activity, NewTransaction.class, R.layout.list_transaction_details, query) {
            @Override
            protected void populateView(View v, final NewTransaction model, int position) {

                TextView date = (TextView)v.findViewById(R.id.date);
                TextView transactionType = (TextView)v.findViewById(R.id.transactionType);
                TextView transactionID = (TextView)v.findViewById(R.id.transactionID);
                TextView bottomAmount = (TextView)v.findViewById(R.id.bottomAmount);
                TextView topAmount = (TextView)v.findViewById(R.id.topAmount);
                TextView fee = (TextView)v.findViewById(R.id.fee);
                TextView transactionResult = (TextView)v.findViewById(R.id.transactionResult);
                TextView phoneNumber = (TextView)v.findViewById(R.id.phoneNumber);


                transactionID.setText(model.getTransactionID());
                transactionType.setText(model.getTransactionType());

                bottomAmount.setText("GHS " + model.getAmount());
                topAmount.setText("GHS " + model.getAmount());

                phoneNumber.setText(model.getPhoneNumber());


                transactionResult.setText(model.getResult());

                fee.setText(model.getFee());




                if(DateUtils.isToday(model.getDate())){
                    date.setText("Today");
                } else {
                    date.setText(DateFormat.format(getString(R.string.dateformat), model.getDate()));
                }


                switch (model.getResult()){

                    case "Success":
                        transactionResult.setTextColor(getResources().getColor(R.color.green));

                        break;

                    case "Failed":
                        transactionResult.setTextColor(getResources().getColor(R.color.colorPrimary));
                        break;
                    case "New Transaction":
                        transactionResult.setTextColor(getResources().getColor(R.color.colorGameFIFA));
                        break;
                    default:
                        transactionResult.setTextColor(getResources().getColor(R.color.black));


                }






            }


        };

        listView.setAdapter(adapter);










    }







    private void loadActionbar(String title){

        final ActionBar abar = getSupportActionBar();
        // abar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_background));//line under the action bar
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

    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }



}
