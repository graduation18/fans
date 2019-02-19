package com.systemonline.fanscoupon;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.systemonline.fanscoupon.Base.BaseFragment;
import com.systemonline.fanscoupon.Helpers.Utility;
import com.systemonline.fanscoupon.brands_tabs.BrandsTab;
import com.systemonline.fanscoupon.coupon_tabs.CouponTab;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FragmentMap extends BaseFragment implements GoogleMap.OnMarkerClickListener {
    public static String mapMode = "driving";
    Utility utility;
    LatLng latlngPlace;
    LatLngBounds bounds;
    TextView brName, brAddress, telephone, city, tv_paths;
    Spinner spin_mode_2;
    Button btnAlterNxt, BtnAlterBefore;
    ArrayList<LatLng> points = null;
    PolylineOptions lineOptions = null;
    List<List<HashMap<String, String>>> possiblePaths;
    Polyline line;
    Intent i;
    ProgressDialog progDialog;
    ImageView transparent_img, tel_arrow;
    ImageView imgCall;
    ScrollView fr_map_scv;
    Marker tempMarker;
    private GoogleMap mMap;
    private int routesCount = 0, co = 0;
    private boolean cameraAllBranches = false;
//    private CustomLoading customLoading;

    public FragmentMap() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.brand_tab_single_branch, container, false);
        Log.e("0Fragment - Map", "0fn - onCreateView");
        fr_map_scv = (ScrollView) rootView.findViewById(R.id.fr_map_scv);
        utility = new Utility(getActivity());
//        customLoading = new CustomLoading(utility.getCurrentActivity());
        imgCall = (ImageView) rootView.findViewById(R.id.img_call);
        tv_paths = (TextView) rootView.findViewById(R.id.tv_pathNum);
        spin_mode_2 = (Spinner) rootView.findViewById(R.id.spin_mode_2);
        btnAlterNxt = (Button) rootView.findViewById(R.id.btn_alterNxt);
        BtnAlterBefore = (Button) rootView.findViewById(R.id.btn_alterBefore);
        btnAlterNxt.setEnabled(false);
        BtnAlterBefore.setEnabled(false);
        transparent_img = (ImageView) rootView.findViewById(R.id.transparent_img);
        progDialog = new ProgressDialog(getActivity());
        progDialog.setTitle(R.string.load);
        progDialog.setMessage(getResources().getString(R.string.fetch));
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(false);
        btnAlterNxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progDialog.show();
                if (co < routesCount - 1) {
                    co++;
                    line.remove();
                    fetchPointsAndDraw(possiblePaths.get(co));// btn nxt route
                    line = mMap.addPolyline(lineOptions);
                    progDialog.hide();
                    tv_paths.setText("  " + getResources().getString(R.string.path) + " " + (co + 1) + " " +
                            getResources().getString(R.string.of) + " " + routesCount + "  ");
                } else if (routesCount != 0) {
                    co = -1;
                    btnAlterNxt.performClick();
                }
            }
        });
        BtnAlterBefore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progDialog.show();
                if (co > 0) {
                    co--;
                    line.remove();
                    fetchPointsAndDraw(possiblePaths.get(co));// btn previous route
                    progDialog.hide();
                    tv_paths.setText("  " + getResources().getString(R.string.path) + " " + (co + 1) + " " +
                            getResources().getString(R.string.of) + " " + routesCount + "  ");
                    line = mMap.addPolyline(lineOptions);
                } else if (routesCount != 0) {
                    co = routesCount;
                    BtnAlterBefore.performClick();
                }
            }
        });
        imgCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!telephone.getText().toString().isEmpty())
                    callConfirmationDialog();
            }
        });
        brName = (TextView) rootView.findViewById(R.id.branch_name);
        brAddress = (TextView) rootView.findViewById(R.id.branch_address);
        telephone = (TextView) rootView.findViewById(R.id.tel);
