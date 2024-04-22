package com.at2024.redis7_study.domain;

/**
 * @author lyh
 * @date 2024/4/22  10:57
 */
public class User {

    private String id;

    private String name;

    private String remark;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
