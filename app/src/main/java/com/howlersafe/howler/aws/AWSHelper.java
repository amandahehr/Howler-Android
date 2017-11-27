package com.howlersafe.howler.aws;

import com.amazonaws.mobile.auth.core.IdentityManager;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.amazonaws.ClientConfiguration;
import org.json.JSONException;
import org.json.JSONObject;
import static com.amazonaws.regions.Regions.US_EAST_1;
import static com.howlersafe.howler.Application.context;

public class AWSHelper {

    private IdentityManager mIdentityManager;
    public String KEY_1;
    public String KEY_2;


    public AWSHelper() {
        mIdentityManager = IdentityManager.getDefaultIdentityManager();
        JSONObject mJSONObject = mIdentityManager.getConfiguration().optJsonObject("SNSPermissions");
        try {
            KEY_1 = mJSONObject.getString("key1");
            KEY_2 = mJSONObject.getString("key2");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


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