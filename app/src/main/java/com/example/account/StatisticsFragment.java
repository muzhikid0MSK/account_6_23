package com.example.account;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.account.mapper.AccountMapper;
import com.example.account.mapper.InitMapper;
import com.example.account.mapper.RecordMapper;
import com.example.account.mapper.StatisticsMapper;
import com.example.account.mapper.UserMapper;
import com.example.account.pojo.dto.StatisticsDTO;
import com.example.account.pojo.dto.StatisticsDetailDTO;
import com.example.account.pojo.entity.Account;
import com.example.account.pojo.entity.Record;
import com.example.account.pojo.entity.User;
import com.example.account.util.PieChartUtil;
import com.example.account.util.SnowFlakeUtil;
import com.github.mikephil.charting.charts.PieChart;


import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * @author 梅盛珂
 * @last_modified_time 2021年06月17日14:22:14
 * @description 统计
 */
public class StatisticsFragment extends Fragment implements View.OnClickListener {
    LineChartView trendChart;
    PieChart proportionChart;

    private List<PointValue> mPointValues = new ArrayList<PointValue>();
    private List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();
    List<String>dates;
    List<Double> weeklyData;
    List<Double> yearlyData;
    List<Double> monthlyData;
    List<StatisticsDetailDTO>proportionData;
    private StatisticsMapper statisticsMapper;

    private SQLiteDatabase database;
    private UserMapper userMapper;
    private RecordMapper recordMapper;
    private AccountMapper accountMapper;

    private User testUser;

    private TextView tvWeek;
    private TextView tvMonth;
    private TextView tvYear;
    private TextView tvSwitch;
    private TextView tvTotalHint;
    private TextView tvTotalData;
    private TextView tvAverageHint;
    private TextView tvAverageData;
    private TextView tvTrendHint;
    private TextView tvProportionHint;
    private int currState = 0;

    private double totalWeeklyData = 0;
    private double totalMonthlyData = 0;
    private double totalYearlyData = 0;

