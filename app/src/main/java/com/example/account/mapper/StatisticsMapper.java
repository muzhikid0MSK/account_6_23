package com.example.account.mapper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.account.pojo.dto.SimpleMonthlyStatisticsDTO;
import com.example.account.pojo.dto.StatisticsDTO;
import com.example.account.pojo.dto.StatisticsDetailDTO;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static com.example.account.util.ConvertUtil.convertMonthToDays;

public class StatisticsMapper {

    private final SQLiteDatabase database = InitMapper.getDatabase();

    /**
     * 根据月份获取收支信息
     * @param userId 用户 ID
     * @param year 年份 2021
     * @param month 月份 (01-12)
     * @return 主页显示信息
     */
    public SimpleMonthlyStatisticsDTO getSimpleMonthlyStatistics(Long userId, String year, String month){
        SimpleMonthlyStatisticsDTO simpleMonthlyStatisticsDTO = new SimpleMonthlyStatisticsDTO();
        Cursor incomeCursor = database.rawQuery(
                "SELECT SUM(amount) " +
                "FROM record, account " +
                "WHERE STRFTIME('%Y',time) = ? AND STRFTIME('%m',time) = ? AND amount > 0 AND record.account_id = account.id AND account.user_id = ? " +
                "GROUP BY STRFTIME('%Y',time), STRFTIME('%m',time) "
                , new String[]{year, month, String.valueOf(userId)}
                );
        Cursor expenditureCursor = database.rawQuery(
                "SELECT SUM(-amount) " +
                        "FROM record, account " +
                        "WHERE STRFTIME('%Y',time) = ? AND STRFTIME('%m',time) = ? AND amount < 0 AND record.account_id = account.id AND account.user_id = ? " +
                        "GROUP BY STRFTIME('%Y',time), STRFTIME('%m',time) "
                , new String[]{year, month, String.valueOf(userId)}
                );

        if (incomeCursor.getCount() == 0){
            simpleMonthlyStatisticsDTO.setIncomeAmount(0.0);
        }else {
            incomeCursor.moveToFirst();
            simpleMonthlyStatisticsDTO.setIncomeAmount(incomeCursor.getDouble(0));
        }

        if (expenditureCursor.getCount() == 0){
            simpleMonthlyStatisticsDTO.setExpenditureAmount(0.0);
        }else {
            expenditureCursor.moveToFirst();
            simpleMonthlyStatisticsDTO.setExpenditureAmount(expenditureCursor.getDouble(0));
        }

        return simpleMonthlyStatisticsDTO;
    }

