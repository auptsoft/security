package weplay.auptsoft.locationtracker.controllers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Andrew Oshodin on 6/6/2018.
 */

public class ServerUtil {

    public static class HeaderItem {
        private String property;
        private String value;

        public HeaderItem(String property, String value) {
            this.property = property;
            this.value = value;
        }

        public String getProperty() {
            return property;
        }

        public String getValue() {
            return value;
        }
    }

    public HeaderItem getHeaderItem(String property, String value) {
        return new HeaderItem(property, value);
    }

    private static Handler handler;

    public interface OnResultListener {
        public void onResult(String data, HttpURLConnection httpURLConnection);
        public void onError(String errorString);
    }


    /*public interface OnGetHealthServiceArrayListListener {
        public void onResult(ArrayList<HealthService> healthServices);

        public void onError(String errorString);
    }*/

    public interface OnLoadImageListener {
        public void onLoadImage(Bitmap bitmap);
        public void onError(Bitmap bitmap, String errorString);
    }

    public interface OnGetLatLngListener {
        public void onGetLatLng(double lat, double lng);
        public void onError(String errorString);
    }

    public interface OnPostReturnListener {
        public void onPostReturn(int code, String jsonString);
        public void onError(String errorString);
    }

    public interface OnReverseGeocodeListener {
        void onResult(List<Address> result);
        void onError(String errorString);
    }

    public interface OnToggleFavouriteListener {
        void onResult(String result);
        void onError(String errorString);
    }

