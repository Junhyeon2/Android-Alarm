package junhyeon.com.alarm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import io.realm.Realm;
import junhyeon.com.alarm.model.Alarm;

public class MainActivity extends AppCompatActivity implements AlarmAdapter.ItemClickListener{
    private Toolbar mToolbar;
    private RecyclerView mAlarmRecyclerView;
    private TextView mAlarmListStatusTextView;
    private AlarmAdapter mAlarmAdapter;
    private Intent mIntent;
    private Realm mRealm = MyApplication.getRealmInstance();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindViews();
        initToolBar();
        initAlarmRecyclerView();
    }

    private void initToolBar(){
        mToolbar.setTitle(getString(R.string.alarm_list_label));
        setSupportActionBar(mToolbar);
    }

    private void bindViews(){
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        mAlarmRecyclerView = (RecyclerView)findViewById(R.id.rv_alarm);
        mAlarmListStatusTextView = (TextView)findViewById(R.id.tv_alarm_list_status);
    }

    private void initAlarmRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mAlarmRecyclerView.setLayoutManager(layoutManager);
        mAlarmRecyclerView.setHasFixedSize(true);
        mAlarmAdapter = new AlarmAdapter(getApplicationContext(), mRealm.where(Alarm.class).findAllAsync(), true, this);
        mAlarmRecyclerView.setAdapter(mAlarmAdapter);

    }

    private void launchDetailActivity(){
        mIntent = new Intent(this, DetailActivity.class);
        startActivity(mIntent);
    }

    @Override
    protected void onResume() {
        mAlarmAdapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    private void onClickAddMenuItem(){
        launchDetailActivity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add:
                onClickAddMenuItem();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(int position, int id) {
        mIntent = new Intent(this, DetailActivity.class);
        mIntent.putExtra("id", id);
        startActivity(mIntent);
    }
}
