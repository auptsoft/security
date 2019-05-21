package weplay.auptsoft.locationtracker.fragments;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.wang.avi.AVLoadingIndicatorView;

import java.net.HttpURLConnection;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import weplay.auptsoft.locationtracker.HomeActivity;
import weplay.auptsoft.locationtracker.MapsActivity;
import weplay.auptsoft.locationtracker.R;
import weplay.auptsoft.locationtracker.controllers.BackService;
import weplay.auptsoft.locationtracker.controllers.ServerUtil;
import weplay.auptsoft.locationtracker.controllers.Utility;
import weplay.auptsoft.locationtracker.models.AppState;
import weplay.auptsoft.locationtracker.models.LocationData;

/**
 * Created by Andrew on 5.2.19.
 */

public class HomeFragment extends Fragment implements  View.OnClickListener {

    View showMap, refresh; // upload;
    AVLoadingIndicatorView loadingIndicatorView;
    View locationView;
    //Switch workInBackground;

    LocationManager locationManager;


    TextView addressView, latLongView, lastUpdatedView;

    View callPoliceView, callFireService, reportEmergencyView, sendLocationAsSMSView,
          callDesteward;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        locationManager = (LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);

        /*AppCompatActivity appCompatActivity = (AppCompatActivity)getA,ctivity();
        appCompatActivity.getSupportActionBar().setTitle("Dashboard"); */


        //((HomeActivity)getActivity()).toolbar.setTitle("Desteward Security");

        showMap = view.findViewById(R.id.view_map_action);
        refresh = view.findViewById(R.id.refresh_and_upload);
        //upload = view.findViewById(R.id.dashboard_upload);
        //workInBackground = (Switch)view.findViewById(R.id.dashboard_work_in_background_switch);
        //loadingIndicatorView = (AVLoadingIndicatorView)view.findViewById(R.id.dashboard_loader_indicator);
        locationView = (View)view.findViewById(R.id.location_view);

        callPoliceView = view.findViewById(R.id.call_police_action);
        //reportEmergencyView = view.findViewById(R.id.report_emergency);
        callFireService = view.findViewById(R.id.call_fire_service_action);
        sendLocationAsSMSView = view.findViewById(R.id.send_location_as_sms_action);
        callDesteward = view.findViewById(R.id.call_desteward_group_action);


        addressView = (TextView)view.findViewById(R.id.address_text);
        //latLongView = (TextView)view.findViewById(R.id.dashboard_latitude_longitude_view);
        lastUpdatedView = (TextView)view.findViewById(R.id.last_updated_text);

        showMap.setOnClickListener(this);
        refresh.setOnClickListener(this);
//        upload.setOnClickListener(this);

        callPoliceView.setOnClickListener(this);
        callFireService.setOnClickListener(this);
        //reportEmergencyView.setOnClickListener(this);
        sendLocationAsSMSView.setOnClickListener(this);
        callDesteward.setOnClickListener(this);

//        viewProfile.setOnClickListener(this);
//        viewMessages.setOnClickListener(this);


        boolean bw = AppState.sharedPreferences.getBoolean(AppState.WORK_IN_BACKGROUND_PREF, false);
        //workInBackground.setChecked(bw);
        if (bw) getActivity().startService(new Intent(getContext(), BackService.class));

        /*workInBackground.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                AppState.sharedPreferences.edit().putBoolean(AppState.WORK_IN_BACKGROUND_PREF, b).apply();

                if(b) {
                    getActivity().startService(new Intent(getContext(), BackService.class));
                } else  {
                    getActivity().stopService(new Intent(getContext(), BackService.class));
                }

                String outText = "Background process has been "+(b ? "Activated" : "Deactivated");
                Toast.makeText(getContext(), outText, Toast.LENGTH_LONG).show();

            }
        }); */

        getContext().registerReceiver(receiver, new IntentFilter(AppState.LOCATION_UPDATE_BROADCAST));

        initializeView();

