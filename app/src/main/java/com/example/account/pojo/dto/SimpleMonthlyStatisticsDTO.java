package com.example.account.pojo.dto;

/**
 * 主页面板
 */
public class SimpleMonthlyStatisticsDTO {

    private Double incomeAmount;

    private Double ExpenditureAmount;

    public Double getIncomeAmount() {
        return incomeAmount;
    }

    public void setIncomeAmount(Double incomeAmount) {
        this.incomeAmount = incomeAmount;
    }

    public Double getExpenditureAmount() {
        return ExpenditureAmount;
    }

    public void setExpenditureAmount(Double expenditureAmount) {
        ExpenditureAmount = expenditureAmount;
    }
}
