package com.systemonline.fanscoupon;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.squareup.picasso.Picasso;
import com.systemonline.fanscoupon.Base.BaseFragment;
import com.systemonline.fanscoupon.Helpers.Const;
import com.systemonline.fanscoupon.Helpers.Utility;
import com.systemonline.fanscoupon.Model.CustomLoading;
import com.systemonline.fanscoupon.WebServices.JSONAsync;
import com.systemonline.fanscoupon.WebServices.JSONWebServices;
import com.systemonline.fanscoupon.WebServices.ParseData;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Online108 on 11/20/2016.
 */

public class EditAccountFragment extends BaseFragment {
    public static final int G_SIGN_IN = 2017;
    public static EditText birthdate;
    public static TwitterLoginButton twitterLoginButton;
    public static String addSocialAccountErr;
    public EditText user_name, first_name, last_name, mail, phone, mob, address, city_name, country, pw, pw_conf;
    Utility _utility;
    TextView full_name, couponsCount, tw_status, g_status;
    ImageView imgv_pp;
    LinearLayout lin_update;
    JSONAsync call;
    ArrayList<EditText> allRequiredFields = new ArrayList<>();
    int PICK_FROM_CAMERA = 1;
    int RESULT_LOAD_IMAGE = 2;
    String social_network_account_id;
    private String requestType;
    private CustomLoading customLoading;
    private String image_str;
    private GoogleApiClient mGoogleApiClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.edit_my_account, null);
        _utility = new Utility(getContext());
        customLoading = new CustomLoading(_utility.getCurrentActivity());
        image_str = "";
        tw_status = (TextView) rootView.findViewById(R.id.tw_status);
        g_status = (TextView) rootView.findViewById(R.id.g_status);
//        final String[] items = new String[]{getResources().getString(R.string.from_cam), getResources().getString(R.string.from_gallery)};
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item, items);
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//
//        builder.setTitle(getResources().getString(R.string.sel_img));
//        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int item) {
//                if (item == 0) {
//                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    startActivityForResult(cameraIntent, PICK_FROM_CAMERA);
//
//                    dialog.cancel();
//                } else {
//                    Intent i = new Intent(Intent.ACTION_PICK,
//                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                    startActivityForResult(i, RESULT_LOAD_IMAGE);
//                }
//            }
//        });
//
//        final AlertDialog dialog = builder.create();

        for (int i = 0; i < MainActivity.myProfile.getSocialAccounts().size(); i++) {
            if (MainActivity.myProfile.getSocialAccounts().get(i).getSocialNetworkType() == 14) {
                tw_status.setText(getResources().getString(R.string.edit));
                social_network_account_id = String.valueOf(MainActivity.myProfile.getSocialAccounts().get(i).getAccountID());
            }
        }
        for (int i = 0; i < MainActivity.myProfile.getSocialAccounts().size(); i++) {
            if (MainActivity.myProfile.getSocialAccounts().get(i).getSocialNetworkType() == 15) {
                g_status.setText(getResources().getString(R.string.edit));
            }
        }


//        mLoginButton = (TwitterLoginButton) rootView.findViewById(R.id.twitter_login);
//        mLoginButton.setOnClickListener(new View.OnClickListener() {
        twitterLoginButton = (TwitterLoginButton) rootView.findViewById(R.id.twitter_login);
        twitterLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.onActivityResultType = 2;

            }
        });
        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Log.e("aa", "twitterLogin:success" + result);

                TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                TwitterAuthToken authToken = session.getAuthToken();
