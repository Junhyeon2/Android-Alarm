package junhyeon.com.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import io.realm.Realm;
import junhyeon.com.alarm.model.Alarm;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //intent를 통해서 받을 데이터: id, isRepeat
        Bundle bundle = intent.getExtras();
        int id = bundle.getInt("id");
        Realm realm = MyApplication.getRealmInstance();
        Alarm alarm = realm.where(Alarm.class).equalTo("id", id).findFirst();
        if(alarm.getEnrolled()) {
            Intent launchAlarmActivity = new Intent(context, AlarmActivity.class);
            launchAlarmActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //launchAlarmActivity에 보낼 데이터 id
            launchAlarmActivity.putExtra("id", id);
            context.startActivity(launchAlarmActivity);
        }
    }
}
