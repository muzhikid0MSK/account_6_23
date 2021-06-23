package com.example.account.mapper;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.account.pojo.entity.Account;
import com.example.account.pojo.entity.User;
import com.example.account.util.ConvertUtil;
import com.example.account.util.SnowFlakeUtil;

import static com.example.account.util.DataBaseUtil.TABLE_USER_NAME;

public class UserMapper {

    private final SQLiteDatabase database = InitMapper.getDatabase();
    private final AccountMapper accountMapper = InitMapper.getAccountMapper();

    /**
     * 新建用户 在新建用户是默认会创建一个账户 一个电话只能由一个用户
     * @param user 用户
     * @throws IllegalArgumentException 目前是电话信息不匹配会跑出
     */
    public void insertUser(User user){
        user.setId(SnowFlakeUtil.getInstance().nextId());
        ContentValues values = ConvertUtil.convertMapToContentValues(ConvertUtil.convertObjectToMap(user, user.getClass()));

        // 电话唯一性判断
        Cursor phoneCheckCursor = database.rawQuery("SELECT id FROM user WHERE user.phone_number = ?", new String[]{user.getPhoneNumber()});
        if (phoneCheckCursor.getCount() > 0){
            throw new IllegalArgumentException("电话号码重复");
        }
        database.insert(TABLE_USER_NAME, null, values);

        // 创建用户时默认创建账户
        Account defaultAccount = new Account();
        // 测试使用
        defaultAccount.setId(SnowFlakeUtil.getInstance().nextId());
        defaultAccount.setUserId(user.getId());
        defaultAccount.setAccountTypeId(0L);
        defaultAccount.setName("默认账户");
        defaultAccount.setRemove(false);
        accountMapper.insertAccount(defaultAccount, 0.0);
    }

    /**
     * 更新用户 注意每一次都要传递 User 的所有信息
     * @param user 用户 需要 ID
     */
    public void updateUserById(User user){
        if (user.getId() == null){
            throw new NullPointerException("更新用户需要用户 ID");
        }
        ContentValues values = ConvertUtil.convertMapToContentValues(ConvertUtil.convertObjectToMap(user, user.getClass()));
        database.update(TABLE_USER_NAME, values, "id=?", new String[]{String.valueOf(values.get("id"))});
    }

    /**
     * 删除用户
     * @param id 用户 ID
     */
    public void deleteUser(Long id){
        if (id == null){
            throw new NullPointerException("删除用户需要用户 id");
        }
        database.delete(TABLE_USER_NAME,"id=?",new String[]{String.valueOf(id)});
    }

    /**
     * 查询用户信息
     * @param id 用户 ID
     * @return 如果匹配到，则返回 User 信息（不含密码），否则返回 null
     */
    public User getUserById(Long id){
        if (id == null){
            throw new NullPointerException("查询用户需要用户 id");
        }
        Cursor cursor = database.rawQuery(
                "SELECT id, user_name, phone_number, avatar_url  FROM user WHERE id = ? limit 1 ",
                new String[]{String.valueOf(id)});
        if (cursor.getCount() > 0){
            cursor.moveToFirst();
            User user = new User();
            user.setId(cursor.getLong(0));
            user.setUserName(cursor.getString(1));
            user.setPhoneNumber(cursor.getColumnName(2));
            user.setAvatarUrl(cursor.getString(3));
            return user;
        }else {
            return null;
        }
    }

    /**
     * 检查用户名密码
     * @param phoneNumber 手机号
     * @param password 密码
     * @return 如果匹配到，则返回 User 信息（不含密码），否则返回 null
     */
    public User checkPassword(String phoneNumber, String password){
        Cursor cursor = database.rawQuery(
                "SELECT id, user_name, phone_number, avatar_url  FROM user WHERE phone_number = ? and password = ? limit 1 ",
                new String[]{phoneNumber, password});
        if (cursor.getCount() > 0){
            cursor.moveToFirst();
            User user = new User();
            user.setId(cursor.getLong(0));
            user.setUserName(cursor.getString(1));
            user.setPhoneNumber(cursor.getColumnName(2));
            user.setAvatarUrl(cursor.getString(3));
            return user;
        }else {
            return null;
        }
    }
}