    /**
     * 绘制
     */
    private void drawTrend(List<Double>data,int flag){

        getAxisXLables();//获取x轴的标注
        getAxisPoints(data);//获取坐标点
        initLineChart(flag);//初始化

    }
    /**
     * 设置X 轴的显示
     */
    private void getAxisXLables() {
        mAxisXValues = new ArrayList<>();
            for (int i = 0; i < dates.size(); i++) {
                mAxisXValues.add(new AxisValue(i).setLabel(dates.get(i)));
            }
    }
    /**
     * 图表的每个点的显示
     */
    private void getAxisPoints(List<Double>data) {
        mPointValues = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            mPointValues.add(new PointValue(i, data.get(i).floatValue()));
        }
    }

    private void initLineChart(int flag) {
        Line line = new Line(mPointValues).setColor(Color.parseColor("#CDCDC1"));
        List<Line> lines = new ArrayList<Line>();
        line.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）

        line.setCubic(false);//曲线是否平滑，即是曲线还是折线
        line.setFilled(true);//是否填充曲线的面积
        line.setHasLabels(true);//曲线的数据坐标是否加上备注
//      line.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
        line.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
        line.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）
        lines.add(line);
        LineChartData data = new LineChartData();
        data.setLines(lines);

        //坐标轴
        Axis axisX = new Axis(); //X轴
        axisX.setHasTiltedLabels(false);  //X坐标轴字体是斜的显示还是直的，true是斜的显示
        axisX.setTextColor(Color.BLACK);  //设置字体颜色
        //axisX.setName("date");  //表格名称
        axisX.setTextSize(10);//设置字体大小
        axisX.setMaxLabelChars(8); //最多几个X轴坐标，意思就是你的缩放让X轴上数据的个数7<=x<=mAxisXValues.length
        axisX.setValues(mAxisXValues);  //填充X轴的坐标名称
        data.setAxisXBottom(axisX); //x 轴在底部
        //data.setAxisXTop(axisX);  //x 轴在顶部
        axisX.setHasLines(true); //x 轴分割线

        // Y轴是根据数据的大小自动设置Y轴上限(在下面我会给出固定Y轴数据个数的解决方案)
//        Axis axisY = new Axis();  //Y轴
//        axisY.setName("");//y轴标注
//        axisY.setTextSize(10);//设置字体大小
//        data.setAxisYLeft(axisY);  //Y轴设置在左边
        //data.setAxisYRight(axisY);  //y轴设置在右边


        //设置行为属性，支持缩放、滑动以及平移

        trendChart.setInteractive(true);
        trendChart.setZoomType(ZoomType.HORIZONTAL);
        trendChart.setMaxZoom((float) 2);//最大方法比例
        trendChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        trendChart.setLineChartData(data);
        trendChart.setVisibility(View.VISIBLE);
        Viewport v = new Viewport(trendChart.getMaximumViewport());
        v.left = 0;

        trendChart.setCurrentViewport(v);
    }

    private void initData(int c){
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
        Calendar calendar = Calendar.getInstance();
        switch (c){
            case 0: // 周
                while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
                    calendar.add(Calendar.DAY_OF_WEEK, -1);
                }
                dates = new ArrayList<>();
                for (int i = -7; i < 0; i++) {
                    dates.add(dateFormat.format(calendar.getTime()));
                    calendar.add(Calendar.DATE, -1);
                }
                Collections.reverse(dates);
                //TODO 从数据库中获取的日期这里是写死的
                StatisticsDTO statisticsDTO;
                if (currState == 0) {
                    statisticsDTO =
                            statisticsMapper.getWeeklyExpenditureStatistics(testUser.getId(),"2021","06","14");

                }else{
                    statisticsDTO =
                            statisticsMapper.getWeeklyIncomeStatistics(testUser.getId(),"2021","06","14");

                }
                weeklyData = new ArrayList<>();
                weeklyData = statisticsDTO.getDetailAmount();
                totalWeeklyData = statisticsDTO.getTotalAmount();
                proportionData = statisticsDTO.getDisplayDetailDTOList();
                break;
            case 1: //月
                dates = new ArrayList<>();
                getDayByMonth(2021,6);
                //TODO 从数据库中获取的日期这里是写死的
                StatisticsDTO statisticsDTO1;
                if(currState == 0){
                    statisticsDTO1 = statisticsMapper.getMonthlyExpenditureStatistics(testUser.getId(),"2021","06");
                }else{
                    statisticsDTO1 = statisticsMapper.getMonthlyIncomeStatistics(testUser.getId(),"2021","06");
                }
                monthlyData = new ArrayList<>();
                monthlyData = statisticsDTO1.getDetailAmount();
                totalMonthlyData = statisticsDTO1.getTotalAmount();
                proportionData = statisticsDTO1.getDisplayDetailDTOList();
                break;
            case 2: //年
                dates = new ArrayList<>();
                genMonths(2021);
                //TODO 从数据库中获取的日期这里是写死的
                StatisticsDTO statisticsDTO2;
                if(currState == 0){
                    statisticsDTO2 = statisticsMapper.getYearlyExpenditureStatistics(testUser.getId(),"2021");
                }else{
                    statisticsDTO2 = statisticsMapper.getYearlyIncomeStatistics(testUser.getId(),"2021");
                }
                yearlyData = new ArrayList<>();
                yearlyData = statisticsDTO2.getDetailAmount();
                totalYearlyData = statisticsDTO2.getTotalAmount();
                proportionData = statisticsDTO2.getDisplayDetailDTOList();
                break;
        }


    }

    /**
     * 绘制提示语 和总支出/收入，日均数据
     * @param flag
     */
    private void drawHintAndTotalAndAverage(int flag){
        DecimalFormat df   = new DecimalFormat("######0.00");
        switch (flag){
            case 0:
                if(currState == 0){
                    tvTotalHint.setText("总支出：");
                    tvTrendHint.setText("支出走势");
                    tvProportionHint.setText("支出占比：");
                }else {
                    tvTotalHint.setText("总收入：");
                    tvTrendHint.setText("收入走势");
                    tvProportionHint.setText("收入占比：");
                }
                tvAverageHint.setText("日均");
                tvTotalData.setText(df.format(totalWeeklyData)+"");
                tvAverageData.setText(df.format(totalWeeklyData/7.0)+"");
                break;
            case 1:
                if(currState == 0){
                    tvTotalHint.setText("总支出：");
                    tvTrendHint.setText("支出走势");
                    tvProportionHint.setText("支出占比：");
                }else {
                    tvTotalHint.setText("总收入：");
                    tvTrendHint.setText("收入走势");
                    tvProportionHint.setText("收入占比：");
                }
                tvAverageHint.setText("日均");
                tvTotalData.setText(df.format(totalMonthlyData)+"");
                tvAverageData.setText(df.format(totalMonthlyData/30.0)+"");
                break;
            case 2:
                if(currState == 0){
                    tvTotalHint.setText("总支出：");
                    tvTrendHint.setText("支出走势");
                    tvProportionHint.setText("支出占比：");
                }else {
                    tvTotalHint.setText("总收入：");
                    tvTrendHint.setText("收入走势");
                    tvProportionHint.setText("收入占比：");
                }
                tvAverageHint.setText("月均");
                tvTotalData.setText(df.format(totalYearlyData)+"");
                tvAverageData.setText(df.format(totalYearlyData/12.0)+"");
                break;

        }
    }
    private void genMonths(int year) {
        String aDate = year+"-";
        for(int i =Calendar.JANUARY;i<=Calendar.DECEMBER;i++){
            dates.add(aDate+(i+1));
        }

    }

    public void getDayByMonth(int yearParam,int monthParam){
        Calendar aCalendar = Calendar.getInstance(Locale.CHINA);
        aCalendar.set(yearParam,monthParam,1);
        int year = aCalendar.get(Calendar.YEAR);//年份
        int month = aCalendar.get(Calendar.MONTH) + 1;//月份
        int day = aCalendar.getActualMaximum(Calendar.DATE);
        for (int i = 1; i <= day; i++) {
            String aDate=null;
            if(month<10&&i<10){
                aDate = "0"+month+"-0"+i;
            }
            if(month<10&&i>=10){
                aDate = "0"+month+"-"+i;
            }
            if(month>=10&&i<10){
                aDate = ""+month+"-0"+i;
            }
            if(month>=10&&i>=10){
                aDate = ""+month+"-"+i;
            }
            dates.add(aDate);
        }

    }

    private void initView(){
        trendChart = Objects.requireNonNull(getView()).findViewById(R.id.chart_trend);
        proportionChart = getView().findViewById(R.id.chart_proportion);
        tvWeek = getView().findViewById(R.id.tv_week);
        tvMonth = getView().findViewById(R.id.tv_month);
        tvYear = getView().findViewById(R.id.tv_year);
        tvSwitch = getView().findViewById(R.id.tv_main_in_stats);
        tvTotalData = getView().findViewById(R.id.tv_total_data);
        tvTotalHint = getView().findViewById(R.id.tv_total_hint);
        tvAverageData = getView().findViewById(R.id.tv_average_data);
        tvAverageHint = getView().findViewById(R.id.tv_average_hint);
        tvTrendHint = getView().findViewById(R.id.tv_total_trend_hint);
        tvProportionHint = getView().findViewById(R.id.tv_total_proportion_hint);

        drawTrend(weeklyData,0);
        drawProportion(0);
        drawHintAndTotalAndAverage(0);
    }

    private void drawProportion(int flag) {
        HashMap<String,Double>dataMap = new HashMap<>();
        for(int i =0;i<proportionData.size();i++){
           dataMap.put(proportionData.get(i).getName(),proportionData.get(i).getAmount());
        }
        PieChartUtil.getPitChart().setPieChart(proportionChart,dataMap,"",true);
    }

    private void initDatabase(){
        database = InitMapper.getDatabase();
        statisticsMapper = InitMapper.getStatisticsMapper();
        userMapper = InitMapper.getUserMapper();
        recordMapper = InitMapper.getRecordMapper();
        accountMapper = InitMapper.getAccountMapper();
        statisticsMapper = InitMapper.getStatisticsMapper();
        insertRecords();

    }

    private void insertRecords() {
//        User user = new User();
//        user.setId(SnowFlakeUtil.getInstance().nextId());
//        user.setUserName("邹皓杰");
//        user.setPhoneNumber("18996345736");
//        user.setPassword("123456");
//        userMapper.insertUser(user);
//
//        User user2 = new User();
//        user2.setId(SnowFlakeUtil.getInstance().nextId());
//        user2.setUserName("刘凯卫");
//        user2.setPhoneNumber("13608382065");
//        user2.setPassword("123456");
//        userMapper.insertUser(user2);

//        testUser = new User();
//        testUser.setId(SnowFlakeUtil.getInstance().nextId());
//        testUser.setUserName("梅盛珂");
//        testUser.setPhoneNumber("15187091901");
//        testUser.setPassword("qwert");
//        userMapper.insertUser(testUser);


        testUser = InitMapper.getUserMapper().checkPassword("15187091901","qwert");

        Account accountTwo = new Account();
        accountTwo.setId(SnowFlakeUtil.getInstance().nextId());
        accountTwo.setUserId(testUser.getId());
        accountTwo.setAccountTypeId(1L);
        accountTwo.setName("我的支付宝");
        accountTwo.setRemove(false);
        accountMapper.insertAccount(accountTwo, 2.0);

        Record record1 = new Record();
        record1.setId(SnowFlakeUtil.getInstance().nextId());
        record1.setAccountId(accountTwo.getId());
        record1.setExpenditureTypeId(null);
        record1.setIncomeTypeId(0L);
        record1.setAmount(52.5);
        record1.setRemark(null);
        record1.setTime("2021-06-13 23:39:34");
        recordMapper.insertRecord(record1);

        Record record2 = new Record();
        record2.setId(SnowFlakeUtil.getInstance().nextId());
        record2.setAccountId(accountTwo.getId());
        record2.setExpenditureTypeId(1L);
        record2.setIncomeTypeId(null);
        record2.setAmount(-20.0);
        record2.setRemark(null);
        record2.setTime("2021-06-08 23:46:11");
        recordMapper.insertRecord(record2);

        Record record3 = new Record();
        record3.setId(SnowFlakeUtil.getInstance().nextId());
        record3.setAccountId(accountTwo.getId());
        record3.setExpenditureTypeId(null);
        record3.setIncomeTypeId(2L);
        record3.setAmount(4.0);
        record3.setRemark(null);
        record3.setTime("2021-06-13 23:46:22");
        recordMapper.insertRecord(record3);


        Record record4 = new Record();
        record4.setId(SnowFlakeUtil.getInstance().nextId());
        record4.setAccountId(accountTwo.getId());
        record4.setExpenditureTypeId(0L);
        record4.setIncomeTypeId(null);
        record4.setAmount(-3.0);
        record4.setRemark(null);
        record4.setTime("2021-06-13 23:46:29");
        recordMapper.insertRecord(record4);


        Record record5 = new Record();
        record5.setId(SnowFlakeUtil.getInstance().nextId());
        record5.setAccountId(accountTwo.getId());
        record5.setExpenditureTypeId(2L);
        record5.setIncomeTypeId(null);
        record5.setAmount(-7.5);
        record5.setRemark(null);
        record5.setTime("2021-06-13 23:26:12");
        recordMapper.insertRecord(record5);


        Record record6 = new Record();
        record6.setId(SnowFlakeUtil.getInstance().nextId());
        record6.setAccountId(accountTwo.getId());
        record6.setExpenditureTypeId(2L);
        record6.setIncomeTypeId(null);
        record6.setAmount(-4.4);
        record6.setRemark(null);
        record6.setTime("2021-06-14 00:29:16");
        recordMapper.insertRecord(record6);


    }

    @Override
    public void onStart() {
        super.onStart();
        initDatabase();
        initData(0);
        initView();
        initListener();
    }

    private void initListener() {
        tvWeek.setOnClickListener(this);
        tvYear.setOnClickListener(this);
        tvMonth.setOnClickListener(this);
        tvSwitch.setOnClickListener(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_week:
                drawMain(0);
                break;
            case R.id.tv_month:
                drawMain(1);
                break;
            case R.id.tv_year:
                drawMain(2);
                break;
            case R.id.tv_main_in_stats:
                if(currState == 1){
                    currState = 0;
                    tvSwitch.setText("支出");
                }else{
                    currState = 1;
                    tvSwitch.setText("收入");
                }
                drawMain(0);
                break;
        }
    }

    /**
     * 主要绘图
     * @param flag
     */
    private void drawMain(int flag) {
        initData(flag);
        drawHintAndTotalAndAverage(flag);
        switch (flag){
            case 0:drawTrend(weeklyData,flag);break;
            case 1:drawTrend(monthlyData,flag);break;
            case 2:drawTrend(yearlyData,flag);break;
        }
        drawProportion(flag);
    }
}