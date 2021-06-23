package com.example.account;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.account.mapper.InitMapper;
import com.example.account.mapper.UserMapper;
import com.example.account.pojo.entity.User;
import com.example.account.util.SnowFlakeUtil;

/**
 * @author 梅盛珂
 * @since 2021年06月23日13:22:42
 * @description 注册
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView ivBack;
    EditText etPhoneNumber;
    EditText etPassword;
    EditText etUserName;
    Button btnRegister;

    UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initData();
        initView();
        initListener();

    }

    private void initData() {
        userInfo = (UserInfo)getApplication();
//        initMapper = new InitMapper(getApplicationContext());

    }

    private void initListener() {
        btnRegister.setOnClickListener(this);
        ivBack.setOnClickListener(this);
    }

    private void initView() {
        btnRegister = findViewById(R.id.btn_register);
        ivBack = findViewById(R.id.iv_back);
        etPhoneNumber = findViewById(R.id.et_phone_number);
        etPassword = findViewById(R.id.et_password);
        etUserName = findViewById(R.id.et_user_name);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_register:
                User user = new User();
                user.setId(SnowFlakeUtil.getInstance().nextId());
                String userName = etUserName.getText().toString();
                String password = etPassword.getText().toString();
                String phoneNumber = etPhoneNumber.getText().toString();
                user.setUserName(userName);
                user.setPassword(password);
                user.setPhoneNumber(phoneNumber);
                InitMapper.getUserMapper().insertUser(user);
                userInfo.setUser(user);
                Toast.makeText(getApplicationContext(),"注册成功！已自动登录",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                break;
        }
    }
}