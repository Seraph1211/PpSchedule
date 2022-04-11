package com.seraph.ppschedule.adapter;

import android.content.Context;
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
import com.seraph.ppschedule.bean.Schedule;
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

    public ScheduleAdapter(Context mContext, BaseFragment mFragment, List<Schedule> mSchedules) {
        this.mContext = mContext;
        this.mFragment = mFragment;
        this.mSchedules = mSchedules;

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
        if(mFragment instanceof EventSetFragment) {
            return new EventViewHolder(LayoutInflater.from(mContext). inflate(R.layout.item_event, viewGroup, false));
        }

        return new ScheduleViewHolder(LayoutInflater.from(mContext). inflate(R.layout.item_schedule, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        final Schedule schedule = mSchedules.get(i);

        if(viewHolder instanceof ScheduleViewHolder) {
            final ScheduleViewHolder scheduleViewHolder = (ScheduleViewHolder) viewHolder;

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

            // TODO: 2022/4/10 没有兼容用户只设置了日期，没有设置具体时间的情况
            if(schedule.getDate() != null) {
                scheduleViewHolder.tvScheduleTime.setText(DateUtils.date2Time(schedule.getDate().getTime()));
            } else {
                scheduleViewHolder.tvScheduleTime.setText("");
            }

            // TODO: 2022/4/9 CheckBox的选中状态改变后，还需要随之同步Schedule的完成状态到DB中
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
            scheduleViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Log.d(TAG, "onLongClick: ing");
                    showDeleteScheduleDialog(schedule);
                    return false;
                }
            });
        } else if(viewHolder instanceof EventViewHolder) {
            final EventViewHolder eventViewHolder = (EventViewHolder) viewHolder;

            //根据Schedule数据设置控件状态
            eventViewHolder.cbEventState.setTag(i);
            eventViewHolder.tvEventTitle.setText(schedule.getTitle());

            if(mCheckState.get(i, false)) { //如果任务状态为已完成
                eventViewHolder.cbEventState.setChecked(true);
                //为标题设置删除线
                eventViewHolder.tvEventTitle
                        .setPaintFlags(eventViewHolder.tvEventTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                //变更字体颜色
                eventViewHolder.tvEventTitle
                        .setTextColor(mContext.getResources().getColor(R.color.color_schedule_finish_title_text));
            } else {
                eventViewHolder.cbEventState.setChecked(false);
                //取消删除线
                eventViewHolder.tvEventTitle
                        .setPaintFlags(eventViewHolder.tvEventTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                //变更字体颜色
                eventViewHolder.tvEventTitle
                        .setTextColor(mContext.getResources().getColor(R.color.color_schedule_title_text));
            }


            if(schedule.getDate() != null) {
                eventViewHolder.tvEventTime.setText(DateUtils.date2DateString(schedule.getDate().getTime()));
            } else {
                eventViewHolder.tvEventTime.setText("");
            }

            // TODO: 2022/4/9 CheckBox的选中状态改变后，还需要随之同步Schedule的完成状态到DB中
            //为CheckBox注册监听事件
            eventViewHolder.cbEventState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Log.d(TAG, "onCheckedChanged: isChecked=" + isChecked);

                    int position = (int) buttonView.getTag();
                    mCheckState.put(position, isChecked);

                    schedule.setFinish(isChecked);
                    mFragment.changeScheduleSate(schedule);  //刷新两个List(done & undo)的UI状态
                }
            });

            //为整个任务条设置点击事件，跳转至ScheduleDetailActivity
            eventViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: " + "[id=" + schedule.getId() + ", isFinish=" + schedule.isFinish() + ", title=" + schedule.getTitle() + "]");
                    Toast.makeText(mContext, "[id=" + schedule.getId() + ", isFinish=" + schedule.isFinish() + ", title=" + schedule.getTitle() + "]", Toast.LENGTH_SHORT).show();
                }
            });

            // TODO: 2022/4/9 注册长按的监听事件：弹出删除任务的对话框
            eventViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Log.d(TAG, "onLongClick: ing");
                    showDeleteScheduleDialog(schedule);
                    return false;
                }
            });
        }


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

    // TODO: 2022/4/9 添加&删除item之后还需要更新DB，放到adapter中完成
    /**
     * 添加schedule item
     * @param schedule
     */
    public void insertItem(Schedule schedule) {
        mSchedules.add(schedule);
        notifyItemInserted(mSchedules.size() - 1);
        notifyDataSetChanged();
    }

    /**
     * 删除schedule item
     * @param schedule
     */
    public void removeItem(Schedule schedule) {
        final int position = mSchedules.indexOf(schedule);
        if(position != -1) {
            mCheckState.delete(position);
            mSchedules.remove(position);
            notifyItemRemoved(position);
            notifyDataSetChanged();
            mFragment.resetVisibilityOfNoTaskView();
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
        mSchedules = schedules;
        notifyDataSetChanged();
    }

}
