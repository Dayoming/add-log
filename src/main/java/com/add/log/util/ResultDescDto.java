package com.add.log.util;

import com.google.gson.GsonBuilder;
import lombok.Generated;

public class ResultDescDto {
    private String code = null;
    private String desc = null;
    private String type = null;
    private String status = null;

    public ResultDescDto() {
    }

    public String toString() {
        return (new GsonBuilder()).serializeNulls().create().toJson(this);
    }

    @Generated
    public String getCode() {
        return this.code;
    }

    @Generated
    public String getDesc() {
        return this.desc;
    }

    @Generated
    public String getType() {
        return this.type;
    }

    @Generated
    public String getStatus() {
        return this.status;
    }

    @Generated
    public void setCode(final String code) {
        this.code = code;
    }

    @Generated
    public void setDesc(final String desc) {
        this.desc = desc;
    }

    @Generated
    public void setType(final String type) {
        this.type = type;
    }

    @Generated
    public void setStatus(final String status) {
        this.status = status;
    }
}
