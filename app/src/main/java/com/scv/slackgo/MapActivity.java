package com.scv.slackgo;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by ayelen@scvsoft.com .
 */
public class MapActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
    OnMapReadyCallback, LocationListener, GoogleApiClient.OnConnectionFailedListener {

    final private double OFFICE_LAT = -34.6024267;
    final private double OFFICE_LONG = -58.45435;
    private static final String OFFICE = "Office";
    final private int RC_ASK_PERMISSIONS = 123;
    private static final String TAG = "MapActivity";

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        Uri data = getIntent().getData();
        //TODO it must be used on slack calls
        String code = data.getQueryParameters("code").get(0);

        SupportMapFragment mapFragment = new SupportMapFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.office_map, mapFragment);
        fragmentTransaction.commit();
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleApiClient.connect();
        LatLng officePosition = new LatLng(OFFICE_LAT, OFFICE_LONG);

        MarkerOptions markerOptions = new MarkerOptions();

        markerOptions.position(officePosition);
        markerOptions.draggable(false);
        markerOptions.title(OFFICE);

        CircleOptions circleOptions = new CircleOptions();

        circleOptions.center(officePosition);
        circleOptions.radius(200);
        circleOptions.strokeColor(Color.argb(200, 255, 0, 255));
        circleOptions.fillColor(Color.argb(25, 255, 0, 255));

        googleMap.addMarker(markerOptions);
        googleMap.addCircle(circleOptions);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(officePosition, 15.0f));

    }

    @Override
    public void onLocationChanged(Location location) {

        startLocationUpdates();
    }

    // Trigger new location updates at interval
    protected void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, RC_ASK_PERMISSIONS);
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            if (mLastLocation.getLatitude() == OFFICE_LAT && mLastLocation.getLongitude() == OFFICE_LONG) {
                //TODO In the office, join the user to the channel
                TextView status = (TextView) findViewById(R.id.status);
                status.setText(R.string.in_the_office);
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        //TODO IMPLEMENT ME
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (String.valueOf(requestCode) == android.Manifest.permission.ACCESS_FINE_LOCATION &&
                grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this.getBaseContext(),
                "Google Maps API Connection Failed: " + connectionResult, Toast.LENGTH_SHORT).show();
    }


    /**
    private class OnTokenAcquired implements AccountManagerCallback<Bundle> {
        @Override
        public void run(AccountManagerFuture<Bundle> result) {
            // Get the result of the operation from the AccountManagerFuture.
            Bundle bundle = null;
            try {
                bundle = result.getResult();
            } catch (OperationCanceledException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (AuthenticatorException e) {
                e.printStackTrace();
            }

            // The token is a named value in the bundle. The name of the value
            // is stored in the constant AccountManager.KEY_AUTHTOKEN.
            String token = bundle.getString(AccountManager.KEY_AUTHTOKEN);

        }
    }
    **/
}
