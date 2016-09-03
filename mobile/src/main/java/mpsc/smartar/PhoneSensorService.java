package mpsc.smartar;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * Created by hafiz on 3/10/16.
 */
public class PhoneSensorService extends IntentService implements SensorEventListener {

    //http://stackoverflow.com/questions/15647841/accelerometer-logger-experiencing-occasional-long-delays-between-frames?noredirect=1#comment36392316_15647841
    //https://www.built.io/blog/2013/05/applying-low-pass-filter-to-android-sensors-readings/
    private static Context arContext = null;
    private String LOG_STR = null;
    private Intent dpIntent;
    private SensorManager mSensorManager;
    private  Sensor mAccelerometer;
    private double acceleorometerRate;// = 60; // Hz
//    private String savedFilename;
    int accelerometerDelayMicroseconds;
    static final float ALPHA = 0.25f; // if ALPHA = 1 OR 0, no filter applies.
    protected float[] gravSensorVals;
    protected StringBuilder filteredsensorData;
    protected StringBuilder rawSensorData;
    private long lastUpdateTime=0;
    private SensorDataManager dataManager;

    private boolean isSensorRunning = false;
    public PhoneSensorService(){super("Data Collection Service");}
    public PhoneSensorService(String name){super(name);}


    //intent filter for receiving message from other service or activity
    private IntentFilter mStartAcceleromterFilter;
    private IntentFilter mStopAcceleromterFilter;


    @Override
    public void onCreate() {
        super.onCreate(); // if you override onCreate(), make sure to call super().
        // If a Context object is needed, call getApplicationContext() here.

        arContext = getApplicationContext();
        dpIntent = new Intent(this, PhoneSensorService.class);


        //create string builder object to hold sensor data to write down in memory
        rawSensorData = new StringBuilder();
        filteredsensorData  = new StringBuilder();
        isSensorRunning = false;

        acceleorometerRate = 50; //Hz
        initializeFilter();
        initializeSensorParam();
//        beginServices();
        dataManager = SensorDataManager.getInstance(this);
        //writing log
        LOG_STR = this.getClass().getSimpleName();
        Log.i(LOG_STR, "Data Collection Service onCreate() Method");
    }

    private void initializeSensorParam()
    {


        accelerometerDelayMicroseconds = (int) (Math.round(((1/this.acceleorometerRate)*1000000.0)));
        //get sensor manager service

//        isSensorRunning = mSensorManager.registerListener(this, mAccelerometer, accelerometerDelayMicroseconds);
    }

    private void beginServices()
    {
//        Intent dsIntent = new Intent(this, DataStoreService.class);
//        dsIntent.setAction(Units.mBeginDataStore);
//        startService(dsIntent);
    }
    private void initializeFilter()
    {
        mStartAcceleromterFilter =  new IntentFilter();
        mStartAcceleromterFilter.addAction(Units.mStartAccelerometer);
        mStopAcceleromterFilter = new IntentFilter();
        mStopAcceleromterFilter.addAction(Units.mStopAccelerometer);
        //creating filter for broadcast receiver and register it
        IntentFilter filter = new IntentFilter(Units.mBeginDataCollectionService);


        //registering filters for different actions
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, mStartAcceleromterFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, mStopAcceleromterFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, filter);

    }
    public boolean startAccelerometerSensor()
    {
        initializeSensorParam();
        Long tsLong = System.currentTimeMillis()/1000;
        System.out.println(tsLong);
        dataManager.savedFilename = tsLong.toString();

        dataManager.savedFilename = dataManager.savedFilename+"_"+String.valueOf((int)acceleorometerRate)+".csv";

        if (!isSensorRunning) {
            mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
            //get accelerometer sensor handle
            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            isSensorRunning = mSensorManager.registerListener(PhoneSensorService.this, mAccelerometer, accelerometerDelayMicroseconds);
            isSensorRunning = true;

            Intent dcIntent = new Intent();
            dcIntent.setAction(Units.mPhoneSensorServiceAction); //set actions
            dcIntent.putExtra("status", "START");
            LocalBroadcastManager.getInstance(this).sendBroadcast(dcIntent);
        }
        return  isSensorRunning;

    }
    public  void stopAccelerometerSensor()
    {
        if (isSensorRunning) {
            isSensorRunning = false;
            mSensorManager.unregisterListener(this, mAccelerometer);
            dataManager.saveSensorData(); // save collected data to memory
            Intent dcIntent= new Intent();
            dcIntent.putExtra("status","STOP");
            dcIntent.setAction(Units.mPhoneSensorServiceAction); //set actions
            LocalBroadcastManager.getInstance(this).sendBroadcast(dcIntent);
        }
    }

    protected void onDestroy(Intent intent)
    {
        super.onDestroy();
        isSensorRunning = false;
        if (mSensorManager!=null)
            mSensorManager.unregisterListener(this, mAccelerometer);
    }

//    protected float[] lowPass( float[] input, float[] output ) {
//        if ( output == null ) return input;
//
//        for ( int i=0; i<input.length; i++ ) {
//            output[i] = output[i] + ALPHA * (input[i] - output[i]);
//        }
//        return output;
//    }

    @Override
    protected void onHandleIntent(Intent intent) {
        switch (intent.getAction()){
            case Units.mBeginDataCollectionService:
                // Write log
                break;
            case Units.mStartAccelerometer:
                acceleorometerRate = intent.getIntExtra("sampling_frequency",50);
                startAccelerometerSensor();
                Log.i(LOG_STR, "Sampling Rate set to :: " + acceleorometerRate);
                Log.i(LOG_STR, "Start sampling at :: "+accelerometerDelayMicroseconds);
                Log.i(LOG_STR,"Smartphone Accelerometer Sensor Started Successfully! sampling at :: "+acceleorometerRate);
                break;
            case Units.mStopAccelerometer:
                stopAccelerometerSensor();
                Log.i(LOG_STR, "Smartphone Accelerometer Sensor Stopped Successfully!");
                break;
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            switch (intent.getAction()){
                case Units.mBeginDataCollectionService:
                    String msg = intent.getStringExtra("BEGINDATACOLLECTION_SERVICE");
                    break;
                case Units.mStartAccelerometer:

                    acceleorometerRate = intent.getIntExtra("sampling_frequency",50);
                    startAccelerometerSensor();
                    Log.i(LOG_STR, "Sampling Rate set to :: " + acceleorometerRate);
                    Log.i(LOG_STR, "Start sampling at :: "+accelerometerDelayMicroseconds);


//                    startAccelerometerSensor();
                    Log.i(LOG_STR,"Smartphone Accelerometer Sensor Started Successfully!");
                    break;
                case Units.mStopAccelerometer:
                    stopAccelerometerSensor();
                    Log.i(LOG_STR, "Smartphone Accelerometer Sensor Stopped Successfully!");
                    break;
            }
        }
    };

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

//            String timestamp = String.valueOf(event.timestamp);
//            if (Units.isSmartStartCollectData) {
//            rawSensorData.append(timestamp).append(",").append(Float.toString(event.values[0])).append(",").append(Float.toString(event.values[1])).append(",").append(Float.toString(event.values[2])).append("\n");
                dataManager.addSensorData(Units.SENSOR_TYPE_SMART, event.accuracy, event.timestamp, event.values);
//            }


        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


}
