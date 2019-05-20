package weplay.auptsoft.locationtracker.controllers;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

import weplay.auptsoft.locationtracker.HomeActivity;
import weplay.auptsoft.locationtracker.models.AppState;

/**
 * Created by Andrew on 4.2.19.
 */

public class BackgroundService extends JobService {
    LocationManager locationManager;
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        Toast.makeText(this, "job started", Toast.LENGTH_LONG).show();
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 1, locationListener);
        } else {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            ServerUtil.reverseGeocode(location.getLatitude(), location.getLongitude(), getApplication(), new ServerUtil.OnReverseGeocodeListener() {
                @Override
                public void onResult(List<Address> result) {
                    Toast.makeText(getApplication(), result.get(0).getAddressLine(0), Toast.LENGTH_LONG).show();
                    AppState.currentAddress = result.get(0);
                    AppState.currentLocation = location;

                    sendBroadcast(new Intent(AppState.LOCATION_UPDATE_BROADCAST));
                }

                @Override
                public void onError(String errorString) {
                    Toast.makeText(getApplication(), "Error while getting address of the location", Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };
}
