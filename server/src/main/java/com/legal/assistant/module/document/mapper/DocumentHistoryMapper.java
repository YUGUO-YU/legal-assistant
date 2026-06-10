package com.legal.assistant.module.document.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.legal.assistant.module.document.entity.DocumentHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 文书历史 Mapper
 */
@Mapper
public interface DocumentHistoryMapper extends BaseMapper<DocumentHistory> {
    
    /**
     * 查询用户的历史记录
     */
    @Select("SELECT * FROM document_histories WHERE user_id = #{userId} AND deleted = false ORDER BY created_at DESC")
    List<DocumentHistory> selectByUserId(String userId);
}
