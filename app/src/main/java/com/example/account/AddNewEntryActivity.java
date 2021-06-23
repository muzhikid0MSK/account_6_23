package com.example.account;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.example.account.pojo.dto.AccountDTO;
import com.example.account.pojo.entity.Account;
import com.example.account.pojo.entity.Record;
import com.example.account.util.DataBaseUtil;
import com.example.account.util.SnowFlakeUtil;

import java.util.Calendar;
import java.util.Locale;

/**
 * @author
 * @last_modified_time
 * @description 添加新记录
 */
public class AddNewEntryActivity extends AppCompatActivity implements View.OnClickListener {

    int Year;
    int Month;
    int Day;
    String pictureSelect;
    ImageView ivZDining;//iv，Z：支出，S,收入；
    ImageView ivZClothing;
    ImageView ivZDailyuse;
    ImageView ivZVegetables;
    ImageView ivZTravel;
    ImageView ivZEntertainment;
    ImageView ivZDigital;
    ImageView ivZOther;
    ImageView ivSSalary;
    ImageView ivSParttime;
    ImageView ivSFinancial;
    ImageView ivSOther;
    double account;
    int point;
    String straccount;
    Button btNumber0;
    Button btNumber1;
    Button btNumber2;
    Button btNumber3;
    Button btNumber4;
    Button btNumber5;
    Button btNumber6;
    Button btNumber7;
    Button btNumber8;
    Button btNumber9;
    Button btNumPoint;
    Button btDelete;
    Button btFinish;
    Button btData;
    Button btAccount;
    TextView tvAccount;
    String selectaccount;

    Calendar calendar;
    private AlertDialog alertDialog;
    ///////////
    private SQLiteDatabase database;
    private AccountMapper accountMapper;
    private AccountTypeMapper accountTypeMapper;
    private ExpenditureTypeMapper expenditureTypeMapper;
    private IncomeTypeMapper incomeTypeMapper;
    private RecordMapper recordMapper;
    private UserMapper userMapper;
    private StatisticsMapper statisticsMapper;
    UserInfo userInfo;

    Long accountId;
    Long incometypeid;
    Long expendituretypeid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_entry);
        userInfo = (UserInfo)getApplication();
        initifuser();
        initdata();
        initview();
        initListener();
        DataBaseUtil.initDB("TestDataBase", 1);
        InitMapper initMapper = new InitMapper(AddNewEntryActivity.this);


        //完成按钮，Toast全部信息
        btFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                account=Double.parseDouble(straccount);
                if (account==(double)0||accountId==null){
                    Toast.makeText(AddNewEntryActivity.this,"请输入金额或选择账户",Toast.LENGTH_SHORT).show();
                } else{
//                    userInfo = (UserInfo)getApplication();
//                    userInfo.getUser().getId();
//                    Account account = new Account();
//                    account.setId(SnowFlakeUtil.getInstance().nextId());
//                    account.setUserId(userInfo.getUser().getId());
//                    account.setAccountTypeId(1L);
//                    account.setName("我的支付宝");
//                    account.setRemove(false);
//                    accountMapper.insertAccount(account, 2.0);
//                    if (incometypeid==null){
//                        account=0-account;
//                    }
                    Calendar cal=Calendar.getInstance();
                    int hour  =cal.get(Calendar.HOUR_OF_DAY);  //小时
                    int minute=cal.get(Calendar.MINUTE);   //分
                    int second=cal.get(Calendar.SECOND);  //秒
                    insertOneRecord(SnowFlakeUtil.getInstance().nextId(),accountId,expendituretypeid,incometypeid,
                            account,null,Year+"-"+Month+"-"+Day+" "+hour+":"+minute+":"+second);


                    Toast.makeText(AddNewEntryActivity.this,pictureSelect+selectaccount+
                            ",金额:"+account+"日期："+Year+Month+Day+"",Toast.LENGTH_SHORT).show();
                }

            }
        });



        btAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] items = {"默认账户", "微信", "支付宝"};
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(AddNewEntryActivity.this);
                alertBuilder.setTitle("请选择你的账户：");
                alertBuilder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Toast.makeText(AddNewEntryActivity.this, items[which], Toast.LENGTH_SHORT).show();
                        btAccount.setText(items[which]);
                        selectaccount=items[which];
                        //TODO which现在只能选默认账户
                        AccountDTO accountDTO = InitMapper.getAccountMapper().getAccountByUserId(userInfo.getUser().getId()).get(which);
                        accountId = (long)accountDTO.getId();
                        alertDialog.dismiss();
                        Toast.makeText(AddNewEntryActivity.this,accountId+"----"+which,Toast.LENGTH_SHORT).show();
                    }
                });

