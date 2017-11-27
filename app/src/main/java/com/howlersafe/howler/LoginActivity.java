package com.howlersafe.howler;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ForgotPasswordContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.ForgotPasswordHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.VerificationHandler;
import com.amazonaws.services.cognitoidentityprovider.model.InvalidParameterException;
import com.amazonaws.services.cognitoidentityprovider.model.LimitExceededException;
import com.amazonaws.services.cognitoidentityprovider.model.NotAuthorizedException;
import com.amazonaws.services.cognitoidentityprovider.model.UserNotConfirmedException;
import com.amazonaws.services.cognitoidentityprovider.model.UserNotFoundException;
import com.amazonaws.services.cognitoidentityprovider.model.UsernameExistsException;
import com.howlersafe.howler.R;
import com.howlersafe.howler.aws.AWSHelper;


public class LoginActivity extends AppCompatActivity implements OnClickListener {

    public CustomPagerAdapter mCustomPagerAdapter;
    public ViewPager mViewPager;

    int[] mResources = {
            R.drawable.first,
            R.drawable.second,
            R.drawable.third
    };

    // control variables
    private AWSHelper mAWSHelper;
    private CognitoUser mCognitoUser;
    // controls for AWS forgot password
    private ForgotPasswordContinuation mForgotPasswordContinuation;
    private ForgotPasswordHandler mForgotPasswordHandler;
    // controls for AWS user sign in
    String mSignInUser, mSignInPassword;
    private AuthenticationHandler mAuthenticationHandler;

    // UI variables
//    Button signInWithFacebookButton;

    //   SignIn
    LinearLayout signInInputsLinearLayout;
    EditText signInUserEditText, signInPasswordEditText;
    RelativeLayout signInButtonsRelativeLayout;
    Button forgotPasswordButton, signUpModeButton, signInButton;
    ImageView rightScrollArrow, leftScrollArrow;

    //   SignUp
    ScrollView signUpScrollView;
    LinearLayout signUpInputsLinearLayout;
    EditText signUpUserEditText, signUpEmailEditText, signUpFullNameEditText, signUpPhoneNumberEditText, signUpFirstPasswordEditText, signUpSecondPasswordEditText;
    RelativeLayout signUpButtonsRelativeLayout;
    Button signInModeButton, signUpButton;
    TextView signUpUserTextView, signUpEmailTextView, signUpFullNameTextView, signUpPhoneNumberTextView, signUpPasswordTextView, signUpSecondPasswordTextView;

    //   Confirm SignUp
    LinearLayout confirmInputsLinearLayout;
    EditText confirmCodeEditText;
    RelativeLayout confirmButtonsRelativeLayout;
    Button resendConfirmationButton, signInModeButton2, confirmButton;

    //   Forgot Password
    LinearLayout forgotPasswordInputsLinearLayout;
    EditText forgotCodeEditText, forgotPasswordEditText;
    RelativeLayout forgotPasswordButtonsRelativeLayout;
    Button signInModeButton3, resetPasswordButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // variables initialization
        mAWSHelper = new AWSHelper();

        //Carousel
        mCustomPagerAdapter = new CustomPagerAdapter(this);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mCustomPagerAdapter);

        // assigning UI variables
