package com.howlersafe.howler;

import android.content.Context;
import android.support.multidex.MultiDexApplication;
import com.amazonaws.mobile.auth.core.IdentityManager;
import com.amazonaws.mobile.auth.userpools.CognitoUserPoolsSignInProvider;
import com.amazonaws.mobile.config.AWSConfiguration;



public class Application extends MultiDexApplication {
    private static final String LOG_TAG = Application.class.getSimpleName();
    public static Context context;


    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        initializeApplication();
        // application initialized
    }

    private void initializeApplication() {

        AWSConfiguration awsConfiguration = new AWSConfiguration(getApplicationContext());

        // If IdentityManager is not created, create it
        if (IdentityManager.getDefaultIdentityManager() == null) {
            IdentityManager identityManager =
                    new IdentityManager(getApplicationContext(), awsConfiguration);
            IdentityManager.setDefaultIdentityManager(identityManager);
        }
        // Add Amazon Cognito User Pools as Identity Provider.
        IdentityManager.getDefaultIdentityManager().addSignInProvider(
                CognitoUserPoolsSignInProvider.class);

    }
}