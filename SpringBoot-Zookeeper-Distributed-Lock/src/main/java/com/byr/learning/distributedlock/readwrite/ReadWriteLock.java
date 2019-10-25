package com.byr.learning.distributedlock.readwrite;

import com.byr.learning.distributedlock.DistributedLock;

/**
 * Created by code4wt on 17/8/26.
 */
public interface ReadWriteLock {

    DistributedLock readLock();
    DistributedLock writeLock();
}
