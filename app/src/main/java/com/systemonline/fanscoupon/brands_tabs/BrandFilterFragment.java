package com.systemonline.fanscoupon.brands_tabs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.systemonline.fanscoupon.Base.BaseFragment;
import com.systemonline.fanscoupon.Helpers.Utility;
import com.systemonline.fanscoupon.Model.BrandFilter;
import com.systemonline.fanscoupon.Model.CustomLoading;
import com.systemonline.fanscoupon.R;
import com.systemonline.fanscoupon.WebServices.JSONAsync;
import com.systemonline.fanscoupon.WebServices.JSONWebServices;
import com.systemonline.fanscoupon.WebServices.ParseData;

import org.json.JSONTokener;

import java.util.ArrayList;

public class BrandFilterFragment extends BaseFragment {

    Spinner categoriesSpinner, citiesSpinner;
    TextView saveFilter, cancelFilter;
    Utility _utility;
    JSONAsync call;
    private CustomLoading customLoading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.brand_filter_fragment, null);

        _utility = new Utility(getContext());
        customLoading = new CustomLoading(_utility.getCurrentActivity());

        categoriesSpinner = (Spinner) rootView.findViewById(R.id.categories_spinner);
        citiesSpinner = (Spinner) rootView.findViewById(R.id.cities_spinner);
        saveFilter = (TextView) rootView.findViewById(R.id.btn_save_filter);
        saveFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.e("brand filter save", "btn ");
                BrandsTab.brandFilters = new ArrayList<>();
                BrandFilter brandFilter;
                if (categoriesSpinner.getSelectedItemPosition() != 0) {
                    brandFilter = new BrandFilter("category_id", String.valueOf(BrandsTab.brandAllFilters.get(0).get(categoriesSpinner.getSelectedItemPosition()).getFilterID() - 1));
                    BrandsTab.brandFilters.add(brandFilter);
                }
                if (citiesSpinner.getSelectedItemPosition() != 0) {
                    brandFilter = new BrandFilter("city_id", String.valueOf(BrandsTab.brandAllFilters.get(1).get(citiesSpinner.getSelectedItemPosition()).getFilterID() - 1));
                    BrandsTab.brandFilters.add(brandFilter);
                }
                BrandsTab.allBrands = null;
                getActivity().onBackPressed();
            }
        });
        cancelFilter = (TextView) rootView.findViewById(R.id.btn_cancel_filter);
        cancelFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        if (!_utility.isConnectingToInternet_ping()) {
            saveFilter.setEnabled(false);
        }

        if (BrandsTab.brandAllFilters.isEmpty()) {
            if (_utility.isConnectingToInternet_ping()) {
                customLoading.showProgress(_utility.getCurrentActivity());

                JSONWebServices service = new JSONWebServices(BrandFilterFragment.this);
                call = service.getBrandFilters();
            } else
                Toast.makeText(getContext(), getResources().getString(R.string.no_net), Toast.LENGTH_LONG).show();

        } else {
            loadSavedFilter();
        }
        return rootView;
    }

    private void loadSavedFilter() {
        String[] categoriesNames = new String[BrandsTab.brandAllFilters.get(0).size() + 1];
        categoriesNames[0] = getString(R.string.All);
        for (int i = 0; i < BrandsTab.brandAllFilters.get(0).size(); i++) {
            categoriesNames[i + 1] = BrandsTab.brandAllFilters.get(0).get(i).getFilterName();
        }
        _utility.spinnerProperties(categoriesNames, categoriesSpinner);

        String[] citiesNames = new String[BrandsTab.brandAllFilters.get(1).size() + 1];
        citiesNames[0] = getString(R.string.All);
        for (int i = 0; i < BrandsTab.brandAllFilters.get(1).size(); i++) {
            citiesNames[i + 1] = BrandsTab.brandAllFilters.get(1).get(i).getFilterName();
        }
        _utility.spinnerProperties(citiesNames, citiesSpinner);

//        setLastFilterChoice();
    }

    void setLastFilterChoice() {
        if (!BrandsTab.brandFilters.isEmpty()) {
            categoriesSpinner.setSelection(Integer.parseInt(BrandsTab.brandFilters.get(0).getValue()));
            if (BrandsTab.brandFilters.size() > 1)
                citiesSpinner.setSelection(Integer.parseInt(BrandsTab.brandFilters.get(1).getValue()));

        }
    }

    @Override
    public void PostBackExecutionJSON(JSONTokener Result) {
        try {
            BrandsTab.brandAllFilters = ParseData.parseBrandFilters(Result);
//
            if (BrandsTab.brandAllFilters != null) {
                loadSavedFilter();

                customLoading.hideProgress();
            } else {
                Toast.makeText(_utility.GetAppContext(), getResources().getString(R.string.ws_err), Toast.LENGTH_LONG).show();
                customLoading.hideProgress();
                saveFilter.setEnabled(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(_utility.GetAppContext(), getResources().getString(R.string.ws_err), Toast.LENGTH_LONG).show();
            saveFilter.setEnabled(false);
        } finally {
            customLoading.hideProgress();
            call = null;
        }
    }


}