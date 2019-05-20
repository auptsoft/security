package weplay.auptsoft.locationtracker;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import weplay.auptsoft.locationtracker.databinding.ActivityEnsureSettingsBinding;

/**
 * Created by Andrew on 8.2.19.
 */

public class EnsureSettingsActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityEnsureSettingsBinding activityEnsureSettingsBinding;
    LocationManager locationManager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            finish();
        }

        activityEnsureSettingsBinding = DataBindingUtil.setContentView(this, R.layout.activity_ensure_settings);

        activityEnsureSettingsBinding.allowBtn.setOnClickListener(this);
        activityEnsureSettingsBinding.denyBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.equals(activityEnsureSettingsBinding.allowBtn)){
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 5);
        } else if (view.equals(activityEnsureSettingsBinding.denyBtn)) {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 5) {
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(this, "Activated. Location Tracker is running", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(this, "Not activated. Tap DENY to close", Toast.LENGTH_LONG).show();
            }
        }
    }
}
