package com.example.account;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.account.mapper.InitMapper;
import com.example.account.mapper.StatisticsMapper;
import com.example.account.pojo.dto.StatisticsDTO;
import com.example.account.util.NumericUtil;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author 梅盛珂
 * @last_modified_time
 * @description 我的
 */
public class MeFragment extends Fragment implements View.OnClickListener {
    UserInfo userInfo;
    LinearLayout linearLayout;
    ImageView ivAlert;
    EditText etThreshold;
    TextView tvAlertHint;
    TextView tvIsLogin;
    TextView tvQuit;
    Button btnConfirm;
    private double threshold;
    private StatisticsMapper statisticsMapper;
    @Override
    public void onStart() {
        super.onStart();
        initDataBase();
        initData();
        initView();
        initListener();
    }

    private void initDataBase() {
        statisticsMapper = InitMapper.getStatisticsMapper();
    }

    private void initData() {
        userInfo = (UserInfo)getActivity().getApplication();
    }

    private void initListener() {
        linearLayout.setOnClickListener(this);
        tvQuit.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
    }

    private void initView() {
        linearLayout = Objects.requireNonNull(getView()).findViewById(R.id.ll_login);
        tvIsLogin =getView().findViewById(R.id.tv_is_login);
        btnConfirm = getView().findViewById(R.id.btn_confirm);
        etThreshold = getView().findViewById(R.id.et_threshold);
        ivAlert = getView().findViewById(R.id.iv_alert);
        tvAlertHint = getView().findViewById(R.id.tv_alert_hint);
        tvQuit = getView().findViewById(R.id.tv_quit);
        if(userInfo.getUser()!=null){   //已登录
            tvIsLogin.setText(userInfo.getUser().getUserName()+"已登录");
            linearLayout.setClickable(false);
        }else{
            tvIsLogin.setText("点我登录");
            linearLayout.setClickable(true);
            etThreshold.setText("");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_me, container, false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_login:
                Intent intent = new Intent(getActivity().getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_quit:
                userInfo.setUser(null);
                initView();
                break;
            case R.id.btn_confirm:
                if(etThreshold.getText().toString()!=null){
                    if(NumericUtil.isNumeric(etThreshold.getText().toString())){
                        userInfo.getUser().setThreshold(Double.parseDouble(etThreshold.getText().toString()));
                        Toast.makeText(getActivity().getApplicationContext(),"设置成功",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getActivity().getApplicationContext(),"请输入正确数字",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getActivity().getApplicationContext(),"不能为空",Toast.LENGTH_LONG).show();
                }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            if(userInfo.getUser()!=null){
                if(userInfo.getUser().getThreshold()!=null){
                    threshold = userInfo.getUser().getThreshold();
                    Calendar calendar = Calendar.getInstance();
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH)+1; //第一个月从0开始，所以得到月份＋1
                    String monthStr;
                    if(month<10){
                        monthStr = "0"+month;
                    }else{
                        monthStr = month+"";
                    }
                    int day = calendar.get(calendar.DAY_OF_MONTH);
                    StatisticsDTO statisticsDTO =
                            statisticsMapper.getMonthlyExpenditureStatistics(userInfo.getUser().getId(),Integer.toString(year),monthStr);
                    double curCost = statisticsDTO.getTotalAmount();
                    if(curCost<=threshold){
                        tvAlertHint.setText("您当前消费尚在计划之内");
                        tvAlertHint.setTextColor(Color.parseColor("#9ACD32"));
                        ivAlert.setImageResource(R.drawable.alert_off);
                    }else{
                        tvAlertHint.setText("您当前消费超出阈值");
                        tvAlertHint.setTextColor(Color.RED);
                        ivAlert.setImageResource(R.drawable.alert_on);
                    }
                }
            }


        }

    }
}