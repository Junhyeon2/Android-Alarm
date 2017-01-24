package junhyeon.com.alarm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements AlarmAdapter.ItemClickListener{
    private Toolbar mToolbar;
    private RecyclerView mAlarmRecyclerView;
    private TextView mAlarmListStatusTextView;
    private AlarmAdapter mAlarmAdapter;

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
        mAlarmRecyclerView.setLayoutManager(layoutManager); //RecyclerView에 LinearLayoutManager 설정.

        //어댑터 등록.
        mAlarmAdapter = new AlarmAdapter(getApplicationContext(), this);
        mAlarmRecyclerView.setAdapter(mAlarmAdapter);
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
                Intent intent = new Intent(this, DetailActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "position: "+position, Toast.LENGTH_SHORT).show();
    }
}
