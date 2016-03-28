package com.appleframework.id.redis;

import java.util.List;
import java.util.Set;

/**
 * Redis client API.
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public interface IRedisClient {

    public final static int DEFAULT_REDIS_PORT = 6379;
    public final static int DEFAULT_READ_TIMEOUT_SEC = 10;

    /**
     * Do not change key's TTL.
     * 
     * @since 0.2.1
     */
    public final static int TTL_NO_CHANGE = 0;

    /**
     * Remove the existing timeout on key
     * 
     * @since 0.2.1
     */
    public final static int TTL_PERSISTENT = -1;

    /**
     * Closes the connection to Redis server, but do not destroy the Redis
     * client object.
     */
    public void close();

    /* API: single value */
    /**
     * "Ping" the Redis server.
     * 
     * @return
     */
    public String ping();

    /**
     * Gets current TTL value of a key in seconds.
     * 
     * @param key
     * @return
     * @since 0.4.0
     */
    public long ttl(String key);

    /**
     * Updates expiry time of a Redis key.
     * 
     * @param key
     * @param ttlSeconds
     *            time to live (a.k.a "expired after write") in seconds. Special
     *            values (since v0.2.1): {@link TTL_NO_CHANGE},
     *            {@link #TTL_PERSISTENT}.
     */
    public void expire(String key, int ttlSeconds);

    /**
     * Deletes value(s) from Redis server.
     * 
     * @param keys
     */
    public void delete(String... keys);

    /**
     * Gets a value from Redis server.
     * 
     * @param key
     * @return
     */
    public String get(String key);

    /**
     * Gets multiple values from Redis server via pipeline.
     * 
     * @param keys
     * @return
     * @since 0.3.1
     */
    public List<String> multiGet(String... keys);

    /**
     * Gets a value from Redis server.
     * 
     * @param key
     * @return
     */
    public byte[] getAsBinary(String key);

    /**
     * Gets multiple values from Redis server via pipeline.
     * 
     * @param keys
     * @return
     * @since 0.3.1
     */
    public List<byte[]> multiGetAsBinary(String... keys);

    /**
     * Atomically sets key to value and returns the old value stored at key.
     * 
     * @param key
     * @param value
     * @return
     * @since 0.2.0
     */
    public String getSet(String key, String value);

    /**
     * Atomically sets key to value and returns the old value stored at key.
     * 
     * @param key
     * @param value
     * @return
     * @since 0.2.0
     */
    public String getSet(String key, byte[] value);

    /**
     * Atomically sets key to value and returns the old value stored at key.
     * 
     * @param key
     * @param value
     * @return
     * @since 0.2.0
     */
    public byte[] getSetAsBinary(String key, String value);

    /**
     * Atomically sets key to value and returns the old value stored at key.
     * 
     * @param key
     * @param value
     * @return
     * @since 0.2.0
     */
    public byte[] getSetAsBinary(String key, byte[] value);

    /**
     * Decrements the number stored at key by a specified value, and returns the
     * value of key after the decrement.
     * 
     * @param key
     * @param value
     * @return the value of key after the decrement
     * @since 0.2.0
     */
    public long decBy(String key, long value);

    /**
     * Increments the number stored at key by a specified value, and returns the
     * value of key after the increment.
     * 
     * @param key
     * @param value
     * @return the value of key after the increment
     * @since 0.2.0
     */
    public long incBy(String key, long value);

    /**
     * Sets a value to Redis server.
     * 
     * @param key
     * @param value
     * @param ttlSeconds
     *            time to live (a.k.a "expired after write") in seconds
     */
    public void set(String key, String value, int ttlSeconds);

    /**
     * Sets a value to Redis server.
     * 
     * @param key
     * @param value
     * @param ttlSeconds
     *            time to live (a.k.a "expired after write") in seconds
     */
    public void set(String key, byte[] value, int ttlSeconds);

    /* API: hash */

    /**
     * Deletes values from a Redis hash.
     * 
     * @param mapName
     * @param fieldName
     */
    public void hashDelete(String mapName, String... fieldName);

    /**
     * Gets number of elements of a Redis hash.
     * 
     * @param mapName
     * @return
     */
    public long hashSize(String mapName);

    /**
     * Gets a field value from a Redis hash.
     * 
     * @param mapName
     * @param fieldName
     * @return
     */
    public String hashGet(String mapName, String fieldName);

    /**
     * Gets multiple field values from multiples Redis hashes via pipeline.
     * 
     * @param mapNames
     * @param fieldNames
     * @return
     * @since 0.3.1
     */
    public List<String> hashMultiGet(String[] mapNames, String[] fieldNames);

    /**
     * Gets a field value from a Redis hash.
     * 
     * @param mapName
     * @param fieldName
     * @return
     */
    public byte[] hashGetAsBinary(String mapName, String fieldName);

    /**
     * Gets multiple field values from multiples Redis hashes via pipeline.
     * 
     * @param mapNames
     * @param fieldNames
     * @return
     * @since 0.3.1
     */
    public List<byte[]> hashMultiGetAsBinary(String[] mapNames, String[] fieldNames);

    /**
     * Gets multiple field values fron a Redis hash.
     * 
     * @param mapName
     * @param fieldNames
     * @return
     * @since 0.3.0
     */
    public List<String> hashMGet(String mapName, String... fieldNames);

    /**
     * Gets multiple field values from a Redis hash.
     * 
     * @param mapName
     * @param fieldNames
     * @return
     * @since 0.3.0
     */
    public List<byte[]> hashMGetAsBinary(String mapName, String... fieldNames);

    /**
     * Increments the number stored at {@code field} in the hash stored at
     * {@code key} by a specified value, and returns the value after the
     * increment.
     * 
     * @param mapName
     * @param fieldName
     * @param value
     * @return the value of key after the increment
     * @since 0.2.2
     */
    public long hashIncBy(String mapName, String fieldName, long value);

    /**
     * Decrements the number stored at {@code field} in the hash stored at
     * {@code key} by a specified value, and returns the value after the
     * decrement.
     * 
     * @param mapName
     * @param fieldName
     * @param value
     * @return the value of key after the increment
     * @since 0.2.2
     */
    public long hashDecBy(String mapName, String fieldName, long value);

    /**
     * Sets a field value of a Redis hash.
     * 
     * @param mapName
     * @param fieldName
     * @param value
     * @param ttlSeconds
     *            time to live (a.k.a "expired after write") in seconds
     */
    public void hashSet(String mapName, String fieldName, String value, int ttlSeconds);

    /**
     * Sets a field value of a Redis hash.
     * 
     * @param mapName
     * @param fieldName
     * @param value
     * @param ttlSeconds
     *            time to live (a.k.a "expired after write") in seconds
     */
    public void hashSet(String mapName, String fieldName, byte[] value, int ttlSeconds);

    /* API: list */

    /**
     * Pushes a message to head of a list.
     * 
     * @param listName
     * @param messages
     */
    public void listPush(String listName, String... messages);

    /**
     * Pushes a message to head of a list.
     * 
     * @param listName
     * @param messages
     */
    public void listPush(String listName, byte[]... message);

    /**
     * Pushes a message to head of a list.
     * 
     * @param listName
     * @param ttlSeconds
     * @param messages
     */
    public void listPush(String listName, int ttlSeconds, String... messages);

    /**
     * Pushes a message to head of a list.
     * 
     * @param listName
     * @param ttlSeconds
     * @param messages
     */
    public void listPush(String listName, int ttlSeconds, byte[]... messages);

    /**
     * Pops a message from tail of a list.
     * 
     * @param listName
     * @return
     */
    public String listPop(String listName);

    /**
     * Pops a message from tail of a list.
     * 
     * @param listName
     * @param block
     *            block until data is available?
     * @return
     */
    public String listPop(String listName, boolean block);

    /**
     * Pops a message from tail of a list.
     * 
     * @param listName
     * @param block
     *            block until data is available?
     * @param timeout
     *            timeout in seconds
     * @return
     */
    public String listPop(String listName, boolean block, int timeout);

    /**
     * Pops a message from tail of a list.
     * 
     * @param listName
     * @return
     */
    public byte[] listPopAsBinary(String listName);

    /**
     * Pops a message from tail of a list.
     * 
     * @param listName
     * @param block
     *            block until data is available?
     * @return
     */
    public byte[] listPopAsBinary(String listName, boolean block);

    /**
     * Pops a message from tail of a list.
     * 
     * @param listName
     * @param block
     *            block until data is available?
     * @param timeout
     *            timeout in seconds
     * @return
     */
    public byte[] listPopAsBinary(String listName, boolean block, int timeout);

    /**
     * Gets all list members.
     * 
     * @param listName
     * @return
     */
    public List<String> listMembers(String listName);

    /**
     * Gets all list members.
     * 
     * @param listName
     * @return
     */
    public List<byte[]> listMembersAsBinary(String listName);

    /**
     * Gets a list's size.
     * 
     * @param listName
     * @return
     */
    public long listSize(String listName);

    /* API: set */

    /**
     * Adds messages to a set.
     * 
     * @param setName
     * @param messages
     */
    public void setAdd(String setName, String... messages);

    /**
     * Adds messages to a set.
     * 
     * @param setName
     * @param messages
     */
    public void setAdd(String setName, byte[]... messages);

    /**
     * Adds messages to a set.
     * 
     * @param setName
     * @param ttlSeconds
     * @param messages
     */
    public void setAdd(String setName, int ttlSeconds, String... messages);

    /**
     * Adds messages to a set.
     * 
     * @param setName
     * @param ttlSeconds
     * @param messages
     */
    public void setAdd(String setName, int ttlSeconds, byte[]... messages);

    /**
     * Checks if a value is a member of a set.
     * 
     * @param setName
     * @param value
     * @return
     */
    public boolean setIsMember(String setName, String value);

    /**
     * Checks if a value is a member of a set.
     * 
     * @param setName
     * @param value
     * @return
     */
    public boolean setIsMember(String setName, byte[] value);

    /**
     * Randomly removes and returns an element from a set.
     * 
     * @param setName
     * @return
     */
    public String setPop(String setName);

    /**
     * Randomly removes and returns an element from a set as a binary string.
     * 
     * @param setName
     * @return
     */
    public byte[] setPopAsBinary(String setName);

    /**
     * Gets all members of a set.
     * 
     * @param setName
     * @return
     */
    public Set<String> setMembers(String setName);

    /**
     * Gets all members of a set as binary.
     * 
     * @param setName
     * @return
     */
    public Set<byte[]> setMembersAsBinary(String setName);

    /**
     * Removes value(s) from a set.
     * 
     * @param setName
     * @param member
     */
    public void setRemove(String setName, String... member);

    /**
     * Removes a value from a set.
     * 
     * @param setName
     * @param member
     */
    public void setRemove(String setName, byte[]... member);

    /**
     * Gets size of a set.
     * 
     * @param setName
     * @return
     */
    public long setSize(String setName);

    /* API: pub/sub */

    /**
     * Publishes a message to a topic.
     * 
     * @param topic
     * @param message
     */
    public void publish(String topic, String message);

    /**
     * Publishes a message to a topic.
     * 
     * @param topic
     * @param message
     */
    public void publish(String topic, byte[] message);

    /**
     * Subscribes to a topic.
     * 
     * Note: This method BLOCKS until
     * {@link MessageListener#unsubscribe(String)} is called.
     * 
     * @param topic
     * @param messageListener
     * @return
     */
    public boolean subscribe(String topic, MessageListener messageListener);

    /**
     * Un-subscribes from a topic.
     * 
     * @param topic
     * @param messageListener
     * @return
     */
    public boolean unsubscribe(String topic, MessageListener messageListener);
    /* Redis API */

}
