package junhyeon.com.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.util.Calendar;

public class AlarmUtils {
    private static final String ACTION = "junhyeon.com.alarm.Alarm";
    private static final long DAY_OF_MILLIS = 24*60*60*1000;

    public static void registerAlarm(Context context, int id, boolean isRepeat[], Calendar currentDate, Calendar targetDate){
        Intent alarmIntent = new Intent(ACTION);

        if(isRepeat == null){ //반복이 없는 경우
            Log.d("AlarmUtils", "isRepeat is null");
            if(currentDate.getTimeInMillis() - targetDate.getTimeInMillis() >= 0){//현재 시각보다 이전 시각으로 등록할 경우 DAY는 다음날
                Log.d("AlarmUtils", "현재 시각보다 같거나 이전 시각으로 등록할 경우");
                long nextDay = targetDate.getTimeInMillis() + DAY_OF_MILLIS;
                targetDate.setTimeInMillis(nextDay);
            }
        }else{ //반복이 있는 경우
            Log.d("AlarmUtils", "isRepeat is not null");
            //가장 가까운 반복요일로 등록
            int today = currentDate.get(Calendar.DAY_OF_WEEK) - 1; // 1~7 -> 0~6;
            int nextDay = 0;
            for(int i=0; i<isRepeat.length; ++i){
                if(isRepeat[((i+today)%isRepeat.length)]){
                    nextDay = i;
                    break;
                }
            }

            long targetTime = targetDate.getTimeInMillis();

            if (nextDay == 0 ){ //가장 가까운 반복 요일이 오늘인 경우.
                if(currentDate.getTimeInMillis() - targetDate.getTimeInMillis() >= 0){//현재 시각보다 이전 시각으로 등록할 경우 일주일후로 등록.
                    //현재 시간을 가져와서. 일주일을 더해준다.
                    targetTime += DAY_OF_MILLIS*7;
                    targetDate.setTimeInMillis(targetTime);
                }
            } else { //오늘이 아닌 경우
                targetTime += DAY_OF_MILLIS*nextDay;
                targetDate.setTimeInMillis(targetTime);
            }
        }
        targetDate.set(Calendar.SECOND, 0);

        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        alarmIntent.putExtras(bundle);


        Log.d("AlarmUtils", "등록 id: "+id);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            manager.setAlarmClock(new AlarmManager.AlarmClockInfo(targetDate.getTimeInMillis(), pendingIntent), pendingIntent);
        } else {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                manager.setExact(AlarmManager.RTC_WAKEUP, targetDate.getTimeInMillis(), pendingIntent);
            } else {
                manager.set(AlarmManager.RTC_WAKEUP, targetDate.getTimeInMillis(), pendingIntent);
            }
        }
    }

    public static void unregisterAlarm(Context context, int id){
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        pendingIntent.cancel();

        Log.d("AlarmUtils", "삭제 id: "+id);
    }
}