        return view;
    }


    @Override
    public void onPause() {
        super.onPause();
        //getContext().unregisterReceiver(receiver);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateView();
        }
    };


    void initializeView() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 12);
                return;
            }
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        AppState.currentLocation = location;
        if (location != null) {
//            latLongView.setText(location.getLatitude()+", "+location.getLongitude());
            ServerUtil.reverseGeocode(location.getLatitude(), location.getLongitude(), getContext(), new ServerUtil.OnReverseGeocodeListener() {
                @Override
                public void onResult(List<Address> result) {
                    Address address = result.get(0);
                    addressView.setText(address.getAddressLine(0));
                }

                @Override
                public void onError(String errorString) {
                    addressView.setText("Could not get current address. Check your internet connection and tap on REFRESH");
                }
            });
        }
        lastUpdatedView.setText("unknown");
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if ((requestCode==13 || requestCode==12 ) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if(requestCode == 12){
                initializeView();
            } else if (requestCode == 13) {
                refresh();
            }
        } else {
            Toast.makeText(getContext(), "This app requires location service. Goodbye", Toast.LENGTH_LONG).show();
            getActivity().finish();
        }
    } */

    @Override
    public void onClick(View view) {
        if(view.equals(showMap)) {
            if(AppState.currentLocation != null) {
                Intent intent = new Intent(getContext(), MapsActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "refresh first to determine your current location", Toast.LENGTH_LONG).show();
            }
        } else if (view.equals(refresh)) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (getContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 12);
                    return;
                }
                refresh();
            }
        } /*else if (view.equals(upload)) {
            if (!(AppState.currentLocationData == null)) {
                //loadingIndicatorView.setIndicator(new BallClipRotateIndicator());
                loadingIndicatorView.setVisibility(View.VISIBLE);
                locationView.setVisibility(View.GONE);

                Snackbar.make(locationView, "uploading...", Snackbar.LENGTH_INDEFINITE).show();
                Utility.uploadLocationData(AppState.currentLocationData, new ServerUtil.OnResultListener() {
                    @Override
                    public void onResult(String data, HttpURLConnection httpURLConnection) {
                        Toast.makeText(getContext(), data, Toast.LENGTH_SHORT).show();
                        loadingIndicatorView.setVisibility(View.GONE);
                        locationView.setVisibility(View.VISIBLE);
                        Snackbar.make(locationView, "done", Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(String errorString) {
                        Toast.makeText(getContext(), errorString, Toast.LENGTH_SHORT).show();
                        loadingIndicatorView.setVisibility(View.GONE);
                        locationView.setVisibility(View.VISIBLE);
                        Snackbar.make(locationView, "error occurred", Snackbar.LENGTH_LONG).show();
                        Snackbar.make(locationView, "Error occurred while uploading", Snackbar.LENGTH_LONG).show();
                    }
                });
            }  else {
                Toast.makeText(getContext(), "Location not gotten. Tap on refresh", Toast.LENGTH_LONG).show();
            }
        } */ else if (view.equals(callPoliceView) || view.equals(callFireService)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            LayoutInflater layoutInflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View dialogView = layoutInflater.inflate(R.layout.phone_number_dialog, null);
            LinearLayout linearLayout = dialogView.findViewById(R.id.phone_numbers_list_layout);
            linearLayout.removeAllViews();
            if(AppState.currentAddress != null) {
                String state = AppState.currentAddress.getMaxAddressLineIndex()+"";
                Set<String> keySet = AppState.statePhoneNumbers.keySet();
                //String[] keysArray = (String[])keySet.toArray();

                for (String key : keySet) {
                    View itemView = layoutInflater.inflate(R.layout.phone_number_item, null);
                    TextView pnItem = (TextView)itemView.findViewById(R.id.phone_number_display_id);
                    TextView stateItem = (TextView)itemView.findViewById(R.id.state_display_id);
                    pnItem.setText(AppState.statePhoneNumbers.get(key));
                    stateItem.setText((String)key);

                    linearLayout.addView(itemView);
                }
            } else {
                //String state = AppState.currentAddress.getMaxAddressLineIndex()+"";
                Set<String> keySet = AppState.statePhoneNumbers.keySet();
                for (String key : keySet) {
                    View itemView = layoutInflater.inflate(R.layout.phone_number_item, null);
                    TextView pnItem = (TextView)itemView.findViewById(R.id.phone_number_display_id);
                    TextView stateItem = (TextView)itemView.findViewById(R.id.state_display_id);
                    pnItem.setText(AppState.statePhoneNumbers.get(key));
                    stateItem.setText((String)key);

                    linearLayout.addView(itemView);
                }
            }

            /*View itemView = layoutInflater.inflate(R.layout.phone_number_item, null);
            TextView pnItem = (TextView)itemView.findViewById(R.id.phone_number_display_id);
            //pnItem.setText("hello");

            linearLayout.addView(itemView); */

            builder.setView(dialogView);
            builder.show();

        } else if (view.equals(sendLocationAsSMSView)) {
            if (AppState.currentLocation != null) {
                String text = AppState.currentUser.getFirstName()+" "
                        +AppState.currentUser.getLastName()+" with email: "
                        +AppState.currentUser.getEmail()+", needs emergency help at location:"
                        +"latitude: "+AppState.currentLocation.getLatitude()
                        +", longitude: "+AppState.currentLocation.getLongitude();
                if (AppState.currentAddress != null) {
                    text += " Address: "+AppState.currentAddress.getAddressLine(0);
                }
                sendSMS(AppState.emergencyPhoneNumber, text);
                //smsManager.sendTextMessage(AppState.emergencyPhoneNumber, null, text, null, null);
            } else {
                Toast.makeText(getContext(), "Tap on reload to get location first", Toast.LENGTH_LONG).show();
            }

        } else if (view.equals(callDesteward)) {
            Uri uri = Uri.parse("tel:"+AppState.emergencyPhoneNumber);
            Intent intent = new Intent(Intent.ACTION_DIAL, uri);
            //intent.putExtra("sms_body", message);
            startActivity(intent);
        } //else if (view.equals(viewProfile)) {
