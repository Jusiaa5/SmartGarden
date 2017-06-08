package pl.lodz.p.smartgarden;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

/**
 * Created by gokhan on 3.6.2017.
 */
//BH533T6SIV809NSU


public class ArduinoThreads {


    private static final String TAG = "UsingThingspeakAPI";
    private static final String SENSORVALUE_CHANNEL_ID = "282440";
    private static final String SENSORVALUE_WRITE_API_KEY = "94O7TRQG51UOX5NC";
    private static final String SENSORVALUE_READ_API_KEY = "KDZJPGT1B0P42G6F";
    private static final String THINGSPEAK_API_KEY_STRING = "api_key";

    private static final String ARDUINO_UPDATE_CHANNEL_ID = "282456";
    private static final String ARDUINO_UPDATE_WRITE_API_KEY = "HSGRETV61NOF1D9Z";
    private static final String ARDUINO_UPDATE_READ_API_KEY = "8BORFPAYUBSUWCDN";

    /* Be sure to use the correct fields for your own app*/
    private static final String THINGSPEAK_FIELD1 = "field1";
    private static final String THINGSPEAK_FIELD2 = "field2";

    private static final String THINGSPEAK_UPDATE_URL = "https://api.thingspeak.com/update?";
    private static final String THINGSPEAK_CHANNEL_URL = "https://api.thingspeak.com/channels/";
    private static final String THINGSPEAK_FEEDS_LAST = "/feeds/last?";
    private double lightValue;
    private double pumpValue;
    private int toggleLight;
    private int togglePump;

    public double getLightValue() {
        return lightValue;
    }

    public void setLightValue(double lightValue) {
        this.lightValue = lightValue;
    }

    public double getPumpValue() {
        return pumpValue;
    }

    public void setPumpValue(double pumpValue) {
        this.pumpValue = pumpValue;
    }

    public int getToggleLight() {
        return toggleLight;
    }

    public void setToggleLight(int toggleLight) {
        this.toggleLight = toggleLight;
    }

    public int getTogglePump() {
        return togglePump;
    }

    public void setTogglePump(int togglePump) {
        this.togglePump = togglePump;
    }


    class FetchThingspeakTask extends AsyncTask<Void, String, String> {

        protected void onPreExecute() {

        }

        protected String doInBackground(Void... urls) {
            try {
                URL url = new URL(THINGSPEAK_CHANNEL_URL + SENSORVALUE_CHANNEL_ID +
                        THINGSPEAK_FEEDS_LAST + THINGSPEAK_API_KEY_STRING + "=" +
                        SENSORVALUE_READ_API_KEY + "");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            if (values == null) {
                Log.e("Error", "An Error Occured");
                return;
            }

            try {
                JSONObject channel = (JSONObject) new JSONTokener(values[0]).nextValue();
                setLightValue(channel.getDouble(THINGSPEAK_FIELD1));
                setPumpValue(channel.getDouble(THINGSPEAK_FIELD2));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        protected void onPostExecute(String response) {
            if (response == null) {
                Log.e("Error", "An Error Occured");
                return;
            }

            try {
                JSONObject channel = (JSONObject) new JSONTokener(response).nextValue();
                setLightValue(channel.getDouble(THINGSPEAK_FIELD1));
                setPumpValue(channel.getDouble(THINGSPEAK_FIELD2));

                Log.i("FETCH", "Fetch Result : " + getLightValue());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    } //Read The Sensor Values from Thingspeak




    public void fetchExecute() {
        new FetchThingspeakTask().execute();
    }

    /*
        =====UNUSED UPDATE AND EXECUTE METHODS=====
    class UpdateComponentState extends AsyncTask<Void, Void, String> {
        protected void onPreExecute() {
        }

        protected String doInBackground(Void... urls) {
            try {
                URL url = new URL(THINGSPEAK_UPDATE_URL + THINGSPEAK_API_KEY_STRING + "=" +
                        ARDUINO_UPDATE_WRITE_API_KEY + "&" + THINGSPEAK_FIELD1 + "=" + getTogglePump() +
                        "&" + THINGSPEAK_FIELD2 + "=" + getToggleLight());
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            Log.i("Post EXECUTE: ", " Thread Closing");
        }
    } //Toggle Pump and Light

    class UpdateThingspeakTask extends AsyncTask<Void, Void, String> {

        protected void onPreExecute() {
        }

        protected String doInBackground(Void... urls) {
            try {
                URL url = new URL(THINGSPEAK_UPDATE_URL + THINGSPEAK_API_KEY_STRING + "=" +
                        SENSORVALUE_WRITE_API_KEY + "&" + THINGSPEAK_FIELD1 + "=" + getLightValue() +
                        "&" + THINGSPEAK_FIELD2 + "=" + getPumpValue());
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            // We completely ignore the response
            // Ideally we should confirm that our update was successful
        }
    } //Update Sensor Value to Thingspeak(not impo)

    class FetchLastState extends AsyncTask<Void, Void, String> {
        protected void onPreExecute() {

        }

        protected String doInBackground(Void... urls) {
            try {
                URL url = new URL(THINGSPEAK_CHANNEL_URL + ARDUINO_UPDATE_CHANNEL_ID +
                        THINGSPEAK_FEEDS_LAST + THINGSPEAK_API_KEY_STRING + "=" +
                        ARDUINO_UPDATE_READ_API_KEY + "");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if (response == null) {
                Log.e("Error", "An Error Occured");
                return;
            }

            try {
                JSONObject channel = (JSONObject) new JSONTokener(response).nextValue();
                setTogglePump((int) channel.getDouble(THINGSPEAK_FIELD1));
                setToggleLight((int) channel.getDouble(THINGSPEAK_FIELD2));

                Log.i("FETCH", "Fetch Result : " + getToggleLight());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public String fetchReturn() {
        try {
            return new FetchThingspeakTask().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return "yok";
    }

    public void updateExecute(double v1, double v2) {
        setLightValue(v1);
        setPumpValue(v2);
        new UpdateThingspeakTask().execute();
    }


    public void fetchArduinoState() {
        new FetchLastState().execute();
    }
*/
}
