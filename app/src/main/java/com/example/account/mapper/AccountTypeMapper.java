package com.example.account.mapper;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.account.pojo.entity.AccountType;
import com.example.account.util.ConvertUtil;
import com.example.account.util.SnowFlakeUtil;

import java.util.ArrayList;
import java.util.List;

import static com.example.account.util.DataBaseUtil.TABLE_ACCOUNT_TYPE_NAME;

public class AccountTypeMapper{

    private final SQLiteDatabase database = InitMapper.getDatabase();

    public AccountTypeMapper() {
        // 默认账户类型创建
        AccountType basicAccountType = new AccountType();
        basicAccountType.setId(0L);
        basicAccountType.setName("默认账户类型");
        basicAccountType.setRemove(false);
        basicAccountType.setImageUrl("");
        this.insertAccountType(basicAccountType);

        // 支付宝
        AccountType aliPay = new AccountType();
        aliPay.setId(1L);
        aliPay.setName("支付宝");
        aliPay.setRemove(false);
        aliPay.setImageUrl("");
        this.insertAccountType(aliPay);

        // 微信
        AccountType wechat = new AccountType();
        wechat.setId(2L);
        wechat.setName("微信");
        wechat.setRemove(false);
        wechat.setImageUrl("");
        this.insertAccountType(wechat);

        // 现金
        AccountType cash = new AccountType();
        cash.setId(3L);
        cash.setName("现金");
        cash.setRemove(false);
        cash.setImageUrl("");
        this.insertAccountType(cash);

        // 借记卡
        AccountType debitCard = new AccountType();
        debitCard.setId(4L);
        debitCard.setName("借记卡");
        debitCard.setRemove(false);
        debitCard.setImageUrl("");
        this.insertAccountType(debitCard);
    }

    /**
     * 添加账户种类
     * @param accountType 账户种类 不需要id
     */
    public void insertAccountType(AccountType accountType){
        if (accountType.getId() == null){
            accountType.setId(SnowFlakeUtil.getInstance().nextId());
        }
        ContentValues values = ConvertUtil.convertMapToContentValues(ConvertUtil.convertObjectToMap(accountType, accountType.getClass()));
        database.insert(TABLE_ACCOUNT_TYPE_NAME, null, values);
    }

    /**
     * 更新账户种类 注意每一次都要传递 accountType 的所有信息
     * @param accountType 账户种类 需要id
     */
    public void updateAccountTypeById(AccountType accountType){
        if (accountType.getId() == null){
            throw new NullPointerException("更新账户种类需要账户种类 ID");
        }
        ContentValues values = ConvertUtil.convertMapToContentValues(ConvertUtil.convertObjectToMap(accountType, accountType.getClass()));
        database.update(TABLE_ACCOUNT_TYPE_NAME, values, "id=?", new String[]{String.valueOf(values.get("id"))});
    }

    /**
     * 删除账户种类
     * @param id 账户种类 ID
     */
    public void deleteAccountTypeById(Long id){
        if (id == null){
            throw new NullPointerException("删除账户种类需要账户种类 ID");
        }
        database.execSQL("UPDATE account_type SET remove = 1 WHERE id = " + id);
    }

    /**
     * 获取账户种类
     */
    public List<AccountType> getAllAccountType(){
        Cursor cursor  = database.rawQuery("SELECT id, name, image_url, remove FROM account_type WHERE remove = 0 ORDER BY id ASC", null);
        List<AccountType> result = new ArrayList<>(cursor.getCount());
        for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
            AccountType accountType = new AccountType();
            accountType.setId(cursor.getLong(0));
            accountType.setName(cursor.getString(1));
            accountType.setImageUrl(cursor.getString(2));
            accountType.setRemove(cursor.getInt(3) == 1);
            result.add(accountType);
        }
        return result;
    }


}
