package com.add.log.util;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import tools.jackson.databind.ObjectMapper;

import java.sql.*;

@MappedTypes(Object.class)
public class JsonTypeHandler extends BaseTypeHandler<Object> {

    private static final ObjectMapper objectMapper = new tools.jackson.databind.ObjectMapper();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        try {
            // 객체를 JSON 문자열로 변환한 뒤 PostgreSQL의 json 타입 객체로 설정
            String json = objectMapper.writeValueAsString(parameter);
            ps.setObject(i, json, Types.OTHER);
        } catch (Exception e) {
            throw new SQLException("Error converting Object to JSON String", e);
        }
    }

    @Override
    public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return toObject(rs.getString(columnName));
    }

    @Override
    public Object getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return toObject(rs.getString(columnIndex));
    }

    @Override
    public Object getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return toObject(cs.getString(columnIndex));
    }

    private Object toObject(String json) {
        if (json == null || json.isEmpty()) return null;
        try {
            return objectMapper.readValue(json, Object.class);
        } catch (Exception e) {
            return json;
        }
    }
}
