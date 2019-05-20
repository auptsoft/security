package weplay.auptsoft.locationtracker;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.method.CharacterPickerDialog;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import weplay.auptsoft.locationtracker.controllers.BackService;
import weplay.auptsoft.locationtracker.controllers.BackgroundService;
import weplay.auptsoft.locationtracker.controllers.ServerUtil;
import weplay.auptsoft.locationtracker.fragments.AlertsFragment;
import weplay.auptsoft.locationtracker.fragments.DashboardFragment;
import weplay.auptsoft.locationtracker.fragments.ProfileFragment;
import weplay.auptsoft.locationtracker.models.AppState;
import weplay.auptsoft.locationtracker.models.User;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    public Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    DashboardFragment dashboardFragment;
    ProfileFragment profileFragment;

    TextView nameTextView;
    TextView emailTextView;

    LocationManager locationManager;
    Criteria criteria = new Criteria();

    JobScheduler jobScheduler;

    static final int LOCATION_PERMISSION_REQUEST_CODE = 2;

    public static BroadcastReceiver sentReceiver, deliveredReceiver;

    public static String SENT = "SENT", DELIVERED = "DELIVERED";

    public static PendingIntent sentPendingIntent, deliveredPendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        sentPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
        deliveredPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);

        AppState.sharedPreferences = getSharedPreferences("SHARED_PREFERENCES", MODE_PRIVATE);
        AppState.currentUser = User.getFromPreference(AppState.sharedPreferences);
        //AppState.currentUser = new User(0, "lastName", "LastName", "email@gmail.com", "07029299292", "");
        if (!AppState.skipStartup && AppState.currentUser == null) {
            Intent intent = new Intent(this, StartupActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_home);

        dashboardFragment = new DashboardFragment();
        profileFragment = new ProfileFragment();

        toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.home_drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.drawer_nav);

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(Gravity.START);
            }
        });

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        ensureSettings();

        String actionType = getIntent().getStringExtra("type");
        if (actionType != null) {
            if (actionType.equals("alert")) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.home_main_view, AlertsFragment.newInstance("", "Alerts"))
                        .commit();
            } else if (actionType.equals("message")) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.home_main_view, AlertsFragment.newInstance(AppState.currentUser.getEmail(), "Messages"))
                        .commit();
            }
        } else {
            startLocationService();
        }


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.dashboard_nav_id:
                        item.setChecked(true);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.home_main_view, dashboardFragment)
                                //.addToBackStack("")
                                .commit();
                        break;
                    case R.id.profile_nav_id:
                        item.setChecked(true);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.home_main_view, profileFragment)
                                .addToBackStack("")
                                .commit();
                        break;

                    case R.id.messages_nav_id:
                        item.setChecked(true);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.home_main_view, AlertsFragment.newInstance(AppState.currentUser.getEmail(), "Messages"))
                                .addToBackStack("")
                                .commit();
                        break;

                    case R.id.alerts_nav_id:
                        item.setChecked(true);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.home_main_view, AlertsFragment.newInstance("", "Alerts"))
                                .addToBackStack("")
                                .commit();
                        break;


                    case R.id.logout_nav_id:
                        AppState.sharedPreferences.edit().clear().apply();
                        Intent intent = new Intent(getBaseContext(), StartupActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                }

                drawerLayout.closeDrawer(Gravity.START);
                return false;
            }
        });

        View navigationHeader = navigationView.getHeaderView(0);
        nameTextView = (TextView) navigationHeader.findViewById(R.id.header_name_id);
        emailTextView = (TextView) navigationHeader.findViewById(R.id.header_email_id);

        nameTextView.setText(AppState.currentUser.getFirstName() + " " + AppState.currentUser.getLastName());
        emailTextView.setText(AppState.currentUser.getEmail());

        nameTextView.setOnClickListener(this);
        emailTextView.setOnClickListener(this);

        //startLocationService();

        jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);

        //startJobInBackground();

        //startService(new Intent(this, BackService.class));

    }

    @Override
    protected void onPause() {
        sentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case RESULT_OK:
                        Toast.makeText(getBaseContext(), "Sent", Toast.LENGTH_LONG).show(); //debug

                        break;

                    default:
                        Toast.makeText(getBaseContext(), "Not sent", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        };

        deliveredReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case RESULT_OK:
                        Toast.makeText(getBaseContext(), "Delivered", Toast.LENGTH_LONG).show();
                        break;
                    case RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "Not delivered", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        };

        super.onPause();

        registerReceiver(sentReceiver, new IntentFilter(SENT));
        registerReceiver(deliveredReceiver, new IntentFilter(DELIVERED));
    }

    void startJobInBackground() {
        JobInfo.Builder builder = new JobInfo.Builder(20, new ComponentName(this, BackgroundService.class.getName()));
        //builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setPeriodic(10000);
        builder.setPersisted(true);

        //jobScheduler.schedule(builder.build());
    }

    @Override
    public void onClick(View view) {
        if (view.equals(nameTextView) || view.equals(emailTextView)) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.home_main_view, profileFragment)
                    .addToBackStack("")
                    .commit();
            drawerLayout.closeDrawer(Gravity.START);
        }
        drawerLayout.closeDrawer(Gravity.START);
    }

    public void ensureSettings() {
        boolean gps_enabled = false;
        //boolean network_enabled = false;

        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        /*try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {} */

        if (!gps_enabled) {
            AlertDialog.Builder builder = new AlertDialog.Builder(appCompatActivity());
            builder.setMessage("This app requires Location service. Tap OK enable");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(intent, 5);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(getBaseContext(), "This application cannot run without location service, Goodbye", Toast.LENGTH_LONG).show();
                    finish();
                }
            });
            builder.show();
        }
    }

    @TargetApi(23)
    public void startLocationService() {

        /*criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM); */

        String provider = LocationManager.GPS_PROVIDER;

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestSingleUpdate(provider, locationListener, null);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.home_main_view, dashboardFragment)
                    .commit();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.SEND_SMS}, LOCATION_PERMISSION_REQUEST_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE || requestCode == 12 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocationService();
        } else {
            Toast.makeText(this, "This app requires location service. Goodbye", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 5) {
            ensureSettings();
        }
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
                    Toast.makeText(getApplication(), "Internet connection not available or too slow. Tap REFRESH to try again", Toast.LENGTH_LONG).show();
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

    //@org.jetbrains.annotations.Contract(value = " -> !null", pure = true)
    private AppCompatActivity appCompatActivity() {
        return this;
    }

    public void sendSMS(String phoneNumber, String text) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, text, sentPendingIntent, deliveredPendingIntent);
        //smsManager.sen
    }
}