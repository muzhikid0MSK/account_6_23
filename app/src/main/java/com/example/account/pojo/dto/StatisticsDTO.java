package com.example.account.pojo.dto;

import java.util.List;

public class StatisticsDTO {

    /**
     * 总支出 or 收入
     */
    private Double totalAmount;

    /**
     * 当为周时，表示每天的（7天） index 0 为七天前 index 6 为今天
     * 当为月时，表示每天的（具体情况分析）， index 0 为 1 号
     * 当为年时，表示每个月的（12个月）， index 0 为 1 月
     *
     */
    private List<Double> detailAmount;

    /**
     * 支出占比与排行列表
     * 列表已经排过序了
     */
    private List<StatisticsDetailDTO> statisticsDetailDTOList;

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<Double> getDetailAmount() {
        return detailAmount;
    }

    public void setDetailAmount(List<Double> detailAmount) {
        this.detailAmount = detailAmount;
    }

    public List<StatisticsDetailDTO> getDisplayDetailDTOList() {
        return statisticsDetailDTOList;
    }

    public void setDisplayDetailDTOList(List<StatisticsDetailDTO> statisticsDetailDTOList) {
        this.statisticsDetailDTOList = statisticsDetailDTOList;
    }
}
