package pl.lodz.p.smartgarden;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class RandomNumberService extends Service {
    ArduinoThreads arduinoThreads = new ArduinoThreads();
    MainActivity mainActivity = new MainActivity();
    AutoActivity autoActivity = new AutoActivity();
    private Handler mHandler;
    private Runnable mRunnable;
    public double c1 = 50, c2 = 50;


    public RandomNumberService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Send a notification that service is started
        Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();

        // Do a periodic task
        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                doTask();
            }

        };
        mHandler.postDelayed(mRunnable, 2000);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service destroyed", Toast.LENGTH_SHORT).show();
        mHandler.removeCallbacks(mRunnable);
    }

    // Custom method to do a task
    public void doTask() {
        arduinoThreads.fetchExecute();
        if(arduinoThreads.getLightValue() > 50){
            notification();
            //send notification and update last state of lamp or pump if needed....
        }
        mHandler.postDelayed(mRunnable, 5000);

    }

    public void notification() {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
        Notification myNotify = new Notification.Builder(this)
                .setContentTitle("Merhaba Notify !")
                .setContentText("www.serifgungor.com")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pIntent)
                .setAutoCancel(true).build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, myNotify);
    }

    public String checkApp() {
        String a="";
        ActivityManager activityManager = (ActivityManager) this
                .getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfos = activityManager
                .getRunningAppProcesses();
        for (int id = 0; id < processInfos.size(); id++) {
            a = processInfos.get(id).processName;
        }
        return a;
    }

}
