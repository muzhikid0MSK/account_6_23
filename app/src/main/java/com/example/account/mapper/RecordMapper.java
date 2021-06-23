package com.example.account.mapper;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.account.pojo.dto.SearchRecordDTO;
import com.example.account.pojo.entity.Record;
import com.example.account.util.ConvertUtil;
import com.example.account.util.SnowFlakeUtil;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static com.example.account.util.DataBaseUtil.TABLE_RECORD_NAME;

public class RecordMapper {

    private final SQLiteDatabase database = InitMapper.getDatabase();

    /**
     * 插入记录
     * @param record 记录 需要 ID
     */
    public void insertRecord(Record record){
        if (record.getId() == null){
            record.setId(SnowFlakeUtil.getInstance().nextId());
        }
        if (record.getTime() == null){
            record.setTime(new Timestamp(System.currentTimeMillis()).toString());
        }
        ContentValues values = ConvertUtil.convertMapToContentValues(ConvertUtil.convertObjectToMap(record, record.getClass()));
        database.insert(TABLE_RECORD_NAME, null, values);
    }

    /**
     * 更新记录 注意每一次都要传递 record 的所有信息
     * @param record 记录 需要 ID
     */
    public void updateRecordById(Record record){
        if (record.getId() == null){
            throw new NullPointerException("更新记录需要记录 ID");
        }
        ContentValues values = ConvertUtil.convertMapToContentValues(ConvertUtil.convertObjectToMap(record, record.getClass()));
        database.update(TABLE_RECORD_NAME, values, "id=?", new String[]{String.valueOf(values.get("id"))});
    }

    /**
     * 删除记录
     * @param id 记录 ID
     */
    public void deleteRecordById(Long id){
        if (id == null){
            throw new NullPointerException("删除记录需要记录 ID");
        }
        database.delete(TABLE_RECORD_NAME,"id=?",new String[]{String.valueOf(id)});
    }

    /**
     * 根据金额搜索大于等于该金额的记录
     * @param userId 用户 id
     * @param amount 金额
     * @return 搜索结果
     */
    public List<SearchRecordDTO> getRecordLikeAmount(Long userId, Double amount){
        Cursor cursor = database.rawQuery(
                "SELECT record.id, income_type.name, record.amount, record.time FROM record, income_type, account WHERE record.income_type_id = income_type.id AND record.amount >= ?  AND record.account_id = account.id AND account.user_id = ? " +
                "UNION ALL " +
                "SELECT record.id, expenditure_type.name, record.amount, record.time FROM record, expenditure_type, account WHERE record.expenditure_type_id = expenditure_type.id AND record.amount <= ?  AND record.account_id = account.id AND account.user_id = ? ",
                new String[]{String.valueOf(amount), String.valueOf(userId), String.valueOf(-amount) ,String.valueOf(userId)});
        List<SearchRecordDTO> result = new ArrayList<>(cursor.getCount());
        for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
            SearchRecordDTO searchRecordDTO = new SearchRecordDTO();
            searchRecordDTO.setId(cursor.getLong(0));
            searchRecordDTO.setName(cursor.getString(1));
            searchRecordDTO.setAmount(cursor.getDouble(2));
            searchRecordDTO.setTime(Timestamp.valueOf(cursor.getString(3)).getTime());
            result.add(searchRecordDTO);
        }
        return result;
    }

    /**
     * 根据备注搜索记录
     * @param userId 用户 id
     * @param remark 备注
     * @return 搜索结果
     */
    public List<SearchRecordDTO> getRecordLikeRemark(Long userId, String remark){
        remark = "%" + remark + "%";
        Cursor cursor = database.rawQuery(
                "SELECT record.id, income_type.name, record.amount, record.time FROM record, income_type, account WHERE record.income_type_id = income_type.id AND record.remark LIKE ? AND record.account_id = account.id AND account.user_id = ? ",
                new String[]{remark, String.valueOf(userId)});
        List<SearchRecordDTO> result = new ArrayList<>(cursor.getCount());
        for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
            SearchRecordDTO searchRecordDTO = new SearchRecordDTO();
            searchRecordDTO.setId(cursor.getLong(0));
            searchRecordDTO.setName(cursor.getString(1));
            searchRecordDTO.setAmount(cursor.getDouble(2));
            searchRecordDTO.setTime(Timestamp.valueOf(cursor.getString(3)).getTime());
            result.add(searchRecordDTO);
        }
        return result;
    }

    /**
     * 根据分类搜索记录
     * @param userId 用户 id
     * @param typeName 金额
     * @return 搜索结果
     */
    public List<SearchRecordDTO> getRecordLikeTypeName(Long userId, String typeName){
        typeName = "%" + typeName + "%";
        Cursor cursor = database.rawQuery(
                "SELECT record.id, income_type.name, record.amount, record.time FROM record, income_type, account WHERE record.income_type_id = income_type.id AND income_type.name LIKE ?  AND record.account_id = account.id AND account.user_id = ? " +
                        "UNION ALL " +
                        "SELECT record.id, expenditure_type.name, record.amount, record.time FROM record, expenditure_type, account WHERE record.expenditure_type_id = expenditure_type.id AND expenditure_type.name LIKE ?  AND record.account_id = account.id AND account.user_id = ? ",
                new String[]{typeName, String.valueOf(userId), typeName, String.valueOf(userId)});
        List<SearchRecordDTO> result = new ArrayList<>(cursor.getCount());
        for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
            SearchRecordDTO searchRecordDTO = new SearchRecordDTO();
            searchRecordDTO.setId(cursor.getLong(0));
            searchRecordDTO.setName(cursor.getString(1));
            searchRecordDTO.setAmount(cursor.getDouble(2));
            searchRecordDTO.setTime(Timestamp.valueOf(cursor.getString(3)).getTime());
            result.add(searchRecordDTO);
        }
        return result;
    }
}

