package com.ql.uniqueId.domain;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author wanqiuli
 * @date 2022/7/26 20:19
 */
public class Segment {

    private volatile long max;
    private volatile int step;
    private AtomicLong currentId;


    public void update(Long currentMaxId, Integer step) {
        this.max = currentMaxId;
        this.step = step;
        this.currentId = new AtomicLong(currentMaxId - step);
    }

    public AtomicLong getCurrentId() {
        return currentId;
    }

    public long getMax() {
        return max;
    }

    public boolean needNextSegment() {
        return max - currentId.get() < 0.9 * step;
    }
}
