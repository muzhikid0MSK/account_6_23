package com.example.account.pojo.entity;

public class Record {

    /**
     * Id
     */
    private Long id;

    /**
     * 账户 ID
     */
    private Long accountId;

    /**
     * 支出种类 ID
     */
    private Long expenditureTypeId;

    /**
     * 收入种类 ID
     */
    private Long incomeTypeId;

    /**
     * 金额
     * 支出 < 0
     * 收入 > 0
     */
    private Double amount;

    private String remark;

    private String time;

    public Record() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getExpenditureTypeId() {
        return expenditureTypeId;
    }

    public void setExpenditureTypeId(Long expenditureTypeId) {
        this.expenditureTypeId = expenditureTypeId;
    }

    public Long getIncomeTypeId() {
        return incomeTypeId;
    }

    public void setIncomeTypeId(Long incomeTypeId) {
        this.incomeTypeId = incomeTypeId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