    public static boolean sentGetRequest(final String urlString, final OnResultListener onResultListener) {
        handler = new Handler();

        new Thread(new Runnable() {
            @Override
            public void run() {

                URL url;
                try {
                    url = new URL(urlString);
                } catch (final IOException ioe) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onResultListener.onError(ioe.getMessage());
                        }
                    });
                    return;
                }
                InputStream inputStream;
                try {
                    //URLConnection connection = url.openConnection();
                    //connection.setDoInput(true);
                    //connection.setDoOutput(true);
                    //connection.setConnectTimeout(5000);
                    //connection.connect();
                    //inputStream = connection.getInputStream();
                    inputStream = url.openStream();
                } catch (final IOException ioe) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onResultListener.onError(ioe.getMessage());
                            ioe.printStackTrace();
                        }
                    });
                    return;
                }

                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line = "";
                    if ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                        while (true) {
                            line = bufferedReader.readLine();
                            if (line == null) break;
                            stringBuilder.append(line);
                        }
                        final String outStr = stringBuilder.toString();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                onResultListener.onResult(outStr, null);
                            }
                        });
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                onResultListener.onError("no data in the  server");
                            }
                        });
                    }
                } catch (final IOException ioe) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onResultListener.onError(ioe.getMessage());
                        }
                    });
                }
            }
        }).start();

        return true;
    }

    public static void getBitmap(final String url, final OnLoadImageListener onLoadImageListener) {
        getBitmap(url, "", onLoadImageListener);
    }

    public static void getBitmap(final String url, String id, final OnLoadImageListener onLoadImageListener) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL imageUrl = new URL(url);
                    final Bitmap bitmap = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onLoadImageListener.onLoadImage(bitmap);
                        }
                    });
                } catch (final IOException ioe) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onLoadImageListener.onError(null, ioe.getMessage());
                        }
                    });
                }
            }
        }).start();
    }

    /*public static ArrayList<Tip> getAllTipsArrayList(ArrayList<HashMap<String, String>> hashMapArrayList) {
        ArrayList<Tip> tipArrayList = new ArrayList<>();

        for (HashMap<String, String> stringHashMap : hashMapArrayList) {
            String uniqueId = stringHashMap.get(Tip.KEY_UNIQUE_ID);
            String tipString = stringHashMap.get(Tip.KEY_TIP);
            String imageUrl = stringHashMap.get(Tip.KEY_IMAGE_URL);
            String others = "";

            Tip tip1 = new Tip(uniqueId, tipString, imageUrl, others);
            tipArrayList.add(tip1);
        }
        return tipArrayList;
    } */


    /*
    public static void getAllSpecialists(final Context context, String urlString, boolean performSync,
                                         final OnGetSpecialistsArrayListListener onGetSpecialistsArrayListListener) {
        final Specialist specialist = new Specialist();
        final ArrayList<HashMap<String, String>> hashMapArrayList = specialist.get(context, null, null);
        if (performSync || hashMapArrayList.size()<1) {
            getJsonStringFromServer(urlString, new OnResultListener() {
                @Override
                public void onResult(String jsonString) {
                    ArrayList<HashMap<String, String>> hashMapArrayList1 = specialist.getFromJsonString(context, jsonString);
                    onGetSpecialistsArrayListListener.onResult(getAllSpecialistArrayList(hashMapArrayList1));
                    saveSpecialistsToDatabase(context, hashMapArrayList1);
                }

                @Override
                public void onError(String errorString) {
                    onGetSpecialistsArrayListListener.onError(errorString);
                }
            });
        }
    } */

    public static void getLatLng(final Context context, final String addressString, final OnGetLatLngListener onGetLatLngListener) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context);
                if (geocoder.isPresent()) {
                    try {
                        List<Address> addresses = geocoder.getFromLocationName(addressString, 1);
                        final Address address = addresses.get(0);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                onGetLatLngListener.onGetLatLng(address.getLatitude(), address.getLongitude());
                            }
                        });
                    } catch (final IOException ioe) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                onGetLatLngListener.onError("Cannot get maps. Try checking your internet connection");
                            }
                        });
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onGetLatLngListener.onError("This phone cannot perform this operation");
                        }
                    });
                }
            }
        }).start();
    }

    public static void sendPostRequest(String urlString, ArrayList<String[]> arrayList, OnResultListener onResultListener) {
        sendPostRequest(urlString, encodeParameters(arrayList), onResultListener);
    }

    public static void sendPostRequest(final String urlString, final String encodedString, final OnResultListener onResultListener) {
        handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url;
                final HttpURLConnection httpURLConnection;
                InputStream inputStream;
                try {
                    url = new URL(urlString);
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setReadTimeout(20000);
                    httpURLConnection.setFollowRedirects(true);

                    httpURLConnection.setRequestProperty("User-Agent", "Android Client");
                    httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
                    dataOutputStream.write(encodedString.getBytes());

                    inputStream = httpURLConnection.getInputStream();

                } catch (final IOException ioe) {
                    ioe.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onResultListener.onError(ioe.getMessage());
                        }
                    });
                    return;
                }

                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line = "";
                    if ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                        while (true) {
                            line = bufferedReader.readLine();
                            if (line == null) break;
                            stringBuilder.append(line);
                        }
                        final String outStr = stringBuilder.toString();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                onResultListener.onResult(outStr, httpURLConnection);
                            }
                        });
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                onResultListener.onError("no data in the  server");
                            }
                        });
                    }
                } catch (final IOException ioe) {
                    ioe.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onResultListener.onError(ioe.getMessage());
                        }
                    });
                }
            }
        }).start();
    }


    public static void sendGetRequest(final String urlString, final ArrayList<HeaderItem> headerItems, final OnResultListener onResultListener) {
        handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url;
                final HttpURLConnection httpURLConnection;
                InputStream inputStream;
                try {
                    url = new URL(urlString);

                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    //httpURLConnection.setDoOutput(true);
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setReadTimeout(20000);

                    //httpURLConnection.setRequestProperty("User-Agent", "Android Client");
                    //httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    for(HeaderItem headerItem : headerItems) {
                        httpURLConnection.setRequestProperty(headerItem.getProperty(), headerItem.getValue());
                    }

                    //DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
                    //dataOutputStream.write(encodedString.getBytes());

                    inputStream = httpURLConnection.getInputStream();

                } catch (final IOException ioe) {
                    ioe.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onResultListener.onError(ioe.getMessage());
                        }
                    });
                    return;
                }

                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line = "";
                    if ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                        while (true) {
                            line = bufferedReader.readLine();
                            if (line == null) break;
                            stringBuilder.append(line);
                        }
                        final String outStr = stringBuilder.toString();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                onResultListener.onResult(outStr, httpURLConnection);
                            }
                        });
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                onResultListener.onError("no data in the  server");
                            }
                        });
                    }
                } catch (final IOException ioe) {
                    ioe.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onResultListener.onError(ioe.getMessage());
                        }
                    });
                }
            }
        }).start();
    }

    public static void sendRequestWithBody(final String urlString, final String method, final ArrayList<HeaderItem> headerItems, final String encodedString, final OnResultListener onResultListener) {
        handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url;
                final HttpURLConnection httpURLConnection;
                InputStream inputStream;
                InputStream errorStream;
                try {
                    url = new URL(urlString);
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setRequestMethod(method);
                    httpURLConnection.setReadTimeout(20000);

                    //httpURLConnection.setRequestProperty("User-Agent", "Android Client");
                    //httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");



                    for(HeaderItem headerItem : headerItems) {
                        httpURLConnection.setRequestProperty(headerItem.getProperty(), headerItem.getValue());
                    }

                    DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
                    dataOutputStream.write(encodedString.getBytes());

                    inputStream = httpURLConnection.getInputStream();

                } catch (final IOException ioe) {
                    try {

                        url = new URL(urlString);
                        HttpURLConnection errorHttpURLConnection = (HttpURLConnection) url.openConnection();
                        errorHttpURLConnection.setDoOutput(true);
                        errorHttpURLConnection.setRequestMethod(method);
                        errorHttpURLConnection.setReadTimeout(20000);

                        //httpURLConnection.setRequestProperty("User-Agent", "Android Client");
                        //httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");



                        for(HeaderItem headerItem : headerItems) {
                            errorHttpURLConnection.setRequestProperty(headerItem.getProperty(), headerItem.getValue());
                        }

                        DataOutputStream dataOutputStream = new DataOutputStream(errorHttpURLConnection.getOutputStream());
                        dataOutputStream.write(encodedString.getBytes());


                        errorStream = errorHttpURLConnection.getErrorStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(errorStream));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line = "";
                        if ((line = bufferedReader.readLine()) != null) {
                            stringBuilder.append(line);
                            while (true) {
                                line = bufferedReader.readLine();
                                if (line == null) break;
                                stringBuilder.append(line);
                            }
                            final String outStr = stringBuilder.toString();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onResultListener.onError(outStr);
                                }
                            });
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onResultListener.onError("no error data in the  server");
                                }
                            });
                        }
                    } catch (Exception e) {
                        ioe.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                onResultListener.onError(ioe.getMessage() + " "+ioe.getLocalizedMessage());
                            }
                        });
                    }
                    return;
                }

                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line = "";
                    if ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                        while (true) {
                            line = bufferedReader.readLine();
                            if (line == null) break;
                            stringBuilder.append(line);
                        }
                        final String outStr = stringBuilder.toString();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                onResultListener.onResult(outStr, httpURLConnection);
                            }
                        });
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                onResultListener.onError("no data in the  server");
                            }
                        });
                    }
                } catch (final IOException ioe) {
                    ioe.printStackTrace();
                    try {
                        errorStream = httpURLConnection.getErrorStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(errorStream));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line = "";
                        if ((line = bufferedReader.readLine()) != null) {
                            stringBuilder.append(line);
                            while (true) {
                                line = bufferedReader.readLine();
                                if (line == null) break;
                                stringBuilder.append(line);
                            }
                            final String outStr = stringBuilder.toString();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onResultListener.onError(outStr);
                                }
                            });
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onResultListener.onError("no error data in the  server");
                                }
                            });
                        }
                    } catch (Exception e) {
                        ioe.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                onResultListener.onError(ioe.getMessage() + " "+ioe.getLocalizedMessage());
                            }
                        });
                    }
                }
            }
        }).start();
    }

    public static void sendPostRequest(final String urlString, final ArrayList<HeaderItem> headerItems, final String encodedString, final OnResultListener onResultListener) {
        sendRequestWithBody(urlString, "POST", headerItems, encodedString, onResultListener);
    }

    public static void sendPutRequest(final String urlString, final ArrayList<HeaderItem> headerItems, final String encodedString, final OnResultListener onResultListener) {
        sendRequestWithBody(urlString, "PUT", headerItems, encodedString, onResultListener);
    }

    public static void sendDeleteRequest(final String urlString, final ArrayList<HeaderItem> headerItems, final String encodedString, final OnResultListener onResultListener) {
        sendRequestWithBody(urlString, "DELETE", headerItems, encodedString, onResultListener);
    }

    @NonNull
    public static String encodeParameters(ArrayList<String[]> arrayList) {
        StringBuilder stringBuilder = new StringBuilder();

        int index = 0;
        for (String[] strings : arrayList) {
            stringBuilder.append(strings[0]);
            stringBuilder.append('=');
            try {
                stringBuilder.append(strings[1]); //URLEncoder.encode(strings[1], "UTF-8"));
            } catch (Exception e) {
                return "";
            }
            if (index<arrayList.size()-1) stringBuilder.append('&');
            index++;
        }
        return stringBuilder.toString();
    }

    public static String responseStatus(String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);
        return jsonObject.getString("status");
    }

    public static void reverseGeocode(final double latitude, final double longitude, final Context context, final OnReverseGeocodeListener onReverseGeocodeListener) {
        handler = new Handler();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                    final List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 10);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onReverseGeocodeListener.onResult(addresses);
                        }
                    });
                } catch (final IOException ioe) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onReverseGeocodeListener.onError(ioe.toString());
                        }
                    });
                }
            }
        }).start();

    }
}