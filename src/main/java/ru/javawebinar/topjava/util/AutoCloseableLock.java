package ru.javawebinar.topjava.util;

import java.util.concurrent.locks.Lock;

public class AutoCloseableLock implements AutoCloseable {
    private final Lock lock;

    public AutoCloseableLock(Lock lock) {
        this.lock = lock;
        lock.lock();
    }

    @Override
    public void close() {
        lock.unlock();
    }
}