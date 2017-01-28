package junhyeon.com.alarm.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import io.realm.Realm;
import io.realm.RealmResults;
import junhyeon.com.alarm.MyApplication;
import junhyeon.com.alarm.model.Alarm;

public class AlarmProvider extends ContentProvider {
    public static final String[] sColumns = new String[]{
            "id",
            "hour",
            "minute",
            "repeatDay",
            "volume",
            "ringtoneUri",
            "repeatInterval",
            "repeatNumber",
            "memo",
            "lat",
            "lng",
            "isEnrolled",
            "isVibrate",
            "timeStamp"
    };

    @Override
    public boolean onCreate() {

        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Realm realm = MyApplication.getRealmInstance();
        RealmResults<Alarm> results = realm.where(Alarm.class).findAll();
        MatrixCursor cursor = new MatrixCursor(sColumns);

        for (Alarm alarm : results) {
            Object[] rowData = new Object[]{
                    alarm.getId(),
                    alarm.getHour(),
                    alarm.getMinute(),
                    alarm.getRepeatDay(),
                    alarm.getVolume(),
                    alarm.getRingtoneUri(),
                    alarm.getRepeatInterval(),
                    alarm.getRepeatNumber(),
                    alarm.getMemo(),
                    alarm.getLat(),
                    alarm.getLng(),
                    alarm.getEnrolled(),
                    alarm.getVibrate(),
                    alarm.getTimeStamp()
            };
            cursor.addRow(rowData);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
