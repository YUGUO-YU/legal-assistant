package com.legal.assistant.common.typehandler;

import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.alibaba.fastjson2.JSON;
import org.apache.ibatis.type.MappedTypes;

/**
 * Fastjson2 类型转换器
 */
@MappedTypes({Object.class})
public class FastjsonTypeHandler<T> extends AbstractJsonTypeHandler<T> {
    
    @Override
    protected T parse(String json) {
        return JSON.parseObject(json, (Class<T>) Object.class);
    }
    
    @Override
    protected String toJson(T obj) {
        return JSON.toJSONString(obj);
    }
}
