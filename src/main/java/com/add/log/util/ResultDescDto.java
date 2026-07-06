package com.add.log.util;

import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResultDescDto {
    private String code = null;
    private String desc = null;
    private String type = null;
    private String status = null;

    public String toString() {
        return (new GsonBuilder()).serializeNulls().create().toJson(this);
    }
}
