package com.systemonline.fanscoupon;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.systemonline.fanscoupon.Base.BaseFragment;
import com.systemonline.fanscoupon.Helpers.Utility;
import com.systemonline.fanscoupon.Model.CustomLoading;
import com.systemonline.fanscoupon.WebServices.JSONAsync;
import com.systemonline.fanscoupon.WebServices.JSONWebServices;
import com.systemonline.fanscoupon.WebServices.ParseData;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * Created by Online108 on 11/20/2016.
 */

public class RegistrationFragment extends BaseFragment {
    public EditText first_name, last_name, mail, pw, pw_conf, birthdate;
    BetterSpinner country;
    Utility _utility;
    LinearLayout lin_signUp;
    JSONAsync call;
    ArrayList<EditText> allFields = new ArrayList<>();
    RadioGroup rad_grp_gender;
    RadioButton radBtn_male, radBtn_female;
    Locale myLocale;
    private String requestType;
    private CustomLoading customLoading;
    private String country_code = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.registration, null);
        _utility = new Utility(getContext());
        customLoading = new CustomLoading(_utility.getCurrentActivity());
        getActivity().setTitle(getString(R.string.sign_up));

        first_name = (EditText) rootView.findViewById(R.id.first_name);
        last_name = (EditText) rootView.findViewById(R.id.last_name);
        mail = (EditText) rootView.findViewById(R.id.mail);
        birthdate = (EditText) rootView.findViewById(R.id.birthdate);
        birthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.birthdate = birthdate;
                DialogFragment newFragment = new SelectDateFragment();
                newFragment.show(getFragmentManager(), "DatePicker");
            }
        });

        pw = (EditText) rootView.findViewById(R.id.pw);
        pw_conf = (EditText) rootView.findViewById(R.id.pw_conf);
        country = (BetterSpinner) rootView.findViewById(R.id.country);
        lin_signUp = (LinearLayout) rootView.findViewById(R.id.lin_update);
        lin_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFieldsNotEmpty())
                    createMyProfile();
                else
                    _utility.showMessage(getContext().getString(R.string.All_fields_required));
            }
        });

        allFields.add(first_name);
        allFields.add(last_name);
        allFields.add(mail);
        allFields.add(birthdate);
        allFields.add(pw);
        allFields.add(pw_conf);

        if (MainActivity.sharedPreferences.getString("lang", "null").equals("ar"))
            myLocale = new Locale("ar");
        else
            myLocale = new Locale("en");


        Locale[] locale = Locale.getAvailableLocales();
        ArrayList<String> countries = new ArrayList<String>();
        String country_string;


        for (Locale loc : locale) {
//            Log.e("locale", loc.getDisplayLanguage(loc) + " ++ ");

            country_string = loc.getDisplayCountry(myLocale);
            if (country_string.length() > 0 && !countries.contains(country_string)) {
                countries.add(country_string);
            }

        }
        Collections.sort(countries, String.CASE_INSENSITIVE_ORDER);

        String[] COUNTRIES = new String[countries.size()];
        COUNTRIES = countries.toArray(COUNTRIES);

        ArrayAdapter<String> adaptera = new ArrayAdapter<String>(_utility.getCurrentActivity(),
                android.R.layout.simple_dropdown_item_1line, COUNTRIES);
        country.setAdapter(adaptera);

        final String[] finalCOUNTRIES = COUNTRIES;
        country.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("0spin.clicked", finalCOUNTRIES[i]);
                Log.e("0spin.clicked iso", getCountryCode(finalCOUNTRIES[i]) + "+++");
                country_code = getCountryCode(finalCOUNTRIES[i]);
            }
        });


        rad_grp_gender = (RadioGroup) rootView.findViewById(R.id.rad_grp_gender);
        radBtn_male = (RadioButton) rootView.findViewById(R.id.radBtn_male);
        radBtn_female = (RadioButton) rootView.findViewById(R.id.radBtn_female);


        return rootView;
    }


    public String getCountryCode(String countryName) {

        // Get all country codes in a string array.
        String[] isoCountryCodes = Locale.getISOCountries();
        Map<String, String> countryMap = new HashMap<>();

        // Iterate through all country codes:
        for (String code : isoCountryCodes) {
            // Create a locale using each country code
            Locale locale = new Locale("", code);
            // Get country name for each code.
            String name = locale.getDisplayCountry(myLocale);
            // Map all country names and codes in key - value pairs.
            countryMap.put(name, code);
        }
        // Get the country code for the given country name using the map.
        // Here you will need some validation or better yet
        // a list of countries to give to user to choose from.
        String countryCode = countryMap.get(countryName); // "NL" for Netherlands.

        return countryCode;

    }

    private void createMyProfile() {
        if (!_utility.isConnectingToInternet_ping()) {
            customLoading.hideProgress();
            MainActivity._utility.showMessage(getResources().getString(R.string.no_net));
            return;
        }

        requestType = "createMyProfile";
//        Log.e("Edit profile -- request", isFieldsNotEmpty() + "");
        customLoading.showProgress(_utility.getCurrentActivity());
        JSONWebServices service = new JSONWebServices(RegistrationFragment.this);
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("firstname", first_name.getText().toString()));
        nameValuePairs.add(new BasicNameValuePair("lastname", last_name.getText().toString()));
        nameValuePairs.add(new BasicNameValuePair("email", mail.getText().toString()));
        nameValuePairs.add(new BasicNameValuePair("password", pw.getText().toString()));
        nameValuePairs.add(new BasicNameValuePair("country", country_code));

        String webDateFormat = birthdate.getText().toString();
        if (!birthdate.getText().toString().contains("-"))
            try {
                String[] splitter = birthdate.getText().toString().split("/");
                webDateFormat = new StringBuilder().append(checkDigit(Integer.valueOf(splitter[2])))
                        .append("-").append(checkDigit(Integer.valueOf(splitter[0])))
                        .append("-").append(checkDigit(Integer.valueOf(splitter[1]))).toString();
            } catch (Exception e) {
                e.printStackTrace();
            }

