package com.at2024.redis_redlock.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.redis", ignoreUnknownFields = false)
public class RedisProperties {
    private int database;
    /**
     * 等待节点回复命令的时间。该时间从命令发送成功时开始计时
     */
    private int timeout;
    private String password;
    private String mode;
    /**
     * 池配置
     */
    private RedisPoolProperties pool;
    /**
     * 单机信息配置
     */
    private RedisSingleProperties single;

    public int getDatabase() {
        return database;
    }

    public void setDatabase(int database) {
        this.database = database;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public RedisPoolProperties getPool() {
        return pool;
    }

    public void setPool(RedisPoolProperties pool) {
        this.pool = pool;
    }

    public RedisSingleProperties getSingle() {
        return single;
    }

    public void setSingle(RedisSingleProperties single) {
        this.single = single;
    }
}
