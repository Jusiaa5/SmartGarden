package pl.lodz.p.smartgarden;


import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AutoActivity extends AppCompatActivity {

    private static final String TAG = "UsingThingspeakAPI";
    private static final String SENSORVALUE_CHANNEL_ID = "282440";
    private static final String SENSORVALUE_READ_API_KEY = "KDZJPGT1B0P42G6F";
    private static final String THINGSPEAK_API_KEY_STRING = "api_key";
    private static final String ARDUINO_UPDATE_WRITE_API_KEY="HSGRETV61NOF1D9Z";

    /* Be sure to use the correct fields for your own app*/
    private static final String THINGSPEAK_FIELD1 = "field1";
    private static final String THINGSPEAK_FIELD2 = "field2";

    private static final String THINGSPEAK_UPDATE_URL = "https://api.thingspeak.com/update?";
    private static final String THINGSPEAK_CHANNEL_URL = "https://api.thingspeak.com/channels/";
    private static final String THINGSPEAK_FEEDS_LAST = "/feeds/last?";


    volatile boolean running;


    private Flower flower;
    private boolean isFullyWatered, isFullyLighted; // FOR REACTION WITH ARDUINO

    public FetchSensorValues fetchSensorValues = new FetchSensorValues();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        running=true;

        fetchSensorValues.execute();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto);
        flower = getIntent().getParcelableExtra("myFlower");

        TextView flowerName = (TextView) findViewById(R.id.currentFlowerTextView);
        flowerName.setText(flower.getName());
        ImageView flowerImage = (ImageView) findViewById(R.id.flowerImage);
        if (flower.getName().equals("Basil")) {

            flowerImage.setImageResource(R.drawable.basil);
        }
        if (flower.getName().equals("Mint")) {
            flowerImage.setImageResource(R.drawable.mint);
        }
        if (flower.getName().equals("Thyme")) {
            flowerImage.setImageResource(R.drawable.thyme);
        }


    }

    public void displayValue(String lightMessage,String waterMessage){
        TextView lightValue = (TextView)findViewById(R.id.lightMessageTextView);
        TextView waterValue =(TextView)findViewById(R.id.waterMessageTextView);

        lightValue.setText(lightMessage);
        waterValue.setText(waterMessage);
    }

    //Generics
    //First Void for doInbackground
    //Second Void for onProgressUpdate
    //Last String is for return value of doInBackground
    //passed to onPostExecute

    private class FetchSensorValues extends AsyncTask<Void,String,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(Void... voids) {
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
                    publishProgress(stringBuilder.toString());
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(String... response) {
            super.onProgressUpdate(response);
            if(response == null) {
                Log.e("Error","An Error Occured");
                return;
            }

            try {
                JSONObject channel = (JSONObject) new JSONTokener(response[0]).nextValue();
                String lightMessage = "";
                String waterMessage = "";
                lightMessage = "Light Value : "+channel.getDouble(THINGSPEAK_FIELD1);
                waterMessage = "Water Value : "+channel.getDouble((THINGSPEAK_FIELD2));
                displayValue(lightMessage,waterMessage);
                if(channel.getDouble(THINGSPEAK_FIELD1) < 50 && channel.getDouble(THINGSPEAK_FIELD2) <50){
                    new UpdateLastStates().execute(0,0);
                }else if(channel.getDouble(THINGSPEAK_FIELD1) >= 50 && channel.getDouble(THINGSPEAK_FIELD2) <50){
                    new UpdateLastStates().execute(0,1);
                }else if(channel.getDouble(THINGSPEAK_FIELD1) < 50 && channel.getDouble(THINGSPEAK_FIELD2) >= 50){
                    new UpdateLastStates().execute(1,0);
                }else{
                    new UpdateLastStates().execute(1,1);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if(running)
                new FetchSensorValues().execute();
        }
    }

    private class UpdateLastStates extends AsyncTask<Integer,Void,String>{


        @Override
        protected String doInBackground(Integer... integers) {
            int pumpState = integers[0];
            int lightState = integers[1];
            // get the values from parameters and pass it to our Thingspeak channel...
            try {
                URL url = new URL(THINGSPEAK_UPDATE_URL + THINGSPEAK_API_KEY_STRING + "=" +
                        ARDUINO_UPDATE_WRITE_API_KEY + "&" + THINGSPEAK_FIELD1 + "=" + pumpState +
                        "&" + THINGSPEAK_FIELD2 + "=" + lightState);
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
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        running=false;
    }
}

