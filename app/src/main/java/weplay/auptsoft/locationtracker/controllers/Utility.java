package weplay.auptsoft.locationtracker.controllers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import weplay.auptsoft.locationtracker.WebActivity;
import weplay.auptsoft.locationtracker.models.Alert;
import weplay.auptsoft.locationtracker.models.AppState;
import weplay.auptsoft.locationtracker.models.LocationData;
import weplay.auptsoft.locationtracker.models.User;
import weplay.auptsoft.locationtracker.view_models.UserViewModel;

/**
 * Created by Andrew on 6.2.19.
 */

public class Utility {

    public interface OnAlertListener {
        void onAlert(ArrayList<Alert> alerts, String msg);

        void onError(String errorMsg);
    }

    public static void uploadLocationData(LocationData locationData, final ServerUtil.OnResultListener onResultListener) {
        String locationDataString;
        try {
            locationDataString = LocationData.toJson(locationData);
        } catch (JSONException je) {
            onResultListener.onError(je.getMessage());
            return;
        }

        ArrayList<ServerUtil.HeaderItem> headerItems = new ArrayList<>();
        headerItems.add(new ServerUtil.HeaderItem("User-Agent", "Android Client"));
        headerItems.add(new ServerUtil.HeaderItem("Content-Type", "application/x-www-form-urlencoded"));

        String data = "operation=upload&data=" + locationDataString;

        ServerUtil.sendPostRequest(AppState.SERVER_URL, headerItems, data, new ServerUtil.OnResultListener() {
            @Override
            public void onResult(String data, HttpURLConnection httpURLConnection) {
                if (data.contains("success")) {
                    onResultListener.onResult(data, httpURLConnection);

                }
            }

            @Override
            public void onError(String errorString) {
                onResultListener.onError(errorString);
            }
        });
    }

    public static void uploadLocationData(ArrayList<LocationData> locationDataArrayList, final SharedPreferences sharedPreferences, final ServerUtil.OnResultListener onResultListener) {
        String locationDataString;
        try {
            locationDataString = LocationData.toJsonArray(locationDataArrayList);
        } catch (JSONException je) {
            onResultListener.onError(je.getMessage());
            return;
        }

        ArrayList<ServerUtil.HeaderItem> headerItems = new ArrayList<>();
        headerItems.add(new ServerUtil.HeaderItem("User-Agent", "Android Client"));
        headerItems.add(new ServerUtil.HeaderItem("Content-Type", "application/x-www-form-urlencoded"));

        String data = "operation=upload&data=" + locationDataString;

        ServerUtil.sendPostRequest(AppState.UPLOAD_URL, headerItems, data, new ServerUtil.OnResultListener() {
            @Override
            public void onResult(String data, HttpURLConnection httpURLConnection) {
                if (data.contains("success")) {
                    try {
                        LocationData.markAllAsUploaded(sharedPreferences);
                        onResultListener.onResult("uploaded successfully", httpURLConnection);
                    } catch (JSONException je) {
                        onResultListener.onError(je.getMessage());
                    }
                }
            }

            @Override
            public void onError(String errorString) {
                onResultListener.onError(errorString);
            }
        });
    }

    public static void login(UserViewModel userViewModel, SharedPreferences sharedPreferences, final ServerUtil.OnResultListener onResultListener) {
        String userJsonString = "";
        try {
            userJsonString = UserViewModel.toJson(userViewModel);
        } catch (JSONException je) {
            onResultListener.onError(je.getMessage());
            return;
        }

        ArrayList<ServerUtil.HeaderItem> headerItems = new ArrayList<>();
        headerItems.add(new ServerUtil.HeaderItem("User-Agent", "Android Client"));
        headerItems.add(new ServerUtil.HeaderItem("Content-Type", "application/x-www-form-urlencoded"));

        String data = "operation=login&data=" + userJsonString;

        ServerUtil.sendPostRequest(AppState.SERVER_URL, data, new ServerUtil.OnResultListener() {
            @Override
            public void onResult(String data, HttpURLConnection httpURLConnection) {
                //onResultListener.onResult(data, httpURLConnection);

                Result result = null;
                try {
                    result = parseResult(data);
                    if (result.getOutput().equals("success")) {
                        User user = User.fromJson(result.getResponse());
                        AppState.currentUser = user;
                        User.saveToPreference(AppState.sharedPreferences, user);
                        onResultListener.onResult(data, httpURLConnection);
                    } else {
                        onError(result.getResponse());
                    }
                } catch (JSONException je) {
                    if (result == null) {
                        onResultListener.onError("invalid response received from server= " + data + " Contact app vendor");
                    } else {
                        onResultListener.onError("invalid data");
                    }
                }
            }

            @Override
            public void onError(String errorString) {
                onResultListener.onError("Error " + errorString);

            }
        });
    }


