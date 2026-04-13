package com.now.admin.service.sys.service;

import com.now.admin.service.sys.domain.SysRelation;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author A51-18
* @description 针对表【sys_relation(系统关联表)】的数据库操作Service
* @createDate 2026-04-12 11:04:18
*/
public interface SysRelationService extends IService<SysRelation> {

    /**
     * 查询资源关系
     * @param sourceId 资源ID
     * @param type 关系类型
     * @return 关系
     */
    List<SysRelation> queryRelation(Long sourceId, String type);

    /**
     * 批量查询多个资源的某种关系信息
     * @param sourceIds 资源信息
     * @param type 关系类型
     * @return 关系
     */
    List<SysRelation> queryRelation(List<Long> sourceIds, String type);
}