//        county = (TextView) rootView.findViewById(R.id.branch_country);
        city = (TextView) rootView.findViewById(R.id.branch_city);
        try {
            getDataSetUi(null);//first enter

            utility.spinnerProperties(getResources().getStringArray(R.array.stAr_distance), spin_mode_2);
            spin_mode_2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    btnAlterNxt.setEnabled(false);
                    BtnAlterBefore.setEnabled(false);
                    progDialog.show();

                    if (utility.isConnectingToInternet_ping() && utility.checkGPS()) {
                        switch (position) {
                            case 0:
                                progDialog.hide();
                                break;

                            case 1://walking
                                mapMode = "walking";
                                if (line != null)
                                    line.remove();
                                drawPath(MainActivity.selectedBranchLatLng);//walking
                                break;

                            case 2://driving
                                if (line != null)
                                    line.remove();
                                mapMode = "driving";
                                drawPath(MainActivity.selectedBranchLatLng);//driving
                                break;

                            default:
                                break;
                        }
                        co = 0;
                    } else {
                        progDialog.hide();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            transparent_img.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    fr_map_scv.requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });
            setUpMapIfNeeded();

            mMap.setOnMarkerClickListener(this);
            mMap.setInfoWindowAdapter(new MyInfoWindowAdapter());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rootView;
    }

    void callConfirmationDialog() {
        AlertDialog.Builder build = new AlertDialog.Builder(utility.getCurrentActivity());
        build.setMessage(utility.getCurrentActivity().getResources().getString(R.string.call_conf))
                .setCancelable(false)
                .setPositiveButton(utility.getCurrentActivity().getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + telephone.getText()));
                        startActivity(callIntent);
                    }
                })
                .setNegativeButton(utility.getCurrentActivity().getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        build.show();
    }

    /**
     * set current branch info
     */
    private void getDataSetUi(LatLng latLngMarker) {
        Log.e("map data set ui", MainActivity.mapType);

        switch (MainActivity.mapType) {
            case "singleBranch":
                cameraAllBranches = false;

                Log.e("00case", "singleBranch");
                for (int i = 0; i < MainActivity.selectedCoupon.getCouponBrand().getBrandBranches().size(); i++) {
                    if (MainActivity.selectedCoupon.getCouponBrand().getBrandBranches().get(i).getBranchLatitude()
                            == MainActivity.selectedBranchLatLng.latitude &&
                            MainActivity.selectedCoupon.getCouponBrand().getBrandBranches().get(i).getBranchLongitude()
                                    == MainActivity.selectedBranchLatLng.longitude) {

                        brName.setText(MainActivity.selectedCoupon.getCouponBrand().getBrandBranches().get(i).getBranchName());
                        brAddress.setText(MainActivity.selectedCoupon.getCouponBrand().getBrandBranches().get(i).getBranchAddress());
                        city.setText(MainActivity.selectedCoupon.getCouponBrand().getBrandBranches().get(i).getBranchCity());
                        telephone.setText(MainActivity.selectedCoupon.getCouponBrand().getBrandBranches().get(i).getBranchPhone());
                        if (MainActivity.selectedCoupon.getCouponBrand().getBrandBranches().get(i).getBranchPhone().equals(getString(R.string.no_phone)))
                            imgCall.setEnabled(false);
                        break;
                    }
                }
                break;
            case "brandBranch":
                cameraAllBranches = false;

                Log.e("00case", "brandBranch");
                for (int i = 0; i < BrandsTab.selectedBrand.getBrandBranches().size(); i++) {
                    if (BrandsTab.selectedBrand.getBrandBranches().get(i).getBranchLatitude()
                            == MainActivity.selectedBranchLatLng.latitude &&
                            BrandsTab.selectedBrand.getBrandBranches().get(i).getBranchLongitude()
                                    == MainActivity.selectedBranchLatLng.longitude) {

                        brName.setText(BrandsTab.selectedBrand.getBrandBranches().get(i).getBranchName());
                        brAddress.setText(BrandsTab.selectedBrand.getBrandBranches().get(i).getBranchAddress());
                        city.setText(MainActivity.selectedCoupon.getCouponBrand().getBrandBranches().get(i).getBranchCity());
                        telephone.setText(BrandsTab.selectedBrand.getBrandBranches().get(i).getBranchPhone());
                        if (BrandsTab.selectedBrand.getBrandBranches().get(i).getBranchPhone().equals(getString(R.string.no_phone)))
                            imgCall.setEnabled(false);

//                        Log.e("00.0...",MainActivity.selectedBrand.getBrandBranches().get(i).getBranchPhone()+",,,,");

                        break;
                    }
                }
                break;
            case "allBranches":
                cameraAllBranches = true;

                Log.e("00case", "allBranches");
                if (latLngMarker == null) {//first entry

//                    brName.setText(CouponTab.couponsBranches.get(0).getBranchName());
//                    brAddress.setText(CouponTab.couponsBranches.get(0).getBranchAddress());
//                    city.setText(CouponTab.couponsBranches.get(0).getBranchCity());
//                    telephone.setText(CouponTab.couponsBranches.get(0).getBranchPhone());
//                    Log.e("00.0.", CouponTab.couponsBranches.get(0).getBranchPhone() + ",,,,");
                } else { //marker clicked
                    for (int i = 0; i < CouponTab.couponsBranches.size(); i++)
                        if (CouponTab.couponsBranches.get(i).getBranchLatitude() == latLngMarker.latitude && CouponTab.couponsBranches.get(i).getBranchLongitude() == latLngMarker.longitude) {
                            brName.setText(CouponTab.couponsBranches.get(i).getBranchName());
                            brAddress.setText(CouponTab.couponsBranches.get(i).getBranchAddress());
                            city.setText(CouponTab.couponsBranches.get(i).getBranchCity());
                            telephone.setText(CouponTab.couponsBranches.get(i).getBranchPhone());
                            Log.e("00.0..", CouponTab.couponsBranches.get(i).getBranchPhone() + ",,,,");
                            break;
                        }
                }
                break;
        }

    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

        progDialog.dismiss();
    }

    /**
     * add branches marker on the map
     */
    private void addMarkersOnMap(LatLng latLngMarker) {
        Log.e("0Fragment - Map", "0fn - addMarkersOnMap");
        Log.e("map add markers", MainActivity.mapType);

        if (MainActivity.mapType.equals("singleBranch")) {
            mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin))
                    .position(MainActivity.selectedBranchLatLng)
            );
            drawPath(MainActivity.selectedBranchLatLng);//add markers single branch

        } else if (MainActivity.mapType.equals("brandBranch")) {
            mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin))
                    .position(MainActivity.selectedBranchLatLng)
            );
            drawPath(MainActivity.selectedBranchLatLng);//add markers single branch
        } else {
            int branchesSize = CouponTab.couponsBranches.size();
            for (int i = 0; i < branchesSize; i++) {
                latlngPlace = new LatLng(
                        CouponTab.couponsBranches.get(i).getBranchLatitude(),
                        CouponTab.couponsBranches.get(i).getBranchLongitude());
                if (latLngMarker != null && (latLngMarker.latitude == latlngPlace.latitude && latLngMarker.longitude == latlngPlace.longitude)) {
                    mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin))
                            .position(latlngPlace)
                    ).showInfoWindow();
                } else {
                    mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin))
                            .position(latlngPlace)
                    );
                }
            }

            if (latLngMarker == null) {
//                drawPath(new LatLng(CouponTab.couponsBranches.get(0).getBranchLatitude(),
//                        CouponTab.couponsBranches.get(0).getBranchLongitude()));//add markers all branches
            } else {
                drawPath(latLngMarker);
            }
        }
    }

    /**
     * check if google map setup
     */
    private void setUpMapIfNeeded() {
        Log.e("0Fragment - Map", "0fn - setUpMapIfNeeded");
        if (mMap == null) {
            SupportMapFragment mMapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
            mMap = mMapFragment.getMap();
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.setOnMarkerClickListener(this);
                setUpMap(null);//first entry
            }
        }
    }

    /**
     * set up google map
     */
    private void setUpMap(LatLng latlngMarker) {
        Log.e("0Fragment - Map", "0fn - setUpMap");

        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mark_red))
                .position(new LatLng(MainActivity.latitudeSrc, MainActivity.longitudeSrc)).title(getResources().getString(R.string.me)));

        mMap.addCircle(new CircleOptions()
                .center(new LatLng(MainActivity.latitudeSrc, MainActivity.longitudeSrc))
                .radius(30)
                .strokeColor(Color.RED)
                .fillColor(Color.CYAN));
        LatLng branchLatLng = null;

        Log.e("map bounds", MainActivity.mapType);

        switch (MainActivity.mapType) {
            case "singleBranch":
                branchLatLng = MainActivity.selectedBranchLatLng;
                break;
            case "brandBranch":
                branchLatLng = MainActivity.selectedBranchLatLng;
                break;
            case "allBranches":
                if (latlngMarker == null) {
//                    getallBranchesBounds();
                    branchLatLng = new LatLng(CouponTab.couponsBranches.get(0).getBranchLatitude(),
                            CouponTab.couponsBranches.get(0).getBranchLongitude());
                } else
                    branchLatLng = latlngMarker;
                break;
        }

        bounds = new LatLngBounds.Builder()
                .include(new LatLng(MainActivity.latitudeSrc, MainActivity.longitudeSrc))
                .include(branchLatLng)
                .build();


        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
