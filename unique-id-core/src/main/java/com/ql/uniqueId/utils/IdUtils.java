package com.ql.uniqueId.utils;

import com.ql.uniqueId.service.IdService;

/**
 * @author wanqiuli
 * @date 2022/7/1 14:37
 */
public class IdUtils {

    private static IdService idService;

    public static Long getNextId(String tableName) {
        return idService.getNextId(tableName);
    }

    public static void setIdService(IdService idServiceImpl) {
        idService = idServiceImpl;
    }

    public IdService getIdService() {
        return idService;
    }
}
