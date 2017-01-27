package junhyeon.com.alarm;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmAsyncTask;
import junhyeon.com.alarm.model.Alarm;

public class DetailActivity extends AppCompatActivity implements View.OnTouchListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMapLongClickListener {
    private static final int PERMISSION_USING_MAP = 10;
    private static final int ACTIVE_GPS_REQUEST = 9;
    private static final int PICK_ALARM_RINGTONE_REQUEST = 1;
    private static final int PICK_ALARM_REPEAT_REQUEST = 2;
    private static final int DEFAULT_ALARM_ID = -1;
    static final LatLng LAT_LNG_SEOUL = new LatLng(37.565650, 126.978017);
    private boolean mIsGrantedMapPermission;
    private boolean mIsActiveGsp;
    private Toolbar mToolbar;
    private TimePicker mTimePicker;
    private DiscreteSeekBar mSeekBar;
    private TextView mRingtoneTitleTextView;
    private TextView mRingtoneUriTextView;
    private SwitchCompat mVibrateSwitch;
    private TextView mRepeatIntervalNumberTextView;
    private EditText mMemoEditText;
    private ImageView mTransparentImageView;
    private ArrayList<ToggleButton> mDayToggleButtonList = new ArrayList<>();
    private int mRepeatInterval;
    private int mRepeatNumber;
    private SupportMapFragment mSupportMapFragment;
    private GoogleMap mGoogleMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationManager mLocationManager;
    private Location mLocation;
    private Marker mMarker;
    private Toast mToast;
    private Realm mRealm;
    private RealmAsyncTask mRealmAsyncTask;
    private MenuItem mAddMenuItem;
    private int mId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        bindViews();