    /**
     * 根据周获取支出统计信息, 这个时间表示今天的日起
     * @param userId 用户 id
     * @param year 年份 2021
     * @param month 月 (01-12)
     * @param day 日 (01-31)
     * @return 统计信息
     */
    public StatisticsDTO getWeeklyExpenditureStatistics(Long userId, String year, String month, String day){
        StatisticsDTO statisticsDTO = new StatisticsDTO();

        long todayTimestamp = new Timestamp(Integer.parseInt(year) - 1900, Integer.parseInt(month) - 1, Integer.parseInt(day), 23, 59, 59, 999999999).getTime() / 1000;
        long startTimeStamp = (todayTimestamp - 1000 * 60 * 60 * 24 * 7) / 1000;

        // 按照种类分类
        Cursor typeCursor = database.rawQuery(
                "SELECT expenditure_type.name, expenditure_type.image_url, SUM(-amount) " +
                        "FROM record, expenditure_type, account " +
                        "WHERE record.expenditure_type_id = expenditure_type.id AND amount < 0 AND STRFTIME('%s',time) < ? AND  STRFTIME('%s',time) > ? AND record.account_id = account.id AND account.user_id = ? " +
                        "GROUP BY expenditure_type.id " +
                        "ORDER BY SUM(-amount) DESC "
                , new String[]{String.valueOf(todayTimestamp), String.valueOf(startTimeStamp),String.valueOf(userId)}
        );
        // 计算总支出
        double sum = 0;
        for (typeCursor.moveToFirst(); !typeCursor.isAfterLast(); typeCursor.moveToNext()){
            sum += typeCursor.getDouble(2);
        }
        // 填入
        statisticsDTO.setTotalAmount(sum);
        List<StatisticsDetailDTO> detailDTOList = new ArrayList<>(typeCursor.getCount());
        for (typeCursor.moveToFirst(); !typeCursor.isAfterLast(); typeCursor.moveToNext()){
            StatisticsDetailDTO temp = new StatisticsDetailDTO();
            temp.setName(typeCursor.getString(0));
            temp.setImageUrl(typeCursor.getString(1));
            temp.setAmount(typeCursor.getDouble(2));
            temp.setPercent(typeCursor.getDouble(2) / sum);
            detailDTOList.add(temp);
        }
        statisticsDTO.setDisplayDetailDTOList(detailDTOList);

        // 计算这七天的消费
        Cursor costCursor = database.rawQuery(
                "SELECT julianday(?) - julianday(time), SUM(-amount) " +
                        "FROM record, account " +
                        "WHERE amount < 0 AND STRFTIME('%s',time) < ? AND  STRFTIME('%s',time) > ?  AND record.account_id = account.id AND account.user_id = ? " +
                        "GROUP BY STRFTIME('%d',time) " +
                        "ORDER BY STRFTIME('%d',time) "
                , new String[]{new Timestamp(todayTimestamp * 1000).toString(), String.valueOf(todayTimestamp), String.valueOf(startTimeStamp),String.valueOf(userId)}
        );

        List<Double> costList = new ArrayList<>(7);
        for (int i = 0; i < 7; i++) {
            costList.add(0D);
        }
        for (costCursor.moveToFirst(); !costCursor.isAfterLast(); costCursor.moveToNext()){
            int diffDay = (int) costCursor.getDouble(0);
            int index = 6 - diffDay;
            costList.set(index, costCursor.getDouble(1));
        }
        statisticsDTO.setDetailAmount(costList);
        return statisticsDTO;
    }

    /**
     * 根据周获取收入统计信息, 这个时间表示今天的日起
     * @param userId 用户 id
     * @param year 年份 2021
     * @param month 月 (01-12)
     * @param day 日 (01-31)
     * @return 统计信息
     */
    public StatisticsDTO getWeeklyIncomeStatistics(Long userId, String year, String month, String day){
        StatisticsDTO statisticsDTO = new StatisticsDTO();

        long todayTimestamp = new Timestamp(Integer.parseInt(year) - 1900, Integer.parseInt(month) - 1, Integer.parseInt(day), 23, 59, 59, 999999999).getTime() / 1000;
        long startTimeStamp = (todayTimestamp - 1000 * 60 * 60 * 24 * 7) / 1000;

        // 按照种类分类
        Cursor typeCursor = database.rawQuery(
                "SELECT income_type.name, income_type.image_url, SUM(amount) " +
                        "FROM record, income_type, account " +
                        "WHERE record.income_type_id = income_type.id AND amount > 0 AND STRFTIME('%s',time) < ? AND  STRFTIME('%s',time) > ?  AND record.account_id = account.id AND account.user_id = ? " +
                        "GROUP BY income_type.id " +
                        "ORDER BY SUM(amount) DESC "
                , new String[]{String.valueOf(todayTimestamp), String.valueOf(startTimeStamp) ,String.valueOf(userId)}
        );
        // 计算总收入
        double sum = 0;
        for (typeCursor.moveToFirst(); !typeCursor.isAfterLast(); typeCursor.moveToNext()){
            sum += typeCursor.getDouble(2);
        }
        // 填入
        statisticsDTO.setTotalAmount(sum);
        List<StatisticsDetailDTO> detailDTOList = new ArrayList<>(typeCursor.getCount());
        for (typeCursor.moveToFirst(); !typeCursor.isAfterLast(); typeCursor.moveToNext()){
            StatisticsDetailDTO temp = new StatisticsDetailDTO();
            temp.setName(typeCursor.getString(0));
            temp.setImageUrl(typeCursor.getString(1));
            temp.setAmount(typeCursor.getDouble(2));
            temp.setPercent(typeCursor.getDouble(2) / sum);
            detailDTOList.add(temp);
        }
        statisticsDTO.setDisplayDetailDTOList(detailDTOList);
        // 计算这七天的收入
        Cursor costCursor = database.rawQuery(
                "SELECT julianday(?) - julianday(time), SUM(amount) " +
                        "FROM record, account " +
                        "WHERE amount > 0 AND STRFTIME('%s',time) < ? AND  STRFTIME('%s',time) > ?  AND record.account_id = account.id AND account.user_id = ? " +
                        "GROUP BY STRFTIME('%d',time) " +
                        "ORDER BY STRFTIME('%d',time) "
                , new String[]{new Timestamp(todayTimestamp * 1000).toString(), String.valueOf(todayTimestamp), String.valueOf(startTimeStamp) ,String.valueOf(userId)}
        );
        List<Double> costList = new ArrayList<>(7);
        for (int i = 0; i < 7; i++) {
            costList.add(0D);
        }
        System.out.println("天数：" + costCursor.getCount());
        for (costCursor.moveToFirst(); !costCursor.isAfterLast(); costCursor.moveToNext()){
            int diffDay = (int) costCursor.getDouble(0);
            int index = 6 - diffDay;
            costList.set(index, costCursor.getDouble(1));
        }
        statisticsDTO.setDetailAmount(costList);
        return statisticsDTO;
    }