//                String token = authToken.token;
//                String secret = authToken.secret;

                Log.e("aa", "twitter token " + authToken.token);
                Log.e("aa", "twitter secret" + authToken.secret);

                if (tw_status.getText().toString().equals(getResources().getString(R.string.edit))) {
                    sendTwitterData(result.data, authToken, social_network_account_id);
                } else {
                    sendTwitterData(result.data, authToken, "add");
                }

            }

            @Override
            public void failure(TwitterException exception) {
                Log.e("aa", "twitterLogin:failure", exception);
                exception.printStackTrace();
            }
        });


        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope("email"),
                        new Scope("openid"),
                        new Scope("profile"),
                        new Scope("https://www.googleapis.com/auth/youtubepartner-channel-audit"),
                        new Scope("https://www.googleapis.com/auth/youtubepartner"),
                        new Scope("https://www.googleapis.com/auth/youtube"),
                        new Scope("https://www.googleapis.com/auth/youtube.force-ssl"))
                .requestServerAuthCode("206053753790-vkur2oakgdlkpl9l7sn1bep85aqblhup.apps.googleusercontent.com")
                .requestIdToken("206053753790-vkur2oakgdlkpl9l7sn1bep85aqblhup.apps.googleusercontent.com")
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(_utility.getCurrentActivity())
//                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        rootView.findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.onActivityResultType = 0;
                signIn();
            }
        });


        couponsCount = (TextView) rootView.findViewById(R.id.couponsNumber);
        user_name = (EditText) rootView.findViewById(R.id.user_name);
        first_name = (EditText) rootView.findViewById(R.id.first_name);
        full_name = (TextView) rootView.findViewById(R.id.full_name);
        last_name = (EditText) rootView.findViewById(R.id.last_name);
        mail = (EditText) rootView.findViewById(R.id.mail);
        imgv_pp = (ImageView) rootView.findViewById(R.id.imgv_pp);
        imgv_pp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.onActivityResultType = 0;
                Intent i = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
        birthdate = (EditText) rootView.findViewById(R.id.birthdate);
        birthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.birthdate = birthdate;
                DialogFragment newFragment = new SelectDateFragment();
                newFragment.show(getFragmentManager(), "DatePicker");
            }
        });


        phone = (EditText) rootView.findViewById(R.id.phone);
        mob = (EditText) rootView.findViewById(R.id.mob);
        address = (EditText) rootView.findViewById(R.id.address);
        city_name = (EditText) rootView.findViewById(R.id.city_name);
        pw = (EditText) rootView.findViewById(R.id.pw);
        pw_conf = (EditText) rootView.findViewById(R.id.pw_conf);
        country = (EditText) rootView.findViewById(R.id.country);
        lin_update = (LinearLayout) rootView.findViewById(R.id.lin_update);
        lin_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFieldsNotEmpty())
                    updateMyProfile();
                else
                    _utility.showMessage(getContext().getString(R.string.All_fields_required));
            }
        });


        couponsCount.setText(String.valueOf(MainActivity.myProfile.getCouponCount()));
        full_name.setText(MainActivity.myProfile.getFanFirstName());
        if (!MainActivity.myProfile.getFanUserName().equals("null"))
            user_name.setText(MainActivity.myProfile.getFanUserName());
//        allRequiredFields.add(user_name);
        first_name.setText(MainActivity.myProfile.getFanFirstName());
        allRequiredFields.add(first_name);
        last_name.setText(MainActivity.myProfile.getFanLastName());
        allRequiredFields.add(last_name);
        mail.setText(MainActivity.myProfile.getFanEMAil());
        allRequiredFields.add(mail);
        Picasso.with(getContext()).load(Const.imagesURL + "users/40x40/" + MainActivity.myProfile.getFanImage()).placeholder(R.drawable.ph_user).into(imgv_pp);
        birthdate.setText(MainActivity.myProfile.getFanBirthDate());
        allRequiredFields.add(birthdate);
        if (!MainActivity.myProfile.getFanPhone().equals("null"))
            phone.setText(MainActivity.myProfile.getFanPhone());
//        allRequiredFields.add(phone);
        if (!MainActivity.myProfile.getFanMob().equals("null"))
            mob.setText(MainActivity.myProfile.getFanMob());
//        allRequiredFields.add(mob);
        if (!MainActivity.myProfile.getFanAddress().equals("null"))
            address.setText(MainActivity.myProfile.getFanAddress());
