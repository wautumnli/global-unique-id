package com.ql.uniqueId.utils;

import com.ql.uniqueId.exception.service.IdService;

/**
 * @author wanqiuli
 * @date 2022/7/1 14:37
 */
public class IdUtils {

    private static IdService idService;

    public static Long getNextId(String tableName) {
        return idService.getNextId(tableName);
    }
}
