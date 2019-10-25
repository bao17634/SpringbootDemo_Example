package com.byr.learning.config;


import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZkConfiguration {
    @Autowired
    ConfigProperties configProperties;

    @Bean(initMethod = "start")
    public CuratorFramework curatorFramework() {
        return CuratorFrameworkFactory.newClient(
                configProperties.getConnectString(),
                configProperties.getSessionTimeoutMs(),
                configProperties.getConnectionTimeoutMs(),
                new RetryNTimes(configProperties.getRetryCount(), configProperties.getElapsedTimeMs()));
    }
}
