package com.example.account.pojo.entity;

public class Account {

    /**
     * ID
     */
    private Long id;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 账户类型 ID
     */
    private Long accountTypeId;

    /**
     * 账户名称
     */
    private String name;

    /**
     * 是否被删除
     */
    private Boolean remove;

    public Account() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getAccountTypeId() {
        return accountTypeId;
    }

    public void setAccountTypeId(Long accountTypeId) {
        this.accountTypeId = accountTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getRemove() {
        return remove;
    }

    public void setRemove(Boolean remove) {
        this.remove = remove;
    }
}
