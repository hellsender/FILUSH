package pbl.g12.sem1.FILUSH;

import android.*;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentChange.Type;
import com.google.firebase.firestore.DocumentListenOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Query.Direction;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import codingwithmitch.com.googlemapsgoogleplaces.models.PlaceInfo;

/**
 * Created by User on 10/2/2017.
 */

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener{
    FirebaseFirestore db;

    //di ko alam to
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {

    }


    //map chuchu
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        //permission sa location
        if (mLocationPermissionsGranted)
        {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);


            //MARKERS//
            markers();


            init();
        }
    }

    public void markers()
    {
        LatLng saguijo = new LatLng(14.563654, 121.012126);
        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_comfort_room_round))
                .position(saguijo)
                .title("Saguijo Comfort Room"));

        LatLng iacademy_nexus = new LatLng(14.559752, 121.008061);
        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_comfort_room_round))
                .position(iacademy_nexus)
                .title("iAcademy Nexus Comfort Room"));

        LatLng ceu = new LatLng(14.560691, 121.012716);
        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_comfort_room_round))
                .position(ceu)
                .title("Centro Escolar University Comfort Room"));

        LatLng rcbc = new LatLng(14.560977, 121.016655);
        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_comfort_room_round))
                .position(rcbc)
                .title("RCBC Plaza Comfort Room"));

        LatLng iacademy_buendia = new LatLng(14.561392, 121.019532);
        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_comfort_room_round))
                .position(iacademy_buendia)
                .title("iAcademy Buendia Comfort Room"));










    }

    //copy codes lang
    private static final String TAG = "MapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));


    //widgets
    private AutoCompleteTextView mSearchText;
    private ImageView mGps, mInfo, mPlacePicker;


    //vars
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    private PlaceInfo mPlace;
    private Marker mMarker;

    Button exit,add;
    Dialog myDialog   , myDialog2;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //pop window
        myDialog = new Dialog(this);

        //pop window new comfort room
        myDialog2 = new Dialog(this);

        mSearchText = (AutoCompleteTextView) findViewById(R.id.input_search);
        mGps = (ImageView) findViewById(R.id.ic_gps);
        mInfo = (ImageView) findViewById(R.id.place_info);

        getLocationPermission();

    }



    //search location tapos auto complete display searches
    private void init(){
        Log.d(TAG, "init: initializing");

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        mSearchText.setOnItemClickListener(mAutocompleteClickListener);

        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient,
                LAT_LNG_BOUNDS, null);

        mSearchText.setAdapter(mPlaceAutocompleteAdapter);

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){

                    //execute our method for searching
                    geoLocate();
                }

                return false;
            }
        });

        //click GPS to show current location
        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked gps icon");
                getDeviceLocation();

                //MARKERS//
                markers();

            }
        });
        //click 'i' to display current marker place info

        //may mga bugs pa dito and kulang
        mInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked place info");
                try{
                    if(mMarker.isInfoWindowShown()){
                        mMarker.hideInfoWindow();
                    }else{
                        Log.d(TAG, "onClick: place info: " + mPlace.toString());
                        mMarker.showInfoWindow();
                    }
                }catch (NullPointerException e){
                    Log.e(TAG, "onClick: NullPointerException: " + e.getMessage() );
                }

                //MARKERS//
                markers();

            }
        });

        /**
         * PLACE PICKER
         */

        //wala to just incase lang

