package com.appleframework.id.redis;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.appleframework.id.redis.impl.JedisClientFactory;
import com.appleframework.id.redis.impl.JedisClientPool;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

/**
 * Factory to create {@link IRedisClient} objects.
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public abstract class RedisClientFactory {

	private static Logger logger = LoggerFactory.getLogger(RedisClientFactory.class);  

    // /**
    // * Stores established Redis client pools as a map of {key:pool}.
    // */
    // private ConcurrentMap<String, RedisClientPool> redisClientPools = new
    // ConcurrentHashMap<String, RedisClientPool>();

    /**
     * Stores established Redis client pools as a map of {key:pool}.
     */
    private Cache<String, JedisClientPool> cacheRedisClientPools;

    /**
     * Creates a new factory.
     * 
     * @return
     */
    public static RedisClientFactory newFactory() {
        JedisClientFactory factory = new JedisClientFactory();
        factory.init();
        return factory;
    }

    /**
     * Initializes the factory.
     */
    public void init() {
        int numProcessors = Runtime.getRuntime().availableProcessors();
        cacheRedisClientPools = CacheBuilder.newBuilder().concurrencyLevel(numProcessors)
                .expireAfterAccess(3600, TimeUnit.SECONDS)
                .removalListener(new RemovalListener<String, JedisClientPool>() {
                    @Override
                    public void onRemoval(RemovalNotification<String, JedisClientPool> notification) {
                        JedisClientPool pool = notification.getValue();
                        pool.destroy();
                    }
                }).build();
    }

    /**
     * Destroys the factory.
     */
    public void destroy() {
        if (cacheRedisClientPools != null) {
            cacheRedisClientPools.invalidateAll();
            cacheRedisClientPools = null;
        }

        // for (Entry<String, RedisClientPool> entry :
        // redisClientPools.entrySet()) {
        // RedisClientPool clientPool = entry.getValue();
        // try {
        // clientPool.close();
        // } catch (Exception e) {
        // LOGGER.warn(e.getMessage());
        // }
        // }
        // redisClientPools.clear();
    }

    /**
     * Builds a unique pool name from configurations.
     * 
     * @param host
     * @param port
     * @param username
     * @param password
     * @param poolConfig
     * @return
     */
    protected static String calcRedisPoolName(String host, int port, String username,
            String password, PoolConfig poolConfig) {
        StringBuilder sb = new StringBuilder();
        sb.append(host != null ? host : "NULL");
        sb.append(".");
        sb.append(port);
        sb.append(".");
        sb.append(username != null ? username : "NULL");
        sb.append(".");
        int passwordHashcode = password != null ? password.hashCode() : "NULL".hashCode();
        int poolHashcode = poolConfig != null ? poolConfig.hashCode() : "NULL".hashCode();
        return sb.append(passwordHashcode).append(".").append(poolHashcode).toString();
    }

    // /**
    // * Gets a {@link RedisClientPool} by name.
    // *
    // * @param poolName
    // * @return
    // */
    // protected RedisClientPool getPool(String poolName) {
    // return redisClientPools.get(poolName);
    // }

    /**
     * Creates a new {@link JedisClientPool}.
     * 
     * @param host
     * @param port
     * @param username
     * @param password
     * @return
     */
    protected JedisClientPool createRedisClientPool(String host, int port, String username,
            String password) {
        return createRedisClientPool(host, port, username, password, null);
    }

    /**
     * Creates a new {@link JedisClientPool}.
     * 
     * @param host
     * @param port
     * @param username
     * @param password
     * @param poolConfig
     * @return
     */
    protected abstract JedisClientPool createRedisClientPool(String host, int port,
            String username, String password, PoolConfig poolConfig);

    /**
     * Gets or Creates a {@link IRedisClient} object.
     * 
     * @param host
     * @return
     */
    public IRedisClient getRedisClient(String host) {
        return getRedisClient(host, IRedisClient.DEFAULT_REDIS_PORT, null, null);
    }

    /**
     * Gets or Creates a {@link IRedisClient} object.
     * 
     * @param host
     * @param port
     * @return
     */
    public IRedisClient getRedisClient(String host, int port) {
        return getRedisClient(host, port, null, null);
    }

    /**
     * Gets or Creates a {@link IRedisClient} object.
     * 
     * @param host
     * @param port
     * @param username
     * @param password
     * @return
     */
    public IRedisClient getRedisClient(String host, int port, String username, String password) {
        return getRedisClient(host, port, username, password, null);
    }

    /**
     * Gets or Creates a {@link IRedisClient} object.
     * 
     * @param host
     * @param port
     * @param username
     * @param password
     * @param poolConfig
     * @return
     */
    public IRedisClient getRedisClient(final String host, final int port, final String username,
            final String password, final PoolConfig poolConfig) {
        String poolName = calcRedisPoolName(host, port, username, password, poolConfig);

        try {
            JedisClientPool redisClientPool = cacheRedisClientPools.get(poolName,
                    new Callable<JedisClientPool>() {
                        @Override
                        public JedisClientPool call() throws Exception {
                            return createRedisClientPool(host, port, username, password, poolConfig);
                        }
                    });
            return redisClientPool.borrowObject();
        } catch (Exception e) {
        	logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Returns the {@link IRedisClient} object after use.
     * 
     * @param redisClient
     */
    public void returnRedisClient(IRedisClient redisClient) {
        if (redisClient != null) {
            redisClient.close();
        }
    }
}
