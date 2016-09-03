package mpsc.smartar;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.SparseArray;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import mpsc.smartar.data.Sensor;
import mpsc.smartar.data.SensorDataPoint;
import mpsc.smartar.shared.ClientPaths;

/**
 * Created by hafiz on 3/18/16.
 */
public class SensorDataManager {
    private static final String TAG = "SmartAR/SensorDataManager";
    private static SensorDataManager instance;
    private Context context;
    private SparseArray<Sensor> sensorMapping;
    private ArrayList<Sensor> sensors;
    private PSensorNames sensorNames;
    private String LOG_STR = null;
    private long lastUpdateTime = 0;
    public String savedFilename;
    private ExecutorService executorService;

    public SensorDataManager(Context context)
    {
        this.context = context;
        this.sensorMapping = new SparseArray<Sensor>();
        this.sensors = new ArrayList<Sensor>();
        this.sensorNames = new PSensorNames();
        LOG_STR = this.getClass().getSimpleName();
        this.executorService = Executors.newCachedThreadPool();
    }

    public static synchronized SensorDataManager getInstance(Context context) {
        if (instance == null) {
            instance = new SensorDataManager(context.getApplicationContext());
        }

        return instance;
    }
    public Sensor getSensor(long id) {
        return sensorMapping.get((int) id);
    }

    private Sensor createSensor(int id)
    {
        Sensor sensor = new Sensor(id, sensorNames.getName(id));

        sensors.add(sensor);
        sensorMapping.append(id, sensor);

//        BusProvider.postOnMainThread(new NewSensorEvent(sensor));

        return sensor;
    }

    private Sensor getOrCreateSensor(int id) {
        Sensor sensor = sensorMapping.get(id);

        if (sensor == null) {
            sensor = createSensor(id);
        }

        return sensor;
    }

    public synchronized void addSensorData(final int sensorType, int accuracy, long timestamp, float[] values) {
        Sensor sensor = getOrCreateSensor(sensorType);

        SensorDataPoint dataPoint = new SensorDataPoint(timestamp, accuracy, values);

        sensor.addDataPoint(dataPoint);
        long curTime = System.currentTimeMillis();
        if ((curTime - lastUpdateTime) >= Units.BATCH_WAIT_TIME) {
            lastUpdateTime = curTime;
            startSaving();

        }

//        Log.i(LOG_STR, "Adding Smartphone Sensor data points to sensor object");

    }
    public synchronized void startSaving()
    {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                saveSensorData();
            }
        });
    }

    public synchronized void saveSensorData()
    {
        //"bad design" but only responsible for saving smartphone sensor data
        Sensor sensor = getSensor(Units.SENSOR_TYPE_SMART);

        Utils.exportFile( sensor,savedFilename);
//        if(!Units.isTraining)
//        {
//            Intent intent = new Intent();
//            intent.setAction(Units.mTestingDataStore);
//            LocalBroadcastManager.getInstance(this.context).sendBroadcast(intent);
//        }
    }






    private class  PSensorNames
    {
        public SparseArray<String> names;

        public PSensorNames()
        {
            names = new SparseArray<String>();
            names.append(0, "Debug Sensor");
            names.append(Units.SENSOR_TYPE_BAND, "Band Accelerometer");
            names.append(Units.SENSOR_TYPE_SMART, "SmartPhone Accelerometer");
        }

        public String getName(int sensorId)
        {
            String name = names.get(sensorId);

            if (name == null) {
                name = "Unknown";
            }

            return name;
        }

    }
}
