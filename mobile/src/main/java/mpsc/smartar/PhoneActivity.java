package mpsc.smartar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.ResultCallback;
import com.squareup.otto.Subscribe;

import org.w3c.dom.Node;

import java.util.List;

import mpsc.smartar.data.Sensor;
import mpsc.smartar.events.BusProvider;
import mpsc.smartar.events.NewSensorEvent;

public class PhoneActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

//    private RemoteSensorManager remoteSensorManager;

    Toolbar mToolbar;

    private ViewPager pager;
    private View emptyState;
    private NavigationView mNavigationView;
    private Menu mNavigationViewMenu;
    private List<Node> mNodes;
    //intent filter for receiving message from other service or activity
    private IntentFilter mPhoneSensorServiceActionFilter;
    private IntentFilter mStopAcceleromterFilter;
    private boolean isCollectData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(mToolbar);


        mToolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        emptyState = findViewById(R.id.empty_state);

        mNavigationView = (NavigationView) findViewById(R.id.navView);
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationViewMenu = mNavigationView.getMenu();

        initToolbar();

        initializeFilter();

        isCollectData = false; //initially not interested to collect data.




        findViewById(R.id.btnStartSensor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText txt = (EditText)findViewById(R.id.txtFrequency);
                int samplingFrequency = Integer.parseInt(txt.getText().toString());
                if (samplingFrequency>0) {


                    isCollectData = true;
                    Button p1_button = (Button) findViewById(R.id.btnStartSensor);
//                    p1_button.setText("STOP Sensor");

                    p1_button.setEnabled(false);
                    onStartCollectData(samplingFrequency);
                    Button stop_button = (Button) findViewById(R.id.btnStopSensor);
                    stop_button.setEnabled(true);
                }

            }
        });

        findViewById(R.id.btnStopSensor).setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View v){
                onStopCollectData();
                isCollectData = false;
                Button p1_button = (Button)findViewById(R.id.btnStartSensor);
                p1_button.setEnabled(true);

                Button stop_button = (Button)findViewById(R.id.btnStopSensor);
                stop_button.setEnabled(false);

            }
        });



        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        initServices();

    }
    private void initServices()
    {
//        start begin data collection service
        Intent bcIntent = new Intent(this, PhoneSensorService.class);
        bcIntent.setAction(Units.mBeginDataCollectionService);
        bcIntent.putExtra("sampling_frequency", 50);

        startService(bcIntent);


    }
    private void initToolbar() {
        setSupportActionBar(mToolbar);

        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(false);
            ab.setTitle(R.string.app_name);
            mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.action_about:
                            startActivity(new Intent(PhoneActivity.this, AboutActivity.class));
                            return true;
//                        case R.id.action_export:
//                            startActivity(new Intent(PhoneActivity.this, TestClsActivity.class));
//                            return true;
                    }

                    return true;
                }
            });
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);


        mNavigationViewMenu.clear();
//        remoteSensorManager.getNodes(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
//            @Override
//            public void onResult(final NodeApi.GetConnectedNodesResult pGetConnectedNodesResult) {
//                mNodes = pGetConnectedNodesResult.getNodes();
//                for (Node node : mNodes) {
//                    SubMenu menu = mNavigationViewMenu.addSubMenu(node.getDisplayName());
//
//                    MenuItem item = menu.add("15 sensors");
//                    if (node.getDisplayName().startsWith("G")) {
//                        item.setChecked(true);
//                        item.setCheckable(true);
//                    } else {
//                        item.setChecked(false);
//                        item.setCheckable(false);
//                    }
//                }
//            }
//        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);

//        remoteSensorManager.stopMeasurement();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mDataReceiver);
    }
    public boolean showNotification(final  String msg)
    {
        Toast.makeText(this,msg, Toast.LENGTH_SHORT).show();
        return false;

    }


     /*------------------------Call Back Implementation -----------------------------------*/

    //define callback when broadcast message is received
    private BroadcastReceiver mDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            switch (intent.getAction()){
//                case ARService.mARBegin:
////                    onHandleBeginService(intent);
//                    //do something
//                    break;

            }
        }
    };


    /*------------------------Button handlers --------------------------------------------*/

    public void onStartCollectData(int samplingFrequency)
    {


        Intent dcIntent= new Intent(this, PhoneSensorService.class);
        dcIntent.setAction(Units.mStartAccelerometer); //set actions
        dcIntent.putExtra("sampling_frequency", samplingFrequency);

//        dcIntent.putExtra("filename",String.valueOf(ti));
        LocalBroadcastManager.getInstance(this).sendBroadcast(dcIntent);
        Units.isSmartStartCollectData = true; //based on this value sensor values are added to the file




        Toast.makeText(this, "Start Collecting Data", Toast.LENGTH_LONG).show();
    }

    public void onStopCollectData()
    {

        Intent dcIntent= new Intent(this, PhoneSensorService.class);
        dcIntent.setAction(Units.mStopAccelerometer); //set actions
        LocalBroadcastManager.getInstance(this).sendBroadcast(dcIntent);
        Units.isSmartStartCollectData = false;

        Toast.makeText(this, "Stop Requested!", Toast.LENGTH_LONG).show();
    }


