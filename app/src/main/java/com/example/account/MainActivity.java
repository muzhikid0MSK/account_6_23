package com.example.account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.account.mapper.AccountMapper;
import com.example.account.mapper.AccountTypeMapper;
import com.example.account.mapper.ExpenditureTypeMapper;
import com.example.account.mapper.IncomeTypeMapper;
import com.example.account.mapper.InitMapper;
import com.example.account.mapper.RecordMapper;
import com.example.account.mapper.StatisticsMapper;
import com.example.account.mapper.UserMapper;

import com.example.account.pojo.dto.AccountDTO;
import com.example.account.pojo.dto.SearchRecordDTO;
import com.example.account.pojo.dto.SimpleMonthlyStatisticsDTO;
import com.example.account.pojo.dto.StatisticsDTO;
import com.example.account.pojo.entity.Account;
import com.example.account.pojo.entity.IncomeType;
import com.example.account.pojo.entity.Record;
import com.example.account.pojo.entity.User;
import com.example.account.util.DataBaseUtil;
import com.example.account.util.SnowFlakeUtil;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.util.List;

/**
 * @author 梅盛珂
 * @last_modified_time 2021年06月15日09:23:46
 * @description 主页面
 */

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener , View.OnClickListener {
    private BottomNavigationView bottomNavigationView;
    private DetailFragment detailFragment;
    private MeFragment meFragment;
    private PropertyFragment propertyFragment;
    private StatisticsFragment statisticsFragment;
    private Fragment[] fragments;

    private FloatingActionButton fabtnAddEntry;
    private int lastFragment ;

    private SQLiteDatabase database;
    private AccountMapper accountMapper;
    private AccountTypeMapper accountTypeMapper;
    private ExpenditureTypeMapper expenditureTypeMapper;
    private IncomeTypeMapper incomeTypeMapper;
    private RecordMapper recordMapper;
    private UserMapper userMapper;
    private StatisticsMapper statisticsMapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDataBase();
        initView();
        initListener();
    }

    private void initDataBase() {
        DataBaseUtil.initDB("TestDataBase", 1);
        InitMapper initMapper = new InitMapper(MainActivity.this);

        database = InitMapper.getDatabase();
        accountMapper = InitMapper.getAccountMapper();
        accountTypeMapper = InitMapper.getAccountTypeMapper();
        expenditureTypeMapper = InitMapper.getExpenditureTypeMapper();
        incomeTypeMapper = InitMapper.getIncomeTypeMapper();
        recordMapper = InitMapper.getRecordMapper();
        userMapper = InitMapper.getUserMapper();
        statisticsMapper = InitMapper.getStatisticsMapper();


        // test();
    }

    public void test(){
        User user = new User();
        user.setId(SnowFlakeUtil.getInstance().nextId());
        user.setUserName("邹皓杰");
        user.setPhoneNumber("18996345736");
        user.setPassword("123456");
        userMapper.insertUser(user);

        User user2 = new User();
        user2.setId(SnowFlakeUtil.getInstance().nextId());
        user2.setUserName("刘凯卫");
        user2.setPhoneNumber("13608382065");
        user2.setPassword("123456");
        userMapper.insertUser(user2);

        Account accountTwo = new Account();
        accountTwo.setId(SnowFlakeUtil.getInstance().nextId());
        accountTwo.setUserId(user.getId());
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

        // AccountMapper 测试
        List<AccountDTO> accountDTOList = accountMapper.getAccountByUserId(user.getId());

        // AccountTypeMapper 测试（非幂等 注意执行之后需要重建数据库）
//        accountTypeMapper.deleteAccountTypeById(0L);
//        List<AccountType> accountTypeList = accountTypeMapper.getAllAccountType();
//        accountTypeList.get(0).setName("应该是支付宝吧");
//        accountTypeList.get(0).setImageUrl("新的 URL");
//        accountTypeMapper.updateAccountTypeById(accountTypeList.get(0));


        // IncomeTypeMapper 测试
        List<IncomeType> incomeTypeList = incomeTypeMapper.getAllIncomeType();

        // RecordMapper 测试
        List<SearchRecordDTO> recordList = recordMapper.getRecordLikeTypeName(user.getId(), "饰");
        List<SearchRecordDTO> recordList2 = recordMapper.getRecordLikeAmount(user.getId(), 4.0);
        List<SearchRecordDTO> recordList3 = recordMapper.getRecordLikeTypeName(user2.getId(), "饰");
        List<SearchRecordDTO> recordList4 = recordMapper.getRecordLikeAmount(user2.getId(), 4.0);

        // StaticsMapper 测试
        SimpleMonthlyStatisticsDTO simpleMonthlyStatisticsDTO = statisticsMapper.getSimpleMonthlyStatistics(user.getId(), "2021", "06");
        StatisticsDTO statisticsDTO = statisticsMapper.getMonthlyExpenditureStatistics(user.getId(), "2021", "06");
        StatisticsDTO statisticsDTO1 = statisticsMapper.getMonthlyIncomeStatistics(user.getId(), "2021", "06");
        StatisticsDTO statisticsDTO2 = statisticsMapper.getYearlyExpenditureStatistics(user.getId(), "2021");
        StatisticsDTO statisticsDTO3 = statisticsMapper.getYearlyIncomeStatistics(user.getId(), "2021");
        StatisticsDTO statisticsDTO4 = statisticsMapper.getWeeklyExpenditureStatistics(user.getId(), "2021", "06", "14");
        StatisticsDTO statisticsDTO5 = statisticsMapper.getWeeklyIncomeStatistics(user.getId(), "2021", "06", "14");

        // UserMapper 测试
        User userT1 = userMapper.checkPassword("18996345736","123");
        User userT2 = userMapper.checkPassword("18996345736","123456");
        User userT3 = userMapper.checkPassword("13608382065","123456");
        User userT4 = userMapper.checkPassword("13608382078","123456");
    }


    private void initView(){
        bottomNavigationView = findViewById(R.id.bnv_menu);
        removeNavigationShiftMode(bottomNavigationView);
        detailFragment = new DetailFragment();
        meFragment = new MeFragment();
        propertyFragment = new PropertyFragment();
        statisticsFragment = new StatisticsFragment();
        fragments = new Fragment[]{detailFragment,meFragment,propertyFragment,statisticsFragment};
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_main,detailFragment).show(detailFragment).commit();
        fabtnAddEntry = findViewById(R.id.fabtn_add_entry);
    }

    private void initListener(){
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        fabtnAddEntry.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fabtn_add_entry:
                Intent intent = new Intent(getApplicationContext(),AddNewEntryActivity.class);
                startActivity(intent);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.mb_item_detail:
                if(lastFragment != 0){
                    switchFragment(lastFragment,0);
                    lastFragment = 0;
                }
                return true;
            case R.id.mb_item_statistics:
                if(lastFragment != 3){
                    switchFragment(lastFragment,3);
                    lastFragment = 3;
                }
                return true;
            case R.id.mb_item_property:
                if(lastFragment!=2){
                    switchFragment(lastFragment,2);
                    lastFragment = 2;
                }
                return true;
            case R.id.mb_item_me:
                if(lastFragment!=1){
                    switchFragment(lastFragment,1);
                    lastFragment = 1;
                }
                return true;
            default:
                return false;
        }
    }

    /**
     * 消除底部导航控件切换item时的动画
     * @param view 底部导航控件
     */
    @SuppressLint("RestrictedApi")
     void removeNavigationShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        menuView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        menuView.buildMenuView();
    }

    /**
     * 用于切换主界面的fragment
     * @param lastFragment 上一个fragment
     * @param index 切换fragment的索引
     */
    private void switchFragment(int lastFragment,int index)
    {
        FragmentTransaction transaction =getSupportFragmentManager().beginTransaction();
        transaction.hide(fragments[lastFragment]);//隐藏上个Fragment
        if(!fragments[index].isAdded())
        {
            transaction.add(R.id.fl_main,fragments[index]);
        }
        transaction.show(fragments[index]).commitAllowingStateLoss();
    }

}

