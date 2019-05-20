package weplay.auptsoft.locationtracker.controllers;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentProvider;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import org.json.JSONException;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import weplay.auptsoft.locationtracker.AlertDialogActivity;
import weplay.auptsoft.locationtracker.EnsureSettingsActivity;
import weplay.auptsoft.locationtracker.HomeActivity;
import weplay.auptsoft.locationtracker.R;
import weplay.auptsoft.locationtracker.models.Alert;
import weplay.auptsoft.locationtracker.models.AppState;
import weplay.auptsoft.locationtracker.models.LocationData;
import weplay.auptsoft.locationtracker.models.User;

/**
 * Created by Andrew on 6.2.19.
 */

public class BackService extends Service {
    LocationManager locationManager;

    NotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Toast.makeText(this, "job started", Toast.LENGTH_LONG).show();
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20000, 1, locationListener);

            AppState.sharedPreferences = getSharedPreferences("SHARED_PREFERENCES", MODE_PRIVATE);
            AppState.sharedPreferences.edit().putBoolean(AppState.WORK_IN_BACKGROUND_PREF, true).apply();
        } else {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("notification_id", "Location notification", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("Channel for Location Tracker");

            notificationManager = (NotificationManager) getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        } else {
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Job stopped", Toast.LENGTH_LONG).show();
        locationManager.removeUpdates(locationListener);
        notificationManager.cancel(12);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            ServerUtil.reverseGeocode(location.getLatitude(), location.getLongitude(), getApplication(), new ServerUtil.OnReverseGeocodeListener() {
                @Override
                public void onResult(List<Address> result) {
                    //Toast.makeText(getApplication(), result.get(0).getAddressLine(0), Toast.LENGTH_LONG).show();
                    AppState.currentAddress = result.get(0);
                    AppState.currentLocation = location;
                    AppState.currentLocationData = new LocationData(AppState.currentUser.getEmail(), location, result.get(0));

                    sendBroadcast(new Intent(AppState.LOCATION_UPDATE_BROADCAST));

                    User user = User.getFromPreference(AppState.sharedPreferences);


                    if (user != null) {
                        LocationData locationData = new LocationData(user.getEmail(), location, result.get(0));
                        try {
                            LocationData.save(AppState.sharedPreferences, locationData);
                        } catch (JSONException je) {
                            Toast.makeText(getBaseContext(), "error while saving", Toast.LENGTH_SHORT).show();
                        }

                        Utility.uploadLocationData(locationData, new ServerUtil.OnResultListener() {
                            @Override
                            public void onResult(String data, HttpURLConnection httpURLConnection) {
                                //Toast.makeText(getBaseContext(), data, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(String errorString) {
                                Toast.makeText(getBaseContext(), errorString, Toast.LENGTH_SHORT).show();
                            }
                        });

                        getAlerts("");
                        getAlerts(AppState.currentUser.getEmail());

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext());
                        builder.setContentText(result.get(0).getAddressLine(0));
                        builder.setContentTitle("Your current location");
                        builder.setSmallIcon(R.drawable.ic_location_on_black_24dp);
                        builder.setPriority(NotificationCompat.PRIORITY_HIGH);

                        Intent intent = new Intent(getBaseContext(), HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        PendingIntent contentPendingIntent = PendingIntent.getActivity(getBaseContext(), 5, intent, 0);
                        builder.setContentIntent(contentPendingIntent);

                        notificationManager.notify(12, builder.build());
                    } else {
                        //Toast.makeText(getBaseContext(), result.get(0).getAddressLine(0), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(String errorString) {
                    LocationData locationData = new LocationData();
                    locationData.setEmail(AppState.currentUser.getEmail());
                    locationData.setAddress("");
                    locationData.setLatitude(location.getLatitude());
                    locationData.setLongitude(location.getLongitude());

                    try {
                        LocationData.save(AppState.sharedPreferences, locationData);
                        //Toast.makeText(getBaseContext(), "recorded", Toast.LENGTH_SHORT).show();
                    } catch (JSONException je) {
                        Toast.makeText(getBaseContext(), "not recorded", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(getApplication(), "Internet connection unavailable or too slow. Retrying", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getBaseContext(), "GPS service unavailable. Location Tracker disabled", Toast.LENGTH_LONG).show();
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    //Toast.makeText(getBaseContext(), "GPS service temporarily unavailable. Location Tracker may be disabled", Toast.LENGTH_LONG).show();
                    break;
            }
        }


        @Override
        public void onProviderEnabled(String s) {
            Toast.makeText(getBaseContext(), "GPS enabled. Location Tracker active", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onProviderDisabled(String s) {
            Intent intent = new Intent(getBaseContext(), EnsureSettingsActivity.class);
            startActivity(intent);
        }
    };

    void getAlerts(final String email) {
        String dateString = AppState.sharedPreferences.getString(AppState.LAST_UPDATE_DATE_PREF, "");
        String timeString = AppState.sharedPreferences.getString(AppState.LAST_UPDATE_TIME_PREF, "");

        //Toast.makeText(this, dateString+" "+timeString, Toast.LENGTH_LONG).show();

        Utility.getAlerts(email, dateString, timeString, new Utility.OnAlertListener() {
            @Override
            public void onAlert(ArrayList<Alert> alerts, String msg) {
                if(email.equals("")) {
                    AppState.currentAlerts = alerts;
                    postNotification(13, "New Alert", alerts.get(0).getContent(), "alert");

                    Intent intent = new Intent(getBaseContext(), AlertDialogActivity.class);
                    intent.putExtra("type", "alert");
                    startActivity(intent);

                    Utility.MDateTime mDateTime = new Utility.MDateTime(new GregorianCalendar());
                    AppState.sharedPreferences.edit().putString(AppState.LAST_UPDATE_DATE_PREF, mDateTime.getDate())
                        .putString(AppState.LAST_UPDATE_TIME_PREF, mDateTime.getTime()).apply();


                    //Toast.makeText(getBaseContext(), "alert = "+msg, Toast.LENGTH_LONG).show();
                } else {
                    postNotification(14, "New message", alerts.get(0).getContent(), "message");

                    AppState.currentMessages = alerts;
                    Intent intent = new Intent(getBaseContext(), AlertDialogActivity.class);
                    intent.putExtra("type", "message");
                    startActivity(intent);

                    Utility.MDateTime mDateTime = new Utility.MDateTime(new GregorianCalendar());
                    AppState.sharedPreferences.edit().putString(AppState.LAST_UPDATE_DATE_PREF, mDateTime.getDate())
                        .putString(AppState.LAST_UPDATE_TIME_PREF, mDateTime.getTime()).commit();
                    //Toast.makeText(getBaseContext(), "message = "+msg, Toast.LENGTH_LONG).show();


                }
            }

            @Override
            public void onError(String errorMsg) {
                //Toast.makeText(getBaseContext(), "error = "+email+" "+errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    void postNotification(int notificationId, String title, String msg, String type) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext());
        builder.setContentText(msg);
        builder.setContentTitle(title);
        builder.setSmallIcon(R.drawable.ic_location_on_black_24dp);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setVibrate(new long[]{500, 1000});
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        //RingtoneManager ringtoneManager = getR

        Intent intent = new Intent(getBaseContext(), HomeActivity.class);
        intent.putExtra("type", type);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent contentPendingIntent = PendingIntent.getActivity(getBaseContext(), 5, intent, 0);
        builder.setContentIntent(contentPendingIntent);

        notificationManager.notify(notificationId, builder.build());
    }
}
