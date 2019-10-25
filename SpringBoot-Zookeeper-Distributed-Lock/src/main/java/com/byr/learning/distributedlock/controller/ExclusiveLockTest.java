package com.byr.learning.distributedlock.controller;

import com.byr.learning.distributedlock.exclusive.ExclusiveLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by code4wt on 17/8/24.
 */
@RestController
public class ExclusiveLockTest {
    @Autowired
    ExclusiveLock exclusiveLock;
    public void lock() throws Exception {
        Runnable runnable = () -> {
            try {
                exclusiveLock.lock();
                Thread.sleep(2000);
                exclusiveLock.unlock();
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
        Boolean locked = exclusiveLock.tryLock();
        System.out.println("locked: " + locked);
    }

    public void tryLock1() throws Exception {
        Boolean locked = exclusiveLock.tryLock(50000);
        System.out.println("locked: " + locked);
    }

}