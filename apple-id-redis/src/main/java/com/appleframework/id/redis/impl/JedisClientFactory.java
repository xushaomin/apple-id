package com.appleframework.id.redis.impl;

import redis.clients.jedis.Jedis;

import org.apache.log4j.Logger;

import com.appleframework.id.redis.PoolConfig;
import com.appleframework.id.redis.RedisClientFactory;

/**
 * Redis client factory that uses {@link Jedis
 * https://github.com/xetorthio/jedis} as the underlying Redis client library.
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class JedisClientFactory extends RedisClientFactory {

	private static Logger logger = Logger.getLogger(JedisClientFactory.class);  

    /**
     * {@inheritDoc}
     */
    @Override
    protected JedisClientPool createRedisClientPool(String host, int port, String username,
            String password, PoolConfig poolConfig) {
        if (logger.isDebugEnabled()) {
        	logger.debug("Building a Redis client pool {host:" + host + ";port:" + port
                    + ";username:" + username + "}...");
        }
        JedisClientPoolableObjectFactory factory = new JedisClientPoolableObjectFactory(host, port,
                username, password);
        JedisClientPool redisClientPool = new JedisClientPool(factory, poolConfig);
        redisClientPool.init();
        return redisClientPool;
    }

}
