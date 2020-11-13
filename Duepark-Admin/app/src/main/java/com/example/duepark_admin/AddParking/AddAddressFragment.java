package com.example.duepark_admin.AddParking;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.duepark_admin.R;
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
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class AddAddressFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveListener {

    private String base_url;
    private View view;
    private GoogleMap mMap;
    private double latitude, longitude;
    private String add, acronym, city, id, a, back, employeeName, employeeMobileNumber, employeeId;
    private Location lastLocation;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private final float DEFAULT_ZOOM = 18;
    private TextView currentAddress, landmark;
    private Geocoder geocoder;
    private ImageView imgLocationPinUp;
    private BottomSheetBehavior bottomSheetBehavior;


    public AddAddressFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_address, container, false);

        base_url = getResources().getString(R.string.base_url);
        Bundle bundle = getArguments();
        if (bundle != null) {
            add = bundle.getString("Add");
            id = bundle.getString("id");
            back = bundle.getString("back");
            employeeId = bundle.getString("employeeId");
            employeeName = bundle.getString("employeeName");
            employeeMobileNumber = bundle.getString("employeeMobileNumber");
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        geocoder = new Geocoder(getContext(), Locale.getDefault());

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getContext()));

        imgLocationPinUp = view.findViewById(R.id.imgLocationPinUp);

        EditText inputBlockNo = view.findViewById(R.id.inputBlockNo);
        EditText inputLandmark = view.findViewById(R.id.inputLandmark);
        currentAddress = view.findViewById(R.id.address);
        landmark = view.findViewById(R.id.landmark);

        Button saveBtn = view.findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!inputBlockNo.getText().toString().trim().isEmpty() && !inputLandmark.getText().toString().trim().isEmpty()) {
                    a = inputBlockNo.getText().toString().trim() + " " + inputLandmark.getText().toString().trim();
                } else {
                    a = currentAddress.getText().toString().trim();
                }
                Bundle args = new Bundle();
                if (add.equals("Parking")) {
                    Fragment addParkingFragment = new AddParkingFragment();
                    assert getFragmentManager() != null;
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    Log.d("Address", a);
                    //String address = currentAddress.getText().toString().trim();
                    args.putString("Address", a);
                    args.putString("Acronym", acronym);
                    args.putString("City", city);
                    args.putString("employeeId", employeeId);
                    args.putString("employeeName", employeeName);
                    args.putString("employeeMobileNumber", employeeMobileNumber);
                    addParkingFragment.setArguments(args);
                    transaction.replace(R.id.fragment_container, addParkingFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();

                } else if (add.equals("Location")) {
                    Fragment addLocationFragment = new AddParkingLocationFragment();
                    assert getFragmentManager() != null;
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    Log.d("Address", a);
                    //String address = currentAddress.getText().toString().trim();
                    args.putString("Address", a);
                    args.putString("Acronym", acronym);
                    args.putString("City", city);
                    args.putString("id", id);
                    args.putString("back", back);
                    addLocationFragment.setArguments(args);
                    transaction.replace(R.id.fragment_container, addLocationFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
        });

        initComponent(view);

        return view;
    }

    private void initComponent(View view) {
        LinearLayout bottomSheet = view.findViewById(R.id.bottom_sheet);

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
        SettingsClient settingsClient = LocationServices.getSettingsClient(Objects.requireNonNull(getActivity()));
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
                    resolvable.startResolutionForResult(getActivity(), 51);
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
                        mMap.addMarker(new MarkerOptions().position(latLng).icon(bitmapDescriptorFromVector(getContext(), R.drawable.marker)));//.draggable(true));
                        try{
                            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                            city = addresses.get(0).getLocality();
                            landmark.setText(addresses.get(0).getSubLocality());
                            currentAddress.setText(addresses.get(0).getAddressLine(0));//.substring(0, addresses.get(0).getAddressLine(0).indexOf(addresses.get(0).getAdminArea()) - 2));
                            BackgroundTask backgroundTask = new BackgroundTask(getContext());
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
                                    BackgroundTask backgroundTask = new BackgroundTask(getContext());
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
                    Toast.makeText(getContext(), "unable to get last location", Toast.LENGTH_SHORT).show();
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
        mMap.addMarker(new MarkerOptions().position(position).icon(bitmapDescriptorFromVector(getContext(), R.drawable.marker)));
        try{
            List<Address> addresses = geocoder.getFromLocation(position.latitude, position.longitude, 1);
            if(!addresses.isEmpty()) {
                city = addresses.get(0).getLocality();
                landmark.setText(addresses.get(0).getSubLocality());
                currentAddress.setText(addresses.get(0).getAddressLine(0));//.substring(0, addresses.get(0).getAddressLine(0).indexOf(addresses.get(0).getAdminArea()) - 2));
                BackgroundTask backgroundTask = new BackgroundTask(getContext());
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

    class BackgroundTask extends AsyncTask<String, String, String>{
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