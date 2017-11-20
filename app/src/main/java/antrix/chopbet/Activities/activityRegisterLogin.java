package antrix.chopbet.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dx.dxloadingbutton.lib.LoadingButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

import antrix.chopbet.R;

public class activityRegisterLogin extends AppCompatActivity {

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    public boolean mVerificationInProgress = false;
    public String mVerificationId;
    public PhoneAuthProvider.ForceResendingToken mResendToken;
    public PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    LoadingButton btnSignIn;


    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_login);
        loadActionbar("Register / Login");
        getSupportActionBar().hide();


        declarations();



        if(mAuth.getCurrentUser() == null) {
            // Start sign in/sign up activity
            btnSignIn.reset();

        } else {
            // User is already signed in. Therefore, display
            // a welcome Toast

            //Verify Login Token
            Intent openLeapIntent = new Intent(context, activityChopBet.class);
            startActivity(openLeapIntent);
            Toast.makeText(context, "This works, already signed in", Toast.LENGTH_LONG).show();

            finish();



        }



        //assign Leap button


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                //Log.d(TAG, "onVerificationCompleted:" + credential);
                btnSignIn.loadingSuccessful();

                Toast.makeText(context, "Verification Successful", Toast.LENGTH_LONG).show();




                signInWithPhoneAuthCredential(credential);
                //btnSignIn.reset();


            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                //Log.w(TAG, "onVerificationFailed", e);

                btnSignIn.loadingFailed();
                btnSignIn.reset();





                Toast.makeText(context, "Account Problem, Contact Admin", Toast.LENGTH_LONG).show();


                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                    Toast.makeText(context, "Invalid phone number provided", Toast.LENGTH_LONG).show();
                    btnSignIn.reset();



                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                    Toast.makeText(context, "SMS quota for project has been exceeded", Toast.LENGTH_LONG).show();
                    btnSignIn.reset();


                }


                btnSignIn.reset();

                // Show a message and update the UI
                // ...

            }


            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                //Log.d(TAG, "onCodeSent:" + verificationId);

                //btnSignIn.loadingSuccessful();


                btnSignIn.reset();

                Toast.makeText(context, "Sent: Check for verification code", Toast.LENGTH_LONG).show();

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                //assign country code picker view from layout
                CountryCodePicker ccp = (CountryCodePicker) findViewById(R.id.ccp);

                //assign phoneNumber view from layout
                EditText phoneNumber = (EditText) findViewById(R.id.phoneNumber);


                //assign phone number to variable CarrierNumberEditText inside CountryCodePicker class.
                // More info here https://github.com/hbb20/CountryCodePickerProject
                ccp.registerCarrierNumberEditText(phoneNumber);


                // get phone number with country code and plus
                String phoneNumberAndCode = ccp.getFullNumber().toString();





                //Open Phone Verification screen (phoneVerifyActivity) to enter CODE
                Intent openPhoneVerifyIntent = new Intent(context, activityPhoneVerify.class);
                //send verification ID and phone number used via intent
                openPhoneVerifyIntent.putExtra("mVerificationId", mVerificationId);
                openPhoneVerifyIntent.putExtra("phoneNumber", phoneNumberAndCode);

                //countryCode is sent to the phoneVerifyActivity activity which is sent to Leap activity if verification is successful.
                openPhoneVerifyIntent.putExtra("countryCode", ccp.getSelectedCountryCode());

                startActivity(openPhoneVerifyIntent);
                btnSignIn.reset();

            }







        };





        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {







                //assign country code picker from layout
                CountryCodePicker ccp = (CountryCodePicker) findViewById(R.id.ccp);

                //assign phoneNumber from layout
                EditText phoneNumber = (EditText) findViewById(R.id.phoneNumber);

                if (TextUtils.isEmpty(phoneNumber.getText().toString())) {
                    //mVerificationField.setError("Cannot be empty.");
                    phoneNumber.setHint("Phone Number");
                    phoneNumber.setHintTextColor(getResources().getColor(R.color.colorAccent));
                    btnSignIn.cancelLoading();
                    btnSignIn.setEnabled(true);



                    return;
                }



                btnSignIn.startLoading(); //start loading
                btnSignIn.setEnabled(false);
                //spinner.setVisibility(View.VISIBLE);

                //remove "0" from number if any
                int firstDigit = Integer.parseInt((phoneNumber.getText().toString()).substring(0, 1));

                if (firstDigit < 1){
                    phoneNumber.setText(phoneNumber.getText().toString().trim().substring(1));
                }

                //assign phone number to variable CarrierNumberEditText inside CountryCodePicker class.
                // More info here https://github.com/hbb20/CountryCodePickerProject
                ccp.registerCarrierNumberEditText(phoneNumber);


                // get phone number with country code and plus
                String phoneNumberAndCode = ccp.getFullNumber().toString().trim();
                Toast.makeText(context, "Verifying +" + phoneNumberAndCode, Toast.LENGTH_LONG).show();


                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+" + phoneNumberAndCode,        // Phone number to verify
                        60,                 // Timeout duration
                        TimeUnit.SECONDS,   // Unit of timeout
                        activityRegisterLogin.this,  // Activity (for callback binding)
                        mCallbacks);        // OnVerificationStateChangedCallbacks


            }



        });

    }



    // Open Main Screen on successful login
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activityRegisterLogin.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {





                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();

                            CountryCodePicker ccp = (CountryCodePicker) findViewById(R.id.ccp);

                            //Open main interface for Leap while passing usr value
                            Intent openLeapIntent = new Intent(context, activityChopBet.class);
                            openLeapIntent.putExtra("countryCode", ccp.getSelectedCountryCode());
                            openLeapIntent.putExtra("countryCodeStatus", "1");


                            //btnSignIn.reset();


                            startActivity(openLeapIntent);
                            finish();
                            btnSignIn.reset();
                            btnSignIn.setEnabled(true);





                        } else {

                            btnSignIn.loadingFailed();
                            btnSignIn.reset();
                            btnSignIn.setEnabled(true);




                            // Sign in failed, display a message and update the UI
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(context, "Unexpected error, retry login", Toast.LENGTH_LONG).show();

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(context, "Invalid credential recorded", Toast.LENGTH_LONG).show();
                                btnSignIn.reset();
                                btnSignIn.setEnabled(true);



                            }
                        }
                    }
                });




        btnSignIn.reset();
        btnSignIn.setEnabled(true);


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




    private void declarations(){

        context = activityRegisterLogin.this;
        btnSignIn = (LoadingButton) findViewById(R.id.btnSignIn);


        //assign progress bar

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]



    }





}