        setEventListeners();
        if (isModifyAlarm()) {
            selectOneAlarm();
        } else {
            setRingtoneValue();
            checkGps();
        }
        buildingGoogleApiClient();
        initToolBar();
    }

    private void bindViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTimePicker = (TimePicker) findViewById(R.id.time_picker);
        mDayToggleButtonList.add((ToggleButton) findViewById(R.id.bt_sun));
        mDayToggleButtonList.add((ToggleButton) findViewById(R.id.bt_mon));
        mDayToggleButtonList.add((ToggleButton) findViewById(R.id.bt_tue));
        mDayToggleButtonList.add((ToggleButton) findViewById(R.id.bt_wed));
        mDayToggleButtonList.add((ToggleButton) findViewById(R.id.bt_thu));
        mDayToggleButtonList.add((ToggleButton) findViewById(R.id.bt_fri));
        mDayToggleButtonList.add((ToggleButton) findViewById(R.id.bt_sat));
        mRingtoneTitleTextView = (TextView) findViewById(R.id.tv_ringtone_title);
        mRingtoneUriTextView = (TextView) findViewById(R.id.tv_ringtone_uri);
        mSeekBar = (DiscreteSeekBar) findViewById(R.id.seek_bar);
        mVibrateSwitch = (SwitchCompat) findViewById(R.id.sw_vibrate);
        mRepeatIntervalNumberTextView = (TextView) findViewById(R.id.tv_repeat_interval_number);
        mMemoEditText = (EditText) findViewById(R.id.et_memo);
        mTransparentImageView = (ImageView) findViewById(R.id.iv_transparent);
        mSupportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mToast = Toast.makeText(this, null, Toast.LENGTH_SHORT);
    }

    private void setEventListeners() {
        mMemoEditText.setOnTouchListener(this);
        mTransparentImageView.setOnTouchListener(this);
    }

    private void initToolBar() {
        if (mId == DEFAULT_ALARM_ID) {
            mToolbar.setTitle(getString(R.string.alarm_add_label));
        } else {
            mToolbar.setTitle(getString(R.string.alarm_modify_label));
        }

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private boolean isModifyAlarm() {
        Intent intent = getIntent();
        if (intent != null) {
            mId = intent.getIntExtra("id", DEFAULT_ALARM_ID);
        } else
            mId = DEFAULT_ALARM_ID;

        return (mId != DEFAULT_ALARM_ID);
    }

    private void setRingtoneValue() {
        //Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Uri defaultRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALARM);
        Ringtone defaultRingtone = RingtoneManager.getRingtone(getApplicationContext(), defaultRingtoneUri);
        mRingtoneTitleTextView.setText(defaultRingtone.getTitle(this));
        mRingtoneUriTextView.setText(defaultRingtoneUri.toString());
        Log.d("DetailActivity", mRingtoneUriTextView.getText().toString());
    }

    private void checkGps() {
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.gps_active_alert_message));
            builder.setPositiveButton(getString(R.string.active_label), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent gpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(gpsIntent, ACTIVE_GPS_REQUEST);
                }
            });
            builder.setNegativeButton(getString(R.string.cancel_label), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mIsActiveGsp = false;
                    mToast.cancel();
                    mToast.setText(getString(R.string.passive_gps_message));
                    mToast.show();
                    mSupportMapFragment.getMapAsync(DetailActivity.this);
                }
            });
            builder.show();
        } else {
            mIsActiveGsp = true;
            mSupportMapFragment.getMapAsync(DetailActivity.this);
        }

    }

    protected synchronized void buildingGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(mSupportMapFragment.getActivity(), this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void updateLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkMapPermission();
        } else {
            mIsGrantedMapPermission = true;
        }

        if (!mIsGrantedMapPermission)
            return;

        if (mId == DEFAULT_ALARM_ID) {
            if (mIsActiveGsp) {
                while (true) {
                    mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    if (mLocation != null)
                        break;
                }

            }
        }
        mSupportMapFragment.getMapAsync(this);
    }

    private void updateMarker(boolean isNotNullLocation) {
        LatLng locationLatLng;
        if (isNotNullLocation) {
            locationLatLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
        } else {
            locationLatLng = LAT_LNG_SEOUL;
        }
        if (mMarker != null)
            mMarker.remove();
        mMarker = mGoogleMap.addMarker(new MarkerOptions().position(locationLatLng).title(getAddress(locationLatLng)));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(locationLatLng));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);
    }

    private String getAddress(LatLng latLng) {
        String address = null;
        Geocoder geocoder = new Geocoder(this, Locale.KOREA);
        List<Address> addressList;
        try {
            addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                address = addressList.get(0).getAddressLine(0);
            }

        } catch (IOException e) {
            Log.d("DetailActivity", "주소를 가져올 수 없습니다.");
            e.printStackTrace();
        }
        return address;
    }

    private void insertOrUpdateAlarm() {
        mAddMenuItem.setEnabled(false);
        mRealm = MyApplication.getRealmInstance();

        mRealmAsyncTask = mRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Number currentIdNum = realm.where(Alarm.class).max("id");
                int nextId;
                if (currentIdNum == null) {
                    nextId = 1;
                } else {
                    nextId = currentIdNum.intValue() + 1;
                }
                if (mId != DEFAULT_ALARM_ID)
                    nextId = mId;

                Alarm alarm = new Alarm();
                alarm.setId(nextId);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarm.setHour(mTimePicker.getHour());
                    alarm.setMinute(mTimePicker.getMinute());
                } else {
                    alarm.setHour(mTimePicker.getCurrentHour());
                    alarm.setMinute(mTimePicker.getCurrentMinute());
                }

                StringBuffer repeatDay = new StringBuffer();
                for (int i = 0; i < mDayToggleButtonList.size(); ++i) {
                    if (mDayToggleButtonList.get(i).isChecked()) {
                        repeatDay.append("1");
                    } else {
                        repeatDay.append("0");
                    }
                }
                alarm.setRepeatDay(repeatDay.toString());
                alarm.setRingtoneUri(mRingtoneUriTextView.getText().toString());
                alarm.setVolume(mSeekBar.getProgress());
                alarm.setRepeatInterval(mRepeatInterval);
                alarm.setRepeatNumber(mRepeatNumber);

                if (mMemoEditText.getText().length() > 0)
                    alarm.setMemo(mMemoEditText.getText().toString());
                else
                    alarm.setMemo(null);

                if (mLocation != null) {
                    alarm.setLat(mLocation.getLatitude());
                    alarm.setLng(mLocation.getLongitude());
                }

                alarm.setEnrolled(true);

                if (mVibrateSwitch.isChecked())
                    alarm.setVibrate(true);
                else
                    alarm.setVibrate(false);

                alarm.setTimeStamp(Long.toString(System.currentTimeMillis()));
                realm.insertOrUpdate(alarm);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                mRealm.close();
                mAddMenuItem.setEnabled(true);
