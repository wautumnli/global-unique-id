package com.ql.uniqueId.dao;

import com.ql.uniqueId.domain.IdEntity;

import java.util.List;

/**
 * @author wanqiuli
 * @date 2022/7/1 10:54
 */
public interface IdDao {

    /**
     * 获取所有表的id
     *
     * @return the {@link List<IdEntity>} data
     */
    List<IdEntity> getAllIdEntity();

    /**
     * 插入新表
     *
     * @param idEntity the idEntity
     */
    void insert(IdEntity idEntity);

    /**
     * 更新并获取
     *
     * @param tableName the tableName
     * @param step      the step
     * @return the {@link Long} data
     */
    Long updateAndGetId(String tableName, int step);
}
