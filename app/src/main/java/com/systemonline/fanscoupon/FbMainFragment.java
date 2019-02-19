package com.systemonline.fanscoupon;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.systemonline.fanscoupon.Base.BaseFragment;
import com.systemonline.fanscoupon.Helpers.Utility;
import com.systemonline.fanscoupon.Model.Fan;
import com.systemonline.fanscoupon.WebServices.JSONAsync;
import com.systemonline.fanscoupon.WebServices.JSONWebServices;
import com.systemonline.fanscoupon.WebServices.ParseData;
import com.systemonline.fanscoupon.coupon_tabs.CouponTab;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.systemonline.fanscoupon.MainActivity.editor;


public class FbMainFragment extends BaseFragment {
    public static String name;
    public static String requestError = "", requestErrorType = "";
    Utility _utility;
    EditText userName, userPassword;
    LoginManager loginManager = LoginManager.getInstance();
    JSONAsync call;
    Handler timerHandler = new Handler();
    private CallbackManager mCallbackManager;
    private String access_token = null, access_token_expires, requestType;
    private String userID;
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            Log.e("has permission ", "-------------- " + AccessToken.getCurrentAccessToken().getPermissions().contains("publish_pages"));
            AccessToken.refreshCurrentAccessTokenAsync();
            for (int i = 0; i < AccessToken.getCurrentAccessToken().getPermissions().toArray().length; i++) {
                Log.e("accessToken permissions", AccessToken.getCurrentAccessToken().getPermissions().toArray()[i].toString());
            }
            Log.e("inside timer ", "-------------- " + AccessToken.getCurrentAccessToken().getPermissions().size());
            Set<String> permissions = AccessToken.getCurrentAccessToken().getPermissions();
            if (permissions.contains("user_birthday") && permissions.contains("user_friends") &&
                    permissions.contains("user_likes")
                    && permissions.contains("email")
                    && permissions.contains("publish_pages") && permissions.contains("manage_pages")) {
                access_token = AccessToken.getCurrentAccessToken().getToken();
                access_token_expires = AccessToken.getCurrentAccessToken().getExpires().toString();
                if (_utility.isConnectingToInternet_ping()) {
                    Log.e("accessToken", "request facebook button");
                    Log.e("accessToken", "perm size array " + AccessToken.getCurrentAccessToken().getPermissions().contains("user_friends"));


                    Log.e("profile fb id", userID + "-+++-");
                    Log.e("fb acc token", AccessToken.getCurrentAccessToken().getToken() + "-+++-");

                    if (MainActivity.currentFan != null)
                        addFBAccount(AccessToken.getCurrentAccessToken().getToken());
                    else {
                        _utility.ShowDialog(getResources().getString(R.string.plz_w8), false);
                        requestType = "requestNewAccessToken";
                        call = Fan.requestNewAccessToken(AccessToken.getCurrentAccessToken().getToken(), FbMainFragment.this, userID);
                    }
                } else {
                    LoginManager.getInstance().logOut();
                    Toast.makeText(getContext(), getResources().getString(R.string.no_net), Toast.LENGTH_LONG).show();
                }
                timerHandler.removeCallbacks(timerRunnable);
            } else
                timerHandler.postDelayed(this, 3000);
        }
    };
    /**
     * facebook login callback (login process result)
     */
    private FacebookCallback<LoginResult> mCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            final AccessToken accessToken = loginResult.getAccessToken();
            Log.e("FB first login", "access token" + accessToken.getToken());
            Log.e("FB first login", "access token last refresh" + accessToken.getLastRefresh());
            Log.e("token expire", accessToken.getExpires().toString());
            GraphRequestAsyncTask request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject user, GraphResponse graphResponse) {
                    userID = user.optString("id");
                    loginManager.logInWithReadPermissions(
                            getActivity(),
                            Arrays.asList("email", "user_likes", "user_friends",
                                    "user_birthday", "user_posts"));
                    timerHandler.postDelayed(timerRunnable, 100);

                }
            }).executeAsync();

        }

        @Override
        public void onCancel() {
            Log.e("first login:", "cancel");
            timerHandler.removeCallbacks(timerRunnable);

        }

        @Override
        public void onError(FacebookException e) {
            e.printStackTrace();
            Log.e("first login:", "error");
            timerHandler.removeCallbacks(timerRunnable);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onResume() {
        super.onResume();
        Profile profile = Profile.getCurrentProfile();
    }


    @Override
    public void onStop() {
        Log.e("on stop", "stop");
        super.onStop();

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fb_fragment_main, container, false);
        _utility = new Utility(getContext());


        return rootView;
    }


    private void addFBAccount(String token) {
        if (!_utility.isConnectingToInternet_ping()) {
//            customLoading.hideProgress();
            _utility.HideDialog();
            _utility.showMessage(getResources().getString(R.string.no_net));
            return;
        }

        try {
            requestType = "addFBAccount";
//            customLoading.showProgress(_utility.getCurrentActivity());
            _utility.ShowDialog(getResources().getString(R.string.plz_w8), true);

            JSONWebServices service = new JSONWebServices(FbMainFragment.this);
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("mobile", "true"));
            nameValuePairs.add(new BasicNameValuePair("account_name", "Fanzo"));
            nameValuePairs.add(new BasicNameValuePair("social_network_account_id", userID));
            nameValuePairs.add(new BasicNameValuePair("social_network_type", "13"));
            nameValuePairs.add(new BasicNameValuePair("token", token));

            call = service.addSocialAccount(nameValuePairs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void PostBackExecutionJSON(JSONTokener Result) {
        try {


            switch (requestType) {
                case "resendVerEmail":
//                    if (ParseData.parseAddTwitterAccount(Result, _utility)) {
                    showDialog(ParseData.parseResendVerEmailResponse(Result));
//                        getActivity().onBackPressed();

//                    } else
//                        showDialog(requestError);

                    break;
                case "addFBAccount":
                    if (ParseData.parseAddTwitterAccount(Result, _utility)) {
                        showDialog(getContext().getResources().getString(R.string.social_account_added));
                        getActivity().onBackPressed();

                    } else
                        showDialog(requestError);

                    break;
                default:
                    MainActivity.currentFan = ParseData.parseFan(Result, _utility);
                    if (MainActivity.currentFan == null) {
//                    LoginManager.getInstance().logOut();
//                    _utility.showMessage(getResources().getString(R.string.error));
                        _utility.HideDialog();
                        showDialog(requestError);
                        return;
                    }
//        Log.e("register  response", Result.toString());
                    editor.putString("fb_account_id", userID);
                    editor.putString("fb_access_token", access_token);
                    editor.putString("fb_access_token_expires", access_token_expires);
                    editor.putString("admin_name", MainActivity.currentFan.getFanFirstName());
                    editor.putString("admin_picture", MainActivity.currentFan.getFanImage());
                    editor.putInt("admin_id", MainActivity.currentFan.getFanID());
                    if (MainActivity.currentFan.getFanEMAil() != null)
                        editor.putString("admin_mail", MainActivity.currentFan.getFanEMAil());
                    MainActivity.WS_ACCESSTOKEN = MainActivity.currentFan.getFanAccessToken();
                    editor.putString("accessToken", MainActivity.currentFan.getFanAccessToken());
                    Log.e("accessToken from resp", MainActivity.currentFan.getFanAccessToken());

                    editor.putBoolean("stop_mobile_notification", MainActivity.currentFan.isStopNotification());
                    Log.e("stop mobile_notif", MainActivity.currentFan.isStopNotification() + " <<");

                    editor.putLong("accessTokenCreation", System.currentTimeMillis() / 1000);
                    editor.putInt("country_id", MainActivity.currentFan.getFanCountryID());
//            editor.putString("lang", "en");
//            MainActivity.langResult = "en";
                    editor.commit();
                    MainActivity.setUserDataNavDrawer();

                    MainActivity.registerToGCM();
//            CouponTab.allCoupons = null;
                    CouponTab.allCoupons2 = null;

//            CouponTab.forYou = null;
                    CouponTab.forYou2 = null;
                    CouponTab.couponPage = 1;
                    CouponTab.couponPageOnlyYou = 1;
                    MainActivity.logout.setText(getResources().getString(R.string.logout));
//            Fragment fragment = new CouponTab();
//            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.fragment_container, fragment);
//            fragmentTransaction.addToBackStack(null);
//            fragmentTransaction.commitAllowingStateLoss();
                    restartService();


                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        getActivity().finishAffinity();
                    } else {
                        getActivity().finish();
                    }

                    Intent i = getActivity().getIntent();
                    startActivity(i);

                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
            _utility.showMessage(getResources().getString(R.string.ws_err));
        } finally {
            _utility.HideDialog();
            call = null;
        }

    }

    void showDialog(final String message) {
        Log.e("fb login show dialog", "begin transaction");
//        customLoading.hideProgress();
        _utility.HideDialog();

        AlertDialog.Builder build = new AlertDialog.Builder(getContext());

        build.setMessage(message)
                .setCancelable(false)

                .setPositiveButton(getContext().getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        if (message.equals(requestError)) {
                            try {
                                LoginManager.getInstance().logOut();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }
                });
        if (requestErrorType.equals("not_verified"))
            build.setNeutralButton(R.string.resend_code, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    resendVerEmail();
                }
            });
        AlertDialog alert = build.create();
        alert.show();
    }

    private void resendVerEmail() {
        if (!_utility.isConnectingToInternet_ping()) {
//            customLoading.hideProgress();
            _utility.HideDialog();
            _utility.showMessage(getResources().getString(R.string.no_net));
            return;
        }

        try {
            requestType = "resendVerEmail";
//            customLoading.showProgress(_utility.getCurrentActivity());
            _utility.ShowDialog(getResources().getString(R.string.plz_w8), true);

            JSONWebServices service = new JSONWebServices(FbMainFragment.this);
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("mail", userName.getText().toString()));
            call = service.resendVerEmail(nameValuePairs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * start promotions notifications service
     */
    private void restartService() {
        Log.e("MainActivity", "0fn - startService");

        try {
            _utility.getCurrentActivity().stopService(new Intent(_utility.getCurrentActivity(), CampaignLocationsService.class));
        } catch (Exception e) {
            e.printStackTrace();
        }

//        String serviceState = sharedPreferences.getString("toggle", "no_serv");

//        if (serviceState.equals("no_serv")) {
        _utility.getCurrentActivity().startService(new Intent(_utility.getCurrentActivity(), CampaignLocationsService.class));
//        editor.putString("toggle", "true");
//        editor.commit();
//        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        ImageView img3 = (ImageView) view.findViewById(R.id.imageView3);
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton login_button = (LoginButton) view.findViewById(R.id.login_button);
        login_button.setFragment(this);
        login_button.setPublishPermissions(Arrays.asList("manage_pages", "publish_pages"));
        login_button.registerCallback(mCallbackManager, mCallback);

        userName = (EditText) view.findViewById(R.id.et_username);
        userPassword = (EditText) view.findViewById(R.id.et_password);
        Button loginButton = (Button) view.findViewById(R.id.btn_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!userName.getText().toString().equals("") && !userPassword.getText().toString().equals("")) {
                    InputMethodManager imm = (InputMethodManager) _utility.getCurrentActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    Log.e("accessToken", "request login button");
                    _utility.ShowDialog(getResources().getString(R.string.plz_w8), false);
                    requestType = "requestNewAccessToken";
                    call = Fan.requestNewAccessToken(userName.getText().toString(), userPassword.getText().toString(), FbMainFragment.this);
                } else
                    _utility.showMessage(getResources().getString(R.string.comp_form));
            }
        });

        TextView register = (TextView) view.findViewById(R.id.register_tv);
//        register.setVisibility(View.GONE);
        register.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Log.e("login fragment", "process fragment transaction");

                    getActivity().getSupportFragmentManager().
                            beginTransaction().
                            replace(R.id.fragment_container, new RegistrationFragment()).
                            addToBackStack(null).
                            commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

}