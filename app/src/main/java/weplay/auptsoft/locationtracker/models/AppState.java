package weplay.auptsoft.locationtracker.models;

import android.content.SharedPreferences;
import android.location.Address;
import android.location.Location;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Andrew on 5.2.19.
 */

public class AppState {
    public static SharedPreferences sharedPreferences;
    public static User currentUser = null;
    public static final String USER_PREF_KEY = "user";
    public static final String ALL_LOCATION_DATA_PREF_KEY = "location_data";
    public static final String ALL_ALERTS_PREF_KEY = "alerts";

    public static boolean skipStartup = false;

    public static final String WORK_IN_BACKGROUND_PREF = "work_in_background";
    public static final String LAST_UPDATE_DATE_PREF = "last_update_date";
    public static final String LAST_UPDATE_TIME_PREF = "last_update_time";

    public static Address currentAddress;
    public static Location currentLocation;

    public static LocationData currentLocationData;

    public static final String LOCATION_UPDATE_BROADCAST = "webplay.auptsoft.location_broadcast";

    public static final String UPLOAD_URL = "http://destewardgroup.com";
    public static final String REGISTRATION_URL = "http://www.destewardgroup.com/security/register.php";// "http://192.168.43.169/locationtracker/public/register";
    public static final String LOGIN_URL = "http://192.168.43.169/locationtracker/public/login";

    public static final String SERVER_URL = "http://destewardgroup.com/security/php/server_bot.php/";

    //public static final String REGISTRATION_URL = "http://www.google.com";


    public static ArrayList<Alert> currentAlerts = null;
    public static ArrayList<Alert> currentMessages = null;

    public static String emergencyPhoneNumber = "+2348086807131";
    //public static String emergencyPhoneNumber = "+2348143914876"; //debug

    public static HashMap<String, String> statePhoneNumbers = new HashMap<>();
    static {
        statePhoneNumbers.put("Lagos 1", "+2347055462708");
        statePhoneNumbers.put("Lagos 2", "+2348035963919");

        statePhoneNumbers.put("Edo 1", "+2348077773721");
        statePhoneNumbers.put("Edo 2", "+2348037646272");

        statePhoneNumbers.put("Delta", "+23480366684974");

        statePhoneNumbers.put("Abuja 1", "+2347037337653");
        statePhoneNumbers.put("Abuja 2", "+2348032003913");
    }
}
