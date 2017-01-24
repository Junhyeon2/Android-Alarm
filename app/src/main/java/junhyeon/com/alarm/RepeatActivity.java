package junhyeon.com.alarm;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

public class RepeatActivity extends AppCompatActivity implements SwitchCompat.OnCheckedChangeListener{
    private Toolbar mToolbar;
    private SwitchCompat mRepeatSwitchCompat;
    private RadioGroup mRepeatIntervalRadioGroup;
    private RadioGroup mRepeatNumberRadioGroup;
    private int mRepeatInterval[];
    private int mRepeatNumber[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repeat);

        bindViews();
        setEventListeners();
        initToolBar();
        initSwitch();
        initRadioButtons();
    }

    private void bindViews(){
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        mRepeatSwitchCompat = (SwitchCompat)findViewById(R.id.sw_repeat);
        mRepeatIntervalRadioGroup = (RadioGroup)findViewById(R.id.rg_repeat_interval);
        mRepeatNumberRadioGroup = (RadioGroup)findViewById(R.id.rg_repeat_number);
    }

    private void initToolBar(){
        mToolbar.setTitle(getString(R.string.ringtone_select_label));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initSwitch(){
        mRepeatSwitchCompat.setTextColor(ContextCompat.getColorStateList(this, R.color.radio_button_selector));
    }

    private void initRadioButtons(){
        mRepeatInterval = getResources().getIntArray(R.array.repeat_interval_array);
        mRepeatNumber = getResources().getIntArray(R.array.repeat_number_array);

        RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        float scale = getResources().getDisplayMetrics().density;
        ColorStateList colorStateList = ContextCompat.getColorStateList(this, R.color.radio_button_selector);

        for(int i=0; i<mRepeatInterval.length; ++i){
            String repeatIntervalLabel = String.valueOf(mRepeatInterval[i]) + getString(R.string.minute_label);
            AppCompatRadioButton radioButton = ViewUtils.getRadioButton(this, params, scale, repeatIntervalLabel, colorStateList, i);
            mRepeatIntervalRadioGroup.addView(radioButton);
        }
        mRepeatIntervalRadioGroup.check(0);

        for(int i=0; i<mRepeatNumber.length; ++i){
            String repeatNumberLabel = String.valueOf(mRepeatNumber[i]) + getString(R.string.number_label);
            AppCompatRadioButton radioButton = ViewUtils.getRadioButton(this, params, scale, repeatNumberLabel, colorStateList, i);
            mRepeatNumberRadioGroup.addView(radioButton);
        }
        mRepeatNumberRadioGroup.check(0);
    }

    private void setEventListeners(){
        mRepeatSwitchCompat.setOnCheckedChangeListener(this);
        mRepeatSwitchCompat.setChecked(true);
    }

    @Override
    public void onBackPressed() {
        Bundle bundle = new Bundle();
        bundle.putInt("interval", mRepeatInterval[mRepeatIntervalRadioGroup.getCheckedRadioButtonId()]);
        bundle.putInt("number", mRepeatNumber[mRepeatNumberRadioGroup.getCheckedRadioButtonId()]);

        Intent resultIntent = new Intent();
        resultIntent.putExtras(bundle);

        setResult(RESULT_OK, resultIntent);
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
            buttonView.setText(getString(R.string.use_label));
        }else{
            buttonView.setText(getString(R.string.no_use_label));
            Intent resultIntent = new Intent();
            setResult(RESULT_CANCELED, resultIntent);
            finish();
        }
    }
}
