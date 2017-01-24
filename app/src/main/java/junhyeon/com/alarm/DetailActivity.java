package junhyeon.com.alarm;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity implements View.OnTouchListener, OnMapReadyCallback{
    private static final int PICK_ALARM_RINGTONE_REQUEST = 1;
    private Toolbar mToolbar;
    private ScrollView mScrollView;
    private TimePicker mTimePicker;
    private DiscreteSeekBar mSeekBar;
    private TextView mRingtoneTitleTextView;
    private TextView mRingtoneUriTextView;
    private EditText mMemoEditText;
    private ImageView mTransparentImageView;
    private ArrayList<ToggleButton> mDayToggleButtonList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        bindViews();
        setEventListeners();
        initToolBar();
        setRingtoneValue();
    }

    private void bindViews(){
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        mScrollView = (ScrollView)findViewById(R.id.scroll_view);
        mTimePicker = (TimePicker)findViewById(R.id.time_picker);
        mDayToggleButtonList.add((ToggleButton)findViewById(R.id.bt_sun));
        mDayToggleButtonList.add((ToggleButton)findViewById(R.id.bt_mon));
        mDayToggleButtonList.add((ToggleButton)findViewById(R.id.bt_tue));
        mDayToggleButtonList.add((ToggleButton)findViewById(R.id.bt_wed));
        mDayToggleButtonList.add((ToggleButton)findViewById(R.id.bt_thu));
        mDayToggleButtonList.add((ToggleButton)findViewById(R.id.bt_fri));
        mDayToggleButtonList.add((ToggleButton)findViewById(R.id.bt_sat));
        mRingtoneTitleTextView = (TextView)findViewById(R.id.tv_ringtone_title);
        mRingtoneUriTextView = (TextView)findViewById(R.id.tv_ringtone_uri);
        mSeekBar = (DiscreteSeekBar)findViewById(R.id.seek_bar);
        mMemoEditText = (EditText)findViewById(R.id.et_memo);
        mTransparentImageView = (ImageView)findViewById(R.id.iv_transparent);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private void setEventListeners(){
        mMemoEditText.setOnTouchListener(this);
        mTransparentImageView.setOnTouchListener(this);
    }

    private void initToolBar(){
        mToolbar.setTitle(getString(R.string.alarm_add_label));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setRingtoneValue(){
        //Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Uri defaultRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALARM);
        Ringtone defaultRingtone = RingtoneManager.getRingtone(getApplicationContext(), defaultRingtoneUri);
        mRingtoneTitleTextView.setText(defaultRingtone.getTitle(this));
        mRingtoneUriTextView.setText(defaultRingtoneUri.toString());
        Log.d("DetailActivity", mRingtoneUriTextView.getText().toString());
    }


    public void onClickLayout(View view){
        switch (view.getId()){
            case R.id.layout_ringtone:
                Intent launchRingtoneActivityIntent = new Intent(this, RingtoneActivity.class);
                launchRingtoneActivityIntent.putExtra("title", mRingtoneTitleTextView.getText().toString());
                startActivityForResult(launchRingtoneActivityIntent, PICK_ALARM_RINGTONE_REQUEST);
                break;
        }
    }

    public void onClickToggleButton(View view){
        if(view instanceof ToggleButton){
            ToggleButton toggleButton = (ToggleButton)view;
            if(toggleButton.isChecked()){
                toggleButton.setTextColor(ContextCompat.getColor(this, R.color.colorTeal));
            } else {
                toggleButton.setTextColor(ContextCompat.getColor(this, R.color.colorGrey400));
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int id = v.getId();
        if(id == R.id.et_memo || id == R.id.iv_transparent) {
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
    public void onMapReady(final GoogleMap map) {
        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(-34, 151);
        map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add:
                Toast.makeText(this, "추가 버튼 클릭", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_ALARM_RINGTONE_REQUEST){
            if(resultCode == RESULT_OK){
                Bundle bundle = data.getExtras();
                mRingtoneTitleTextView.setText(bundle.getString("title"));
                mRingtoneUriTextView.setText(bundle.getString("uri"));
            }
        }
    }
}
