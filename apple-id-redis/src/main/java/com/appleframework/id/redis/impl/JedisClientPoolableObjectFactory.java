package com.appleframework.id.redis.impl;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import com.appleframework.id.redis.IRedisClient;

/**
 * Apache commons-pool2 factory to create {@link JedisRedisClient} objects.
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class JedisClientPoolableObjectFactory extends BasePooledObjectFactory<JedisRedisClient> {

    private String redisHost, redisUser, redisPassword;
    private int redisPort = IRedisClient.DEFAULT_REDIS_PORT;

    public JedisClientPoolableObjectFactory(String host, int port, String username, String password) {
        redisHost = host;
        redisPort = port;
        redisUser = username;
        redisPassword = password;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JedisRedisClient create() throws Exception {
        JedisRedisClient redisClient = new JedisRedisClient();
        redisClient.setRedisHost(redisHost).setRedisPort(redisPort).setRedisUsername(redisUser)
                .setRedisPassword(redisPassword);
        redisClient.init();
        return redisClient;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroyObject(PooledObject<JedisRedisClient> redisClient) throws Exception {
        redisClient.getObject().destroy();
        super.destroyObject(redisClient);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validateObject(PooledObject<JedisRedisClient> redisClient) {
        try {
            return redisClient.getObject().ping() != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PooledObject<JedisRedisClient> wrap(JedisRedisClient redisClient) {
        return new DefaultPooledObject<JedisRedisClient>(redisClient);
    }

}
