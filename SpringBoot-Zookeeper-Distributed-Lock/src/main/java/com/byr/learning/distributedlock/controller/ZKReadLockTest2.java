package com.byr.learning.distributedlock.controller;

import com.byr.learning.distributedlock.readwrite.impl.ZKReadWriteLock2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Crea0ted by code4wt on 17/8/27.
 */
@RestController
public class ZKReadLockTest2 {
    @Autowired
    ZKReadWriteLock2 zkReadWriteLock2;

    public void lock() throws Exception {
        Runnable runnable = () -> {
            try {
                zkReadWriteLock2.readLock().lock();
                Thread.sleep(1000 + new Random(System.nanoTime()).nextInt(2000));
                zkReadWriteLock2.readLock().unlock();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        int poolSize = 4;
        ExecutorService executorService = Executors.newFixedThreadPool(poolSize);
        for (int i = 0; i < poolSize; i++) {
            executorService.submit(runnable);
        }
        executorService.awaitTermination(10, TimeUnit.SECONDS);
    }


    public void tryLock() throws Exception {
        Boolean locked = zkReadWriteLock2.readLock().tryLock();
        System.out.println("locked: " + locked);
        zkReadWriteLock2.readLock().unlock();
    }


    public void tryLock1() throws Exception {
        Boolean locked = zkReadWriteLock2.readLock().tryLock(20000);
        System.out.println("locked: " + locked);
        zkReadWriteLock2.readLock().unlock();
    }

}