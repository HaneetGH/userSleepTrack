package com.haneet.sleepwell.services;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.haneet.sleepwell.MainActivity;

import java.util.Calendar;

public class SleepDetectionService extends Service implements SensorEventListener {
    float accelationSquareRoot;
    private SensorManager sensorManager;
    private long lastUpdate;
    public static final int DETECT_NONE = 0;
    public static final int DETECT_SNORE = 1;
    public static int selectedDetection = DETECT_NONE;
    int snorcound = 0;
    CountDownTimer countDownTimer;
    private com.haneet.sleepwell.DetectorThread detectorThread;
    private com.haneet.sleepwell.RecorderThread recorderThread;
    boolean isCoutStart = false;
    boolean isSleepStart = false;
    public static int snoreValue = 0;

    private View mainView;
    private Button mSleepRecordBtn, mAlarmBtn, mRecordBtn, mTestBtn;
    private TextView txtAbs;

    private Toast mToast;
    double frequencySound;

    private Handler rhandler = new Handler();
    private Handler showhandler = null;
    private Handler alarmhandler = null;

    private Intent intent;
    private PendingIntent pendingIntent;
    private AlarmManager am;

    private SurfaceView sfv;
    private Paint mPaint;
    SharedPreferences sharedpreferences;
    String MyPREFERENCES = "test";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public boolean stopService(Intent name) {

        return super.stopService(name);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //getting systems default ringtone
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        if (recorderThread != null)
            if (recorderThread.isRecording())
                recorderThread.stopRecording();
        if (countDownTimer!=null)
        countDownTimer.cancel();
        prepareThings();
        startLogic();
        // goHomeView();

        //we have some options for service
        //start sticky means service will be explicity started and stopped
        return START_STICKY;
    }

    private void startLogic() {

        Log.d("Start Time", Calendar.getInstance().getTime() + "");
        Log.d("Start Time", Calendar.getInstance().getTime() + "");
        selectedDetection = DETECT_SNORE;
        // alarmThread = new AlarmThread(pendingIntent, am);
        recorderThread = new com.haneet.sleepwell.RecorderThread(showhandler);
        recorderThread.start();
        detectorThread = new com.haneet.sleepwell.DetectorThread(recorderThread,
                alarmhandler);
        detectorThread.start();
      /*  drawThread = new com.haneet.sleepwell.DrawThread(sfv.getHeight() / 2, sfv, mPaint);
        drawThread.start();*/
        // clsOscilloscope.baseLine = sfv.getHeight() / 2;
        // clsOscilloscope.Start(audioRecord, recBufSize, sfv, mPaint);

        mToast = Toast.makeText(getApplicationContext(),
                "Recording & Detecting start", Toast.LENGTH_LONG);
        mToast.show();
        // goListeningView();

    }

    private void prepareThings() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        intent = new Intent(getApplicationContext(), com.haneet.sleepwell.AlarmReceiverActivity.class);
        pendingIntent = PendingIntent.getActivity(getApplicationContext(), 2, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        am = (AlarmManager) getSystemService(ALARM_SERVICE);

        mPaint = new Paint();
        mPaint.setColor(Color.GREEN);

        /**
         * show variable handler
         */
        showhandler = new Handler() {
            public void handleMessage(Message msg) {
                frequencySound = Double.parseDouble(msg.obj.toString());

//                 txtAbs.setText(msg.obj.toString());
                int interval = 1;
                if (frequencySound < 2000 && accelationSquareRoot < 3) {

                   /* Log.d("Start Snore", ++snorcound + "");

                    Log.d("User May Sleeping", Calendar.getInstance().getTime() + "");

                 */
                    if (!isSleepStart)
                        if (!isCoutStart) {
                            isCoutStart = true;
                            countTime();
                        }
                } else {
                    if (isCoutStart) {
                        isCoutStart = false;
                        countDownTimer.cancel();
                    } else {
                        isSleepStart = false;
                        Log.d("User May wake", Calendar.getInstance().getTime() + "");
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("wake", Calendar.getInstance().getTime() + "");
                        editor.commit();
                    }
                }

            }
        }

        ;

        /**
         * Output alarm handler
         */
        alarmhandler = new

                Handler() {
                    public void handleMessage(Message msg) {
                       /* int interval = 1;
                        int i = msg.arg1;
                        if (i != 1)
                            setLevel(i);
                        com.haneet.sleepwell.AlarmStaticVariables.level = frequencySound;
                        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                                + (interval * 1000), pendingIntent);*/
                    }
                }

        ;
    }

    private void countTime() {
        countDownTimer = new CountDownTimer(300000, 1000) {

            public void onTick(long c) {
                // mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
                //here you can have your logic to set text to edittext
                Log.d("seconds remaining: ", "" + c / 1000);

                isCoutStart = true;
            }

            public void onFinish() {
                isCoutStart = false;
                isSleepStart = true;
                //  mTextField.setText("done!");
                Log.d("User Confirm Sleeping", Calendar.getInstance().getTime() + "");
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("sleep", Calendar.getInstance().getTime() + "");
                editor.commit();
            }

        };
        countDownTimer.start();
    }

    public void startOneShoot() {
        int i = 5;
        am.set(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + (i * 1000), pendingIntent);
    }

    public void setLevel(int l) {
        switch (l) {
            case 0:
                com.haneet.sleepwell.AlarmStaticVariables.level = com.haneet.sleepwell.AlarmStaticVariables.level0;
                break;
            case 1:
                com.haneet.sleepwell.AlarmStaticVariables.level = com.haneet.sleepwell.AlarmStaticVariables.level1;
                break;
            case 2:
                com.haneet.sleepwell.AlarmStaticVariables.level = com.haneet.sleepwell.AlarmStaticVariables.level2;
                break;
            case 3:
                com.haneet.sleepwell.AlarmStaticVariables.level = com.haneet.sleepwell.AlarmStaticVariables.level3;
                break;
            default:
                com.haneet.sleepwell.AlarmStaticVariables.level = com.haneet.sleepwell.AlarmStaticVariables.level1;
                break;
        }
    }

    private void goHomeView() {

        if (recorderThread != null) {
            Log.d("Stop Time", Calendar.getInstance().getTime() + "");

            recorderThread.stopRecording();
            recorderThread = null;
        }
        if (detectorThread != null) {
            Log.d("Stop Time", Calendar.getInstance().getTime() + "");
            detectorThread.stopDetection();
            detectorThread = null;
        }
        selectedDetection = DETECT_NONE;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
        }

    }

    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;
        // Movement
        float x = values[0];
        float y = values[1];
        float z = values[2];

        accelationSquareRoot = (x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        long actualTime = event.timestamp;
        // Log.d("values ", accelationSquareRoot + "");
        if (accelationSquareRoot >= 2) //
        {
            //   Log.d("values 2", accelationSquareRoot + "");
            if (actualTime - lastUpdate < 200) {
                return;
            }
            lastUpdate = actualTime;
            Toast.makeText(this, "Device was shuffed", Toast.LENGTH_SHORT)
                    .show();

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}

