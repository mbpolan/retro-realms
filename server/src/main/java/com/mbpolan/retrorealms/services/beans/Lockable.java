package com.mbpolan.retrorealms.services.beans;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Class that models a resource that requires synchronization.
 *
 * @author Mike Polan
 */
public abstract class Lockable {

    private Lock lock;

    public Lockable() {
        this.lock = new ReentrantLock();
    }

    public void lock() {
        this.lock.lock();
    }

    public void unlock() {
        this.lock.unlock();
    }
}
