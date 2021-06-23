package com.example.account.mapper;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.account.pojo.dto.RankDTO;
import com.example.account.pojo.entity.IncomeType;
import com.example.account.util.ConvertUtil;
import com.example.account.util.SnowFlakeUtil;

import java.util.ArrayList;
import java.util.List;

import static com.example.account.util.DataBaseUtil.TABLE_INCOME_TYPE_NAME;

public class IncomeTypeMapper {

    private final SQLiteDatabase database = InitMapper.getDatabase();

    public IncomeTypeMapper() {
        IncomeType basicIncome = new IncomeType();
        basicIncome.setId(1L);
        basicIncome.setName("账户初始收入");
        basicIncome.setRank(1);
        basicIncome.setRemove(false);
        this.insertIncomeType(basicIncome);

        IncomeType wage = new IncomeType();
        wage.setId(2L);
        wage.setName("工资");
        wage.setRank(2);
        wage.setRemove(false);
        this.insertIncomeType(wage);

        IncomeType financial = new IncomeType();
        financial.setId(3L);
        financial.setName("理财");
        financial.setRank(3);
        financial.setRemove(false);
        this.insertIncomeType(financial);

        IncomeType parTime = new IncomeType();
        parTime.setId(4L);
        parTime.setName("兼职");
        parTime.setRank(4);
        parTime.setRemove(false);
        this.insertIncomeType(parTime);
    }

    /**
     * 查询所有的收入类型
     * @return 收入类型列表，按照 rank 排序
     */
    public List<IncomeType> getAllIncomeType(){
        Cursor cursor  = database.rawQuery("SELECT id, name, image_url, rank, remove FROM income_type WHERE remove = 0 ORDER BY rank ASC", null);
        List<IncomeType> result = new ArrayList<>(cursor.getCount());
        for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
            IncomeType incomeType = new IncomeType();
            incomeType.setId(cursor.getLong(0));
            incomeType.setName(cursor.getString(1));
            incomeType.setImageUrl(cursor.getString(2));
            incomeType.setRank(cursor.getInt(3));
            incomeType.setRemove(cursor.getInt(4) == 1);
            result.add(incomeType);
        }
        return result;
    }

    /**
     * 插入收入方式
     * @param incomeType 收入方式 不需要id
     */
    public void insertIncomeType(IncomeType incomeType){
        if (incomeType.getId() == null){
            incomeType.setId(SnowFlakeUtil.getInstance().nextId());
        }
        ContentValues values = ConvertUtil.convertMapToContentValues(ConvertUtil.convertObjectToMap(incomeType, incomeType.getClass()));
        database.insert(TABLE_INCOME_TYPE_NAME, null, values);
    }

    /**
     * 更新收入种类 注意每一次都要传递 IncomeType 的所有信息
     * @param incomeType 收入种类 需要 ID
     */
    public void updateIncomeTypeById(IncomeType incomeType){
        if (incomeType.getId() == null){
            throw new NullPointerException("更新收入种类需要收入种类 ID");
        }
        ContentValues values = ConvertUtil.convertMapToContentValues(ConvertUtil.convertObjectToMap(incomeType, incomeType.getClass()));
        database.update(TABLE_INCOME_TYPE_NAME, values, "id=?", new String[]{String.valueOf(values.get("id"))});
    }

    /**
     * 删除收入种类
     * @param id 收入种类 ID
     */
    public void deleteIncomeTypeById(Long id){
        if (id == null){
            throw new NullPointerException("更新收入种类需要收入种类 ID");
        }
        database.execSQL("UPDATE income_type SET remove = 1 WHERE id = " + id);

    }

    /**
     * 更新支出种类排名，注意每一次都要传递所有的种类的排名，从而保证其 rank 不会重复
     * @param rankDTOList 排名列表
     */
    public void updateIncomeRank(List<RankDTO> rankDTOList){
        for (RankDTO rankDTO : rankDTOList) {
            database.rawQuery("UPDATE income_type SET rank = ? WHERE id = ?", new String[]{String.valueOf(rankDTO.getRank()), String.valueOf(rankDTO.getId())});
        }
    }
}

