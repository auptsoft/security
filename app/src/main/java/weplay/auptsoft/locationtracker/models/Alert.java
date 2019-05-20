package weplay.auptsoft.locationtracker.models;

import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Andrew on 12.2.19.
 */

public class Alert {
    private int id;
    private String title;
    private String content;
    private String createdBy;
    private GregorianCalendar dateTime;
    private boolean isNew;

    public Alert() {
        id = 0;
        title = "";
        content = "";
        createdBy = "";
        dateTime = new GregorianCalendar();
        isNew = true;
    }

    public Alert(int id, String title, String content, String createdBy, GregorianCalendar dateTime) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdBy = createdBy;
        this.dateTime = dateTime;
        this.isNew = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public GregorianCalendar getDateTime() {
        return dateTime;
    }

    public void setDateTime(GregorianCalendar dateTime) {
        this.dateTime = dateTime;
    }

    public String getDateTimeString() {
        String dateString =""+dateTime.get(Calendar.YEAR)+
                "/"+dateTime.get(Calendar.MONTH)+
                "/"+dateTime.get(Calendar.DATE);
        String timeString = dateTime.get(Calendar.HOUR_OF_DAY)+
                ":"+dateTime.get(Calendar.MINUTE);
        if (dateTime.get(Calendar.DAY_OF_MONTH) == new GregorianCalendar().get(Calendar.DAY_OF_MONTH)){
            return "today  "+timeString;
        } else  {
            return dateString+"  "+timeString;
        }
    }

    public static String toJson(Alert alert) throws JSONException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.accumulate("id", alert.getId());
        jsonObject.accumulate("title", alert.getTitle());
        jsonObject.accumulate("content", alert.getContent());

        int year = alert.getDateTime().get(Calendar.YEAR);
        int month = alert.getDateTime().get(Calendar.MONTH);
        int day = alert.getDateTime().get(Calendar.DAY_OF_MONTH);

        int hour = alert.getDateTime().get(Calendar.HOUR_OF_DAY);
        int minute = alert.getDateTime().get(Calendar.MINUTE);
        int second = alert.getDateTime().get(Calendar.SECOND);

        String date = day+"/"+month+"/"+year;
        String time = hour+":"+minute+":"+second;

        jsonObject.accumulate("date", date);
        jsonObject.accumulate("time", time);
        jsonObject.accumulate("isNew", alert.isNew);

        return jsonObject.toString();
    }


    public static Alert fromJson(String jsonString) throws  JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);

        //int id = jsonObject.getInt("id");
        String title = jsonObject.getString("title");
        String content = jsonObject.getString("content");
        String createdBy = jsonObject.getString("createdBy");

        String date = jsonObject.getString("date");
        String time = jsonObject.getString("time");

        boolean isNew;
        try {
            isNew = jsonObject.getBoolean("isNew");
        } catch (Exception e) {
            isNew = true;
        }

        String[] dateSplit = date.split("/"); //d/m/yyyy h:m:s
        String[] timeSplit = time.split(":");

        int day = Integer.parseInt(dateSplit[0]);
        int month = Integer.parseInt(dateSplit[1]);
        int year = Integer.parseInt(dateSplit[2]);

        int hour = Integer.parseInt(timeSplit[0]);
        int minute = Integer.parseInt(timeSplit[1]);
        int second = Integer.parseInt(timeSplit[2]);

        GregorianCalendar dateTime = new GregorianCalendar(year, month, day, hour, minute, second);

        Alert alert = new Alert(0, title, content, createdBy, dateTime);
        alert.isNew = isNew;

        return alert;
    }

    public static ArrayList<Alert> fromJsonArray(String jsonString) throws JSONException{
        ArrayList<Alert> alertsList = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(jsonString);
        for (int i=0; i<jsonArray.length(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            alertsList.add(fromJson(jsonObject.toString()));
            //alertsList.add(new Alert(0, "title", "content", "author", new GregorianCalendar()));
            //alertsList.add(new Alert(0, "", jsonObject.getString("email"), "",new GregorianCalendar()));
        }

        return alertsList;
    }

    public static String toJsonArray(ArrayList<Alert> alertList) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (int i=0; i<alertList.size(); i++){
            JSONObject jsonObject = new JSONObject(toJson(alertList.get(i)));
            jsonArray.put(jsonObject);
        }
        return jsonArray.toString();
    }

    public static ArrayList<Alert> getAllFromPreference(SharedPreferences sharedPreferences) throws JSONException {
        String locationDataJson = sharedPreferences.getString(AppState.ALL_ALERTS_PREF_KEY, "[]");
        ArrayList<Alert> alertsList = new ArrayList<>();
        try {
            alertsList = fromJsonArray(locationDataJson);
        } catch (JSONException je) {

        }

        return alertsList;
    }

    public static void saveAllToPreference(ArrayList<Alert> alertsList, SharedPreferences sharedPreferences) throws  JSONException{
        String alertsJson = toJsonArray(alertsList);
        sharedPreferences.edit().putString(AppState.ALL_ALERTS_PREF_KEY, alertsJson).apply();
    }

    public static void save(Alert alert, SharedPreferences sharedPreferences) throws JSONException {
        ArrayList<Alert> alerts = getAllFromPreference(sharedPreferences);
        alerts.add(alert);
        saveAllToPreference(alerts, sharedPreferences);
    }

    public static String removeBackSlash(String string) {
        return string.replace("\\", "");
    }
}
