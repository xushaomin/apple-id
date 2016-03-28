package com.appleframework.id.redis;

import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Redis client pool configurations.
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class PoolConfig {

    /**
     * Default lifetime (ms) of an open connection.
     */
    public final static long DEFAULT_MAX_CONNECTION_LIFETIME = -1;

    /**
     * Default wait time (ms) the pool will wait to obtain a connection.
     */
    public final static long DEFAULT_MAX_WAIT_TIME = 3000;

    /**
     * Default maximum number of active connections.
     */
    public final static int DEFAULT_MAX_ACTIVE = 8;

    /**
     * Default maximum number of idle connections.
     */
    public final static int DEFAULT_MAX_IDLE = 1 + DEFAULT_MAX_ACTIVE / 2;

    /**
     * Default minimum number of idle connections.
     */
    public final static int DEFAULT_MIN_IDLE = 1 + DEFAULT_MAX_ACTIVE / 4;

    private int maxActive = DEFAULT_MAX_ACTIVE, maxIdle = DEFAULT_MAX_IDLE,
            minIdle = DEFAULT_MIN_IDLE;
    private long maxWaitTimeMs = DEFAULT_MAX_WAIT_TIME;

    private boolean testOnBorrow = false, testWhileIdle = true, testOnCreate = true;

    public int getMaxActive() {
        return maxActive;
    }

    public PoolConfig setMaxActive(int maxActive) {
        this.maxActive = maxActive;
        return this;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public PoolConfig setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
        return this;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public PoolConfig setMinIdle(int minIdle) {
        this.minIdle = minIdle;
        return this;
    }

    public long getMaxWaitTime() {
        return maxWaitTimeMs;
    }

    public PoolConfig setMaxWaitTime(long maxWaitTimeMs) {
        this.maxWaitTimeMs = maxWaitTimeMs;
        return this;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public boolean getTestOnBorrow() {
        return testOnBorrow;
    }

    public PoolConfig setTestOnBorrow(boolean value) {
        this.testOnBorrow = value;
        return this;
    }

    public boolean isTestOnCreate() {
        return testOnCreate;
    }

    public boolean getTestOnCreate() {
        return testOnCreate;
    }

    public PoolConfig setTestOnCreate(boolean value) {
        this.testOnCreate = value;
        return this;
    }

    public boolean isTestWhileIdle() {
        return testWhileIdle;
    }

    public boolean getTestWhileIdle() {
        return testWhileIdle;
    }

    public PoolConfig setTestWhileIdle(boolean value) {
        this.testWhileIdle = value;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder(19, 81);
        hcb.append(maxActive).append(maxIdle).append(minIdle).append(maxWaitTimeMs);
        return hcb.hashCode();
    }

}