//                Intent resultIntent = new Intent();
//                resultIntent.putExtra("position", mPosition);
//                setResult(RESULT_OK, resultIntent);
                if (!DetailActivity.this.isFinishing()) {
                    finish();
                }
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                //핸들러 처리.
                mRealm.close();
                mAddMenuItem.setEnabled(true);
            }
        });
    }

    private void selectOneAlarm() {
        Realm realm = MyApplication.getRealmInstance();

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // 1. ID 값으로 셀렉트문을 날려 알람 하나 정보를 가져온다.
                Alarm alarm = realm.where(Alarm.class).equalTo("id", mId).findFirst();
                // 2. 값을 셋팅한다.
                // 2. - 1) 알람 시간
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mTimePicker.setHour(alarm.getHour());
                    mTimePicker.setMinute(alarm.getMinute());
                } else {
                    mTimePicker.setCurrentHour(alarm.getHour());
                    mTimePicker.setCurrentMinute(alarm.getMinute());
                }
                // 2. - 2) 반복 요일
                String repeatDay = alarm.getRepeatDay();
                for (int i = 0; i < repeatDay.length(); ++i) {
                    if (repeatDay.charAt(i) == '1') {
                        mDayToggleButtonList.get(i).setChecked(true);
                    } else {
                        mDayToggleButtonList.get(i).setChecked(false);
                    }
                }
                // 2. - 3) 알람음 - > 눌렀을 때 액티비티 수정.
                String ringtoneUri = alarm.getRingtoneUri();
                Log.d("DetailActivity", "Ringtone URI: "+ringtoneUri);
                Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), Uri.parse(ringtoneUri));
                mRingtoneTitleTextView.setText(ringtone.getTitle(DetailActivity.this));
                mRingtoneUriTextView.setText(ringtoneUri);

                // 2. - 4) 볼륨
                mSeekBar.setProgress(alarm.getVolume());

                // 2. - 5) 진동 유무
                if (alarm.getVibrate()) {
                    mVibrateSwitch.setChecked(true);
                } else {
                    mVibrateSwitch.setChecked(false);
                }

                // 2. - 6) 다시 알림 - > 눌렀을 때 액티비티 수정.
                mRepeatInterval = alarm.getRepeatInterval();
                mRepeatNumber = alarm.getRepeatNumber();
                String repeatIntervalNumberText;
                if(mRepeatNumber == 0 && mRepeatInterval == 0){
                    repeatIntervalNumberText = getString(R.string.no_use_label);
                }else {
                    repeatIntervalNumberText = mRepeatInterval + getString(R.string.repeat_interval_label)
                            + ", " + mRepeatNumber + getString(R.string.repeat_number_label);
                }
                mRepeatIntervalNumberTextView.setText(repeatIntervalNumberText);

                // 2. - 7) 메모
                if (alarm.getMemo() != null) {
                    mMemoEditText.setText(alarm.getMemo());
                }

                // 2. - 8) 위치
                if (alarm.getLat() != null && alarm.getLng() != null) {
                    mLocation = new Location("");
                    mLocation.setLatitude(alarm.getLat());
                    mLocation.setLongitude(alarm.getLng());
                    mSupportMapFragment.getMapAsync(DetailActivity.this);
                }

            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {

            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {

            }
        });
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        if (mRealmAsyncTask != null && !mRealmAsyncTask.isCancelled()) {
            mRealmAsyncTask.cancel();
        }
        if (mRealm != null) {
            mRealm.close();
        }
        if (!mAddMenuItem.isEnabled()) {
            mAddMenuItem.setEnabled(true);
        }
        super.onStop();
    }

    public void onClickLayout(View view) {
        switch (view.getId()) {
            case R.id.layout_ringtone:
                Intent launchRingtoneActivityIntent = new Intent(this, RingtoneActivity.class);
                launchRingtoneActivityIntent.putExtra("title", mRingtoneTitleTextView.getText().toString());
                startActivityForResult(launchRingtoneActivityIntent, PICK_ALARM_RINGTONE_REQUEST);
                break;
            case R.id.layout_repeat:
                Intent launchRepeatActivityIntent = new Intent(this, RepeatActivity.class);
                startActivityForResult(launchRepeatActivityIntent, PICK_ALARM_REPEAT_REQUEST);
                break;
        }
    }

    public void onClickButton(View view) {
        if (view instanceof ImageButton) {
            switch (view.getId()) {
                case R.id.ib_location:
                    if (mIsActiveGsp) {
                        updateLocation();
                    } else {
                        mToast.cancel();
                        mToast.setText(getString(R.string.passive_gps_message));
                        mToast.show();
                    }
                    break;
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int id = v.getId();
        if (id == R.id.et_memo || id == R.id.iv_transparent) {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_UP:
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }
        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mGoogleMap = map;
        mGoogleMap.setOnMapLongClickListener(this);
        if (mLocation != null) {
            Log.d("DetailActivity", "onMapReady: mLocation is not null");
            updateMarker(true);
        } else {
            Log.d("DetailActivity", "onMapReady: mLocation is null");
            updateMarker(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        mAddMenuItem = menu.findItem(R.id.action_add);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_add:
                insertOrUpdateAlarm();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_ALARM_RINGTONE_REQUEST:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    mRingtoneTitleTextView.setText(bundle.getString("title"));
                    mRingtoneUriTextView.setText(bundle.getString("uri"));
                }
                break;
            case PICK_ALARM_REPEAT_REQUEST:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    mRepeatInterval = bundle.getInt("interval");
                    mRepeatNumber = bundle.getInt("number");
                    String text = String.valueOf(mRepeatInterval) + getString(R.string.minute_label) + ", "
                            + String.valueOf(mRepeatNumber) + getString(R.string.number_label);
                    mRepeatIntervalNumberTextView.setText(text);
                } else if (resultCode == RESULT_CANCELED) {
                    mRepeatIntervalNumberTextView.setText(getString(R.string.no_use_label));
                    mRepeatInterval = 0;
                    mRepeatNumber = 0;
                }
                break;
            case ACTIVE_GPS_REQUEST:
                if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    mIsActiveGsp = true;
                    mGoogleApiClient.connect();
                } else {
                    mToast.cancel();
                    mToast.setText(getString(R.string.passive_gps_message));
                    mToast.show();
                    mIsActiveGsp = false;
                    mSupportMapFragment.getMapAsync(this);
                }
                break;
        }
    }

    private void checkMapPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_USING_MAP);
        } else {
            mIsGrantedMapPermission = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_USING_MAP:
                mIsGrantedMapPermission = false;
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mIsGrantedMapPermission = true;
                } else {
                    mToast.cancel();
                    mToast.setText(getString(R.string.location_permission_deny_message));
                    mToast.show();
                }
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        updateLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("DetailActivity", "temporarily in a disconnected: " + i);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("DetailActivity", "connection failed: " + connectionResult);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if (mLocation == null) {
            mLocation = new Location("");
        }
        mLocation.setLatitude(latLng.latitude);
        mLocation.setLongitude(latLng.longitude);
        updateMarker(true);
    }

    @Override
    public void onBackPressed() {
        if (mId != DEFAULT_ALARM_ID) {
            insertOrUpdateAlarm();
        }
        super.onBackPressed();
    }
}