//        signInWithFacebookButton = (Button) findViewById(R.id.signInWithFacebookButton);

        //   SignIn
        signInInputsLinearLayout = (LinearLayout) findViewById(R.id.signInInputsLinearLayout);
        signInUserEditText = (EditText) findViewById(R.id.signInUserEditText);
        signInPasswordEditText = (EditText) findViewById(R.id.signInPasswordEditText);
        signInButtonsRelativeLayout = (RelativeLayout) findViewById(R.id.signInButtonsRelativeLayout);
        forgotPasswordButton = (Button) findViewById(R.id.forgotPasswordButton);
        signUpModeButton = (Button) findViewById(R.id.signUpModeButton);
        signInButton = (Button) findViewById(R.id.signInButton);
        rightScrollArrow = findViewById(R.id.rightScrollArrow);
        leftScrollArrow = findViewById(R.id.leftScrollArrow);

        //   SignUp
        signUpScrollView = (ScrollView) findViewById(R.id.signUpScrollView);
        signUpInputsLinearLayout = (LinearLayout) findViewById(R.id.signUpInputsLinearLayout);
        signUpUserEditText = (EditText) findViewById(R.id.signUpUserEditText);
        signUpEmailEditText = (EditText) findViewById(R.id.signUpEmailEditText);
        signUpFullNameEditText = (EditText) findViewById(R.id.signUpFullNameEditText);
        signUpPhoneNumberEditText = (EditText) findViewById(R.id.signUpPhoneEditText);
        signUpFirstPasswordEditText = (EditText) findViewById(R.id.signUpFirstPasswordEditText);
        signUpSecondPasswordEditText = (EditText) findViewById(R.id.signUpSecondPasswordEditText);
        signUpButtonsRelativeLayout = (RelativeLayout) findViewById(R.id.signUpButtonsRelativeLayout);
        signInModeButton = (Button) findViewById(R.id.signInModeButton);
        signUpButton = (Button) findViewById(R.id.signUpButton);
        signUpUserTextView = (TextView) findViewById(R.id.signUpUsernameTextView);
        signUpEmailTextView = (TextView) findViewById(R.id.signUpEmailTextView);
        signUpFullNameTextView = (TextView) findViewById(R.id.signUpFullNameTextView);
        signUpPhoneNumberTextView = (TextView) findViewById(R.id.signUpPhoneNumberTextView);
        signUpPasswordTextView = (TextView) findViewById(R.id.signUpPasswordTextView);
        signUpSecondPasswordTextView = (TextView) findViewById(R.id.signUpSecondPasswordTextView);


        //   Confirm SignUp
        confirmInputsLinearLayout = (LinearLayout) findViewById(R.id.confirmInputsLinearLayout);
        confirmCodeEditText = (EditText) findViewById(R.id.confirmCodeEditText);
        confirmButtonsRelativeLayout = (RelativeLayout) findViewById(R.id.confirmButtonsRelativeLayout);
        resendConfirmationButton = (Button) findViewById(R.id.resendConfirmationButton);
        signInModeButton2 = (Button) findViewById(R.id.signInModeButton2);
        confirmButton = (Button) findViewById(R.id.confirmButton);

        //   Forgot Password
        forgotPasswordInputsLinearLayout = (LinearLayout) findViewById(R.id.forgotPasswordInputsLinearLayout);
        forgotCodeEditText = (EditText) findViewById(R.id.forgotCodeEditText);
        forgotPasswordEditText = (EditText) findViewById(R.id.forgotPasswordEditText);
        forgotPasswordButtonsRelativeLayout = (RelativeLayout) findViewById(R.id.forgotPasswordButtonsRelativeLayout);
        signInModeButton3 = (Button) findViewById(R.id.signInModeButton3);
        resetPasswordButton = (Button) findViewById(R.id.resetPasswordButton);

        // setting listeners
