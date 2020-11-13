package com.example.duepark_consumer.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.duepark_consumer.Helper.SessionManagerHelper;
import com.example.duepark_consumer.R;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveListener {

    private String base_url;
    private GoogleMap mMap;
    private double latitude, longitude;
    private Location lastLocation;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private final float DEFAULT_ZOOM = 18;
    private TextView currentAddress, landmark;
    private Geocoder geocoder;
    private ImageView imgLocationPinUp;
    private BottomSheetBehavior bottomSheetBehavior;
    private String city, acronym, a;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        base_url = getResources().getString(R.string.base_url);
        geocoder = new Geocoder(this, Locale.getDefault());

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(this));

        imgLocationPinUp = findViewById(R.id.imgLocationPinUp);

        final EditText inputBlockNo = findViewById(R.id.inputBlockNo);
        final EditText inputLandmark = findViewById(R.id.inputLandmark);
        currentAddress = findViewById(R.id.address);
        landmark = findViewById(R.id.landmark);

        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Button saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!inputBlockNo.getText().toString().trim().isEmpty() && !inputLandmark.getText().toString().trim().isEmpty()) {
                    a = inputBlockNo.getText().toString().trim() + " " + inputLandmark.getText().toString().trim();
                } else {
                    a = currentAddress.getText().toString().trim();
                }

                Intent i = new Intent(MapsActivity.this, AddLocationActivity.class);
                i.putExtra("address", a);
                i.putExtra("acronym", acronym);
                i.putExtra("city", city);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });

        initComponent();

    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(MapsActivity.this, AddLocationActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    private void initComponent(){
        LinearLayout bottomSheet = findViewById(R.id.bottom_sheet);

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        SettingsClient settingsClient = LocationServices.getSettingsClient(Objects.requireNonNull(MapsActivity.this));
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getDeviceLocation();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                ResolvableApiException resolvable = (ResolvableApiException) e;
                try {
                    resolvable.startResolutionForResult(MapsActivity.this, 51);
                } catch (IntentSender.SendIntentException e1) {
                    e1.printStackTrace();
                }
            }
        });
        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveListener(this);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 51){
            if (resultCode == RESULT_OK) {
                getDeviceLocation();
            }
        }
    }

    private void getDeviceLocation(){
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if(task.isSuccessful()){
                    lastLocation = task.getResult();
                    if(lastLocation != null){
                        latitude = lastLocation.getLatitude();
                        longitude = lastLocation.getLongitude();
                        LatLng latLng = new LatLng(latitude,longitude);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()),DEFAULT_ZOOM));
                        mMap.addMarker(new MarkerOptions().position(latLng).icon(bitmapDescriptorFromVector(MapsActivity.this, R.drawable.marker)));//.draggable(true));
                        try{
                            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                            city = addresses.get(0).getLocality();
                            landmark.setText(addresses.get(0).getSubLocality());
                            currentAddress.setText(addresses.get(0).getAddressLine(0));//.substring(0, addresses.get(0).getAddressLine(0).indexOf(addresses.get(0).getAdminArea()) - 2));
                            BackgroundTask backgroundTask = new BackgroundTask(MapsActivity.this);
                            backgroundTask.execute(addresses.get(0).getAdminArea());
                        }catch (IOException ie){
                            ie.printStackTrace();
                        }
                    } else {
                        final LocationRequest locationRequest = LocationRequest.create();
                        locationRequest.setInterval(10000);
                        locationRequest.setFastestInterval(5000);
                        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                        locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                super.onLocationResult(locationResult);
                                if (locationResult == null) {
                                    return;
                                }
                                lastLocation = locationResult.getLastLocation();
                                latitude = lastLocation.getLatitude();
                                longitude = lastLocation.getLongitude();
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()), DEFAULT_ZOOM));
                                try{
                                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                    city = addresses.get(0).getLocality();
                                    landmark.setText(addresses.get(0).getSubLocality());
                                    currentAddress.setText(addresses.get(0).getAddressLine(0));//.substring(0, addresses.get(0).getAddressLine(0).indexOf(addresses.get(0).getAdminArea()) - 2));
                                    BackgroundTask backgroundTask = new BackgroundTask(MapsActivity.this);
                                    backgroundTask.execute(addresses.get(0).getAdminArea());
                                }catch (IOException ie){
                                    ie.printStackTrace();
                                }
                                fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                            }
                        };
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
                    }
                } else {
                    Toast.makeText(MapsActivity.this, "unable to get last location", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onCameraIdle() {
        imgLocationPinUp.setVisibility(View.GONE);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        LatLng position = mMap.getCameraPosition().target;
        mMap.addMarker(new MarkerOptions().position(position).icon(bitmapDescriptorFromVector(MapsActivity.this, R.drawable.marker)));
        try{
            List<Address> addresses = geocoder.getFromLocation(position.latitude, position.longitude, 1);
            if(!addresses.isEmpty()) {
                city = addresses.get(0).getLocality();
                landmark.setText(addresses.get(0).getSubLocality());
                currentAddress.setText(addresses.get(0).getAddressLine(0));//.substring(0, addresses.get(0).getAddressLine(0).indexOf(addresses.get(0).getAdminArea()) - 2));
                BackgroundTask backgroundTask = new BackgroundTask(MapsActivity.this);
                backgroundTask.execute(addresses.get(0).getAdminArea());
            }
        }catch (IOException ie){
            ie.printStackTrace();
        }
    }

    @Override
    public void onCameraMove() {
        mMap.clear();
        bottomSheetBehavior.setPeekHeight(400);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        imgLocationPinUp.setVisibility(View.VISIBLE);
    }

    class BackgroundTask extends AsyncTask<String, String, String> {
        String abbr;
        Context ctx;

        BackgroundTask(Context ctx){
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... params) {
            String name = params[0];
            String json_string = base_url+"get_stateAbbreviation.php?state="+name;
            //String json_string = "https://duepark.000webhostapp.com/consumer/get_stateAbbreviation.php?state="+name;
            try{
                URL url = new URL(json_string);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuilder stringBuilder = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine())!=null){
                    stringBuilder.append(line+"\n");
                }
                httpURLConnection.disconnect();

                String jsonString = stringBuilder.toString().trim();

                JSONObject jsonObject = new JSONObject(jsonString);
                JSONArray jsonArray = jsonObject.getJSONArray("server_response");
                int count = 0;
                while(count<jsonArray.length()){
                    JSONObject jo = jsonArray.getJSONObject(count);
                    count++;
                    abbr = jo.getString("StateAbbreviation");
                }
                Log.d("JSON-STRING",jsonString);
                return abbr;
            }catch(Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            getStateAbbreviation(result);
        }
    }

    private void getStateAbbreviation(String abbreviation) {
        acronym = abbreviation;
    }
}