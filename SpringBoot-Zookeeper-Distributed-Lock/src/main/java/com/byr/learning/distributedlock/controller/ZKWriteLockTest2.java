package com.byr.learning.distributedlock.controller;

import com.byr.learning.distributedlock.readwrite.impl.ZKReadWriteLock2;
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
public class ZKWriteLockTest2 {
    @Autowired
    ZKReadWriteLock2 zkReadWriteLock2;

    public void lock() throws Exception {
        Runnable runnable = () -> {
            try {
                ZKReadWriteLock2 crwl = new ZKReadWriteLock2();
                crwl.writeLock().lock();
                Thread.sleep(1000 + new Random(System.nanoTime()).nextInt(2000));
                crwl.writeLock().unlock();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        int poolSize = 3;
        ExecutorService executorService = Executors.newFixedThreadPool(poolSize);
        for (int i = 0; i < poolSize; i++) {
            executorService.submit(runnable);
            Thread.sleep(10);
        }

        executorService.awaitTermination(10, TimeUnit.SECONDS);
    }


    public void tryLock() throws Exception {
        Boolean locked = zkReadWriteLock2.writeLock().tryLock();
        System.out.println("locked: " + locked);
        zkReadWriteLock2.writeLock().unlock();
    }

}