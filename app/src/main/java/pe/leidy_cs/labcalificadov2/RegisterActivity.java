package pe.leidy_cs.labcalificadov2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int PERMISSIONS_REQUEST = 100;

    private GoogleMap mMap;

    private String var;

    private EditText nombreInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        nombreInput = findViewById(R.id.nombre_input);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                initMap();
            }
        });

    }

    /**Mapa*/
    private void initMap(){
        Log.d(TAG, "initMap");

        if(ContextCompat.checkSelfPermission(RegisterActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST);
            return;
        }

        // setMyLocationEnabled (Button & Current position)
        mMap.setMyLocationEnabled(true);

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                Toast.makeText(RegisterActivity.this, "Go MyLocation", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // Custom UiSettings
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);    // Controles de zoom
        uiSettings.setCompassEnabled(true); // Brújula
        uiSettings.setMyLocationButtonEnabled(true);    // Show MyLocationButton

        // Set OnMapClickListener
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Toast.makeText(RegisterActivity.this, "onMapClick: " + latLng, Toast.LENGTH_LONG).show();
                var = latLng.toString();
            }
        });

        // Set a marker: http://www.bufa.es/google-maps-latitud-longitud

        LatLng latLng = new LatLng(-12.044105839704793, -76.95285092537847);
        Log.d(TAG, "LatLng: " + latLng);

        // Marker: https://developers.google.com/maps/documentation/android-api/marker?hl=es-419
        MarkerOptions markerOptions = new MarkerOptions().position(latLng)
                .title("Marcador")
                .snippet("Mi ubicación...");
        Marker marker = mMap.addMarker(markerOptions);

        // Show InfoWindow
        marker.showInfoWindow();
        //marker.hideInfoWindow();

        // Custom InfoWindow: https://developers.google.com/maps/documentation/android-api/infowindows?hl=es-419#ventanas_de_informacion_personalizadas
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                // Not implemented yet
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View view = getLayoutInflater().inflate(R.layout.info_contents, null);

                ((ImageView) view.findViewById(R.id.icon)).setImageResource(R.mipmap.ic_launcher);

                TextView titleText = view.findViewById(R.id.title);
                titleText.setText(marker.getTitle());

                TextView snippetText = view.findViewById(R.id.snippet);
                snippetText.setText(marker.getSnippet());

                return view;
            }
        });

        // Set current position camera
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));

        // Set zoom limit
        //mMap.setMinZoomPreference(10);
        //mMap.setMaxZoomPreference(15);

        // https://developers.google.com/maps/documentation/android-sdk/views?hl=es-419
        CameraPosition camera = new CameraPosition.Builder()
                .target(latLng)
                .zoom(15)   // 1 - 21
                .bearing(180) // Giro: 0° - 360°
                .tilt(45)   // Inclinación: 0° - 90°
                .build();
        //mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));

        // Get Address by GeoCode: https://developer.android.com/training/location/display-address.html?hl=es-419
        try {

            List<Address> addresses = new Geocoder(this).getFromLocation(latLng.latitude, latLng.longitude, 1);

            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0); // First address from position
                String city = address.getLocality();
                String state = address.getAdminArea();
                String country = address.getCountryName();
                String postalCode = address.getPostalCode();
                String fulladdress = address.getAddressLine(0);
                Toast.makeText(this, fulladdress, Toast.LENGTH_LONG).show();
            }

        }catch (IOException e){
            Log.e(TAG, e.getMessage(), e);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.register, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_send:
                sendPost();
                return true;

            default:
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendPost() {
        Log.d(TAG, " sendPost()");

        String nombre = nombreInput.getText().toString();

        /**Mapa**/
        Log.d(TAG, "initMap");

        if(ContextCompat.checkSelfPermission(RegisterActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST);
            return;
        }

        // setMyLocationEnabled (Button & Current position)
        mMap.setMyLocationEnabled(true);

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                Toast.makeText(RegisterActivity.this, "Go MyLocation", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // Custom UiSettings
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);    // Controles de zoom
        uiSettings.setCompassEnabled(true); // Brújula
        uiSettings.setMyLocationButtonEnabled(true);    // Show MyLocationButton

        // Set OnMapClickListener
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                Toast.makeText(RegisterActivity.this, "onMapClick: " + latLng, Toast.LENGTH_LONG).show();
            }
        });


        if (nombre.isEmpty()) {
            Toast.makeText(this, "Debes completar todos los campos", Toast.LENGTH_LONG).show();
            return;
        }

        // Get currentuser from FirebaseAuth
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.d(TAG, "currentUser: " + currentUser);

        // Registrar a Firebase Database
        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("posts");
        DatabaseReference postRef = postsRef.push();

        if(var == null){
            var = "No especificó";
        }
        Post post = new Post();
        post.setId(postRef.getKey());
        post.setNombre(nombre);
        post.setUserid(currentUser.getUid());
        post.setEmail(currentUser.getEmail());
        post.setLatLng(var.toString());

        postRef.setValue(post)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onSuccess");
                            Toast.makeText(RegisterActivity.this, "Registro guardado", Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                            Log.e(TAG, "onFailure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Error al guardar", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
