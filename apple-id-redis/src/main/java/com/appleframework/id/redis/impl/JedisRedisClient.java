package com.appleframework.id.redis.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang3.StringUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.util.SafeEncoder;

import com.appleframework.id.redis.IRedisClient;
import com.appleframework.id.redis.MessageListener;

/**
 * An implementation of {@link IRedisClient} that uses {@link Jedis
 * https://github.com/xetorthio/jedis} as the underlying Redis client engine.
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class JedisRedisClient implements IRedisClient {

    private Jedis redisClient;
    private final ConcurrentMap<String, Set<MessageListener>> topicSubscriptions = new ConcurrentHashMap<String, Set<MessageListener>>();
    private final ConcurrentMap<MessageListener, WrappedJedisPubSub> topicSubscriptionMappings = new ConcurrentHashMap<MessageListener, WrappedJedisPubSub>();

    private String redisHost = "localhost", redisUsername, redisPassword;
    private int redisPort = DEFAULT_REDIS_PORT;
    private JedisClientPool redisClientPool;

    protected JedisClientPool getRedisClientPool() {
        return redisClientPool;
    }

    public JedisRedisClient setRedisClientPool(JedisClientPool redisClientPool) {
        this.redisClientPool = redisClientPool;
        return this;
    }

    protected String getRedisHost() {
        return redisHost;
    }

    public JedisRedisClient setRedisHost(String redisHost) {
        this.redisHost = redisHost;
        return this;
    }

    protected int getRedisPort() {
        return redisPort;
    }

    public JedisRedisClient setRedisPort(int redisPort) {
        this.redisPort = redisPort;
        return this;
    }

    protected String getRedisUsername() {
        return redisUsername;
    }

    public JedisRedisClient setRedisUsername(String redisUsername) {
        this.redisUsername = redisUsername;
        return this;
    }

    protected String getRedisPassword() {
        return redisPassword;
    }

    public JedisRedisClient setRedisPassword(String redisPassword) {
        this.redisPassword = redisPassword;
        return this;
    }

    public void init() {
        int timeout = 10000; // timeout in milliseconds
        redisClient = new Jedis(getRedisHost(), getRedisPort(), timeout);
        if (!StringUtils.isBlank(getRedisPassword())) {
            redisClient.auth(getRedisPassword());
        }
        redisClient.connect();
    }

    public void destroy() {
        try {
            if (redisClient != null) {
                try {
                    redisClient.disconnect();
                } finally {
                    redisClient.quit();
                }
            }
        } catch (Exception e) {
        }
    }

    public void close() {
        if (getRedisClientPool() != null) {
            getRedisClientPool().returnObject(this);
        }
    }

    /* API: single value */
    /**
     * {@inheritDoc}
     */
    @Override
    public String ping() {
        return redisClient.isConnected() ? redisClient.ping() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long ttl(String key) {
        return redisClient.ttl(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void expire(String key, int ttlSeconds) {
        if (ttlSeconds > 0) {
            redisClient.expire(key, ttlSeconds);
        } else if (ttlSeconds == TTL_PERSISTENT) {
            redisClient.persist(key);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(String... keys) {
        redisClient.del(keys);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String get(String key) {
        return redisClient.get(key);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<String> multiGet(String... keys) {
        Pipeline p = redisClient.pipelined();
        for (String key : keys) {
            p.get(key);
        }
        List<?> result = p.syncAndReturnAll();
        return (List<String>) result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getAsBinary(String key) {
        return redisClient.get(SafeEncoder.encode(key));
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<byte[]> multiGetAsBinary(String... keys) {
        Pipeline p = redisClient.pipelined();
        for (String key : keys) {
            p.get(SafeEncoder.encode(key));
        }
        List<?> result = p.syncAndReturnAll();
        return (List<byte[]>) result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSet(String key, String value) {
        return redisClient.getSet(key, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSet(String key, byte[] value) {
        return redisClient.getSet(key, SafeEncoder.encode(value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getSetAsBinary(String key, String value) {
        return redisClient.getSet(SafeEncoder.encode(key), SafeEncoder.encode(value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getSetAsBinary(String key, byte[] value) {
        return redisClient.getSet(SafeEncoder.encode(key), value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long decBy(String key, long value) {
        Long result = redisClient.decrBy(key, value);
        return result != null ? result.longValue() : 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long incBy(String key, long value) {
        Long result = redisClient.incrBy(key, value);
        return result != null ? result.longValue() : 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void set(String key, String value, int ttlSeconds) {
        if (ttlSeconds > 0) {
            redisClient.setex(key, ttlSeconds, value);
        } else {
            redisClient.set(key, value);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void set(String key, byte[] value, int ttlSeconds) {
        if (ttlSeconds > 0) {
            redisClient.setex(SafeEncoder.encode(key), ttlSeconds, value);
        } else {
            redisClient.set(SafeEncoder.encode(key), value);
        }
    }

    /* API: hash */

    /**
     * {@inheritDoc}
     */
    @Override
    public void hashDelete(String mapName, String... fieldName) {
        redisClient.hdel(mapName, fieldName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long hashSize(String mapName) {
        Long result = redisClient.hlen(mapName);
        return result != null ? result.longValue() : -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String hashGet(String mapName, String fieldName) {
        return redisClient.hget(mapName, fieldName);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<String> hashMultiGet(String[] mapNames, String[] fieldNames) {
        if (mapNames.length != fieldNames.length) {
            throw new IllegalArgumentException(
                    "List of map names and list of field names must have same number of elements!");
        }
        Pipeline p = redisClient.pipelined();
        for (int i = 0; i < mapNames.length; i++) {
            String mapName = mapNames[i];
            String fieldName = fieldNames[i];
            p.hget(mapName, fieldName);
        }
        List<?> result = p.syncAndReturnAll();
        return (List<String>) result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] hashGetAsBinary(String mapName, String fieldName) {
        return redisClient.hget(SafeEncoder.encode(mapName), SafeEncoder.encode(fieldName));
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<byte[]> hashMultiGetAsBinary(String[] mapNames, String[] fieldNames) {
        if (mapNames.length != fieldNames.length) {
            throw new IllegalArgumentException(
                    "List of map names and list of field names must have same number of elements!");
        }
        Pipeline p = redisClient.pipelined();
        for (int i = 0; i < mapNames.length; i++) {
            byte[] mapName = SafeEncoder.encode(mapNames[i]);
            byte[] fieldName = SafeEncoder.encode(fieldNames[i]);
            p.hget(mapName, fieldName);
        }
        List<?> result = p.syncAndReturnAll();
        return (List<byte[]>) result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> hashMGet(String mapName, String... fieldNames) {
        return redisClient.hmget(mapName, fieldNames);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<byte[]> hashMGetAsBinary(String mapName, String... fieldNames) {
        return redisClient.hmget(SafeEncoder.encode(mapName), SafeEncoder.encodeMany(fieldNames));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long hashIncBy(String mapName, String fieldName, long value) {
        Long result = redisClient.hincrBy(mapName, fieldName, value);
        return result != null ? result.longValue() : 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long hashDecBy(String mapName, String fieldName, long value) {
        Long result = redisClient.hincrBy(mapName, fieldName, -value);
        return result != null ? result.longValue() : 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void hashSet(String mapName, String fieldName, String value, int ttlSeconds) {
        redisClient.hset(mapName, fieldName, value);
        expire(mapName, ttlSeconds);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void hashSet(String mapName, String fieldName, byte[] value, int ttlSeconds) {
        redisClient.hset(SafeEncoder.encode(mapName), SafeEncoder.encode(fieldName), value);
        expire(mapName, ttlSeconds);
    }

    /* API: list */

    /**
     * {@inheritDoc}
     */
    @Override
    public void listPush(String listName, String... messages) {
        listPush(listName, 0, messages);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void listPush(String listName, byte[]... messages) {
        listPush(listName, 0, messages);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void listPush(String listName, int ttlSeconds, String... messages) {
        redisClient.rpush(listName, messages);
        expire(listName, ttlSeconds);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void listPush(String listName, int ttlSeconds, byte[]... messages) {
        redisClient.rpush(SafeEncoder.encode(listName), messages);
        expire(listName, ttlSeconds);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String listPop(String listName) {
        return listPop(listName, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String listPop(String listName, boolean block) {
        return listPop(listName, block, DEFAULT_READ_TIMEOUT_SEC);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String listPop(String listName, boolean block, int timeout) {
        if (!block) {
            return redisClient.lpop(listName);
        }
        List<String> result = redisClient.blpop(timeout, listName);
        return result != null && result.size() > 1 ? result.get(1) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] listPopAsBinary(String listName) {
        return listPopAsBinary(listName, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] listPopAsBinary(String listName, boolean block) {
        return listPopAsBinary(listName, block, DEFAULT_READ_TIMEOUT_SEC);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] listPopAsBinary(String listName, boolean block, int timeout) {
        if (!block) {
            return redisClient.lpop(SafeEncoder.encode(listName));
        }
        List<byte[]> result = redisClient.blpop(timeout, SafeEncoder.encode(listName));
        return result != null && result.size() > 1 ? result.get(1) : null;
    }

    /**
     * {@inheritDoc}
     */
    public long listSize(String listName) {
        Long size = redisClient.llen(listName);
        return size != null ? size.longValue() : -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> listMembers(String listName) {
        long listSize = listSize(listName);
        List<String> result = redisClient.lrange(listName, 0, listSize - 1);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<byte[]> listMembersAsBinary(String listName) {
        long listSize = listSize(listName);
        List<byte[]> result = redisClient.lrange(SafeEncoder.encode(listName), 0, listSize - 1);
        return result;
    }

    /* API: set */

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAdd(String setName, String... message) {
        setAdd(setName, 0, message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAdd(String setName, byte[]... message) {
        setAdd(setName, 0, message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAdd(String setName, int ttlSeconds, String... message) {
        redisClient.sadd(setName, message);
        expire(setName, ttlSeconds);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAdd(String setName, int ttlSeconds, byte[]... message) {
        redisClient.sadd(SafeEncoder.encode(setName), message);
        expire(setName, ttlSeconds);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setIsMember(String setName, String value) {
        Boolean result = redisClient.sismember(setName, value);
        return result != null ? result.booleanValue() : false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setIsMember(String setName, byte[] value) {
        Boolean result = redisClient.sismember(SafeEncoder.encode(setName), value);
        return result != null ? result.booleanValue() : false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String setPop(String setName) {
        return redisClient.spop(setName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] setPopAsBinary(String setName) {
        return redisClient.spop(SafeEncoder.encode(setName));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> setMembers(String setName) {
        Set<String> result = redisClient.smembers(setName);
        return result != null ? result : new HashSet<String>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<byte[]> setMembersAsBinary(String setName) {
        Set<byte[]> result = redisClient.smembers(SafeEncoder.encode(setName));
        return result != null ? result : new HashSet<byte[]>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRemove(String setName, String... member) {
        redisClient.srem(setName, member);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRemove(String setName, byte[]... member) {
        redisClient.srem(SafeEncoder.encode(setName), member);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long setSize(String setName) {
        Long size = redisClient.scard(setName);
        return size != null ? size.longValue() : -1;
    }

    /* API: pub/sub */

    /**
     * {@inheritDoc}
     */
    @Override
    public void publish(String topic, String message) {
        redisClient.publish(topic, message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publish(String topic, byte[] message) {
        redisClient.publish(SafeEncoder.encode(topic), message);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean subscribe(String topic, MessageListener messageListener) {
        Set<MessageListener> subcription = topicSubscriptions.putIfAbsent(topic,
                new HashSet<MessageListener>());
        if (subcription == null) {
            subcription = topicSubscriptions.get(topic);
        }

        boolean subscribe = false;
        WrappedJedisPubSub wrappedJedisPubSub = null;
        synchronized (subcription) {
            if (subcription.add(messageListener)) {
                wrappedJedisPubSub = new WrappedJedisPubSub(messageListener);
                topicSubscriptionMappings.put(messageListener, wrappedJedisPubSub);
                subscribe = true;
            }
        }
        if (subscribe) {
            byte[] topicName = SafeEncoder.encode(topic);
            // this operation blocks!
            redisClient.subscribe(wrappedJedisPubSub, topicName);
            return true;
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean unsubscribe(String topic, MessageListener messageListener) {
        Set<MessageListener> subcription = topicSubscriptions.get(topic);
        boolean unsubscribe = false;
        WrappedJedisPubSub wrappedJedisPubSub = null;
        if (subcription != null) {
            synchronized (subcription) {
                if (subcription.remove(messageListener)) {
                    wrappedJedisPubSub = topicSubscriptionMappings.remove(messageListener);
                    if (wrappedJedisPubSub != null) {
                        unsubscribe = true;
                    }
                }
            }
        }
        if (unsubscribe) {
            byte[] topicName = SafeEncoder.encode(topic);
            wrappedJedisPubSub.unsubscribe(topicName);
            return true;
        } else {
            return false;
        }
    }
}
