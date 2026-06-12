package com.legal.assistant.common.typehandler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import org.apache.ibatis.type.MappedTypes;

import java.util.ArrayList;
import java.util.List;

/**
 * Fastjson2 类型转换器
 */
@MappedTypes({Object.class})
public class FastjsonTypeHandler<T> extends AbstractJsonTypeHandler<T> {
    
    @Override
    protected T parse(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        String trimmed = json.trim();
        if (trimmed.startsWith("[")) {
            JSONArray jsonArray = JSON.parseArray(json);
            ArrayList<Object> list = new ArrayList<>(jsonArray.size());
            list.addAll(jsonArray);
            return (T) list;
        }
        return JSON.parseObject(json, (Class<T>) Object.class);
    }
    
    @Override
    protected String toJson(T obj) {
        return JSON.toJSONString(obj);
    }
}
