package com.seraph.ppschedule;

import android.content.Context;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.jimmy.common.listener.OnTaskFinishedListener;
import com.seraph.ppschedule.bean.Schedule;

import java.util.List;

class ScheduleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "ScheduleAdapter";

    private Context mContext;
    private List<Schedule> mSchedules;

    public ScheduleAdapter(Context mContext, List<Schedule> mSchedules) {
        this.mContext = mContext;
        this.mSchedules = mSchedules;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ScheduleViewHolder(LayoutInflater.from(mContext). inflate(R.layout.item_schedule, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        final Schedule schedule = mSchedules.get(i);
        final ScheduleViewHolder scheduleViewHolder = (ScheduleViewHolder) viewHolder;

        //根据Schedule数据设置控件状态
        scheduleViewHolder.cbScheduleState.setChecked(schedule.isFinish());
        scheduleViewHolder.tvScheduleTitle.setText(schedule.getTitle());

        if(schedule.isFinish()) { //如果任务状态为已完成
            scheduleViewHolder.tvScheduleTitle
                    .setPaintFlags(scheduleViewHolder.tvScheduleTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);  //为标题设置删除线
        }


        // TODO: 2022/4/9 CheckBox的选中状态改变后，还需要随之同步Schedule的完成状态到DB中
        //为CheckBox注册监听事件
        scheduleViewHolder.cbScheduleState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    schedule.setFinish(false);
                    scheduleViewHolder.cbScheduleState.setChecked(false);  //取消选中CheckBox
                    scheduleViewHolder.tvScheduleTitle
                            .setPaintFlags(scheduleViewHolder.tvScheduleTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));  //取消删除线
                } else {
                    schedule.setFinish(true);
                    scheduleViewHolder.cbScheduleState.setChecked(true);  //选中CheckBox
                    scheduleViewHolder.tvScheduleTitle
                            .setPaintFlags(scheduleViewHolder.tvScheduleTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);  //设置删除线
                }

                changeScheduleItem(schedule);  //更新schedule item的UI状态
            }
        });

        //为整个任务条设置点击事件，跳转至ScheduleDetailActivity
        scheduleViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: " + "[id=" + schedule.getId() + ", isFinish=" + schedule.isFinish() + ", title=" + schedule.getTitle() + "]");
                Toast.makeText(mContext, "[id=" + schedule.getId() + ", isFinish=" + schedule.isFinish() + ", title=" + schedule.getTitle() + "]", Toast.LENGTH_SHORT).show();
            }
        });

        // TODO: 2022/4/9 注册长按的监听事件：弹出删除任务的对话框
    }

    @Override
    public int getItemCount() {
        return mSchedules.size();
    }

    protected class ScheduleViewHolder extends RecyclerView.ViewHolder {
        protected CheckBox cbScheduleState;
        protected TextView tvScheduleTitle;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);

            cbScheduleState = itemView.findViewById(R.id.cbScheduleState);
            tvScheduleTitle = itemView.findViewById(R.id.tvScheduleTitle);
        }

    }

    // TODO: 2022/4/9 添加&删除item之后还需要更新DB，放到adapter中完成
    /**
     * 添加schedule item
     * @param schedule
     */
    public void insertItem(Schedule schedule) {
        mSchedules.add(schedule);
        notifyItemInserted(mSchedules.size() - 1);
    }

    /**
     * 删除schedule item
     * @param schedule
     */
    public void removeItem(Schedule schedule) {
        if(mSchedules.remove(schedule)) {
            notifyDataSetChanged();
        }
    }

    /**
     * 更新单个schedule item的状态
     * @param schedule
     */
    private void changeScheduleItem(Schedule schedule) {
        int i = mSchedules.indexOf(schedule);
        if (i != -1) {
            notifyItemChanged(i);
        }
    }

    /**
     * 更新Adapter数据源（mSchedules），保证Schedule的变更及时反馈到ListView中
     * @param schedules
     */
    private void updateAllScheduleData(List<Schedule> schedules) {
        mSchedules = schedules;
        notifyDataSetChanged();
    }


}
