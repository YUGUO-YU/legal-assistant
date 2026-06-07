package com.legal.assistant.module.document.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.legal.assistant.module.document.entity.DocumentTemplate;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 文书模板 Mapper
 */
@Mapper
public interface DocumentTemplateMapper extends BaseMapper<DocumentTemplate> {
    
    /**
     * 查询所有公开模板
     */
    List<DocumentTemplate> selectPublicTemplates();
    
    /**
     * 按分类查询模板
     */
    List<DocumentTemplate> selectByCategory(String category);
}