    public static void register(UserViewModel userViewModel, final ServerUtil.OnResultListener onResultListener) {
        String userJsonString = "";
        try {
            userJsonString = UserViewModel.toJson(userViewModel);
        } catch (JSONException je) {
            onResultListener.onError(je.getMessage());
            return;
        }

        ArrayList<ServerUtil.HeaderItem> headerItems = new ArrayList<>();
        headerItems.add(new ServerUtil.HeaderItem("User-Agent", "Android Client"));
        headerItems.add(new ServerUtil.HeaderItem("Content-Type", "application/x-www-form-urlencoded"));

        String data = "operation=register&data=" + userJsonString;

        ServerUtil.sendPostRequest(AppState.SERVER_URL, data, new ServerUtil.OnResultListener() {
            @Override
            public void onResult(String data, HttpURLConnection httpURLConnection) {
                //onResultListener.onResult(data, httpURLConnection);

                Result result = null;
                try {
                    result = parseResult(data);
                    if (result.getOutput().equals("success")) {
                        User user = User.fromJson(result.getResponse());
                        AppState.currentUser = user;
                        User.saveToPreference(AppState.sharedPreferences, user);
                        onResultListener.onResult(data, httpURLConnection);
                    } else {
                        onError(result.getResponse());
                    }
                } catch (JSONException je) {
                    if (result == null) {
                        onResultListener.onError("invalid response received from server= " + data + " Contact app vendor");
                    } else {
                        onResultListener.onError("invalid data");
                    }
                }
            }

            @Override
            public void onError(String errorString) {
                onResultListener.onError("Error " + errorString);

            }
        });
    }

    public static void getAlerts(String email, String dateString, String timeString, final OnAlertListener onAlertListener) {
        String data = getFormattedMessageRequest(email, dateString, timeString);

        ServerUtil.sendPostRequest(AppState.SERVER_URL, data, new ServerUtil.OnResultListener() {
            @Override
            public void onResult(String data, HttpURLConnection httpURLConnection) {
                Result result = null;
                try {
                    result = parseResult(data);
                    if (result.getOutput().equals("success")) {
                        ArrayList<Alert> alerts = Alert.fromJsonArray(result.getResponse());
                        onAlertListener.onAlert(alerts, data);
                    } else {
                        onAlertListener.onError("no messages");
                    }
                } catch (JSONException je) {
                    if (result == null) {
                        onAlertListener.onError("invalid result format = " + je.getMessage());
                    } else {
                        onAlertListener.onError("invalid alert format = " + je.getMessage());
                    }
                }
            }

            @Override
            public void onError(String errorString) {
                onAlertListener.onError(errorString);
            }
        });
    }

    public static String getFormattedMessageRequest(String email, String dateString, String timeString) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("date", dateString);
            jsonObject.accumulate("time", timeString);
            jsonObject.accumulate("email", email);

            return "operation=message&data=" + jsonObject.toString();
        } catch (JSONException je) {
            return "";
        }
    }

    public static void reg(Context context, final UserViewModel userViewModel, final ServerUtil.OnResultListener onResultListener) {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, AppState.REGISTRATION_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                onResultListener.onResult("result " + response, null);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onResultListener.onError("error " + error.getLocalizedMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> stringMap = new HashMap<>();

                String userJsonString;
                try {
                    userJsonString = UserViewModel.toJson(userViewModel);
                    stringMap.put("operation", "register");
                    stringMap.put("data", userJsonString);
                } catch (JSONException je) {
                    onResultListener.onError(je.getMessage());
                }

                return stringMap;
            }


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return super.getHeaders();
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
        requestQueue.start();
    }


    public static void useWebView(Context context, String url, String method, String data) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra("data", data);
        intent.putExtra("url", url);
        intent.putExtra("method", method);
        context.startActivity(intent);
    }

    public static Result parseResult(String data) throws JSONException {
        JSONObject jsonObject = new JSONObject(data);
        String output = jsonObject.getString("output");
        String response = jsonObject.getString("response");

        return new Result(output, response);
    }

    public static class Result {
        String output;
        String response;

        public Result(String output, String response) {
            this.output = output;
            this.response = response;
        }

        public String getOutput() {
            return output;
        }

        public void setOutput(String output) {
            this.output = output;
        }

        public String getResponse() {
            return response;
        }

        public void setResponse(String response) {
            this.response = response;
        }

    }

    public static class MDateTime {
        private GregorianCalendar gregorianCalendar;
        String date;
        String time;

        public MDateTime(GregorianCalendar gregorianCalendar) {
            this.gregorianCalendar = gregorianCalendar;
        }

        public GregorianCalendar getGregorianCalendar() {
            return gregorianCalendar;
        }

        public void setGregorianCalendar(GregorianCalendar gregorianCalendar) {
            this.gregorianCalendar = gregorianCalendar;
        }

        public String getDate() {
            return "" + gregorianCalendar.get(Calendar.YEAR) +
                    "/" + gregorianCalendar.get(Calendar.MONTH) +
                    "/" + gregorianCalendar.get(Calendar.DATE);
        }

        public String getTime() {
            return gregorianCalendar.get(Calendar.HOUR_OF_DAY) +
                    ":" + gregorianCalendar.get(Calendar.MINUTE);
        }
    }

}
