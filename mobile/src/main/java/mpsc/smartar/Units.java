package mpsc.smartar;

/**
 * Created by hafiz on 12/24/15.
 */
public class Units {

    //different devices sensor index

    public  static final int SENSOR_TYPE_BAND = 1;
    public  static final int SENSOR_TYPE_SMART = 2;
//    public static  final int TYPE_BAND_FILTERED_ACC = 3;
//    public static  final int TYPE_SMART_FILTERED_ACC = 4;
    public static boolean isBandStartCollectData = false;
    public static boolean isSmartStartCollectData = false;
    //different states
    public static int SITTING = 1;
    public static int STANDING = 2;
    public static int WALKING = 3;

    public static final int ACTIVITY_TYPE_FSM = 1;
    public static final int ACTIVITY_TYPE_AR = 2;

    //data collection batch times
    public static final long BATCH_WAIT_TIME = 10000; //10 seconds

    //output index for band and smartphone
    public static final int BAND_INDEX = 0;
    public static final int SMART_INDEX = 1;

    //empirical values for selected sensors for different activities/subactivities
    public static final boolean  SENSORS_WALKING[] = {true, true}; //first position for band and second positions for smartphone
    public static final boolean SENSORS_SITTING[]={true, false};
    public static final boolean SENSORS_STANDING[]={true, false};

    //Thresholds for detemining finitie state machine's state changes
    public static final double LL_TH_WALKING = 0.40;
    public static final double LL_TH_SITTING = 0.45;
    public static final double LL_TH_STANDING = 0.54;

    //List of actions performed by different services
    public static final String  mProcessRawDataAction = "SMARTAR.RAWDATA_FILE_PROCESS"; //when raw data arrives from datacollector
    public static final String  mFeatureFile = "SMARTAR.FEATURE_FILE"; //when raw data arrives from datacollector
//    public static final String  mARTest = "DP.Genfile";
//    public static final String  mARAction = "mpsc.smartar.ActivityRecognitionService";
    public static final String  mDataPAction = "mpsc.smartar.DataProcessingService";
//    public static final String mARTrain ="mpsc.smartar.Train"; //this line only used to open training file and train the classifier

    //keys for different intent messages
    public static final String tmpRawDataFileName = "SMARTAR.TMP_RAW_FILE";
    public static final String tmpFeatureFileName = "SMARTAR.TMP_FEATURE_FILE";

    public static final String actionSensorSelect ="smart.ar.select.sensor";

    public static final String actionSensorSelection = "smart.ar.sensorselection";
    public static final String actionClassify ="smart.ar.classify";
    public static final String mPhoneSensorServiceAction = "SMART.SERVICE_ACTION";
    public static final String mTrainDataStore = "SMARTAR.TRAIN_DATA_STORE";
    public static final String mTestDataStore = "SMARTAR.TEST_DATA_STORE";
    public static final String mBeginDataStore = "SMARTAR.BEGIN_DATA_STORE";
    public static final String mStartAccelerometer = "SMARTAR.START_ACCELEROMETER";
    public static final String mStopAccelerometer = "SMARTAR.STOP_ACCELEROMETER";

    //    public static final String mTrainFilteredDataStore = "SMARTAR.TRAIN_FILTERDATA_STORE";
    public static final String mTestingDataStore = "SMARTAR.TEST_RAWDATA_STORE";
//    public static final String mTestingFilteredDataStore = "SMARTAR.TEST_FILTERDATA_STORE";
    public static final String mBeginDataCollectionService = "SMART.BEGIN_DATA_SERVICE";

/*************************************************************************************************************************************/


    public static final String realTimeTestFileName = "arreal.test";
    public static boolean isTraining = true;
    public static  final String  rootDir = "/DataCollector/";
    public static  final String  rawFolder = "/SmartAR/raw/";
    public static final String featureDir = "/features/";
    public static final String fileExtension = ".csv";
    public static final String trainDir = "/SmartAR/train/";
    public static final String resultDir = "log/";

    public static final int frameLength = 150; //50 Hz smapling rate, framelength 2.5 seconds 2.5*50 = 125 or 3*50 = 150
    public static final int frameStep = 75;

    public static final String prop_filename = "ar.prop";
    public static final String train_file = "ar.train";
    public static final String test_file = "ar.test";
    public static final String train_fsm_file = "fsm_ar.train";
}
