package junhyeon.com.alarm;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Calendar;

import io.realm.Realm;
import junhyeon.com.alarm.model.Alarm;

public class AlarmActivity extends AppCompatActivity implements OnMapReadyCallback {
    private int mId;
    private TextView mMemoTextView;
    private MediaPlayer mMediaPlayer;
    private Vibrator mVibrator;
    private boolean mIsClose;
    private LatLng mLatLng;
    private boolean mIsRepeat[];
    private Calendar currentDate;
    private Calendar targetDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        mMemoTextView = (TextView)findViewById(R.id.tv_memo);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mId = getIntent().getIntExtra("id", -1);
        if(mId != -1) {
            //id로 테이블 조회.
            Realm realm = MyApplication.getRealmInstance();
            realm.beginTransaction();
            Alarm alarm = realm.where(Alarm.class).equalTo("id", mId).findFirst();
            //알람음 재생.
            mMediaPlayer = MediaPlayer.create(this, Uri.parse(alarm.getRingtoneUri()));
            float volume = alarm.getVolume()/(float)100;
            mMediaPlayer.setVolume(volume, volume);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.start();

            //진동
            mVibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
            if(alarm.getVibrate()){
                Thread vibratorThread = new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        while (!mIsClose){
                            mVibrator.vibrate(500);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
                vibratorThread.start();
            }
            //메모 표시.
            if(alarm.getMemo() != null){
                mMemoTextView.setText(alarm.getMemo());
            }
            //위치 정보 표시.
            if(alarm.getLat() != null && alarm.getLng() != null) {
                mLatLng = new LatLng(alarm.getLat(), alarm.getLng());
                mapFragment.getMapAsync(this);
            }

            String repeatDay = alarm.getRepeatDay();
            mIsRepeat = new boolean[repeatDay.length()];

            boolean isNotRepeat = true;
            for(int i=0; i<repeatDay.length(); ++i){
                if(repeatDay.charAt(i) == '1'){
                    mIsRepeat[i] = true;
                    isNotRepeat = false;
                }else{
                    mIsRepeat[i] = false;
                }
            }
            if(isNotRepeat){
                alarm.setEnrolled(false);
                realm.insertOrUpdate(alarm);
                mIsRepeat = null;
            }
            long now = System.currentTimeMillis();
            currentDate = Calendar.getInstance();
            currentDate.setTimeInMillis(now);

            targetDate = Calendar.getInstance();
            targetDate.setTimeInMillis(now);
            targetDate.set(Calendar.HOUR_OF_DAY, alarm.getHour());
            targetDate.set(Calendar.MINUTE, alarm.getMinute());
            targetDate.set(Calendar.SECOND, 0);

            realm.commitTransaction();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions().position(mLatLng));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(mLatLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);
    }

    public void onClickButton(View view){
        mIsClose = true;
        mMediaPlayer.stop();
        switch (view.getId()){
            case R.id.ib_repeat:
                //스누즈
                break;
            case R.id.ib_close:
                //반복 요일 있을 시 등록.
                AlarmUtils.registerAlarm(
                        this,
                        mId,
                        mIsRepeat,
                        currentDate,
                        targetDate
                );
                break;
        }
        finish();
    }
}
