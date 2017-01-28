package junhyeon.com.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmResults;
import junhyeon.com.alarm.model.Alarm;

public class DeviceBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Realm realm = MyApplication.getRealmInstance();
            RealmResults<Alarm> alarmRealmResults = realm.where(Alarm.class).equalTo("isEnrolled", true).findAll();

            for(int i=0; i<alarmRealmResults.size(); ++i){
                Alarm alarm = alarmRealmResults.get(i);
                int id = alarm.getId();
                String repeatDay = alarm.getRepeatDay();
                boolean isRepeat[] = new boolean[repeatDay.length()];

                boolean isNotRepeat = true;
                for(int j=0; j<repeatDay.length(); ++j){
                    if(repeatDay.charAt(i) == '1'){
                        isRepeat[i] = true;
                        isNotRepeat = false;
                    }else{
                        isRepeat[i] = false;
                    }
                }
                if(isNotRepeat){
                    alarm.setEnrolled(false);
                    realm.insertOrUpdate(alarm);
                    isRepeat = null;
                }

                long now = System.currentTimeMillis();
                Calendar currentDate = Calendar.getInstance();
                currentDate.setTimeInMillis(now);

                Calendar  targetDate = Calendar.getInstance();
                targetDate.setTimeInMillis(now);
                targetDate.set(Calendar.HOUR_OF_DAY, alarm.getHour());
                targetDate.set(Calendar.MINUTE, alarm.getMinute());
                targetDate.set(Calendar.SECOND, 0);

                AlarmUtils.registerAlarm(
                        context,
                        id,
                        isRepeat,
                        currentDate,
                        targetDate
                );
            }
        }
    }
}
