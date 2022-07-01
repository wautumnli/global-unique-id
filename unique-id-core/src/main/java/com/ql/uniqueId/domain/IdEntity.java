package com.ql.uniqueId.domain;

import com.ql.uniqueId.dao.IdDao;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author wanqiuli
 * @date 2022/7/1 10:51
 */
@Data
@NoArgsConstructor
public class IdEntity {

    private String tableName;
    private Long currentId;
    private Long end;
    private int step;
    private AtomicLong id;
    private IdDao idDao;
    private final Lock lock = new ReentrantLock();

    public static IdEntity create(String tableName) {
        IdEntity idEntity = new IdEntity();
        idEntity.setTableName(tableName);
        idEntity.setCurrentId(0L);
        return idEntity;
    }


    public void init(int step, IdDao idDao) {
        setStep(step);
        setIdDao(idDao);
    }

    public Long getNextId() {
        // 获取此次id范围
        if (end == null) {
            lock.lock();
            try {
                if (end == null) {
                    updateAndSet();
                }
            } finally {
                lock.unlock();
            }
        }
        // 加1
        long nextId = id.addAndGet(1L);
        // 超过此次拉取的范围，重新拉取id
        if (nextId > end) {
            lock.lock();
            try {
                if (nextId > end) {
                    updateAndSet();
                }
                nextId = id.addAndGet(1L);
            } finally {
                lock.unlock();
            }
        }
        return nextId;
    }

    private void updateAndSet() {
        end = idDao.updateAndGetId(tableName, step);
        id = new AtomicLong(end - step);
    }
}