//        signInWithFacebookButton.setOnClickListener(this);
        forgotPasswordButton.setOnClickListener(this);
        signUpModeButton.setOnClickListener(this);
        signInButton.setOnClickListener(this);
        signInModeButton.setOnClickListener(this);
        signUpButton.setOnClickListener(this);
        resendConfirmationButton.setOnClickListener(this);
        signInModeButton2.setOnClickListener(this);
        confirmButton.setOnClickListener(this);
        signInModeButton3.setOnClickListener(this);
        resetPasswordButton.setOnClickListener(this);


        signUpUserEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0){
                    signUpUserTextView.setHeight(50);
                }
            }
        });
        signUpEmailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0){
                    signUpEmailTextView.setHeight(50);
                }
            }
        });
        signUpFullNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0){
                    signUpFullNameTextView.setHeight(50);
                }
            }
        });
        signUpPhoneNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0){
                    signUpPhoneNumberTextView.setHeight(50);
                }
            }
        });
        signUpFirstPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0){
                    signUpPasswordTextView.setHeight(50);
                }
            }
        });
        signUpSecondPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0){
                    signUpSecondPasswordTextView.setHeight(50);
                }
            }
        });

        // setting handlers
        // handler for forgot password function, this one needs to be here because it's called two times (verification code and effective password change)
        mForgotPasswordHandler = new ForgotPasswordHandler() {
            @Override
            public void onSuccess() {
                Toast.makeText(LoginActivity.this, "Password reset! You can login now.", Toast.LENGTH_SHORT).show();
                showSignInUI(true);
            }

            @Override
            public void getResetCode(ForgotPasswordContinuation continuation) {
                mForgotPasswordContinuation = continuation;
                showForgotPasswordUI(true);
            }

            @Override
            public void onFailure(Exception exception) {
                if (exception instanceof LimitExceededException) {
                    Toast.makeText(LoginActivity.this, "Limit exceeded. Wait to try again.", Toast.LENGTH_SHORT).show();
                } else if (exception instanceof UserNotFoundException) {
                    Toast.makeText(LoginActivity.this, "User does not exist.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        };
        // handler for log in function
        mAuthenticationHandler = new AuthenticationHandler() {
            @Override
            public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
                Intent i = new Intent(LoginActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.putExtra("USER", mSignInUser);
                startActivity(i);
                finish();
            }

            @Override
            public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {
                if (null != mSignInUser && null != mSignInPassword) {
                    final AuthenticationDetails authenticationDetails = new AuthenticationDetails(mSignInUser, mSignInPassword, null);
                    authenticationContinuation.setAuthenticationDetails(authenticationDetails);
                    authenticationContinuation.continueTask();
                }
            }

            @Override
            public void getMFACode(MultiFactorAuthenticationContinuation continuation) {
                Toast.makeText(LoginActivity.this, "MFACode not supported.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void authenticationChallenge(ChallengeContinuation continuation) {
                Toast.makeText(LoginActivity.this, "Authentication challenge not supported.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception exception) {
                if (exception instanceof UserNotConfirmedException) {
                    Toast.makeText(LoginActivity.this, "Confirm user first.", Toast.LENGTH_SHORT).show();
//                    showConfirmationUI(true);
                } else if (exception instanceof UserNotFoundException) {
                    Toast.makeText(LoginActivity.this, "User does not exist.", Toast.LENGTH_SHORT).show();
                } else if (exception instanceof NotAuthorizedException) {
                    Toast.makeText(LoginActivity.this, "Incorrect username or password.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        };

    }

    @Override
    protected void onResume() {
        super.onResume();

        showSignInUI(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.forgotPasswordButton:
                forgotUserPassword();
                break;

            case R.id.signUpModeButton:
                showSignUpUI(true);
                break;

            case R.id.signInModeButton:
            case R.id.signInModeButton2:
            case R.id.signInModeButton3:
                showSignInUI(true);
                break;

            case R.id.signInButton:
                signInUser();
                break;

            case R.id.signUpButton:
                if (signUpFirstPasswordEditText.getText().toString().equals(signUpSecondPasswordEditText.getText().toString())) {
                    signUpUser();
                } else {
                    Toast.makeText(LoginActivity.this, "Passwords don't match!", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.resendConfirmationButton:
                resendConfirmationCode();
                break;

            case R.id.confirmButton:
                confirmUser();
                break;

            case R.id.resetPasswordButton:
                resetUserPassword();
                break;

//            case R.id.signInWithFacebookButton:
////                Intent i = new Intent(LoginActivity.this, FacebookActivity.class);
////                startActivityForResult(i, 1000);
//                break;

        }
    }

    private void showSignInUI(boolean show) {
        if (show) {
            showConfirmationUI(false);
            showSignUpUI(false);
            showForgotPasswordUI(false);

            signInInputsLinearLayout.setVisibility(View.VISIBLE);
            signInButtonsRelativeLayout.setVisibility(View.VISIBLE);
            mViewPager.setVisibility(View.VISIBLE);
            rightScrollArrow.setVisibility(View.VISIBLE);
            leftScrollArrow.setVisibility(View.VISIBLE);
//            signInWithFacebookButton.setVisibility(View.VISIBLE);
        } else {
            signInUserEditText.setText("");
            signInPasswordEditText.setText("");
            signInInputsLinearLayout.setVisibility(View.GONE);
            signInButtonsRelativeLayout.setVisibility(View.GONE);
            mViewPager.setVisibility(View.GONE);
            rightScrollArrow.setVisibility(View.GONE);
            leftScrollArrow.setVisibility(View.GONE);
        }
    }

    private void showSignUpUI(boolean show) {
        if (show) {
            showSignInUI(false);
            showConfirmationUI(false);
            showForgotPasswordUI(false);
            signUpScrollView.setVisibility(View.VISIBLE);
            signUpInputsLinearLayout.setVisibility(View.VISIBLE);
            signUpButtonsRelativeLayout.setVisibility(View.VISIBLE);
//            signInWithFacebookButton.setVisibility(View.VISIBLE);
        } else {
            signUpUserEditText.setText("");
            signUpEmailEditText.setText("");
            signUpFirstPasswordEditText.setText("");
            signUpSecondPasswordEditText.setText("");
            signUpScrollView.setVisibility(View.GONE);
            signUpInputsLinearLayout.setVisibility(View.GONE);
            signUpButtonsRelativeLayout.setVisibility(View.GONE);
        }
    }

    private void showConfirmationUI(boolean show) {
        if (show) {
            showSignInUI(false);
            showSignUpUI(false);
            showForgotPasswordUI(false);

            confirmInputsLinearLayout.setVisibility(View.VISIBLE);
            confirmButtonsRelativeLayout.setVisibility(View.VISIBLE);
//            signInWithFacebookButton.setVisibility(View.GONE);
        } else {
            confirmCodeEditText.setText("");
            confirmInputsLinearLayout.setVisibility(View.GONE);
            confirmButtonsRelativeLayout.setVisibility(View.GONE);
        }
    }

    private void showForgotPasswordUI(boolean show) {
        if (show) {
            showSignInUI(false);
            showSignUpUI(false);
            showConfirmationUI(false);

            forgotPasswordInputsLinearLayout.setVisibility(View.VISIBLE);
            forgotPasswordButtonsRelativeLayout.setVisibility(View.VISIBLE);
//            signInWithFacebookButton.setVisibility(View.GONE);
        } else {
            forgotCodeEditText.setText("");
            forgotPasswordEditText.setText("");
            forgotPasswordInputsLinearLayout.setVisibility(View.GONE);
            forgotPasswordButtonsRelativeLayout.setVisibility(View.GONE);
        }
    }

    private void signInUser() {
        mSignInUser = signInUserEditText.getText().toString();
        if (mSignInUser.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Username required.", Toast.LENGTH_SHORT).show();
            return;
        }
        mSignInPassword = signInPasswordEditText.getText().toString();
        if (mSignInPassword.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Password required.", Toast.LENGTH_SHORT).show();
            return;
        }
        mCognitoUser = mAWSHelper.getUserPool().getUser(mSignInUser);
        mCognitoUser.getSessionInBackground(mAuthenticationHandler);
    }

    private void forgotUserPassword() {
        String userName = signInUserEditText.getText().toString();
        if (userName.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Username required.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mCognitoUser == null) {
            mCognitoUser = mAWSHelper.getUserPool().getUser(userName);
        }

        mCognitoUser.forgotPasswordInBackground(mForgotPasswordHandler); // handler created inside onCreate
    }

    private void resetUserPassword() {
        String resetCode = forgotCodeEditText.getText().toString();
        if (resetCode.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Code required.", Toast.LENGTH_SHORT).show();
            return;
        }

        String newPassword = forgotPasswordEditText.getText().toString();
        if (newPassword.isEmpty()) {
            Toast.makeText(LoginActivity.this, "New password required.", Toast.LENGTH_SHORT).show();
            return;
        }

        mForgotPasswordContinuation.setVerificationCode(resetCode);
        mForgotPasswordContinuation.setPassword(newPassword);
        mForgotPasswordContinuation.continueTask();
    }

    private void signUpUser() {
        String userName = signUpUserEditText.getText().toString();
        if (userName.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Username required.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userEmail = signUpEmailEditText.getText().toString();
        if (userEmail.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Email address required.", Toast.LENGTH_SHORT).show();
            return;
//        } else {
//            if (!CKEmail.isValidEmail(userEmail)) {
//                Toast.makeText(LoginActivity.this, "Email format invalid.", Toast.LENGTH_SHORT).show();
//            }
        }

        String userPassword = signUpFirstPasswordEditText.getText().toString();
        if (userPassword.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Password required.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userPhone = signUpPhoneNumberEditText.getText().toString();
        if (userPassword.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Phone number required.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userFullName = signUpFullNameEditText.getText().toString();
        if (userPassword.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Full name required.", Toast.LENGTH_SHORT).show();
            return;
        }

        SignUpHandler signUpHandler = new SignUpHandler() {
            @Override
            public void onSuccess(CognitoUser user, boolean signUpConfirmationState, CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
                if(!signUpConfirmationState) {
                    mCognitoUser = user;
                    showConfirmationUI(true);
                } else {
                    showSignInUI(true);
                }
            }

            @Override
            public void onFailure(Exception exception) {
                // TODO: see these messages
                if (exception instanceof UsernameExistsException) {
                    Toast.makeText(LoginActivity.this, "username/email already exists.", Toast.LENGTH_SHORT).show();
                } else if (exception instanceof InvalidParameterException) {
                    Toast.makeText(LoginActivity.this, "Invalid parameters.", Toast.LENGTH_SHORT).show();
                    Log.d("AHH", exception.toString());
                } else {
                    Toast.makeText(LoginActivity.this, "Ooops!", Toast.LENGTH_SHORT).show();
                    Log.e("wotom", exception.getMessage());
                }
            }
        };
        mAWSHelper.signUpUser(userName, userEmail, userPassword, userPhone, userFullName, signUpHandler);
    }

    private void confirmUser() {
        String confirmationCode = confirmCodeEditText.getText().toString();
        GenericHandler confirmationCallback = new GenericHandler() {
            @Override
            public void onSuccess() {
                showSignInUI(true);
                Toast.makeText(LoginActivity.this, "Confirmation success! You can login now.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception exception) {
                Toast.makeText(LoginActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        mCognitoUser.confirmSignUpInBackground(confirmationCode, false, confirmationCallback);
    }

    private void resendConfirmationCode() {
        VerificationHandler verificationHandler = new VerificationHandler() {
            @Override
            public void onSuccess(CognitoUserCodeDeliveryDetails verificationCodeDeliveryMedium) {
                Toast.makeText(LoginActivity.this, "New code sent to email.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception exception) {
                Toast.makeText(LoginActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        mCognitoUser.resendConfirmationCodeInBackground(verificationHandler);
    }

    class CustomPagerAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;

        public CustomPagerAdapter(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return mResources.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
            imageView.setImageResource(mResources[position]);

            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }

    @Override
    public void onBackPressed() {
    }

}
