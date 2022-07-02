package com.ql.uniqueId.service.impl;

import com.ql.uniqueId.dao.IdDao;
import com.ql.uniqueId.dao.impl.IdDaoImpl;
import com.ql.uniqueId.domain.IdEntity;
import com.ql.uniqueId.service.IdService;
import com.ql.uniqueId.utils.IdUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author wanqiuli
 * @date 2022/7/1 10:48
 */
public class IdServiceImpl implements IdService {

    @Resource
    private JdbcTemplate jdbcTemplate;
    private static final int DEFAULT_STEP = 100;
    private int step = DEFAULT_STEP;
    private final Map<String, IdEntity> tableIdMap = new ConcurrentHashMap<>();
    private Map<String, Integer> tableStepMap = new HashMap<>();
    private IdDao idDao;
    private final Lock lock = new ReentrantLock();

    public void setStep(int step) {
        this.step = step;
    }

    @PostConstruct
    public void init() {
        initDao();
        initMap();
        IdUtils.setIdService(this);
    }

    private void initDao() {
        // 初始化dao
        idDao = new IdDaoImpl(jdbcTemplate);
    }

    public void initStepMap(Map<String, Integer> stepMap) {
        if (stepMap != null && !stepMap.isEmpty()) {
            tableStepMap = stepMap;
        }
    }

    private void initMap() {
        // 初始化map
        List<IdEntity> allIdEntity = idDao.getAllIdEntity();
        allIdEntity.forEach(idEntity -> {
            Integer stepVal = tableStepMap.get(idEntity.getTableName());
            if (stepVal == null) {
                idEntity.init(step, idDao);
            } else {
                idEntity.init(stepVal, idDao);
            }
            tableIdMap.put(idEntity.getTableName(), idEntity);
        });
    }


    @Override
    public Long getNextId(String tableName) {
        IdEntity idEntity = getIdEntity(tableName);
        return idEntity.getNextId();
    }

    private IdEntity getIdEntity(String tableName) {
        IdEntity idEntity = tableIdMap.get(tableName);
        if (idEntity == null) {
            lock.lock();
            try {
                idEntity = tableIdMap.get(tableName);
                // 找不到idEntity就创建一个新的
                if (idEntity == null) {
                    idEntity = IdEntity.create(tableName);
                    Integer stepVal = tableStepMap.get(idEntity.getTableName());
                    if (stepVal == null) {
                        idEntity.init(step, idDao);
                    } else {
                        idEntity.init(stepVal, idDao);
                    }
                    idDao.insert(idEntity);
                    tableIdMap.put(tableName, idEntity);
                }
            } finally {
                lock.unlock();
            }
        }
        return idEntity;
    }
}
