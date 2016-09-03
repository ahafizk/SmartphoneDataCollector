package mpsc.smartar;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by hafiz on 3/10/16.
 */
public class DataStoreService  extends IntentService{

    private IntentFilter mTrainDataStoreFilter;

    private IntentFilter mTestDataStoreFilter;
    private Intent dpIntent;
    private Utils utilObj;
    private static Context arserviceContext = null;
    SensorDataManager sensorDataManager;

    private String LOG_STR = null;
    public DataStoreService(){super("Data Store Service");}
    public DataStoreService(String name){super(name);}


    @Override
    public void onCreate() {
        super.onCreate(); // if you override onCreate(), make sure to call super().
        // If a Context object is needed, call getApplicationContext() here.

        arserviceContext = getApplicationContext();
//        dpIntent = new Intent(this, DataProcessingService.class);
        utilObj = new Utils();

        initializeFilter();

        sensorDataManager  = SensorDataManager.getInstance(arserviceContext);


        LOG_STR = this.getClass().getSimpleName();
        Log.i(LOG_STR, "Data Store Service Created [onCreate()]");
    }

    private void initializeFilter()
    {
        mTrainDataStoreFilter = new IntentFilter();
        mTrainDataStoreFilter.addAction(Units.mTrainDataStore);

        mTestDataStoreFilter = new IntentFilter();
        mTestDataStoreFilter.addAction(Units.mTestDataStore);

        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, mTrainDataStoreFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, mTestDataStoreFilter);
    }

    //    @Override
    protected void onDestroy(Intent intent){
        super.onDestroy();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);

        mTrainDataStoreFilter = null;

        arserviceContext = null;

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //do some processing over here
        switch (intent.getAction()){
            case Units.mBeginDataStore:
                break;
            case Units.mTrainDataStore: //save raw accelerometer data to a file


                processTrainDataFile(intent);

                break;

        }
    }

    public void processTrainDataFile(Intent intent)
    {
//        String rawTrainData = intent.getExtras().getString("RawTrainData");
//        String rootPath= Environment.getExternalStorageDirectory().getAbsolutePath()+Units.rootDir;
//        String filename = Units.mSmartRawTrainingDataFileName;
//        Utils.writeToFile(rootPath,filename,)
//        if(rawTrainData.length()>1)
//        {
//            //save data to file
////            Log.i(LOG_STR, "Data Store Service in ProcessDataFile()");
////            String text = intent.getExtras().getString("RawTrainData");
//            writeToFile(rawTrainData, Units.mSmartRawTrainingDataFileName, true);
//            Log.i(LOG_STR, rawTrainData);
//        }
//        String filterTrainData = intent.getExtras().getString("FilterTrainData");
//        if (filterTrainData.length()>1)
//        {
//            // save data to filtered train file FilterTrainData
////            String text = intent.getExtras().getString("FilterTrainData");
//            writeToFile(filterTrainData, Units.mSmartFilterTrainingDataFileName, true);
//            Log.i(LOG_STR, filterTrainData);
//        }

    }
    public void processTestDataFile(Intent intent)
    {
//        String rawTestData = intent.getExtras().getString("RawTestData");
//        if (rawTestData.length()>1)
//        {
//            //save data to testing file
//
//            writeToFile(rawTestData, Units.mSmartRawTestingDataFileName, true);
//        }
//        String filterTestData = intent.getExtras().getString("FilterTestData");
//        if(filterTestData.length()>1)
//        {
//            //save filtered data
//            String text = intent.getExtras().getString("FilterTestData");
//            writeToFile(filterTestData, Units.mSmartFilterTestingDataFileName, false);
//        }
    }


    public synchronized void writeToFile(String text, String filename, boolean append)
    {
        String rootPath= Environment.getExternalStorageDirectory().getAbsolutePath()+"/SmartActivity/";

        File file=new File(rootPath);
        if(!file.exists()){
            file.mkdirs();

        }

        FileOutputStream fOut = null;
        try {

            File savedfile = new File(rootPath+filename);
            savedfile.createNewFile();//create file if not exists
            fOut = new FileOutputStream(savedfile, true);
            OutputStreamWriter outWriter = new OutputStreamWriter(fOut);
//            if (append==true)
//             outWriter.append(text);
//            else outWriter.write(text);
            outWriter.write(text);
            fOut.flush();
            outWriter.flush();
            fOut.close();
            outWriter.close();
        } catch (IOException e) {

            e.printStackTrace();
        }

    }


    //received broadcast message and performed proper actions
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            switch (intent.getAction()){
                case Units.mTrainDataStore:

                    processTrainDataFile(intent);
                    break;
                case Units.mTestDataStore:

                    processTestDataFile(intent);
                    break;

            }
        }
    };
}
