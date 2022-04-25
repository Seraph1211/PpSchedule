package com.seraph.ppschedule.activity;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.seraph.ppschedule.R;
import com.seraph.ppschedule.dao.ConcentrationDataDao;
import com.seraph.ppschedule.dao.ScheduleDao;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PieChartView;

public class ChartsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ChartsActivity";

    //数字数据展示
    private TextView tvDoneCountOfWeek;
    private TextView tvUndoCountOfWeek;
    private TextView tvConcentratedTimeOfWeek;
    private TextView tvDoneCountOfMonth;
    private TextView tvUndoCountOfMonth;
    private TextView tvConcentratedTimeOfMonth;

    private int countOfDoneForWeek;  //本周已完成事件数
    private int countOfUndoForWeek;  //本周未完成事件数
    private long durationOfConcentrationForWeek;  //本周专注时长
    private int countOfDoneForMonth;  //本月已完成事件数
    private int countOfUndoForMonth;  //本月未完成事件数
    private long durationOfConcentrationForMonth;  //本月专注时长（单位：min)


    //饼状图
    private PieChartView pieChart;  //饼状图UI控件
    private Button btnShowWeekDataOfPieChart;  //切换周数据按钮
    private Button btnShowMonthDataOfPieChart;  //切换月数据按钮
    private List<SliceValue> sliceValues = new ArrayList<>();
    private String[] pieLabels = {"已完成事件", "未完成事件"};
    private int[] pieColors = {Color.parseColor("#43CD80"), Color.parseColor("#EE6363")};

    //折线图
    private LineChartView lineChart;  //折线图UI控件
    private Button btnShowWeekDataOfLineChart;  //切换周数据按钮
    private Button btnShowMonthDataOfLineChart;  //切换月数据按钮
    private String[] labelsOfWeek;  //X轴标签(周）
    private float[]  scoresOfWeek;  //Y轴数据（周）
    private String[] labelsOfMonth;  //X轴标签（月）
    private float[]  scoresOfMonth;  //Y轴数据（月）


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charts);

        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();

        initData();
        resetPieChart(countOfDoneForWeek, countOfUndoForWeek);
        resetLineChart(labelsOfWeek, scoresOfWeek);
    }

    private void initView() {
        tvDoneCountOfWeek = findViewById(R.id.tvCountOfDoneForWeek);
        tvUndoCountOfWeek = findViewById(R.id.tvCountOfUndoForWeek);
        tvConcentratedTimeOfWeek = findViewById(R.id.tvConcentratedTimeOfWeek);

        tvDoneCountOfMonth = findViewById(R.id.tvCountOfDoneForMonth);
        tvUndoCountOfMonth = findViewById(R.id.tvCountOfUndoForMonth);
        tvConcentratedTimeOfMonth = findViewById(R.id.tvConcentratedTimeOfMonth);

        pieChart = findViewById(R.id.pieChartOfDoneAndUndo);
        btnShowWeekDataOfPieChart = findViewById(R.id.btnPieChartWeekly);
        btnShowMonthDataOfPieChart = findViewById(R.id.btnPieChartMonthly);

        lineChart = findViewById(R.id.lineChartOfConcentrationTime);
        btnShowWeekDataOfLineChart = findViewById(R.id.btnLineChartWeekly);
        btnShowMonthDataOfLineChart = findViewById(R.id.btnLineChartMonthly);

        btnShowWeekDataOfPieChart.setOnClickListener(this);
        btnShowMonthDataOfPieChart.setOnClickListener(this);
        btnShowWeekDataOfLineChart.setOnClickListener(this);
        btnShowMonthDataOfLineChart.setOnClickListener(this);

        btnShowWeekDataOfPieChart.setTextColor(getResources().getColor(R.color.color_select_chart));
        btnShowWeekDataOfLineChart.setTextColor(getResources().getColor(R.color.color_select_chart));
        btnShowMonthDataOfPieChart.setTextColor(getResources().getColor(R.color.color_normal_chart));
        btnShowMonthDataOfLineChart.setTextColor(getResources().getColor(R.color.color_normal_chart));
    }

    /**
     * 活动启动时，需要初始化：
     * 本周已完成任务数、本周未完成任务数、本周专注总时长；
     * 本月已完成任务数、本月未完成任务数、本月专注总时长；
     * 本周迄今为止每一天的专注时长、本月迄今为止每一天的专注时长
     *
     * 该方法应该onStart()中调用
     */
    private void initData() {
        Calendar currentCalendar = Calendar.getInstance();

        countOfDoneForWeek = ScheduleDao
                .getInstance()
                .findScheduleOfOneWeekRelatedToState(currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.DAY_OF_MONTH),
                        true)
                .size();
        tvDoneCountOfWeek.setText(countOfDoneForWeek + "");  //更新TextView状态

        countOfUndoForWeek = ScheduleDao
                .getInstance()
                .findScheduleOfOneWeekRelatedToState(currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.DAY_OF_MONTH),
                        false)
                .size();
        tvUndoCountOfWeek.setText(countOfUndoForWeek + "");  //更新TextView状态

        durationOfConcentrationForWeek = ConcentrationDataDao
                .getInstance()
                .getDurationOfWeek(currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.DAY_OF_MONTH));
        durationOfConcentrationForWeek /= 60;  //s换算为min
        tvConcentratedTimeOfWeek.setText(durationOfConcentrationForWeek + "");  //更新TextView状态

        countOfDoneForMonth = ScheduleDao
                .getInstance()
                .findScheduleOfOneWeekRelatedToState(currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH), 1,
                        true)
                .size();
        tvDoneCountOfMonth.setText(countOfDoneForMonth + "");  //更新TextView状态

        countOfUndoForMonth = ScheduleDao
                .getInstance()
                .findScheduleOfOneWeekRelatedToState(currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH), 1,
                        false)
                .size();
        tvUndoCountOfMonth.setText(countOfUndoForMonth + "");  //更新TextView状态

        durationOfConcentrationForMonth = ConcentrationDataDao
                .getInstance()
                .getDurationOfMonth(currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH));
        durationOfConcentrationForMonth /= 60;  //s换算为min
        tvConcentratedTimeOfMonth.setText(durationOfConcentrationForMonth + "");  //更新TextView状态


        //初始化周数据
        int dayOfWeek = currentCalendar.get(Calendar.DAY_OF_WEEK);  //今天是本周第几天
        labelsOfWeek = new String[dayOfWeek];
        scoresOfWeek = new float[dayOfWeek];
        Calendar weekDate = Calendar.getInstance();
        for(int i=1; i <= dayOfWeek; i++) {
            weekDate.set(Calendar.DAY_OF_WEEK, i);
            labelsOfWeek[i-1] = weekDate.get(Calendar.MONTH) + "-" + weekDate.get(Calendar.DAY_OF_MONTH);

            long duration = ConcentrationDataDao
                    .getInstance()
                    .getDurationOfDate(weekDate.get(Calendar.YEAR), weekDate.get(Calendar.MONTH), weekDate.get(Calendar.DAY_OF_MONTH));

            scoresOfWeek[i-1] = duration != 0 ? duration : 0;
        }

        //初始化月数据
        labelsOfMonth = new String[currentCalendar.get(Calendar.DAY_OF_MONTH)];
        scoresOfMonth = new float[currentCalendar.get(Calendar.DAY_OF_MONTH)];
        for(int i=1; i <= dayOfWeek; i++) {
            labelsOfMonth[i-1] = currentCalendar.get(Calendar.MONTH) + "-" + i;
            long duration = ConcentrationDataDao
                    .getInstance()
                    .getDurationOfDate(currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH), i);
            scoresOfMonth[i-1] = duration != 0 ? duration : 0;
        }

    }

    //重置饼状图数据
    public void resetPieChart(float countOfDone, float countOfUndo){

        float[] pieValues = {countOfDone, countOfUndo};

        //计算百分比
        float sum = 0;
        for(int i=0; i<pieLabels.length; i++){
            sum += pieValues[i];
        }
        NumberFormat numberFormat=NumberFormat.getPercentInstance();
        numberFormat.setMinimumFractionDigits(1);

        //向sliceValues中填充数据
        for(int i=0; i<pieLabels.length; i++){
            String result=numberFormat.format(((double)pieValues[i])/((double)sum));
            SliceValue sliceValue = new SliceValue(pieValues[i], pieColors[i]); //新建对象的同时设置数值和颜色

            sliceValue.setLabel(pieLabels[i]+":"+result); //设置标签
            sliceValues.add(sliceValue);
        }

        PieChartData pieChartData = new PieChartData(sliceValues);
        pieChartData.setHasLabels(true); //显示标签，默认不显示
        pieChartData.setHasLabelsOutside(true); //标签的显示位置在饼状图之外
        pieChartData.setHasCenterCircle(true); //显示圆环状饼状图
        pieChartData.setCenterText1("Comparison"); //设置中心圆中的文本
        pieChartData.setCenterText2("专注度比较");
        pieChartData.setCenterText1FontSize(16);
        pieChartData.setCenterText1Color(Color.parseColor("#4876FF"));
        pieChartData.setCenterText2FontSize(12);
        pieChartData.setCenterCircleScale(0.5f);

        pieChart.setPieChartData(pieChartData);
        pieChart.setVisibility(View.VISIBLE);
        pieChart.startDataAnimation();
        pieChart.setCircleFillRatio(0.8f);   //设置饼状图占整个View的比例
        pieChart.setViewportCalculationEnabled(true);  //饼状图自动适应大小
        pieChart.setInteractive(false );
    }

    //重置折线图数据
    public void resetLineChart(String[] labels, float[] scores){

        List<PointValue> mPointValuesOfConcentrationTime = new ArrayList<>();
        List<AxisValue> mAxisXValuesOfConcentrationTime = new ArrayList<>();

        /*
        填充数据
         */
        for(int i=0; i<scores.length; i++){
            mPointValuesOfConcentrationTime.add(new PointValue(i, scores[i]));
        }
        for(int i=0; i<labels.length; i++){
            mAxisXValuesOfConcentrationTime.add(new AxisValue(i).setLabel(labels[i]));
        }

        /*
        对曲线进行相关设置
         */
        Line line = new Line(mPointValuesOfConcentrationTime)
                .setColor(Color.parseColor("#FF6347"))  //设置折线颜色为：浅红色
                .setShape(ValueShape.CIRCLE)  //设置折线统计图上每个数据点的形状（有三种：ValueShape.SQUARE/CIRCLE/DIAMOND)
                .setCubic(false)  //曲线是否平滑，即是曲线还是折线
                .setFilled(true)  //是否填充曲线以下的面积
                .setHasLabels(true)  //曲线的数据点是否加上标签/备注
                //.setHasLabelsOnlyForSelected(true)  //点击数据点是否提示数据（与setHasLabels(true)冲突）
                .setHasLines(true)  //是否用曲线显示。 false:只有点，没有曲线显示
                .setPointRadius(3)
                .setHasPoints(true); //是否显示圆点。 false:没有圆点只有点显示

        List<Line> lines = new ArrayList<>();
        lines.add(line);
        LineChartData lineChartData = new LineChartData();
        lineChartData.setLines(lines);

        /*
        对X轴进行相关设置
         */
        Axis axisX = new Axis()   //X轴
                .setHasTiltedLabels(false)  //X轴的标签是否倾斜，true-倾斜
                .setTextColor(Color.BLACK)  //字体颜色
                //.setName("Data")    //表格名称
                .setTextSize(10)   //字体大小
                .setMaxLabelChars(8)  //最多显示几个X轴坐标 ？
                .setValues(mAxisXValuesOfConcentrationTime) //X轴标签的填充
                .setHasLines(true);   //X轴是否有分割线
        lineChartData.setAxisXBottom(axisX); //X轴在底部（可以在上、下）

        /*
        对Y轴进行相关设置（Y轴是根据数据的大小自动设置Y轴的上限）
         */
        Axis axisY = new Axis()  //Y轴
                .setName("")  //Y轴标注
                .setTextSize(10); //字体大小
        lineChartData.setAxisYLeft(axisY); //Y轴在左边（可以在左、右）

        /*
        设置折线统计图的行为属性，支持缩放、滑动、平移
         */
        lineChart.setInteractive(true); //可交互
        lineChart.setZoomType(ZoomType.HORIZONTAL) ; //放大方式
        lineChart.setMaxZoom((float)2); //最大放大比例 ？
        lineChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        lineChart.setLineChartData(lineChartData);
        lineChart.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnPieChartWeekly:
                resetPieChart(countOfDoneForWeek, countOfUndoForWeek);
                btnShowWeekDataOfPieChart.setTextColor(getResources().getColor(R.color.color_select_chart));
                btnShowMonthDataOfPieChart.setTextColor(getResources().getColor(R.color.color_normal_chart));
                break;

            case R.id.btnPieChartMonthly:
                resetPieChart(countOfDoneForMonth, countOfUndoForMonth);
                btnShowMonthDataOfPieChart.setTextColor(getResources().getColor(R.color.color_select_chart));
                btnShowWeekDataOfPieChart.setTextColor(getResources().getColor(R.color.color_normal_chart));
                break;

            case R.id.btnLineChartWeekly:
                resetLineChart(labelsOfWeek, scoresOfWeek);
                btnShowWeekDataOfLineChart.setTextColor(getResources().getColor(R.color.color_select_chart));
                btnShowMonthDataOfLineChart.setTextColor(getResources().getColor(R.color.color_normal_chart));
                break;

            case R.id.btnLineChartMonthly:
                resetLineChart(labelsOfMonth, scoresOfMonth);
                btnShowMonthDataOfLineChart.setTextColor(getResources().getColor(R.color.color_select_chart));
                btnShowWeekDataOfLineChart.setTextColor(getResources().getColor(R.color.color_normal_chart));
                break;

            case R.id.btnBackOfCharts:
                finish();
                break;
        }

    }
}