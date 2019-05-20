package weplay.auptsoft.locationtracker.models;

import android.content.SharedPreferences;
import android.location.Address;
import android.location.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Andrew on 5.2.19.
 */

public class LocationData {
    private int id;
    private String email;
    private String address;
    private double longitude;
    private double latitude;
    private GregorianCalendar dateTime;
    private boolean uploaded;

    public static int idIndex = 0;

    public LocationData() {
        this(idIndex++, "", "", 0, 0);
    }

    public LocationData(String email, Location location, Address address) {
        this.id = idIndex++;
        this.email = email;
        this.address = address.getAddressLine(0);
        this.latitude = address.getLatitude();
        this.longitude = address.getLongitude();
        this.dateTime = new GregorianCalendar();
        this.uploaded = false;
    }

    public LocationData(int id, String email, String address, double longitude, double latitude) {
        this.id = id;
        this.email = email;
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
        this.dateTime = new GregorianCalendar();
        this.uploaded = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public GregorianCalendar getDateTime() {
        return dateTime;
    }

    public void setDateTime(GregorianCalendar dateTime) {
        this.dateTime = dateTime;
    }

    public boolean isUploaded() {
        return uploaded;
    }

    public void setUploaded(boolean uploaded) {
        this.uploaded = uploaded;
    }

    public static String toJson(LocationData locationData) throws JSONException{
        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("id", locationData.getId());
        jsonObject.accumulate("email", locationData.getEmail());
        jsonObject.accumulate("address", locationData.getAddress());
        jsonObject.accumulate("latitude", locationData.getLatitude());
        jsonObject.accumulate("longitude", locationData.getLongitude());
        jsonObject.accumulate("is_uploaded", locationData.isUploaded());

        int year = locationData.getDateTime().get(Calendar.YEAR);
        int month = locationData.getDateTime().get(Calendar.MONTH);
        int day = locationData.getDateTime().get(Calendar.DAY_OF_MONTH);

        int hour = locationData.getDateTime().get(Calendar.HOUR_OF_DAY);
        int minute = locationData.getDateTime().get(Calendar.MINUTE);
        int second = locationData.getDateTime().get(Calendar.SECOND);

        String date = day+"/"+month+"/"+year;
        String time = hour+":"+minute+":"+second;

        jsonObject.accumulate("date", date);
        jsonObject.accumulate("time", time);

        return jsonObject.toString();
    }

    public static LocationData fromJson(String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);
        int id = jsonObject.getInt("id");
        String email = jsonObject.getString("email");
        String address = jsonObject.getString("address");
        double latitude = jsonObject.getDouble("latitude");
        double longitude = jsonObject.getDouble("longitude");

        String date = jsonObject.getString("date");
        String time = jsonObject.getString("time");
        boolean isUploaded = jsonObject.getBoolean("is_uploaded");

        String[] dateSplit = date.split("/"); //d/m/yyyy h:m:s
        String[] timeSplit = time.split(":");

        int day = Integer.parseInt(dateSplit[0]);
        int month = Integer.parseInt(dateSplit[1]);
        int year = Integer.parseInt(dateSplit[2]);

        int hour = Integer.parseInt(timeSplit[0]);
        int minute = Integer.parseInt(timeSplit[1]);
        int second = Integer.parseInt(timeSplit[2]);

        GregorianCalendar dateTime = new GregorianCalendar(year, month, day, hour, minute, second);

        LocationData locationData = new LocationData(id, email, address, latitude, longitude);
        locationData.setDateTime(dateTime);
        locationData.setUploaded(true);

        return locationData;
    }

    public static ArrayList<LocationData> fromJsonArray(String jsonString) throws JSONException{
        ArrayList<LocationData> locationDataArrayList = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i=0; i<jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                locationDataArrayList.add(LocationData.fromJson(jsonObject.toString()));
            }

            return locationDataArrayList;
    }

    public static String toJsonArray(ArrayList<LocationData> locationDataArrayList) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (int i=0; i<locationDataArrayList.size(); i++){
            JSONObject jsonObject = new JSONObject(LocationData.toJson(locationDataArrayList.get(i)));
            jsonArray.put(jsonObject);
        }
        return jsonArray.toString();
    }

    public static ArrayList<LocationData> getAllFromPreference(SharedPreferences sharedPreferences) throws JSONException {
        String locationDataJson = sharedPreferences.getString(AppState.ALL_LOCATION_DATA_PREF_KEY, "[]");
        ArrayList<LocationData> locationDataArrayList = new ArrayList<>();
        locationDataArrayList = fromJsonArray(locationDataJson);

        return locationDataArrayList;
    }

    public static void saveAllToPreference(ArrayList<LocationData> locationDataArrayList, SharedPreferences sharedPreferences) throws  JSONException{
        String locationDataJson = toJsonArray(locationDataArrayList);
        sharedPreferences.edit().putString(AppState.ALL_LOCATION_DATA_PREF_KEY, locationDataJson).apply();
    }

    public static ArrayList<LocationData> getPending(SharedPreferences sharedPreferences) throws JSONException {
        ArrayList<LocationData> locationDataArrayList = getAllFromPreference(sharedPreferences);

        ArrayList<LocationData> pendingLocationData = new ArrayList<>();

        for(int i=0; i<locationDataArrayList.size(); i++){
            if (!pendingLocationData.get(i).uploaded){
                pendingLocationData.add(pendingLocationData.get(i));
            }
        }

        return pendingLocationData;
    }

    public static void markAllAsUploaded(SharedPreferences sharedPreferences) throws JSONException {
        ArrayList<LocationData> locationDataArrayList = getAllFromPreference(sharedPreferences);
        for(LocationData locationData:locationDataArrayList) {
            locationData.setUploaded(true);
        }
        saveAllToPreference(locationDataArrayList, sharedPreferences);
    }

    public static void save(SharedPreferences sharedPreferences, LocationData locationData) throws JSONException {
        ArrayList<LocationData> locationDataArrayList = getAllFromPreference(sharedPreferences);
        locationDataArrayList.add(locationData);
        saveAllToPreference(locationDataArrayList, sharedPreferences);
    }
}