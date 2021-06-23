package com.example.account;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Objects;


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
    @Override
    public void onStart() {
        super.onStart();
        initData();
        initView();
        initListener();
    }

    private void initData() {
        userInfo = (UserInfo)getActivity().getApplication();
    }

    private void initListener() {
        linearLayout.setOnClickListener(this);
        tvQuit.setOnClickListener(this);
    }

    private void initView() {
        linearLayout = Objects.requireNonNull(getView()).findViewById(R.id.ll_login);
        tvIsLogin =getView().findViewById(R.id.tv_is_login);
        if(userInfo.getUser()!=null){   //已登录
            tvIsLogin.setText(userInfo.getUser().getUserName()+"已登录");
            linearLayout.setClickable(false);
        }else{
            tvIsLogin.setText("点我登录");
            linearLayout.setClickable(true);
        }
        ivAlert = getView().findViewById(R.id.iv_alert);
        etThreshold = getView().findViewById(R.id.et_threshold);
        tvAlertHint = getView().findViewById(R.id.tv_alert_hint);
        tvQuit = getView().findViewById(R.id.tv_quit);
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
        }
    }
}