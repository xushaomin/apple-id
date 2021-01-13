package com.appleframework.id.redis;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Apache commons-pool2 of {@link IRedisClient} instances.
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 * @since 0.3.0
 */
public class RedisClientPool<T extends IRedisClient> extends GenericObjectPool<T> {

	private static Logger logger = LoggerFactory.getLogger(RedisClientPool.class);  

	private PoolConfig poolConfig;
    private Set<T> activeClients = new HashSet<T>();

    public RedisClientPool(PooledObjectFactory<T> factory, PoolConfig poolConfig) {
        super(factory);
        setPoolConfig(poolConfig);
    }

    public RedisClientPool<T> setPoolConfig(PoolConfig poolConfig) {
        this.poolConfig = poolConfig;
        return this;
    }

    public PoolConfig getPoolConfig() {
        return poolConfig;
    }

    public void init() {
        setTestOnBorrow(true);
        setTestWhileIdle(true);
        setBlockWhenExhausted(true);

        if (poolConfig != null) {
            int maxActive = poolConfig != null ? poolConfig.getMaxActive()
                    : PoolConfig.DEFAULT_MAX_ACTIVE;
            long maxWaitTime = poolConfig != null ? poolConfig.getMaxWaitTime()
                    : PoolConfig.DEFAULT_MAX_WAIT_TIME;
            int maxIdle = poolConfig != null ? poolConfig.getMaxIdle()
                    : PoolConfig.DEFAULT_MAX_IDLE;
            int minIdle = poolConfig != null ? poolConfig.getMinIdle()
                    : PoolConfig.DEFAULT_MIN_IDLE;
            logger.debug("Updating Redis client pool {maxActive:" + maxActive + ";maxWait:"
                    + maxWaitTime + ";minIdle:" + minIdle + ";maxIdle:" + maxIdle + "}...");
            this.setMaxTotal(maxActive);
            this.setMaxIdle(maxIdle);
            this.setMinIdle(minIdle);
            this.setMaxWaitMillis(maxWaitTime);

            this.setTestOnBorrow(poolConfig.isTestOnBorrow());
            this.setTestOnCreate(poolConfig.isTestOnCreate());
            this.setTestWhileIdle(poolConfig.isTestWhileIdle());
        } else {
            this.setMaxTotal(PoolConfig.DEFAULT_MAX_ACTIVE);
            this.setMaxIdle(PoolConfig.DEFAULT_MAX_IDLE);
            this.setMinIdle(PoolConfig.DEFAULT_MIN_IDLE);
            this.setMaxWaitMillis(PoolConfig.DEFAULT_MAX_WAIT_TIME);

            this.setTestOnBorrow(false);
            this.setTestOnCreate(true);
            this.setTestWhileIdle(true);
        }
        this.setTimeBetweenEvictionRunsMillis(10000);
        this.setTestOnReturn(false);
    }

    public void destroy() {
        close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        try {
            for (T client : activeClients) {
                try {
                    invalidateObject(client);
                } catch (Exception e) {
                	logger.warn(e.getMessage(), e);
                }
            }
        } finally {
            super.close();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T borrowObject() throws Exception {
        T redisClient = super.borrowObject();
        if (redisClient != null) {
            activeClients.add(redisClient);
        }
        return redisClient;
    }

    /**
     * {@inheritDoc}
     * 
     * @throws Exception
     */
    @Override
    public void returnObject(T redisClient) {
        try {
            super.returnObject(redisClient);
        } finally {
            activeClients.remove(redisClient);
        }
    }
}
