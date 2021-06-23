package com.example.account;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.UserManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.account.mapper.AccountMapper;
import com.example.account.mapper.AccountTypeMapper;
import com.example.account.mapper.ExpenditureTypeMapper;
import com.example.account.mapper.IncomeTypeMapper;
import com.example.account.mapper.InitMapper;
import com.example.account.mapper.RecordMapper;
import com.example.account.mapper.StatisticsMapper;
import com.example.account.mapper.UserMapper;
import com.example.account.pojo.entity.User;
import com.example.account.util.DataBaseUtil;

/**
 * @author 梅盛珂
 * @since 2021年06月23日13:22:10
 * @description 登录
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    TextView tvRegister;
    Button btnLogin;
    EditText etPhoneNumber;
    EditText etPassword;
    UserInfo userInfo;
    ImageView imgBack;
    InitMapper initMapper;

    private SQLiteDatabase database;
    private AccountMapper accountMapper;
    private AccountTypeMapper accountTypeMapper;
    private ExpenditureTypeMapper expenditureTypeMapper;
    private IncomeTypeMapper incomeTypeMapper;
    private RecordMapper recordMapper;
    private UserMapper userMapper;
    private StatisticsMapper statisticsMapper;
    //    private boolean stateLogin = false;
    private void initData(){
        userInfo = (UserInfo)getApplication();
    }
    private void initView(){
        imgBack = findViewById(R.id.iv_back);
        tvRegister = findViewById(R.id.tv_register);
        btnLogin = findViewById(R.id.btn_login);
        etPassword = findViewById(R.id.et_password);
        etPhoneNumber = findViewById(R.id.et_phone_number);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initData();
        initView();
        initListener();
    }

    private void initListener() {
        tvRegister.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        imgBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_login:
                String phoneNumber = etPhoneNumber.getText().toString();
                String password = etPassword.getText().toString();
                Log.d("LoginActivity","insert user before");
                User user = InitMapper.getUserMapper().checkPassword(phoneNumber,password);
                Log.d("LoginActivity","insert user after");
                if(user != null){
                    Log.d("LoginActivity","insert user successfully");
                    userInfo.setUser(user);
                    Log.d("LoginActivity","set user successfully");
                    Toast.makeText(getApplicationContext(),"登录成功",Toast.LENGTH_LONG).show();
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),"账号或密码错误",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.tv_register:
                Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }
}