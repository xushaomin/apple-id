package com.appleframework.id.redis;

/**
 * Redis topic message listener.
 * 
 * @author Thanh Ba Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public abstract class MessageListener {
	
    private IRedisClient redisClient;

    public MessageListener() {
    }

    protected IRedisClient getRedisClient() {
        return redisClient;
    }

    protected MessageListener setRedisClient(IRedisClient redisClient) {
        this.redisClient = redisClient;
        return this;
    }

    /**
     * Un-subscribes from a topic.
     * 
     * @param topic
     */
    public void unsubscribe(String topic) {
        redisClient.unsubscribe(topic, this);
    }

    /**
     * Called when a message arrives.
     * 
     * @param topic
     * @param message
     */
    public abstract void onMessage(String topic, byte[] message);
}