    /**
     * 根据月份获取支出统计信息
     * @param userId 用户 id
     * @param year 年份
     * @param month 月份 (01-12)
     * @return 统计信息
     */
    public StatisticsDTO getMonthlyExpenditureStatistics(Long userId, String year, String month){
        StatisticsDTO statisticsDTO = new StatisticsDTO();
        // 按照种类分类
        Cursor typeCursor = database.rawQuery(
                "SELECT expenditure_type.name, expenditure_type.image_url, SUM(-amount) " +
                        "FROM record, expenditure_type, account " +
                        "WHERE record.expenditure_type_id = expenditure_type.id AND STRFTIME('%Y',time) = ? AND STRFTIME('%m',time) = ? AND amount < 0  AND record.account_id = account.id AND account.user_id = ? " +
                        "GROUP BY expenditure_type.id " +
                        "ORDER BY SUM(-amount) DESC "
                , new String[]{year, month ,String.valueOf(userId)}
        );

        // 计算总支出
        double sum = 0;
        for (typeCursor.moveToFirst(); !typeCursor.isAfterLast(); typeCursor.moveToNext()){
            sum += typeCursor.getDouble(2);
        }
        // 填入
        statisticsDTO.setTotalAmount(sum);
        List<StatisticsDetailDTO> detailDTOList = new ArrayList<>(typeCursor.getCount());
        for (typeCursor.moveToFirst(); !typeCursor.isAfterLast(); typeCursor.moveToNext()){
            StatisticsDetailDTO temp = new StatisticsDetailDTO();
            temp.setName(typeCursor.getString(0));
            temp.setImageUrl(typeCursor.getString(1));
            temp.setAmount(typeCursor.getDouble(2));
            temp.setPercent(typeCursor.getDouble(2) / sum);
            detailDTOList.add(temp);
        }
        statisticsDTO.setDisplayDetailDTOList(detailDTOList);
        // 计算这个月每天的消费
        Cursor costCursor = database.rawQuery(
                "SELECT STRFTIME('%d',time), SUM(-amount) " +
                        "FROM record, account " +
                        "WHERE STRFTIME('%Y',time) = ? AND STRFTIME('%m',time) = ? AND amount < 0  AND record.account_id = account.id AND account.user_id = ? " +
                        "GROUP BY STRFTIME('%d',time) " +
                        "ORDER BY STRFTIME('%d',time)"
                , new String[]{year, month ,String.valueOf(userId)}
        );
        List<Double> costList = new ArrayList<>(convertMonthToDays(year, month));
        for (int i = 0; i < convertMonthToDays(year, month); i++) {
            costList.add(0D);
        }
        for (costCursor.moveToFirst(); !costCursor.isAfterLast(); costCursor.moveToNext()){
            costList.set(Integer.parseInt(costCursor.getString(0)) - 1, costCursor.getDouble(1));
        }
        statisticsDTO.setDetailAmount(costList);
        return statisticsDTO;
    }

