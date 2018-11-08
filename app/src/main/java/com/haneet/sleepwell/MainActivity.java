package com.haneet.sleepwell;
//package com.haneet.sleepwell.sleepwell;


import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.haneet.sleepwell.services.SleepDetectionService;

import java.util.Calendar;

//import com.haneet.sleepwell.sleepwell.R;

public class MainActivity extends Activity {
    Button reset;
    float accelationSquareRoot;
    private SensorManager sensorManager;
    private boolean color = false;
    private View view;
    private long lastUpdate;
    public static final int DETECT_NONE = 0;
    public static final int DETECT_SNORE = 1;
    public static int selectedDetection = DETECT_NONE;
    int snorcound = 0;
    private com.haneet.sleepwell.DetectorThread detectorThread;
    private com.haneet.sleepwell.RecorderThread recorderThread;
    private com.haneet.sleepwell.DrawThread drawThread;

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
    private TextView sleepstart;
    private TextView sleepstop;
    SharedPreferences sharedpreferences;
    String MyPREFERENCES = "test";

    @Override
    protected void onResume() {
        super.onResume();
        sleepstart.setText("sleep at-->"+sharedpreferences.getString("sleep", "sleep at"));
        sleepstop.setText("wake at-->"+sharedpreferences.getString("wake", "wake at"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                        123);
            }
            return;
        }
        reset = (Button) findViewById(R.id.reset);
        reset.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("sleep", "lsleep at");
                editor.putString("wake", "wake at");
                editor.commit();
                sleepstart.setText("sleep at-->"+sharedpreferences.getString("sleep", "sleep at"));
                sleepstop.setText("wake at-->"+sharedpreferences.getString("wake", "wake at"));

            }
        });
        sleepstart = (TextView) findViewById(R.id.sleepstart);
        sleepstop = (TextView) findViewById(R.id.sleepstop);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        startService(new Intent(this, SleepDetectionService.class));
        sleepstart.setText("sleep at-->"+sharedpreferences.getString("sleep", "sleep at"));
        sleepstop.setText("wake at-->"+sharedpreferences.getString("wake", "wake at"));
        /*
        setTitle("UIC SleepTracker Demo");
//        view.setBackgroundColor(Color.GREEN);

      *//*  sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lastUpdate = System.currentTimeMillis();
        mSleepRecordBtn = (Button) this.findViewById(R.id.btnSleepRecord);
        mAlarmBtn = (Button) findViewById(R.id.btnSelectAlarm);
        mRecordBtn = (Button) findViewById(R.id.btnRecordAlarm);
        mTestBtn = (Button) findViewById(R.id.btnAlarmTest);
        txtAbs = (TextView) findViewById(R.id.txtaverageAbsValue);
        sfv = (SurfaceView) this.findViewById(R.id.SurfaceView);

        intent = new Intent(MainActivity.this, com.haneet.sleepwell.sleepwell.AlarmReceiverActivity.class);
        pendingIntent = PendingIntent.getActivity(MainActivity.this, 2, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        am = (AlarmManager) getSystemService(ALARM_SERVICE);

        mPaint = new Paint();
        mPaint.setColor(Color.GREEN);*//*

         *//**
         * show variable handler
         *//*
        showhandler = new Handler() {
            public void handleMessage(Message msg) {
                frequencySound = Double.parseDouble(msg.obj.toString());

                txtAbs.setText(msg.obj.toString());
                int interval = 1;
                if (frequencySound > 200) {

                    Log.d("Start Snore", ++snorcound + "");
                    if(accelationSquareRoot>3)
                    {
                        Log.d("shake Time", Calendar.getInstance().getTime()+"");
                    }
                }
            }
        }

        ;

        *//**
         * Output alarm handler
         *//*
        alarmhandler = new

                Handler() {
                    public void handleMessage(Message msg) {
				*//*int interval = 1;
				int i = msg.arg1;
				if(i!=1)
				setLevel(i);
				com.haneet.sleepwell.sleepwell.AlarmStaticVariables.level = frequencySound;
				am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
						+ (interval * 1000), pendingIntent);*//*
                    }
                }

        ;

        *//**
         * Sleep Record Button
         *//*
        mSleepRecordBtn.setOnClickListener(new

                                                   OnClickListener() {
                                                       public void onClick(View view) {
                                                           Log.d("Start Time", Calendar.getInstance().getTime()+"");
                                                           selectedDetection = DETECT_SNORE;
                                                           // alarmThread = new AlarmThread(pendingIntent, am);
                                                           recorderThread = new com.haneet.sleepwell.sleepwell.RecorderThread(showhandler);
                                                           recorderThread.start();
                                                           detectorThread = new com.haneet.sleepwell.sleepwell.DetectorThread(recorderThread,
                                                                   alarmhandler);
                                                           detectorThread.start();
                                                           drawThread = new com.haneet.sleepwell.sleepwell.DrawThread(sfv.getHeight() / 2, sfv, mPaint);
                                                           drawThread.start();
                                                           // clsOscilloscope.baseLine = sfv.getHeight() / 2;
                                                           // clsOscilloscope.Start(audioRecord, recBufSize, sfv, mPaint);

                                                           mToast = Toast.makeText(getApplicationContext(),
                                                                   "Recording & Detecting start", Toast.LENGTH_LONG);
                                                           mToast.show();
                                                           // goListeningView();
                                                       }
                                                   });

        *//**
         * Select alarm Button
         *//*
        mAlarmBtn.setOnClickListener(new

                                             OnClickListener() {
                                                 public void onClick(View view) {
                                                     Intent intent = new Intent(MainActivity.this,
                                                             com.haneet.sleepwell.sleepwell.AlarmSelectActivity.class);
                                                     startActivity(intent);
                                                 }
                                             });

        *//**
         * Record name Button
         *//*
        mRecordBtn.setOnClickListener(new

                                              OnClickListener() {
                                                  public void onClick(View view) {

                                                      goHomeView();

                                                  }
                                              });

        *//**
         * Test
         *//*
        mTestBtn.setOnClickListener(new

                                            OnClickListener() {
                                                public void onClick(View view) {
                                                    int level = 1;
                                                    setLevel(level);
                                                    startOneShoot();
                                                }
                                            });
*/
    }
