package junhyeon.com.alarm;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {
    private Context mContext;
    final private ItemClickListener mOnClickListener;

    interface ItemClickListener{
        void onItemClick(int position);
    }

    public AlarmAdapter(Context mContext, ItemClickListener mOnClickListener) {
        this.mContext = mContext;
        this.mOnClickListener = mOnClickListener;
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

    @Override
    public int getItemCount() {
        return 10;
    }

    class AlarmViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        CardView mCardView;
        ImageButton mAlarmDeleteImageButton;
        ImageButton mAlarmImageButton;
        ImageButton mAlarmOnImageButton;
        TextView mAmPmTextView;
        TextView mTimeTextView;
        TextView mSunTextView;
        TextView mMonTextView;
        TextView mTueTextView;
        TextView mWedTextView;
        TextView mThuTextView;
        TextView mFriTextView;
        TextView mSatTextView;
        TextView mSoundTextView;
        TextView mVibrateTextView;

        public AlarmViewHolder(View itemView) {
            super(itemView);
            mCardView = (CardView)itemView.findViewById(R.id.card_view);
            mAlarmDeleteImageButton = (ImageButton)itemView.findViewById(R.id.ib_alarm_delete);
            mAlarmImageButton = (ImageButton)itemView.findViewById(R.id.ib_alarm);
            mAlarmOnImageButton = (ImageButton)itemView.findViewById(R.id.ib_alarm_on);
            mAmPmTextView = (TextView)itemView.findViewById(R.id.tv_am_pm);
            mTimeTextView = (TextView)itemView.findViewById(R.id.tv_time);
            mSunTextView = (TextView)itemView.findViewById(R.id.tv_sun);
            mMonTextView = (TextView)itemView.findViewById(R.id.tv_mon);
            mTueTextView = (TextView)itemView.findViewById(R.id.tv_tue);
            mWedTextView = (TextView)itemView.findViewById(R.id.tv_wed);
            mThuTextView = (TextView)itemView.findViewById(R.id.tv_thu);
            mFriTextView = (TextView)itemView.findViewById(R.id.tv_fri);
            mSatTextView = (TextView)itemView.findViewById(R.id.tv_sat);
            mSoundTextView = (TextView)itemView.findViewById(R.id.tv_sound);
            mVibrateTextView = (TextView)itemView.findViewById(R.id.tv_vibrate);
            itemView.setOnClickListener(this);
            mAlarmDeleteImageButton.setOnClickListener(this);
            mAlarmImageButton.setOnClickListener(this);
            mAlarmOnImageButton.setOnClickListener(this);
        }

        void bind(int index){

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ib_alarm_delete:
                    break;
                case R.id.ib_alarm:
                    registerAlarm();
                    break;
                case R.id.ib_alarm_on:
                    unregisterAlarm();
                    break;
                default:
                    int position = getAdapterPosition();
                    mOnClickListener.onItemClick(position);
            }
        }

        private void registerAlarm(){
            mAlarmImageButton.setVisibility(View.GONE);
            mAlarmOnImageButton.setVisibility(View.VISIBLE);
            mCardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.colorTeal200));

        }
        private void unregisterAlarm(){
            mAlarmImageButton.setVisibility(View.VISIBLE);
            mAlarmOnImageButton.setVisibility(View.GONE);
            mCardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.colorWhite));
        }
    }
}
