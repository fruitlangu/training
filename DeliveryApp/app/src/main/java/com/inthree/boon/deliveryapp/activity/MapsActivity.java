package com.inthree.boon.deliveryapp.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.inthree.boon.deliveryapp.LocationUtils.LocationHelper;
import com.inthree.boon.deliveryapp.R;
import com.inthree.boon.deliveryapp.app.AppController;
import com.inthree.boon.deliveryapp.app.Constants;
import com.inthree.boon.deliveryapp.model.SavedAddress;
import com.inthree.boon.deliveryapp.utils.GPSTracker;
import com.inthree.boon.deliveryapp.views.DelayAutoCompleteTextView;
import com.inthree.boon.deliveryapp.interfaces.*;

import java.util.ArrayList;
import java.util.List;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, View.OnClickListener, SavedPlaceListener {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    GPSTracker gps;
    Marker marker;


    /**
     * Get the longititude
     */
    double lang;

    /**
     * Get the latitude
     */
    double lati;

    /**
     * Get the icons by shufling the image view
     */
    Menu mapMenu;

    /**
     * This is for change the image
     */
    static boolean isPressed = false;

    /**
     * Change the menu item color
     */

    private boolean mIsConnected;

    TextView ok;

    ImageView mapView;

    /**
     * location helper
     */
    LocationHelper locationHelper;

    /**
     * Search the location as user wish to search
     */
    private DelayAutoCompleteTextView search;

    /**
     * Trigger the button which is search  the location by user
     */
    private Button searchBtn;

    /**
     * Integer thresh hold
     */
    private Integer THRESHOLD = 2;

    /**
     * private clear the text of automatic textview
     */
    private ImageView clear;

    /**
     * Menu for toggle the street view or satellite
     */

    LinearLayout mParent;
    private RecyclerView mRecyclerView;
    LinearLayoutManager llm;

    // PlaceSavedAdapter mSavedAdapter;
    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(
            new LatLng(-0, 0), new LatLng(0, 0));

    EditText mSearchEdittext;
    ImageView mClear;

    SupportMapFragment mapFragment;

    /**
     * Check whether deliver or undelivery based on which is to show the location
     */
    private String shipOrderValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        FragmentManager manager = this.getSupportFragmentManager();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mSearchEdittext = (EditText) findViewById(R.id.search_et);
        mClear = (ImageView) findViewById(R.id.clear);
        mClear.setOnClickListener(this);

        mapView = (ImageView) findViewById(R.id.view);
        ok = (TextView) findViewById(R.id.ok);
        setSupportActionBar(toolbar);

        Intent getMapShipValue = getIntent();
        shipOrderValue = getMapShipValue.getStringExtra("MapshipAddValue");


        ok.setOnClickListener(this);
        mapView.setOnClickListener(this);


        locationHelper = new LocationHelper(this);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        mapFragment = (SupportMapFragment) manager
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        initViews();

        gps = new GPSTracker(MapsActivity.this, this);
        double curlat = gps.getLatitude();
        double curlon = gps.getLongitude();
        final LatLng currentpos = new LatLng(curlat, curlon);

        if (shipOrderValue.equalsIgnoreCase("delivery")) {

            double lat = Double.longBitsToDouble(AppController.getLongPreference(MapsActivity.this, Constants.LATITUDE, -1));
            double lon = Double.longBitsToDouble(AppController.getLongPreference(MapsActivity.this, Constants.LONGITUDE, -1));
            String latis = String.valueOf(lat);
            String longi = String.valueOf(lon);


            /**
             * Get the current location
             */
            double curLat = Double.longBitsToDouble(AppController.getLongPreference(MapsActivity.this, Constants.CUR_LATITUDE, -1));
            double curLon = Double.longBitsToDouble(AppController.getLongPreference(MapsActivity.this, Constants.CUR_LONGITUDE, -1));


            if (latis != null && longi != null && !Double.isNaN(lat) && !Double.isNaN(lon)) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 17.0f));
                getAddress(lat, lon);
            } else {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(curLat, curLon), 17.0f));
                getAddress(curLat, curLon);
            }
        } else if (shipOrderValue.equalsIgnoreCase("undelivery")) {
            double lat = Double.longBitsToDouble(AppController.getLongPreference(MapsActivity.this, Constants.UN_DEL_LATITUDE, -1));
            double lon = Double.longBitsToDouble(AppController.getLongPreference(MapsActivity.this, Constants.UN_DEL_LONGITUDE,
                    -1));
            String latis = String.valueOf(lat);
            String longi = String.valueOf(lon);


            /**
             * Get the current location
             */
            double curLat = Double.longBitsToDouble(AppController.getLongPreference(MapsActivity.this, Constants.UN_DEL_CUR_LATITUDE, -1));
            double curLon = Double.longBitsToDouble(AppController.getLongPreference(MapsActivity.this, Constants.UN_DEL_CUR_LONGITUDE, -1));


            if (latis != null && longi != null && !Double.isNaN(lat) && !Double.isNaN(lon)) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 17.0f));
                getAddress(lat, lon);
            } else {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(curLat, curLon), 17.0f));
                getAddress(curLat, curLon);
            }
        }


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();

                // Setting the position for the marker
                markerOptions.position(latLng);

                // Setting the title for the marker.
                // This will be displayed on taping the marker
                // markerOptions.title(latLng.latitude + " : " + latLng.longitude);

                lang = latLng.latitude;
                lati = latLng.longitude;

                if (shipOrderValue.equalsIgnoreCase("delivery")) {

                    /**
                     * Store the lat and lang in shared preference
                     */
                    AppController.setLongPreference(MapsActivity.this, Constants.LATITUDE, Double.doubleToRawLongBits(latLng
                            .latitude));
                    AppController.setLongPreference(MapsActivity.this, Constants.LONGITUDE, Double.doubleToRawLongBits(latLng.longitude));
                }else if (shipOrderValue.equalsIgnoreCase("undelivery")) {
                    /**
                     * Store the lat and lang in shared preference
                     */
                    AppController.setLongPreference(MapsActivity.this, Constants.UN_DEL_LATITUDE, Double.doubleToRawLongBits(latLng
                            .latitude));
                    AppController.setLongPreference(MapsActivity.this, Constants.UN_DEL_LONGITUDE, Double.doubleToRawLongBits(latLng.longitude));
                }

                // Clears the previously touched position
                mMap.clear();

                // Animating to the touched position
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                // Placing a marker on the touched position
                mMap.addMarker(markerOptions);
                getAddress(lang, lati);
            }
        });
    }


    /**
     * Get the complete addresskyc of the location
     */
    /**
     * Get the current location of the addresskyc initialization
     */
    public void getAddress(double latitude, double longitude) {
        Address locationAddress;

        locationAddress = locationHelper.getAddress(latitude, longitude);

        if (locationAddress != null) {
            String address = locationAddress.getAddressLine(0);
            String address1 = locationAddress.getAddressLine(1);
            String city = locationAddress.getLocality();
            String state = locationAddress.getAdminArea();
            String country = locationAddress.getCountryName();
            String postalCode = locationAddress.getPostalCode();

            String currentLocation;

            if (!TextUtils.isEmpty(address)) {
                currentLocation = address;

                if (!TextUtils.isEmpty(address1))
                    currentLocation += "," + address1;

                if (!TextUtils.isEmpty(city)) {
                    currentLocation += "," + city;

                    if (!TextUtils.isEmpty(postalCode))
                        currentLocation += " - " + postalCode;
                } else {
                    if (!TextUtils.isEmpty(postalCode))
                        currentLocation += "," + postalCode;
                }

                if (!TextUtils.isEmpty(state))
                    currentLocation += "," + state;

                if (!TextUtils.isEmpty(country))
                    currentLocation += "," + country;

                mMap.addMarker(
                        new MarkerOptions()
                                .position(new LatLng(latitude, longitude))
                                .draggable(true)
                                .icon(BitmapDescriptorFactory
                                        .defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                                .title(currentLocation))
                        .showInfoWindow();
            }
        } else {
            //  Toast.makeText(this, "unable to get the location", Toast.LENGTH_SHORT).show();
        }
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        if (shipOrderValue.equalsIgnoreCase("delivery")) {
            /**
             * Get the current location
             */
            double curLat = Double.longBitsToDouble(AppController.getLongPreference(MapsActivity.this, Constants.CUR_LATITUDE, -1));
            double curLon = Double.longBitsToDouble(AppController.getLongPreference(MapsActivity.this, Constants.CUR_LONGITUDE, -1));
            //Place current location marker
            LatLng latLng = new LatLng(curLat, curLon);
            MarkerOptions markerOptions = new MarkerOptions();

            double lat = Double.longBitsToDouble(AppController.getLongPreference(MapsActivity.this, Constants.LATITUDE, -1));
            double lon = Double.longBitsToDouble(AppController.getLongPreference(MapsActivity.this, Constants.LONGITUDE, -1));
            String lati = String.valueOf(lat);
            String longi = String.valueOf(lon);


            if (lati != null && longi != null && !Double.isNaN(lat) && !Double.isNaN(lon)) {
           /* Toast.makeText(MapsActivity.this, lat + "" + lat, Toast.LENGTH_LONG).show();*/
                LatLng curLatLng = new LatLng(lat, lon);
                markerOptions.position(curLatLng);
                markerOptions.title("Current Position");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                mCurrLocationMarker = mMap.addMarker(markerOptions);
                getAddress(lat, lon);

                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLng(curLatLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
            } else {
                markerOptions.position(latLng);
                markerOptions.title("Current Position");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                mCurrLocationMarker = mMap.addMarker(markerOptions);
              /*  Toast.makeText(MapsActivity.this, location.getLatitude() + "" + location.getLongitude(), Toast.LENGTH_LONG).show();*/

                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
                getAddress(curLat, curLon);
            }
        }else if(shipOrderValue.equalsIgnoreCase("undelivery")){
            /**
             * Get the current location
             */
            double curLat = Double.longBitsToDouble(AppController.getLongPreference(MapsActivity.this, Constants.UN_DEL_CUR_LATITUDE, -1));
            double curLon = Double.longBitsToDouble(AppController.getLongPreference(MapsActivity.this, Constants.UN_DEL_CUR_LONGITUDE, -1));
            //Place current location marker
            LatLng latLng = new LatLng(curLat, curLon);
            MarkerOptions markerOptions = new MarkerOptions();

            double lat = Double.longBitsToDouble(AppController.getLongPreference(MapsActivity.this, Constants.UN_DEL_LATITUDE, -1));
            double lon = Double.longBitsToDouble(AppController.getLongPreference(MapsActivity.this, Constants.UN_DEL_LONGITUDE, -1));
            String lati = String.valueOf(lat);
            String longi = String.valueOf(lon);


            if (lati != null && longi != null && !Double.isNaN(lat) && !Double.isNaN(lon)) {
           /* Toast.makeText(MapsActivity.this, lat + "" + lat, Toast.LENGTH_LONG).show();*/
                LatLng curLatLng = new LatLng(lat, lon);
                markerOptions.position(curLatLng);
                markerOptions.title("Current Position");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                mCurrLocationMarker = mMap.addMarker(markerOptions);
                getAddress(lat, lon);

                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLng(curLatLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
            } else {
                markerOptions.position(latLng);
                markerOptions.title("Current Position");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                mCurrLocationMarker = mMap.addMarker(markerOptions);
              /*  Toast.makeText(MapsActivity.this, location.getLatitude() + "" + location.getLongitude(), Toast.LENGTH_LONG).show();*/

                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
                getAddress(curLat, curLon);
            }
        }



        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }


    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    /*
     Initialize Views
      */
    private void initViews() {


        mSearchEdittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearchEdittext.setCursorVisible(true);


                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(MapsActivity.this);
                    startActivityForResult(intent, 1);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }
        });


        mSearchEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mSearchEdittext.getText().toString() != "" || mSearchEdittext.getText().toString() != null) {
                    mClear.setVisibility(View.VISIBLE);
                } else {
                    mClear.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.view:
                if (!isPressed) {
                    isPressed = true;
                    Drawable myDrawable = getResources().getDrawable(R.drawable.satelite);
                    mapView.setImageDrawable(myDrawable);
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                } else {
                    Drawable myDrawable = getResources().getDrawable(R.drawable.street_icon);
                    mapView.setImageDrawable(myDrawable);
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    isPressed = false;
                }
                break;
            case R.id.ok:
                finish();
                onBackPressed();

                if (shipOrderValue.equalsIgnoreCase("undelivery")){
                    Intent data = new Intent();
                    setResult(MapsActivity.RESULT_OK, data);
                }
                    break;
            case R.id.search_button:

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
                break;

            case R.id.clear:
                mSearchEdittext.setText("");
                if (mSearchEdittext.getText().toString().equalsIgnoreCase(""))
                    mClear.setVisibility(View.GONE);
                break;
        }
    }


    /**
     * Get the location which is used by search
     */

    public void mapSearch(double latitude, double longitude, LatLng latlng) {

        List<Address> addressList = null;

        if (shipOrderValue.equalsIgnoreCase("delivery")) {
            /**
             * Store the lat and lang in shared preference
             */
            AppController.setLongPreference(MapsActivity.this, Constants.LATITUDE, Double.doubleToRawLongBits(latitude));
            AppController.setLongPreference(MapsActivity.this, Constants.LONGITUDE, Double.doubleToRawLongBits(longitude));
        }else if (shipOrderValue.equalsIgnoreCase("undelivery")) {
            /**
             * Store the lat and lang in shared preference
             */
            AppController.setLongPreference(MapsActivity.this, Constants.UN_DEL_LATITUDE, Double.doubleToRawLongBits(latitude));
            AppController.setLongPreference(MapsActivity.this, Constants.UN_DEL_LONGITUDE, Double.doubleToRawLongBits(longitude));
        }
            getAddress(latitude, longitude);


        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));


    }


    // A place has been received; use requestCode to track the request.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {

            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.e("Tag", "Place: " + place.getAddress() + place.getPhoneNumber() + place.getLatLng().latitude);
                mSearchEdittext.setText(place.getAddress() + " " + place.getPhoneNumber());

                LatLng latLng = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);

                mapSearch(place.getLatLng().latitude, place.getLatLng().longitude, latLng);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mSearchEdittext.getWindowToken(), 0);

               /* Intent intent = new Intent(MapsActivity.this, GoogleMapActivity.class);
                intent.putExtra("latitude",place.getLatLng().latitude);
                intent.putExtra("longitute",place.getLatLng().longitude);
                intent.putExtra("name",place.getName());
                intent.putExtra("addresskyc",place.getAddress());
                startActivity(intent);*/


//                        ((TextView) findViewById(R.id.searched_address)).setText(place.getName() + ",\n" +
//                        place.getAddress() + "\n" + place.getPhoneNumber());

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.e("Tag", status.getStatusMessage());
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mSearchEdittext.getWindowToken(), 0);

            } else if (resultCode == RESULT_CANCELED) {
                Log.e("backpress", "backpress");
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mSearchEdittext.getWindowToken(), 0);

                // The user canceled the operation.
            }
        }
    }


    @Override
    public void onSavedPlaceClick(ArrayList<SavedAddress> mResponse, int position) {
        if (mResponse != null) {
            try {
                Intent data = new Intent();
                data.putExtra("lat", String.valueOf(mResponse.get(position).getLatitude()));
                data.putExtra("lng", String.valueOf(mResponse.get(position).getLongitude()));
                setResult(MapsActivity.RESULT_OK, data);
                finish();
            } catch (Exception e) {

            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mSearchEdittext.getWindowToken(), 0);
    }
}
