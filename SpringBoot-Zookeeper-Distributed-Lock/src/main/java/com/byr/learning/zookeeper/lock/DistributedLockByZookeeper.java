package com.byr.learning.zookeeper.lock;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CountDownLatch;

/**
 * Distributed lock by zookeeper
 * <p>
 * Created in 2018.11.12
 * 另外一种思路：可以根据有序节点+watch实现，实现思路，如：
 * 为每个线程生成一个有序的临时节点，为确保有序性，在排序一次全部节点，获取全部节点，
 * 每个线程判断自己是否最小，如果是的话，获得锁，执行操作，操作完删除自身节点。
 * 如果不是第一个的节点则监听它的前一个节点，当它的前一个节点被删除时，则它会获得锁，以此类推。
 * 同时，其可以避免服务宕机导致的锁无法释放，而产生的死锁问题。
 * </p>
 * 优点：具备高可用、可重入、阻塞锁特性，可解决失效死。
 * 缺点：因为需要频繁的创建和删除节点，性能上不如Redis方式。
 *
 * @author Liaozihong
 */
@Slf4j
public class DistributedLockByZookeeper {
    private final static String ROOT_PATH_LOCK = "byr";

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    /**
     * The Curator framework.
     */
    @Autowired
    CuratorFramework curatorFramework;

    /**
     * 创建 watcher 事件
     */
    private void addWatcher(String path) {
        String keyPath;
        if (path.equals(ROOT_PATH_LOCK)) {
            keyPath = "/" + path;
        } else {
            keyPath = "/" + ROOT_PATH_LOCK + "/" + path;
        }
        try {
            final PathChildrenCache cache = new PathChildrenCache(curatorFramework, keyPath, false);
            cache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
            cache.getListenable().addListener((client, event) -> {
                if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_REMOVED)) {
                    String oldPath = event.getData().getPath();
                    log.info("上一个节点 " + oldPath + " 已经被断开");
                    if (oldPath.contains(path)) {
                        //释放计数器，让当前的请求获取锁
                        countDownLatch.countDown();
                    }
                }
            });
        } catch (Exception e) {
            log.info("监听是否锁失败!{}", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 创建父节点，并创建永久节点
     */
    public void init() {
        curatorFramework = curatorFramework.usingNamespace("lock-namespace");
        String path = "/" + ROOT_PATH_LOCK;
        try {
            Thread.sleep(1000);
            if (curatorFramework.checkExists().forPath(path) == null) {
                curatorFramework.create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT)
                        .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                        .forPath(path);
            }
            addWatcher(ROOT_PATH_LOCK);
            log.info("root path 的 watcher 事件创建成功");
        } catch (Exception e) {
            log.error("connect zookeeper fail，please check the log >> {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

}
