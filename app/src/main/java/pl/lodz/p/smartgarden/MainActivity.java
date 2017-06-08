package pl.lodz.p.smartgarden;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private String flowerSpinnerValue, modeSpinnerValue;
    private Flower flower;

    private Context mContext;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Get the application context
        mContext = getApplicationContext();

        // Get the activity
        mActivity = MainActivity.this;

        final Spinner flowerSpinner = (Spinner) findViewById(R.id.flowerSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.flowers_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        flowerSpinner.setAdapter(adapter);
        flowerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (flowerSpinner.getSelectedItem().equals("Basil")) {
                    flower = new Basil();
                }

                if(flowerSpinner.getSelectedItem().equals("Mint")) {
                    flower = new Mint();
                    System.out.println("jusia");
                }
                if (flowerSpinner.getSelectedItem().equals("Thyme")) {
                    flower = new Thyme();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final Spinner modeSpinner = (Spinner) findViewById(R.id.modeSpinner);
        ArrayAdapter<CharSequence> adapterTwo = ArrayAdapter.createFromResource(this,
                R.array.mode_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modeSpinner.setAdapter(adapterTwo);


        // Initialize a new Intent instance
        final Intent intent = new Intent(mContext, RandomNumberService.class);

        final Button okButton = (Button) findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modeSpinner.getSelectedItem().equals("Manual")) {
                    // If the service is running then stop it
                    if(isServiceRunning(RandomNumberService.class)){
                        Toast.makeText(mContext, "Service is running...",Toast.LENGTH_SHORT).show();
                        // Stop the service
                        stopService(intent);
                    }else {
                        Toast.makeText(mContext, "Service already stopped...",Toast.LENGTH_SHORT).show();
                    }
                    goToManualActivity();
                }
                else {
                    // If the service is not running then start it
                    if(!isServiceRunning(RandomNumberService.class)){
                        Toast.makeText(mContext, "Service is stopped...",Toast.LENGTH_SHORT).show();
                        // Start the service
                        startService(intent);
                    }else {
                        Toast.makeText(mContext, "Service already running...",Toast.LENGTH_SHORT).show();
                    }
                    goToAutoActivity();
                }
            }
        });
    }

    private void goToManualActivity() {
        Intent intent = new Intent(this, ManualActivity.class);
        intent.putExtra("myFlower", flower);
        startActivity(intent);
    }

    private void goToAutoActivity() {
        Intent intent = new Intent(this, AutoActivity.class);
        intent.putExtra("myFlower", flower);
        startActivity(intent);
    }

    // Custom method to determine whether a service is running
    private boolean isServiceRunning(Class<?> serviceClass){
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        // Loop through the running services
        for(ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                // If the service is running then return true
                return true;
            }
        }
        return false;
    }



}