/*
    private Runnable recordActivity = new Runnable() {
        public void run() {
            Intent intent = new Intent(MainActivity.this,
                    com.haneet.sleepwell.sleepwell.AlarmRecordActivity.class);
            startActivity(intent);
        }
    };

    public void startOneShoot() {
        int i = 5;
        am.set(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + (i * 1000), pendingIntent);
    }

    public void setLevel(int l) {
        switch (l) {
            case 0:
                com.haneet.sleepwell.sleepwell.AlarmStaticVariables.level = com.haneet.sleepwell.sleepwell.AlarmStaticVariables.level0;
                break;
            case 1:
                com.haneet.sleepwell.sleepwell.AlarmStaticVariables.level = com.haneet.sleepwell.sleepwell.AlarmStaticVariables.level1;
                break;
            case 2:
                com.haneet.sleepwell.sleepwell.AlarmStaticVariables.level = com.haneet.sleepwell.sleepwell.AlarmStaticVariables.level2;
                break;
            case 3:
                com.haneet.sleepwell.sleepwell.AlarmStaticVariables.level = com.haneet.sleepwell.sleepwell.AlarmStaticVariables.level3;
                break;
            default:
                com.haneet.sleepwell.sleepwell.AlarmStaticVariables.level = com.haneet.sleepwell.sleepwell.AlarmStaticVariables.level1;
                break;
        }
    }*/

/*    private void c() {
        setContentView(mainView);
        if (recorderThread != null) {
            Log.d("Stop Time", Calendar.getInstance().getTime()+"");

            recorderThread.stopRecording();
            recorderThread = null;
        }
        if (detectorThread != null) {
            Log.d("Stop Time", Calendar.getInstance().getTime()+"");
            detectorThread.stopDetection();
            detectorThread = null;
        }
        selectedDetection = DETECT_NONE;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "Quit demo");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                am.cancel(pendingIntent);
                finish();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }



    protected void onDestroy() {
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
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

    @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and
        // accelerometer sensors
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        sensorManager.unregisterListener(this);
    }*/

}

