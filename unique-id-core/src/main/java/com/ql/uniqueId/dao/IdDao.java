package com.ql.uniqueId.dao;

import com.ql.uniqueId.domain.SequencePo;

import java.util.List;

/**
 * @author wanqiuli
 * @date 2022/7/26 20:24
 */
public interface IdDao {

    /**
     * 获取当前数据库存储的所有需要用到Id的模块
     *
     * @return the {@link List<String>} data
     */
    List<String> getAllSequenceInfo();

    /**
     * function is addSequence
     *
     * @param tableName   the tableName
     * @param defaultStep the defaultStep
     */
    void addSequence(String tableName, Integer defaultStep);

    /**
     * function is updateAndGet
     *
     * @param tableName the tableName
     * @return the {@link SequencePo} data
     */
    SequencePo updateAndGet(String tableName);
}