    /**
     * 根据月份获取收入统计信息
     * @param userId 用户 id
     * @param year 年份
     * @param month 月份 (01-12)
     * @return 统计信息
     */
    public StatisticsDTO getMonthlyIncomeStatistics(Long userId, String year, String month){
        StatisticsDTO statisticsDTO = new StatisticsDTO();
        // 按照种类分类
        Cursor typeCursor = database.rawQuery(
                "SELECT income_type.name, income_type.image_url, SUM(amount) " +
                        "FROM record, income_type, account " +
                        "WHERE record.income_type_id = income_type.id AND STRFTIME('%Y',time) = ? AND STRFTIME('%m',time) = ? AND amount > 0  AND record.account_id = account.id AND account.user_id = ? " +
                        "GROUP BY income_type.id " +
                        "ORDER BY SUM(amount) DESC "
                , new String[]{year, month ,String.valueOf(userId)}
        );
        // 计算总支出
        double sum = 0;
        for (typeCursor.moveToFirst(); !typeCursor.isAfterLast(); typeCursor.moveToNext()){
            sum += typeCursor.getDouble(2);
        }
        // 填入
        statisticsDTO.setTotalAmount(sum);
        List<StatisticsDetailDTO> detailDTOList = new ArrayList<>(typeCursor.getCount());
        for (typeCursor.moveToFirst(); !typeCursor.isAfterLast(); typeCursor.moveToNext()){
            StatisticsDetailDTO temp = new StatisticsDetailDTO();
            temp.setName(typeCursor.getString(0));
            temp.setImageUrl(typeCursor.getString(1));
            temp.setAmount(typeCursor.getDouble(2));
            temp.setPercent(typeCursor.getDouble(2) / sum);
            detailDTOList.add(temp);
        }
        statisticsDTO.setDisplayDetailDTOList(detailDTOList);
        // 计算这个月每天的消费
        Cursor costCursor = database.rawQuery(
                "SELECT STRFTIME('%d',time), SUM(amount) " +
                        "FROM record, account " +
                        "WHERE STRFTIME('%Y',time) = ? AND STRFTIME('%m',time) = ? AND amount > 0  AND record.account_id = account.id AND account.user_id = ? " +
                        "GROUP BY STRFTIME('%d',time) " +
                        "ORDER BY STRFTIME('%d',time)"
                , new String[]{year, month ,String.valueOf(userId)}
        );
        List<Double> costList = new ArrayList<>(convertMonthToDays(year, month));
        for (int i = 0; i < convertMonthToDays(year, month); i++) {
            costList.add(0D);
        }
        for (costCursor.moveToFirst(); !costCursor.isAfterLast(); costCursor.moveToNext()){
            costList.set(Integer.parseInt(costCursor.getString(0)) - 1, costCursor.getDouble(1));
        }
        statisticsDTO.setDetailAmount(costList);
        return statisticsDTO;
    }