//                if (cameraAllBranches) {
//                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
//
//                    for (int p = 0; p < CouponTab.couponsBranches.size(); p++) {
//                        builder.include(new LatLng(CouponTab.couponsBranches.get(p).getBranchLatitude(),
//                                CouponTab.couponsBranches.get(p).getBranchLongitude()));
//                    }
//                    bounds = builder.build();
//                }
                if (cameraAllBranches) {

                    LatLng alex = new LatLng(31.205753, 29.924526);
                    CameraPosition camPos = new CameraPosition.Builder().target(alex).zoom(10).build();
                    CameraUpdate cam = CameraUpdateFactory.newCameraPosition(camPos);
                    mMap.animateCamera(cam);
                } else
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));


            }
        });


        addMarkersOnMap(latlngMarker);
    }

    private void getAllBranchesBounds() {

    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.e("0Fragment - Map", "0fn - onMarkerClick");
        tempMarker = marker;
        MainActivity.selectedBranchLatLng = marker.getPosition();

        progDialog.show();

        getDataSetUi(marker.getPosition());//marker click
        cameraAllBranches = false;

        mMap.clear();
        setUpMap(marker.getPosition());//marker click

        return false;
    }

    /**
     * draw path between user location and branch location on the map
     *
     * @param dest
     */
    public void drawPath(LatLng dest) {
        Log.e("0Fragment - Map", "0fn - drawPath");
        if (dest == null) {
            progDialog.hide();
            return;
        }

        LatLng origin = new LatLng(MainActivity.latitudeSrc, MainActivity.longitudeSrc);
        String url = getDirectionsUrl(origin, dest);
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);

    }

    /**
     * get possible paths
     *
     * @param origin
     * @param dest
     * @return
     */
    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        String sensor = "mode=" + mapMode + "&alternatives=true";

        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        String output = "json?key=" + getResources().getString(R.string.google_maps_key);

        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "&" + parameters;
