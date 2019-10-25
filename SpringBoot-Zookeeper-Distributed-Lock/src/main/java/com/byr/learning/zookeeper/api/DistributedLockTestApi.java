package com.byr.learning.zookeeper.api;

import com.baomidou.mybatisplus.extension.api.ApiResult;
import com.byr.learning.common.ZookeeperMother;
import com.byr.learning.zookeeper.lock.DistributedLockByZookeeper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Distributed lock controller api
 * <p/>
 * Created in 2018.11.12
 * <p/>
 *
 * @author Liaozihong
 */
@RestController
@Slf4j
@Api(value = "分布式锁测试接口", tags = "DistributedLockTestApi")
public class DistributedLockTestApi {
    /**
     * The Distributed lock by zookeeper.
     */
    @Autowired
    DistributedLockByZookeeper distributedLockByZookeeper;
    @Autowired
    ZookeeperMother zookeeperMother;
    private final static String PATH = "testv3";

    /**
     * Gets lock 1.
     *
     * @return the lock 1
     */
    @GetMapping("/lock1")
    @ApiOperation(value = "获取分布式锁", notes = "获取分布式锁，获取到的期间，其他请求被阻塞，等待上一个释放锁资源", response = ApiResult.class)
    public ApiResult getLock1() {
        Boolean flag = false;
        try {
            if (zookeeperMother.creteZookeeperNode(PATH)) {
                Thread.sleep(5000);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (zookeeperMother.creteZookeeperNode(PATH)) {
                log.info("释放锁成功！");
                return ApiResult.ok("释放锁成功");
            }
            log.error("释放锁失败！");
            return ApiResult.failed("释放锁失败");
        }

    }
}
