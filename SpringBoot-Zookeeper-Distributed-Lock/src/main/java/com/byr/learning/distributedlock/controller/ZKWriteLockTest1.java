package com.byr.learning.distributedlock.controller;

import com.byr.learning.distributedlock.readwrite.impl.ZKReadWriteLock1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by code4wt on 17/8/27.
 */
@RestController
public class ZKWriteLockTest1 {
    @Autowired
    ZKReadWriteLock1 zkReadWriteLock1;

    public void lock() throws Exception {
        Runnable runnable = () -> {
            try {
                zkReadWriteLock1.writeLock().lock();
                Thread.sleep(1000 + new Random(System.nanoTime()).nextInt(2000));
                zkReadWriteLock1.writeLock().unlock();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        int poolSize = 4;
        ExecutorService executorService = Executors.newFixedThreadPool(poolSize);
        for (int i = 0; i < poolSize; i++) {
            Thread.sleep(10);
            executorService.submit(runnable);
        }

        executorService.awaitTermination(10, TimeUnit.SECONDS);
    }


    public void tryLock() throws Exception {
        Boolean locked = zkReadWriteLock1.writeLock().tryLock();
        System.out.println("locked: " + locked);
        zkReadWriteLock1.writeLock().unlock();
    }


    public void tryLock1() throws Exception {
        Boolean locked = zkReadWriteLock1.writeLock().tryLock(20000);
        System.out.println("locked: " + locked);
        zkReadWriteLock1.writeLock().unlock();
    }
}