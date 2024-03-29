package com.github.acikima.testmsauth;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.exception.MsalClientException;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.identity.client.exception.MsalServiceException;
import com.microsoft.identity.client.exception.MsalUiRequiredException;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {

    final static String SCOPES [] = {"https://graph.microsoft.com/User.Read"};
    final static String MSGRAPH_URL = "https://graph.microsoft.com/v1.0/me";

    public String userInfo;

    /* UI & Debugging Variables */
    private static final String TAG = MainActivity.class.getSimpleName();
//    Button callGraphButton;
//    Button signOutButton;

    /* Azure AD Variables */
    private PublicClientApplication sampleApp;
    private IAuthenticationResult authResult;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

//        callGraphButton = (Button) findViewById(R.id.callGraph);
//        signOutButton = (Button) findViewById(R.id.clearCache);
//
//        callGraphButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                onCallGraphClicked();
//            }
//        });
//
//        signOutButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                onSignOutClicked();
//            }
//        });

        /* Configure your sample app and save state for this activity */
//        sampleApp = new PublicClientApplication(
//                this.getApplicationContext(),
//                R.raw.auth_config);

        /* Attempt to get a user and acquireTokenSilent */
//        sampleApp.getAccounts(new PublicClientApplication.AccountsLoadedCallback() {
//            @Override
//            public void onAccountsLoaded(final List<IAccount> accounts) {
//                if (!accounts.isEmpty()) {
//                    /* This sample doesn't support multi-account scenarios, use the first account */
//                    sampleApp.acquireTokenSilentAsync(SCOPES, accounts.get(0), getAuthSilentCallback());
//                } else {
//                    /* No accounts */
//                }
//            }
//        });
//    }

    public MainActivity(Context context){

        /* Configure your sample app and save state for this activity */
        sampleApp = new PublicClientApplication(
                context,
                R.raw.auth_config);

        /* Attempt to get a user and acquireTokenSilent */
        sampleApp.getAccounts(new PublicClientApplication.AccountsLoadedCallback() {
            @Override
            public void onAccountsLoaded(final List<IAccount> accounts) {
                if (!accounts.isEmpty()) {
                    /* This sample doesn't support multi-account scenarios, use the first account */
                    sampleApp.acquireTokenSilentAsync(SCOPES, accounts.get(0), getAuthSilentCallback());
                } else {
                    /* No accounts */
                }
            }
        });

//        onCallGraphClicked();

    }

    /* Set the UI for successful token acquisition data */
//    private void updateSuccessUI() {
//        callGraphButton.setVisibility(View.INVISIBLE);
//        signOutButton.setVisibility(View.VISIBLE);
//        findViewById(R.id.welcome).setVisibility(View.VISIBLE);
//        ((TextView) findViewById(R.id.welcome)).setText("Welcome, " +
//                authResult.getAccount().getUsername());
//        findViewById(R.id.graphData).setVisibility(View.VISIBLE);
//    }

    /* Set the UI for signed out account */
//    private void updateSignedOutUI() {
//        callGraphButton.setVisibility(View.VISIBLE);
//        signOutButton.setVisibility(View.INVISIBLE);
//        findViewById(R.id.welcome).setVisibility(View.INVISIBLE);
//        findViewById(R.id.graphData).setVisibility(View.INVISIBLE);
//        ((TextView) findViewById(R.id.graphData)).setText("No Data");
//
//        Toast.makeText(getBaseContext(), "Signed Out!", Toast.LENGTH_SHORT)
//                .show();
//    }

    /* Use MSAL to acquireToken for the end-user
     * Callback will call Graph api w/ access token & update UI
     */
    public void showToast(Context context){
        Toast.makeText(context, "U made it Imaaaa", Toast.LENGTH_LONG).show();
    }

    public String getUserInfo() {
        userInfo = setUserInfo();
        return userInfo;
    }

    public String setUserInfo() {
        String name = "Fatimatul Zaharah Ghazali";

        return name;
    }

    public String onCallGraphClicked(Activity activity) {
//        sampleApp.acquireToken(getActivity(), SCOPES, getAuthInteractiveCallback());
        sampleApp.acquireToken(activity, SCOPES, getAuthInteractiveCallback());

        return userInfo;
    }

    public Activity getActivity() {
        return this;
    }

    /* Callback used in for silent acquireToken calls.
     * Looks if tokens are in the cache (refreshes if necessary and if we don't forceRefresh)
     * else errors that we need to do an interactive request.
     */
    public AuthenticationCallback getAuthSilentCallback() {
        return new AuthenticationCallback() {

            @Override
            public void onSuccess(IAuthenticationResult authenticationResult) {
                /* Successfully got a token, call graph now */
                Log.d(TAG, "Successfully authenticated");

                /* Store the authResult */
                authResult = authenticationResult;

                /* call graph */
                callGraphAPI();

                /* update the UI to post call graph state */
//                updateSuccessUI();
            }

            @Override
            public void onError(MsalException exception) {
                /* Failed to acquireToken */
                Log.d(TAG, "Authentication failed: " + exception.toString());

                if (exception instanceof MsalClientException) {
                    /* Exception inside MSAL, more info inside the exception */
                } else if (exception instanceof MsalServiceException) {
                    /* Exception when communicating with the STS, likely config issue */
                } else if (exception instanceof MsalUiRequiredException) {
                    /* Tokens expired or no session, retry with interactive */
                }
            }

            @Override
            public void onCancel() {
                /* User cancelled the authentication */
                Log.d(TAG, "User cancelled login.");
            }
        };
    }

    /* Callback used for interactive request.  If succeeds we use the access
     * token to call the Microsoft Graph. Does not check cache
     */
    public AuthenticationCallback getAuthInteractiveCallback() {
        return new AuthenticationCallback() {

            @Override
            public void onSuccess(IAuthenticationResult authenticationResult) {
                /* Successfully got a token, call graph now */
                Log.d(TAG, "Successfully authenticated");
                Log.d(TAG, "ID Token: " + authenticationResult.getIdToken());

                /* Store the auth result */
                authResult = authenticationResult;

                /* call graph */
                callGraphAPI();

                /* update the UI to post call graph state */
//                updateSuccessUI();
            }

            @Override
            public void onError(MsalException exception) {
                /* Failed to acquireToken */
                Log.d(TAG, "Authentication failed: " + exception.toString());

                if (exception instanceof MsalClientException) {
                    /* Exception inside MSAL, more info inside the exception */
                } else if (exception instanceof MsalServiceException) {
                    /* Exception when communicating with the STS, likely config issue */
                }
            }

            @Override
            public void onCancel() {
                /* User cancelled the authentication */
                Log.d(TAG, "User cancelled login.");
                userInfo = "User cancelled login.";
            }
        };
    }

    /* Clears an account's tokens from the cache.
     * Logically similar to "sign out" but only signs out of this app.
     * User will get interactive SSO if trying to sign back-in.
     */
    public void onSignOutClicked() {
        /* Attempt to get a user and acquireTokenSilent
         * If this fails we do an interactive request
         */
        sampleApp.getAccounts(new PublicClientApplication.AccountsLoadedCallback() {
            @Override
            public void onAccountsLoaded(final List<IAccount> accounts) {

                if (accounts.isEmpty()) {
                    /* No accounts to remove */

                } else {
                    for (final IAccount account : accounts) {
                        sampleApp.removeAccount(
                                account,
                                new PublicClientApplication.AccountsRemovedCallback() {
                                    @Override
                                    public void onAccountsRemoved(Boolean isSuccess) {
                                        if (isSuccess) {
                                            /* successfully removed account */
                                        } else {
                                            /* failed to remove account */
                                        }
                                    }
                                });
                    }
                }

//                updateSignedOutUI();
            }
        });
    }

    /* Use Volley to make an HTTP request to the /me endpoint from MS Graph using an access token */
    public void callGraphAPI() {
        Log.d(TAG, "Starting volley request to graph");

        /* Make sure we have a token to send to graph */
        if (authResult.getAccessToken() == null) {return;}

        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject parameters = new JSONObject();

        try {
            parameters.put("key", "value");
        } catch (Exception e) {
            Log.d(TAG, "Failed to put parameters: " + e.toString());
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, MSGRAPH_URL,
                parameters,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                /* Successfully called graph, process data and send to UI */
                Log.d(TAG, "Response: " + response.toString());

                updateGraphUI(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + authResult.getAccessToken());
                return headers;
            }
        };

        Log.d(TAG, "Adding HTTP GET to Queue, Request: " + request.toString());

        request.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);



    }

    /* Sets the graph response */
    private void updateGraphUI(JSONObject graphResponse) {
        Toast.makeText(this, graphResponse.toString(), Toast.LENGTH_LONG).show();
        userInfo = graphResponse.toString();
//        TextView graphText = findViewById(R.id.graphData);
//        graphText.setText(graphResponse.toString());
    }
}
