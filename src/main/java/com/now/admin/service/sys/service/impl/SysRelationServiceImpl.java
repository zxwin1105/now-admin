package com.now.admin.service.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.now.admin.service.sys.domain.SysRelation;
import com.now.admin.service.sys.service.SysRelationService;
import com.now.admin.service.sys.mapper.SysRelationMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
* @author A51-18
* @description 针对表【sys_relation(系统关联表)】的数据库操作Service实现
* @createDate 2026-04-12 11:04:18
*/
@Service
public class SysRelationServiceImpl extends ServiceImpl<SysRelationMapper, SysRelation>
    implements SysRelationService{

    @Resource
    private SysRelationMapper sysRelationMapper;

    @Override
    public List<SysRelation> queryRelation(Long sourceId, String type) {
        if(Objects.isNull(sourceId) || !StringUtils.hasText(type)){
            return List.of();
        }
        LambdaQueryWrapper<SysRelation> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysRelation::getSourceId,sourceId)
                .eq(SysRelation::getRelationType, type);
        return sysRelationMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public List<SysRelation> queryRelation(List<Long> sourceIds, String type) {
        if(CollectionUtils.isEmpty(sourceIds) || !StringUtils.hasText(type)){
            return List.of();
        }
        LambdaQueryWrapper<SysRelation> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SysRelation::getSourceId,sourceIds)
                .eq(SysRelation::getRelationType, type);

        return sysRelationMapper.selectList(lambdaQueryWrapper);
    }

}