//            ((HomeActivity)getActivity()).getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.home_main_view, new ProfileFragment())
//                    .addToBackStack("")
//                    .commit();
//        } else if (view.equals(viewMessages)) {
//            ((HomeActivity)getActivity()).getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.home_main_view, AlertsFragment.newInstance(AppState.currentUser.getEmail()
//                            , "Messages"))
//                    .addToBackStack("")
//                    .commit();
//        }
    }

    public void updateView() {
        addressView.setText(AppState.currentAddress.getAddressLine(0));
//        latLongView.setText(""+AppState.currentLocation.getLatitude()+", "+AppState.currentLocation.getLongitude());

        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        /*String dateTimeText = String.format("%i/%i/%i   %i:%i", gregorianCalendar.get(Calendar.YEAR),
                gregorianCalendar.get(Calendar.MONTH),
                gregorianCalendar.get(Calendar.DATE),
                gregorianCalendar.get(Calendar.HOUR_OF_DAY),
                gregorianCalendar.get(Calendar.MINUTE)
        );
        lastUpdatedView.setText(dateTimeText); */

                    lastUpdatedView.setText(""+gregorianCalendar.get(Calendar.YEAR)+
                            "/"+gregorianCalendar.get(Calendar.MONTH)+
                            "/"+gregorianCalendar.get(Calendar.DATE)+
                            "   "+gregorianCalendar.get(Calendar.HOUR_OF_DAY)+
                            ":"+gregorianCalendar.get(Calendar.MINUTE)
                            //gregorianCalendar.get(Calendar.AM_PM)
                    );
    }

    public void refresh() {
        locationView.setVisibility(View.GONE);
        loadingIndicatorView.setVisibility(View.VISIBLE);
        Snackbar.make(locationView, "refreshing...", Snackbar.LENGTH_INDEFINITE).show();
        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
    }

    LocationListener  locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            ServerUtil.reverseGeocode(location.getLatitude(), location.getLongitude(), getContext(), new ServerUtil.OnReverseGeocodeListener() {
                @Override
                public void onResult(List<Address> result) {
                    Address address = result.get(0);
                    AppState.currentAddress = address;
                    AppState.currentLocation = location;

                    Snackbar.make(locationView, "done", Snackbar.LENGTH_LONG).show();
                    AppState.currentLocationData = new LocationData(AppState.currentUser.getEmail(), location, address);

                    updateView();

                    loadingIndicatorView.setVisibility(View.GONE);
                    locationView.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), "Location refreshed successfully", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String errorString) {
                    loadingIndicatorView.setVisibility(View.GONE);
                    locationView.setVisibility(View.VISIBLE);
                    Snackbar.make(locationView, "error occurred", Snackbar.LENGTH_LONG).show();
                    Toast.makeText(getContext(), "Error in internet connection", Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

            String msg = "";

            switch (i) {
                case LocationProvider.AVAILABLE:
                    //Toast.makeText(getBaseContext(), "GPS service available. Location Tracker active", Toast.LENGTH_LONG).show();
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    loadingIndicatorView.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "GPS service unavailable. Location Tracker disabled", Toast.LENGTH_LONG).show();
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    loadingIndicatorView.setVisibility(View.GONE);
                    //Toast.makeText(getContext(), "GPS service temporarily unavailable. Location Tracker may be disabled", Toast.LENGTH_LONG).show();
                    break;
            }
        }
        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {
            Toast.makeText(getContext(), "Location service disabled", Toast.LENGTH_SHORT).show();
            loadingIndicatorView.setVisibility(View.GONE);
        }
    };

    void sendSMS(String phoneNumber, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        //smsManager.sendTextMessage(phoneNumber, null, message, HomeActivity.sentPendingIntent, HomeActivity.deliveredPendingIntent);
        //((HomeActivity)getActivity()).sendSMS(phoneNumber, message);
        //Toast.makeText(getContext(), "Sending SMS.", Toast.LENGTH_LONG).show();
        Uri uri = Uri.parse("smsto:"+phoneNumber);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", message);
        startActivity(intent);
    }
}
