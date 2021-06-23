package com.example.account.pojo.entity;

public class ExpenditureType {

    /**
     * ID
     */
    private Long id;

    /**
     * 支出种类名称
     */
    private String name;

    /**
     * 图标
     */
    private String imageUrl;

    /**
     * 排序
     */
    private Integer rank;

    /**
     * 是否被删除
     */
    private Boolean remove;

    public ExpenditureType() {
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

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Boolean getRemove() {
        return remove;
    }

    public void setRemove(Boolean remove) {
        this.remove = remove;
    }
}