//        mPlacePicker.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
//
//                try {
//                    startActivityForResult(builder.build(MapActivity.this), PLACE_PICKER_REQUEST);
//                } catch (GooglePlayServicesRepairableException e) {
//                    Log.e(TAG, "onClick: GooglePlayServicesRepairableException: " + e.getMessage() );
//                } catch (GooglePlayServicesNotAvailableException e) {
//                    Log.e(TAG, "onClick: GooglePlayServicesNotAvailableException: " + e.getMessage() );
//                }
//
//                //MARKERS//
//
//                markers();
//            }
//        });

        hideSoftKeyboard();
    }

    //di ko rin maintindihan to
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);

                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient, place.getId());
                placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            }
        }

        markers();

    }

    //find location
    private void geoLocate(){
        Log.d(TAG, "geoLocate: geolocating");

        String searchString = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString, 1);
        }catch (IOException e){
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage() );
        }

        if(list.size() > 0){
            Address address = list.get(0);

            Log.d(TAG, "geoLocate: found a location: " + address.toString());
            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM,
                    address.getAddressLine(0));
        }

        //MARKERS//

        markers();
    }

    //get device current location
    private void getDeviceLocation()
    {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionsGranted){

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM,
                                    "My Location");

                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MapActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }

    //move camera to location
    private void moveCamera(LatLng latLng, float zoom, PlaceInfo placeInfo)
    {
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        mMap.clear();

        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(MapActivity.this));

        if(placeInfo != null)
        {
            try
            {
                String snippet = "Address: " + placeInfo.getAddress() + "\n" +
                        "Comfort Room Rating: " + placeInfo.getRating() + "\n" +
                        "Users comment: " + "\n";

                MarkerOptions options = new MarkerOptions()
                        .position(latLng)
                        .title(placeInfo.getName())
                        .snippet(snippet);
                mMarker = mMap.addMarker(options);

            }
            catch (NullPointerException e)
            {
                Log.e(TAG, "moveCamera: NullPointerException: " + e.getMessage() );
            }

        }
        else
        {
            mMap.addMarker(new MarkerOptions().position(latLng));
        }

        hideSoftKeyboard();
    }

    //di ko alam bakit dalawa
    private void moveCamera(LatLng latLng, float zoom, String title)
    {
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if(!title.equals("My Location")){
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMap.addMarker(options);
        }

        hideSoftKeyboard();
    }

    //initialize map
    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(MapActivity.this);
    }

    //permission chuchu ulet
    private void getLocationPermission()
    {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            }
            else
            {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
        else
        {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    //ewan
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch(requestCode)
        {
            case LOCATION_PERMISSION_REQUEST_CODE:
            {
                if(grantResults.length > 0)
                {
                    for(int i = 0; i < grantResults.length; i++)
                    {
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED)
                        {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }

                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }

    private void hideSoftKeyboard()
    {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
    //di ko na maintindihan
    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
        {
            hideSoftKeyboard();

            final AutocompletePrediction item = mPlaceAutocompleteAdapter.getItem(i);
            final String placeId = item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

        }
    };

    //putangina
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>()
    {
        @Override
        public void onResult(@NonNull PlaceBuffer places)
        {
            if(!places.getStatus().isSuccess())
            {
                Log.d(TAG, "onResult: Place query did not complete successfully: " + places.getStatus().toString());
                places.release();
                return;
            }
            final Place place = places.get(0);

            try
            {
                mPlace = new PlaceInfo();
                mPlace.setName(place.getName().toString());
                Log.d(TAG, "onResult: name: " + place.getName());
                mPlace.setAddress(place.getAddress().toString());
                Log.d(TAG, "onResult: address: " + place.getAddress());
                mPlace.setId(place.getId());
                Log.d(TAG, "onResult: id:" + place.getId());
                mPlace.setLatlng(place.getLatLng());
                Log.d(TAG, "onResult: latlng: " + place.getLatLng());
                mPlace.setRating(place.getRating());
                Log.d(TAG, "onResult: rating: " + place.getRating());
                mPlace.setPhoneNumber(place.getPhoneNumber().toString());

                Log.d(TAG, "onResult: place: " + mPlace.toString());
            }
            catch (NullPointerException e)
            {
                Log.e(TAG, "onResult: NullPointerException: " + e.getMessage() );
            }

            moveCamera(new LatLng(place.getViewport().getCenter().latitude,
                    place.getViewport().getCenter().longitude), DEFAULT_ZOOM, mPlace);

            places.release();

            markers();

        }
    };






    ////////BUTTON METHODS//////////
        public void Exit(View view)
        {
            exit = (Button) findViewById(R.id.btnExit);
            finish();
            System.exit(0);
        }




        //wala pako nalalagay, bobo pako firebase
        public void Add(View view)
        {
            db = FirebaseFirestore.getInstance();


            add = (Button) findViewById(R.id.btnAdd);

            TextView txtclose;
            Button comment, create, rate;

            myDialog.setContentView(R.layout.popwindow);
            myDialog.setCancelable(true);
            myDialog.setCanceledOnTouchOutside(true);

            txtclose = (TextView) myDialog.findViewById(R.id.txtClose);
            comment = (Button) myDialog.findViewById(R.id.btnComment);
            create = (Button) myDialog.findViewById(R.id.btnCreate);
            rate = (Button) myDialog.findViewById(R.id.btnRate);


            //CLOSE BUTTON
            txtclose.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    myDialog.dismiss();

                }


            });
            //COMMENT BUTTON
            comment.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {

                }
            });

            //CREATE NEW COMFORT ROOM BUTTON
            create.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    myDialog.dismiss();

                    myDialog2.setContentView(R.layout.popwindow_new_comfort_room);
                    myDialog2.setCancelable(true);
                    myDialog2.setCanceledOnTouchOutside(true);
                    //new
                    Button btnAdd;
                    TextView txtclose2;
                    final EditText place_name;

                    btnAdd = (Button) myDialog2.findViewById(R.id.btnAdd);
                    txtclose2 = (TextView) myDialog2.findViewById(R.id.txtClose2);
                    place_name = (EditText) myDialog2.findViewById(R.id.place_name);






                    //new
                    //DISPLAY ANOTHER PREVIEW

                    txtclose2.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            myDialog2.dismiss();
                        }
                    });


                    btnAdd.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            //ADD CURRENT LOCATION (LATITUDE AND LONGITUDE TO FIREBASE)
                            Log.d(TAG, "getDeviceLocation: getting the devices current location");

                            try
                            {
                                if(mLocationPermissionsGranted)
                                {
                                    final Task location = mFusedLocationProviderClient.getLastLocation();
                                    location.addOnCompleteListener(new OnCompleteListener()
                                    {
                                        @Override
                                        public void onComplete(@NonNull Task task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                Log.d(TAG, "onComplete: found location!");
                                                Location currentLocation = (Location) task.getResult();

                                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                                DEFAULT_ZOOM,
                                                "My Location");

                                                LatLng current = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                                                mMap.addMarker(new MarkerOptions()
                                                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_comfort_room_round))
                                                .position(current)
                                                .title(place_name.getText().toString()));

                                                Map<String, Object> bathroomInformation = new HashMap<>();
                                                bathroomInformation.put("name", place_name);



                                                db.collection("name").document("bathroom")
                                                        .set(bathroomInformation)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            public void onSuccess(Void aVoid) {
                                                                Log.d(TAG, "DocumentSnapshot successfully written!");
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.w(TAG, "Error writing document", e);
                                                            }
                                                        });



                                                Toast.makeText(MapActivity.this, "Comfort Room Added!", Toast.LENGTH_SHORT).show();
                                                myDialog2.dismiss();



                                            }
                                            else
                                            {
                                                Log.d(TAG, "onComplete: current location is null");
                                                Toast.makeText(MapActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                            catch (SecurityException e)
                            {
                            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
                            }

                        }
                    });

                    myDialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    myDialog2.show();
                }


            });



            //RATE BUTTON
            rate.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {

                }
            });

            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myDialog.show();

            /** To do list:

            //COMMENTS ON NEARBY COMFORT ROOM
            //ADD CURRENT COMFORT ROOM LOCATION
                //ADD FIREBASE
            //RATE COMFORT ROOM

            **/

        }


}















