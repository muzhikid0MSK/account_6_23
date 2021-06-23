package com.example.account.mapper;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.account.pojo.dto.AccountDTO;
import com.example.account.pojo.entity.Account;
import com.example.account.pojo.entity.Record;
import com.example.account.util.ConvertUtil;
import com.example.account.util.SnowFlakeUtil;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static com.example.account.util.DataBaseUtil.TABLE_ACCOUNT_NAME;

public class AccountMapper {

    private final SQLiteDatabase database = InitMapper.getDatabase();
    private final RecordMapper recordMapper = InitMapper.getRecordMapper();

    /**
     * 添加账户，默认会添加一条记录作为初始资金来源
     * @param account 账户 不需要id
     * @param basicAccount 初始金额
     */
    public void insertAccount(Account account, Double basicAccount){
        if (account.getId() == null){
            account.setId(SnowFlakeUtil.getInstance().nextId());
        }
        ContentValues values = ConvertUtil.convertMapToContentValues(ConvertUtil.convertObjectToMap(account, account.getClass()));
        database.insert(TABLE_ACCOUNT_NAME, null, values);

        // 初始化账户余额，如果有的话
        if (basicAccount != 0.0){
            Record record = new Record();
            record.setId(SnowFlakeUtil.getInstance().nextId());
            record.setAccountId(account.getId());
            record.setIncomeTypeId(0L);
            record.setAmount(basicAccount);
            record.setRemark("账户初始金额");
            record.setTime(new Timestamp(System.currentTimeMillis()).toString());
            recordMapper.insertRecord(record);
        }
    }

    /**
     * 更新账户信息 注意每一次都要传递 Account 的所有信息
     * @param account 账户 需要id
     */
    public void updateAccountById(Account account){
        if (account.getId() == null){
            throw new NullPointerException("更新账户信息需要账户 ID");
        }
        ContentValues values = ConvertUtil.convertMapToContentValues(ConvertUtil.convertObjectToMap(account, account.getClass()));
        database.update(TABLE_ACCOUNT_NAME, values, "id=?", new String[]{String.valueOf(values.get("id"))});
    }

    /**
     * 删除账户
     * @param id 账户 id
     */
    public void deleteAccountById(Long id){
        if (id == null){
            throw new NullPointerException("删除账户信息需要账户 ID");
        }
        database.execSQL("UPDATE account SET remove = 1 WHERE id = " + id);
    }

    /**
     * 按照用户 ID 查询用户名下的所有账户
     * @param id 用户 ID
     * @return 所有的账户
     */
    public List<AccountDTO> getAccountByUserId(Long id){
        if (id == null){
            throw new NullPointerException("查询账户需要用户 ID");
        }
        Cursor cursor = database.rawQuery(
                "SELECT account.id, account_type.name, account_type.image_url, account.name, SUM(record.amount) " +
                        "FROM account " +
                        "   LEFT JOIN record ON account.id = record.account_id" +
                        ", account_type " +
                        "WHERE account.user_id = ? AND account.account_type_id = account_type.id " +
                        "GROUP BY account.id",
                new String[]{String.valueOf(id)});
        List<AccountDTO> result = new ArrayList<>(cursor.getCount());
        for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
            AccountDTO temp = new AccountDTO();
            temp.setId(cursor.getLong(0));
            temp.setAccountType(cursor.getString(1));
            temp.setName(cursor.getString(3));
            temp.setImageUrl(cursor.getString(2));
            temp.setAmount(cursor.getDouble(4));
            result.add(temp);
        }
        return result;
    }
}
