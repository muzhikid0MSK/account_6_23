package com.example.account.pojo.entity;

public class AccountType {

    /**
     * ID
     */
    private Long id;

    /**
     * 账户种类名称
     */
    private String name;

    /**
     * 账户图标
     */
    private String imageUrl;

    /**
     * 是否被删除
     */
    private Boolean remove;

    public AccountType() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getRemove() {
        return remove;
    }

    public void setRemove(Boolean remove) {
        this.remove = remove;
    }
}
