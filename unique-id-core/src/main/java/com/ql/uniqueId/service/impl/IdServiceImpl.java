package com.ql.uniqueId.service.impl;

import com.ql.uniqueId.dao.IdDao;
import com.ql.uniqueId.domain.Segment;
import com.ql.uniqueId.domain.SequenceBuffer;
import com.ql.uniqueId.domain.SequencePo;
import com.ql.uniqueId.exception.IdException;
import com.ql.uniqueId.service.IdService;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.*;

/**
 * @author wanqiuli
 * @date 2022/7/26 20:18
 */
@Slf4j
public class IdServiceImpl implements IdService {

    private IdDao idDao;
    private boolean initOver;
    private static final Integer DEFAULT_STEP = 1000;
    private final Map<String, SequenceBuffer> sequenceBufferMap = new ConcurrentHashMap<>();
    private final ExecutorService threadPoolExecutor = new ThreadPoolExecutor(5, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new IdServiceThreadFactory());

    public static class IdServiceThreadFactory implements ThreadFactory {

        private static int threadNum = 0;

        private static synchronized int nextThreadNum() {
            return threadNum++;
        }

        @Override
        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, "Thread-Segment-Update-" + nextThreadNum());
        }
    }


    @Override
    public boolean init() {
        // 更新map数据
        updateSequenceMap();
        initOver = true;
        // 开启一个线程可以定时更新map里的数据
        runThreadToUpdateSequenceMap();
        return true;
    }

    private void runThreadToUpdateSequenceMap() {
        // TODO: 新线程定时跑
    }

    @Override
    public Long getNextId(String sequence) {
        // 没有初始化完成等待初始化完成
        if (!initOver) {
            throw new IdException("id utils init is not complete!");
        }
        // 如果没有这个Sequence，需要插入一个新的Sequence
        if (!sequenceBufferMap.containsKey(sequence)) {
            synchronized (this) {
                if (!sequenceBufferMap.containsKey(sequence)) {
                    idDao.addSequence(sequence, DEFAULT_STEP);
                    SequenceBuffer buffer = new SequenceBuffer(sequence);
                    sequenceBufferMap.put(sequence, buffer);
                }
            }
        }
        SequenceBuffer sequenceBuffer = sequenceBufferMap.get(sequence);
        // 当前这个buffer没有初始化需要初始化
        if (!sequenceBuffer.isInitIdOver()) {
            synchronized (sequenceBuffer) {
                if (!sequenceBuffer.isInitIdOver()) {
                    // 更新号段
                    updateSequenceBufferSegment(sequenceBuffer.getSequence(), sequenceBuffer.currentSegment());
                    sequenceBuffer.initIdOver();
                }
            }
        }
        return getNextIdFromSequenceBuffer(sequenceBuffer);
    }

    private Long getNextIdFromSequenceBuffer(SequenceBuffer sequenceBuffer) {
        while (true) {
            sequenceBuffer.rLock().lock();
            try {
                Segment segment = sequenceBuffer.currentSegment();
                // 判断下一号段是否准备好了，是否超过阈值，是否已经有线程在更新了..
                if (segment.needNextSegment()
                        && sequenceBuffer.nextNotReady()
                        && sequenceBuffer.getThreadRunning().compareAndSet(false, true)) {
                    threadPoolExecutor.execute(() -> {
                        Segment nextSegment = sequenceBuffer.nextSegment();
                        boolean updateOver = false;
                        try {
                            updateSequenceBufferSegment(sequenceBuffer.getSequence(), nextSegment);
                            updateOver = true;
                        } catch (Exception e) {
                            log.error("[id utils] next segment update error");
                        } finally {
                            if (updateOver) {
                                sequenceBuffer.wLock().lock();
                                sequenceBuffer.setNextReady(true);
                                sequenceBuffer.getThreadRunning().set(false);
                                sequenceBuffer.wLock().unlock();
                            } else {
                                sequenceBuffer.getThreadRunning().set(false);
                            }
                        }
                    });
                }
                long currentId = segment.getCurrentId().incrementAndGet();
                if (currentId < segment.getMax()) {
                    return currentId;
                }
            } finally {
                sequenceBuffer.rLock().unlock();
            }
            // 可以让当前线程睡眠一会，因为前面已经开启了更新
            threadSleep(sequenceBuffer);
            sequenceBuffer.wLock().lock();
            try {
                Segment segment = sequenceBuffer.currentSegment();
                long currentId = segment.getCurrentId().incrementAndGet();
                if (currentId < segment.getMax()) {
                    return currentId;
                }
                if (sequenceBuffer.nextIsReady()) {
                    sequenceBuffer.switchPos();
                    sequenceBuffer.setNextReady(false);
                } else {
                    throw new IdException("[id utils] both segment not ready");
                }
            } finally {
                sequenceBuffer.wLock().unlock();
            }
        }
    }

    private void threadSleep(SequenceBuffer sequenceBuffer) {
        int times = 0;
        while (sequenceBuffer.getThreadRunning().get()) {
            if (times > 100) {
                break;
            }
            times++;
            try {
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (InterruptedException e) {
                log.error("[id utils] thread interrupted exception");
                break;
            }
        }
    }

    private void updateSequenceBufferSegment(String tableName, Segment segment) {
        // 更新db号段
        SequencePo sequencePo = idDao.updateAndGet(tableName);
        // 更新号段信息
        segment.update(sequencePo.getCurrentMaxId(), sequencePo.getStep());
    }

    private void updateSequenceMap() {
        List<String> sequenceNameList = idDao.getAllSequenceInfo();
        List<String> isExistSequence = new ArrayList<>(sequenceBufferMap.keySet());
        Set<String> dbSequence = new HashSet<>(sequenceNameList);
        Set<String> isExistSequenceSet = new HashSet<>(isExistSequence);
        // 删除掉已经存在的sequence
        for (String sequence : isExistSequence) {
            dbSequence.remove(sequence);
        }
        // 剩下的是新的需要重新插入的
        for (String sequence : dbSequence) {
            SequenceBuffer buffer = new SequenceBuffer(sequence);
            sequenceBufferMap.put(sequence, buffer);
        }
        // 去除掉已经存在的
        for (String sequence : sequenceNameList) {
            isExistSequenceSet.remove(sequence);
        }
        for (String sequence : isExistSequenceSet) {
            sequenceBufferMap.remove(sequence);
        }
    }

    public void setIdDao(IdDao idDao) {
        this.idDao = idDao;
    }
}