//                alertBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        alertDialog.dismiss();
//                    }
//                });

                alertDialog = alertBuilder.create();
                alertDialog.show();

            }
        });



        btData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(AddNewEntryActivity.this,  4,  calendar);
            }
        });




        //键盘按钮：
        btDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (straccount.length()>0) {
                    straccount=straccount.substring(0,straccount.length()-1);
                    if (point>0){
                        point--;
                    }
                    if (straccount.equals("")){
                        straccount="0";
                    }
                }
                putTextview(straccount);
            }
        });
        btNumPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (point==0){
                    straccount=straccount+".";
                    point++;
                }
                putTextview(straccount);
            }
        });
        btNumber0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (point==0&&Integer.parseInt(straccount)==0&&straccount.length()<8){
                    straccount="0";
                }else if (point!=0&&point<3){
                    straccount+="0";
                    point++;
                }else if (point==0&&straccount.length()<7){
                    straccount+="0";
                }
                putTextview(straccount);
            }
        });
        btNumber1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (point==0&&Integer.parseInt(straccount)==0){
                    straccount="1";
                }else if (point!=0&&point<3){
                    straccount+="1";
                    point++;
                }else if (point==0&&straccount.length()<7){
                    straccount+="1";
                }
                putTextview(straccount);
            }
        });
        btNumber2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (point==0&&Integer.parseInt(straccount)==0){
                    straccount="2";
                }else if (point!=0&&point<3){
                    straccount+="2";
                    point++;
                }else if (point==0&&straccount.length()<7){
                    straccount+="2";
                }
                putTextview(straccount);
            }
        });
        btNumber3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (point==0&&Integer.parseInt(straccount)==0){
                    straccount="3";
                }else if (point!=0&&point<3){
                    straccount+="3";
                    point++;
                }else if (point==0&&straccount.length()<7){
                    straccount+="3";
                }
                putTextview(straccount);
            }
        });
        btNumber4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (point==0&&Integer.parseInt(straccount)==0){
                    straccount="4";
                }else if (point!=0&&point<3){
                    straccount+="4";
                    point++;
                }else if (point==0&&straccount.length()<7){
                    straccount+="4";
                }
                putTextview(straccount);
            }
        });
        btNumber5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (point==0&&Integer.parseInt(straccount)==0){
                    straccount="5";
                }else if (point!=0&&point<3){
                    straccount+="5";
                    point++;
                }else if (point==0&&straccount.length()<7){
                    straccount+="5";
                }
                putTextview(straccount);
            }
        });
        btNumber6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (point==0&&Integer.parseInt(straccount)==0){
                    straccount="6";
                }else if (point!=0&&point<3){
                    straccount+="6";
                    point++;
                }else if (point==0&&straccount.length()<7){
                    straccount+="6";
                }
                putTextview(straccount);
            }
        });
        btNumber7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (point==0&&Integer.parseInt(straccount)==0){
                    straccount="7";
                }else if (point!=0&&point<3){
                    straccount+="7";
                    point++;
                }else if (point==0&&straccount.length()<7){
                    straccount+="7";
                }
                putTextview(straccount);
            }
        });
        btNumber8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (point==0&&Integer.parseInt(straccount)==0){
                    straccount="8";
                }else if (point!=0&&point<3){
                    straccount+="8";
                    point++;
                }else if (point==0&&straccount.length()<7){
                    straccount+="8";
                }
                putTextview(straccount);
            }
        });
        btNumber9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (point==0&&Integer.parseInt(straccount)==0){
                    straccount="9";
                }else if (point!=0&&point<3){
                    straccount+="9";
                    point++;
                }else if (point==0&&straccount.length()<7){
                    straccount+="9";
                }
                putTextview(straccount);
            }
        });
    }

    private void initifuser() {
        userInfo = (UserInfo)getApplication();
        if (userInfo.getUser()==null){
            Toast.makeText(AddNewEntryActivity.this,"未登录！",Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initdata() {
        accountId = null;
        incometypeid=null;
        expendituretypeid=1L;
        database = InitMapper.getDatabase();
        accountMapper = InitMapper.getAccountMapper();
        accountTypeMapper = InitMapper.getAccountTypeMapper();
        expenditureTypeMapper = InitMapper.getExpenditureTypeMapper();
        incomeTypeMapper = InitMapper.getIncomeTypeMapper();
        recordMapper = InitMapper.getRecordMapper();
        userMapper = InitMapper.getUserMapper();
        statisticsMapper = InitMapper.getStatisticsMapper();
    }

    private void insertOneRecord(long nextId, Long accountId, Long expenditureTypeId, Long incomeTypeId, double account, String remark, String time) {
        Record record1 = new Record();
        record1.setId(nextId);
        record1.setAccountId(accountId);
        record1.setExpenditureTypeId(expenditureTypeId);
        record1.setIncomeTypeId(incomeTypeId);
        record1.setAmount(account);
        record1.setRemark(remark);
        record1.setTime(time);
        recordMapper.insertRecord(record1);

    }

    private void initListener() {
        ivZDining.setOnClickListener(this);
        ivZClothing.setOnClickListener(this);
        ivZDailyuse.setOnClickListener(this);
        ivZVegetables.setOnClickListener(this);
        ivZTravel.setOnClickListener(this);
        ivZEntertainment.setOnClickListener(this);
        ivZDigital.setOnClickListener(this);
        ivZOther.setOnClickListener(this);
        ivSSalary.setOnClickListener(this);
        ivSParttime.setOnClickListener(this);
        ivSFinancial.setOnClickListener(this);
        ivSOther.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageView2:
                pictureSelect="餐饮支出";
                initImageView();
                ivZDining.setAlpha((float)0.3);
                expendituretypeid= 1L;
                break;
            case R.id.imageView:
                pictureSelect="服饰支出";
                initImageView();
                ivZClothing.setAlpha((float)0.3);
                expendituretypeid= 2L;
                break;
            case R.id.imageView3:
                pictureSelect="日用支出";
                initImageView();
                ivZDailyuse.setAlpha((float)0.3);
                expendituretypeid= 3L;
                break;
            case R.id.imageView4:
                pictureSelect="蔬菜支出";
                initImageView();
                ivZVegetables.setAlpha((float)0.3);
                expendituretypeid= 4L;
                break;
            case R.id.imageView6:
                pictureSelect="出行支出";
                initImageView();
                ivZTravel.setAlpha((float)0.3);
                expendituretypeid= 5L;
                break;
            case R.id.imageView7:
                pictureSelect="娱乐支出";
                initImageView();
                ivZEntertainment.setAlpha((float)0.3);
                expendituretypeid= 6L;
                break;
            case R.id.imageView8:
                pictureSelect="数码支出";
                initImageView();
                ivZDigital.setAlpha((float)0.3);
                expendituretypeid= 7L;
                break;
            case R.id.imageView9:
                pictureSelect="其他支出";
                initImageView();
                ivZOther.setAlpha((float)0.3);
                expendituretypeid= 8L;
                break;
            case R.id.imageView10:
                pictureSelect="工资收入";
                initImageView();
                ivSSalary.setAlpha((float)0.3);
                incometypeid = 1L;
                break;
            case R.id.imageView11:
                pictureSelect="兼职收入";
                initImageView();
                ivSParttime.setAlpha((float)0.3);
                incometypeid = 2L;
                break;
            case R.id.imageView5:
                pictureSelect="理财收入";
                initImageView();
                ivSFinancial.setAlpha((float)0.3);
                incometypeid = 3L;
                break;
            case R.id.imageView12:
                pictureSelect="其他收入";
                initImageView();
                ivSOther.setAlpha((float)0.3);
                incometypeid = 4L;
                break;
        }
    }
    public void showDatePickerDialog(Activity activity, int themeResId, Calendar calendar) {
        // 直接创建一个DatePickerDialog对话框实例，并将它显示出来
        new DatePickerDialog(activity, themeResId, new DatePickerDialog.OnDateSetListener() {
            // 绑定监听器(How the parent is notified that the date is set.)
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // 此处得到选择的时间，可以进行你想要的操作
                Year=year;
                Month=monthOfYear+1;
                Day=dayOfMonth;
                btData.setText(year+"."+(monthOfYear+1)+"."+dayOfMonth);
            }
        }
                // 设置初始日期
                , calendar.get(Calendar.YEAR)
                , calendar.get(Calendar.MONTH)
                , calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
    private void initImageView(){
        incometypeid=null;
        expendituretypeid=null;
        ivZDining.setAlpha((float) 1);
        ivZClothing.setAlpha((float)1);
        ivZDailyuse.setAlpha((float)1);
        ivZVegetables.setAlpha((float)1);
        ivZTravel.setAlpha((float)1);
        ivZEntertainment.setAlpha((float)1);
        ivZDigital.setAlpha((float)1);
        ivZOther.setAlpha((float)1);
        ivSSalary.setAlpha((float)1);
        ivSParttime.setAlpha((float)1);
        ivSFinancial.setAlpha((float)1);
        ivSOther.setAlpha((float)1);
    }

    private void initview(){
        btNumber0 = findViewById(R.id.btnumber0);
        btNumber1 = findViewById(R.id.btnumber1);
        btNumber2 = findViewById(R.id.btnumber2);
        btNumber3 = findViewById(R.id.btnumber3);
        btNumber4 = findViewById(R.id.btnumber4);
        btNumber5 = findViewById(R.id.btnumber5);
        btNumber6 = findViewById(R.id.btnumber6);
        btNumber7 = findViewById(R.id.btnumber7);
        btNumber8 = findViewById(R.id.btnumber8);
        btNumber9 = findViewById(R.id.btnumber9);
        btNumPoint = findViewById(R.id.btnumpoint);
        btDelete = findViewById(R.id.btDelete);
        btFinish = findViewById(R.id.btFinish);
        btData = findViewById(R.id.btData);
        tvAccount = findViewById(R.id.textviewAccount);
        btAccount = findViewById(R.id.button);
        account=0;
        straccount="0";
        point=0;//0无小数点其他小数点
        selectaccount="默认账户";
        pictureSelect="餐饮支出";

        ivZDining = findViewById(R.id.imageView2);
        ivZClothing = findViewById(R.id.imageView);
        ivZDailyuse = findViewById(R.id.imageView3);
        ivZVegetables = findViewById(R.id.imageView4);
        ivZTravel = findViewById(R.id.imageView6);
        ivZEntertainment = findViewById(R.id.imageView7);
        ivZDigital = findViewById(R.id.imageView8);
        ivZOther = findViewById(R.id.imageView9);
        ivSSalary = findViewById(R.id.imageView10);
        ivSParttime = findViewById(R.id.imageView11);
        ivSFinancial= findViewById(R.id.imageView5);
        ivSOther= findViewById(R.id.imageView12);

        calendar= Calendar.getInstance(Locale.CHINA);
        Year = calendar.get(Calendar.YEAR);
        Month = calendar.get(Calendar.MONTH)+1;
        Day = calendar.get(Calendar.DAY_OF_MONTH);
/////////////
        userMapper = InitMapper.getUserMapper();
    }
    public void putTextview(String straccount) {
        tvAccount.setText(straccount);
    }
}