//        allRequiredFields.add(address);
        if (!MainActivity.myProfile.getFanCity().equals("null"))
            city_name.setText(MainActivity.myProfile.getFanCity());
        city_name.setEnabled(false);
        allRequiredFields.add(city_name);
        country.setText(MainActivity.myProfile.getFanCountry());
        country.setEnabled(false);
        allRequiredFields.add(country);
        return rootView;
    }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, G_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("edit prof onActResult", "  >> " + requestCode);

        if (requestCode == G_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
            return;
        }

        if (resultCode == Activity.RESULT_OK && null != data) {
            switch (requestCode) {
                case 1://PICK_FROM_CAMERA
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    imgv_pp.setImageBitmap(photo);

                    break;

                case 2://RESULT_LOAD_IMAGE
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();

                    imgv_pp.setImageBitmap(BitmapFactory.decodeFile(picturePath));
//                    Bitmap bitmap = ((BitmapDrawable) imgv_pp.getDrawable()).getBitmap();
////                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.launcher_icons_512);
//                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 50 , stream); //compress to which format you want.
//                    byte[] byte_arr = stream.toByteArray();
//                    image_str = Base64.encodeToString(byte_arr, Base64.DEFAULT);
//                    image_str = "data:image/png;base64," + image_str;

////////////////////////////////////////////////////////////////////////////////////////////////////
                    Bitmap bm = BitmapFactory.decodeFile(picturePath);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
                    byte[] b = baos.toByteArray();
                    image_str = Base64.encodeToString(b, Base64.DEFAULT);
                    image_str = "data:image/png;base64," + image_str;


                    break;

                default:
                    break;
            }
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.e("Main activity", "handleSignInResult:" + result.isSuccess());
        Log.e("Main activity", "handleSignInResult status:" + result.getStatus());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            sendGoogleData(acct.getServerAuthCode());

            Log.e("Main activity", "getDisplayName:" + acct.getDisplayName() + "");
            Log.e("Main activity", "getFamilyName:" + acct.getFamilyName() + "");
            Log.e("Main activity", "getEmail:" + acct.getEmail());
            Log.e("Main activity", "getId:" + acct.getId());
            Log.e("Main activity", "getIdToken:" + acct.getIdToken());
            Log.e("Main activity", "getServerAuthCode:" + acct.getServerAuthCode());
            Log.e("Main activity", "getDisplayName:" + acct.getDisplayName());

//            mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
//            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
//            updateUI(false);
        }
    }


    private void sendTwitterData(TwitterSession data, TwitterAuthToken authToken, String type) {
        if (!_utility.isConnectingToInternet_ping()) {
            customLoading.hideProgress();
            MainActivity._utility.showMessage(getResources().getString(R.string.no_net));
            return;
        }

        try {
            requestType = "sendTwitterData";
            customLoading.showProgress(_utility.getCurrentActivity());
            JSONWebServices service = new JSONWebServices(EditAccountFragment.this);
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("mobile", "true"));
            nameValuePairs.add(new BasicNameValuePair("account_name", data.getUserName()));
            JSONObject oauth = new JSONObject();
            oauth.put("oauth_token", authToken.token);
            oauth.put("oauth_token_secret", authToken.secret);
            nameValuePairs.add(new BasicNameValuePair("oauth_values", oauth.toString()));
            nameValuePairs.add(new BasicNameValuePair("social_network_account_id", new StringBuilder().append(data.getUserId()).toString()));
            nameValuePairs.add(new BasicNameValuePair("social_network_type", "14"));
            nameValuePairs.add(new BasicNameValuePair("token", new StringBuilder().append(authToken.token).append(":").append(authToken.secret).append("@").append(getResources().getString(R.string.CONSUMER_KEY)).toString()));

            if (type.equals("add")) {
                call = service.addSocialAccount(nameValuePairs);
            } else {
                call = service.editSocialAccount(nameValuePairs, type);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateMyProfile() {
        if (!_utility.isConnectingToInternet_ping()) {
            customLoading.hideProgress();
            MainActivity._utility.showMessage(getResources().getString(R.string.no_net));
            return;
        }

        requestType = "updateMyProfile";
//        Log.e("Edit profile -- request", isFieldsNotEmpty() + "");
        customLoading.showProgress(_utility.getCurrentActivity());
        JSONWebServices service = new JSONWebServices(EditAccountFragment.this);
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("user_id", String.valueOf(MainActivity.currentFan.getFanID())));
        if (!user_name.getText().toString().equals("") && !user_name.getText().toString().isEmpty())
            nameValuePairs.add(new BasicNameValuePair("username", user_name.getText().toString()));
        nameValuePairs.add(new BasicNameValuePair("firstname", first_name.getText().toString()));
        nameValuePairs.add(new BasicNameValuePair("lastname", last_name.getText().toString()));
        nameValuePairs.add(new BasicNameValuePair("email", mail.getText().toString()));
        String webDateFormat = birthdate.getText().toString();

//        if (pw.getText().length() > 5 && pw_conf.getText().length() > 5
//                && pw.getText().toString().equals(pw_conf.getText().toString())) {
        nameValuePairs.add(new BasicNameValuePair("password", pw.getText().toString()));
        nameValuePairs.add(new BasicNameValuePair("password_confirmation", pw_conf.getText().toString()));
//        }

        if (!birthdate.getText().toString().contains("-"))
            try {
                String[] splitter = birthdate.getText().toString().split("/");
                webDateFormat = new StringBuilder().append(checkDigit(Integer.valueOf(splitter[2])))
                        .append("-").append(checkDigit(Integer.valueOf(splitter[0])))
                        .append("-").append(checkDigit(Integer.valueOf(splitter[1]))).toString();
            } catch (Exception e) {
                e.printStackTrace();
            }

        if (!birthdate.getText().toString().equals("0000-00-00") && !birthdate.getText().toString().equals(""))
            nameValuePairs.add(new BasicNameValuePair("birthday", webDateFormat));
        Log.e("0-0-birtdate", webDateFormat);

        if (!phone.getText().toString().equals("") && !phone.getText().toString().isEmpty())
            nameValuePairs.add(new BasicNameValuePair("telephone", phone.getText().toString()));
        if (!mob.getText().toString().equals("") && !mob.getText().toString().isEmpty())
            nameValuePairs.add(new BasicNameValuePair("mobile", mob.getText().toString()));
        if (!address.getText().toString().equals("") && !address.getText().toString().isEmpty())
            nameValuePairs.add(new BasicNameValuePair("address", address.getText().toString()));
        nameValuePairs.add(new BasicNameValuePair("city", String.valueOf(MainActivity.myProfile.getFanCityID())));
        nameValuePairs.add(new BasicNameValuePair("gender", MainActivity.myProfile.getFanGender()));

        if (!image_str.isEmpty())
            nameValuePairs.add(new BasicNameValuePair("picture", image_str));


        call = service.updateMyProfile(nameValuePairs);


    }

    private void sendGoogleData(String serverAuthCode) {
        if (!_utility.isConnectingToInternet_ping()) {
            customLoading.hideProgress();
            MainActivity._utility.showMessage(getResources().getString(R.string.no_net));
            return;
        }

        requestType = "sendGoogleData";
        customLoading.showProgress(_utility.getCurrentActivity());
        JSONWebServices service = new JSONWebServices(EditAccountFragment.this);
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("uid", String.valueOf(MainActivity.currentFan.getFanID())));
        nameValuePairs.add(new BasicNameValuePair("code", serverAuthCode));

        call = service.addSocialAccount_G(nameValuePairs);


    }

    private String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }

    private boolean isFieldsNotEmpty() {
        for (int i = 0; i < allRequiredFields.size(); i++) {
            if (allRequiredFields.get(i).getText().length() == 0)
                return false;
        }
        if (pw.getText().length() > 0 || pw_conf.getText().length() > 0)
            if (pw.getText().length() < 6 || pw_conf.getText().length() < 6
                    || !pw.getText().toString().equals(pw_conf.getText().toString())) {
                _utility.showMessage(getString(R.string.check_pass), true);
                return false;
            }

        return true;
    }

    void showDialog(final String message) {
        Log.e("edit acc show dialog", "begin transaction");
        customLoading.hideProgress();
        AlertDialog.Builder build = new AlertDialog.Builder(getContext());

        build.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(getContext().getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        if (!message.equals(addSocialAccountErr))

                            getActivity().onBackPressed();
                    }
                });
        AlertDialog alert = build.create();
        alert.show();
    }


    @Override
    public void PostBackExecutionJSON(JSONTokener Result) {
        try {
            call = null;

            Log.e("edit response", Result.toString() + "--");


            switch (requestType) {
                case "sendGoogleData":
                    if (ParseData.parseAddTwitterAccount(Result, _utility))
                        showDialog(getContext().getResources().getString(R.string.social_account_added));
                    else
                        showDialog(addSocialAccountErr);

                    /////////////////////////////////////////////////////////////////
//                    _utility.showMessage(getString(R.string.social_account_added));
//                else
//                    _utility.showMessage(getString(R.string.ws_err));

                    break;
                case "sendTwitterData":
                    if (ParseData.parseAddTwitterAccount(Result, _utility))
                        showDialog(getContext().getResources().getString(R.string.social_account_added));
                    else
                        showDialog(addSocialAccountErr);
//                    _utility.showMessage(getString(R.string.social_account_added));
//                else
//                    _utility.showMessage(getString(R.string.ws_err));

                    break;
                case "updateMyProfile":
                    ArrayList<String> result = ParseData.parseUpdateProfileResult(Result);
                    if (result == null || result.isEmpty())
                        _utility.showMessage(getString(R.string.ws_err));
                    else {
                        if (result.get(0).equals("success")) {
                            _utility.showMessage(getString(R.string.profile_updated));
                            _utility.getCurrentActivity().onBackPressed();
                        } else
                            showErrorDialog(result);
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            _utility.showMessage(getResources().getString(R.string.ws_err));

        } finally {
            customLoading.hideProgress();
        }
    }


    void showErrorDialog(ArrayList<String> result) {
        AlertDialog.Builder build = new AlertDialog.Builder(getContext());
        build.setMessage(result.get(1))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = build.create();
        alert.show();
    }
}