//    @Override
    public boolean onNavigationItemSelected(final MenuItem pMenuItem) {

        Toast.makeText(this, "Device: " + pMenuItem.getTitle(), Toast.LENGTH_SHORT).show();
        return false;
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        private List<Sensor> sensors;

        public ScreenSlidePagerAdapter(FragmentManager fm, List<Sensor> symbols) {
            super(fm);
            this.sensors = symbols;
        }


        public void addNewSensor(Sensor sensor) {
            this.sensors.add(sensor);
        }


        private Sensor getItemObject(int position) {
            return sensors.get(position);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return SensorFragment.newInstance(sensors.get(position).getId());
        }

        @Override
        public int getCount() {
            return sensors.size();
        }

    }

    private void notifyUSerForNewSensor(Sensor sensor) {
        Toast.makeText(this, "New Sensor!\n" + sensor.getName(), Toast.LENGTH_SHORT).show();
    }


    @Subscribe
    public void onNewSensorEvent(final NewSensorEvent event) {
//        ((ScreenSlidePagerAdapter) pager.getAdapter()).addNewSensor(event.getSensor());
//        pager.getAdapter().notifyDataSetChanged();
        emptyState.setVisibility(View.GONE);

        notifyUSerForNewSensor(event.getSensor());

    }
    private void initializeFilter()
    {
        mPhoneSensorServiceActionFilter =  new IntentFilter();
        mPhoneSensorServiceActionFilter.addAction(Units.mPhoneSensorServiceAction);
        mPhoneSensorServiceActionFilter.addAction(Units.actionSensorSelection);
        mPhoneSensorServiceActionFilter.addAction(Units.mDataPAction);
        mStopAcceleromterFilter = new IntentFilter();
        mStopAcceleromterFilter.addAction(Units.mStopAccelerometer);
        //creating filter for broadcast receiver and register it
        IntentFilter filter = new IntentFilter(Units.mBeginDataCollectionService);


        //registering filters for different actions
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, mPhoneSensorServiceActionFilter);
//        LocalBroadcastManager.getActivityClassifierInstance(this).registerReceiver(mReceiver, mStopAcceleromterFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, filter);

    }
    public void showMessage(String msg)
    {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();

    }

    private synchronized void handleSensorSelectionCommand(boolean bandON, boolean smartON)
    {

        if (bandON)
        {
            if (!Units.isBandStartCollectData){
                //turn on the band sensor
//                remoteSensorManager.startMeasurement();
                Units.isBandStartCollectData = true;
                Log.i("PhoneActivity", "BAND sensor  command : TURN ON");
            } else {
                Log.i("PhoneActivity", "Already on >> BAND Sensor command: TURN ON");
            }
        }
        else
        {
            //band sensor should be turn off
            if (Units.isBandStartCollectData){
                //turn on the band sensor
//                remoteSensorManager.stopMeasurement();
                Units.isBandStartCollectData = false;
                Log.i("PhoneActivity", "BAND sensor  command : TURN OFF");
            }else {
                Log.i("PhoneActivity", "Already off >> BAND Sensor command: TURN OFF");
            }
        }

        if (smartON)
        {
            if (!Units.isSmartStartCollectData){
                //turn on the band sensor
                Intent dcIntent= new Intent(this, PhoneSensorService.class);
                dcIntent.setAction(Units.mStartAccelerometer); //set actions
                LocalBroadcastManager.getInstance(this).sendBroadcast(dcIntent);
                Units.isSmartStartCollectData = true;
                Log.i("PhoneActivity", "SMART Sensor  command : TURN ON");
            } else {
                Log.i("PhoneActivity", "Already on >> SMART Sensor command: TURN ON");
            }
        }
        else
        {
            //smart sensor should be turn off
            if (Units.isSmartStartCollectData){
                //turn on the band sensor
                Intent dcIntent= new Intent(this, PhoneSensorService.class);
                dcIntent.setAction(Units.mStopAccelerometer); //set actions
                LocalBroadcastManager.getInstance(this).sendBroadcast(dcIntent);
                Units.isSmartStartCollectData = false;
                Log.i("PhoneActivity", "SMART sensor  command : TURN OFF");
            } else {
                Log.i("PhoneActivity", "Already off >> SMART Sensor command: TURN OFF");
            }
        }

    }
    //received broadcast message and performed proper actions
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            switch (intent.getAction()){
                case Units.mPhoneSensorServiceAction:
                    String st = intent.getStringExtra("status");
                    if (st.equals("START"))
                    {
                        showMessage("Smartphone ACC Started!");
                    }
                    else if (st.equals("STOP"))
                    {
                        showMessage("Smartphone ACC Stopped!");
                    }


                    break;
                case Units.actionSensorSelection:
                    String band = intent.getStringExtra("BAND");
                    String smart = intent.getStringExtra("SMART");
                    handleSensorSelectionCommand(Boolean.parseBoolean(band),Boolean.parseBoolean(smart));

                    break;
                case Units.mDataPAction:
                    showMessage(intent.getStringExtra("msg"));
                    break;

            }
        }
    };

}
