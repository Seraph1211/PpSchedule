package com.seraph.ppschedule.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.seraph.ppschedule.R;
import com.seraph.ppschedule.activity.ScheduleDetailActivity;
import com.seraph.ppschedule.bean.Schedule;
import com.seraph.ppschedule.dao.ScheduleDao;
import com.seraph.ppschedule.dialog.ConfirmDialog;
import com.seraph.ppschedule.fragment.BaseFragment;
import com.seraph.ppschedule.fragment.EventSetFragment;
import com.seraph.ppschedule.utils.DateUtils;

import java.util.Iterator;
import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "ScheduleAdapter";

    private Context mContext;
    private BaseFragment mFragment;
    private List<Schedule> mSchedules;

    private SparseBooleanArray mCheckState = new SparseBooleanArray();  //用于避免因RecyclerView复用item而导致item数据错乱

    private RecyclerView mRv;

    public ScheduleAdapter(Context mContext, BaseFragment mFragment, List<Schedule> mSchedules, RecyclerView mRv) {
        this.mContext = mContext;
        this.mFragment = mFragment;
        this.mSchedules = mSchedules;
        this.mRv = mRv;

        initCheckState();
    }

    /**
     * 根据mScheduleList初始化标记数组
     */
    private void initCheckState() {
        Iterator<Schedule> iterator = mSchedules.iterator();
        Schedule schedule;
        while(iterator.hasNext()) {
            schedule = iterator.next();
            mCheckState.put(mSchedules.indexOf(schedule), schedule.isFinish());
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ScheduleViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_schedule, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        final Schedule schedule = mSchedules.get(i);
        final ScheduleViewHolder scheduleViewHolder = (ScheduleViewHolder) viewHolder;

        Log.d(TAG, "onBindViewHolder: schedule=" + schedule.toString());

        //根据Schedule数据设置控件状态
        scheduleViewHolder.cbScheduleState.setTag(i);
        scheduleViewHolder.tvScheduleTitle.setText(schedule.getTitle());

        if(mCheckState.get(i, false)) { //如果任务状态为已完成
            scheduleViewHolder.cbScheduleState.setChecked(true);
            //为标题设置删除线
            scheduleViewHolder.tvScheduleTitle
                    .setPaintFlags(scheduleViewHolder.tvScheduleTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            //变更字体颜色
            scheduleViewHolder.tvScheduleTitle
                    .setTextColor(mContext.getResources().getColor(R.color.color_schedule_finish_title_text));
        } else {
            scheduleViewHolder.cbScheduleState.setChecked(false);
            //取消删除线
            scheduleViewHolder.tvScheduleTitle
                    .setPaintFlags(scheduleViewHolder.tvScheduleTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            //变更字体颜色
            scheduleViewHolder.tvScheduleTitle
                    .setTextColor(mContext.getResources().getColor(R.color.color_schedule_title_text));
        }

        if(schedule.getTime() != 0) {
            scheduleViewHolder.tvScheduleTime.setText(DateUtils.timeStamp2Time(schedule.getTime()));
        } else {
            scheduleViewHolder.tvScheduleTime.setText("");
        }


        //为CheckBox注册监听事件
        scheduleViewHolder.cbScheduleState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "onCheckedChanged: isChecked=" + isChecked);
                int position = (int) buttonView.getTag();
                if(!isChecked) {
                    mCheckState.put(position, false);
                    schedule.setFinish(false);
                    //取消删除线
                    scheduleViewHolder.tvScheduleTitle
                            .setPaintFlags(scheduleViewHolder.tvScheduleTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    //变更字体颜色
                    scheduleViewHolder.tvScheduleTitle
                            .setTextColor(mContext.getResources().getColor(R.color.color_schedule_title_text));

                } else {
                    mCheckState.put(position, true);
                    schedule.setFinish(true);
                    //设置删除线
                    scheduleViewHolder.tvScheduleTitle
                            .setPaintFlags(scheduleViewHolder.tvScheduleTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    //变更字体颜色
                    scheduleViewHolder.tvScheduleTitle
                            .setTextColor(mContext.getResources().getColor(R.color.color_schedule_finish_title_text));
                }

                ScheduleDao.getInstance().updateSchedule(schedule);
                changeScheduleItem(schedule);  //更新schedule item的UI状态
            }
        });

        //为整个任务条设置点击事件，跳转至ScheduleDetailActivity
        scheduleViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: " + "[id=" + schedule.getId() + ", isFinish=" + schedule.isFinish() + ", title=" + schedule.getTitle() + "]");

                mContext.startActivity(new Intent(mContext, ScheduleDetailActivity.class)
                        .putExtra(ScheduleDetailActivity.SCHEDULE_OBJ, schedule)
                        .putExtra(ScheduleDetailActivity.CALENDAR_POSITION, mFragment.getCurrentCalendarPosition())
                        .putExtra(ScheduleDetailActivity.TOOLBAR_TITLE, "日程"));
            }
        });

        scheduleViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d(TAG, "onLongClick: ing");
                showDeleteScheduleDialog(schedule);
                return false;
            }
        });


    }

    @Override
    public int getItemCount() {
        return mSchedules == null ? 0 : mSchedules.size();
    }

    /**
     * 适配日程（Schedule)内的item
     */
    protected class ScheduleViewHolder extends RecyclerView.ViewHolder {
        protected CheckBox cbScheduleState;
        protected TextView tvScheduleTitle;
        protected TextView tvScheduleTime;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);

            cbScheduleState = itemView.findViewById(R.id.cbScheduleState);
            tvScheduleTitle = itemView.findViewById(R.id.tvScheduleTitle);
            tvScheduleTime = itemView.findViewById(R.id.tvScheduleTime);
        }

    }

    /**
     * 适配收集箱(Event Set)内的item
     */
    protected class EventViewHolder extends RecyclerView.ViewHolder {
        protected CheckBox cbEventState;
        protected TextView tvEventTitle;
        protected TextView tvEventTime;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);

            cbEventState = itemView.findViewById(R.id.cbEventState);
            tvEventTitle = itemView.findViewById(R.id.tvEventTitle);
            tvEventTime = itemView.findViewById(R.id.tvEventTime);
        }

    }

    /**
     * 展示删除Item的对话框
     * @param schedule
     */
    private void showDeleteScheduleDialog(final Schedule schedule) {
        new ConfirmDialog(mContext, R.string.schedule_delete_this_schedule, new ConfirmDialog.OnClickListener() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onConfirm() {
                removeItem(schedule);
            }
        }).show();
    }

    /**
     * 添加schedule item
     * @param schedule
     */
    public void insertItem(Schedule schedule) {
        while(!mRv.isComputingLayout() && mRv.getScrollState() == RecyclerView.SCROLL_STATE_IDLE) {
            mSchedules.add(schedule);
            ScheduleDao.getInstance().addSchedule(schedule.getTitle(), schedule.getDesc(), schedule.getDate(), schedule.getTime());
            mCheckState.put(mSchedules.size(), schedule.isFinish());
            Log.d(TAG, "insertItem: mCheckState=" + mCheckState.toString());
            notifyItemInserted(mSchedules.size() - 1);
            notifyDataSetChanged();
            mFragment.resetVisibilityOfNoTaskView();
            break;
        }

    }

    /**
     * 删除schedule item
     * @param schedule
     */
    public void removeItem(Schedule schedule) {
        final int position = mSchedules.indexOf(schedule);
        if(position != -1) {
            while(!mRv.isComputingLayout() && mRv.getScrollState() == RecyclerView.SCROLL_STATE_IDLE) {
                mCheckState.delete(position);
                mSchedules.remove(position);
                ScheduleDao.getInstance().deleteSchedule(schedule.getId());
                notifyItemRemoved(position);
                notifyDataSetChanged();
                mFragment.resetVisibilityOfNoTaskView();
                break;
            }
        }
    }

    /**
     * 更新单个schedule item的状态
     * @param schedule
     */
    private void changeScheduleItem(Schedule schedule) {
        final int i = mSchedules.indexOf(schedule);
        if (i != -1) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    notifyItemChanged(i);
                }
            });
        }
    }

    /**
     * 更新Adapter数据源（mSchedules），保证Schedule的变更及时反馈到ListView中
     * @param schedules
     */
    public void updateAllScheduleData(List<Schedule> schedules) {
        Log.d(TAG, "updateAllScheduleData: ing");
        while(!mRv.isComputingLayout() && mRv.getScrollState() == RecyclerView.SCROLL_STATE_IDLE) {
            Log.d(TAG, "updateAllScheduleData: ing2");
            mSchedules = schedules;
            notifyDataSetChanged();
            break;
        }

    }


}
