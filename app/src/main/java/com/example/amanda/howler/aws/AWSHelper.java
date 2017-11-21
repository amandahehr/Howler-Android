package com.example.amanda.howler.aws;

import android.util.Log;
import com.amazonaws.mobile.auth.core.IdentityManager;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.amazonaws.ClientConfiguration;


import static com.amazonaws.regions.Regions.US_EAST_1;
import static com.example.amanda.howler.Application.context;

public class AWSHelper {

    private IdentityManager mIdentityManager;
    public static String fullName = " ";



//    public AWSHelper(LoginActivity loginActivity) {
//        mIdentityManager = IdentityManager.getDefaultIdentityManager();
//    }
//
//    public IdentityManager getIdentityManager() {
//        return mIdentityManager;
//    }


    public static CognitoUserPool getUserPool() {
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        CognitoUserPool myUserPool = new CognitoUserPool(context, "us-east-1_hndr06qgm", "4tmbf6gmqv50c1isbpm8f96ug2", "uppd3rqia17n59eajtnnrl9taaemg9bmaqh16tohcce7e14fl7r", clientConfiguration, US_EAST_1);
        return myUserPool;
    }

    public void signUpUser(String userName, String userEmail, String userPassword, String userPhone, String userFullName, SignUpHandler signUpHandler) {
        // Create a CognitoUserAttributes object and add user attributes
        CognitoUserAttributes userAttributes = new CognitoUserAttributes();
        // Adding user's email address
        userAttributes.addAttribute("email", userEmail);
        userAttributes.addAttribute("phone_number", "+1"+userPhone);
        userAttributes.addAttribute("custom:full_name", userFullName);

        getUserPool().signUpInBackground(userName, userPassword, userAttributes, null, signUpHandler);
    }

    public static CognitoUser getCognitoUser() {
        return getUserPool().getCurrentUser();
    }



}