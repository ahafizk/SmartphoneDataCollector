package mpsc.smartar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import mpsc.smartar.data.Sensor;
import mpsc.smartar.events.BusProvider;
import mpsc.smartar.events.NewSensorEvent;
import com.google.android.gms.common.api.ResultCallback;

import com.squareup.otto.Subscribe;

import java.util.List;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
//    private RemoteSensorManager remoteSensorManager;

    Toolbar mToolbar;

    private ViewPager pager;
    private View emptyState;
    private NavigationView mNavigationView;
    private Menu mNavigationViewMenu;
//    private List<Node> mNodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        emptyState = findViewById(R.id.empty_state);

        mNavigationView = (NavigationView) findViewById(R.id.navView);
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationViewMenu = mNavigationView.getMenu();

        initToolbar();
        initViewPager();



        final EditText tagname = (EditText) findViewById(R.id.tagname);

        findViewById(R.id.btnStopSensor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tagnameText = "EMPTY";
                if (!tagname.getText().toString().isEmpty()) {
                    tagnameText = tagname.getText().toString();
                }


            }
        });

        tagname.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                    in.hideSoftInputFromWindow(tagname
                                    .getApplicationWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);


                    return true;

                }
                return false;
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
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
                            startActivity(new Intent(MainActivity.this, AboutActivity.class));
                            return true;

                    }

                    return true;
                }
            });
        }
    }

    private void initViewPager() {
        pager = (ViewPager) findViewById(R.id.pager);

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int id) {
                ScreenSlidePagerAdapter adapter = (ScreenSlidePagerAdapter) pager.getAdapter();
                if (adapter != null) {
                    Sensor sensor = adapter.getItemObject(id);
                    if (sensor != null) {

                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
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

//        pager.setAdapter(new ScreenSlidePagerAdapter(getSupportFragmentManager(), sensors));



//        remoteSensorManager.startMeasurement();

        mNavigationViewMenu.clear();

    }

    @Override
    protected void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);


    }

    @Override
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
        ((ScreenSlidePagerAdapter) pager.getAdapter()).addNewSensor(event.getSensor());
        pager.getAdapter().notifyDataSetChanged();
        emptyState.setVisibility(View.GONE);
        notifyUSerForNewSensor(event.getSensor());
    }
}
