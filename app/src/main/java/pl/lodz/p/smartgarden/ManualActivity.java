package pl.lodz.p.smartgarden;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ManualActivity extends AppCompatActivity {


    private static final String THINGSPEAK_API_KEY_STRING = "api_key";

    private static final String ARDUINO_UPDATE_CHANNEL_ID="282456";
    private static final String ARDUINO_UPDATE_WRITE_API_KEY="HSGRETV61NOF1D9Z";
    private static final String ARDUINO_UPDATE_READ_API_KEY="8BORFPAYUBSUWCDN";

    /* Be sure to use the correct fields for your own app*/
    private static final String THINGSPEAK_FIELD1 = "field1";
    private static final String THINGSPEAK_FIELD2 = "field2";

    private static final String THINGSPEAK_UPDATE_URL = "https://api.thingspeak.com/update?";
    private static final String THINGSPEAK_CHANNEL_URL = "https://api.thingspeak.com/channels/";
    private static final String THINGSPEAK_FEEDS_LAST = "/feeds/last?";

    private int ligthToggle;
    private int pumpToggle;

    ArduinoThreads arduinoThreads = new ArduinoThreads();
    public boolean isFullyLighted, isFullyWatered;
    public Flower flower;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);
        LastStateCheck lastStateCheck = new LastStateCheck();
        lastStateCheck.execute();


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


        final ToggleButton lightButton = (ToggleButton) findViewById(R.id.lightButton);
        lightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFullyLighted) {
                    lightButton.setChecked(false);
                    isFullyLighted = false;
                    updateLigth(0);
                }
                else {
                    lightButton.setChecked(true);
                    isFullyLighted = true;
                    updateLigth(1);
                }
            }
        });

        final ToggleButton waterButton = (ToggleButton) findViewById(R.id.waterButton);
        waterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFullyWatered) {
                    waterButton.setChecked(false);
                    isFullyWatered = false;
                    updatePump(0);
                }
                else {
                    waterButton.setChecked(true);
                    isFullyWatered = true;
                    updatePump(1);
                }

            }
        });

    }

    //To toggle Button onCreate
    public void displayButton(Double val1,Double val2){
        ToggleButton lightToggle = (ToggleButton)findViewById(R.id.lightButton);
        ToggleButton pumpToggle = (ToggleButton)findViewById(R.id.waterButton);
        if(val1==1 && val2==1){
            lightToggle.setChecked(true);
            pumpToggle.setChecked(true);
            new UpdateLastState().execute(1,1);
        }else if(val1==1 && val2==0){
            pumpToggle.setChecked(true);
            lightToggle.setChecked(false);
            new UpdateLastState().execute(1,0);
        }else if(val1==0 && val2==1){
            pumpToggle.setChecked(false);
            lightToggle.setChecked(true);
            new UpdateLastState().execute(0,1);
        }else{
            pumpToggle.setChecked(false);
            lightToggle.setChecked(false);
            new UpdateLastState().execute(0,0);
        }
    }

    public void updateLigth(int lightVal){
        setLigthToggle(lightVal);
        new UpdateLastState().execute(getPumpToggle(),lightVal);
    }

    public void updatePump(int pumpVal){
        setPumpToggle(pumpVal);
        new UpdateLastState().execute(pumpVal,getLigthToggle());
    }

    public int getLigthToggle() {
        return ligthToggle;
    }

    public void setLigthToggle(int ligthToggle) {
        this.ligthToggle = ligthToggle;
    }

    public int getPumpToggle() {
        return pumpToggle;
    }

    public void setPumpToggle(int pumpToggle) {
        this.pumpToggle = pumpToggle;
    }


    private class UpdateLastState extends AsyncTask<Integer,Void,String>{

        @Override
        protected String doInBackground(Integer... integers) {
            try {
                URL url = new URL(THINGSPEAK_UPDATE_URL + THINGSPEAK_API_KEY_STRING + "=" +
                        ARDUINO_UPDATE_WRITE_API_KEY + "&" + THINGSPEAK_FIELD1 + "=" + integers[0] +
                        "&" + THINGSPEAK_FIELD2 + "=" + integers[1]);
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

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    private class LastStateCheck extends AsyncTask<Void,String,String>{


        @Override
        protected String doInBackground(Void... voids) {
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
                displayButton(channel.getDouble(THINGSPEAK_FIELD1),channel.getDouble(THINGSPEAK_FIELD2));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        protected void onPostExecute(String response) {

        }

    }


}

