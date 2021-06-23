package com.example.account.mapper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.account.util.DataBaseUtil.DATABASE_NAME;
import static com.example.account.util.DataBaseUtil.DATABASE_VERSION;
import static com.example.account.util.DataBaseUtil.TABLE_ACCOUNT_CREATE;
import static com.example.account.util.DataBaseUtil.TABLE_ACCOUNT_TYPE_CREATE;
import static com.example.account.util.DataBaseUtil.TABLE_EXPENDITURE_TYPE_CREATE;
import static com.example.account.util.DataBaseUtil.TABLE_INCOME_TYPE_CREATE;
import static com.example.account.util.DataBaseUtil.TABLE_RECORD_CREATE;
import static com.example.account.util.DataBaseUtil.TABLE_USER_CREATE;

public class InitMapper extends SQLiteOpenHelper {

    private static volatile SQLiteDatabase database;

    private static volatile AccountMapper accountMapper;
    private static volatile AccountTypeMapper accountTypeMapper;
    private static volatile ExpenditureTypeMapper expenditureTypeMapper;
    private static volatile IncomeTypeMapper incomeTypeMapper;
    private static volatile RecordMapper recordMapper;
    private static volatile UserMapper userMapper;
    private static volatile StatisticsMapper statisticsMapper;

    private static volatile boolean init = false;

    /**
     * 开放接口 单例模式
     * @param context 上下文
     */
    public InitMapper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = getWritableDatabase();
        /**
         * 下面的初始化顺序不要改变 因为其根据依赖关系确定了
         */
        if (!init){
            init = true;
            recordMapper = new RecordMapper();
            accountMapper = new AccountMapper();
            userMapper = new UserMapper();

            accountTypeMapper = new AccountTypeMapper();
            expenditureTypeMapper = new ExpenditureTypeMapper();
            incomeTypeMapper = new IncomeTypeMapper();

            statisticsMapper = new StatisticsMapper();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        database = db;
        database.execSQL(TABLE_ACCOUNT_CREATE);
        database.execSQL(TABLE_USER_CREATE);
        database.execSQL(TABLE_ACCOUNT_TYPE_CREATE);
        database.execSQL(TABLE_EXPENDITURE_TYPE_CREATE);
        database.execSQL(TABLE_INCOME_TYPE_CREATE);
        database.execSQL(TABLE_RECORD_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static SQLiteDatabase getDatabase() {
        return database;
    }

    public static AccountMapper getAccountMapper() {
        return accountMapper;
    }

    public static AccountTypeMapper getAccountTypeMapper() {
        return accountTypeMapper;
    }

    public static ExpenditureTypeMapper getExpenditureTypeMapper() {
        return expenditureTypeMapper;
    }

    public static IncomeTypeMapper getIncomeTypeMapper() {
        return incomeTypeMapper;
    }

    public static RecordMapper getRecordMapper() {
        return recordMapper;
    }

    public static UserMapper getUserMapper() {
        return userMapper;
    }

    public static StatisticsMapper getStatisticsMapper() {
        return statisticsMapper;
    }
}
