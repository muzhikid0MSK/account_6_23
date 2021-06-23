package com.example.account.mapper;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.account.pojo.dto.RankDTO;
import com.example.account.pojo.entity.ExpenditureType;
import com.example.account.util.ConvertUtil;
import com.example.account.util.SnowFlakeUtil;

import java.util.ArrayList;
import java.util.List;

import static com.example.account.util.DataBaseUtil.TABLE_EXPENDITURE_TYPE_NAME;

public class ExpenditureTypeMapper {

    private final SQLiteDatabase database = InitMapper.getDatabase();

    public ExpenditureTypeMapper() {
        ExpenditureType loan = new ExpenditureType();
        loan.setId(1L);
        loan.setName("餐饮");
        loan.setRank(1);
        loan.setRemove(false);
        this.insertExpenditureType(loan);

        ExpenditureType apparel = new ExpenditureType();
        apparel.setId(2L);
        apparel.setName("服饰");
        apparel.setRank(2);
        apparel.setRemove(false);
        this.insertExpenditureType(apparel);

        ExpenditureType traffic = new ExpenditureType();
        traffic.setId(3L);
        traffic.setName("交通");
        traffic.setRank(3);
        traffic.setRemove(false);
        this.insertExpenditureType(traffic);

        ExpenditureType shop = new ExpenditureType();
        shop.setId(4L);
        shop.setName("购物");
        shop.setRank(4);
        shop.setRemove(false);
        this.insertExpenditureType(shop);

        ExpenditureType dailyUse = new ExpenditureType();
        dailyUse.setId(5L);
        dailyUse.setName("日用");
        dailyUse.setRank(5);
        dailyUse.setRemove(false);
        this.insertExpenditureType(dailyUse);

        ExpenditureType vegetables = new ExpenditureType();
        vegetables.setId(6L);
        vegetables.setName("蔬菜");
        vegetables.setRank(6);
        vegetables.setRemove(false);
        this.insertExpenditureType(vegetables);

        ExpenditureType fruit = new ExpenditureType();
        fruit.setId(7L);
        fruit.setName("水果");
        fruit.setRank(7);
        fruit.setRemove(false);
        this.insertExpenditureType(fruit);

        ExpenditureType Snacks = new ExpenditureType();
        Snacks.setId(8L);
        Snacks.setName("零食");
        Snacks.setRank(8);
        Snacks.setRemove(false);
        this.insertExpenditureType(Snacks);

        ExpenditureType movement = new ExpenditureType();
        movement.setId(9L);
        movement.setName("运动");
        movement.setRank(9);
        movement.setRemove(false);
        this.insertExpenditureType(movement);

        ExpenditureType entertainment = new ExpenditureType();
        entertainment.setId(10L);
        entertainment.setName("娱乐");
        entertainment.setRank(10);
        entertainment.setRemove(false);
        this.insertExpenditureType(entertainment);

        ExpenditureType communication = new ExpenditureType();
        communication.setId(11L);
        communication.setName("通讯");
        communication.setRank(11);
        communication.setRemove(false);
        this.insertExpenditureType(communication);

        ExpenditureType beauty = new ExpenditureType();
        beauty.setId(12L);
        beauty.setName("美容");
        beauty.setRank(12);
        beauty.setRemove(false);
        this.insertExpenditureType(beauty);

        ExpenditureType digital = new ExpenditureType();
        digital.setId(13L);
        digital.setName("数码");
        digital.setRank(13);
        digital.setRemove(false);
        this.insertExpenditureType(digital);

        ExpenditureType gift = new ExpenditureType();
        gift.setId(14L);
        gift.setName("礼物");
        gift.setRank(14);
        gift.setRemove(false);
        this.insertExpenditureType(gift);

        ExpenditureType travel = new ExpenditureType();
        travel.setId(15L);
        travel.setName("旅行");
        travel.setRank(15);
        travel.setRemove(false);
        this.insertExpenditureType(travel);

        ExpenditureType home = new ExpenditureType();
        home.setId(16L);
        home.setName("居家");
        home.setRank(16);
        home.setRemove(false);
        this.insertExpenditureType(home);
    }

    /**
     * 查询所有的支出类型
     * @return 支出类型列表，按照 rank 排序
     */
    public List<ExpenditureType> getAllExpenditureType(){
        Cursor cursor  = database.rawQuery("SELECT id, name, image_url, rank, remove FROM expenditure_type WHERE remove = 0 ORDER BY rank ASC", null);
        List<ExpenditureType> result = new ArrayList<>(cursor.getCount());
        for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
            ExpenditureType expenditureType = new ExpenditureType();
            expenditureType.setId(cursor.getLong(0));
            expenditureType.setName(cursor.getString(1));
            expenditureType.setImageUrl(cursor.getString(2));
            expenditureType.setRank(cursor.getInt(3));
            expenditureType.setRemove(cursor.getInt(4) == 1);
            result.add(expenditureType);
        }

        return result;
    }

    /**
     * 添加支出种类
     * @param expenditureType 支出种类 不需要 id
     */
    public void insertExpenditureType(ExpenditureType expenditureType){
        if (expenditureType.getId() == null){
            expenditureType.setId(SnowFlakeUtil.getInstance().nextId());
        }
        ContentValues values = ConvertUtil.convertMapToContentValues(ConvertUtil.convertObjectToMap(expenditureType, expenditureType.getClass()));
        database.insert(TABLE_EXPENDITURE_TYPE_NAME, null, values);
    }

    /**
     * 更新支出种类 注意每一次都要传递 expenditureType 的所有信息
     * @param expenditureType 支出种类 需要 ID
     */
    public void updateExpenditureTypeById(ExpenditureType expenditureType){
        if (expenditureType.getId() == null){
            throw new NullPointerException("更新支出种类需要支出种类 ID");
        }
        ContentValues values = ConvertUtil.convertMapToContentValues(ConvertUtil.convertObjectToMap(expenditureType, expenditureType.getClass()));
        database.update(TABLE_EXPENDITURE_TYPE_NAME, values, "id=?", new String[]{String.valueOf(values.get("id"))});
    }

    /**
     * 删除支出种类
     * @param id 支出种类 ID
     */
    public void deleteExpenditureTypeById(Long id){
        if (id == null){
            throw new NullPointerException("更新支出种类需要支出种类 ID");
        }
        database.execSQL("update expenditure_type SET remove = 1 WHERE id = " + id);
    }

    /**
     * 更新支出种类排名，注意每一次都要传递所有的种类的排名，从而保证其 rank 不会重复
     * @param rankDTOList 排名列表
     */
    public void updateExpenditureRank(List<RankDTO> rankDTOList){
        for (RankDTO rankDTO : rankDTOList) {
            database.rawQuery("UPDATE expenditure_type SET rank = ? WHERE id = ?", new String[]{String.valueOf(rankDTO.getRank()), String.valueOf(rankDTO.getId())});
        }


    }
}
