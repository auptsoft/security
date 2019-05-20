package weplay.auptsoft.locationtracker.models;

import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Andrew on 5.2.19.
 */

public class User {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String photo_url;

    public User() {
        this(0, "", "", "", "", "");
    }

    public User(int id, String firstName, String lastName, String email, String phoneNumber, String photo_url) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.photo_url = photo_url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public static String toJson(User user) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("id", user.id);
        jsonObject.accumulate("firstName", user.firstName);
        jsonObject.accumulate("lastName", user.lastName);
        jsonObject.accumulate("email", user.email);
        jsonObject.accumulate("phoneNumber", user.phoneNumber);
        //jsonObject.accumulate("photo_url", user.photo_url);

        return jsonObject.toString();
    }

    public static User fromJson(String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);

        int id = 0;
        try {
            id = jsonObject.getInt("id");
        } catch (JSONException je) {
        }

        String firstName = jsonObject.getString("firstName");
        String lastName = jsonObject.getString("lastName");
        String email = jsonObject.getString("email");
        String phoneNumber = jsonObject.getString("phoneNumber");
        //String photo_url = jsonObject.getString("photo_url");

        User user = new User(id, firstName, lastName, email, phoneNumber, "");

        return user;
    }

    public static User getFromPreference(SharedPreferences sharedPreferences) {
        String userJson = sharedPreferences.getString(AppState.USER_PREF_KEY, null);
        if (userJson != null) {
            try {
                User user = User.fromJson(userJson);
                return user;
            } catch (JSONException je) {
                return null;
            }
        } else {
            return null;
        }
    }

    public static void saveToPreference(SharedPreferences sharedPreferences, User user) throws JSONException {
        String jsonString = User.toJson(user);
        sharedPreferences.edit().putString(AppState.USER_PREF_KEY, jsonString).apply();
    }
}