    /**
     * 根据年份获取支出统计信息
     * @param userId 用户 id
     * @param year 年份
     * @return 统计信息
     */
    public StatisticsDTO getYearlyExpenditureStatistics(Long userId, String year){
        StatisticsDTO statisticsDTO = new StatisticsDTO();
        // 按照种类分类
        Cursor typeCursor = database.rawQuery(
                "SELECT expenditure_type.name, expenditure_type.image_url, SUM(-amount) " +
                        "FROM record, expenditure_type, account " +
                        "WHERE record.expenditure_type_id = expenditure_type.id AND STRFTIME('%Y',time) = ? AND amount < 0  AND record.account_id = account.id AND account.user_id = ? " +
                        "GROUP BY expenditure_type.id " +
                        "ORDER BY SUM(-amount) DESC "
                , new String[]{year ,String.valueOf(userId)}
        );
        // 计算总支出
        double sum = 0;
        for (typeCursor.moveToFirst(); !typeCursor.isAfterLast(); typeCursor.moveToNext()){
            sum += typeCursor.getDouble(2);
        }
        // 填入
        statisticsDTO.setTotalAmount(sum);
        List<StatisticsDetailDTO> detailDTOList = new ArrayList<>(typeCursor.getCount());
        for (typeCursor.moveToFirst(); !typeCursor.isAfterLast(); typeCursor.moveToNext()){
            StatisticsDetailDTO temp = new StatisticsDetailDTO();
            temp.setName(typeCursor.getString(0));
            temp.setImageUrl(typeCursor.getString(1));
            temp.setAmount(typeCursor.getDouble(2));
            temp.setPercent(typeCursor.getDouble(2) / sum);
            detailDTOList.add(temp);
        }
        statisticsDTO.setDisplayDetailDTOList(detailDTOList);
        // 计算每个月的消费
        Cursor costCursor = database.rawQuery(
                "SELECT STRFTIME('%m',time), SUM(-amount) " +
                        "FROM record, account " +
                        "WHERE STRFTIME('%Y',time) = ? AND amount < 0  AND record.account_id = account.id AND account.user_id = ? " +
                        "GROUP BY STRFTIME('%m',time)" +
                        "ORDER BY STRFTIME('%m',time)"
                , new String[]{year ,String.valueOf(userId)}
        );
        List<Double> costList = new ArrayList<>(12);
        for (int i = 0; i < 12; i++) {
            costList.add(0D);
        }
        for (costCursor.moveToFirst(); !costCursor.isAfterLast(); costCursor.moveToNext()){
            costList.set(Integer.parseInt(costCursor.getString(0)) - 1, costCursor.getDouble(1));
        }
        statisticsDTO.setDetailAmount(costList);
        return statisticsDTO;
    }

    /**
     * 根据年份获取支出统计信息
     * @param userId 用户 id
     * @param year 年份
     * @return 统计信息
     */
    public StatisticsDTO getYearlyIncomeStatistics(Long userId, String year){
        StatisticsDTO statisticsDTO = new StatisticsDTO();
        // 按照种类分类
        Cursor typeCursor = database.rawQuery(
                "SELECT income_type.name, income_type.image_url, SUM(amount) " +
                        "FROM record, income_type, account " +
                        "WHERE record.income_type_id = income_type.id AND STRFTIME('%Y',time) = ? AND amount > 0  AND record.account_id = account.id AND account.user_id = ? " +
                        "GROUP BY income_type.id " +
                        "ORDER BY SUM(amount) DESC "
                , new String[]{year ,String.valueOf(userId)}
        );
        // 计算总收入
        double sum = 0;
        for (typeCursor.moveToFirst(); !typeCursor.isAfterLast(); typeCursor.moveToNext()){
            sum += typeCursor.getDouble(2);
        }
        // 填入
        statisticsDTO.setTotalAmount(sum);
        List<StatisticsDetailDTO> detailDTOList = new ArrayList<>(typeCursor.getCount());
        for (typeCursor.moveToFirst(); !typeCursor.isAfterLast(); typeCursor.moveToNext()){
            StatisticsDetailDTO temp = new StatisticsDetailDTO();
            temp.setName(typeCursor.getString(0));
            temp.setImageUrl(typeCursor.getString(1));
            temp.setAmount(typeCursor.getDouble(2));
            temp.setPercent(typeCursor.getDouble(2) / sum);
            detailDTOList.add(temp);
        }
        statisticsDTO.setDisplayDetailDTOList(detailDTOList);
        // 计算每个月的消费
        Cursor costCursor = database.rawQuery(
                "SELECT STRFTIME('%m',time), SUM(amount) " +
                        "FROM record, account " +
                        "WHERE STRFTIME('%Y',time) = ? AND amount > 0  AND record.account_id = account.id AND account.user_id = ? " +
                        "GROUP BY STRFTIME('%m',time)" +
                        "ORDER BY STRFTIME('%m',time)"
                , new String[]{year ,String.valueOf(userId)}
        );
        List<Double> costList = new ArrayList<>(12);
        for (int i = 0; i < 12; i++) {
            costList.add(0D);
        }
        for (costCursor.moveToFirst(); !costCursor.isAfterLast(); costCursor.moveToNext()){
            costList.set(Integer.parseInt(costCursor.getString(0)) - 1, costCursor.getDouble(1));
        }
        statisticsDTO.setDetailAmount(costList);
        return statisticsDTO;
    }
}
