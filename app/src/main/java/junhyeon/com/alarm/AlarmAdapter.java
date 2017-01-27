package junhyeon.com.alarm;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import junhyeon.com.alarm.model.Alarm;

public class AlarmAdapter extends RealmRecyclerViewAdapter<Alarm, AlarmAdapter.AlarmViewHolder> {
    private Context mContext;
    final private ItemClickListener mOnClickListener;

    interface ItemClickListener{
        void onItemClick(int position, int id);
    }

    public AlarmAdapter(@NonNull Context context, @Nullable OrderedRealmCollection<Alarm> data, boolean autoUpdate, ItemClickListener mOnClickListener) {
        super(context, data, autoUpdate);
        this.mOnClickListener = mOnClickListener;
        this.mContext = context;
    }

    @Override
    public AlarmViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForItem = R.layout.alarm_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForItem, parent, shouldAttachToParentImmediately);

        return new AlarmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AlarmViewHolder holder, int position) {
        holder.bind(position);
    }


    class AlarmViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        int mId;
        CardView mCardView;
        ImageButton mAlarmDeleteImageButton;
        ImageButton mAlarmImageButton;
        ImageButton mAlarmOnImageButton;
        TextView mAmPmTextView;
        TextView mTimeTextView;
        ArrayList<TextView> mDayTextViewList = new ArrayList<>();
        TextView mVibrateTextView;
        TextView mRepeatTextView;
        TextView mMemoTextView;
        TextView mLocationTextView;

        public AlarmViewHolder(View itemView) {
            super(itemView);
            mCardView = (CardView)itemView.findViewById(R.id.card_view);
            mAlarmDeleteImageButton = (ImageButton)itemView.findViewById(R.id.ib_alarm_delete);
            mAlarmImageButton = (ImageButton)itemView.findViewById(R.id.ib_alarm);
            mAlarmOnImageButton = (ImageButton)itemView.findViewById(R.id.ib_alarm_on);
            mAmPmTextView = (TextView)itemView.findViewById(R.id.tv_am_pm);
            mTimeTextView = (TextView)itemView.findViewById(R.id.tv_time);
            mDayTextViewList.add((TextView)itemView.findViewById(R.id.tv_sun));
            mDayTextViewList.add((TextView)itemView.findViewById(R.id.tv_mon));
            mDayTextViewList.add((TextView)itemView.findViewById(R.id.tv_tue));
            mDayTextViewList.add((TextView)itemView.findViewById(R.id.tv_wed));
            mDayTextViewList.add((TextView)itemView.findViewById(R.id.tv_thu));
            mDayTextViewList.add((TextView)itemView.findViewById(R.id.tv_fri));
            mDayTextViewList.add((TextView)itemView.findViewById(R.id.tv_sat));
            mVibrateTextView = (TextView)itemView.findViewById(R.id.tv_vibrate);
            mRepeatTextView = (TextView)itemView.findViewById(R.id.tv_repeat);
            mMemoTextView = (TextView)itemView.findViewById(R.id.tv_memo);
            mLocationTextView = (TextView)itemView.findViewById(R.id.tv_location);
            itemView.setOnClickListener(this);
            mAlarmDeleteImageButton.setOnClickListener(this);
            mAlarmImageButton.setOnClickListener(this);
            mAlarmOnImageButton.setOnClickListener(this);
        }

        void bind(int index){
            Alarm alarm = getData().get(index);
            mId = alarm.getId();

            if(alarm.getEnrolled()){
                mCardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.colorTeal200));
                mAlarmImageButton.setVisibility(View.GONE);
                mAlarmOnImageButton.setVisibility(View.VISIBLE);
            }else{
                mCardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.colorWhite));
                mAlarmImageButton.setVisibility(View.VISIBLE);
                mAlarmOnImageButton.setVisibility(View.GONE);
            }

            if(alarm.getHour() < 12){
                mAmPmTextView.setText(mContext.getString(R.string.am_label));
                String time = String.valueOf(alarm.getHour())+":"+String.valueOf(alarm.getMinute());
                mTimeTextView.setText(time);
            }else{
                mAmPmTextView.setText(mContext.getString(R.string.pm_label));
                String time = String.valueOf(alarm.getHour()-12)+":"+String.valueOf(alarm.getMinute());
                mTimeTextView.setText(time);
            }
            String repeatDay = alarm.getRepeatDay();

            for(int i=0; i<repeatDay.length(); ++i){
                if(repeatDay.charAt(i) == '1'){
                    mDayTextViewList.get(i).setTextColor(ContextCompat.getColor(mContext, R.color.colorTeal));
                }else{
                    mDayTextViewList.get(i).setTextColor(ContextCompat.getColor(mContext, R.color.colorGrey700));
                }
            }

            if(alarm.getVibrate()){
                mVibrateTextView.setTextColor(ContextCompat.getColor(mContext, R.color.colorTeal));
            }else{
                mVibrateTextView.setTextColor(ContextCompat.getColor(mContext, R.color.colorGrey700));
            }

            if(alarm.getRepeatInterval() == 0 && alarm.getRepeatNumber() == 0){
                mRepeatTextView.setTextColor(ContextCompat.getColor(mContext, R.color.colorGrey700));
            }else{
                mRepeatTextView.setTextColor(ContextCompat.getColor(mContext, R.color.colorTeal));
            }

            if(alarm.getMemo() != null){
                mMemoTextView.setTextColor(ContextCompat.getColor(mContext, R.color.colorTeal));
            }else{
                mMemoTextView.setTextColor(ContextCompat.getColor(mContext, R.color.colorGrey700));
            }

            if(alarm.getLat() == null && alarm.getLng() == null){
                mLocationTextView.setTextColor(ContextCompat.getColor(mContext, R.color.colorGrey700));
            }else{
                mLocationTextView.setTextColor(ContextCompat.getColor(mContext, R.color.colorTeal));
            }
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            switch (v.getId()){
                case R.id.ib_alarm_delete:
                    deleteAlarm(position);
                    break;
                case R.id.ib_alarm:
                    registerOrUnregisterAlarm(true);
                    break;
                case R.id.ib_alarm_on:
                    registerOrUnregisterAlarm(false);
                    break;
                default:
                    mOnClickListener.onItemClick(position, mId);
            }
        }

        private void deleteAlarm(final int position){

            Realm realm = MyApplication.getRealmInstance();
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Alarm alarm = realm.where(Alarm.class).equalTo("id", mId).findFirst();
                    alarm.deleteFromRealm();
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    notifyItemRemoved(position);
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    //핸들러 처리
                }
            });
        }

        private void registerOrUnregisterAlarm(final boolean isRegister){
            Realm realm = MyApplication.getRealmInstance();

            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Alarm alarm = realm.where(Alarm.class).equalTo("id", mId).findFirst();
                    alarm.setEnrolled(isRegister);
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    notifyDataSetChanged();
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    //핸들러 처리
                }
            });
        }
    }
}
