package com.ql.uniqueId.service;

/**
 * @author wanqiuli
 * @date 2022/7/1 10:48
 */
public interface IdService {

    /**
     * function is init
     *
     * @return the {@link Boolean} data
     */
    boolean init();

    /**
     * 获取下一个Id
     *
     * @param tableName the tableName
     * @return the {@link Long} data
     */
    Long getNextId(String tableName);
}