//        if (!birthdate.getText().toString().equals("0000-00-00") && !birthdate.getText().toString().equals("null"))
        nameValuePairs.add(new BasicNameValuePair("birthday", webDateFormat));
        Log.e("0-0-birtdate", webDateFormat);

        if (rad_grp_gender.getCheckedRadioButtonId() == radBtn_male.getId())
            nameValuePairs.add(new BasicNameValuePair("gender", "7"));
        else
            nameValuePairs.add(new BasicNameValuePair("gender", "8"));

        call = service.createMyProfile(nameValuePairs);


    }

    private String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }

    private boolean isFieldsNotEmpty() {
        for (int i = 0; i < allFields.size(); i++) {
            if (allFields.get(i).getText().length() == 0)
                return false;
        }

        if (!radBtn_male.isChecked() && !radBtn_female.isChecked())
            return false;

        if (country_code.isEmpty())
            return false;

        if (pw.getText().length() < 6 || pw_conf.getText().length() < 6
                || !pw.getText().toString().equals(pw_conf.getText().toString())) {
            _utility.showMessage(getString(R.string.check_pass), true);
            return false;
        }

        return true;
    }

    void showDialog() {
        Log.e("edit acc show dialog", "begin transaction");
        customLoading.hideProgress();
        AlertDialog.Builder build = new AlertDialog.Builder(getContext());

        build.setMessage(getContext().getResources().getString(R.string.social_account_added))
                .setCancelable(false)
                .setPositiveButton(getContext().getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
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

            Log.e("registration response", Result.toString() + "--");

            ArrayList<String> result = ParseData.parseRegistrationResult(Result);
            if (result == null || result.isEmpty())
                _utility.showMessage(getString(R.string.ws_err));
            else {
                if (result.get(0).equals("success")) {
//                    _utility.showMessage(getString(R.string.you_fanz_user));
                    result.add(_utility.getCurrentActivity().getString(R.string.mail_sent));
                }
//                else
                showErrorDialog(result);
            }

        } catch (Exception e) {
            e.printStackTrace();
            _utility.showMessage(getResources().getString(R.string.ws_err));

        } finally {
            customLoading.hideProgress();
        }
    }


    void showErrorDialog(final ArrayList<String> result) {
        AlertDialog.Builder build = new AlertDialog.Builder(getContext());
        build.setMessage(result.get(1))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        if (result.get(0).equals("success"))
                            _utility.getCurrentActivity().onBackPressed();
                    }
                });
        AlertDialog alert = build.create();
        alert.show();
    }
}

