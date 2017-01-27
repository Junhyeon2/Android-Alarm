package junhyeon.com.alarm;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import java.util.ArrayList;

public class RingtoneActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener{
    private static final int INDEX_RINGTONE_TITLE = 0;
    private static final int INDEX_RINGTONE_URI = 1;
    private Toolbar mToolbar;
    private RadioGroup mRadioGroup;
    private ArrayList<String[]> mRingtoneList;
    private Ringtone mRingtone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ringtone);

        bindViews();
        setEventListeners();
        initToolBar();
        initRadioButtonsAndList();
    }
    private void bindViews() {
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        mRadioGroup = (RadioGroup)findViewById(R.id.radio_group);
    }

    private void setEventListeners(){
        mRadioGroup.setOnCheckedChangeListener(this);
    }

    private void initToolBar(){
        mToolbar.setTitle(getString(R.string.ringtone_select_label));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initRadioButtonsAndList(){
        Intent intent = getIntent();
        String ringtoneTitle = intent.getStringExtra("title");

        RingtoneManager manager = new RingtoneManager(this);

        manager.setType(RingtoneManager.TYPE_ALARM);
        Cursor cursor = manager.getCursor();

        mRingtoneList = new ArrayList<>();

        int selectedRingtoneId = -1;

        RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        float scale = getResources().getDisplayMetrics().density;
        ColorStateList colorStateList = ContextCompat.getColorStateList(this, R.color.radio_button_selector);

        while (cursor.moveToNext()) {
            String title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
            String index = cursor.getString(RingtoneManager.ID_COLUMN_INDEX);
            String uri = cursor.getString(RingtoneManager.URI_COLUMN_INDEX)+"/"+index;

            AppCompatRadioButton radioButton = ViewUtils.getRadioButton(this, params, scale, title, colorStateList, mRingtoneList.size());
            if(title.equals(ringtoneTitle))
                selectedRingtoneId = radioButton.getId();
            mRadioGroup.addView(radioButton);
            mRingtoneList.add(new String[]{title, uri});
        }
        mRadioGroup.check(selectedRingtoneId);

        cursor.close();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(mRingtone != null){
            mRingtone.stop();
        }
        mRingtone = RingtoneManager.getRingtone(getApplicationContext(), Uri.parse(mRingtoneList.get(checkedId)[INDEX_RINGTONE_URI]));
        mRingtone.play();
    }

    @Override
    public void onBackPressed() {
        if(mRingtone != null){
            mRingtone.stop();
        }
        Bundle bundle = new Bundle();
        bundle.putString("title", mRingtoneList.get(mRadioGroup.getCheckedRadioButtonId())[INDEX_RINGTONE_TITLE]);
        bundle.putString("uri", mRingtoneList.get(mRadioGroup.getCheckedRadioButtonId())[INDEX_RINGTONE_URI]);


        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
