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

public class EventSetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "EventSetAdapter";

    private Context mContext;
    private BaseFragment mFragment;
    private List<Schedule> mSchedules;

    private SparseBooleanArray mCheckState = new SparseBooleanArray();  //用于避免因RecyclerView复用item而导致item数据错乱

    private RecyclerView mRv;

    public EventSetAdapter(Context mContext, BaseFragment mFragment, List<Schedule> mSchedules, RecyclerView mRv) {
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
        return new EventViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_event, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int i) {
        final Schedule schedule = mSchedules.get(i);

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


        if(schedule.getYear() != 0 && schedule.getMonth() != 0 && schedule.getDay() != 0) {
            eventViewHolder.tvEventTime.setText(schedule.getYear() + "/" + (schedule.getMonth() + 1) + "/" + schedule.getDay());
        } else {
            eventViewHolder.tvEventTime.setText("");
        }

        //为CheckBox注册监听事件
        eventViewHolder.cbEventState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // 判断CheckBox是否被点击
                // setChecked()方法会回调setOnCheckedChanged()方法，而业务逻辑仅需要在用户点击CheckBox时执行
                if(!buttonView.isPressed()) {
                    return;
                }

                mCheckState.delete((int) buttonView.getTag());
                schedule.setFinish(isChecked);
                ScheduleDao.getInstance().updateSchedule(schedule);

                mFragment.resetScheduleList();
            }
        });

        //为整个任务条设置点击事件，跳转至ScheduleDetailActivity
        eventViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: " + "[id=" + schedule.getId() + ", isFinish=" + schedule.isFinish() + ", title=" + schedule.getTitle() + "]");
                mContext.startActivity(new Intent(mContext, ScheduleDetailActivity.class)
                        .putExtra(ScheduleDetailActivity.SCHEDULE_ID, schedule.getId())
                        .putExtra(ScheduleDetailActivity.CALENDAR_POSITION, -1)
                        .putExtra(ScheduleDetailActivity.TOOLBAR_TITLE, "收集箱"));
            }
        });


        eventViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
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
        //RecyclerView正在测绘或者正在滚动时，调用notify相关方法会报错
        while(!mRv.isComputingLayout() && mRv.getScrollState() == RecyclerView.SCROLL_STATE_IDLE) {
            mCheckState.put(mSchedules.size(), schedule.isFinish());
            mSchedules.add(schedule);
            ScheduleDao.getInstance().addSchedule(schedule);
            Log.d(TAG, "insertItem: mCheckState=" + mCheckState.toString());
            notifyItemInserted(mSchedules.size() - 1);
            notifyDataSetChanged();
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
        mSchedules = schedules;
        initCheckState();
        while(!mRv.isComputingLayout() && mRv.getScrollState() == RecyclerView.SCROLL_STATE_IDLE) {
            notifyDataSetChanged();
            break;
        }
    }

}

