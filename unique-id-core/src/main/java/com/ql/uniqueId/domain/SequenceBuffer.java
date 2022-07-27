package com.ql.uniqueId.domain;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author wanqiuli
 * @date 2022/7/26 20:19
 */
public class SequenceBuffer {

    private Segment[] segments;
    private String sequence;

    private final AtomicBoolean threadRunning;
    private final ReadWriteLock lock;
    private volatile boolean initIdSuc;
    private volatile int currentPos;
    private volatile boolean nextReady;

    public SequenceBuffer(String sequence) {
        this.segments = new Segment[]{new Segment(), new Segment()};
        this.sequence = sequence;
        this.threadRunning = new AtomicBoolean(false);
        this.lock = new ReentrantReadWriteLock();
        this.nextReady = false;
        this.currentPos = 0;
    }

    public Lock rLock() {
        return lock.readLock();
    }

    public Lock wLock() {
        return lock.writeLock();
    }

    public boolean isInitIdOver() {
        return initIdSuc;
    }

    public void initIdOver() {
        initIdSuc = true;
    }

    public Segment currentSegment() {
        return segments[getCurrentPos()];
    }

    public Segment nextSegment() {
        return segments[nextPos()];
    }

    private int getCurrentPos() {
        return currentPos;
    }

    public void switchPos() {
        currentPos = nextPos();
    }

    private int nextPos() {
        return currentPos ^ 1;
    }

    public String getSequence() {
        return sequence;
    }

    public boolean nextIsReady() {
        return nextReady;
    }

    public boolean nextNotReady() {
        return !nextReady;
    }

    public AtomicBoolean getThreadRunning() {
        return threadRunning;
    }

    public void setNextReady(boolean ready) {
        this.nextReady = ready;
    }
}