//https://maps.googleapis.com/maps/api/directions/json?key=AIzaSyD4LTzZBmDwyMo-7Wrwqp87BN6eQ8BiN9k&origin=31.2273548,29.9587404&destination=31.2101985,29.9426771&mode=driving&alternatives=true

        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            //Log.e("Network exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /**
     * fetch path points and draw it on map
     *
     * @param path
     */
    public void fetchPointsAndDraw(List<HashMap<String, String>> path) {
        Log.e("0Fragment - Map", "0fn - fetchPointsAndDraw");

        points = new ArrayList<LatLng>();
        lineOptions = new PolylineOptions();

        for (int j = 0; j < path.size(); j++) {
            HashMap<String, String> point = path.get(j);

            double lat = Double.parseDouble(point.get("lat"));
            double lng = Double.parseDouble(point.get("lng"));
            LatLng position = new LatLng(lat, lng);

            points.add(position);
        }

        tv_paths.setText("  " + getResources().getString(R.string.path) +
                " 1 " + getResources().getString(R.string.of) + routesCount + "  ");

        lineOptions.addAll(points);
        lineOptions.width(10);
        lineOptions.color(Color.RED);

        progDialog.hide();
        btnAlterNxt.setEnabled(true);
        BtnAlterBefore.setEnabled(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            parserTask.execute(result);
        }
    }

    /*
    at the implementation of Fragment, you'll see that when moving to the detached state,
     it'll reset its internal state. However, it doesn't reset mChildFragmentManager
      (this is a bug in the current version of the support library).
       This causes it to not reattach the child fragment manager when the Fragment is reattached,
        causing the exception Activity has been destroyed
     */

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            try {
                possiblePaths = result;
                routesCount = result.size();

                if (routesCount > 1) {
                    btnAlterNxt.setVisibility(View.VISIBLE);
                    BtnAlterBefore.setVisibility(View.VISIBLE);
                    tv_paths.setVisibility(View.VISIBLE);
                } else {
                    btnAlterNxt.setVisibility(View.INVISIBLE);
                    BtnAlterBefore.setVisibility(View.INVISIBLE);
                    tv_paths.setVisibility(View.INVISIBLE);
                }

                List<HashMap<String, String>> path = result.get(0);

                fetchPointsAndDraw(path);// draw first route

                line = mMap.addPolyline(lineOptions);
            } catch (Exception e) {
            }

            progDialog.hide();

        }

    }


    class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private final View myContentsView;

        MyInfoWindowAdapter() {
            myContentsView = getActivity().getLayoutInflater().inflate(R.layout.ballon, null);
        }

        @Override
        public View getInfoContents(Marker marker) {

            TextView brand_name = ((TextView) myContentsView.findViewById(R.id.brand_name));
            TextView off_name = ((TextView) myContentsView.findViewById(R.id.off_name));

            for (int i = 0; i < CouponTab.couponsBranches.size(); i++) {

                if (CouponTab.couponsBranches.get(i).getBranchLatitude() == marker.getPosition().latitude
                        && CouponTab.couponsBranches.get(i).getBranchLongitude() == marker.getPosition().longitude) {
                    brand_name.setText(CouponTab.couponsBranches.get(i).getBranchName());
                    off_name.setText(CouponTab.couponsBranches.get(i).getOffName());
                    Log.e("Map - OfferName", CouponTab.couponsBranches.get(i).getOffName() + "..s");
                    break;
                }
            }
            return myContentsView;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            // TODO Auto-generated method stub
            Log.e("00mrk--00", "infoWindow");
            return null;
        }
    }

}