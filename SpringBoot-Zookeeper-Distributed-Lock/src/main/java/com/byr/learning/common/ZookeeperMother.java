package com.byr.learning.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

/**
 * @ClassName: ZookeeperMother
 * @Description: zookeeper 加锁公共方法
 * @Author: yanrong
 * @Date: 2019/10/25 14:25
 * @Version: 1.0
 */
@Component
@Slf4j
public class ZookeeperMother {
    private final static String ROOT_PATH_LOCK = "byr";

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    /**
     * The Curator framework.
     */
    @Autowired
    CuratorFramework curatorFramework;

    /**
     * 创建zookeeper临时节点
     *
     * @param path
     */
    public Boolean creteZookeeperNode(String path) {
        String keyPath = "/" + ROOT_PATH_LOCK + "/" + path;
        while (true) {
            try {
                String returnValue = curatorFramework.create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.EPHEMERAL)
                        .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                        .forPath(keyPath);
                log.info("创建令临时节点成功:{}", keyPath);
                if (returnValue != null) {
                    return true;
                } else {
                    if (countDownLatch.getCount() <= 0) {
                        countDownLatch = new CountDownLatch(1);
                    }
                    //当前线程等待，直到计数为零(避免死循环)
                    countDownLatch.await();
                }
            } catch (Exception e) {
                log.info("创建临时节点失败:{}", keyPath);
                throw new RuntimeException(e);
            }
        }
    }
    /**
     * 释放分布式锁
     * @param path the  节点路径
     * @return the boolean
     */
    public boolean unlockzookeeper(String path) {
        try {
            String keyPath = "/" + ROOT_PATH_LOCK + "/" + path;
            if (curatorFramework.checkExists().forPath(keyPath) != null) {
                curatorFramework.delete().forPath(keyPath);
            }
        } catch (Exception e) {
            log.error("释放锁失败", e);
            throw new RuntimeException(e);
        }
        return true;
    }
}
