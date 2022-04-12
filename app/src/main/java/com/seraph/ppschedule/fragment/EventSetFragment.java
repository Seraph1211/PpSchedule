package com.seraph.ppschedule.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.seraph.ppschedule.R;
import com.seraph.ppschedule.adapter.EventSetAdapter;
import com.seraph.ppschedule.adapter.ScheduleAdapter;
import com.seraph.ppschedule.bean.Schedule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EventSetFragment extends BaseFragment {

    private static final String TAG = "EventSetFragment";

    private Activity mActivity;
    private View mView;

    private RelativeLayout rlNoTask;  //无任务时的兜底View

    //用于展示未完成list的相关控件与数据
    private List<Schedule> undoList = new ArrayList<>();  //未完成的schedule列表
    private EventSetAdapter adapterUndo;  //未完成schedule列表的适配器
    private RelativeLayout rlUndo;  //用于展示未完成list的LinearLayout
    private RecyclerView rvUndo;  //用于展示未完成list的RecyclerView

    //用于展示已完成list的相关控件与数据
    private List<Schedule> doneList = new ArrayList<>();  //已完成的schedule列表
    private EventSetAdapter adapterDone;  //已完成schedule列表的适配器
    private LinearLayout llDone;  //用于展示已完成list的LinearLayout
    private RecyclerView rvDone;  //用于展示已完成list的RecyclerView


    public static EventSetFragment getInstance() {
        return new EventSetFragment();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mActivity = getActivity();
        mView = inflater.inflate(R.layout.fragment_eventset, container, false);

        initView();
        loadScheduleList();
        initRecyclerView();

        return mView;
    }

    private void initView() {
        rlNoTask = mView.findViewById(R.id.rlEventFragNoTask);
        rlNoTask.setVisibility(View.VISIBLE);

        rlUndo = mView.findViewById(R.id.rlEventSetOfUndo);
        rvUndo = mView.findViewById(R.id.rvListUndo);

        llDone = mView.findViewById(R.id.llEventSetOfDone);
        rvDone = mView.findViewById(R.id.rvListDone);
    }

    private void initRecyclerView() {
        DefaultItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setSupportsChangeAnimations(false);

        LinearLayoutManager managerUndo = new LinearLayoutManager(mActivity);
        managerUndo.setOrientation(LinearLayoutManager.VERTICAL);
        managerUndo.setItemPrefetchEnabled(false);
        adapterUndo = new EventSetAdapter(mActivity, this, undoList, rvUndo);
        rvUndo.setLayoutManager(managerUndo);
        rvUndo.setItemAnimator(itemAnimator);
        rvUndo.setAdapter(adapterUndo);

        LinearLayoutManager managerDone = new LinearLayoutManager(mActivity);
        managerDone.setOrientation(LinearLayoutManager.VERTICAL);
        managerDone.setItemPrefetchEnabled(false);
        adapterDone = new EventSetAdapter(mActivity, this, doneList, rvDone);
        rvDone.setLayoutManager(managerDone);
        rvDone.setItemAnimator(itemAnimator);
        rvDone.setAdapter(adapterDone);
    }

    private void loadScheduleList() {
        loadUndoList();
        loadDoneList();

        //设置兜底View的可见性
        resetVisibilityOfNoTaskView();
    }

    private void loadUndoList() {
        Log.d(TAG, "loadUndoList: ");

        for(int i = 0; i < 5; i++) {
            undoList.add(new Schedule("Title" + (i+1), "", Calendar.getInstance(), false));
        }
    }

    private void loadDoneList() {
        Log.d(TAG, "loadDoneList: ");
        for(int i = 0; i < 10; i++) {
            doneList.add(new Schedule("Title" + (i+1), "", Calendar.getInstance(), true));
        }
    }

    @Override
    public void resetVisibilityOfNoTaskView() {
        Log.d(TAG, "resetVisibilityOfNoTaskView: ");
        //设置展示控件的可见性
        if(undoList.size() > 0) {
            rlUndo.setVisibility(View.VISIBLE);
        } else {
            rlUndo.setVisibility(View.GONE);
        }

        if(doneList.size() > 0) {
            llDone.setVisibility(View.VISIBLE);
        } else {
            llDone.setVisibility(View.GONE);
        }
        
        rlNoTask.setVisibility((undoList.size() > 0 || doneList.size() > 0) ? View.GONE : View.VISIBLE);
    }

    @Override
    public void changeScheduleSate(Schedule schedule) {
        Log.d(TAG, "changeScheduleSate: schedule.isFinish=" + schedule.isFinish());

        if(schedule.isFinish()) {  //如果已完成，则将schedule从undoList中移除并添加到doneList中
            //adapterUndo.removeItem(schedule);
            adapterDone.insertItem(schedule);
        } else {
            //adapterDone.removeItem(schedule);
            adapterUndo.insertItem(schedule);
        }

    }
}
